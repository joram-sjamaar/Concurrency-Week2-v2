package model.customers;

import model.PrinterManager;

public abstract class Customer extends Thread {

    private String name;
    private PrinterManager printerManager;

    public Customer(String name, PrinterManager printerManager) {
        this.name = name;
        this.printerManager = printerManager;
    }

    public void run() {
        while (true) {
            try {
                printerManager.sendPrintJob(this);
            } catch (InterruptedException e) {}
        }
    }
}
