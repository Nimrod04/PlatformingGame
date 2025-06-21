/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import entities.Crabby;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.Game;
import static utils.Constants.Enemy.CRABBY;

/**
 *
 * @author nimro
 */
public class LoadSave {

    public static final String PLAYER_ATLAS = "/player_sprites.png";
    public static final String STATUS_BAR = "/health_power_bar.png";

    public static final String LEVEL_ATLAS = "/outside_sprites.png";
    public static final String MENU_BUTTONS = "/button_atlas.png";
    public static final String MENU_BACKGROUND = "/menu_background.png";
    public static final String MENU_BACKGROUND_IMG = "/background_menu.png";
    public static final String PLAYING_BG_IMG = "/playing_bg_img.png";
    public static final String PLAYING_BG_BCLOUDS = "/big_clouds.png";
    public static final String PLAYING_BG_SCLOUDS = "/small_clouds.png";

    public static final String CRABBY_SPRITE = "/crabby_sprite.png";

    public static final String URM_BUTTONS = "/urm_buttons.png";
    public static final String COMPLETED_IMG = "/completed_sprite.png";
    
    public static final String TRAP_ATLAS = "/trap_atlas.png";
    public static final String DEATH_SCREEN = "/death_screen.png";


    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream(fileName);
        try {
            img = ImageIO.read(is);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        File[] files = file.listFiles();
        File[] sortedFiles = new File[files.length];

        for (int i = 0; i < sortedFiles.length; i++) {
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png")) {
                    sortedFiles[i] = files[j];
                }
            }
        }
        BufferedImage[] imgs = new BufferedImage[sortedFiles.length];
        for (int i = 0; i < imgs.length; i++) {
            try {
                imgs[i] = ImageIO.read(sortedFiles[i]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return imgs;
    }

    

    
}
