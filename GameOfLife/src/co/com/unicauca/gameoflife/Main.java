package co.com.unicauca.gameoflife;

import co.com.unicauca.gameoflife.view.GameOfLifeView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameOfLifeView frame = new GameOfLifeView();
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.start();
    }
}