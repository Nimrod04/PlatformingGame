/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.Game;
import utils.LoadSave;

/**
 *
 * @author nimro
 */
public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
    }

    public void draw(Graphics g, int lvlOffset) {
        for (int i = 0; i < Game.TILES_IN_HEIGHT; i++) {
            for (int j = 0; j < levels.get(lvlIndex).getLvlData()[0].length; j++) {
                int index = levels.get(lvlIndex).getSpriteIndex(j, i);
                g.drawImage(levelSprite[index], Game.TILES_SIZE * j - lvlOffset, Game.TILES_SIZE * i, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }

    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levels.get(lvlIndex);
    }

    private void importOutsideSprites() {
        levelSprite = new BufferedImage[48];
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 12; j++) {
                int index = i * 12 + j;
                levelSprite[index] = img.getSubimage(j * 32, i * 32, 32, 32);
            }
        }
    }

    private void buildAllLevels() {
        BufferedImage[] allLvls = LoadSave.GetAllLevels();
        for (BufferedImage img : allLvls) {
            levels.add(new Level(img));
        }
    }
    public int getLevelSize(){
        return levels.size();
    }

    public void setLvlIndex(int lvlIndex) {
        this.lvlIndex = lvlIndex;
    }
    

    public void loadNextLevel() {
        lvlIndex++;
        if (lvlIndex >= levels.size()) {
            //Completed
            System.out.println("GG");
            
        }
        Level nextLvl = levels.get(lvlIndex);
        game.getPlaying().getEnemyManager().loadEnemies(nextLvl);
        game.getPlaying().getPlayer().loadLvlData(nextLvl.getLvlData());
        game.getPlaying().setMaxLvlOffsetX(nextLvl.getMaxLvlOffsetX());
        game.getPlaying().getObjectManager().loadObjects(nextLvl);
    }
    

}
