package astar.interactive;

import astar.aes.LGenerator;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
 
public class Main {
 
  public static void main(String[] arguments) {

      // create a basic JFrame
      JFrame.setDefaultLookAndFeelDecorated(true);
      JFrame frame = new JFrame("JFrame Color Example");

      frame.setSize(new Dimension(500, 500));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Container contentPane = frame.getContentPane();

      LGenerator lg = new LGenerator(0);

      char[][] map = lg.generateLevel(10);

      System.out.println(map.length + " x " + map[0].length);

      lg.dump();
      
      WorldPanel worldPanel = new WorldPanel(map);

      worldPanel.setPreferredSize(new Dimension(800, 800));

      JScrollPane scroller = new JScrollPane(worldPanel);

      scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scroller.getVerticalScrollBar().setUnitIncrement(10);
      scroller.getHorizontalScrollBar().setUnitIncrement(10);

//    contentPane.add(BorderLayout.CENTER, worldPanel);
      contentPane.add(BorderLayout.CENTER, scroller);

      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BorderLayout());
      buttonPanel.add(BorderLayout.EAST, new JButton("Button1"));
      buttonPanel.add(BorderLayout.WEST, new JButton("Button2"));

      contentPane.add(BorderLayout.SOUTH, buttonPanel);
      contentPane.setPreferredSize(new Dimension(500, 500));

//    mainPanel.add(myPanel);
      frame.setLocationRelativeTo(null);

      frame.setVisible(true);

  }
}
 
// create a panel that you can draw on.
class MyPanel extends JPanel implements MouseListener {
  public void paint(Graphics g) {
    g.setColor(Color.red);
    g.fillRect(10,10,100,100);
  }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("hello");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}