package ru.kononov.quotationservice.util.db;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static ru.kononov.quotationservice.error.exception.ExceptionCode.I_DB;
import static ru.kononov.quotationservice.error.exception.ExceptionFactory.newApplicationException;
import static ru.kononov.quotationservice.error.operation.ModuleOperationCode.resolve;

@Log4j2
@Singleton
public class DbHelper {

    private final DataSource dataSource;

    @Inject
    public DbHelper(@Named("quotationDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Выбор нескольких строк с маппингом.
     *
     * @param sql                запрос
     * @param resultSetExtractor маппинг
     * @param parameters         параметры запроса
     * @param <T>                тип, в который будет преобразована строка запроса
     * @return список
     */
    public <T> List<T> select(String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        try (var connection = dataSource.getConnection()) {
            return select(connection, sql, resultSetExtractor, parameters);
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB, e.getMessage());
        }
    }

    /**
     * Выбор нескольких строк с маппингом и переданным соединением. При использовании данного метода соединение необходимо закрывать вручную.
     *
     * @param connection         используемое соединение
     * @param sql                запрос
     * @param resultSetExtractor маппинг
     * @param parameters         параметры запроса
     * @param <T>                тип, в который будет преобразована строка запроса
     * @return список
     */
    public <T> List<T> select(Connection connection, String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            try (var resultSet = preparedStatement.executeQuery()) {
                var result = new ArrayList<T>();
                while (resultSet.next()) {
                    var row = resultSetExtractor.extract(resultSet);
                    result.add(row);
                }
                return result;
            }
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB, e.getMessage());
        }
    }

    /**
     * Вставка записи.
     *
     * @param connection используемое соединение
     * @param sql        запрос
     * @param parameters параметры запроса
     * @return значение сгенерированного первичного ключа
     */
    public Object insert(Connection connection, String sql, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            preparedStatement.executeUpdate();
            try (var resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getObject(1);
                }
                throw newApplicationException(resolve(), I_DB, "Не удалось выполнить INSERT");
            }
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB, e.getMessage());
        }
    }

    /**
     * Обновление записей
     *
     * @param connection используемое соединение
     * @param sql        запрос
     * @param parameters параметры запроса
     * @return количество обновлённых записей
     */
    public int update(Connection connection, String sql, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB, e.getMessage());
        }
    }

    /**
     * Выполнение нескольких запросов в одной транзакции.
     *
     * @param query коллбэк, принимающий на вход соединение и возвращающий какой-то результат
     * @param <T>   тип возвращаемого результата
     * @return результат функции
     */
    public <T> T inTransaction(Function<Connection, T> query) {
        ThreadContext.put("transactionId", UUID.randomUUID().toString());
        try (var connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                var result = query.apply(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error("DbHelper.inTransaction.thrown {}", e.getMessage());
            throw newApplicationException(e, resolve(), I_DB, String.format("Произошёл откат транзакции с transactionId = '%s'", ThreadContext.get("transactionId")), e.getMessage());
        } finally {
            ThreadContext.remove("transactionId");
        }
    }

}
