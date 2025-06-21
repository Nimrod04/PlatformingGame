/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import database.Leaderboard;
import gamestates.Playing;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import main.Game;
import static utils.Constants.*;

import static utils.Constants.Directions.*;
import static utils.Constants.PlayerConstants.*;
import utils.LoadSave;
import static utils.HelpMethods.*;

/**
 *
 * @author nimro
 */
public class Player extends Entity {

    private int seconds = 0; // Az eltelt idő másodpercben
    private Timer timer; // Az időzítő
    private boolean running = false;
    private int timerX;
    private int timerY;
    String time;

    public String playerName;

    private int highScore;
    private int enemyKilled;

    private BufferedImage[][] aniMatrix;

    private boolean moving = false, attacking = false;

    private boolean left, right, jump;

    private int[][] lvlData;

    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    //gravity / jump
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    //StatusBar UI
    private BufferedImage statusBarimg;
    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int HealthBarWidth = (int) (150 * Game.SCALE);
    private int HealthBarHeight = (int) (4 * Game.SCALE);
    private int HealthBarXStart = (int) (34 * Game.SCALE);
    private int HealthBarYStart = (int) (14 * Game.SCALE);

    private int healthWidth = HealthBarWidth;

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private boolean highscoreAdded;

    private Playing playing;

    public Player(float x, float y, int w, int h, Playing playing) {
        super(x, y, w, h);
        this.highScore = 0;
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = Game.SCALE * 1.0f;
        this.enemyKilled = 0;
        loadAnimations();
        initHitbox(20, 27);
        initAttackBox();
        timerX = statusBarX + statusBarWidth + 10;
        timerY = statusBarY + statusBarHeight / 2;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
            }
        });
        startTimer();
    }

    public void setSpawn(Point p) {
        this.x = p.x;
        this.y = p.y;
        hitbox.x = x;
        hitbox.y = y;
    }

    public void update() {
        updateHp();
        if (currentHealth <= 0) {
            if (state != DEAD) {
                state = DEAD;
                aniTick = 0;
                aniIndex = 0;
                playing.setPlayerDying(true);
            } else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANISPEED - 1) {
                playing.setGameOver(true);
                stopTimer();
                this.playerName = JOptionPane.showInputDialog(null, "Mi a neved? \n(ha üresen hagyod akkor nem lesz elmentve a pontszámod)", "Játékos név", JOptionPane.QUESTION_MESSAGE);
                if (playerName.equals("")) {
                    System.out.println("Score not saved!");
                } else {
                    highScore += enemyKilled * 1000 + seconds * 0.1;
                    System.out.println(playerName + ": " + highScore);

                    if (highScore > 0) {
                        Leaderboard.saveScore(playerName, highScore);
                        enemyKilled = 0;
                        resetAll();
                        resetDirBooleans();
                    }

                }
            } else {
                updateAnimationTick();
            }

            //playing.setGameOver(true);
            return;
        }

        updateAttackBox();
        updatePos();
        if (moving) {
            checkSpikesTouched();
        }

        if (attacking) {
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g, int lvlOffset) {
        g.drawImage(aniMatrix[state][aniIndex],
                (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset),
                width * flipW, height, null);
        //drawHitbox(g, lvlOffset);
        //drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANISPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(state)) {
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    private void setAnimation() {
        int startAni = state;
        if (moving) {
            state = RUNNING;
        } else {
            state = IDLE;
        }
        if (inAir) {
            if (airSpeed < 0) {
                state = JUMP;
            } else {
                state = FALLING;
            }
        }
        if (attacking) {
            state = ATTACK;
            if (startAni != ATTACK) {
                aniIndex = 1;
                aniTick = 0;
                return;
            }
        }
        if (startAni != state) {
            resetAniTick();
        }
    }

    private void updatePos() {
        moving = false;
        if (jump) {
            jump();
        }
        if (!inAir) {
            if ((!left && !right) || (left && right)) {
                return;
            }
        }
        float xSpeed = 0;

        if (left) {
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }
        if (!inAir) {
            if (!IsEntityOnFloor(hitbox, lvlData)) {
                inAir = true;
            }
        }

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0) {
                    resetInAir();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        } else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        aniMatrix = new BufferedImage[7][8];
        for (int i = 0; i < aniMatrix.length; i++) {
            for (int j = 0; j < aniMatrix[i].length; j++) {
                aniMatrix[i][j] = img.getSubimage(j * 64, i * 40, 64, 40);
            }
        }
        statusBarimg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    public void changeHp(int value) {
        currentHealth += value;
        if (currentHealth <= 0) {
            if (!highscoreAdded) {
                System.out.println("Enemy killed: " + enemyKilled);
                highscoreAdded = true;
            }
            currentHealth = 0;

            //gameOver();
        } else if (currentHealth >= maxHealth) {
            currentHealth = maxHealth;
        }
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void jump() {
        if (inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    public void setJump(boolean b) {
        this.jump = b;
    }

    public void startTimer() {
        if (!running) {
            timer.start();
            running = true;
        }
    }

    public void stopTimer() {
        if (running) {
            timer.stop();
            running = false;
        }
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarimg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        g.fillRect(HealthBarXStart + statusBarX, HealthBarYStart + statusBarY, healthWidth, HealthBarHeight);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        time = String.format("%02d:%02d", seconds / 60, seconds % 60);
        g.drawString(time, timerX, timerY);
    }

    private void updateHp() {
        healthWidth = (int) ((currentHealth / ((float) maxHealth)) * HealthBarWidth);
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
    }

    private void updateAttackBox() {
        if (right && left) {
            if (flipW == 1) {
                attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
            } else {
                attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
            }
        } else if (right) {
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
        } else if (left) {
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
        }
        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    private void checkAttack() {
        if (attackChecked || aniIndex != 1) {
            return;
        }
        attackChecked = true;
        playing.checkEnemyHit(attackBox);

    }

    public void resetAll() {
        resetDirBooleans();
        inAir = true;

        //jump = false;
        attacking = false;
        moving = false;
        state = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x;
        hitbox.y = y;

    }

    public int getEnemyKilled() {
        return enemyKilled;
    }

    public void setEnemyKilled(int enemyKilled) {
        this.enemyKilled += enemyKilled;
    }

    public void kill() {
        currentHealth = 0;
        if (!highscoreAdded) {
            System.out.println("Enemy killed: " + enemyKilled);
            highscoreAdded = true;
        }
    }

    private void checkSpikesTouched() {
        playing.checkSpikesTouched(this);
    }

    public void setHighscoreAdded(boolean b) {
        this.highscoreAdded = b;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

}
