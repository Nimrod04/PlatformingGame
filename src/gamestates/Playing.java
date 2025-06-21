/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gamestates;

import entities.EnemyManager;
import entities.Player;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import levels.LevelManager;
import main.Game;
import static main.Game.SCALE;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelComplatedOverlay;
import utils.LoadSave;
import static utils.Constants.Environment.*;

/**
 *
 * @author nimro
 */
public class Playing extends State implements Statemethods {

    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;

    private BufferedImage bgImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;

    private Random rnd = new Random();

    private boolean gameOver = false;
    private GameOverOverlay gameOverOverlay;
    private LevelComplatedOverlay levelComplatedOverlay;

    private boolean lvlCompleted;
    private boolean dying;

    public Playing(Game game) {
        super(game);
        initClasses();
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_BCLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_SCLOUDS);
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++) {
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (120 * Game.SCALE));
        }
        calcLvlOffset();
        loadStartLvl();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        player = new Player(200, 200, (int) (64 * SCALE), (int) (40 * SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        gameOverOverlay = new GameOverOverlay(this);
        levelComplatedOverlay = new LevelComplatedOverlay(this);
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void update() {
        if (lvlCompleted) {
            levelComplatedOverlay.update();
        } else if (gameOver) {
            gameOverOverlay.update();
        } else if (dying) {
            player.update();
        } else {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkBorder();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(bgImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        objectManager.draw(g, xLvlOffset);
        if (gameOver) {
            gameOverOverlay.draw(g);
        } else if (lvlCompleted) {
            levelComplatedOverlay.draw(g);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                player.setAttacking(true);
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (lvlCompleted) {
                levelComplatedOverlay.mousePressed(e);
            }
        }else{
            gameOverOverlay.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (lvlCompleted) {
                levelComplatedOverlay.mouseReleased(e);
            }
        }else{
            gameOverOverlay.mouseReleased(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            if (lvlCompleted) {
                levelComplatedOverlay.mouseMoved(e);
            }
        }else{
            gameOverOverlay.mouseMoved(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            //gameOverOverlay.keyPressed(e);
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    Gamestate.state = Gamestate.MENU;
                    break;

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
            }
        }
    }

    private void checkBorder() {
        int playerX = (int) (player.getHitbox().x);
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }
        if (xLvlOffset > maxLvlOffsetX) {
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0) {
            xLvlOffset = 0;
        }
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++) {
            g.drawImage(bigCloud, i * BCLOUDS_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE), BCLOUDS_WIDTH, BCLOUDS_HEIGHT, null);
        }
        for (int i = 0; i < smallCloudsPos.length; i++) {
            g.drawImage(smallCloud, SCLOUDS_WIDTH * 4 * i - (int) (xLvlOffset * 0.8), smallCloudsPos[i], SCLOUDS_WIDTH, SCLOUDS_HEIGHT, null);
        }

    }

    public void resetAll() {
        gameOver = false;
        lvlCompleted = false;
        dying = false;
        player.resetAll();
        enemyManager.resetAll();
        objectManager.resetAllObjects();
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox,player);
    }

    public void setGameOver(boolean b) {
        this.gameOver = b;
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getMaxLvlOffsetX();
    }

    private void loadStartLvl() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel());
    }

    public void loadNextLvl() {
        resetAll();
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public void setMaxLvlOffsetX(int maxLvlOffsetX) {
        this.maxLvlOffsetX = maxLvlOffsetX;
    }

    public void setLevelCompleted(boolean b) {
        lvlCompleted = b;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void checkSpikesTouched(Player p) {
        objectManager.checkSpikesTouched(p);
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public void setPlayerDying(boolean b) {
        this.dying = b;
    }

}
