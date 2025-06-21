/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.Graphics;
import gamestates.*;
import static gamestates.Gamestate.MENU;
import static gamestates.Gamestate.PLAYING;
import database.*;
import utils.LoadSave;

/**
 *
 * @author nimro
 */
public class Game implements Runnable {

    private Leaderboard leaderboard;
    private boolean showingLeaderboard;

    private Playing playing;
    private Menu menu;

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 240;
    private final int UPS_SET = 200;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);

    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();

    }

    public boolean isShowingLeaderboard() {
        return showingLeaderboard;
    }

    public void setShowingLeaderboard(boolean showingLeaderboard) {
        this.showingLeaderboard = showingLeaderboard;
    }
    

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long preTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - preTime) / timePerUpdate;
            deltaF += (currentTime - preTime) / timePerFrame;
            preTime = currentTime;
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;

            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }

        }

    }

    private void update() {
        switch (Gamestate.state) {
            case PLAYING:
                playing.update();
//                playing.getPlayer().setHighscoreAdded(false);
                playing.getPlayer().setEnemyKilled(0);
                playing.getPlayer().setHighScore(0);
                break;
            case MENU:
                
                menu.update();
                showingLeaderboard = false;
                break;

            case QUIT:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    public void render(Graphics g) {
        switch (Gamestate.state) {
            case LEADERBOARD:
                if (!showingLeaderboard) {
                    Leaderboard.showTopScores();
                    showingLeaderboard = true;
                }
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case MENU:
                menu.draw(g);
                break;
            default:
                break;
        }
    }

    public Playing getPlaying() {
        return playing;
    }

    public Menu getMenu() {
        return menu;
    }

    private void initClasses() {
        menu = new Menu(this);
        playing = new Playing(this);
    }

    public void windowFocusLost() {
        if (Gamestate.state == Gamestate.PLAYING) {
            playing.getPlayer().resetDirBooleans();
        }
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

}
