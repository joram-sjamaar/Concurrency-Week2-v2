package model;

import model.customers.BigBoy;
import model.customers.Customer;
import model.customers.LittleFish;
import utils.ConsoleColors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrinterManager {

    private static final int NR_OF_PRINTERS = 30;
    private Lock lock;
    private int nrOfCustomers = 0;

    private Condition printerAvailable,
                      newJob;

    public PrinterManager() {
        lock = new ReentrantLock();
        printerAvailable = lock.newCondition();
        newJob = lock.newCondition();
    }

    public Customer nextCustomer() throws InterruptedException {
        lock.lock();
        try {
            while (noCustomers())
                newJob.await();


            return null;

        } finally {
            lock.unlock();
        }
    }

    public void sendPrintJob(Customer customer) throws InterruptedException {

        lock.lock();

        try {

            // TODO
            while (noPrinterAvailable())
                printerAvailable.await();

            nrOfCustomers++;
            newJob.signal();

            if (customer instanceof LittleFish) {
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Hallo klein visje" + ConsoleColors.RESET);
            }

        } finally {
            lock.unlock();
        }
    }

    private boolean noPrinterAvailable() {
        return nrOfCustomers == NR_OF_PRINTERS;
    }

    private boolean noCustomers() {
        return nrOfCustomers == 0;
    }

}
