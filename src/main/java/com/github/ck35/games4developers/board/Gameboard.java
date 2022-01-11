package com.github.ck35.games4developers.board;

import com.github.ck35.games4developers.manual.ManualPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Gameboard {

    public enum State {
        WAITING_FOR_START,
        RUNNING,
        LOST,
        WON;
    }

    private final Spec spec;
    private final Player player;
    private final int visibleAreaRadius;
    private final int numberOfEnemies;
    private final List<Runnable> changeListeners;

    private final Location goalLocation;
    private final List<Enemy> enemies;
    private volatile Location hero;
    private final Set<Location> visitedLocations;

    private volatile State gameState;

    public Gameboard(Spec spec, Player player, int visibleAreaRadius, int numberOfEnemies) {
        this.spec = spec;
        this.player = player;
        this.visibleAreaRadius = visibleAreaRadius;
        this.numberOfEnemies = numberOfEnemies;
        this.changeListeners = new CopyOnWriteArrayList<>();
        this.visitedLocations = ConcurrentHashMap.newKeySet();
        this.enemies = new CopyOnWriteArrayList<>();
        this.goalLocation = spec.elementsWithLocation()
                                .filter(entry -> entry.getValue() == Spec.Element.GOAL)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(("Spec does not contain a goal!")));
        this.hero = spec.elementsWithLocation()
                        .filter(entry -> entry.getValue() == Spec.Element.START)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Spec does not contain a start!"));
        this.gameState = State.WAITING_FOR_START;
    }

    public Spec.Element elementAt(Location location) {
        if (hero.equals(location)) {
            return Spec.Element.HERO;
        }
        if (enemies.stream().anyMatch(enemy -> enemy.getLocation().equals(location))) {
            return Spec.Element.ENEMY;
        }
        return spec.elementAt(location);
    }

    public Location getGoalLocation() {
        return goalLocation;
    }

    public Location getHero() {
        return hero;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void play() throws PlayerLostException {
        this.gameState = State.RUNNING;
        try {
            this.visitedLocations.clear();
            this.hero = spec.elementsWithLocation()
                            .filter(entry -> entry.getValue() == Spec.Element.START)
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Spec does not contain a start!"));

            this.enemies.clear();
            IntStream.range(0, numberOfEnemies).mapToObj(i -> {
                for (int round = 0; round < (spec.getRows() * spec.getColumns() * 4); round++) {
                    int row = new Random().nextInt(spec.getRows());
                    int column = new Random().nextInt(spec.getColumns());
                    Location location = Location.of(row, column);
                    if (elementAt(location) == Spec.Element.LAND) {
                        return new Enemy(location);
                    }
                }
                throw new IllegalArgumentException("Could not place enemy on game board!");
            }).forEach(enemies::add);
            fireChangeEvent();
            while (!isGameWon()) {
                Player.Action heroesLastAction = heroesTurn();
                enemiesTurn(heroesLastAction);
            }
        } catch (PlayerLostException e) {
            this.gameState = State.LOST;
            fireChangeEvent();
            throw e;
        }
        this.gameState = State.WON;
        fireChangeEvent();
    }

    private void enemiesTurn(Player.Action heroesLastAction) throws PlayerLostException {
        for (Enemy enemy : enemies) {
            Player.Action action = enemy.next(this,
                                              VisibleArea.visibleLocationsAround(this, enemy.getLocation(),
                                                                                 visibleAreaRadius),
                                              heroesLastAction);
            if (action instanceof Player.Action.Movement) {
                Location newLocation = enemy.getLocation()
                                            .move(((Player.Action.Movement) action).getCardinalDirection());
                enemy.setLocation(newLocation);
                fireChangeEvent();
                if (newLocation.equals(hero)) {
                    throw new PlayerLostException("Eliminated by guard!");
                }
            }
        }
    }

    private Player.Action heroesTurn() throws PlayerLostException {
        VisibleArea visibleArea = VisibleArea.of(this, hero, visibleAreaRadius);
        Player.Action action;
        try {
            action = player.next(visibleArea);
        } catch (RuntimeException e) {
            throw new PlayerLostException("Player has thrown an exception!", e);
        }
        if (action instanceof Player.Action.Movement) {
            move((Player.Action.Movement) action);
        }
        return action;
    }

    private void move(Player.Action.Movement movement) throws PlayerLostException {
        Location newLocation = hero.move(movement.getCardinalDirection());
        Spec.Element element = elementAt(newLocation);
        Enemy enemy = enemies.stream()
                             .filter(e -> e.getLocation().equals(newLocation))
                             .findFirst()
                             .orElse(null);
        if (enemy != null) {
            enemies.remove(enemy);
        }
        if (element == Spec.Element.CLIFF || element == Spec.Element.WATER) {
            throw new PlayerLostException("Player fell into water!");
        } else if (element == Spec.Element.TREE) {
            // IGNORE movement
        } else {
            visitedLocations.addAll(VisibleArea.visibleLocationsAround(this, hero, visibleAreaRadius)
                                               .stream()
                                               .flatMap(list -> list.stream().filter(Objects::nonNull))
                                               .collect(
                                                       Collectors.toList()));
            hero = newLocation;
            fireChangeEvent();
            if (!(player instanceof ManualPlayer)) {
                try {
                    Thread.sleep(8_00);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during sleep period exiting now!");
                }
            }
        }
    }

    public boolean isGameWon() {
        return hero.equals(goalLocation);
    }

    public State getGameState() {
        return gameState;
    }

    public Spec getSpec() {
        return spec;
    }

    public int getVisibleAreaRadius() {
        return visibleAreaRadius;
    }

    public Set<Location> getVisitedLocations() {
        return visitedLocations;
    }

    public void addChangeListener(Runnable changeListener) {
        changeListeners.add(changeListener);
    }

    private void fireChangeEvent() {
        changeListeners.forEach(changeListener -> changeListener.run());
    }

}