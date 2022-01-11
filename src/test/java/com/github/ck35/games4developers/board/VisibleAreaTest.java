package com.github.ck35.games4developers.board;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisibleAreaTest {

    @Test
    void visibleLocationsAround() throws Exception {
        int visibleAreaRadius = 2;
        Spec spec = Spec.load("/VisibleAreaTestMap.txt");
        Player player = next -> null;
        Gameboard gameboard = new Gameboard(spec, player, visibleAreaRadius, 1);
        List<List<Location>> locationsAround = VisibleArea.visibleLocationsAround(gameboard,
                                                                                  gameboard.getHero(),
                                                                                  visibleAreaRadius);
        assertNull(locationsAround.get(0).get(3));
        assertNull(locationsAround.get(0).get(4));
        assertNull(locationsAround.get(1).get(4));
    }

}