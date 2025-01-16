import java.awt.*;
import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private  boolean easyMode;
    private boolean samuraiMode;
    private int count = 0;
    private int countForGold = 0;
    private OutputWindow window;
    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
        samuraiMode = false;
        window = new OutputWindow();
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("\nWelcome to TREASURE HUNTER!", Color.BLACK);
        window.addTextToWindow("\nGoing hunting for the big treasure, eh?", Color.BLACK);
        window.addTextToWindow("\nWhat's your name, Hunter?", Color.BLACK);
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 20);

        window.addTextToWindow("Hard mode? (y/n/e) OR test: ", Color.BLUE);
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("y")) {
            hardMode = true;
        } else if(hard.equals("test")){
            hunter.changeGold(80);
            hunter.buyItem("Water", 2);
            hunter.buyItem("Rope", 4);
            hunter.buyItem("Machete", 6);
            hunter.buyItem("Horse", 12);
            hunter.buyItem("Boat", 20);
            hunter.changeGold(44);
        }else if(hard.equals("e")){
            easyMode = true;
            hunter.changeGold(20);
        } else if(hard.equals("s")){
           samuraiMode = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if(easyMode){
            markdown = 1;
            toughness = 0.1;
        }
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }

        if(samuraiMode){
            toughness = 0;
        }
        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown, samuraiMode, this.window);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness, easyMode, this.samuraiMode);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x") && hunter.getGold() > 0 && !hunter.hasAllTreasures()) {
            window.addTextToWindow("\n", Color.WHITE);
            String c = currentTown.getLatestNews();
            window.addTextToWindow(c, Color.BLACK);
            window.addTextToWindow("\n***", Color.BLACK);
            String h = hunter.infoString();
            window.addTextToWindow(h, Color.BLACK);
            String c1 = currentTown.infoString();
            window.addTextToWindow(c1, Color.BLACK);
            window.addTextToWindow("\n(B)uy something at the shop.", Color.BLACK);
            window.addTextToWindow("\n(S)ell something at the shop.", Color.BLACK);
            window.addTextToWindow("\n(E)xplore surrounding terrain.", Color.BLACK);
            window.addTextToWindow("\n(M)ove on to a different town.", Color.BLACK);
            window.addTextToWindow("\n(L)ook for trouble!", Color.BLACK);
            window.addTextToWindow("\n(H)unt for treasure.", Color.BLACK);
            window.addTextToWindow("\n(D)ig for gold.", Color.BLACK);
            window.addTextToWindow("\nGive up the hunt and e(X)it.", Color.BLACK);
            window.addTextToWindow("\n", Color.WHITE);
            window.addTextToWindow("What's your next move? ", Color.BLACK);
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
        end();
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            String explore = currentTown.getTerrain().infoString();
            window.addTextToWindow(explore, Color.BLACK);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                String c3 = currentTown.getLatestNews();
                window.addTextToWindow(c3, Color.BLACK);
                enterTown();
                count = 0;
                countForGold = 0;
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("h")) {
            if (count == 1) {
                window.addTextToWindow("\nYou have already searched this town", Color.BLACK);
            } else {
                String treasure = currentTown.getTreasure();
                if(treasure.equals("dust")){
                    window.addTextToWindow("\nYou found dust!", Color.BLACK);
                } else if (hunter.hasItemInTreasureList(treasure)){
                    window.addTextToWindow("\nYou have already collected this " + treasure + "!", Color.BLACK);
                } else {
                    window.addTextToWindow("\nYou found a " + treasure + "!", Color.BLACK);
                    hunter.addTreasures(treasure);
                }
                count = 1;
            }
        } else if (choice.equals("d")) {
            if (countForGold == 1) {
                window.addTextToWindow("\nYou already dug for gold in this town", Color.BLACK);
            } else if(!hunter.hasItemInKit("Shovel")) {
                window.addTextToWindow("\nYou can't dig for gold without a shovel", Color.BLACK);
            } else {
                countForGold = 1;
                if (Math.random() < 0.5) {
                    int gold = (int) (Math.random() * 20) + 1;
                    window.addTextToWindow("\nYou dug up " + gold + " gold!", Color.BLACK);
                    hunter.changeGold(gold);
                } else {
                    window.addTextToWindow("\nYou dug but only found dirt", Color.BLACK);
                }
            }
        } else if (choice.equals("x")) {
            window.addTextToWindow("\nFare thee well, " + hunter.getHunterName() + "!", Color.BLACK);
        } else {
            window.addTextToWindow("\nYikes! That's an invalid option! Try again.", Color.BLACK);
        }
    }
    private void end(){
        window.addTextToWindow("\nFare thee well, " + hunter.getHunterName(), Color.BLACK);
        if (hunter.hasAllTreasures()){
            window.addTextToWindow( ", you have bested this game.", Color.GREEN);
            window.addTextToWindow( "\nGOOD ENDING", Color.GREEN);
        } else {
            window.addTextToWindow( ", you are out of money!", Color.RED);
            window.addTextToWindow("\nBAD ENDING", Color.RED);
        }
    }
}