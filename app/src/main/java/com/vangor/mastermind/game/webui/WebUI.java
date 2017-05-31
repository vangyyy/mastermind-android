package com.vangor.mastermind.game.webui;

import com.vangor.mastermind.game.core.*;

public class WebUI {
    private Field field;

    public void processCommand(String command, String rowCount, String colCount, String duplicates, String columnString, String colorString) {
        if (field == null) {
            field = new Field(8, 4, false);
        }

        if (command != null) {
            switch (command) {
                case "newGame":
                    if (rowCount != null && colCount != null && duplicates != null) {
                        field = new Field(Integer.parseInt(rowCount), Integer.parseInt(colCount), Boolean.parseBoolean(duplicates));
                    }
                    break;
                case "check":
                    if (field.getState() == GameState.PLAYING && field.isRowFilled()) {
                        field.updateGameState();
                        field.generateClue();
                        field.nextRow();
                        if (field.getState() == GameState.FAILED) {
                            System.out.println("You lost!");
                        }
                    } else if (field.getState() == GameState.FAILED) {
                        System.out.println("You have already lost, please start a new game!");
                    } else {
                        System.out.println("Row is not complete!");
                    }
                    break;
            }
        } else if (columnString != null && colorString != null) {
            if (field.getState() == GameState.PLAYING) {
                try {
                    int column = Integer.parseInt(columnString);
                    BallColor nextColor = BallColor.valueOf(colorString.toUpperCase());
                    field.setBall(column + 1, nextColor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getState() == GameState.SOLVED) {
                System.out.println("You won!");
            } else if (field.getState() == GameState.FAILED) {
                System.out.println("You lost!");
            }
        }
    }

    public String renderAsHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div align=\"center\">\n<table>\n");

        for (int row = 0; row < field.getRowCount(); row++) {
            sb.append("<tr>\n");
            sb.append(clueToImg(field.getClue(), row, true));

            for (int column = 0; column < field.getColCount(); column++) {
                Ball ball = field.getTile(row, column);
                sb.append("<td>\n");

                if (field.getCurrentRow() == row) {
                    String color = (ball != null) ? BallColor.getNext(ball.getColor()).toString().toLowerCase() : "red";
                    sb.append("<a  href='").append(String.format("?column=%d&color=%s", column, color)).append("'>\n");
                }

                String image = (row == 0 && field.getState() == GameState.PLAYING) ? "unknown" :
                        (ball != null) ? ball.getColor().toString().toLowerCase() : "void";
                sb.append("<img src='").append(String.format("/images/mastermind/vangor/colors/%s.png", image)).append("'>\n");

                if (field.getCurrentRow() == row) {
                    sb.append("</a>\n");
                }

                sb.append("</td>\n");
            }

            sb.append(clueToImg(field.getClue(), row, false));
            sb.append("</tr>\n");
        }

        sb.append("</table>\n</div>\n");
        return sb.toString();
    }

    private String clueToImg(ClueType[][] clue, int row, boolean first) {
        int places = 0;
        int colors = 0;
        for (int col = 0; col < field.getColCount(); col++) {
            places = clue[row][col] == ClueType.PLACE ? places + 1 : places;
            colors = clue[row][col] == ClueType.COLOR ? colors + 1 : colors;
        }

        if (row > field.getCurrentRow()) {
            return (first) ? "<td>\n" + "<img src='" + String.format("/images/mastermind/vangor/clues/places_%s.png\n", places) + "'></td>\n" :
                    "<td>\n" + "<img src='" + String.format("/images/mastermind/vangor/clues/faces_%s.png\n", colors) + "'></td>\n";
        } else {
            return "<td>\n<img src='/images/mastermind/vangor/clues/blank.png'>\n</td>\n";
        }
    }

    public String sendScore() {
        StringBuilder sb = new StringBuilder();
        sb.append(field.calculateScore());
        return sb.toString();
    }
}
