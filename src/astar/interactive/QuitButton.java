/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.interactive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author roncoleman
 */
public class QuitButton extends JButton implements ActionListener {

    public QuitButton() {
        super("Quit");
        
        init();

    }
    
    protected void init() {
        this.addActionListener(this);        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
    
}
