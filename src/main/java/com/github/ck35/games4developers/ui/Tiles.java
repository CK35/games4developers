package com.github.ck35.games4developers.ui;

import com.github.ck35.games4developers.board.Location;
import com.github.ck35.games4developers.board.Spec;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public abstract class Tiles {

    protected final Spec spec;
    protected final int boxSize;

    public Tiles(Spec spec, int boxSize) {
        this.spec = spec;
        this.boxSize = boxSize;
    }

    public abstract void drawWalls(Graphics2D g2d, int row);

    public abstract void drawBackground(Graphics2D g2d);

    public abstract BufferedImage baseImage();

    public void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(1f, 1f, 1f, 0.5f));
        for (int x = boxSize; x < spec.getColumns() * boxSize; x += boxSize) {
            g2d.drawLine(x, 0, x, spec.getRows() * boxSize);
        }
        for (int y = boxSize; y < spec.getRows() * boxSize; y += boxSize) {
            g2d.drawLine(0, y, spec.getColumns() * boxSize, y);
        }
    }

    public void coverInvisibleTiles(Graphics2D g2d, Set<Location> visibleTiles, Set<Location> visitedLocations) {
        spec.locations().forEach(loc -> {
            if (visibleTiles.contains(loc)) {
                return;
            }
            if (visitedLocations.contains(loc)) {
                g2d.setColor(new Color(0f, 0f, 0f, 0.1f));
            } else {
                g2d.setColor(new Color(0f, 0f, 0f, 0.5f));
            }
            g2d.fillRect(loc.getColumn() * boxSize, loc.getRow() * boxSize, boxSize, boxSize);
        });
    }

    public static BufferedImage merge(BufferedImage... images) {
        BufferedImage result = new BufferedImage(images[0].getWidth(),
                                                 images[1].getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        for (BufferedImage image : images) {
            graphics.drawImage(image, 0, 0, null);
        }
        return result;
    }

}
