/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.interactive;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *
 * @author roncoleman
 */
public class WorldPanel extends JPanel implements MouseListener, MouseMotionListener {
    public final int SIZE = 10;
    public final int INSET = 3;
    private final int rowCount;
    private final int colCount;
    
    // Base x,y give the upper left corner of the world
    private int baseX = 0;
    private int baseY = 0;
    
    // Last base x,y is the last base x,y after releasing the mouse; this
    // allows relative movements.
    private int lastBaseX;
    private int lastBaseY;
    private int touchX;
    private int touchY;
    
    public WorldPanel(int rowCount, int colCount) {
        super();
        
        this.rowCount = rowCount;
        this.colCount = colCount;
        
        init();
    }
    
    private void init() {
        addMouseListener(this);
        addMouseMotionListener(this);
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        g.setColor(Color.BLACK);
        
        for(int row=0; row < rowCount; row++) {
            for(int col=0; col < colCount; col++) {
                if(row == rowCount-1 && col == colCount-1)
                    g.setColor(Color.RED);
                
                else if(row != 0 || col != 0)
                    g.setColor(Color.WHITE);
                    
                int x = col * (SIZE + INSET) + INSET + baseX;
                int y = row * (SIZE + INSET) + INSET + baseY;
                
                g.fillRect(x, y, SIZE, SIZE);
            }
            
        }
    }   

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("clicked x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point point = e.getPoint();

        touchX = point.x;
        touchY = point.y;
        
        repaint();        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastBaseX = baseX;
        lastBaseY = baseY;
        
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("entered x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("exited x = "+x+" y = "+y);
        
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();

        int dragToX = point.x;
        int dragToY = point.y;
        
        // Update the base x,y depending on how much we dragged the world
        baseX = lastBaseX + dragToX - touchX;
        baseY = lastBaseY + dragToY - touchY;
        
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}
