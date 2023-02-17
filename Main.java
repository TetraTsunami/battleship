package battleship;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    public static final String[][] DEBUG_SHIP_COORDS = {{"F3", "F7"}, {"A1", "D1"}, {"J10", "J8"}, {"B9", "D9"}, {"I2", "J2"}}; // for testing
    public static final String[] DEBUG_GUESSES = {"F3", "F4", "F5", "F6", "F7", "A1", "B1", "C1", "D1", "J8", "J9", "J10", "B9", "C9", "D9", "I2", "J2"}; // for testing
    public static final boolean DEBUG = false;
    private static Sea P1Sea;
    private static Sea P2Sea;
    private static int currentPlayer = 1;
    public static void main(String[] args) {
        P1Sea = new Sea();
        P2Sea = new Sea();

        for (Sea sea : new Sea[]{P1Sea, P2Sea}) {
            System.out.printf("Player %d, place your ships on the game field\n\n", currentPlayer);
            sea.print();
            promptPlaceShips(sea);
            System.out.println();
            passController();
        }

        while (true) {
            Sea ourSea = (currentPlayer == 1) ? P1Sea : P2Sea;
            Sea enemySea = (currentPlayer == 1) ? P2Sea : P1Sea;
            if (promptGuess(ourSea, enemySea, currentPlayer) == 3) {
                break;
            }
            passController();
        }
    }

    public static void passController() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    public static void promptPlaceShips(Sea sea) {
        for (Ship ship : Ship.values()) {
            System.out.printf("\nEnter the coordinates of the %s (%d cells)\n\n", ship.getName(), ship.getLength());
            attemptPlace(sea, ship);
            System.out.println();
            sea.print();
        }
    }

    private static void attemptPlace(Sea sea, Ship ship) {
        while (true) {
            try {
                if (DEBUG) {
                    int i = ship.ordinal();
                    System.out.printf("DEBUG: %s %s\n", DEBUG_SHIP_COORDS[i][0], DEBUG_SHIP_COORDS[i][1]);
                    sea.placeShip(DEBUG_SHIP_COORDS[i][0], DEBUG_SHIP_COORDS[i][1], ship.getName(), ship.getLength());
                } else {
                    sea.placeShip(scanner.next(), scanner.next(), ship.getName(), ship.getLength());
                }
                return;
            } catch (IllegalArgumentException e) {
                System.out.printf("\nError! %s Try again:\n\n", e.getMessage());
            }
        }
    }

    public static int promptGuess(Sea ourSea, Sea enemySea, int playerNumber) {
        enemySea.printRevealed();
        System.out.println("---------------------");
        ourSea.print();

        System.out.printf("\nPlayer %d, it's your turn:\n\n", playerNumber);
        while (true) {
            try {
                int success;
                if (DEBUG) {
                    System.out.printf("DEBUG: %s\n", DEBUG_GUESSES[enemySea.getShotsTaken()]);
                    success = enemySea.takeShot(DEBUG_GUESSES[enemySea.getShotsTaken()]);
                } else {
                    success = enemySea.takeShot(scanner.next());
                }

                switch (success) {
                    case 0 -> System.out.print("\nYou missed!\n");
                    case 1 -> System.out.print("\nYou hit a ship!\n");
                    case 2 -> System.out.print("\nYou sank a ship!\n");
                    case 3 -> System.out.print("\nYou sank the last ship. You won. Congratulations!\n");
                }
                return success;
            } catch (IllegalArgumentException e) {
                System.out.printf("\nError! %s Try again:\n\n", e.getMessage());
            }
        }
    }
}
