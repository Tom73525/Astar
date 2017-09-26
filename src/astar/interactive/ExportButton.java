/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar.interactive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
/*
 *
 * @author Tom
 */
public class ExportButton extends JButton implements ActionListener{
    
    
    
    String model;
    String heuristic;
    int tries;
    int nodes;
    int distance;
    WorldPanel worldPanel;
    StepButton stepButton;
    File path;
    
    public ExportButton() {
    
    super("Export");
    this.addActionListener(this);
    path = new File("."); 
    
    }
    
    public ExportButton(WorldPanel panel,StepButton button)
    {
        super("Export");
        this.addActionListener(this);
        path = new File("."); 
        worldPanel = panel;
        stepButton = button;
    }
    
    /*
        Takes a screensot of the WorldPanel context and saves it to an image file.
    */
    void takeSnapShot(WorldPanel panel, File path ){
    
        this.path = path;
        BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
        panel.paint(bufImage.createGraphics());
        File imageFile = new File(path.getPath()+"/Astar.jpeg");
        try{
            imageFile.createNewFile();
            ImageIO.write(bufImage, "jpeg", imageFile);
        }catch(Exception ex){
        
            ex.printStackTrace();
            
        }
    }
    
    /*
        Gets the final metric values of the path and saves it to a CSV file.
    */
    void getSummary(StepButton button, File path)
    {
        this.path=path;
        model = button.astar.getModel().getClass().getSimpleName();
        heuristic = button.astar.getEstimator().getClass().getSimpleName();
        tries = button.tries1;
        nodes = button.nodes;
        distance = button.dist;
        
        
        File f = new File(path.getPath()+"/Astar.csv");
       
        try{

            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            String output="Model,Heuristic,Tries,Nodes,Distance\n"+model+","
                    +heuristic+","+tries+","+nodes+","+distance+"\n";
            byte[] b = output.getBytes();
            fo.write(b);
            fo.close();
        }
        catch(Exception ef){

            ef.printStackTrace();;
        }
       
            
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
    
        JFrame frame = new JFrame("Choose Folder");
     
        ExportPanel panel = new ExportPanel();
        
        frame.addWindowListener(
        
            new WindowAdapter() {
     
                public void windowClosing(WindowEvent e) {
         
                System.exit(0);
           
                }
            }
        );
     
        frame.getContentPane().add(panel,"Center");
        frame.setSize(panel.getPreferredSize());
        frame.setVisible(true);
       
       
        
    }
    /*
        Implementation for File Chooser
    */
    class ExportPanel extends JPanel implements ActionListener{

        JButton directoryButton; 
        JFileChooser chooser;
        
        public ExportPanel()
        {
            directoryButton = new JButton("Choose Directory");
            directoryButton.addActionListener(this);
            add(directoryButton);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
                 path=chooser.getCurrentDirectory();
                 System.out.println(chooser.getCurrentDirectory());
                 takeSnapShot(worldPanel,path);
                 getSummary(stepButton,path);
            }
             
        }
        
        @Override
        public Dimension getPreferredSize(){
        
            return new Dimension(200, 200);
    }
        
    }


}


 
    
