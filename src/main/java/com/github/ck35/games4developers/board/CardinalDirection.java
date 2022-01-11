package com.github.ck35.games4developers.board;

public enum CardinalDirection {

    NORTH(-1, 0),
    NORTH_EAST(-1, 1),
    EAST(0, 1),
    SOUTH_EAST(1, 1),
    SOUTH(1, 0),
    SOUTH_WEST(1, -1),
    WEST(0, -1),
    NORTH_WEST(-1, -1);

    private final int rowOffset;
    private final int columnOffset;

    CardinalDirection(int rowOffset, int columnOffset) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    public Location move(Location location) {
        return move(location, 1);
    }

    public Location move(Location location, int distance) {
        return new Location(location.getRow() + (rowOffset * distance),
                            location.getColumn() + (columnOffset * distance));
    }

}
