/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.connections;

import com.oracle.refapp.exceptions.DatabaseConnectionException;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleException;
import oracle.soda.rdbms.OracleRDBMSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JSONConnection {

  private final OracleDatabase db;

  private static final Logger LOG = LoggerFactory.getLogger(JSONConnection.class);

  public JSONConnection(
    @Value("${datasources.default.url}") String jdbcUrl,
    @Value("${datasources.default.username}") String username,
    @Value("${datasources.default.password}") String password
  ) throws DatabaseConnectionException {
    Properties userCredentials = new Properties();
    userCredentials.setProperty("user", username);
    userCredentials.setProperty("password", password);
    try {
      Connection conn = DriverManager.getConnection(jdbcUrl, userCredentials);
      OracleRDBMSClient cl = new OracleRDBMSClient();
      db = cl.getDatabase(conn);
      OracleCollection collection = db.openCollection("ENCOUNTERS");
      if (collection == null) {
        db.admin().createCollection("ENCOUNTERS");
        LOG.info("Created collection ENCOUNTERS");
      }
    } catch (OracleException | SQLException e) {
      LOG.error("Error connecting to JSON DB : {}", e.getCause().toString());
      throw new DatabaseConnectionException("Unable to connect to Json DB -> " + e.getMessage());
    }
  }

  public OracleDatabase getDbConnection() {
    return db;
  }
}
