package com.laptop;

import java.util.LinkedList;


/** Main method, creates object to interact with DB, and object to provide UI */

public class InventoryController {


    static InventoryModel db ;

    public static void main(String[] args) {

        try {
            InventoryController controller = new InventoryController();
            db = new InventoryModel(controller);
            db.setupDatabase();
            new InventoryView(controller).launchUI();
        }

        finally {
            if (db != null) {
                db.cleanup();   //Whether there were errors or not, always need to clean up and close connection.
            }
        }

    }


    public void addLaptop(Laptop l) {

        //This message should arrive from the UI. Send a message to the db to request that this laptop is added.

        db.addLaptop(l);

    }

    public LinkedList<Laptop> requestAllInventory() {


        //This message should arrive from the UI. Send a message to the db to request all laptop data.
        //Returns a list of laptop objects

        LinkedList<Laptop> allLaptops = db.getAllLaptops();
        return allLaptops;


    }

    public Laptop requestLaptopById(int id) {

        //This message should arrive from the UI. Send a message to the db to request this laptop.
        //Returns a com.laptop.Laptop object if laptop is found, or null if it is not found.

        return db.getLaptop(id);

    }
}

