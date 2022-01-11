package com.github.ck35.games4developers.automated;

import com.github.ck35.games4developers.board.VisibleArea;
import com.github.ck35.games4developers.board.CardinalDirection;
import com.github.ck35.games4developers.board.Player;

public class AutomatedPlayer implements Player {

    @Override
    public Action next(VisibleArea visibleArea) {

        //TODO save the princess!!!

        // Use the information from the visibleArea object to calculate the next move.
        // You can move to any cardinal direction by returning e.g. Action.move(CardinalDirection.NORTH)
        // If you do not want to move you can also wait by returning Action.doWait();

        return Action.doWait();

    }

}