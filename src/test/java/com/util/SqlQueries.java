package com.util;

public interface SqlQueries {

	String INSERT_INSTRUMENT = "INSERT INTO instrument_details (id, name, isin, unit_price) VALUES (?, ?, ?, ?)";
	String INSERT_POSITION = "INSERT INTO position_details (id, instrument_id, qty) VALUES (?, ?, ?)";
	String INSERT_POSITION_REPORT = "INSERT INTO position_report (id, position_id, isin, qty, total_price) SELECT p.id, p.id, i.isin, p.qty, p.qty * i.unit_price FROM position_details p JOIN instrument_details i on p.instrument_id = i.id";
	String SELECT_POSITION_REPORT_COUNT = "SELECT COUNT(*) FROM position_report WHERE total_price IS NOT NULL";
	String SELECT_POSITION_REPORT = "SELECT id, position_id, isin, qty, total_price FROM position_report";
	
}
