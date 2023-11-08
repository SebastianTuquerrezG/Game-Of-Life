package co.com.unicauca.gameoflife.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Universe.java
 * Clase que representa el universo de Conway
 * @autor Joan Sebastian Tuquerrez Gomez
 * @version 1.0
 * @copy Universidad del Cauca - Ingenieria de Sistemas
 */
public class Universe extends JPanel {
    private Map<String, Cell> space = new HashMap<String, Cell>();
    private ArrayList<Integer> rulesToLive = new ArrayList<Integer>();
    private ArrayList<Integer> rulesToBorn = new ArrayList<Integer>();
    private int generation;
    private int population;

    private int scale;
    private final int width;
    private final int height;

    public int x;
    public int y;
    public int offsetX;
    public int offsetY;
    public int button;

    public static final int MAX_SCALE = 16;

    /**
     * Constructor de la clase Universe
     * @param width Ancho del universo
     * @param height Alto del universo
     */
    public Universe(int width, int height) {
        this.width = width;
        this.height = height;
        resetUniverse();
        setRulesToLive(23);
        setRulesToBorn(3);

        final Universe u = this;
        this.addMouseListener(new MouseAdapter() {
            /**
             * Metodo que se ejecuta cuando se presiona un boton del mouse, si es presionado se calcula si se aumento o se decrementa
             * la poblacion de celulas
             * @param e Evento de tipo MouseEvent
             */
            @Override
            public void mousePressed(MouseEvent e) {
                u.button = e.getButton();
                u.x = e.getX();
                u.y = e.getY();
                if (u.button == MouseEvent.BUTTON1) {
                    int x = e.getX()/scale - offsetX;
                    int y = e.getY()/scale - offsetY;
                    Cell cell = space.get(String.format("%d/%d", x, y));
                    if (cell == null) {
                        (new Cell(space, x, y)).alive();
                        ++population;
                    } else {
                        cell.destructor();
                        --population;
                    }
                }
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            /**
             * Metodo que se ejecuta cuando se mueve el mouse, si se mueve el mouse se calcula la posicion en X y Y
             * @param e Evento de tipo MouseEvent
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                if (u.button == MouseEvent.BUTTON3) {
                    offsetX -= u.x - e.getX();
                    u.x = e.getX();

                    offsetY -= u.y - e.getY();
                    u.y = e.getY();
                }
            }
        });
    }

    public void loadMapFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] coordinates = line.split("/");

                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                int adjustedX = x / scale - offsetX;
                int adjustedY = y / scale - offsetY;

                Cell cell = space.get(String.format("%d/%d", adjustedX, adjustedY));
                if (cell == null) {
                    (new Cell(space, adjustedX, adjustedY)).alive();
                    ++population;
                }
            }
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que reinicia el universo
     */
    public void resetUniverse() {
        setPreferredSize(new Dimension(width, height));
        offsetX = offsetY = generation = population = 0;
        scale = 1;
        space.clear();
    }

    /**
     * Metodo que calcula la siguiente generacion del universo
     */
    public void nextGeneration() {
        int num;
        Collection<Cell> cc = space.values();
        Cell[] cells = cc.toArray(new Cell[cc.size()]);
        int length = cells.length;
        for (int i = 0; i < length; ++i) {
            num = neighboringCells(cells[i].getX(), cells[i].getY());
            checkNeighbors(cells[i].getX(), cells[i].getY());

            if (!rulesToLive.contains(num)) {
                cells[i].willDie();
                --population;
            }
        }

        cc = space.values();
        cells = cc.toArray(new Cell[cc.size()]);
        length = cells.length;
        for (int i = 0; i < length; ++i) {
            cells[i].nextState();
        }

        ++generation;
    }

    /**
     * Metodo que determina si una celula puede nacer o no
     * @param cellX Posicion en X de la celula
     * @param cellY Posicion en Y de la celula
     */
    public void checkNeighbors(int cellX, int cellY) {
        int xi, xf, yi, yf;

        xi = cellX - 1;
        xf = cellX + 1;

        yi = cellY - 1;
        yf = cellY + 1;

        int num = 0;
        Cell neighbour;
        for (int x = xi; x <= xf; ++x) {
            for (int y = yi; y <= yf; ++y) {
                if (x != cellX || y != cellY) {
                    neighbour = space.get(String.format("%d/%d", x, y));
                    if (neighbour == null) {
                        num = neighboringCells(x, y);

                        if (rulesToBorn.contains(num)) {
                            new Cell(space, x, y);
                            ++population;
                        }
                    }
                }
            }
        }
    }

    /**
     * Metodo que calcula el numero de vecinos vivos de una celula
     * @param cellX Posicion en X de la celula
     * @param cellY Posicion en Y de la celula
     * @return
     */
    public int neighboringCells(int cellX, int cellY) {
        int xi, xf, yi, yf;

        xi = cellX - 1;
        xf = cellX + 1;

        yi = cellY - 1;
        yf = cellY + 1;

        int num = 0;
        Cell neighbour;
        for (int x = xi; x <= xf; ++x) {
            for (int y = yi; y <= yf; ++y) {
                if (x != cellX || y != cellY) {
                    neighbour = space.get(String.format("%d/%d", x, y));
                    if (neighbour != null && neighbour.getState() != Cell.WILL_BORN) {
                        ++num;
                    }
                }
            }
        }

        return num;
    }

    /**
     * Metodo que pinta el universo
     * @param g Objeto de tipo Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.scale(scale, scale);
        setBackground(Color.black);

        Collection<Cell> cc = new ArrayList<>(space.values());

        for (Cell cell : cc) {
            cell.paint(g2D, offsetX, offsetY);
        }
        //for (Map.Entry<String, Cell> mapEntry : space.entrySet()) {
        //    mapEntry.getValue().paint(g2D, offsetX, offsetY);
        //}
    }

    /**
     * Metodo que establece las reglas para que una celula viva
     * @param num numero de vecinos vivos para que una celula viva
     */
    public void setRulesToLive(int num) {
        rulesToLive.clear();
        if (num == 0) {
            rulesToLive.add(0);
        }

        int tmp = num;
        while (tmp != 0) {
            rulesToLive.add(tmp%10);
            tmp /= 10;
        }
    }

    /**
     * Metodo que establece las reglas para que una celula nazca
     * @param num numero de vecinos vivos para que una celula nazca
     */
    public void setRulesToBorn(int num) {
        rulesToBorn.clear();
        if (num == 0) {
            rulesToBorn.add(0);
        }

        int tmp = num;
        while (tmp != 0) {
            rulesToBorn.add(tmp%10);
            tmp /= 10;
        }
    }

    /**
     * Metodo que establece la escala del universo
     * @param scale Escala del universo
     */
    public void setScale(int scale) {
        int tmp = this.scale;

        if (scale > MAX_SCALE) {
            this.scale = MAX_SCALE;
        } else if (scale < 1) {
            this.scale = 1;
        } else {
            this.scale = scale;
        }

        if (tmp < this.scale) {
            offsetX -= width/(2*scale);
            offsetY -= height/(2*scale);
        } else if (tmp > this.scale) {
            offsetX += width/(2*tmp);
            offsetY += height/(2*tmp);
        }
    }

    public int getScale() {
        return scale;
    }

    public int getGeneration() {
        return generation;
    }

    public int getPopulation() {
        return population;
    }
}
