package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class GameMap extends JPanel {

    private int human = 1;
    private int ai = 2;
    private int emptyField = 0;

    private Random random = new Random();

    private int mapSizeX;
    private int mapSizeY;

    private int cellWidth;
    private int cellHeight;

    private int winLength;
    private int[][] map;

    private boolean gameOver;
    private boolean isMapExist;

    private final int STATE_HUMAN_WIN = 1;
    private final int STATE_AI_WIN = 2;
    private final int STATE_DRAW = 0;

    private final String MSG_HUMAN_WIN = "Human win!";
    private final String MSG_AI_WIN = "AI win!";
    private final String MSG_DRAW = "Draw!";

    private MainWindow mainWindow;

    public GameMap(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseObject) {
                super.mouseReleased(mouseObject);
                update(mouseObject);
            }
        });
        isMapExist = false;
    }

    void startGame(int mapSizeX, int mapSizeY, int winLength) {
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.winLength = winLength;
        this.gameOver = false;
        this.isMapExist = true;
        this.map = new int[mapSizeY][mapSizeX];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        createMap(g);

        for (int y = 0; y < mapSizeY; y++) {
            for (int x = 0; x < mapSizeX; x++) {
                if (isEmptyCell(x, y)) {
                    continue;
                }

                if (map[y][x] == human) {
                    g.setColor(Color.WHITE);
                    g.fillOval(x * cellWidth + 10, y * cellHeight +10, cellWidth - 20, cellHeight - 20);
                }

                if (map[y][x] == ai) {
                    g.setColor(Color.RED);
                    g.fillRect(x * cellWidth + 10, y * cellHeight +10, cellWidth - 20, cellHeight - 20);
                }
            }
        }
        if (gameOver) {
            return;
        }
    }

    private void update(MouseEvent mouseObject) {
        if (!isMapExist) {
            return;
        }
        if (gameOver) {
            return;
        }

        int cellX = mouseObject.getX() / cellWidth;
        int cellY = mouseObject.getY() / cellHeight;

        if (!isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) {
            return;
        }

        map[cellY][cellX] = human;
        mainWindow.recordLog("Your move in [" + (cellX + 1) + ":" + (cellY + 1) + "]");

        if (checkWin(human)) {
            setGameOver(STATE_HUMAN_WIN);
            return;
        }

        if (isFullMap()) {
            setGameOver(STATE_DRAW);
            return;
        }

        aiTurn();
        repaint();

        if (checkWin(human)) {
            setGameOver(STATE_AI_WIN);
            return;
        }

        if (isFullMap()) {
            setGameOver(STATE_DRAW);
            return;
        }
    }

    private void setGameOver(int state) {
        repaint();
        this.gameOver = true;
        showGameOverMessage(state);
    }

    private void showGameOverMessage(int state) {
        switch (state) {
            case STATE_HUMAN_WIN:
                mainWindow.recordLog(MSG_HUMAN_WIN);
                JOptionPane.showMessageDialog(this, MSG_HUMAN_WIN);
                break;
            case STATE_AI_WIN:
                mainWindow.recordLog(MSG_AI_WIN);
                JOptionPane.showMessageDialog(this, MSG_AI_WIN);
                break;
            case STATE_DRAW:
                mainWindow.recordLog(MSG_DRAW);
                JOptionPane.showMessageDialog(this, MSG_DRAW);
                break;
            default:
                mainWindow.recordLog("Something wrong!");
                JOptionPane.showMessageDialog(this, "Something wrong! Incorrect game over state > " + state) ;
        }
    }

    private void createMap(Graphics g) {

        if (!isMapExist) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        cellWidth = width / mapSizeX;
        cellHeight = height / mapSizeY;

        g.setColor(Color.WHITE);

        for (int i = 0; i <= mapSizeY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, width, y);
        }

        for (int i = 0; i < mapSizeX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, height);
        }

    }

    private void aiTurn() {
        if (turnAIWinCell()) {
            return;
        }
        if (turnHumanWinCell()) {
            return;
        }
        int x;
        int y;
        do {
            x = random.nextInt(mapSizeX);
            y = random.nextInt(mapSizeY);
        } while (!isEmptyCell(x, y));
        map[y][x] = ai;
        mainWindow.recordLog("PC move in [" + (x + 1) + ":" + (y + 1) + "]");
    }

    private boolean turnAIWinCell() {
        for (int i = 0; i < mapSizeY; i++) {
            for (int j = 0; j < mapSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    map[i][j] = ai;
                    if (checkWin(ai)) {
                        return true;
                    }
                    map[i][j] = emptyField;
                }
            }
        }
        return false;
    }

    private boolean turnHumanWinCell() {
        for (int i = 0; i < mapSizeY; i++) {
            for (int j = 0; j < mapSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    map[i][j] = human;
                    if (checkWin(human)) {
                        map[i][j] = ai;
                        return true;
                    }
                    map[i][j] = emptyField;
                }
            }
        }
        return false;
    }

    private boolean checkWin(int player) {
        for (int i = 0; i < mapSizeX; i++) {
            for (int j = 0; j < mapSizeY; j++) {
                if (checkLine(i, j, 1, 0, winLength, player)) {
                    return true;
                }
                if (checkLine(i, j, 1, 1, winLength, player)) {
                    return true;
                }
                if (checkLine(i, j, 0, 1, winLength, player)) {
                    return true;
                }
                if (checkLine(i, j, 1, -1, winLength, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    // проверка линии
    private boolean checkLine(int x, int y, int vX, int vY, int len, int player) {
        int endX = x + (len - 1) * vX;
        int endY = y + (len - 1) * vY;
        if (!isValidCell(endX, endY)) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (map[y + i * vY][x + i * vX] != player) {
                return false;
            }
        }
        return true;
    }

    private boolean isFullMap() {
        for (int i = 0; i < mapSizeY; i++) {
            for (int j = 0; j < mapSizeX; j++) {
                if (map[i][j] == emptyField) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < mapSizeX && y >= 0 && y < mapSizeY;
    }

    private boolean isEmptyCell(int x, int y) {
        return map[y][x] == emptyField;
    }
}
