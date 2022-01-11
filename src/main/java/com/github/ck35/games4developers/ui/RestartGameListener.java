package com.github.ck35.games4developers.ui;

import com.github.ck35.games4developers.board.Gameboard;
import com.github.ck35.games4developers.board.PlayerLostException;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RestartGameListener implements KeyEventDispatcher {

    private final Thread gameThread;
    private final Gameboard gameboard;

    private Lock restartLock;
    private Condition restartCondition;
    private boolean restart;

    public RestartGameListener(Gameboard gameboard) {
        this.gameboard = gameboard;
        restart = false;
        restartLock = new ReentrantLock();
        restartCondition = restartLock.newCondition();
        gameThread = new Thread(this::play, "GameThread");
        gameThread.setDaemon(true);
        gameThread.start();
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                            .addKeyEventDispatcher(this);
    }

    private void play() {
        while (!Thread.currentThread().isInterrupted()) {
            this.restartLock.lock();
            try {
                restart = false;
                while (!restart) {
                    restartCondition.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                this.restartLock.unlock();
            }
            try {
                gameboard.play();
            } catch (PlayerLostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.restartLock.lock();
            try {
                restart = true;
                restartCondition.signalAll();
            } finally {
                this.restartLock.unlock();
            }
        }
        return false;
    }

}