import java.awt.*;
import java.util.*;
/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean easyMode;
    private boolean samuraiMode;
    private OutputWindow window = new OutputWindow();
    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean easyMode, boolean samuraiMode) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.easyMode = easyMode;
        this.samuraiMode = samuraiMode;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
            if(checkItemBreak() && easyMode){
                printMessage += "\nA powerful force prevents you from losing your " + item + ".";
            }else if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item + ".";
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            window.addTextToWindow("You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n", Color.RED);
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                if(hunter.hasItemInKit("sword")){
                    window.addTextToWindow("The braweler, seeing your sword, made him realize that he needs to do better\n", Color.BLACK);
                    window.addTextToWindow("He couldn't win this fight, so he gave you his gold", Color.BLACK);
                    window.addTextToWindow(("\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET), Color.BLACK);
                    hunter.changeGold(goldDiff);
                    printMessage += "You won, nice.";
                } else {
                    window.addTextToWindow("Okay, stranger! You proved yer mettle. Here, take my gold.", Color.BLACK);
                    window.addTextToWindow(("\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET), Color.BLACK);
                    hunter.changeGold(goldDiff);
                    printMessage += "You won, nice.";
                }
            } else {
                if(hunter.hasItemInKit("Sword")){
                    window.addTextToWindow("The braweler, seeing your sword, made him realize that he needs to do better\n", Color.BLACK);
                    window.addTextToWindow("He couldn't win this fight, so he gave you his gold", Color.BLACK);
                    window.addTextToWindow(("\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET), Color.BLACK);
                    hunter.changeGold(goldDiff);
                    printMessage += "You won, nice.";
                } else {
                    window.addTextToWindow("That'll teach you to go lookin' fer trouble in MY town! Now pay up!", Color.BLACK);
                    window.addTextToWindow(("\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET), Color.BLACK);
                    hunter.changeGold(-goldDiff);
                    printMessage += "\n You're weak.";
                }
            }
        }
    }

    public String getTreasure(){
        String treasure = "";
        double num = Math.random() * 4 + 1;
        if (num == 1){
            treasure = "crown";
        } else if (num == 2){
            treasure = "trophy";
        } else if (num == 3){
            treasure = "gem";
        } else {
            treasure = "dust";
        }
        return treasure;
    }
    
    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random() * 6 + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 5){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

}