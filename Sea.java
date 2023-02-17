package battleship;

import java.util.Arrays;

public class Sea {
    private String[][] data;
    private String[][] revealed;
    private final int seaWidth;
    private final int seaHeight;

    private int shotsTaken = 0;

    public Sea(int seaWidth, int seaHeight) {
        this.seaWidth = seaWidth;
        this.seaHeight = seaHeight;
        data = generateFog();
        revealed = generateFog();
    }

    private String[][] generateFog() {
        String[][] fog = new String[seaHeight][seaWidth];
        for (int y = 0; y < seaWidth; y++) {
            for (int x = 0; x < seaHeight; x++) {
                fog[y][x] = "~";
            }
        }
        return fog;
    }

    public Sea() {
        this(10, 10);
    }

    public void print() {
        printData(data);
    }

    public void printRevealed() {
        printData(revealed);
    }

    private void printData(String[][] sea) {
        char rowLetter = 'A';
        int columnNumber = 1;
        for (int y = -1; y < seaHeight; y++) {
            for (int x = -1; x < seaWidth; x++) {
                if (y == -1 && x == -1) {
                    System.out.print(" ");
                } else if (y == -1) {
                    System.out.print(columnNumber);
                    columnNumber++;
                } else if (x == -1) {
                    System.out.print(rowLetter);
                    rowLetter++;
                } else {
                    System.out.print(sea[y][x]);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private int[] convertCoordinates(String coords) {
        // A10 -> [0,9]
        int[] convertedCoords = new int[2];
        convertedCoords[0] = coords.toUpperCase().charAt(0) - 65;
        convertedCoords[1] = Integer.parseInt(coords.substring(1)) - 1;
        if (convertedCoords[0] > seaHeight - 1 || convertedCoords[0] < 0 || convertedCoords[1] > seaWidth - 1 || convertedCoords[1] < 0) {
            throw new IllegalArgumentException("Invalid coordinates.");
        }
        return convertedCoords;
    }

    public void placeShip(String pos1, String pos2, String shipName, int length) {
        int[] pos1Coords;
        int[] pos2Coords;

        try {
            pos1Coords = convertCoordinates(pos1);
            pos2Coords = convertCoordinates(pos2);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong ship location!");
        }

        if (pos1Coords[0] == pos2Coords[0]) {
            if (Math.abs(pos1Coords[1] - pos2Coords[1]) + 1 != length) {
                throw new IllegalArgumentException("Wrong length of the " + shipName + "!");
            }
            for (int i = Math.min(pos1Coords[1], pos2Coords[1]); i <= Math.max(pos1Coords[1], pos2Coords[1]); i++) {
                if (checkInvalidPlacement(new int[]{pos1Coords[0], i})) {
                    throw new IllegalArgumentException("You placed it too close to another one.");
                }
            }
            for (int i = Math.min(pos1Coords[1], pos2Coords[1]); i <= Math.max(pos1Coords[1], pos2Coords[1]); i++) {
                data[pos1Coords[0]][i] = "O";
            }
        } else if (pos1Coords[1] == pos2Coords[1]) {
            if (Math.abs(pos1Coords[0] - pos2Coords[0]) + 1 != length) {
                throw new IllegalArgumentException("Wrong length of the " + shipName + "!");
            }
            for (int i = Math.min(pos1Coords[0], pos2Coords[0]); i <= Math.max(pos1Coords[0], pos2Coords[0]); i++) {
                if (checkInvalidPlacement(new int[]{i, pos1Coords[1]})) {
                    throw new IllegalArgumentException("You placed it too close to another one.");
                }
            }
            for (int i = Math.min(pos1Coords[0], pos2Coords[0]); i <= Math.max(pos1Coords[0], pos2Coords[0]); i++) {
                data[i][pos1Coords[1]] = "O";
            }
        } else {
            throw new IllegalArgumentException("Wrong ship location!");
        }
    }

    private boolean checkInvalidPlacement(int[] coords) {
//        check tile for overlap, check surrounding four tiles for too close ships.
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
//                don't check out of bounds or 4 diagonal tiles
                if (coords[0] + i < 0 || coords[0] + i > seaHeight - 1 || coords[1] + j < 0 || coords[1] + j > seaWidth - 1 || (i != 0 && j != 0)) {
                    continue;
                } else if (data[coords[0] + i][coords[1] + j].equals("O")) {
                    return true;
                }
            }
        }
        return false;
    }

    public int takeShot(String coords) {
        /**
         * @return 0 if missed, 1 if hit, 2 if sunk, 3 if all are sunk
         */
        int[] targetCoords;
        try {
            targetCoords = convertCoordinates(coords);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("You entered wrong coordinates!");
        }
//        if (data[targetCoords[0]][targetCoords[1]].equals("X") || data[targetCoords[0]][targetCoords[1]].equals("M")) {
//            throw new IllegalArgumentException("You already shot here!");
//        }
        shotsTaken++;
        if (data[targetCoords[0]][targetCoords[1]].equals("~")) {

            data[targetCoords[0]][targetCoords[1]] = "M";
            revealed[targetCoords[0]][targetCoords[1]] = "M";
            return 0;
        }
        data[targetCoords[0]][targetCoords[1]] = "X";
        revealed[targetCoords[0]][targetCoords[1]] = "X";
        if (areAllShipsSunk()) {
            return 3;
        } else if (isShipSunk(targetCoords)) {
            return 2;
        }
        return 1;
    }

    private boolean isShipSunk(int[] coords) {
        return isShipSunk(coords, new int[]{-1, -1});
    }

    private boolean isShipSunk(int[] coords, int[] ignoredCoords) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (coords[0] + i < 0 || coords[0] + i > seaHeight - 1 || coords[1] + j < 0 || coords[1] + j > seaWidth - 1 || (i != 0) == (j != 0) || (coords[0] + i == ignoredCoords[0] && coords[1] + j == ignoredCoords[1])) {
                    continue;
                } else if (data[coords[0] + i][coords[1] + j].equals("O")) {
                    return false;
                } else if (data[coords[0] + i][coords[1] + j].equals("X") && !isShipSunk(new int[]{coords[0] + i, coords[1] + j}, coords)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean areAllShipsSunk() {
        for (int i = 0; i < seaHeight; i++) {
            for (int j = 0; j < seaWidth; j++) {
                if (data[i][j].equals("O")) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getShotsTaken() {
        return shotsTaken;
    }
}