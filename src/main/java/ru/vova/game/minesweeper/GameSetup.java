package ru.vova.game.minesweeper;

import ru.vova.game.minesweeper.ex.GameSetupException;
import ru.vova.game.minesweeper.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class GameSetup extends JFrame {
    private final I18NProvider i18NProvider = I18NProvider.getInstance();

    GameSetup(GameSetupCallback callback) throws HeadlessException {
        super();
        setTitle(i18NProvider.getString("gamesetup.title"));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        setBounds(40, 40, 350, 180);
        JLabel mineCountJLabel = new JLabel(i18NProvider.getString("gamesetup.mine-count"));
        panel.add(mineCountJLabel);

        JTextField mineJTextField = new JTextField();
        panel.add(mineJTextField);
        //------------------
        JLabel widthJLabel = new JLabel(i18NProvider.getString("gamesetup.width"));
        panel.add(widthJLabel);

        JTextField widthJTextField = new JTextField();
        panel.add(widthJTextField);
        //-------------
        JLabel heightJLabel = new JLabel(i18NProvider.getString("gamesetup.height"));
        panel.add(heightJLabel);

        JTextField heightJTextField = new JTextField();
        panel.add(heightJTextField);
        //----------------
        JButton okBtn = new JButton(i18NProvider.getString("gamesetup.ok"));

        okBtn.addActionListener(e -> {
            try {
                int mineCount = Integer.parseInt(mineJTextField.getText());
                int width = Integer.parseInt(widthJTextField.getText());
                int height = Integer.parseInt(heightJTextField.getText());
                if (width > 50 || width < 10) {
                    throw new GameSetupException(i18NProvider.getString("gamesetup.width-error"));
                }
                if (height > 50 || height < 10) {
                    throw new GameSetupException(i18NProvider.getString("gamesetup.height-error"));
                }
                if (width * height - mineCount < 2) {
                    throw new GameSetupException(i18NProvider.getString("gamesetup.mine-count-error"));
                }
                GameModel gameModel = new GameModel(width, height, mineCount);
                callback.onGameSetup(gameModel);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, i18NProvider.getString("gamesetup.number-format-error"));
            } catch (GameSetupException e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage());
            }
        });
        panel.add(okBtn);

        JButton cancelBtn = new JButton(i18NProvider.getString("gamesetup.cancel"));
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);

        getContentPane().add(panel);
        setPreferredSize(new Dimension(320, 100));

        setResizable(false);
        changeFont(panel, AppConstants.MENU_FONT);
        setVisible(true);
    }

    private static void changeFont(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFont(child, font);
            }
        }
    }
}
