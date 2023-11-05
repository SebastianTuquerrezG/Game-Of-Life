package co.com.unicauca.gameoflife.model;

import java.util.*;
import java.awt.*;

/**
 * Cell.java
 * Esta clase representa una celula en el universo de Conway, Juego de la vida
 * @autor Joan Sebastian Tuquerrez Gomez
 * @version 1.0
 * @copy Universidad del Cauca - Ingenieria de Sistemas
 */
public class Cell {
    private Map<String, Cell> space;
    private int x;
    private int y;
    private int state;

    public static final int WILL_BORN = 1;
    public static final int ALIVE = 2;
    public static final int WILL_DIE = 3;

    /**
     * Constructor de la clase Cell, crea una instancia de una celula en el universo
     * @param space Mapa de celulas
     * @param x Posicion en el eje x
     * @param y Posicion en el eje y
     */
    public Cell(Map<String, Cell> space, int x, int y) {
        this.space = space;
        this.x = x;
        this.y = y;
        willBorn();
        space.put(String.format("%d/%d", x, y), this);
    }

    /**
     * Pinta la celula en el universo
     * @param g2D Objeto de tipo Graphics2D para pintar la celula
     * @param offsetX Posicion en el eje x
     * @param offsetY Posicion en el eje y
     */
    public void paint(Graphics2D g2D, int offsetX, int offsetY) {
        if (state == ALIVE) {
            g2D.setColor(Color.WHITE);
            g2D.fillRect(x + offsetX, y + offsetY, 1, 1);
        }
    }

    /**
     * Calcula el estado de la celula en la siguiente generacion
     */
    public void nextState() {
        if (state == WILL_BORN) {
            alive();
        } else if (state == WILL_DIE) {
            destructor();
        }
    }

    /**
     * Define el estado de la celula como posible a nacer
     */
    public void willBorn() {
        state = WILL_BORN;
    }

    /**
     * Define el estado de la celula como viva
     */
    public void alive() {
        state = ALIVE;
    }

    /**
     * Define el estado de la celula como posible a morir
     */
    public void willDie() {
        state = WILL_DIE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getState() {
        return state;
    }

    /**
     * Elimina la celula del universo
     */
    public void destructor() {
        space.remove(String.format("%d/%d", x, y));
    }
}
