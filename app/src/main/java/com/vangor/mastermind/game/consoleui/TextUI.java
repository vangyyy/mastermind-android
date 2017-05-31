package com.vangor.mastermind.game.consoleui;

import com.vangor.mastermind.game.core.*;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUI {

    private Field field;
    private Scanner scanner;
    private static final Pattern RATING_REGEX = Pattern.compile("([1-5]*)");
    private static final String GAME = "mastermind";

    public TextUI() {
        newField(8, 4, false);
        scanner = new Scanner(System.in);
    }

    public void play() {
        while (field.getState() == GameState.PLAYING) {
            render();
            handleInput();
        }
        switch (field.getState()) {
            case FAILED:
                render();
                System.out.println("Ha, you lose !");
                newGamePrompt();
                break;
            case SOLVED:
                render();
                System.out.println("Congratulations, you don't suck!");
                databasePrompt();
                printSummary();
                newGamePrompt();
                break;
            default:
                throw new IllegalStateException("Unexpected game state " + field.getState());
        }
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < field.getRowCount(); row++) {
            for (int col = 0; col < field.getColCount(); col++) {
                /*if (field.getState() == GameState.PLAYING && row == 0) {
                    System.out.print("\u001B[1m?");
                } else */if (field.getTile(row, col) == null && row == field.getCurrentRow()) {
                    sb.append("-");
                } else if (field.getTile(row, col) == null) {
                    sb.append("*");
                } else {
                    switch (field.getTile(row, col).getColor()) {
                        case RED:
                            sb.append("R");
                            break;
                        case GREEN:
                            sb.append("G");
                            break;
                        case BLUE:
                            sb.append("B");
                            break;
                        case YELLOW:
                            sb.append("Y");
                            break;
                        case CYAN:
                            sb.append("C");
                            break;
                        case DARK:
                            sb.append("D");
                            break;
                        case MAGENTA:
                            sb.append("M");
                            break;
                        case WHITE:
                            sb.append("W");
                            break;
                        default:
                            throw new IllegalArgumentException("Unexpected ball color " + field.getTile(col, row).getColor());
                    }
                }
                sb.append(" ");
            }
            if (row > field.getCurrentRow()) {
                sb.append("\t" + clueToString(field.getClue(), row));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void handleInput() {
        System.out.print("Please enter command ([1-" + field.getColCount() + "])([RGBYCDMW]): ");
        String line = scanner.nextLine().toUpperCase();
        Matcher matcher = Pattern.compile("([1-" + field.getColCount() + "])([RGBYCDMW])").matcher(line);
        if ("EXIT".equals(line)) {
            System.exit(0);
        } else if ("CHECK".equals(line)) {
            if (field.isRowFilled()) {
                field.updateGameState();
                field.generateClue();
                field.nextRow();
            } else {
                System.out.println("Row is not complete!");
            }
        } else if (matcher.matches()) {
            field.setBall(matcher.group(1).charAt(0) - 48, signToBallColor(matcher.group(2).charAt(0)));
        } else {
            System.out.println("Wrong input!");
        }
    }

    public BallColor signToBallColor(char sign) {
        switch (sign) {
            case 'R':
                return BallColor.RED;
            case 'G':
                return BallColor.GREEN;
            case 'B':
                return BallColor.BLUE;
            case 'Y':
                return BallColor.YELLOW;
            case 'C':
                return BallColor.CYAN;
            case 'D':
                return BallColor.DARK;
            case 'M':
                return BallColor.MAGENTA;
            case 'W':
                return BallColor.WHITE;
            default:
                throw new IllegalArgumentException("Unexpected color sign" + sign);
        }
    }

    public String clueToString(ClueType[][] clue, int row) {
        int places = 0;
        int colors = 0;
        for (int col = 0; col < field.getColCount(); col++) {
            places = clue[row][col] == ClueType.PLACE ? places + 1 : places;
            colors = clue[row][col] == ClueType.COLOR ? colors + 1 : colors;
        }
        return "You guessed " + places + " place/s and " + colors + " color/s right!";
    }

    private void databasePrompt() {

    }

    private void newGamePrompt() {
        System.out.print("\nWould you like to start a new game? [Y/n]: ");
        String line = scanner.nextLine().toUpperCase();
        if ("Y".equals(line)) {
            field.restart();
            play();
        } else if ("N".equals(line)) {
            System.exit(0);
        } else {
            System.out.println("Wrong input!");
            newGamePrompt();
        }
    }

    private void printSummary() {

    }

    public void newField(int rowCount, int colCount, boolean duplicates) {
        this.field = new Field(rowCount, colCount, duplicates);
    }

    public Field getField() {
        return field;
    }
}
