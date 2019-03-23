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
import java.awt.Color;
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
class AttributeDialog extends JDialog implements ActionListener {
        
    JButton closeButton, applyButton, cancelButton, contourColorButton, fillColorButton;
    JTextField widthField ;
    JCheckBox contourPresentBox, fillPresentBox, dashedBox;
    
    Attribute attr;
    Stack<Drawable> selection;
    Color contourcolor, fillcolor;
    
    CrystalDrawing cd;
    boolean contourpresent, fillpresent, isdashed;
    float contourwidth;
    
    public static float readfloat(Component comp, String s, int lower, int upper, float defaultvalue){
        float res;
        try {
              res=Float.parseFloat(s);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(comp, "Enter a number between "+lower+" and "+upper+".");
                res=defaultvalue;
            }
        if ((res<lower)|| (res>upper)){
            JOptionPane.showMessageDialog(comp, "Enter a number between "+lower+" and "+upper+".");
            res=defaultvalue;
        }
        return res;
    }
    
    private void copyresults(){
        if (attr!=null){
            attr.contourwidth=readfloat(this, widthField.getText(), 1, 1000, attr.contourwidth);
            attr.contourpresent = contourPresentBox.isSelected();
            attr.fillpresent=fillPresentBox.isSelected();
            attr.contourcolor=contourcolor;
            attr.isdashed=dashedBox.isSelected();
            attr.fillcolor=fillcolor;
            System.out.println("attr link:"+attr);
            System.out.println("attr:\n"+attr.toString());
        }
        
        if (selection!=null){
            if (selection.size()>0){
                Attribute a=new Attribute();
                float w=selection.peek().getAttribute().contourwidth;
                a.contourwidth=readfloat(this, widthField.getText(), 1, 1000, w);
                a.contourpresent = contourPresentBox.isSelected();
                a.fillpresent=fillPresentBox.isSelected();
                a.isdashed=dashedBox.isSelected();
                a.contourcolor=contourcolor;
                a.fillcolor=fillcolor;
                Drawable d;
                for (int i=0; i<selection.size(); i++){
                    d=selection.elementAt(i);
                    System.out.println("Selection element at "+i+":"+d);
                    d.setAttribute((Attribute) a.clone());
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        //System.out.println(s);
        if ("Annuler".equals(s)) {
            setVisible(false);
        }
	else if  ("Appliquer".equals(s)){
            copyresults();
            cd.repaint();
        }
        else if ("Fermer".equals(s)){
            copyresults();
	    setVisible(false);
            }
        else if ("widthField".equals(s)){
            System.out.println("Modified width");
            copyresults();
            cd.repaint();
        }
        else if ("Contour color".equals(s)){
            System.out.println("Pressed contour color");
            Color color=JColorChooser.showDialog(this, "Couleur du contour", contourcolor);
            if (color!=null){contourcolor=color;}
            copyresults();
            cd.repaint();
        }
        else if ("Fill color".equals(s)){
            System.out.println("Pressed fill color");
            Color color=JColorChooser.showDialog(this,"Couleur du fonds", fillcolor);
            if (color!=null){fillcolor=color;}
            copyresults();
            cd.repaint();
        }
        else if ("contourPresentBox".equals(s)){
            copyresults();
            cd.repaint();
        }
        else if ("fillPresentBox".equals(s)){
            copyresults();
            cd.repaint();
        }
        else if ("dashedBox".equals(s)){
            copyresults();
            cd.repaint();
        }
    }
    
    public void setAttr(Attribute a){
        attr=a;
        initialize(a);
    }
    
    public void setSelection(Stack argselection){
        selection=argselection;
        System.out.println("selection:"+selection);
        if (selection!=null){
            System.out.println("selection.size()="+selection.size());
            if (selection.size()>0){
                Attribute a=selection.peek().getAttribute();
                System.out.println("Selection attribute:\n"+a);
                initialize(a);
            }
            else {
                Attribute a=new Attribute();
                initialize(a);
            }
        }
    }
    
    private void initialize(Attribute a){
        contourcolor=a.contourcolor;
        fillcolor=a.fillcolor;
        contourpresent=a.contourpresent;
        fillpresent=a.fillpresent;
        isdashed=a.isdashed;
        contourwidth=a.contourwidth;
        widthField.setText(""+contourwidth);
        contourPresentBox.setSelected(contourpresent);
        fillPresentBox.setSelected(fillpresent);
    }
    

    public AttributeDialog(Frame fr, String title, CrystalDrawing argcd, Attribute attrarg, Stack argselection){

	super(fr, title, false);
        cd=argcd;
        attr=attrarg;
        selection=argselection;
        

        closeButton=new JButton("Fermer");
        closeButton.addActionListener(this);
        applyButton=new JButton("Appliquer");
        applyButton.addActionListener(this);
        cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(this);
        
        getRootPane().setDefaultButton(applyButton);
        
	widthField = new JTextField();
        widthField.setText(""+contourwidth);
        widthField.setActionCommand("widthField");
        widthField.addActionListener(this);
       
        //JLabel contourLabel=new JLabel("Contour");
        JLabel widthLabel=new JLabel("Largeur");
        widthLabel.setLabelFor(widthField);
        //JLabel fillLabel=new JLabel("Fond");
        
        JButton contourColorButton=new JButton("Couleur ...");
        contourColorButton.setActionCommand("Contour color");
        contourColorButton.addActionListener(this);
        
        JButton fillColorButton=new JButton("Couleur ...");
        fillColorButton.setActionCommand("Fill color");
        fillColorButton.addActionListener(this);
        
	contourPresentBox=new JCheckBox("Contour");
        contourPresentBox.setSelected(contourpresent);
        contourPresentBox.setActionCommand("contourPresentBox");
        contourPresentBox.addActionListener(this);
        //System.out.println("contourpresent: "+contourpresent);
        //System.out.println("contourPresentBox.isEnabled:"+contourPresentBox.isSelected());
        
        fillPresentBox=new JCheckBox("Fond");
        fillPresentBox.setSelected(fillpresent);
        fillPresentBox.setActionCommand("fillPresentBox");
        fillPresentBox.addActionListener(this);
        
        dashedBox=new JCheckBox("Traitillé");
        dashedBox.setSelected(isdashed);
        dashedBox.setActionCommand("dashedBox");
        dashedBox.addActionListener(this);
        
        if (attr!=null){
            initialize(attr);
        }
        if (selection!=null){
            if (selection.size()>0){
                Attribute a=selection.peek().getAttribute();
                initialize(a);
            }
        }
        
	//JPanel panel1=new JPanel(new GridLayout(1,1));
        JPanel panel2=new JPanel(new GridLayout(1,3));
	panel2.add(contourPresentBox);
        panel2.add(widthLabel);
        panel2.add(widthField);
        panel2.add(contourColorButton);
        
        JPanel panel3=new JPanel(new GridLayout(1,1));
        panel3.add(dashedBox);
        
        JPanel panel4=new JPanel(new GridLayout(1,2));
        panel4.add(fillPresentBox);
        panel4.add(fillColorButton);
        //textPanel.add(ShakeBox);
        
        //panel1.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel3.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel4.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        
        Container cont=getContentPane();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        //cont.add(panel1);
        //System.out.println("Panel 1:"+cont.getComponentCount());
        cont.add(panel2);
        //System.out.println("Panel 2:"+cont.getComponentCount());
        cont.add(panel3);
        //System.out.println("Panel 3:"+cont.getComponentCount());
        cont.add(panel4);
        //System.out.println("Panel 4:"+cont.getComponentCount());
        
        
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        //buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(cancelButton);
        buttonPane.add(applyButton);
        buttonPane.add(closeButton);
        cont.add(buttonPane);
        //System.out.println("Component count:"+cont.getComponentCount());

        //Initialize values.
        pack();
        setLocationRelativeTo(cd);
	setVisible(true);
    }
}
