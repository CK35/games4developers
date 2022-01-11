package com.github.ck35.games4developers.board;

import java.util.*;
import java.util.stream.Collectors;

public class Enemy {

    private Location location;
    private boolean following;

    public Enemy(Location location) {
        this.location = location;
        this.following = false;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isFollowing() {
        return following;
    }

    public Player.Action next(Gameboard gameboard,
                              List<List<Location>> visibleLocations,
                              Player.Action heroesLastAction) {
        Location heroVisible = visibleLocations.stream()
                                               .flatMap(list -> list.stream())
                                               .filter(loc -> gameboard.getHero().equals(loc))
                                               .findFirst()
                                               .orElse(null);
        if (heroVisible == null) {
            following = false;
            return Player.Action.doWait();
        }
        if (new Random(11).nextInt() == 10) {
            return Player.Action.doWait();
        }
        if (!following) {
            following = new Random().nextInt(2) == 1;
        }
        if (!following) {
            return Player.Action.doWait();
        }
        List<Map.Entry<Integer, CardinalDirection>> possibleMoves = new ArrayList<>();
        for (CardinalDirection direction : CardinalDirection.values()) {
            Location next = location.move(direction);
            Spec.Element element = gameboard.elementAt(next);
            if (element.equals(Spec.Element.HERO) || element.equals(Spec.Element.LAND)) {
                possibleMoves.add(new AbstractMap.SimpleEntry(next.distance(heroVisible), direction));
            }
        }
        int minDistance = Integer.MAX_VALUE;
        for (Map.Entry<Integer, CardinalDirection> entry : possibleMoves) {
            if (entry.getKey() < minDistance) {
                minDistance = entry.getKey();
            }
        }
        int result = minDistance;
        List<CardinalDirection> remainingDirections = possibleMoves.stream()
                                                                   .filter(entry -> entry.getKey() ==
                                                                                    result)
                                                                   .map(Map.Entry::getValue)
                                                                   .collect(
                                                                           Collectors.toList());
        if (remainingDirections.isEmpty()) {
            return Player.Action.doWait();
        }
        return Player.Action.move(remainingDirections.get(new Random().nextInt(remainingDirections.size())));
    }
}
