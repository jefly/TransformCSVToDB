package com.stepdefinition.server;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.stepdefinition.database.DatabaseHandler;
import com.util.FileUtil;
import com.util.SqlQueries;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FileVerificationStepDef {

	private String filePath = null;
	private Connection connection = null;
	private String instrumentFile = null;
	private String positionFile = null;
	File directory = null;

	@Given("^the input file \"([^\"]*)\" and \"([^\"]*)\" is located in \"([^\"]*)\"$")
	public void inputFileIsLocated(String instrumentFile, String positionFile, String path) {

		this.filePath = path;
		this.instrumentFile = instrumentFile;
		this.positionFile = positionFile;

		boolean file1Exists = checkFileExists(instrumentFile, filePath);
		boolean file2Exists = checkFileExists(positionFile, filePath);
		assertTrue(file1Exists, FileUtil.DB_SUCCESS_MSG + instrumentFile);
		assertTrue(file2Exists, FileUtil.POS_NOT_FOUND_MSG + positionFile);

	}

	@When("the application loads the input files")
	public void loadInputFiles() {

		loadInstrumentFile(filePath + instrumentFile);
		loadPositionFile(filePath + positionFile);
	}

	@When("^the data is transformed$")
	public void transformData() {
		transformDataInDatabase();
	}

	@Then("^the database should be updated successfully$")
	public void verifyDatabaseUpdate() {

		boolean databaseUpdated = checkDatabaseUpdate();
		assertTrue(databaseUpdated, FileUtil.DB_SUCCESS_MSG);

	}

	private boolean checkFileExists(String inputFile, String path) {

		File file = new File(path + inputFile);
		return file.exists();
	}

	private void loadInstrumentFile(String instrumentFile) {

		boolean firstTime = true;

		try (CSVParser parser = CSVFormat.DEFAULT.parse(Files.newBufferedReader(Paths.get(instrumentFile)))) {

			PreparedStatement statement = DatabaseHandler.getPreparedStatement(SqlQueries.INSERT_INSTRUMENT);

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
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadPositionFile(String positionFile) {

		boolean firstTime = true;
		try (CSVParser parser = CSVFormat.DEFAULT.parse(Files.newBufferedReader(Paths.get(positionFile)))) {

			PreparedStatement statement = DatabaseHandler.getPreparedStatement(SqlQueries.INSERT_POSITION);

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
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Given("^the transformed data is ready$")
	public void transformedDataIsReady() {

		boolean transformedDataReady = checkTransformedDataReady();

		assertTrue(transformedDataReady, FileUtil.TRNSFRM_DATA_RDY_MSG);
	}

	@When("^the application generates the output file \"([^\"]*)\"$")
	public void generateOutputFile(String outputFile) {
		generateReport(FileUtil.OUTPUT_PATH + outputFile);
	}

	@Then("^the output file \"([^\"]*)\" should be generated in \"([^\"]*)\"$")
	public void verifyOutputFile(String fileName, String filePath) {

		boolean fileExists = checkFileExists(fileName, filePath);

		assertTrue(fileExists, FileUtil.OUT_NOT_FOUND_MSG + fileName);
	}

	private void transformDataInDatabase() {

		try {
			PreparedStatement statement = DatabaseHandler.getPreparedStatement(SqlQueries.INSERT_POSITION_REPORT);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkDatabaseUpdate() {

		try {
			
			int count = DatabaseHandler.getRecordCountInPositionReport();
			return count > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void generateReport(String outputFile) {

		try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT.withHeader("ID", "PositionID", "ISIN", "Quantity", "Total Price"))) {

			ResultSet resultSet = DatabaseHandler.getResultSet(SqlQueries.SELECT_POSITION_REPORT);

			while (resultSet.next()) {
				String id = resultSet.getString(FileUtil.ID);
				String positionId = resultSet.getString(FileUtil.POS_ID);
				String isin = resultSet.getString(FileUtil.ISIN);
				String quantity = resultSet.getString(FileUtil.QTY);
				String totalPrice = resultSet.getString(FileUtil.TOTAL);

				printer.printRecord(id, positionId, isin, quantity, totalPrice);
			}

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkTransformedDataReady() {

		try {

			int count = DatabaseHandler.getRecordCountInPositionReport();

			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
