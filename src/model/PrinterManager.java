package model;

import model.customers.BigBoy;
import model.customers.Customer;
import model.customers.LittleFish;
import utils.ConsoleColors;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrinterManager {

    private static final int NR_OF_PRINTERS = 30;
    private ReentrantLock lock;
    private int nrOfPrintersInUse = 0;

    private Queue<LittleFish> littleFishQueue = new LinkedList<>();
    private Queue<BigBoy> bigBoyQueue = new LinkedList<>();

    private int amount_of_bigboys_processed = 0;
    private boolean process_littlefish = false;
    private boolean process_bigboy = false;
    private boolean bigboy_waiting = false;

    private Condition printerAvailable;
    private Condition newJob;
    private Condition allPrintersAvailable;
    private Condition bigBoyDone;

    public PrinterManager() {
        lock                 = new ReentrantLock();

        printerAvailable     = lock.newCondition();
        newJob               = lock.newCondition();
        allPrintersAvailable = lock.newCondition();
        bigBoyDone           = lock.newCondition();
    }

    public Customer getPrintJob() throws InterruptedException {
        lock.lock();
        try {

            while (noPrintJobs())
                newJob.await();

            while (bigboy_waiting)
                bigBoyDone.await();

            while (noPrinterAvailable())
                printerAvailable.await();

            System.out.println(ConsoleColors.CYAN_BACKGROUND + "Available printers: " + (NR_OF_PRINTERS - nrOfPrintersInUse) + ConsoleColors.RESET);
            if ((bigBoyQueue.size() > 0 && !process_littlefish) ||
                (process_bigboy && !process_littlefish) ||
                (littleFishQueue.size() == 0 && bigBoyQueue.size() > 0)) {

                Customer c = bigBoyQueue.poll();
                assert c != null;

                if (littleFishQueue.size() == 0)
                    process_littlefish = false;


                while (!allPrintersAvailable()) {
                    System.out.println(ConsoleColors.YELLOW_BACKGROUND + "Not all printers are available, waiting. " + c.getCustomerName() + ConsoleColors.RESET);
                    bigboy_waiting = true;
                    allPrintersAvailable.await();
                }


                System.out.println(ConsoleColors.BLUE_BACKGROUND + "Starting BigBoy: " + c.getCustomerName() + ConsoleColors.RESET);
                amount_of_bigboys_processed++;

                if (amount_of_bigboys_processed == 3) {
                    System.out.println(ConsoleColors.YELLOW_BACKGROUND + "Processed 3 BigBoys. Starting all LittleFish." + ConsoleColors.RESET);
                    process_littlefish = true;
                    process_bigboy = false;
                    amount_of_bigboys_processed = 0;
                } else if (bigBoyQueue.size() > 0) {
                    process_bigboy = true;
                } else {
                    amount_of_bigboys_processed = 0;
                    process_bigboy = false;
                }

                nrOfPrintersInUse = NR_OF_PRINTERS;

                System.out.println(ConsoleColors.CYAN_BACKGROUND + "Available printers: " + (NR_OF_PRINTERS - nrOfPrintersInUse) + ConsoleColors.RESET);

                return c;
            }

            else if (littleFishQueue.size() > 0) {

                while (noPrintJobs()) {
                    newJob.await();
                }

                Customer c = littleFishQueue.poll();

                if (littleFishQueue.size() == 0) {
                    process_littlefish = false;
                }

                nrOfPrintersInUse++;
                System.out.println(ConsoleColors.CYAN_BACKGROUND + "Available printers: " + lock.getHoldCount() + ConsoleColors.RESET);

                System.out.println(ConsoleColors.CYAN_BACKGROUND + "Starting LittleFish: " + c.getCustomerName() + ConsoleColors.RESET);

                return c;
            }

            else if (noPrintJobs()) {
//                process_littlefish = false;
                System.out.println("They took our jobs!");
            }

            assert true : "Code should never reach this point";

            System.out.println();
            System.out.println(ConsoleColors.RED_BACKGROUND + "ERROR: Returning NULL. " + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "Amount of jobs: " + getAmountOfJobs() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "Amount of printers in use: " + nrOfPrintersInUse + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "bigBoyQueue: " + bigBoyQueue.size() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "littleFishQueue: " + littleFishQueue.size() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "process_bigboy: " + process_bigboy + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED_BACKGROUND + "process_littlefish: " + process_littlefish + ConsoleColors.RESET);
            System.out.println();
            System.exit(0);

            return null;

        } finally {
            lock.unlock();
        }
    }

    public void releasePrinter(Customer customer) {
        lock.lock();
        try {
            if (customer instanceof BigBoy) {
                nrOfPrintersInUse = 0;
                bigboy_waiting = false;
                bigBoyDone.signal();
            }
            else {
                nrOfPrintersInUse--;
            }

            if (customer != null)
                System.out.println(ConsoleColors.GREEN_BACKGROUND + "Finished job for customer: " + customer.getCustomerName() + ConsoleColors.RESET);
            System.out.println();


            if (allPrintersAvailable())
                allPrintersAvailable.signal();
            else
                printerAvailable.signal();

        } finally {
            lock.unlock();
        }
    }

    public void sendPrintJob(Customer customer) {
        lock.lock();

        try {
            if (customer instanceof LittleFish) {
                littleFishQueue.add((LittleFish) customer);
            } else {
                bigBoyQueue.add((BigBoy) customer);
            }

            newJob.signal();
        } finally {
            lock.unlock();
        }

    }

    private boolean noPrinterAvailable() {
        return nrOfPrintersInUse == NR_OF_PRINTERS;
    }

    private boolean allPrintersAvailable() {
        return nrOfPrintersInUse == 0;
    }

    private boolean noPrintJobs() {
        return getAmountOfJobs() == 0;
    }

    private int getAmountOfJobs() {
        int amount_of_jobs = 0;

        amount_of_jobs += littleFishQueue.size();
        amount_of_jobs += bigBoyQueue.size();

        return amount_of_jobs;
    }

}
