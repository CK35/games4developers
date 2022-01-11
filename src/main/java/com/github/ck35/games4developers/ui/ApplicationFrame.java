package com.github.ck35.games4developers.ui;

import com.github.ck35.games4developers.board.Enemy;
import com.github.ck35.games4developers.board.Gameboard;
import com.github.ck35.games4developers.board.Location;
import com.github.ck35.games4developers.board.VisibleArea;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationFrame extends JFrame {

    private final Character enemyCharacter;
    private final Character princessCharacter;
    private final Character heroCharacter;
    private final Gameboard gameboard;
    private final Tiles tiles;
    private final boolean renderGrid;

    public ApplicationFrame(Tiles tiles,
                            Character enemyCharacter,
                            Character princessCharacter,
                            Character heroCharacter,
                            Gameboard gameboard,
                            boolean renderGrid) {
        super("games4developers");
        this.enemyCharacter = enemyCharacter;
        this.princessCharacter = princessCharacter;
        this.heroCharacter = heroCharacter;
        this.gameboard = gameboard;
        this.tiles = tiles;
        this.renderGrid = renderGrid;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        add(new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                ApplicationFrame.this.repaint(this, g);
            }
        }, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
            }
        });
        gameboard.addChangeListener(this::update);
    }

    private void repaint(JPanel panel, Graphics graphics) {
        BufferedImage image = tiles.baseImage();
        Graphics2D imageGraphics = image.createGraphics();
        imageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);

        tiles.drawBackground(imageGraphics);
        if (renderGrid) {
            tiles.drawGrid(imageGraphics);
        }

        for (int row = 0; row < gameboard.getSpec().getRows(); row++) {

            if (gameboard.getGoalLocation().getRow() == row) {
                princessCharacter.draw(imageGraphics, gameboard.getGoalLocation());
            }

            if (gameboard.getHero().getRow() == row) {
                heroCharacter.draw(imageGraphics, gameboard.getHero());
            }

            for (Enemy enemy : gameboard.getEnemies()) {
                if (enemy.getLocation().getRow() == row) {
                    enemyCharacter.draw(imageGraphics, enemy.getLocation());
                }
            }
            tiles.drawWalls(imageGraphics, row);
        }

        if (gameboard.getGameState() == Gameboard.State.LOST) {
            imageGraphics.setColor(new Color(1f, 0f, 0f, 0.8f));
            imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        } else if (gameboard.getGameState() == Gameboard.State.WON) {
            imageGraphics.setColor(new Color(0f, 1f, 0f, 0.8f));
            imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        } else {
            Set<Location> visibleLocations = VisibleArea.visibleLocationsAround(gameboard,
                                                                                gameboard.getHero(),
                                                                                gameboard.getVisibleAreaRadius())
                                                        .stream()
                                                        .flatMap(list -> list.stream().filter(
                                                                Objects::nonNull))
                                                        .collect(Collectors.toSet());
            visibleLocations.addAll(gameboard.getGoalLocation()
                                             .locationsAround(1)
                                             .stream()
                                             .flatMap(list -> list.stream())
                                             .collect(
                                                     Collectors.toList()));
            tiles.coverInvisibleTiles(imageGraphics, visibleLocations, gameboard.getVisitedLocations());
        }

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        if (image.getWidth() > image.getHeight()) {
            double scale = (double) image.getHeight() / (double) image.getWidth();
            graphics.drawImage(image.getScaledInstance(panel.getWidth(),
                                                       (int) (panel.getWidth() * scale),
                                                       Image.SCALE_SMOOTH),
                               0,
                               0,
                               null);
        } else {
            double scale = (double) image.getWidth() / (double) image.getHeight();
            graphics.drawImage(image.getScaledInstance((int) (panel.getWidth() * scale),
                                                       panel.getHeight(),
                                                       Image.SCALE_SMOOTH),
                               0,
                               0,
                               null);
        }
    }

    public void update() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

}