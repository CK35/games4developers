# games4developers

<img src="/doc/preview.png" alt="Game Preview" width="550">

Playing video games is fun but automating the game by writing code is even more fun! 
At least for developers who can not stop programming :grinning:

The idea behind games for developers is learning programming by having fun and immediately seeing the
result of your work. Use it for educational purpose or for just having fun.

## How to play
The project uses pure Java without any dependencies for running it. Start by opening the
[Main](/src/main/java/com/github/ck35/games4developers/Main.java) class.

You can directly start the game by invoking the main method. When the game window is visible press the
return key to start the game. If you win the game the screen will be filled with green color. If you
loose red color will show up. You can always restart the game by pressing the return key on your keyboard.

To safe the princess simply implement an algorithm within the
[AutomatedPlayer](/src/main/java/com/github/ck35/games4developers/automated/AutomatedPlayer.java) class 
and control the movements of the hero. 

## Rules
The board is separated into tiles every action will move the hero by one tile. You can move into any cardinal direction
but be careful not to walk onto a cliff or water tile. There are also enemies which are trying to get you!
So make sure that you hit them first by moving to their tile.

You will win the game when you step onto the tile where the princess is waiting for you.

## Feedback welcome
I am really curious to know what you think about this project. What do you like and what not? Please
report any bugs or improvements.


### Copyright Notice

This project is licensed under the terms of the [GNU General Public License v3.0](LICENSE).

All the images used inside this project are taken from [OpenGameArt](https://opengameart.org/). A great
resource for free video game graphics.

- \[LPC\] Sara by Mandi Paugh and Stephen "Redshrike" Challener contributed by William.Thompsonj licensed by CC-BY 4.0: https://opengameart.org/content/lpc-sara
- Sorlo (Ultimate Smash Friends) by Stephen Challener (Redshrike) and Doudoulolita licensed by CC-BY 3.0: https://opengameart.org/content/sorlo-ultimate-smash-friends
- \[LPC\] Goblin by Stephen "Redshrike" Challener contributed by William.Thompsonj licensed by CC-BY 4.0: https://opengameart.org/content/lpc-goblin
- \[LPC\] Forest tiles: see [forest_tiles_credits.txt](/doc/forest_tiles_credits.txt)