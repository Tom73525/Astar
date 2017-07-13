/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.interactive;

import astar.util.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 *
 * @author roncoleman
 */
public final class StepButton extends JButton implements ActionListener {

    private final WorldPanel worldPanel;
    private final SingleStepAstar astar;
    private final JCheckBox runEndCheckBox;
    private int tries = 0;
    private final AstarFrame frame;
    
    public StepButton(AstarFrame frame) {
        super("Step");
        this.frame = frame;
        this.worldPanel = frame.worldPanel;
        this.astar = frame.astar;
        this.runEndCheckBox = frame.runEndCheckBox;
        
        init();
    }
    
    protected void init() {
        this.addActionListener(this);  
        astar.begin();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {  
        new Thread(new Runnable() {
            @Override
            public void run() {                                      
                do {
                    Node head = astar.find1();
                    
                    Node dest = astar.getDest();

                    tries++;
                    
                    LinkedList<Node> open = astar.getOpen();
                    LinkedList<Node> closed = astar.getClosed();

                    worldPanel.update(head, astar.getDest(), open, closed);
                    
                    if(!runEndCheckBox.isSelected()) {
                        setEnabled(true);
                        return;
                    }
                     
                    if(head == null || head.equals(dest)) {
                        String msg = "Tries: " + tries; 
                        msg += "\nElapsed time: " + 0;
                        
                        JOptionPane.showMessageDialog(frame, msg);
                        
                        setEnabled(false);
                        
                        return;
                    }
                    
                    sleep(300);
                } while (true);
            }
        }).start();
    }
    
    private void sleep(int time) {
        try {
            Thread.sleep(time);
            
        } catch (InterruptedException ex) {
            
        }
    }
    
}

class Stepper implements Runnable {
    
    @Override
    public void run() {
        
    }
    
}
