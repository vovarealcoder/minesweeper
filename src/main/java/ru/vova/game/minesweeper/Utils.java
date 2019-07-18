package ru.vova.game.minesweeper;

import java.awt.*;

public class Utils {
    private Utils(){
    }

    static Color colorByCount(int count) {
        switch (count) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.YELLOW;
            case 4:
                return new Color(118, 1, 122);
            case 5:
                return Color.RED;
            case 6:
                return new Color(153, 0, 10);
            case 7:
                return Color.PINK;
            default:
                return Color.BLACK;
        }
    }
}
