import model.Printer;
import model.PrinterManager;
import model.customers.BigBoy;
import model.customers.Customer;
import model.customers.LittleFish;
import utils.ConsoleColors;

import java.util.Random;

public class Apl {

    private PrinterManager printerManager = new PrinterManager();

    public static void main(String[] args) {
        new Apl().run();
    }

    private void run() {

        createRandomCustomers(250);
//        createBigBoys(100);
//        createLittleFishes(100);

        createPrinters(20);
//        generateRandomCustomers(500);

    }

    /**
     * Generates Customers. Randomly selects between BigBoys or LittleFish
     *
     * Chances:
     *   - 20% BigBoy
     *   - 80% LittleFish
     *
     * @param speed how fast customers should be created. (In ms)
     */
    private void generateRandomCustomers(long speed) {
        while (true) {
            createRandomCustomer();
            sleep(speed);
        }
    }

    /**
     * Pauses the thread
     *
     * @param time  time in ms
     */
    private void sleep(long time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a random customer
     *
     * Chances:
     *   - 20% BigBoy
     *   - 80% LittleFish
     *
     * @return Generated customer
     */
    private Customer createRandomCustomer() {

        if (new Random().nextInt(10) + 1 < 8) {
            return createLittleFish();
        } else {
            return createBigBoy();
        }
    }

    /**
     * Creates random customers
     *
     * @param amount amount to create
     */
    private void createRandomCustomers(int amount) {
        for (int i = 0; i < amount; i++) {
            createRandomCustomer();
        }
    }

    /**
     * Creates a LittleFish thread and starts it.
     *
     * @return Created LittleFish
     */
    private LittleFish createLittleFish() {
        LittleFish l = new LittleFish("Littlefish " + (new Random().nextInt(10000)), printerManager);
        System.out.println(ConsoleColors.WHITE_BACKGROUND + "Created LittleFish: " + l.getCustomerName() + ConsoleColors.RESET);
        l.start();
        return l;
    }

    /**
     * Creates LittleFishes
     *
     * @param amount    amount to create
     */
    private void createLittleFishes(int amount) {
        for (int i = 0; i < amount; i++) {
            createLittleFish();
        }
    }

    /**
     * Creates a BigBoy
     *
     * @return Created BigBoy
     */
    private BigBoy createBigBoy() {
        BigBoy b = new BigBoy("BigBoy " + new Random().nextInt(10000), printerManager);
        System.out.println(ConsoleColors.WHITE_BACKGROUND + "Created BigBoy:" + b.getCustomerName() + ConsoleColors.RESET);
        b.start();
        return b;
    }

    /**
     * Creates BigBoys
     *
     * @param amount amount to create
     */
    private void createBigBoys(int amount) {
        for (int i = 0; i < amount; i++) {
            createBigBoy();
        }
    }

    /**
     * Creates a Printer and starts it.
     *
     * @return Printer
     */
    private Printer createPrinter() {
        Printer p = new Printer(printerManager);
        p.start();
        return p;
    }

    /**
     * Creates Printers
     *
     * @param amount amount to create
     */
    private void createPrinters(int amount) {
        for (int i = 0; i < amount; i++) {
            createPrinter();
        }
    }

}
