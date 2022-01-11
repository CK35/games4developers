package com.github.ck35.games4developers.ui.opengameart;

import com.github.ck35.games4developers.board.Location;
import com.github.ck35.games4developers.board.Spec;
import com.github.ck35.games4developers.ui.Tiles;

import static com.github.ck35.games4developers.board.CardinalDirection.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ForestTiles extends Tiles {

    public static final int TILE_SIZE = 32;

    private final Spec spec;
    private final Image goal;
    private final Image start;
    private final Image tree;
    private final Image background;

    private ForestTiles(Spec spec, Image goal, Image start, Image tree, Image background) {
        super(spec, TILE_SIZE);
        this.spec = spec;
        this.goal = goal;
        this.start = start;
        this.tree = tree;
        this.background = background;
    }

    @Override
    public BufferedImage baseImage() {
        return new BufferedImage(spec.getColumns() * TILE_SIZE,
                                 spec.getRows() * TILE_SIZE,
                                 BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void drawWalls(Graphics2D g2d, int row) {
        spec.visit((loc, element) -> {
            if (loc.getRow() != row) {
                return;
            }
            if (element == Spec.Element.TREE) {
                g2d.drawImage(tree, loc.getColumn() * TILE_SIZE, (loc.getRow() - 1) * TILE_SIZE, null);
            }
        });
    }

    @Override
    public void drawBackground(Graphics2D g2d) {
        g2d.drawImage(background, 0, 0, null);
    }

    public static ForestTiles load(Spec spec) throws IOException {
        BufferedImage image;
        try (InputStream stream = Tiles.class.getResourceAsStream("/opengameart/forest_tiles.png")) {
            image = ImageIO.read(stream);
        }

        Image goal = image.getSubimage(1 * TILE_SIZE, 5 * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Image start = image.getSubimage(12 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Image tree = image.getSubimage(10 * TILE_SIZE, 1 * TILE_SIZE, TILE_SIZE, TILE_SIZE * 2);
        Image background = createBackground(image, spec);

        return new ForestTiles(spec, goal, start, tree, background);
    }

    private static BufferedImage createBackground(BufferedImage image, Spec spec) {
        List<List<List<Image>>> coastLines = Arrays.asList(loadCoastLine(image, 4, 7),
                                                           loadCoastLine(image, 7, 7),
                                                           loadCoastLine(image, 10, 8));
        Supplier<List<List<Image>>> randomCoastLine = () -> coastLines.get(new Random().nextInt(3));


        List<Image> landImages = loadLandImages(image);
        Supplier<Image> randomLandImage = () -> landImages.get(new Random().nextInt(landImages.size()));

        BufferedImage background = new BufferedImage(TILE_SIZE * spec.getColumns(),
                                                     TILE_SIZE * spec.getRows(),
                                                     BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = background.createGraphics();
        BiConsumer<Location, Image> draw = (loc, img) -> graphics.drawImage(img,
                                                                            loc.getColumn() * TILE_SIZE,
                                                                            loc.getRow() * TILE_SIZE,
                                                                            null);
        Predicate<Location> isWater = loc -> spec.elementAt(loc) == Spec.Element.WATER;
        Predicate<Location> isLand = loc -> spec.elementAt(loc) != Spec.Element.WATER;
        spec.visit((location, element) -> {
            if (element == Spec.Element.WATER) {
                draw.accept(location, randomCoastLine.get().get(1).get(1));
            } else if (isWater.test(location.move(WEST)) && isWater.test(location.move(SOUTH))) {
                draw.accept(location, image.getSubimage(5 * TILE_SIZE, 10 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
            } else if (isWater.test(location.move(EAST)) && isWater.test(location.move(NORTH))) {
                draw.accept(location, image.getSubimage(4 * TILE_SIZE, 11 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
            } else if (isWater.test(location.move(EAST)) && isWater.test(location.move(SOUTH))) {
                draw.accept(location, image.getSubimage(6 * TILE_SIZE, 10 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
            } else if (isWater.test(location.move(WEST)) && isWater.test(location.move(NORTH))) {
                draw.accept(location, image.getSubimage(5 * TILE_SIZE, 11 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
            } else if (isWater.test(location.move(NORTH_EAST)) && isLand.test(location.move(EAST)) && isLand.test(
                    location.move(NORTH))) {
                draw.accept(location, randomCoastLine.get().get(2).get(0));
            } else if (isWater.test(location.move(NORTH_WEST)) &&
                       isLand.test(location.move(WEST)) && isLand.test(location.move(NORTH))) {
                draw.accept(location, randomCoastLine.get().get(2).get(2));
            } else if (isWater.test(location.move(SOUTH_EAST)) &&
                       isLand.test(location.move(EAST)) && isLand.test(location.move(SOUTH))) {
                draw.accept(location, randomCoastLine.get().get(0).get(0));
            } else if (isWater.test(location.move(SOUTH_WEST)) &&
                       isLand.test(location.move(WEST)) && isLand.test(location.move(SOUTH))) {
                draw.accept(location, randomCoastLine.get().get(0).get(2));
            } else if (isWater.test(location.move(WEST))) {
                draw.accept(location, randomCoastLine.get().get(1).get(2));
            } else if (isWater.test(location.move(EAST))) {
                draw.accept(location, randomCoastLine.get().get(1).get(0));
            } else if (isWater.test(location.move(SOUTH))) {
                draw.accept(location, randomCoastLine.get().get(0).get(1));
            } else if (isWater.test(location.move(NORTH))) {
                draw.accept(location, randomCoastLine.get().get(2).get(1));
            } else if (element == Spec.Element.LAND) {
                draw.accept(location, randomLandImage.get());
            } else {
                draw.accept(location, landImages.get(0));
            }
        });
        return background;
    }

    private static List<List<Image>> loadCoastLine(BufferedImage image, int x, int y) {
        List<List<Image>> coastLine = new ArrayList<>();
        coastLine.add(Arrays.asList(image.getSubimage(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 1) * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 2) * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
        coastLine.add(Arrays.asList(image.getSubimage(x * TILE_SIZE, (y + 1) * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 1) * TILE_SIZE, (y + 1) * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 2) * TILE_SIZE, (y + 1) * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
        coastLine.add(Arrays.asList(image.getSubimage(x * TILE_SIZE, (y + 2) * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 1) * TILE_SIZE, (y + 2) * TILE_SIZE, TILE_SIZE, TILE_SIZE),
                                    image.getSubimage((x + 2) * TILE_SIZE, (y + 2) * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
        return coastLine;
    }

    private static List<Image> loadLandImages(BufferedImage image) {
        BufferedImage baseImage = image.getSubimage(0, 0, TILE_SIZE, TILE_SIZE);
        return Arrays.asList(baseImage,
                             baseImage,
                             image.getSubimage(0, TILE_SIZE, TILE_SIZE, TILE_SIZE),
                             baseImage,
                             baseImage,
                             merge(baseImage, image.getSubimage(0, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE)),
                             baseImage,
                             baseImage,
                             merge(baseImage, image.getSubimage(1 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE)),
                             baseImage,
                             baseImage,
                             merge(baseImage, image.getSubimage(4 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE)),
                             baseImage,
                             baseImage,
                             merge(baseImage, image.getSubimage(5 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE)),
                             baseImage,
                             baseImage,
                             merge(baseImage, image.getSubimage(6 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
    }

}
