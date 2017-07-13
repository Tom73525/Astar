package astar.interactive;

import astar.Astar;
import astar.pcg.WellsLevelGenerator;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
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
    protected Astar astar;
    protected JTextField stepSizeField;
    protected JCheckBox runEndCheckBox;
  
  public AstarFrame() {
      init();
  }
  
  protected void init() {
      // create a basic JFrame
      setDefaultLookAndFeelDecorated(true);
//      JFrame frame = new JFrame("JFrame Color Example");

      setSize(new Dimension(500, 500));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Container contentPane = getContentPane();

      WellsLevelGenerator lg = new WellsLevelGenerator(0);

      char[][] map = lg.generateLevel(10);

      System.out.println(map.length + " x " + map[0].length);

      lg.dump();
      
      astar = new Astar(map);
      
      // Configure the world panel where
      worldPanel = new WorldPanel(map);

      worldPanel.setPreferredSize(new Dimension(800, 800));

      JScrollPane scroller = new JScrollPane(worldPanel);

      scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scroller.getVerticalScrollBar().setUnitIncrement(10);
      scroller.getHorizontalScrollBar().setUnitIncrement(10);

      contentPane.add(BorderLayout.CENTER, scroller);

      // Configure the button panel
      JPanel buttonPanel = new JPanel();
      
      buttonPanel.setLayout(new BorderLayout());
      
      buttonPanel.add(BorderLayout.EAST, new QuitButton());
      
      JPanel stepPanel = new JPanel(new BorderLayout());
      
      runEndCheckBox = new JCheckBox("To end");
      
      runEndCheckBox.setSelected(false);
  
      stepPanel.add(BorderLayout.CENTER,runEndCheckBox);
      
      stepPanel.add(BorderLayout.WEST, new StepButton(this));
      
      buttonPanel.add(BorderLayout.WEST,stepPanel);

      contentPane.add(BorderLayout.SOUTH, buttonPanel);
      
      contentPane.setPreferredSize(new Dimension(500, 500));

      setLocationRelativeTo(null);

//      setVisible(true);

  }
}