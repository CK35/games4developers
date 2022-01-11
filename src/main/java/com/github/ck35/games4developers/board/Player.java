package com.github.ck35.games4developers.board;

public interface Player {

    interface Action {

        static DoWait doWait() {
            return new DoWait();
        }

        static Movement move(CardinalDirection cardinalDirection) {
            return new Movement(cardinalDirection);
        }

        class DoWait implements Action {

            private DoWait() {

            }

        }

        class Movement implements Action {

            private final CardinalDirection cardinalDirection;

            private Movement(CardinalDirection cardinalDirection) {
                this.cardinalDirection = cardinalDirection;
            }

            public CardinalDirection getCardinalDirection() {
                return cardinalDirection;
            }
        }
    }

    Action next(VisibleArea visibleArea);

}
