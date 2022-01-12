package com.github.ck35.games4developers.board;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisibleArea {

    private final Location center;
    private final List<List<Spec.Element>> elements;
    private final int radius;

    private VisibleArea(Location center, List<List<Spec.Element>> elements, int radius) {
        this.center = center;
        this.elements = elements;
        this.radius = radius;
    }

    public Stream<Spec.Element> allElements() {
        return elements.stream().flatMap(Collection::stream);
    }

    public Optional<Spec.Element> elementAt(CardinalDirection direction) {
        return elementAt(direction, 1);
    }

    public Optional<Spec.Element> elementAt(CardinalDirection direction, int distance) {
        Location location = center.move(direction, distance);
        if (location.getRow() >= 0 && location.getRow() < elements.size()) {
            List<Spec.Element> columns = this.elements.get(location.getRow());
            if (location.getColumn() >= 0 && location.getColumn() < columns.size()) {
                return Optional.ofNullable(columns.get(location.getColumn()));
            }
        }
        return Optional.empty();
    }

    public int getRadius() {
        return radius;
    }

    public static VisibleArea of(Gameboard gameboard, Location location, int radius) {
        Function<Location, Spec.Element> mapToElement = loc -> loc == null ? null : gameboard.elementAt(loc);
        Function<List<Location>, List<Spec.Element>> mapToElementList = list -> list.stream()
                                                                                    .map(mapToElement)
                                                                                    .collect(Collectors.toList());
        List<List<Spec.Element>> elements = visibleLocationsAround(gameboard, location, radius).stream()
                                                                                               .map(mapToElementList)
                                                                                               .collect(Collectors.toList());
        Location center = Location.of(radius, radius);
        return new VisibleArea(center, elements, radius);
    }

    public static List<List<Location>> visibleLocationsAround(Gameboard gameboard, Location location, int radius) {
        List<List<Location>> locations = location.locationsAround(radius);
        Function<Location, Location> getLocation = loc -> locations.get(loc.getRow()).get(loc.getColumn());
        Consumer<Location> clearLocation = loc -> locations.get(loc.getRow()).set(loc.getColumn(), null);

        Location center = Location.of(radius, radius);

        Consumer<Location> checkNorthEast = northEast -> {
            Location gameboardLocation = getLocation.apply(northEast);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(northEast.move(CardinalDirection.NORTH));
                clearLocation.accept(northEast.move(CardinalDirection.NORTH_EAST));
                clearLocation.accept(northEast.move(CardinalDirection.EAST));
            }
        };
        Consumer<Location> checkEast = east -> {
            Location gameboardLocation = getLocation.apply(east);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(east.move(CardinalDirection.EAST));
                clearLocation.accept(east.move(CardinalDirection.SOUTH_EAST));
                clearLocation.accept(east.move(CardinalDirection.NORTH_EAST));
            }
        };
        Consumer<Location> checkSouthEast = southEast -> {
            Location gameboardLocation = getLocation.apply(southEast);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(southEast.move(CardinalDirection.EAST));
                clearLocation.accept(southEast.move(CardinalDirection.SOUTH_EAST));
                clearLocation.accept(southEast.move(CardinalDirection.SOUTH));
            }
        };
        Consumer<Location> checkSouth = south -> {
            Location gameboardLocation = getLocation.apply(south);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(south.move(CardinalDirection.SOUTH));
                clearLocation.accept(south.move(CardinalDirection.SOUTH_WEST));
                clearLocation.accept(south.move(CardinalDirection.SOUTH_EAST));
            }
        };
        Consumer<Location> checkSouthWest = southWest -> {
            Location gameboardLocation = getLocation.apply(southWest);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(southWest.move(CardinalDirection.SOUTH));
                clearLocation.accept(southWest.move(CardinalDirection.SOUTH_WEST));
                clearLocation.accept(southWest.move(CardinalDirection.WEST));
            }
        };
        Consumer<Location> checkWest = west -> {
            Location gameboardLocation = getLocation.apply(west);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(west.move(CardinalDirection.WEST));
                clearLocation.accept(west.move(CardinalDirection.SOUTH_WEST));
                clearLocation.accept(west.move(CardinalDirection.NORTH_WEST));
            }
        };
        Consumer<Location> checkNorthWest = northWest -> {
            Location gameboardLocation = getLocation.apply(northWest);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(northWest.move(CardinalDirection.NORTH));
                clearLocation.accept(northWest.move(CardinalDirection.NORTH_WEST));
                clearLocation.accept(northWest.move(CardinalDirection.WEST));
            }
        };
        Consumer<Location> checkNorth = north -> {
            Location gameboardLocation = getLocation.apply(north);
            if (gameboardLocation == null || gameboard.elementAt(gameboardLocation) == Spec.Element.TREE) {
                clearLocation.accept(north.move(CardinalDirection.NORTH));
                clearLocation.accept(north.move(CardinalDirection.NORTH_EAST));
                clearLocation.accept(north.move(CardinalDirection.NORTH_WEST));
            }
        };

        for (int distance = 1; distance < radius; distance++) {
            checkNorth.accept(center.move(CardinalDirection.NORTH, distance));
            checkNorthEast.accept(center.move(CardinalDirection.NORTH_EAST, distance));
            checkEast.accept(center.move(CardinalDirection.EAST, distance));
            checkSouthEast.accept(center.move(CardinalDirection.SOUTH_EAST, distance));
            checkSouth.accept(center.move(CardinalDirection.SOUTH, distance));
            checkSouthWest.accept(center.move(CardinalDirection.SOUTH_WEST, distance));
            checkWest.accept(center.move(CardinalDirection.WEST, distance));
            checkNorthWest.accept(center.move(CardinalDirection.NORTH_WEST, distance));
        }

        return locations;
    }

    @Override
    public String toString() {
        return elements.stream()
                       .map(e -> e.stream().map(element -> "" + element).collect(Collectors.joining(" | ")))
                       .collect(
                               Collectors.joining("\n"));
    }
}
