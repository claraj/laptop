package com.laptop; /** @author Clara MCTC Java Programming Class */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;


/** Handles all database interactions. */

public class InventoryModel {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";        //Configure the driver needed
    static final String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/";     //Connection string – where's the database?

    static final String DB_NAME = "laptops";    // TODO create this table

    static final String USER = "clara";          //TODO replace with your username
    static final String PASSWORD = "password";   //TODO replace with your password

    InventoryController myController;

    Connection connection = null;

    // Lists to contain all statements and result sets - used at end of program to close all of these */
    LinkedList<Statement> allStatements = new LinkedList<Statement>();
    LinkedList<ResultSet> allResultSets = new LinkedList<ResultSet>();

    // Some of these we'll reuse, so declare as global variables.
    PreparedStatement psReassignLaptop = null;
    PreparedStatement psGetLaptopById = null;
    PreparedStatement psAddLaptop;

    Statement statementTestData = null;
    Statement statementCreateTable = null;
    Statement statementGetAll = null;

    ResultSet rsAllLaptops = null;
    ResultSet rsOneLaptop = null;

    public InventoryModel(InventoryController controller) {

        this.myController = controller;

        try {

            //Instantiate an instance of the JDBC driver class.

            Class.forName(JDBC_DRIVER);

            //When the driver class is instantiated, it should register itself with the DriverManager.
            //You don't need to do anything else here.

        } catch (ClassNotFoundException ce) {
            throw new RuntimeException("Can't find JDBC driver class.");
        }


        //Add all of the global statements and result sets to the appropriate list.
        // If you create another statement, prepared statement or result set, add it to the list too.
        allStatements.add(statementTestData);
        allStatements.add(statementCreateTable);
        allStatements.add(statementGetAll);

        allStatements.add(psReassignLaptop);
        allStatements.add(psGetLaptopById);

        allResultSets.add(rsAllLaptops);
        allResultSets.add(rsOneLaptop);

    }


    public void setupDatabase() {

        //**TODO change this from true to false as you need for testing/debugging **//

        setupDatabase(true);  //true = delete and recreate database, false = keep existing database

    }

    public void setupDatabase(boolean resetWithTestData) {

        try {
            createConnection();

        } catch (SQLException sqle) {
            dbError(sqle, "Unable to connect to database");
        }

        createTable();

        if (resetWithTestData) {
            addTestData();
        }


        //TODO Remove the test data for real program

    }


    private void createTable() {

        String createLaptopTableSQL = "CREATE TABLE IF NOT EXISTS laptops (id INT PRIMARY KEY AUTO_INCREMENT, make VARCHAR(30), model VARCHAR(30), staff VARCHAR(50))";

        try {

            Statement statementCreateTable = connection.createStatement();
            statementCreateTable.execute(createLaptopTableSQL);

        } catch (SQLException sqle) {
            dbError(sqle, "Error creating table with this SQL: \n " + createLaptopTableSQL);
        }
    }

    private void createConnection() throws SQLException {

        connection = DriverManager.getConnection(DB_CONNECTION_URL + DB_NAME, USER, PASSWORD);

    }


    private void addTestData() {

        try {
            //Delete all records and add a set of example data.

            statementTestData = connection.createStatement();
            String deleteAll = "DELETE FROM laptops";
            statementTestData.execute(deleteAll);

            String addRecord1 = "INSERT INTO laptops (make, model, staff) VALUES ('Toshiba', 'XQ-45', 'Ryan' )";
            statementTestData.executeUpdate(addRecord1);
            String addRecord2 = "INSERT INTO laptops (make, model, staff) VALUES ('Sony', '1234', 'Jane' )";
            statementTestData.executeUpdate(addRecord2);
            String addRecord3 = "INSERT INTO laptops (make, model, staff) VALUES ('Apple', 'Air', 'Alex' )";
            statementTestData.executeUpdate(addRecord3);

        } catch (SQLException sqle) {
            dbError(sqle, "Unable to add test data, check validity of SQL statements?");
        }
    }


