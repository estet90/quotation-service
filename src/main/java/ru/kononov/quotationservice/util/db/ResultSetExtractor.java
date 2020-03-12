package ru.kononov.quotationservice.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {

    T extract(ResultSet resultSet) throws SQLException;

}
