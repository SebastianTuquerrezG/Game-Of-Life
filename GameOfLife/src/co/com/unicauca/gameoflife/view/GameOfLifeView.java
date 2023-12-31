package co.com.unicauca.gameoflife.view;

import co.com.unicauca.gameoflife.model.Grid;
import co.com.unicauca.gameoflife.model.Universe;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    public final JSlider speedSlider;
    public final JLabel rulesToLive , rulesToBorn;
    public boolean isPlay, isStop, isIncreaseZoom, isDecreaseZoom;
    public int speed = 30;

    /**
     * Constructor de la clase GameOfLifeView
     */
    public GameOfLifeView() {
        super("Juego de la Vida");

        isPlay = isStop = isIncreaseZoom = isDecreaseZoom = false;
        setLayout(new FlowLayout());

        panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(640, 730));
        add(panel);

        universe = new Universe(640, 730);
        grid = new Grid(universe, 640, 730);
        panel.add(grid);
        grid.setBounds(0, 0, 640, 730);
        panel.add(universe);
        universe.setBounds(0, 0, 640, 730);

        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 850));
        add(sidebar);

        JPanel options = new JPanel(new GridLayout(12, 1));
        sidebar.add(options, BorderLayout.CENTER);

        JPanel generation = new JPanel(new FlowLayout());
        options.add(generation);

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

        JLabel speedLabel = new JLabel("Velocidad: ");
        velocidad.add(speedLabel);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        velocidad.add(speedSlider);

        speedSlider.addChangeListener(new ChangeHandler());

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

        JButton mapMenuButton = new JButton("Mapa");
        options.add(mapMenuButton);

        JPopupMenu mapMenu = new JPopupMenu();
        createMapMenu(mapMenu, mapMenuButton);

        JButton saveMapButton = new JButton("Guardar");
        options.add(saveMapButton);

        saveMapButton.addActionListener(e -> {
            universe.saveMap();
            mapMenu.removeAll();
            createMapMenu(mapMenu, mapMenuButton);
        });

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
     * Metodo que carga un mapa desde un archivo
     * @param filePath Ruta del archivo
     */
    public void loadMapFromFile(String filePath) {
        universe.loadMapFromFile(filePath);
    }

    /**
     * Metodo que crea el menu de mapas
     * @param mapMenu Menu de mapas
     * @param mapMenuButton Boton del menu de mapas
     */
    public void createMapMenu(JPopupMenu mapMenu, JButton mapMenuButton) {
        File folder = new File("src/co/com/unicauca/gameoflife/test");
        File[] files = folder.listFiles();

        if (files != null) {
            int count = 1;
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("mapa") && file.getName().endsWith(".txt")) {
                    String filePath = file.getAbsolutePath();
                    MapMenuItemListener mapMenuItemListener = new MapMenuItemListener(filePath, this);
                    JMenuItem mapMenuItem = new JMenuItem("Mapa " + count);
                    mapMenuItem.addActionListener(mapMenuItemListener);
                    mapMenu.add(mapMenuItem);
                    count++;
                }
            }
        }

        mapMenuButton.addActionListener(e -> mapMenu.show(mapMenuButton, 0, mapMenuButton.getHeight()));
    }

    /**
     * Clase que representa el manejador de eventos del menu de mapas
     */
    public class MapMenuItemListener implements ActionListener {
        private String filePath;
        private GameOfLifeView gameOfLifeView;

        /**
         * Constructor de la clase MapMenuItemListener
         * @param filePath Ruta del archivo
         * @param gameOfLifeView Vista del juego de la vida
         */
        public MapMenuItemListener(String filePath, GameOfLifeView gameOfLifeView) {
            this.filePath = filePath;
            this.gameOfLifeView = gameOfLifeView;
        }

        /**
         * Metodo que se ejecuta cuando se presiona un item del menu de mapas, carga el mapa desde el archivo
         * @param e Evento de tipo ActionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            gameOfLifeView.universe.resetUniverse();
            gameOfLifeView.loadMapFromFile(filePath);
        }
    }

    /**
     * Clase que representa el manejador de eventos del slider de velocidad
     */
    private class ChangeHandler implements ChangeListener {
        /**
         * Metodo que se ejecuta cuando se cambia el valor del slider de velocidad
         * @param e Evento de tipo ChangeEvent
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int originalValue = source.getValue();
                int invertedValue = 100 - originalValue;
                speed = invertedValue;
            }
        }
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
