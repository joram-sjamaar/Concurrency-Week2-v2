import model.Printer;
import model.PrinterManager;
import model.customers.LittleFish;

public class Apl {

    private static final int NR_OF_CUSTOMERS = 9;

    public static void main(String[] args) {

        // TODO: Je kanker moeder
        PrinterManager printerManager = new PrinterManager();
        Thread[] customer;
        Thread printer;
        customer = new Thread[NR_OF_CUSTOMERS];

        printer = new Printer(printerManager);
        printer.start();

        for (int i = 0; i < NR_OF_CUSTOMERS; i++) {
            customer[i] = new LittleFish("c" + i, printerManager);
            customer[i].start();
        }

    }

}
