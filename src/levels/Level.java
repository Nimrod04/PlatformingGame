/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package levels;

import entities.Crabby;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.Game;
import objects.Spike;
import static utils.HelpMethods.GetLevelData;
import static utils.HelpMethods.GetCrabs;
import static utils.HelpMethods.GetPlayerSpawn;
import static utils.HelpMethods.GetSpikes;

/**
 *
 * @author nimro
 */
public class Level {
    private int baseScore;

    private BufferedImage img;
    
    private ArrayList<Crabby> crabs;
    private ArrayList<Spike> spikes;

    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;

    private int[][] lvlData;
    private Point playerSpawn;

    public Level(BufferedImage img) {
        this.img = img;
        createLvlData();
        createEnemies();
        createSpikes();
        
        
        calculateLvlOffset();
        spawnPlayer();
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int[][] getLvlData() {
        return lvlData;
    }

    private void createLvlData() {
        lvlData = GetLevelData(img);
    }

    private void createEnemies() {
        crabs = GetCrabs(img);
    }

    private void calculateLvlOffset() {
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
    }

    public ArrayList<Crabby> getCrabs() {
        return crabs;
    }

    public int getMaxLvlOffsetX() {
        return maxLvlOffsetX;
    }

    private void spawnPlayer() {
        playerSpawn = GetPlayerSpawn(img);
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }

    private void createSpikes() {
        spikes = GetSpikes(img);
    }

    public ArrayList<Spike> getSpikes() {
        return spikes;
    }
    

}
