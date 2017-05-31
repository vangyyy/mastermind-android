package com.vangor.mastermind.game.core;

public enum BallColor {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    CYAN,
    DARK,
    MAGENTA,
    WHITE;

    public static BallColor getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }

    public static BallColor getNext(BallColor ballColor) {
        return (ballColor == null || ballColor.ordinal() == values().length - 1) ? values()[0] : values()[ballColor.ordinal() + 1];
    }
}
