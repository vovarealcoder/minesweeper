package ru.vova.game.minesweeper;

import ru.vova.game.minesweeper.model.GameModel;

public interface GameSetupCallback {
    void onGameSetup(GameModel model);
}
