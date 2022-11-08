package App;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    private int winWidth = 750;
    private int halfWinWidth = winWidth / 2;
    private int winHeight = 600;
    private int halfHeight = winHeight / 2;
    private int winPosX = 200;
    private int winPosY = 150;

    private final int minMapSize = 3;
    private final int maxMapSize = 10;
    private final int minMinWinLength = 3;

    private JPanel panelSettings;
    private JPanel panelControls;

    private JButton btnStart;
    private JButton btnExit;
    private JButton btnClearLog;

    private JTextArea gameLog;
    private JScrollPane scrollPanel;

    private JLabel labelMapSize;
    private String mapSizePrefix = "Your Map size: ";

    private JLabel labelWinLength;
    private String winLengthPrefix = "Your Win length: ";

    private JSlider sliderMapSizeSetup;
    private JSlider sliderWinLengthSetup;

    private GameMap gameMap;

    private int round = 0;

    public MainWindow() {
        prepareWindowSettings();

        prepareGameSettings();

        prepareButtons();

        prepareControls();

        prepareGameLog();

        gameMap = new GameMap(this);

        panelSettings.add(panelControls, BorderLayout.NORTH);
        panelSettings.add(scrollPanel, BorderLayout.SOUTH);

        add(panelSettings, BorderLayout.EAST);
        add(gameMap);

        setVisible(true);
    }

    private void prepareGameSettings() {
        panelSettings = new JPanel();
        panelSettings.setLayout(new GridLayout(2, 1));
    }

    private void prepareControls() {
        panelControls = new JPanel();
        panelControls.setLayout(new GridLayout(7, 1));

        labelMapSize = new JLabel(mapSizePrefix + minMapSize);

        sliderMapSizeSetup = new JSlider(minMapSize, maxMapSize, minMapSize);
        sliderMapSizeSetup.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentValueMapSize = sliderMapSizeSetup.getValue();
                labelMapSize.setText(mapSizePrefix + currentValueMapSize);
                sliderWinLengthSetup.setMaximum(currentValueMapSize);
            }
        });

        labelWinLength = new JLabel(winLengthPrefix + minMinWinLength);

        sliderWinLengthSetup = new JSlider(minMinWinLength, minMapSize, minMapSize);
        sliderWinLengthSetup.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                labelWinLength.setText(winLengthPrefix + sliderWinLengthSetup.getValue());
            }
        });

        panelControls.add(labelMapSize);
        panelControls.add(sliderMapSizeSetup);
        panelControls.add(labelWinLength);
        panelControls.add(sliderWinLengthSetup);
        panelControls.add(btnStart);
        panelControls.add(btnExit);
        panelControls.add(btnClearLog);
    }

    private void prepareWindowSettings() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(winWidth, winHeight);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) screenSize.getWidth() / 2 - halfWinWidth, (int) screenSize.getHeight() / 2 - halfHeight);
        setTitle("Tic-Tac-Toe");
        setResizable(false);
    }

    private void prepareButtons() {
        btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                collectAllGameSetupFromUser();
            }
        });

        btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        btnClearLog = new JButton("Clear Log");
        btnClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLog.setText("");
            }
        });
    }

    private void prepareGameLog() {
        gameLog = new JTextArea();
        scrollPanel = new JScrollPane(gameLog);
        gameLog.setEditable(false);
        gameLog.setLineWrap(true);
    }

    void recordLog(String text) {
        gameLog.append(text + "\n");
    }

    private void collectAllGameSetupFromUser() {
        int mapSize = sliderMapSizeSetup.getValue();
        int winLen = sliderWinLengthSetup.getValue();
        ++round;
        recordLog("--- Round " + round + " ---");
        recordLog("User choose mapSize " + mapSize + "x" + mapSize);
        recordLog("User choose winLen " + winLen);
        gameMap.startGame(mapSize, mapSize, winLen);
    }
}
