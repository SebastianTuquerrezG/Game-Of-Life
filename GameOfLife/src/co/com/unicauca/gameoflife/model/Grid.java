package co.com.unicauca.gameoflife.model;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Grid.java
 * Clase que representa la cuadricula de la interfaz grafica
 * @autor Joan Sebastian Tuquerrez Gomez
 * @version 1.0
 * @copy Universidad del Cauca - Ingenieria de Sistemas
 */
public class Grid extends JPanel {
    private BufferedImage grid;
    private int width;
    private int height;
    private boolean gridOn;

    private final Universe universe;

    public static final int GRAY = (new Color(80, 80, 80)).getRGB();

    /**
     * Constructor de la clase Grid
     * @param universe Universo de Conway
     * @param width Ancho de la cuadricula
     * @param height Alto de la cuadricula
     */
    public Grid(Universe universe, int width, int height) {
        this.width = width;
        this.height = height;
        this.universe = universe;
        setOpaque(false);
        setPreferredSize(new Dimension(width, height));
        initializeGrid();
        gridOn = false;
    }

    /**
     * Pinta la cuadricula
     * @param g Objeto de tipo Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        int scale = universe.getScale()/4;
        g2D.scale(scale, scale);
        if (gridOn && scale >= 1) {
            g2D.drawImage(grid, null, null);
        }
    }

    /**
     * Inicializa la cuadricula
     */
    public void initializeGrid() {
        grid = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (i%4 == 0 || j%4 == 0) {
                    grid.setRGB(i, j, GRAY);
                }
            }
        }
    }
    /**
     * Activa o desactiva la cuadricula
     */
    public void toggleGrid() {
        if (gridOn) {
            gridOn = false;
        } else {
            gridOn = true;
        }
    }
}
