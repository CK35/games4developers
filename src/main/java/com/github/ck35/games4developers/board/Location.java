package com.github.ck35.games4developers.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Location {

    private final int row;
    private final int column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static Location of(int row, int column) {
        return new Location(row, column);
    }

    public Location move(CardinalDirection direction) {
        return move(direction, 1);
    }

    public Location move(CardinalDirection direction, int distance) {
        return direction.move(this, distance);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public List<List<Location>> locationsAround(int radius) {
        List<List<Location>> rows = new ArrayList<>();
        for (int currentRow = row - radius; currentRow <= row + radius; currentRow++) {
            List<Location> columns = new ArrayList<>();
            rows.add(columns);
            for (int currentColumn = column - radius; currentColumn <= column + radius; currentColumn++) {
                columns.add(Location.of(currentRow, currentColumn));
            }
        }
        return rows;
    }

    public int distance(Location other) {
        if (this.equals(other)) {
            return 0;
        }
        int columns = (other.column - column);
        if (columns < 0) {
            columns = columns * -1;
        }
        int rows = (other.row - row);
        if (rows < 0) {
            rows = rows * -1;
        }
        if (columns == rows) {
            return columns;
        } else {
            return columns + rows;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return row == location.row && column == location.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + "|" + column + ")";
    }

}
