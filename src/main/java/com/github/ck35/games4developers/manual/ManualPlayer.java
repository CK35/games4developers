package com.github.ck35.games4developers.manual;

import com.github.ck35.games4developers.board.VisibleArea;
import com.github.ck35.games4developers.board.CardinalDirection;
import com.github.ck35.games4developers.board.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManualPlayer implements Player {

    private final Lock lock;
    private final Condition condition;

    private Action nextAction;

    public ManualPlayer() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                            .addKeyEventDispatcher(e -> {
                                onKeyEvent(e);
                                return false;
                            });
    }

    private void onKeyEvent(KeyEvent event) {
        Action action = null;
        if (event.getID() == KeyEvent.KEY_PRESSED) {
            if ((event.getModifiersEx() & (KeyEvent.SHIFT_DOWN_MASK)) == KeyEvent.SHIFT_DOWN_MASK) {
                System.out.println("modifier");
            }
            switch (event.getKeyCode()) {
                case KeyEvent.VK_W:
                    action = Action.move(CardinalDirection.NORTH); break;
                case KeyEvent.VK_E:
                    action = Action.move(CardinalDirection.NORTH_EAST); break;
                case KeyEvent.VK_D:
                    action = Action.move(CardinalDirection.EAST); break;
                case KeyEvent.VK_X:
                    action = Action.move(CardinalDirection.SOUTH_EAST); break;
                case KeyEvent.VK_S:
                    action = Action.move(CardinalDirection.SOUTH); break;
                case KeyEvent.VK_Y:
                    action = Action.move(CardinalDirection.SOUTH_WEST); break;
                case KeyEvent.VK_A:
                    action = Action.move(CardinalDirection.WEST); break;
                case KeyEvent.VK_Q:
                    action = Action.move(CardinalDirection.NORTH_WEST); break;
                case KeyEvent.VK_SPACE:
                    action = Action.doWait(); break;
            }
        }
        if (action == null) {
            return;
        }
        this.lock.lock();
        try {
            this.nextAction = action;
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Action next(VisibleArea visibleArea) {
        this.lock.lock();
        try {
            nextAction = null;
            while (nextAction == null) {
                condition.await();
            }
            return nextAction;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for next action command!");
        } finally {
            this.lock.unlock();
        }
    }

}