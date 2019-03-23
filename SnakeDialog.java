import java.awt.Color;
import java.util.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernhard
 */
class SnakeDialog extends JDialog implements ActionListener {
    
    Snake snake;
    CrystalDrawing cd;
    
    JTextField lengthField, widthField, driftField, nervosityField, lapseField, colorvarField, seedField;
    JCheckBox blackbackgroundBox;
    
    JButton startButton, stopButton, clearButton;
        
    
    
    public static Integer readint(String s){
        Integer res=null;
         try {
              res=new Integer(Integer.parseInt(s));
            } catch (NumberFormatException ex){
                res=null;
            }
        return res;
    }
    
    public static float readfloat(String s){
        float res=0;
        try {
              res=Float.parseFloat(s);
            } catch (NumberFormatException ex){
                System.out.println("Number format exception:"+ex.getMessage());
            }
        return res;
    }
    
    private void copyresults(){
        snake.length=readfloat(lengthField.getText());
        snake.width=readfloat(widthField.getText());
        snake.drift=readfloat(driftField.getText());
        snake.nervosity=readfloat(nervosityField.getText());
        snake.lapse=readint(lapseField.getText());
        snake.colorvariability=readint(colorvarField.getText());
        snake.seed=(long) readint(seedField.getText());
        snake.blackbackground=blackbackgroundBox.isSelected();
    }
    
    public void close(){
        setVisible(false);
    }
    
    public void setSnake(Snake argsnake){
        snake=argsnake;
        initialize();
    }

    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        //System.out.println(s);
        if ("Démarrer".equals(s)) {
            copyresults();
            //System.out.println("Snake width:"+snake.width);
            cd.startSnake();
        }
	else if  ("Stopper".equals(s)){
            cd.stopSnake();
        }
        else if ("Effacer".equals(s)){
            copyresults();
            snake.clear((MPoint) cd.getCenter());
            cd.repaint();
        }
        else if ("blackbackgroundBox".equals(s)){
            System.out.println("blackbackgroundBox command received");
            snake.blackbackground=blackbackgroundBox.isSelected();
            if (snake.blackbackground){
                cd.mp.setBackground(Color.black);
            }
            else {
                cd.mp.setBackground(Color.white);
            }
            cd.repaint();
        }
    }
    
    private void initialize(){
        lengthField.setText(""+snake.length);
        widthField.setText(""+snake.width);
        driftField.setText(""+snake.drift);
        nervosityField.setText(""+snake.nervosity);
        lapseField.setText(""+snake.lapse);
        colorvarField.setText(""+snake.colorvariability);
        seedField.setText(""+snake.seed);
        blackbackgroundBox.setSelected(snake.blackbackground);
    }
    

    public SnakeDialog(Frame fr, CrystalDrawing argcd, Snake argsnake){

	super(fr, "Propriétés du serpent", false);
        cd=argcd;
        snake=argsnake;
        

        startButton=new JButton("Démarrer");
        startButton.addActionListener(this);
        stopButton=new JButton("Stopper");
        stopButton.addActionListener(this);
        clearButton = new JButton("Effacer");
        clearButton.addActionListener(this);
        
        getRootPane().setDefaultButton(startButton);
        
	lengthField = new JTextField();
        widthField=new JTextField();
        driftField=new JTextField();
        nervosityField=new JTextField();
        lapseField=new JTextField();
        colorvarField=new JTextField();
        seedField=new JTextField();
        blackbackgroundBox=new JCheckBox("Arrière plan noir");
        blackbackgroundBox.setActionCommand("blackbackgroundBox");
        blackbackgroundBox.addActionListener(this);
        
        initialize();
       
        //JLabel contourLabel=new JLabel("Contour");
        JLabel lengthLabel=new JLabel("Longueur");
        JLabel widthLabel=new JLabel("Largeur");
        JLabel driftLabel=new JLabel("Tendance");
        JLabel nervosityLabel=new JLabel("Nervosité");
        JLabel lapseLabel=new JLabel("Lapse en millisec.");
        JLabel colorvarLabel=new JLabel("Var. couleur");
        JLabel seedLabel=new JLabel("Graine couleur");
        
        //JLabel fillLabel=new JLabel("Fond");
        
        JPanel panel=new JPanel(new GridLayout(0,2));
        panel.add(lengthLabel);
        panel.add(lengthField);
        panel.add(widthLabel);
        panel.add(widthField);
        panel.add(nervosityLabel);
        panel.add(nervosityField);
        panel.add(driftLabel);
        panel.add(driftField);
        panel.add(lapseLabel);
        panel.add(lapseField);
        panel.add(colorvarLabel);
        panel.add(colorvarField);
        panel.add(seedLabel);
        panel.add(seedField);
        panel.add(blackbackgroundBox);
        
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        Container cont=getContentPane();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.add(panel);
        
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        //buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(startButton);
        buttonPane.add(stopButton);
        buttonPane.add(clearButton);
        cont.add(buttonPane);
        //System.out.println("Component count:"+cont.getComponentCount());

        //Initialize values.
        pack();
        setLocationRelativeTo(cd);
	setVisible(true);
    }
}