    public void cleanup() {

        try {

            for (ResultSet rs : allResultSets) {
                if (rs != null) {
                    rs.close();  //Close result set
                }
            }
        } catch (SQLException sqle) {
            dbError(sqle, "Error closing result set");
        }

        try {

            for (Statement s : allStatements) {
                if (s != null) {
                    s.close();
                }
            }
        } catch (SQLException sqle) {
            dbError(sqle, "Error closing Statements/Prepared Statements");
        }


        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            dbError(sqle, "Error closing database connection");
        }
    }


    public void addLaptop(Laptop laptop) {


        //Create SQL query to add this laptop info to DB

        String addLaptopSQLps = "INSERT INTO laptops (make, model, staff) VALUES ( ? , ? , ? )";

        try {
            psAddLaptop = connection.prepareStatement(addLaptopSQLps, PreparedStatement.RETURN_GENERATED_KEYS);
            psAddLaptop.setString(1, laptop.getMake());
            psAddLaptop.setString(2, laptop.getModel());
            psAddLaptop.setString(2, laptop.getModel());
            psAddLaptop.setString(3, laptop.getStaff());
            psAddLaptop.execute();

            //Retrieve new laptop ID and add it to the com.laptop.Laptop
            ResultSet keys = psAddLaptop.getGeneratedKeys();

            //We assume just one key, which will be the first thing in the ResultSet

            keys.next();

            laptop.id = keys.getInt(1);    // Pass by reference!


        } catch (SQLException sqle) {
            dbError(sqle, "Error preparing statement or executing prepared statement to add laptop");
        }
    }


    /**
     * @return list of laptops in the DB (will be empty list if no laptops found in DB)
     */

    public LinkedList<Laptop> getAllLaptops() {

        LinkedList<Laptop> allLaptops = new LinkedList<Laptop>();

        String displayAllSQL = "SELECT * FROM laptops";

        try {
            statementGetAll = connection.createStatement();
            rsAllLaptops = statementGetAll.executeQuery(displayAllSQL);

        } catch (SQLException sqle) {
            dbError(sqle, "Database error fetching all laptops");
        }

        try {
            while (rsAllLaptops.next()) {

                int id = rsAllLaptops.getInt("id");
                String make = rsAllLaptops.getString("make");
                String model = rsAllLaptops.getString("model");
                String staff = rsAllLaptops.getString("staff");

                Laptop l = new Laptop(id, make, model, staff);
                allLaptops.add(l);

            }

        } catch (SQLException sqle) {
            dbError(sqle, "Error reading from result set after fetching all laptop data");
        }

        //if we get here, everything should have worked...
        //Return the list of laptops, which will be empty if there is no data in the database
        return allLaptops;
    }


    /**
     * @return laptop object for a laptop ID.  Returns null if the ID is not found.
     * @throws RuntimeException if SQL error occurs
     */

    public Laptop getLaptop(int id) {

        Laptop laptop = null;

        try {
            String fetchLaptop = "SELECT * FROM laptop where id = ?";

            psGetLaptopById = connection.prepareStatement(fetchLaptop);

            psGetLaptopById.setInt(1, id);

            rsOneLaptop = psGetLaptopById.executeQuery();


            if (rsOneLaptop.next()) {
                String make = rsOneLaptop.getString("make");
                String model = rsOneLaptop.getString("model");
                String staff = rsOneLaptop.getString("staff");
                laptop = new Laptop(id, make, model, staff);
            }

        } catch (SQLException sqle) {
            dbError(sqle, "Database error fetching laptop for ID " + id);
        }

        return laptop;   //Will be null if the rsOneLaptop has no rows.

    }

    //TODO test this method. Use it in the code.

    /**
     * @return true if laptop update is successful or false if laptop not updated = this will be because the id isn't in the database
     */

    public boolean reassignLaptop(int id, String newUser) {

        try {

            //Does this laptop exist in the database?
            Laptop reassign = getLaptop(id);

            if (reassign == null) {
                return false;          //com.laptop.Laptop not found.
            }

            String reassignLaptopSQL = "UPDATE laptop SET staff = ? WHERE id = ?";

            psReassignLaptop = connection.prepareStatement(reassignLaptopSQL);

            psReassignLaptop.setInt(1, id);
            psReassignLaptop.setString(2, newUser);

            psReassignLaptop.executeUpdate();

        } catch (SQLException sqle) {
            dbError(sqle, "Error changing staff assignment laptop number + id");
        }

        return true;
    }


    /* Prints error messages then throws RuntimeException to crash the program. If there's a DB connection error, there's nothing that can be done
    * in the running program to recover. The developer needs to investigate the cause (DB server down? SQL statement has errors?) and fix.  */
    private void dbError(SQLException sqle, String msg) {
        System.err.println(msg);
        System.err.println(sqle.getMessage());
        System.err.println(sqle.getErrorCode());
        throw new RuntimeException(sqle);
    }
}




