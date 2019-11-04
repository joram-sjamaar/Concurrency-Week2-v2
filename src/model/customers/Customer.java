package model.customers;

import model.PrinterManager;
import utils.ConsoleColors;

import java.util.Random;

public abstract class Customer extends Thread {

    private String name;
    private PrinterManager printerManager;
    private boolean isPrinted = false;

    public Customer(String name, PrinterManager printerManager) {
        this.name = name;
        this.printerManager = printerManager;
    }

    public void run() {
        printerManager.sendPrintJob(this);
    }

    private void stayAlive() {
        try {
            Thread.currentThread().sleep(500 +
                    ((new Random().nextInt(5)) * 200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getCustomerName() {
        return name;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }
}
