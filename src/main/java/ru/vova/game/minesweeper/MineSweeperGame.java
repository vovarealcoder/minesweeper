package ru.vova.game.minesweeper;

import com.sun.istack.internal.Nullable;
import ru.vova.game.minesweeper.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

/**
 * @author vova
 */
public class MineSweeperGame extends JFrame implements GameSetupCallback {


    private final I18NProvider i18NProvider = I18NProvider.getInstance();
    private GameModel model;
    private int xStep;
    private int yStep;
    private boolean gameOver = false;
    private boolean win = false;
    private final JPanel panel = new JPanel();
    private final JMenu gameJMenu;
    private final JMenu setup;
    private final JMenuItem newGameRootJMenu;
    private final JMenuItem noviceJMenuItem;
    private final JMenuItem amateurJMenuItem;
    private final JMenuItem profJMenuItem;
    private final JMenuItem customJMenuItem;
    private final JMenuItem exitJMenuItem;
    private final JMenu help;
    private final JMenuItem about;
    private final JMenuBar mb;
    private final JMenu langJMenu;
    private final JMenuItem langRuJMenuItem;
    private final JMenuItem langEnJMenuItem;

    private MineSweeperGame(String title) throws HeadlessException {
        super(title);

        UIManager.put("Menu.font", AppConstants.MENU_FONT);
        UIManager.put("MenuItem.font", AppConstants.MENU_FONT);
        UIManager.put("RadioButtonMenuItem.font", AppConstants.MENU_FONT);


        setBounds(20, 20, 700, 500);

        mb = new JMenuBar();
        mb.setFont(AppConstants.MENU_FONT);
        gameJMenu = new JMenu();

        newGameRootJMenu = new JMenu();

        noviceJMenuItem = new JMenuItem();
        noviceJMenuItem.addActionListener(e -> onGameSetup(new GameModel(9, 9, 10)));
        newGameRootJMenu.add(noviceJMenuItem);

        amateurJMenuItem = new JMenuItem();
        amateurJMenuItem.addActionListener(e -> onGameSetup(new GameModel(16, 16, 40)));
        newGameRootJMenu.add(amateurJMenuItem);

        profJMenuItem = new JMenuItem();
        profJMenuItem.addActionListener(e -> onGameSetup(new GameModel(30, 16, 99)));
        newGameRootJMenu.add(profJMenuItem);

        customJMenuItem = new JMenuItem();
        customJMenuItem.addActionListener(e -> new GameSetup(this));
        newGameRootJMenu.add(customJMenuItem);

        gameJMenu.add(newGameRootJMenu);

        exitJMenuItem = new JMenuItem();
        exitJMenuItem.addActionListener(e -> dispose());
        gameJMenu.add(exitJMenuItem);

        mb.add(gameJMenu);

        setup = new JMenu();
        langJMenu = new JMenu();


        langRuJMenuItem = new JRadioButtonMenuItem();
        langEnJMenuItem = new JRadioButtonMenuItem();

        langRuJMenuItem.addActionListener(e -> {
            initializeI18nText(new Locale("ru"));
            langEnJMenuItem.setSelected(false);
        });

        langJMenu.add(langRuJMenuItem);

        langEnJMenuItem.addActionListener(e -> {
            initializeI18nText(new Locale("en"));
            langRuJMenuItem.setSelected(false);
        });

        langJMenu.add(langEnJMenuItem);
        Locale aDefault = Locale.getDefault();
        if ("ru".equals(aDefault.getLanguage())) {
            langRuJMenuItem.setSelected(true);
        } else {
            langEnJMenuItem.setSelected(true);
        }
        setup.add(langJMenu);
        mb.add(setup);


        help = new JMenu();

        about = new JMenuItem();
        about.addActionListener(e -> showAboutDialog());
        help.add(about);

        mb.add(help);
        setJMenuBar(mb);

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouse(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        panel.addMouseListener(mouseListener);
        setContentPane(panel);

        initializeI18nText(null);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeI18nText(@Nullable Locale locale) {
        if (locale != null) {
            i18NProvider.changeLocale(locale);
        }
        gameJMenu.setText(i18NProvider.getString("main.game"));
        newGameRootJMenu.setText(i18NProvider.getString("main.new"));
        noviceJMenuItem.setText(i18NProvider.getString("main.novice"));
        amateurJMenuItem.setText(i18NProvider.getString("main.amateur"));
        profJMenuItem.setText(i18NProvider.getString("main.professional"));
        customJMenuItem.setText(i18NProvider.getString("main.custom"));
        exitJMenuItem.setText(i18NProvider.getString("main.exit"));
        setup.setText(i18NProvider.getString("main.setup"));
        langJMenu.setText(i18NProvider.getString("main.lang"));
        langRuJMenuItem.setText(i18NProvider.getString("main.russian-lang"));
        langEnJMenuItem.setText(i18NProvider.getString("main.english-lang"));
        help.setText(i18NProvider.getString("main.help"));
        about.setText(i18NProvider.getString("main.about"));
    }

    public static void main(String[] args) {
        new MineSweeperGame("MineSweeper");

    }

    /**
     * Обработка клика мышкой
     *
     * @param e параметры события
     */
    private void handleMouse(MouseEvent e) {
        if (model == null || gameOver) {
            return;
        }
        calcStep();
        int x = e.getX() / xStep;
        int y = e.getY() / yStep;
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                GameModel.OpenResult openResult = model.openCell(x, y);
                processOpenResult(openResult);
                repaint();
                break;
            case MouseEvent.BUTTON3:
                model.right(x, y);
                repaint();
                break;
            case MouseEvent.BUTTON2:
                GameModel.OpenResult wheel = model.wheel(x, y);
                processOpenResult(wheel);
                repaint();
                break;
        }
    }

    private void processOpenResult(GameModel.OpenResult wheel) {
        if (wheel == GameModel.OpenResult.MINE) {
            gameOver = true;
            win = false;
        }
        if (wheel == GameModel.OpenResult.WIN) {
            gameOver = true;
            win = true;
        }
        if (gameOver) {
            showFinalDialog();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) panel.getGraphics();
        if (model != null) {
            paintModel(graphics2D);
        }
    }

    /**
     * Отрисовка игрового поля
     *
     * @param graphics2D канвас
     */
    private void paintModel(Graphics2D graphics2D) {
        calcStep();
        graphics2D.setStroke(AppConstants.LINE_STROKE);
        graphics2D.setColor(AppConstants.CLOSED_COLOR);
        graphics2D.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        graphics2D.setColor(AppConstants.LINE_COLOR);
        for (int i = 0; i < model.getWidth(); i++) {
            graphics2D.drawLine(xStep * i, 0, xStep * i, panel.getHeight());
        }
        for (int j = 0; j < model.getHeight(); j++) {
            graphics2D.drawLine(0, yStep * j, panel.getWidth(), yStep * j);
        }
        int[][] model = this.model.getModel();
        GameModel.CellState[][] state = this.model.getState();
        Color color = graphics2D.getColor();
        Font currentFont = graphics2D.getFont();
        Font newFont = currentFont.deriveFont(Font.BOLD, currentFont.getSize() * 1.9F);
        graphics2D.setFont(newFont);
        for (int i = 0; i < this.model.getWidth(); i++) {
            for (int j = 0; j < this.model.getHeight(); j++) {

                switch (state[i][j]) {
                    case OPEN:
                        graphics2D.setColor(AppConstants.OPENED_COLOR);
                        graphics2D.fillRect(i * xStep, j * yStep, xStep, yStep);
                        graphics2D.setColor(AppConstants.MINE_QUESTION_TAG_COLOR);
                        if (model[i][j] > 0) {
                            graphics2D.setColor(Utils.colorByCount(model[i][j]));
                            graphics2D.drawString(String.valueOf(model[i][j]), i * xStep + (int) (xStep * 0.3), j * yStep + (int) (yStep * 0.7));
                        }
                        break;
                    case MINE_TAGGED:
                        graphics2D.setColor(AppConstants.MINE_QUESTION_TAG_COLOR);
                        graphics2D.drawString("*", i * xStep + (int) (xStep * 0.3), j * yStep + (int) (yStep * 0.7));

                        break;
                    case QUESTION_TAGGED:
                        graphics2D.setColor(AppConstants.MINE_QUESTION_TAG_COLOR);
                        graphics2D.drawString("?", i * xStep + (int) (xStep * 0.3), j * yStep + (int) (yStep * 0.7));
                        break;
                }
                if (gameOver) {
                    if (!win && model[i][j] == GameModel.MINE) {
                        graphics2D.setColor(AppConstants.MINE_GAMEOVER_COLOR);
                        graphics2D.fillRect(i * xStep, j * yStep, xStep, yStep);
                    } else if (win && state[i][j] != GameModel.CellState.OPEN) {
                        graphics2D.setColor(AppConstants.MINE_WIN_COLOR);
                        graphics2D.fillRect(i * xStep, j * yStep, xStep, yStep);
                    }


                }
                graphics2D.setColor(color);
            }
        }
    }

    /**
     * Расчет ширины и длины ячеек
     */
    private void calcStep() {
        if (model != null) {
            yStep = panel.getHeight() / model.getHeight();
            xStep = panel.getWidth() / model.getWidth();
        }
    }

    private void showFinalDialog() {
        if (win) {
            JOptionPane.showMessageDialog(null, i18NProvider.getString("main.win"));
        } else {
            JOptionPane.showMessageDialog(null, i18NProvider.getString("main.lose"));
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(null, i18NProvider.getString("main.about-dlg"));
    }

    @Override
    public void onGameSetup(GameModel model) {
        this.model = model;
        gameOver = false;
        win = false;
        repaint();
    }
}
