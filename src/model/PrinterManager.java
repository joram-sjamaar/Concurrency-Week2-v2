package model;

import model.customers.Customer;

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

            while (inChair == null)
                readyForCut.await();

            return inChair;

        } finally {
            lock.unlock();
        }
    }

    private void print() throws InterruptedException {

        lock.lock();

        try {

            // TODO
            while (noPrinterAvailable())
                printerAvailable.await();

            nrOfCustomers++;
            newJob.signal();

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
