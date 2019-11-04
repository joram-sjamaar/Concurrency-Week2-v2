package model;

import model.customers.Customer;

import java.util.Random;

public class Printer extends Thread {

    private PrinterManager printerManager;

    public Printer(PrinterManager printerManager) {
        this.printerManager = printerManager;
    }

    public void run() {
        while (true) {
            try {
                Customer customer = printerManager.getPrintJob();
//                print();
                printerManager.releasePrinter(customer);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                e.printStackTrace();
            }
        }
    }

    public void print() {
        try {
            Thread.sleep(500 + (new Random().nextInt(5) *100));
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
            e.printStackTrace();
        }
    }

}
