package com.github.ck35.games4developers.automated;

import com.github.ck35.games4developers.board.VisibleArea;
import com.github.ck35.games4developers.board.CardinalDirection;
import com.github.ck35.games4developers.board.Player;

public class AutomatedPlayer implements Player {

    @Override
    public Action next(VisibleArea visibleArea) {

        //TODO save the princess!!!

        // Use the information from the visibleArea object to calculate the next move.
        // Find out what is around you by invoking e.g. visibleArea.elementAt(CardinalDirection.NORTH_EAST)
        // All elements within a certain radius are visible if they are not covered by wall elements.

        // You can move to any cardinal direction by returning e.g. Action.move(CardinalDirection.NORTH)
        // If you do not want to move you can also wait by returning Action.doWait();

        return Action.doWait();

    }

}