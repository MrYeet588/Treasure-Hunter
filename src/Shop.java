import java.awt.*;
import java.util.Scanner;

/**
 * The Shop class controls the cost of the items in the Treasure Hunt game. <p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method. <p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Shop {
    // constants
    private int waterCost = 2;
    private int ropeCost = 4;
    private int macheteCost = 6;
    private int horseCost = 12;
    private int boatCost = 20;
    private int bootsCost = 10;
    private int shovelCost = 8;
    private int swordCost = 0;
    private OutputWindow window;
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private double markdown;
    private Hunter customer;
    private Boolean isSamuraiMode;


    /**
     * The Shop constructor takes in a markdown value and leaves customer null until one enters the shop.
     *
     * @param markdown Percentage of markdown for selling items in decimal format.
     */
    public Shop(double markdown, boolean isSamuraiMode, OutputWindow window) {
        this.markdown = markdown;
        customer = null; // customer is set in the enter method
        this.isSamuraiMode = isSamuraiMode;
        this.window = window;
    }

    /**
     * Method for entering the shop.
     *
     * @param hunter the Hunter entering the shop
     * @param buyOrSell String that determines if hunter is "B"uying or "S"elling
     * @return a String to be used for printing in the latest news
     */
    public String enter(Hunter hunter, String buyOrSell) {
        customer = hunter;

        if (buyOrSell.equals("b")) {
            window.addTextToWindow("\nWelcome to the shop! We have the finest wares in town.", Color.BLACK);
            window.addTextToWindow("\nCurrently we have the following items:", Color.BLACK);
            window.addTextToWindow("\n" + inventory(), Color.BLACK);
            window.addTextToWindow("\nWhat're you lookin' to buy? ", Color.BLUE);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, true);
            if (cost == 0) {
                if(item.equals("sword")){
                    window.addTextToWindow("\nIt'll cost you 0 gold. Buy it (y/n)? ", Color.BLUE);
                    String option = SCANNER.nextLine().toLowerCase();
                    if (option.equals("y")) {
                        buyItem(item);
                    }
                } else {
                    window.addTextToWindow("\nIt'll cost you ", Color.BLUE);
                    window.addTextToWindow(cost + " gold", Color.YELLOW);
                    window.addTextToWindow(". Buy it (y/n)?", Color.BLUE);
                    String option = SCANNER.nextLine().toLowerCase();
                    if (option.equals("y")) {
                        buyItem(item);
                    }
                    window.addTextToWindow("\nWe ain't got none of those.", Color.BLUE);
                }
            } else {
                window.addTextToWindow("\nIt'll cost you ", Color.BLUE);
                window.addTextToWindow(cost + " gold", Color.YELLOW);
                window.addTextToWindow(". Buy it (y/n)?", Color.BLUE);
//                window.addTextToWindow(("\nIt'll cost you " + Colors.YELLOW + cost + " gold" + Colors.RESET + ". Buy it (y/n)? "), Color.BLUE);
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    buyItem(item);
                }
            }
        } else {
            window.addTextToWindow("\nWhat're you lookin' to sell? ", Color.BLUE);
            window.addTextToWindow(("\nYou currently have the following items: " + customer.getInventory()), Color.BLUE);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, false);
            if (cost == 0) {
                window.addTextToWindow("\nWe don't want none of those.", Color.BLUE);
            } else {
                window.addTextToWindow("\nIt'll get you " + cost + " gold. Sell it (y/n)? ", Color.BLUE);
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    sellItem(item);
                }
            }
        }
        window.clear();
        return "You left the shop";

    }

    /**
     * A method that returns a string showing the items available in the shop
     * (all shops sell the same items).
     *
     * @return the string representing the shop's items available for purchase and their prices.
     */
    public String inventory() {
        if(isSamuraiMode){
            if(customer.hasItemInKit("sword")){
                waterCost = 0;
                ropeCost = 0;
                macheteCost = 0;
                horseCost = 0;
                boatCost = 0;
                bootsCost = 0;
                shovelCost = 0;
                String str = "Water: " + waterCost + " gold\n";
                str += "Rope: " + ropeCost + " gold\n";
                str += "Machete: " + macheteCost + " gold\n";
                str += "Horse: " + horseCost + " gold\n";
                str += "Boat: " + boatCost + " gold\n";
                str += "Boots: " + bootsCost + " gold\n";
                str += "Shovel: " + shovelCost + " gold\n";
                return str;
            } else {
                String str = "Water: " + waterCost + " gold\n";
                str += "Rope: " + ropeCost + " gold\n";
                str += "Machete: " + macheteCost + " gold\n";
                str += "Horse: " + horseCost + " gold\n";
                str += "Boat: " + boatCost + " gold\n";
                str += "Boots: " + bootsCost + " gold\n";
                str += "Shovel: " + shovelCost + " gold\n";
                str += "Sword: " + swordCost + " gold\n";
                return str;
            }
        } else {
            String str = "Water: " + waterCost + " gold\n";
            str += "Rope: " + ropeCost + " gold\n";
            str += "Machete: " + macheteCost + " gold\n";
            str += "Horse: " + horseCost + " gold\n";
            str += "Boat: " + boatCost + " gold\n";
            str += "Boots: " + bootsCost + " gold\n";
            str += "Shovel: " + shovelCost + " gold\n";
            return str;
        }
    }

    /**
     * A method that lets the customer (a Hunter) buy an item.
     *
     * @param item The item being bought.
     */
    public void buyItem(String item) {
        int costOfItem = checkMarketPrice(item, true);
        if (customer.buyItem(item, costOfItem)) {
            window.addTextToWindow("\nYe' got yerself a " + item + ". Come again soon.", Color.BLUE);
        } else {
            window.addTextToWindow("\nHmm, either you don't have enough gold or you've already got one of those!", Color.BLUE);
        }
    }

    /**
     * A pathway method that lets the Hunter sell an item.
     *
     * @param item The item being sold.
     */
    public void sellItem(String item) {
        int buyBackPrice = checkMarketPrice(item, false);
        if (customer.sellItem(item, buyBackPrice)) {
            window.addTextToWindow("\nPleasure doin' business with you.", Color.BLUE);
        } else {
            window.addTextToWindow("\nStop stringin' me along!", Color.BLUE);
        }
    }

    /**
     * Determines and returns the cost of buying or selling an item.
     *
     * @param item The item in question.
     * @param isBuying Whether the item is being bought or sold.
     * @return The cost of buying or selling the item based on the isBuying parameter.
     */
    public int checkMarketPrice(String item, boolean isBuying) {
        if (isBuying) {
            return getCostOfItem(item);
        } else {
            return getBuyBackCost(item);
        }
    }

    /**
     * Checks the item entered against the costs listed in the static variables.
     *
     * @param item The item being checked for cost.
     * @return The cost of the item or 0 if the item is not found.
     */
    public int getCostOfItem(String item) {
        if (item.equals("water")) {
            return waterCost;
        } else if (item.equals("rope")) {
            return ropeCost;
        } else if (item.equals("machete")) {
            return macheteCost;
        } else if (item.equals("horse")) {
            return horseCost;
        } else if (item.equals("boat")) {
            return boatCost;
        } else if (item.equals("boots")){
            return bootsCost;
        } else if (item.equals("shovel")){
            return shovelCost;
        } else {
            return 0;
        }
    }

    /**
     * Checks the cost of an item and applies the markdown.
     *
     * @param item The item being sold.
     * @return The sell price of the item.
     */
    public int getBuyBackCost(String item) {
        int cost = (int) (getCostOfItem(item) * markdown);
        return cost;
    }


}