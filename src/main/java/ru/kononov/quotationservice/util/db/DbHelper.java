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

import static java.util.Objects.isNull;
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
     * @param sql
     * @param resultSetExtractor
     * @param parameters
     * @param <T>
     * @return
     */
    public <T> List<T> select(String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        try (var connection = dataSource.getConnection()) {
            return select(connection, sql, resultSetExtractor, parameters);
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param connection
     * @param sql
     * @param resultSetExtractor
     * @param parameters
     * @param <T>
     * @return
     */
    public <T> List<T> select(Connection connection, String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            try (var resultSet = preparedStatement.getResultSet()) {
                if (isNull(resultSet)) {
                    return List.of();
                }
                var result = new ArrayList<T>();
                while (resultSet.next()) {
                    var row = resultSetExtractor.extract(resultSet);
                    result.add(row);
                }
                return result;
            }
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param sql
     * @param parameters
     * @return
     */
    public long insert(String sql, Object... parameters) {
        try (var connection = dataSource.getConnection()) {
            return insert(connection, sql, parameters);
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param connection
     * @param sql
     * @param parameters
     * @return
     */
    public long insert(Connection connection, String sql, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            preparedStatement.executeUpdate();
            try (var resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                throw newApplicationException(resolve(), I_DB, "Не удалось выполнить INSERT");
            }
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param sql
     * @param parameters
     * @return
     */
    public int update(String sql, Object... parameters) {
        try (var connection = dataSource.getConnection()) {
            return update(connection, sql, parameters);
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param connection
     * @param sql
     * @param parameters
     * @return
     */
    public int update(Connection connection, String sql, Object... parameters) {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw newApplicationException(e, resolve(), I_DB);
        }
    }

    /**
     * @param query
     * @param <T>
     * @return
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
            throw newApplicationException(e, resolve(), I_DB, String.format("Произошёл откат транзакции с transactionId = %s", ThreadContext.get("transactionId")));
        } finally {
            ThreadContext.remove("transactionId");
        }
    }

}
