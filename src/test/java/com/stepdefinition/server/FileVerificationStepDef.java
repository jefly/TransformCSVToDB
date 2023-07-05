package com.stepdefinition.server;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FileVerificationStepDef {
	
	private String inputFileDirectory = "/app/in"; 
	private String insFile1 = "InstrumentDetails.csv";
	private String posFile2 = "PositionDetails.csv";
	private final String JDBC_URL = "jdbc:mysql://localhost:3306/cucumber";
	private final String JDBC_UN = "root";
	private final String JDBC_PW = "123456";
	private String filePath = null;
	private Connection connection = null;
	private String instrumentFile = null;
	private String positionFile = null;
	File directory = null;
	
//	@Given("I have input files in the \"/app/in\" directory")
//	public void getInputFiles() {
//		
//		
//	}
	
	@Given("^the input file \"([^\"]*)\" and \"([^\"]*)\" is located in$")
//	@Given("^the input file \"([^\"]*)\" and \"([^\"]*)\" is located in \"([^\"]*)\"$")
	  public void inputFileIsLocated(String instrumentFile, String positionFile) {

	    this.filePath = "src/csv_input/";
	    this.instrumentFile = instrumentFile;
	    
	    boolean file1Exists = checkFileExists(instrumentFile, filePath);
	    boolean file2Exists = checkFileExists(positionFile, filePath);
	    assertTrue(file1Exists, "Instrument file doesn't exist: " + instrumentFile);
	    assertTrue(file2Exists, "Position file doesn't exist: " + positionFile);
	    
	  }
	
	@When("the application loads the input files")
	public void loadInputFiles() {
		
		loadInstrumentFile(filePath + instrumentFile);
	    loadPositionFile(filePath + positionFile);
		
//		try {
//			
//			Path path = Paths.get("src/csv/InstrumentDetails.csv");
//			
//			try(BufferedReader reader = Files.newBufferedReader(path);
//				CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
//				
//				boolean isFirstRecord = true;
//				
//				for(CSVRecord record : parser) {
//					
//					if(isFirstRecord) {
//						isFirstRecord = false;
//						continue;
//					}
//					
//					String id = record.get(0);
//					String name = record.get(1);
//					String isin = record.get(2);
//					String unitPrice = record.get(3);
//					
//					System.out.println(id);
//					
//				}
//			}
//			
//		} catch(IOException e) {
//			
//			
//		}
		
	}
	
	@When("^the data is transformed$")
	  public void transformData() {
	    transformDataInDatabase();
	 }
	
	@Then("^the database should be updated successfully$")
	  public void verifyDatabaseUpdate() {
		
	    boolean databaseUpdated = checkDatabaseUpdate();
	    assertTrue(databaseUpdated, "Database update success");
	    
	  }
	
	private boolean checkFileExists(String inputFile, String path) {
		
		File file = new File(path + inputFile);
	    return file.exists();
	}
	
	private void loadInstrumentFile(String instrumentFile) {
		
		boolean firstTime = true;
		
		try (CSVParser parser = CSVFormat.DEFAULT.parse(Files.newBufferedReader(Paths.get("src/csv_input/InstrumentDetails.csv")))) {
			
	      connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
	      PreparedStatement statement = connection.prepareStatement("INSERT INTO instrument_details (id, name, isin, unit_price) VALUES (?, ?, ?, ?)");
	      
	      for (CSVRecord record : parser) {
	    	  
	    	  if(firstTime) {
	    		  firstTime = false;
	    		  continue;
	    	  }
	    	  
	        String id = record.get(0);
	        String name = record.get(1);
	        String isin = record.get(2);
	        String unitPrice = record.get(3);
	        
	        statement.setString(1, id);
	        statement.setString(2, name);
	        statement.setString(3, isin);
	        statement.setString(4, unitPrice);
	        
	        statement.executeUpdate();
	      }
	    } catch (IOException | SQLException e) {
	      e.printStackTrace();
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
	
	private void loadPositionFile(String positionFile) {
		
		boolean firstTime = true;
//		try (CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(positionFile))) {
		try (CSVParser parser = CSVFormat.DEFAULT.parse(Files.newBufferedReader(Paths.get("src/csv_input/PositionDetails.csv")))) {
			
	      connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
	      PreparedStatement statement = connection.prepareStatement("INSERT INTO position_details (id, instrument_id, qty) VALUES (?, ?, ?)");
	      
	      for (CSVRecord record : parser) {
	    	  
	    	  if(firstTime) {
	    		  firstTime = false;
	    		  continue;
	    	  }
	    	  
	        String positionId = record.get(0);
	        String instrumentId = record.get(1);
	        String quantity = record.get(2);
	        
	        statement.setString(1, positionId);
	        statement.setString(2, instrumentId);
	        statement.setString(3, quantity);
	        
	        statement.executeUpdate();
	      }
	    } catch (IOException | SQLException e) {
	      e.printStackTrace();
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
	
	@Given("^the transformed data is ready$")
	public void transformedDataIsReady() {
		
	   boolean transformedDataReady = checkTransformedDataReady();
	   
	   assertTrue(transformedDataReady, "Transformed data is ready");
	}

	@When("^the application generates the output file$")
	public void generateOutputFile() {
	    String outputFile = "src/csv_output/PositionReport.csv";
	    generateReport(outputFile);
	}
	
	@Then("^the output file \"([^\"]*)\" should be generated in \"([^\"]*)\"$")
	  public void verifyOutputFile(String fileName, String filePath) {
		
	    boolean fileExists = checkFileExists(fileName, filePath);
	    
	    assertTrue(fileExists, "Output file doesn't exist: " + fileName);
	 }
	
	private void transformDataInDatabase() {
		
		try {
		      connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
		      PreparedStatement statement = connection.prepareStatement("INSERT INTO position_report (id, position_id, isin, qty, total_price) SELECT p.id, p.id, i.isin, p.qty, p.qty * i.unit_price FROM position_details p JOIN instrument_details i on p.instrument_id = i.id");
		      
		      statement.executeUpdate();
		      
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
	
	private boolean checkDatabaseUpdate() {
		
		try {
		      connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
		      Statement statement = connection.createStatement();
		      
		      String sql = "SELECT COUNT(*) FROM position_report WHERE total_price IS NOT NULL";
		      
		      ResultSet resultSet = statement.executeQuery(sql);
		      resultSet.next();
		      int count = resultSet.getInt(1);
		      
		      return count > 0;
		      
	    } catch (SQLException e) {
	      e.printStackTrace();
	      return false;
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
	
	private void generateReport(String outputFile) {
		
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT.withHeader("ID", "PositionID", "ISIN", "Quantity", "Total Price"))) {
			
			connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
			Statement statement = connection.createStatement();
	      
//			String sql = "SELECT pd.ID, pd.PositionID, id.ISIN, pd.Quantity, pd.Total_Price FROM position_details pd INNER JOIN instrument_details id ON pd.InstrumentID = id.ID";
			String sql = "SELECT id, position_id, isin, qty, total_price FROM position_report";
			ResultSet resultSet = statement.executeQuery(sql);
	      
			while (resultSet.next()) {
		        String id = resultSet.getString("id");
		        String positionId = resultSet.getString("position_id");
		        String isin = resultSet.getString("isin");
		        String quantity = resultSet.getString("qty");
		        String totalPrice = resultSet.getString("total_price");
		        
		        printer.printRecord(id, positionId, isin, quantity, totalPrice);
			}
	      
	    } catch (IOException | SQLException e) {
	      e.printStackTrace();
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
	
	private boolean checkTransformedDataReady() {
		
		try {
			
	      connection = DriverManager.getConnection(JDBC_URL, JDBC_UN, JDBC_PW);
	      Statement statement = connection.createStatement();
	      
	      String sql = "SELECT COUNT(*) FROM position_report WHERE total_price IS NOT NULL";
	      
	      ResultSet resultSet = statement.executeQuery(sql);
	      resultSet.next();
	      int count = resultSet.getInt(1);
	      
	      return count > 0;
	    } catch (SQLException e) {
	      e.printStackTrace();
	      return false;
	    } finally {
	      if (connection != null) {
	        try {
	          connection.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	}
}
