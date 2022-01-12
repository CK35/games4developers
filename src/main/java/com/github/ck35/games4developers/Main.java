package com.github.ck35.games4developers;

import com.github.ck35.games4developers.automated.AutomatedPlayer;
import com.github.ck35.games4developers.board.Gameboard;
import com.github.ck35.games4developers.board.Spec;
import com.github.ck35.games4developers.manual.ManualPlayer;
import com.github.ck35.games4developers.ui.ApplicationFrame;
import com.github.ck35.games4developers.ui.Character;
import com.github.ck35.games4developers.ui.RestartGameListener;
import com.github.ck35.games4developers.ui.Tiles;
import com.github.ck35.games4developers.ui.opengameart.ForestTiles;
import com.github.ck35.games4developers.ui.opengameart.GoblinsWord;
import com.github.ck35.games4developers.ui.opengameart.Sara;
import com.github.ck35.games4developers.ui.opengameart.SorloSuperSheet;

public class Main {

    public static void main(String[] args) throws Exception {

        // Open this class and provide an implementation for controlling the movements of the hero.
        AutomatedPlayer player = new AutomatedPlayer();

        // A player which is controlled by pressing w-a-s-d on your keyboard (use q-e-y-x for diagonal moves).
        // You can use this implementation to play around and find out how the enemies react.
        // ManualPlayer player = new ManualPlayer();

        // Defines the number of total enemies on the board. Can be zero for no enemies.
        int numberOfEnemies = 20;

        // For better orientation a grid can be rendered by setting the value to true.
        boolean renderGrid = false;

        // Nothing needs to be changed below this line...
        Spec spec = Spec.load("/map.txt");
        Tiles tiles = ForestTiles.load(spec);
        Character enemyCharacter = GoblinsWord.load(ForestTiles.TILE_SIZE);
        Character princessCharacter = Sara.load(ForestTiles.TILE_SIZE);
        Character heroCharacter = SorloSuperSheet.load(ForestTiles.TILE_SIZE);

        Gameboard gameboard = new Gameboard(spec, player, 2, numberOfEnemies);
        ApplicationFrame applicationFrame = new ApplicationFrame(tiles,
                                                                 enemyCharacter,
                                                                 princessCharacter,
                                                                 heroCharacter,
                                                                 gameboard,
                                                                 renderGrid);
        RestartGameListener restartGameListener = new RestartGameListener(gameboard);
    }

}
