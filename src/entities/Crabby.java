/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import main.Game;
import utils.Constants;
import static utils.Constants.Directions.*;

import static utils.Constants.Enemy.*;

/**
 *
 * @author nimro
 */
public class Crabby extends Enemy {

    
    private int attackBoxOffsetX;

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(22,19);
        initAttackBox();
    }

    public void update(int[][] lvlData, Player player) {
        updateBehavior(lvlData, player);
        updateAnimationTick();
        updateAttackBox();

    }

    private void updateBehavior(int[][] lvlData, Player player) {
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }
        if (inAir) {
            inAirUpdate(lvlData);
        } else {
            switch (state) {
                case ATTACK:
                    if (aniIndex == 0) {
                        attackChecked = false;
                    }
                    if (aniIndex == 3 && !attackChecked) {
                        checkPlayerHit(attackBox, player);
                    }
                    break;
                case HIT:
                    break;
                case IDLE:
                    changeState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnToPlayer(player);
                        if (isPlayerCloseForAttack(player)) {
                            changeState(ATTACK);
                        }
                    }

                    move(lvlData);
                    break;
            }
        }

    }

    public int flipX() {
        if (walkDir == RIGHT) {
            return width;
        }
        return 0;

    }

    public int flipW() {
        if (walkDir == RIGHT) {
            return -1;
        }
        return 1;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (82 * Game.SCALE), (int) (19 * Game.SCALE));
        attackBoxOffsetX = (int) (Game.SCALE * 30);
    }

    private void updateAttackBox() {
        attackBox.x = hitbox.x - attackBoxOffsetX;
        attackBox.y = hitbox.y;
    }

}
