package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The entry point of the program.
 */
public class App {
    private static final Logger logger = LogManager.getLogger("App");
    /**
     * Initialization of the parking system.
     * @see InteractiveShell
     */
    public static void main(String args[]) {
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
