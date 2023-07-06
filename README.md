# TransformCSVToDB (Cucumber, Java, JDBC, MySQL)
This is a lightweight tool that enables seamless loading of CSV data columns into a database while providing transformation capabilities. Simplify your data import process and effortlessly transform your CSV data for efficient database integration

1. Create a MySQL Connection using the user `root` and the password `123456` and connect to it.
2. Create a database called "cucumber"
   `create database cucumber;`

3. Select the newly created database
   `use cucumber;`

4. Create the below tables to feed data from the CSV files.

```
CREATE TABLE instrument_details (
    id INT(11) NOT NULL,
    name VARCHAR(255) NOT NULL,
    isin VARCHAR(11),
    unit_price DECIMAL(10,2),
    PRIMARY KEY (id)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE position_details (
     id INT(11) NOT NULL,
     instrument_id INT(11) NOT NULL,
     qty DECIMAL(10,2),
     PRIMARY KEY (id)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE position_report (
   id INT(11) NOT NULL AUTO_INCREMENT,
   position_id INT(11) NOT NULL,
   isin INT(11) NOT NULL,
   qty DECIMAL(10,2),
   total_price DECIMAL(10,2),
   PRIMARY KEY (id)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

5. Download the project file, extract it and create a folder called `csv_output` under `src` folder. It already contains a folder called `csv_input`.
6. Download maven from `https://maven.apache.org/install.html`, copy the path to the bin folder and add it to the `'PATH'` variable under environment variables (user & system).
7. Check the maven is set up properly by entering the `mvn -version` on a terminal.
8. Go to the root of the extracted project, open a terminal for that location & type `mvn clean install` which will download the necessary dependencies for the project.
9. Run `mvn test`.
10. This will give you a URL to view the test results in the end of the log inside the terminal ` View your Cucumber Report at:` and the next line URL starts as `https://reports.cucumber.io/reports/..`. Copy the URL to the browser, paste it and hit enter.
11. You can see the output fille `PositionReport.csv` inside `csv_output` folder.
