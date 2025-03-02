/*
 * #%L
 * anwiba commons database
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.database.postgresql.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.anwiba.commons.jdbc.name.DatabaseIndexName;
import net.anwiba.commons.jdbc.name.IDatabaseIndexName;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public class PostgresqlUtilities {

  private static ILogger logger = Logging.getLogger(PostgresqlUtilities.class.getName());

  public static void makeVacuumAnalyze(final Connection connection, final String schemaName, final String tableName)
      throws SQLException {
    try (Statement statement = connection.createStatement()) {
      final String activateVacumAnalyze = "VACUUM ANALYZE \"" + schemaName + "\".\"" + tableName + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static void makeVacuumAnalyze(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    try (Statement statement = connection.createStatement()) {
      final String activateVacumAnalyze =
          "VACUUM ANALYZE \"" + schemaName + "\".\"" + tableName + "\" ( \"" + columnName + "\" )"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static IDatabaseIndexName getIndexName(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(PostgresqlUtilitiesStatementStrings.IndexNameStatement);) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IndexNameStatement, exception); //$NON-NLS-1$
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet();) {
        if (resultSet.next()) {
          final String schema = resultSet.getString(1);
          final String index = resultSet.getString(2);
          return new DatabaseIndexName(schema, index);
        }
        return null;
      }
    }
  }

  public static boolean isIndexed(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String columnName)
      throws SQLException {
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName + " column " + columnName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(PostgresqlUtilitiesStatementStrings.IsIndexedStatement)) {
      statement.setString(1, schemaName);
      statement.setString(2, tableName);
      statement.setString(3, columnName);
      try {
        if (!statement.execute()) {
          return false;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + PostgresqlUtilitiesStatementStrings.IsIndexedStatement, exception); //$NON-NLS-1$
        return false;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) != 0;
        }
        return false;
      }
    }
  }

  public static void cluster(
      final Connection connection,
      final String schemaName,
      final String tableName,
      final String indexName)
      throws SQLException {
    try (Statement statement = connection.createStatement();) {
      final String activateVacumAnalyze = "CLUSTER \"" + schemaName + "\".\"" + tableName + "\" table_name USING \"" //$NON-NLS-1$ //$NON-NLS-2$
          + schemaName + "\".\"" + indexName + "\"";
      logger.log(ILevel.FINE, "Statement: " + activateVacumAnalyze); //$NON-NLS-1$
      statement.execute(activateVacumAnalyze);
    }
  }

  public static String createStatement(
      final Connection connection,
      final String schemaName,
      final String tableName)
          throws SQLException {
    String statementString = "SELECT 'CREATE TABLE ' || pn.nspname || '.' || pc.relname "
        + "   || E'(\\n' ||\n   string_agg(pa.attname || ' ' || pg_catalog.format_type(pa.atttypid, pa.atttypmod) "
        + "   || coalesce(' DEFAULT ' "
        + "               || (\n     SELECT pg_catalog.pg_get_expr(d.adbin, d.adrelid)\n"
        + "                            FROM pg_catalog.pg_attrdef d\n"
        + "                           WHERE d.adrelid = pa.attrelid\n"
        + "                             AND d.adnum = pa.attnum\n"
        + "                             AND pa.atthasdef\n"
        + "                 ),\n"
        + "   '') || ' ' ||\n"
        + "              CASE pa.attnotnull\n"
        + "                  WHEN TRUE THEN 'NOT NULL'\n"
        + "                  ELSE 'NULL'\n"
        + "              END, E',\\n') ||\n"
        + "   coalesce((SELECT E',\\n' || string_agg('CONSTRAINT ' || pc1.conname || ' ' || pg_get_constraintdef(pc1.oid), E',\\n' ORDER BY pc1.conindid)\n"
        + "            FROM pg_constraint pc1\n"
        + "            WHERE pc1.conrelid = pa.attrelid), '') ||\n"
        + "   E');'\n"
        + "FROM pg_catalog.pg_attribute pa\n"
        + "JOIN pg_catalog.pg_class pc\n"
        + "    ON pc.oid = pa.attrelid\n"
        + "    AND pc.relname = ?\n"
        + "JOIN pg_catalog.pg_namespace pn\n"
        + "    ON pn.oid = pc.relnamespace\n"
        + "    AND pn.nspname = ?\n"
        + "WHERE pa.attnum > 0\n"
        + "    AND NOT pa.attisdropped\n"
        + "GROUP BY pn.nspname, pc.relname, pa.attrelid;";
    logger.log(ILevel.FINE, "Query: Schema " + schemaName + " table " + tableName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    logger.log(ILevel.FINE, "Query: " + statementString); //$NON-NLS-1$
    try (PreparedStatement statement =
        connection.prepareStatement(statementString)) {
      statement.setString(2, schemaName);
      statement.setString(1, tableName);
      try {
        if (!statement.execute()) {
          return null;
        }
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "Query faild: " + statementString, exception); //$NON-NLS-1$
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        if (resultSet.next()) {
          return resultSet.getString(1);
        }
        return null;
      }
    }
  }
}