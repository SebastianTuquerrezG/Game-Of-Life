package co.com.unicauca.gameoflife.view;

import co.com.unicauca.gameoflife.model.Grid;
import co.com.unicauca.gameoflife.model.Universe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * GameOfLifeView.java
 * Clase que representa la vista del juego de la vida.
 * @autor Joan Sebastian Tuquerrez Gomez
 * @version 1.0
 * @copy Universidad del Cauca - Ingenieria de Sistemas
 */
public class GameOfLifeView extends JFrame {
    public final JPanel panel, sidebar;
    public final Universe universe;
    public final Grid grid;
    public final JButton stop, play, next, increaseZoom, decreaseZoom, showGrid;
    public final JTextField generationText, populationText, xText, yText;
    public final JLabel rulesToLive , rulesToBorn,  speedText;
    public boolean isPlay, isStop, isIncreaseZoom, isDecreaseZoom;
    public int speed;

    /**
     * Constructor de la clase GameOfLifeView
     */
    public GameOfLifeView() {
        super("Juego de la Vida");

        isPlay = isStop = isIncreaseZoom = isDecreaseZoom = false;
        setLayout(new FlowLayout());

        panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(640, 640));
        add(panel);
        speed = 30;

        universe = new Universe(637, 650);
        grid = new Grid(universe, 637, 640);
        panel.add(grid);
        grid.setBounds(0, 0, 637, 637);
        panel.add(universe);
        universe.setBounds(0, 0, 637, 637);

        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 640));
        add(sidebar);

        JPanel options = new JPanel(new GridLayout(10, 1));
        sidebar.add(options, BorderLayout.CENTER);

        JPanel generation = new JPanel(new FlowLayout());
        options.add(generation);
        speedText = new JLabel("30");

        JLabel generationLabel = new JLabel("Generaci\u00f3n:");
        generation.add(generationLabel);

        generationText = new JTextField(10);
        generationLabel.setLabelFor(generationText);
        generationText.setEditable(false);
        generation.add(generationText);

        JPanel population = new JPanel(new FlowLayout());
        options.add(population);

        JLabel populationLabel = new JLabel("Poblaci\u00f3n:");
        population.add(populationLabel);

        populationText = new JTextField(10);
        populationLabel.setLabelFor(populationText);
        populationText.setEditable(false);
        population.add(populationText);

        ActionEventHandler handler = new ActionEventHandler();

        JPanel rules = new JPanel(new FlowLayout());
        options.add(rules);

        JLabel rulesLabel = new JLabel("Reglas");
        rules.add(rulesLabel);

        JPanel rulesText = new JPanel(new FlowLayout());
        rules.add(rulesText);

        rulesToLive = new JLabel("Vivir: 2 o 3");
        rulesText.add(rulesToLive);

        rulesToBorn = new JLabel("Nacer: 3");
        rulesText.add(rulesToBorn);

        JPanel velocidad = new JPanel(new FlowLayout());
        options.add(velocidad);

        JLabel speed = new JLabel("Velocidad: 30");
        velocidad.add(speed);

        stop = new JButton("Detener");
        stop.addActionListener(handler);
        options.add(stop);

        play = new JButton("Reproducir");
        play.addActionListener(handler);
        options.add(play);

        next = new JButton("Siguiente");
        next.addActionListener(handler);
        options.add(next);

        increaseZoom = new JButton("Zoom (+)");
        increaseZoom.addActionListener(handler);
        options.add(increaseZoom);

        decreaseZoom = new JButton("Zoom (-)");
        decreaseZoom.addActionListener(handler);
        options.add(decreaseZoom);

        showGrid = new JButton("Cuadr\u00edcula");
        showGrid.addActionListener(handler);
        options.add(showGrid);

        JPanel coordinates = new JPanel(new GridLayout(1, 1));
        sidebar.add(coordinates, BorderLayout.PAGE_END);

        xText = new JTextField(5);
        coordinates.add(xText);
        xText.setEditable(false);
        xText.setText("X: ");

        yText = new JTextField(5);
        coordinates.add(yText);
        yText.setEditable(false);
        yText.setText("Y: ");

        universe.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                xText.setText(String.format("X: %d", e.getX()/universe.getScale() - universe.offsetX));
                yText.setText(String.format("Y: %d", e.getY()/universe.getScale() - universe.offsetY));
            } // - mouseMoved
        });
    }

    /**
     * Metodo que inicia el hilo de ejecucion
     */
    public void start() {
        (new Thread(new PaintHandler())).start();
    }

    /**
     * Clase que representa el hilo de ejecucion
     */
    private class PaintHandler implements Runnable {
        /**
         * Metodo que verifica si el juego esta en modo play o stop, pausa el hilo de ejecuion
         * y actualiza la interfaz grafica
         */
        @Override
        public void run() {
            while (true) {
                try {
                    if (isPlay) {
                        Thread.sleep(speed);
                    } else {
                        Thread.sleep(33);
                        if (isStop) {
                            universe.resetUniverse();
                            isStop = false;
                        }
                    }
                    generationText.setText(String.format("%s", universe.getGeneration()));
                    populationText.setText(String.format("%s", universe.getPopulation()));
                    if (isIncreaseZoom) {
                        universe.setScale(universe.getScale()*2);
                        isIncreaseZoom = false;
                    } else if (isDecreaseZoom) {
                        universe.setScale(universe.getScale()/2);
                        isDecreaseZoom = false;
                    }
                    if (!isIncreaseZoom && !isDecreaseZoom && isPlay) {
                        universe.nextGeneration();
                    }
                    panel.updateUI();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Clase que representa el manejador de eventos
     */
    private class ActionEventHandler implements ActionListener {
        /**
         * Metodo que se ejecuta cuando se presiona un boton, si se presiona un boton se calcula la siguiente generacion,
         * se detiene el juego, se pausa el juego, se aumenta el zoom, se disminuye el zoom o se muestra la cuadricula
         * @param e Evento de tipo ActionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == rulesToLive) {
                universe.setRulesToLive(Integer.parseInt(rulesToLive.getText()));
                universe.setRulesToBorn(Integer.parseInt(rulesToBorn.getText()));
            } else if (e.getSource() == rulesToBorn) {
                universe.setRulesToLive(Integer.parseInt(rulesToLive.getText()));
                universe.setRulesToBorn(Integer.parseInt(rulesToBorn.getText()));
            } else if (e.getSource() == speedText) {
                speed = Integer.parseInt(speedText.getText());
            } else if (e.getSource() == next) {
                if (!isPlay) {
                    universe.nextGeneration();
                }
            } else if (e.getSource() == stop) {
                isPlay = false;
                play.setText("Reproducir");
                isStop = true;
            } else if (e.getSource() == play) {
                if (isPlay) {
                    isPlay = false;
                    play.setText("Reproducir");
                } else {
                    isPlay = true;
                    play.setText("Pausar");
                }
            } else if (e.getSource() == increaseZoom) {
                isIncreaseZoom = true;
            } else if (e.getSource() == decreaseZoom) {
                isDecreaseZoom = true;
            } else if (e.getSource() == showGrid) {
                grid.toggleGrid();
            }
        }
    }
}
