package com.laptop;

/** Handles user interaction. Could be replaced by GUI if desired. */

import java.util.LinkedList;
import java.util.Scanner;

public class InventoryView  {

    private final int QUIT = 5;   //Modify if you add more menu items.

    //TODO Can you think of a more robust way of handling menu options which would be easy to modify with a varying number of menu choices?


    InventoryController myController;
    Scanner scanner = new Scanner(System.in);

    InventoryView(InventoryController c) {

        myController = c;
    }


    public void launchUI() {

        //This is a text-based UI. Probably a GUI in a real program

        while (true) {

            int userChoice = displayMenuGetUserChoice();

            if (userChoice == QUIT ) {
                return;
            }

            doTask(userChoice);
        }

    }


    private void doTask(int userChoice) {

        switch (userChoice) {

            case 1:  {
                displayAllInventory();
                break;
            }
            case 2: {
                addNewLaptop();
                break;
            }
            case 3 : {
                //TODO finish this
                System.out.println("Reassign laptop - In the process of being implemented");
                reassignLaptop();
                break;
            }
            case 4 : {
                //TODO implement this
                System.out.println("Retire laptop - Not yet implemented");
                retireLaptop();
                break;
            }
        }

    }

    private void retireLaptop() {

        //TODO ask controller, to ask database, to delete this laptop.

        // Optional: modify database to include a boolean column called 'active'.
        // The DB can then store whether laptops are active or retired. UI can
        // use this info, e.g. to display only active laptops.

    }

    private void reassignLaptop() {


        //TODO finish this

        //Ask for laptop ID
        //Fetch laptop info and display for user to confirm this is the correct laptop

        System.out.println("Enter laptop ID to reassign");
        int id = getPositiveIntInput();

        displayLaptopById(id);

        //TODO once laptop has been found, ask for new staff member's name
        //TODO Write this to the database, see draft method in com.laptop.InventoryModel

    }


    private void addNewLaptop() {

        //Get data about new laptop from user

        System.out.println("Please enter make of laptop (e.g. Toshiba, Sony) : ");
        String make = scanner.nextLine();

        System.out.println("Please enter make of laptop (e.g. Macbook, X-123) : ");
        String model = scanner.nextLine();

        System.out.println("Please enter name of staff member laptop is assigned to : ");
        String staff = scanner.nextLine();

        Laptop l = new Laptop(make, model, staff);

        myController.addLaptop(l);

        System.out.println("New laptop added to database");


    }


    private void displayAllInventory() {

        LinkedList<Laptop> allLaptops = myController.requestAllInventory();

        if (allLaptops.isEmpty()) {
            System.out.println("No laptops found in database");

        } else {

            System.out.println("List of all laptops in the database:");
            for (Laptop l : allLaptops) {
                System.out.println(l);   //Call the toString method in com.laptop.Laptop
            }

        }
    }


    private void displayLaptopById(int id) {

        Laptop l = myController.requestLaptopById(id);

        if (l == null) {

            System.out.println("com.laptop.Laptop " + id + " not found");

        } else {

            System.out.println(l);   //Call the toString method in com.laptop.Laptop

        }
    }


    private int displayMenuGetUserChoice() {

        boolean inputOK = false;
        int userChoice = -1;

        while (!inputOK) {

            System.out.println("1. View all inventory");
            System.out.println("2. Add a new laptop");
            System.out.println("3. To be added - reassign a laptop to another staff member");
            System.out.println("4. To be added - retire a laptop");
            System.out.println(QUIT + ". Quit program");

            System.out.println();
            System.out.println("Please enter your selection");

            userChoice = getPositiveIntInput();

            if (userChoice >= 1  && userChoice <= 5) {
                inputOK = true;
            }

            else {
                System.out.println("Please enter a number between 1 and 5");
            }

        }

        return userChoice;

    }


    //Validation method

    private int getPositiveIntInput() {

        while (true) {
            try {
                String stringInput = scanner.nextLine();
                int intInput = Integer.parseInt(stringInput);
                if (intInput >= 0) {
                    return intInput;
                } else {
                    System.out.println("Please type a positive number");
                }
            } catch (NumberFormatException ime) {
                System.out.println("Please type a positive number");
            }
        }

    }

}