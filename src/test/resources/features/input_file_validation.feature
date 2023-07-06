Feature: Process input files and generate output file

Scenario: Load and transform input files
  Given the input file "InstrumentDetails.csv" and "PositionDetails.csv" is located in "src/csv_input/"
  When the application loads the input files
  And the data is transformed
  Then the database should be updated successfully

Scenario: Generate output file
  Given the transformed data is ready
  When the application generates the output file "PositionReport.csv"
  Then the output file "PositionReport.csv" should be generated in "src/csv_output/"