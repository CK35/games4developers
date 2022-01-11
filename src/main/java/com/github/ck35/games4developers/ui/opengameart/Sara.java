package com.github.ck35.games4developers.ui.opengameart;

import com.github.ck35.games4developers.board.Location;
import com.github.ck35.games4developers.ui.Character;
import com.github.ck35.games4developers.ui.Tiles;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Sara implements Character {

    private final Image image;
    private final int tileSize;

    private Sara(Image image, int tileSize) {
        this.image = image;
        this.tileSize = tileSize;
    }

    @Override
    public void draw(Graphics2D graphics2D, Location location) {
        graphics2D.drawImage(image, (location.getColumn() * tileSize) + 6, (location.getRow() * tileSize) - 21, null);
    }

    public static Sara load(int tileSize) throws IOException {
        BufferedImage image;
        try (InputStream stream = Tiles.class.getResourceAsStream("/opengameart/SaraFullSheet_single.png")) {
            image = ImageIO.read(stream);
        }
        // 28w x 50h original size
        Image scaled = image.getScaledInstance(24, 46, Image.SCALE_SMOOTH);
        return new Sara(scaled, tileSize);
    }

}
