package ru.vova.game.minesweeper.model;

import java.util.Arrays;
import java.util.Random;

/**
 * @author vova
 */
public class GameModel {
    /**
     * Обозначение мины на игровом поле
     */
    public static final int MINE = -1000;
    /**
     * Игровое поле
     */
    private final int[][] model;
    /**
     * Состояния ячеек
     */
    private final CellState[][] state;
    private final int width;
    private final int height;

    public GameModel(int width, int height, int mineCount) {
        this.width = width;
        this.height = height;
        model = new int[width][height];
        generate(mineCount);
        state = new CellState[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                state[i][j] = CellState.CLOSED;
            }
        }
    }

    /**
     * Открытие всех ячеек вокруг, где нет мин и нет числа
     *
     * @param x координата x ячейки вокруг которой проверить
     * @param y координата y ячейки вокруг которой проверить
     */
    private void processIsland(int x, int y) {
        if (state[x][y] != CellState.OPEN) {
            return;
        }
        Borders borders = new Borders(x, y);
        for (int i = borders.xStart; i <= borders.xEnd; i++) {
            for (int j = borders.yStart; j <= borders.yEnd; j++) {
                if ((i == x && j == y) || state[i][j] != CellState.CLOSED) {
                    continue;
                }
                if (model[i][j] != MINE) {
                    state[i][j] = CellState.OPEN;
                }

                if (model[i][j] == 0) {
                    processIsland(i, j);
                }
            }
        }
    }

    /**
     * Обработка клика правой кнопкой по ячейке(поставить вопрос или пометить мину)
     *
     * @param x координата x ячейки по которой кликнули
     * @param y координата y ячейки по которой кликнули
     */
    public void right(int x, int y) {
        switch (state[x][y]) {
            case MINE_TAGGED:
                state[x][y] = CellState.QUESTION_TAGGED;
                break;
            case QUESTION_TAGGED:
                state[x][y] = CellState.CLOSED;
                break;
            case CLOSED:
                state[x][y] = CellState.MINE_TAGGED;
        }

    }

    /**
     * Обработка нажатия колесом на ячейку(автоматически открыть, если все мины вокруг помечены)
     *
     * @param x координата x ячейки по которой кликнули
     * @param y координата y ячейки по которой кликнули
     * @return результат октрытия
     */
    public OpenResult wheel(int x, int y) {
        if (state[x][y] != CellState.OPEN) {
            return OpenResult.NOTHING;
        }
        if (model[x][y] == 0) {
            return OpenResult.NOTHING;
        }
        int mines = 0;
        Borders borders = new Borders(x, y);
        for (int i = borders.xStart; i <= borders.xEnd; i++) {
            for (int j = borders.yStart; j <= borders.yEnd; j++) {
                if (state[i][j] == CellState.MINE_TAGGED) {
                    mines++;
                }
            }
        }
        if (mines != model[x][y]) {
            return OpenResult.NOTHING;
        }

        for (int i = borders.xStart; i <= borders.xEnd; i++) {
            for (int j = borders.yStart; j <= borders.yEnd; j++) {
                if (state[i][j] == CellState.CLOSED) {
                    if (openCell(i, j) == OpenResult.MINE) {
                        return OpenResult.MINE;
                    }
                }
            }
        }
        return (checkWin()) ? OpenResult.WIN : OpenResult.OPENED;
    }

    /**
     * Открыть ячейку
     *
     * @param x координата x ячейки по которой кликнули
     * @param y координата y ячейки по которой кликнули
     * @return результат октрытия
     */
    public OpenResult openCell(int x, int y) {
        if (state[x][y] != CellState.CLOSED) {
            return OpenResult.NOTHING;
        }
        if (model[x][y] == MINE) {
            return OpenResult.MINE;
        }
        state[x][y] = CellState.OPEN;
        if (model[x][y] == 0) {
            processIsland(x, y);
        }
        return (checkWin()) ? OpenResult.WIN : OpenResult.OPENED;
    }

    private boolean checkWin() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (model[i][j] != MINE && state[i][j] != CellState.OPEN) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Посчитать количество мин вокруг ячейки
     *
     * @param x координата x ячейки
     * @param y координата y ячейки
     * @return количество мин
     */
    private int countMinesAround(int x, int y) {
        Borders borders = new Borders(x, y);
        int count = 0;
        for (int i = borders.xStart; i <= borders.xEnd; i++) {
            for (int j = borders.yStart; j <= borders.yEnd; j++) {
                if (model[i][j] == MINE) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Сгенерировать поле
     *
     * @param mineCount количество мин
     */
    private void generate(int mineCount) {
        Random random = new Random();
        for (int i = 0; i < mineCount; i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (model[x][y] == MINE);
            model[x][y] = MINE;
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (model[i][j] != MINE) {
                    model[i][j] = countMinesAround(i, j);
                }
            }
        }
    }

    /**
     * Костыль для toString()
     *
     * @return Отформатированный вывод поля
     */
    private String arrToString() {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            result.append(Arrays.toString(model[i])).append("\n");
        }
        result.append("]");
        return result.toString();
    }

    @Override
    public String toString() {
        return "GameModel{" +
                "model=" + arrToString() +
                ", width=" + width +
                ", height=" + height +
                '}';
    }


    public int[][] getModel() {
        return model;
    }

    public CellState[][] getState() {
        return state;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Состояние ячейки
     */
    public enum CellState {
        /**
         * Закрыта
         */
        CLOSED,
        /**
         * Открыта
         */
        OPEN,
        /**
         * Помечена миной
         */
        MINE_TAGGED,
        /**
         * Помечена вопросом
         */
        QUESTION_TAGGED
    }

    /**
     * Результат открытия ячейки
     */
    public enum OpenResult {
        /**
         * Открылась нормально
         */
        OPENED,
        /**
         * Мина
         */
        MINE,
        /**
         * Ячейку нельзя октрыть(она уже открыта или помечена)
         */
        NOTHING,
        /**
         * Юзер выиграл
         */
        WIN
    }

    /**
     * Границы для перебора ячеек вокруг(учет того что ячейка может быть в углу или у края)
     */
    private class Borders {

        final int xStart;
        final int xEnd;
        final int yStart;
        final int yEnd;

        Borders(int x, int y) {
            xStart = (x == 0) ? 0 : x - 1;
            xEnd = (x == width - 1) ? x : x + 1;
            yStart = (y == 0) ? 0 : y - 1;
            yEnd = (y == height - 1) ? y : y + 1;
        }

    }
}
