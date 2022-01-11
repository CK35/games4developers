package com.github.ck35.games4developers.board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Spec {

    public enum Element {
        WATER,
        CLIFF,
        LAND,
        TREE,
        GOAL,
        START,
        ENEMY,
        HERO;
    }

    private final List<List<Map.Entry<Location, Element>>> elements;

    private Spec(List<List<Element>> elements) {
        this.elements = new ArrayList<>();
        for (int row = 0; row < elements.size(); row++) {
            List<Map.Entry<Location, Element>> columnLocations = new ArrayList<>();
            this.elements.add(columnLocations);
            List<Element> columnElements = elements.get(row);
            for (int column = 0; column < columnElements.size(); column++) {
                columnLocations.add(new AbstractMap.SimpleEntry<>(Location.of(row, column),
                                                                  columnElements.get(column)));
            }
        }
    }

    public int getColumns() {
        return elements.get(0).size();
    }

    public int getRows() {
        return elements.size();
    }

    public void visit(Consumer<Location> visitor) {
        locations().forEach(visitor);
    }

    public void visit(BiConsumer<Location, Element> visitor) {
        locations().forEach(loc -> visitor.accept(loc, elementAt(loc)));
    }

    public Stream<Location> locations() {
        return elements.stream().flatMap(loc -> loc.stream().map(Map.Entry::getKey));
    }

    public Stream<Element> elements() {
        return elements.stream().flatMap(loc -> loc.stream().map(Map.Entry::getValue));
    }

    public Stream<Map.Entry<Location, Element>> elementsWithLocation() {
        return elements.stream().flatMap(loc -> loc.stream());
    }

    public Element elementAt(Location location) {
        if (location.getRow() >= 0 && location.getRow() < elements.size()) {
            List<Map.Entry<Location, Element>> columns = this.elements.get(location.getRow());
            if (location.getColumn() >= 0 && location.getColumn() < columns.size()) {
                return columns.get(location.getColumn()).getValue();
            }
        }
        return Element.WATER;
    }

    public static Spec load(String resourcePath) throws IOException {
        List<List<Element>> spec = new ArrayList<>();
        try (InputStream in = Spec.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            List<Element> elements = new ArrayList<>();
            spec.add(elements);
            for (int current = reader.read(); current != -1; current = reader.read()) {
                char c = (char) current;
                if (c == '\n') {
                    elements = new ArrayList<>();
                    spec.add(elements);
                } else if (c == '\r' || c == ' ') {
                    // Ignore them
                } else {
                    Element found = Element.LAND;
                    for (Element element : Element.values()) {
                        if (element.name().charAt(0) == c) {
                            found = element;
                            break;
                        }
                    }
                    elements.add(found);
                }
            }
        }
        if (spec.get(spec.size() - 1).isEmpty()) {
            spec.remove(spec.size() - 1);
        }
        int maxWidth = 0;
        for (List<Element> elements : spec) {
            if (elements.size() > maxWidth) {
                maxWidth = elements.size();
            }
        }
        for (List<Element> elements : spec) {
            while (elements.size() < maxWidth) {
                elements.add(Element.WATER);
            }
        }
        BiFunction<Integer, Integer, Element> elementAtIndex = (row, column) -> {
            if (row >= 0 && row < spec.size()) {
                List<Element> elements = spec.get(row);
                if (column >= 0 && column < elements.size()) {
                    return elements.get(column);
                }
            }
            return Element.WATER;
        };
        for (int row = 0; row < spec.size(); row++) {
            List<Element> elements = spec.get(row);
            for (int column = 0; column < elements.size(); column++) {
                if (elements.get(column) == Element.ENEMY || elements.get(column) == Element.HERO) {
                    elements.set(column, Element.LAND);
                } else if (elements.get(column) != Element.WATER) {
                    if (elementAtIndex.apply(row - 1, column) == Element.WATER ||
                        elementAtIndex.apply(row + 1, column) == Element.WATER ||
                        elementAtIndex.apply(row, column - 1) == Element.WATER ||
                        elementAtIndex.apply(row, column + 1) == Element.WATER ||
                        elementAtIndex.apply(row - 1, column - 1) == Element.WATER ||
                        elementAtIndex.apply(row - 1, column + 1) == Element.WATER ||
                        elementAtIndex.apply(row + 1, column - 1) == Element.WATER ||
                        elementAtIndex.apply(row + 1, column + 1) == Element.WATER) {
                        elements.set(column, Element.CLIFF);
                    }
                }
            }
        }
        return new Spec(spec);
    }

}