package com.stepdefinition.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.util.ConfigurationManager;
import com.util.SqlQueries;

public class DatabaseHandler {

	private static final String JDBC_URL = "jdbc.url";
	private static final String JDBC_UN= "jdbc.username";
	private static final String JDBC_PW = "jdbc.password";
	
	private static Connection getConnection() throws SQLException {
		
		ConfigurationManager dbConfiguration = ConfigurationManager.getInstance();
		
		String jdbc_url = dbConfiguration.getProperty(JDBC_URL);
		String jdbc_username = dbConfiguration.getProperty(JDBC_UN);
		String jdbc_password = dbConfiguration.getProperty(JDBC_PW);
		
		return DriverManager.getConnection(jdbc_url, jdbc_username, jdbc_password);
	}
	
	public static PreparedStatement getPreparedStatement(String sqlQuery) throws SQLException {
		
		return getConnection().prepareStatement(sqlQuery);
	}
	
	public static Statement getCreateStatement() throws SQLException {
		
		return getConnection().createStatement();
	}
	
	public static ResultSet getResultSet(String sql) throws SQLException {
		Statement statement = getCreateStatement();
		return statement.executeQuery(sql);
	}
	
	public static int getRecordCountInPositionReport() throws SQLException {
		
		ResultSet resultSet = getResultSet(SqlQueries.SELECT_POSITION_REPORT_COUNT);
		resultSet.next();
	     
		return resultSet.getInt(1);
	}
	
	
}
