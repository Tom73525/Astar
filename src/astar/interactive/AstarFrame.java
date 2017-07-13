package astar.interactive;

import astar.pcg.AbstractLevelGenerator;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class AstarFrame extends JFrame {

    WorldPanel worldPanel;

    public static void main(String[] arguments) {
        AstarFrame mainFrame = new AstarFrame();

        mainFrame.setVisible(true);

    }

    protected SingleStepAstar astar;
    protected JTextField stepSizeField;
    protected JCheckBox runEndCheckBox;
    protected int seed = 0;
    protected int level = 10;
    protected char[][] tileMap;
    private AbstractLevelGenerator levelGenerator;
    private boolean debug = false;

    public AstarFrame() {
        init();
    }

    protected final void init() {
        initConfig();
        
        initWorld();
        
        initFrame();
    }

    /**
     * Initializes the configuration.
     */
    protected void initConfig() {
        String debugs = System.getProperty("astar.debug");
        if(debugs != null && debugs.equals("true"))
            debug = true;
            
        String seeds = System.getProperty("astar.seed");
        if (seeds != null) {
            seed = Integer.parseInt(seeds);
        }

        String levels = System.getProperty("astar.level");
        if (levels != null) {
            level = Integer.parseInt(levels);
        }

        String className = System.getProperty("astar.lg");
        
        if (className == null)
            className = "astar.pcg.WellsLevelGenerator";

        try {
            Class<?> cl = Class.forName(className);

            Constructor<?> cons = cl.getConstructor(Integer.class);

            this.levelGenerator = (AbstractLevelGenerator) cons.newInstance(seed);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {

        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(AstarFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void initWorld() {
        tileMap = levelGenerator.generateLevel(level); 
        
        System.out.println(tileMap.length + " x " + tileMap[0].length);
        
        if(debug)
            levelGenerator.dump();
        
        astar = new SingleStepAstar(tileMap);
    }
    
    protected void initFrame() {
        setTitle("Interactive A*");
        
        setDefaultLookAndFeelDecorated(true);

        setSize(new Dimension(500, 500));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();

        // Configure the world panel where
        worldPanel = new WorldPanel(tileMap);

        worldPanel.setPreferredSize(new Dimension(800, 800));

        JScrollPane scroller = new JScrollPane(worldPanel);

        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getVerticalScrollBar().setUnitIncrement(10);
        scroller.getHorizontalScrollBar().setUnitIncrement(10);

        contentPane.add(BorderLayout.CENTER, scroller);

        // Configure the control panel
        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(new BorderLayout());

        controlPanel.add(BorderLayout.EAST, new QuitButton());

        // Create the step control subpanel
        JPanel stepPanel = new JPanel(new BorderLayout());

        // Add the checkbox that allows automated steps
        runEndCheckBox = new JCheckBox("Run to end");

        runEndCheckBox.setSelected(false);

        stepPanel.add(BorderLayout.CENTER, runEndCheckBox);

        // Add manual single-step button
        stepPanel.add(BorderLayout.WEST, new StepButton(this));

        controlPanel.add(BorderLayout.WEST, stepPanel);

        contentPane.add(BorderLayout.SOUTH, controlPanel);

        contentPane.setPreferredSize(new Dimension(500, 500));

        setLocationRelativeTo(null);

//      setVisible(true);        
    }
}
