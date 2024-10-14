package com.heroku.reference.archiveagent.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import javax.sql.DataSource;

public class DatabaseInfo {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInfo.class);

    // Construct a subset of the database schema with just the invoice tables
    public static String getSchema(DataSource dataSource) {
        try  {
            StringBuilder schemaBuilder = new StringBuilder();
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                // Get the schema for the invoice tables
                ResultSet tables = metaData.getTables(null, null, "INVOICE%", new String[] {"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString(3);
                    schemaBuilder.append("Table: ").append(tableName).append(" (");
                    // Get columns for each table
                    ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("TYPE_NAME");
                        schemaBuilder.append(columnName).append(" ").append(columnType).append(", ");
                    }
                    schemaBuilder.setLength(schemaBuilder.length() - 2);  // Remove last comma and space
                    schemaBuilder.append("). ");
                }
            }
            return schemaBuilder.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }
}
