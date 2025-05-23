package org.petya8bachey;

import org.petya8bachey.service.DatabaseService;
import org.petya8bachey.ui.MainMenu;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try (DatabaseService dbService = new DatabaseService()) {
            MainMenu mainMenu = new MainMenu(dbService);
            mainMenu.run();
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}