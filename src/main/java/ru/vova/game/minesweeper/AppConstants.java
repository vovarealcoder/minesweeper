package ru.vova.game.minesweeper;

import java.awt.*;

public class AppConstants {
    /**
     * Цвет мин, когда юзер выиграл
     */
    public static final Color MINE_WIN_COLOR = Color.ORANGE;
    /**
     * Цвет закрытой ячейки
     */
    static final Color CLOSED_COLOR = Color.LIGHT_GRAY;
    /**
     * Цвет линии
     */
    static final Color LINE_COLOR = Color.BLACK;
    /**
     * Цвет открытой ячейки
     */
    static final Color OPENED_COLOR = Color.DARK_GRAY;
    /**
     * Цвет значка вопроса и пометки о мине
     */
    static final Color MINE_QUESTION_TAG_COLOR = Color.BLUE;
    /**
     * Цвет мин когда юзер проиграл
     */
    static final Color MINE_GAMEOVER_COLOR = Color.RED;
    /**
     * Шрифт меню
     */
    static final Font MENU_FONT = new Font("sans-serif", Font.BOLD, 18);
    /**
     * Толщина линии сетки
     */
    static final BasicStroke LINE_STROKE = new BasicStroke(3);

    private AppConstants() {

    }
}
