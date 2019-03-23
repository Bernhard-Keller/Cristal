
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



/**
 *
 * @author bernhard
 */
public class CrystalDrawing extends JPanel implements ActionListener, Printable, WindowListener  {
    
   
   public static final int WAITING_FOR_SEGMENT=0;
   public static final int WAITING_FOR_GLASS1=1;
   public static final int WAITING_FOR_GLASS2=2;
   public static final int WAITING_FOR_SELECTION=3;
   public static final int WAITING_FOR_PARTIAL_SELECTION=4;
   public static final int WAITING_FOR_POLYGON1=5;
   public static final int WAITING_FOR_POLYGON2=6;
   public static final int WAITING_FOR_BEZIER1=7;
   public static final int WAITING_FOR_BEZIER2=8;
   public static final int WAITING_FOR_BEZIER3=9;
   public static final int WAITING_FOR_BEZIER4=10;
   public static final int WAITING_FOR_BEZIER5=11;
   public static final int WAITING_FOR_CIRCLE=12;
   public static final int WAITING_FOR_TRANSLATOR=13;
   public static final int WAITING_FOR_PROPAGATOR=14;
   public static final int WAITING_FOR_SNAKE=15;
   public static final int WAITING_FOR_TEXT=16;
   public static final int WAITING_FOR_HOMOTHETY1=17;
   public static final int WAITING_FOR_HOMOTHETY2=18;
   public static final int WAITING_FOR_ROTATION1=19;
   public static final int WAITING_FOR_ROTATION2=20;
   public static final int WAITING_FOR_SYMMETRY1=21;
   public static final int WAITING_FOR_SYMMETRY2=22;
   public static final int WAITING_FOR_GLIDE1=23;
   public static final int WAITING_FOR_GLIDE2=24;
   public static final int WAITING_FOR_TEST1=27;
   public static final int WAITING_FOR_TEST2=31;
   public static final int WAITING_FOR_TEST3=32;
   public static final int WAITING_FOR_TEST4=33;
   public static final int WAITING_FOR_INSERTION=28;
   public static final int WAITING_FOR_DELETION=29;
   public static final int WAITING_FOR_CONTINUE=30;
   public static final int WAITING_FOR_VORONOI1=34;
   public static final int WAITING_FOR_VORONOI2=35;
   public static final int WAITING_FOR_DELAUNAY1=36;
   public static final int WAITING_FOR_DELAUNAY2=37;
   public static final int WAITING_FOR_MERGE=38;
   
   public static final int FUNDDOM=-1;
   public static final int SEGMENT=0;
   public static final int POLYGON=1;
   public static final int BEZIERPATH=2;
   public static final int CIRCLE=3;
   public static final int BLOB=4;
   public static final int SNAKE=5;
   public static final int TEXT=6;
   
   

   Frame fr;
   static Group[] groups;
   Motive M, copiedMotive;
   MotiveSet ms;
   MotivePanel mp;
   
   JMenu motiveMenu;
   JLabel statusLabel;
   JMenuItem previewMenuItem, hideGroupMenuItem, multipleMotivesItem, cutMotiveItem, pasteMotiveItem, newMotiveItem;
  
   static float gridsize=(float) 1/(float) 12;
   
   Drawable currentDrawable;
   Stack<Drawable> extractedDrawables;
   Blob currentBlob;
   
   
   Attribute copiedAttribute;
   AttributeDialog selectionAttrDialog, toolAttrDialog;
   FontDialog fontDialog;
   
   
   JFileChooser fileChooser;
   String fileName;
   
   PrinterJob printJob=null;
   
   javax.swing.Timer lapsetimer;
   int timelapse;
   SnakeDialog snakedialog;
   
   boolean multiplemodeon;
   MotivePanel[] motivePanels;
   int nbrows, nbcols;
   
   boolean changesMade;
   
   public CrystalDrawing(Dimension dim) {
   
   setPreferredSize(dim);
    
   groups=new Group[17];
   
   initializegroups();
   initializesymcharts();
   
   extractedDrawables=new Stack();
   
   copiedMotive=null;
   M=new Motive(new Rectangle(dim), groups[0].subgroups[0],1);
   ms=new MotiveSet();
   setLayout(new GridLayout(1,1));
   ms.add(M);
   mp=new MotivePanel(M,this);
   mp.setBackground(Color.white);
   
   add(mp);
   validate();
   
   
   fileChooser=new JFileChooser();
   fileChooser.addChoosableFileFilter(new CrystalFilter());
   fileName = "";
   
   changesMade=false;
   
   //addComponentListener(this);
   //addMouseMotionListener(this);
   //addMouseListener(this);
}
   
public void windowClosing(WindowEvent e) {
        //This will only be seen on standard output.
        System.out.println("WindowListener method called: windowClosing.");
        quitApplication();
    }
    public void windowClosed(WindowEvent e) {
        //This will only be seen on standard output.
        System.out.println("WindowListener method called: windowClosed.");
        
    }

    public void windowOpened(WindowEvent e) {
        //System.out.println("WindowListener method called: windowOpened.");
    }

    
    public void windowIconified(WindowEvent e) {
        //System.out.println("WindowListener method called: windowIconified.");
    }

    public void windowDeiconified(WindowEvent e) {
        //System.out.println("WindowListener method called: windowDeiconified.");
    }

    public void windowActivated(WindowEvent e) {
        //System.out.println("WindowListener method called: windowActivated.");
    }

    public void windowDeactivated(WindowEvent e) {
        //System.out.println("WindowListener method called: windowDeactivated.");
    }

    public void windowGainedFocus(WindowEvent e) {
        //System.out.println("WindowFocusListener method called: windowGainedFocus.");
    }

    public void windowLostFocus(WindowEvent e) {
        //System.out.println("WindowFocusListener method called: windowLostFocus.");
    }

    public void windowStateChanged(WindowEvent e) {
        //System.out.println("WindowStateListener method called: windowStateChanged.");
    }
    
    
   
public void setCutMotiveItem(JMenuItem item){
    cutMotiveItem=item;
}

public void setPasteMotiveItem(JMenuItem item){
    pasteMotiveItem=item;
}

public void setNewMotiveItem(JMenuItem item){
    newMotiveItem=item;
}

   public void changeCurrentMotivePanel(MotivePanel motpanel){
       if (mp==motpanel){
           return;
       }
       mp=motpanel;
   }
   
   public void changeCurrentMotive(Motive mot){
       if (M==mot){
           updateFrameTitle();
           return;
       }
       if (M.isEmpty()){
            ms.removeElement(M);
        }
       M=mot;
       mp.M=mot;
       mp.voronoiCircle=null;
       mp.voronoiPolygon=null;
       updatestatus(M.status);
       updateMenus();
       updateFrameTitle();
       currentDrawable=M.getCurrentDrawable();
       currentBlob=M.getCurrentBlob();
   }
   
   
   public void settimeLapse(int l){
        timelapse=l;
        if (lapsetimer!=null){
            lapsetimer.setDelay(timelapse);
        }
    }
   
   public void setPreviewMenuItem(JMenuItem item){
       previewMenuItem=item;
   }
   
   public void setHideGroupItem(JMenuItem item){
       hideGroupMenuItem=item;
   }
   
   public void setMultipleMotivesItem(JMenuItem item){
       multipleMotivesItem=item;
   }
   
   
   public void initializextou(){
       M.initializextou(-2,2,-2,2, getBounds());
   }
   
   public MPoint getCenter(){
       Rectangle r=getBounds();
       float x=r.width/2;
       float y=r.height/2;
       MPoint P=new MPoint(x,y);
       M.xtou.transform(P, P);
       return P;
   }
   
   private void Open(){
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileName=file.getName();
            //System.out.println("Opening: " + file.getName() + ".");
            FileReader in=null;
            try{
                in = new FileReader(file);
                }
            catch (FileNotFoundException e) {
                System.out.println("File not found: "+e.getMessage()); 
            }

            if (in!=null){
               if (multiplemodeon){
                   leaveMultipleMode();
               }
               BufferedReader r= new BufferedReader(in);
               String str;
                try{
                    multiplemodeon=Utils.readboolean(r);
                    nbrows=Utils.readint(r);
                    nbcols=Utils.readint(r);
                    ms.clear();
                    ms.read(r);
                    r.close();
                }
                catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
                }
               changeCurrentMotive(ms.getCurrentMotive());
               if (multiplemodeon){
                   enterMultipleMode(nbrows,nbcols);
               }
            }
	}
        else {
            System.out.println("Open command cancelled by user.");
        }
    }
   
   private void AddMotives(){
            JFileChooser addfileChooser=new JFileChooser();
            addfileChooser.addChoosableFileFilter(new CrystalFilter());
            int returnVal = addfileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = addfileChooser.getSelectedFile();
            String addfileName=file.getName();
            //System.out.println("Opening: " + file.getName() + ".");
            FileReader in=null;
            try{
                in = new FileReader(file);
                }
            catch (FileNotFoundException e) {
                System.out.println("File not found: "+e.getMessage()); 
            }
            if (in!=null){
               BufferedReader r= new BufferedReader(in);
               String str;
               MotiveSet newms=new MotiveSet();
                try{
                    Utils.readboolean(r);
                    Utils.readint(r);
                    Utils.readint(r);
                    newms.read(r);
                    r.close();
                }
                catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
                }
                for (int i=0;i<newms.size();i++){
                    Motive mot=newms.motives.elementAt(i);
                    mot.title=addfileName+" "+mot.title;
                    ms.add(mot);
                }
                updateMenus();
            }
	}
        else {
            System.out.println("Add command cancelled by user.");
        }
    }

   
   public void updatestatus(int st){
       M.status=st;
       if ((st!=WAITING_FOR_SELECTION)&&(st!=WAITING_FOR_PROPAGATOR)&&(st!=WAITING_FOR_TRANSLATOR)
               &&(st!=WAITING_FOR_GLASS1)&&(st!=WAITING_FOR_GLASS2)&&(st!=WAITING_FOR_SNAKE)
               &&(st!=WAITING_FOR_HOMOTHETY1)&&(st!=WAITING_FOR_HOMOTHETY2)
               &&(st!=WAITING_FOR_ROTATION1)&&(st!=WAITING_FOR_ROTATION2)
               &&(st!=WAITING_FOR_SYMMETRY1)&&(st!=WAITING_FOR_SYMMETRY2)
               &&(st!=WAITING_FOR_GLIDE1)&&(st!=WAITING_FOR_GLIDE2)
               &&(st!=WAITING_FOR_INSERTION)&&(st!=WAITING_FOR_DELETION)
               &&(st!=WAITING_FOR_CONTINUE)&&(st!=WAITING_FOR_TEST2)
               &&(st!=WAITING_FOR_TEST3)&&(st!=WAITING_FOR_TEST4)
               &&(st!=WAITING_FOR_VORONOI1)&&(st!=WAITING_FOR_VORONOI2)
               &&(st!=WAITING_FOR_VORONOI1)&&(st!=WAITING_FOR_VORONOI2)
               &&(st!=WAITING_FOR_MERGE)){
           //System.out.println("1 Current drawable: "+M.currentDrawable);
           M.currentDrawable=M.getCurrentDrawable();
           //System.out.println("2 Current drawable: "+M.currentDrawable);
           M.clearSelection();
           M.clearLimboDrawables();
       }
       if ((st!=WAITING_FOR_PARTIAL_SELECTION)&&(st!=WAITING_FOR_TRANSLATOR)
               &&(st!=WAITING_FOR_GLASS1)&&(st!=WAITING_FOR_GLASS2)&&(st!=WAITING_FOR_SNAKE)
               &&(st!=WAITING_FOR_INSERTION)&&(st!=WAITING_FOR_TEST1)
               &&(st!=WAITING_FOR_MERGE)){
           M.unpartiallySelectDrawables();
       }
       if ((st!=WAITING_FOR_PROPAGATOR)&&(st!=WAITING_FOR_TRANSLATOR)
               &&(st!=WAITING_FOR_GLASS1)&&(st!=WAITING_FOR_GLASS2)&&(st!=WAITING_FOR_SNAKE)){
           M.removeBlob();
       }
       switch (st){
           case WAITING_FOR_SNAKE:
               statusLabel.setText("Serpent : cliquez pour revenir au mode dessin");
               break;
           case WAITING_FOR_PROPAGATOR:
               statusLabel.setText("Propagateur : cliquez les disques ou les marques, alt-clique pour copier"); break;
           case WAITING_FOR_CIRCLE:
               statusLabel.setText("Cercle : appuyez et faites glisser"); break;
            case WAITING_FOR_TRANSLATOR:
                statusLabel.setText("Translater : appuyez et faites glisser"); break;
            case WAITING_FOR_SEGMENT:
                statusLabel.setText("Segment : faites glisser la souris"); break;
            case WAITING_FOR_POLYGON1:
                statusLabel.setText("Nouveau polygone : faites glisser la souris"); break;
            case WAITING_FOR_POLYGON2:
                    statusLabel.setText("Polygone : cliquez et faites glisser la souris"); break;
            case WAITING_FOR_GLASS1:
                statusLabel.setText("Loupe, 1er point : faites glisser la souris"); break;
            case WAITING_FOR_GLASS2:
                statusLabel.setText("Loupe 2e point : faites glisser la souris"); break;
            case WAITING_FOR_SELECTION:
                statusLabel.setText("Sélecteur : cliquez dans sur les objets et faites glisser la souris, shift-clique pour étendre la sélection"); break;
            case WAITING_FOR_PARTIAL_SELECTION:
                statusLabel.setText("Sélecteur partiel : cliquez dans les marques et faites glisser la souris"); 
                break;
            case WAITING_FOR_BEZIER1:
                statusLabel.setText("C. de Bézier 1 : faites glisser la souris pour la première tangente");
                break;
            case WAITING_FOR_BEZIER2:
                statusLabel.setText("C. de Bézier 2 : faites glisser la souris pour la première tangente");
                break;
            case WAITING_FOR_BEZIER3:
                statusLabel.setText("C. de Bézier 3 : faites glisser la souris pour le prochain point");
                break;
            case WAITING_FOR_BEZIER4:
                statusLabel.setText("C. de Bézier 4 : faites glisser la souris pour la prochaine tangente");
                break;
            case WAITING_FOR_BEZIER5:
                statusLabel.setText("C. de Bézier 5 : faites glisser la souris pour la prochaine tangente");
                break;
            case WAITING_FOR_TEXT:
                statusLabel.setText("Texte : cliquez");
                break;
            case WAITING_FOR_HOMOTHETY1:
                statusLabel.setText("Homothétie : cliquez sur le centre");
                break;
            case WAITING_FOR_HOMOTHETY2:
                statusLabel.setText("Homothétie : faites glisser la souris, alt pour copier");
                break;
            case WAITING_FOR_ROTATION1:
                statusLabel.setText("Rotation : cliquez sur le centre, alt-clique pour dialogue");
                break;
            case WAITING_FOR_ROTATION2:
                statusLabel.setText("Rotation : faite glisser la souris, alt pour copier");
                break;
            case WAITING_FOR_SYMMETRY1:
                statusLabel.setText("Symétrie : faites glisser pour l'axe");
                break;
            case WAITING_FOR_SYMMETRY2:
                statusLabel.setText("Symétrie : faites glisser pour l'axe, alt pour copier");
                    break;
            case WAITING_FOR_GLIDE1:
                statusLabel.setText("Glissement : faites glisser pour l'axe");
                break;
            case WAITING_FOR_GLIDE2:
                statusLabel.setText("Glissement : faites glisser pour l'axe, alt pour copier");
                break;
            case WAITING_FOR_TEST1:
                statusLabel.setText("Test");
                break;
            case WAITING_FOR_INSERTION:
                statusLabel.setText("Insérer point : cliquez sur le tracé");
                break;
            case WAITING_FOR_DELETION:
                statusLabel.setText("Enlever point : cliquez sur le point");
                break;
            case WAITING_FOR_CONTINUE:
                statusLabel.setText("Continuer : cliquez sur la courbe à continuer:");
                break;
            case WAITING_FOR_VORONOI1:
                statusLabel.setText("Domaine de Voronoï : faites glisser pour dessiner un cercle");
                break;
            case WAITING_FOR_VORONOI2:
                statusLabel.setText("Domaine de Voronoi : faites glisser pour faire varier le dessin");
                break;
            case WAITING_FOR_DELAUNAY1:
                statusLabel.setText("Triangulation de Delaunay : faites glisser pour dessiner un cercle");
                break;
            case WAITING_FOR_DELAUNAY2:
                statusLabel.setText("Triangulation de Delaunay : faites glisser pour faire varier le dessin");
                break;
            case WAITING_FOR_MERGE:
                statusLabel.setText("Confondre points : faites glisser pour entourer les points d'un rectangle");
        }
       repaint();
   }
   
   public void setstatusLabel(JLabel label){
       statusLabel=label;
   }
   
  
   

  
    public Subgroup getSubgroupFromName(String sgrname){
    Subgroup returnSubgroup=null;
    for (int i=0;i<17;i++){
        for (int j=0; j<groups[i].subgroups.length;j++){
            if (groups[i].subgroups[j].name.equals(sgrname)){
                returnSubgroup=groups[i].subgroups[j];
            }
        }
    }
    return returnSubgroup;
}

    /**
     *
     * @param tit
     * @return
     */
    public Motive getMotiveFromTitle(String tit){
    Motive returnMotive=null;
    for (int i=0;i<ms.motives.size();i++){
        if (ms.motives.elementAt(i).title.equals(tit)){
            returnMotive=ms.motives.elementAt(i);
        }
    }
    return returnMotive;
}
  
    /**
     *
     * @param fra
     */
    public void setFrame(Frame fra){
        fr=fra;
   }
   
    /**
     *
     * @param mm
     */
    public void setMotiveMenu(JMenu mm){
       motiveMenu=mm;
   }
    
    public void updateDialogs(){
        setBackground(Color.white);
        if (toolAttrDialog!=null){
            toolAttrDialog.setAttr(M.currentattr);
        }
        if (selectionAttrDialog!=null){
            //System.out.println("updateDialogs: calling setSelection");
            selectionAttrDialog.setSelection(M.selectedDrawables);
        }
        if (snakedialog!=null){
            if (M.status!=WAITING_FOR_SNAKE){
                snakedialog.close();
                if (lapsetimer!=null){
                    lapsetimer.stop();
                }
            }
            else {
                if (lapsetimer!=null){
                    lapsetimer.setDelay(M.snake.lapse);
                }
                snakedialog.setSnake(M.snake);
                snakedialog.setVisible(true);
                if (M.snake.blackbackground){
                    setBackground(Color.black);
                }
            }
        }
    }
    
    public void updateMenus(){
        updateMotiveMenu();
        if (multiplemodeon){
            newMotiveItem.setEnabled(false);
            cutMotiveItem.setEnabled(false);
            pasteMotiveItem.setEnabled(false);
            multipleMotivesItem.setText("Simple");
        }
        else {
            newMotiveItem.setEnabled(true);
            cutMotiveItem.setEnabled(true);
            pasteMotiveItem.setEnabled(true);
            System.out.println("Motive items enabled");
            multipleMotivesItem.setText("Multiples ...");
        }
        if (M.previewmode){
            previewMenuItem.setText("Tracés");
        } 
        else {
            previewMenuItem.setText("Prévisualisation");
        }
        if (M.hidegroup){
            hideGroupMenuItem.setText("Montrer le groupe");
        } 
        else {
            hideGroupMenuItem.setText("Cacher le groupe");
        }
    }
   
    
    public void updateMotiveMenu(){
       //System.out.println("Before removal: "+MenuToString(motiveMenu));
       motiveMenu.removeAll();
       //System.out.println("After removal: "+MenuToString(motiveMenu));
       
        JMenu menu=motiveMenu;
        
        JMenuItem menuItem=new JMenuItem("Nouveau motif");
        newMotiveItem=menuItem;
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Titre ...");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem=new JMenuItem(multipleMotivesItem.getText());
        setMultipleMotivesItem(menuItem);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Copier motif");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Coller motif");
        pasteMotiveItem=menuItem;
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Couper motif");
        cutMotiveItem=menuItem;
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
       
       for (int i=0; i<ms.motives.size();i++){
           menuItem=new JMenuItem(ms.motives.elementAt(i).title);
           menuItem.addActionListener(this);
           motiveMenu.add(menuItem);
       }
       
       if (multiplemodeon){
            newMotiveItem.setEnabled(false);
            cutMotiveItem.setEnabled(false);
            pasteMotiveItem.setEnabled(false);
            multipleMotivesItem.setText("Simple");
        }
        else {
            newMotiveItem.setEnabled(true);
            cutMotiveItem.setEnabled(true);
            pasteMotiveItem.setEnabled(true);
            System.out.println("Motive items enabled");
            multipleMotivesItem.setText("Multiples ...");
        }
   }
   
    public void updateFrameTitle(){
       //System.out.println("updateFrameTitle : M.title="+M.title);
       String subgroupname;
       if (M.hidegroup){
           subgroupname="";
       } else {
           subgroupname=" : "+M.subgroup.name;
       }
       if (M.hidegroup){
           fr.setTitle("");
       }
       else if (fileName.equals("")){
           fr.setTitle(M.title+subgroupname);}
       else {
           fr.setTitle(fileName+" : "+M.title+subgroupname);
       }
   }
   
   public AffineTransform homotethy(float scale, MPoint fixpoint){
       float px=(float) fixpoint.getX();
       float py=(float) fixpoint.getY();
       float tx=px-scale*px;
       float ty=py-scale*py;
       return new AffineTransform(scale,0,0,scale,tx,ty);
   } 
   
   public void startSnake(){
        System.out.println("startSnake: lapse: "+M.snake.lapse);
        lapsetimer = new javax.swing.Timer(M.snake.lapse, this);
        lapsetimer.start();
        if (M.snake.blackbackground){
            mp.setBackground(Color.black);
        }
        else {
            mp.setBackground(Color.white);
        }
        setPreviewMode(true);
   }
   
   public void stopSnake(){
       if (lapsetimer!=null){
        lapsetimer.stop();
       }
   }
   
   public void setPreviewMode(boolean b){
       if (b){
           M.previewmode=true;
           previewMenuItem.setText("Tracés");
            repaint();
        }
       else {
           M.previewmode=false;
           previewMenuItem.setText("Prévisualisation");
       }
   }
   
   public void leaveMultipleMode(){
        multiplemodeon=false;
        updateMenus();
        this.removeAll();
        mp.M=M;
        mp.setBackground(Color.white);
        mp.setBorder(null);
        setLayout(new GridLayout(1,1));
        add(mp);
        validate();
        mp.updateMotiveRectangle();
        repaint();
   }
   
   public void enterMultipleMode(int rows, int cols){
        this.removeAll();
        setLayout(new GridLayout(rows,cols));
        motivePanels=new MotivePanel[Math.min(ms.size(),rows*cols)];
        for (int i=0;i<ms.size();i++){
            if (i<rows*cols){
                motivePanels[i]=new MotivePanel(ms.motives.elementAt(i),this);
                motivePanels[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,10,10,10),
                                                                          BorderFactory.createLineBorder(Color.black)));
                motivePanels[i].setBackground(Color.white);
                add(motivePanels[i]);
            }
        }
       validate();
       for (int i=0; i<motivePanels.length;i++){
           motivePanels[i].updateMotiveRectangle();
       }
       multiplemodeon=true;
       updateMenus();
       repaint();
   }
   
   public void quitApplication(){
       System.out.println("Changes made: "+changesMade);
       if (changesMade){
            JOptionPane d = new JOptionPane(); // les textes figurant // sur les boutons 
            String buttonTexts[]={ "Enregistrer", "Ne pas enregistrer", "Annuler"}; 
            // indice du bouton qui a été // cliqué ou CLOSED_OPTION 
            int ans = d.showOptionDialog(fr, "Enregistrer les modifications ?", "Avant de quitter", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, // les textes de boutons 
                    buttonTexts, // le bouton par défaut 
                    buttonTexts[0]); 
            if (ans!=JOptionPane.CLOSED_OPTION){
                if (ans==0){
                    Save();
                    System.exit(1);
                }
                if (ans==1){
                    System.exit(1);
                }
                
            }
        }
        else {
            System.exit(1);
        }
   }
   

   
   public void actionPerformed(ActionEvent event) {
   
    String actionCommand = event.getActionCommand(); 
    
    //System.out.println("actionCommand="+actionCommand);
    
    if (actionCommand==null){
        //System.out.println("actionCommand null received");
        if (event.getSource()==lapsetimer){
            M.snake.grow();
            //System.out.println("lapsetimer event received");
            repaint();
        }
        return;
    }
    
    if (actionCommand.equals("Serpent ...")){
        if (M.snake==null){
            M.snake=new Snake(M.subgroup, getCenter());
        }
        M.snake.clear(getCenter());
        M.add(M.snake);
        if (snakedialog==null){
            snakedialog=new SnakeDialog(fr,this,M.snake);
        } 
        else {
            snakedialog.setSnake(M.snake);
            snakedialog.setVisible(true);
        }
        if (M.snake.blackbackground){
            mp.setBackground(Color.black);
        }
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_SNAKE);
    }
    
    
    if (actionCommand.equals("Imprimer ...")){
        Print();
    }
    
    if (actionCommand.equals("Enregistrer")){
        Save();
    }
    
    if (actionCommand.equals("Enregistrer sous ...")){
        SaveAs();
    }
    
    if (actionCommand.equals("Ouvrir ...")){
        Open();
        System.out.println("actionPerformed:");
        for (int i=0; i<M.drawables.size(); i++){
           System.out.println("Drawable "+i+": \n"+M.drawables.elementAt(i));
        }
        repaint();
    }
    
    if (actionCommand.equals("Rajouter ...")){
        AddMotives();
        System.out.println("actionPerformed:");
        for (int i=0; i<M.drawables.size(); i++){
           System.out.println("Drawable "+i+": \n"+M.drawables.elementAt(i));
        }
        repaint();
    }
    
    if (actionCommand.equals("Propagateur")){
        System.out.println("Propagateur choisi");
        System.out.println("M.selectionmodeon: "+M.selectionmodeon);
        
        M.setSelectionMode(true);
        System.out.println("Currentdrawable: "+currentDrawable);
        if (!M.selectionNotEmpty()){
            M.initializeSelection(currentDrawable);
            System.out.println("Selection initialized");
        }
            
        MPoint P1=M.getSelectionBarycenter();
        System.out.println("Barycenter: "+P1);
        if (P1!=null){
            Blob Bl=new Blob(P1,M.getSubgroup());
            M.drawables.add(Bl);
            currentBlob=Bl;
            System.out.println("Blob added:"+Bl+", P="+P1);
            updatestatus(WAITING_FOR_PROPAGATOR);
            repaint();
        }
    }
    
    if (actionCommand.equals("Multiples ...")){
        int N=ms.motives.size();
        int n=(int) Math.sqrt(N);
        int p=N/n;
        if (n*p<N){
            p=p+1;
        }
        String s=(String)JOptionPane.showInputDialog(this, "Nombre de motifs: "+ ms.motives.size()+".\n"+
                "Rentrez le nombre de lignes n et \nle nombre de colonnes"
                + " p sous la forme : n p", ""+n+" "+p);
        if ((s!=null)&&(!s.equals(""))){ 
            String[] fields=s.split(" ");
            nbrows=Integer.parseInt(fields[0]);
            nbcols=Integer.parseInt(fields[1]);
            enterMultipleMode(nbrows,nbcols);
        }
    }
    
    if (actionCommand.equals("Simple")){
        leaveMultipleMode();
    }
    
    if (actionCommand.equals("Cercle")){
        updatestatus(WAITING_FOR_CIRCLE);
    }
    
    if (actionCommand.equals("Translater")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_TRANSLATOR);
    }
    
    if (actionCommand.equals("Copier attr.")){
        if (currentDrawable!=null){
            copiedAttribute=(Attribute) currentDrawable.attr.clone();
        }
        else {
            copiedAttribute=new Attribute();
        }
    }
    
    if (actionCommand.equals("Coller attr.")){
        if (currentDrawable!=null){
            currentDrawable.attr=(Attribute) copiedAttribute.clone();
        }
        if (M.selectionNotEmpty()){
            M.setSelectionAttribute(copiedAttribute);
        }
        repaint();
    }
    
    if (actionCommand.equals("Premier/arrière plan")){
        M.bringBackgroundForward();
        M.moveSelectionToBackground();
        repaint();
    }
    
    if (actionCommand.equals("Eloigner")){
        if (currentDrawable!=null){
            int i=M.drawables.indexOf(currentDrawable);
            if ((i>0) && (M.drawables.size()>1)){
                Drawable temp=M.drawables.elementAt(i-1);
                M.drawables.set(i-1, currentDrawable);
                M.drawables.set(i, temp);
            }
            repaint();
        }
    }    
    
    if (actionCommand.equals("Premier plan")){
        if (currentDrawable!=null){
            int i=M.drawables.indexOf(currentDrawable);
            int s=M.drawables.size();
            if ((i<s-1) && (s>1)){
                Drawable temp=M.drawables.elementAt(s-1);
                M.drawables.set(s-1, currentDrawable);
                M.drawables.set(i,temp);
            }
            repaint();
        }
    } 
    
    if (actionCommand.equals("Arrière plan")){
        if (currentDrawable!=null){
            int i=M.drawables.indexOf(currentDrawable);
            int s=M.drawables.size();
            if ((i>0) && (s>1)){
                Drawable temp=M.drawables.elementAt(0);
                M.drawables.set(0, currentDrawable);
                M.drawables.set(i, temp);
            }
            repaint();
        }
    }
    
    if (actionCommand.equals("Rapprocher")){
        if (currentDrawable!=null){
            int i=M.drawables.indexOf(currentDrawable);
            int s=M.drawables.size();
            if ((i<s-1) && (s>1)){
                Drawable temp=M.drawables.elementAt(i+1);
                M.drawables.set(i+1,currentDrawable);
                M.drawables.set(i,temp);
            }
            repaint();
        }
    }
    
    if (actionCommand.equals("Cacher le groupe")){
        M.hidegroup=true;
        hideGroupMenuItem.setText("Montrer le groupe");
        updateFrameTitle();
    }
    
    if (actionCommand.equals("Montrer le groupe")){
        M.hidegroup=false;
        hideGroupMenuItem.setText("Cacher le groupe");
        updateFrameTitle();
    }
    
    if (actionCommand.equals("Sélectionner tout")){
        M.oldstatus=M.status;
        M.setSelectionMode(true);
        M.selectAll();
        if (M.status!=WAITING_FOR_PROPAGATOR){
            updatestatus(WAITING_FOR_SELECTION);
        }
        repaint();
    }
    
    if (actionCommand.equals("Polygone")){
        updatestatus(WAITING_FOR_POLYGON1);
    }
    
    if (actionCommand.equals("C. de Bézier")){
        updatestatus(WAITING_FOR_BEZIER1);
    }
    
    if (actionCommand.equals("Attr. outil ...")){
       //System.out.println("Current attribute link:"+M.currentattr);
       //System.out.println("Current attribute: \n"+M.currentattr.toString());
       toolAttrDialog=new AttributeDialog(fr, "Attribut outil", this, M.currentattr, null);
   }
   
   if (actionCommand.equals("Attr. sél. ...")){
      if (currentDrawable!=null){
        selectionAttrDialog=new AttributeDialog(fr, "Attribut sélection", this, currentDrawable.attr, M.selectedDrawables);
      }
   }
   
   if (actionCommand.equals("Attr. texte ...")){
       //System.out.println("Before dialog: Font name: "+M.textfont.getFontName());
       //System.out.println("Before dialog: Font size: "+M.textfont.getSize());
       fontDialog=new FontDialog(fr, M.textfont);
       M.textfont=fontDialog.getFont();
       //System.out.println("Font name: "+M.textfont.getFontName());
       //System.out.println("Font size: "+M.textfont.getSize());
   }
   
   if (actionCommand.equals("Texte ...")){
       M.textstring=(String)JOptionPane.showInputDialog(fr, "Rentrez le texte souhaité");
       updatestatus(WAITING_FOR_TEXT);
   }
    
    if (actionCommand.equals("Prévisualisation")){
        M.previewmode=true;
        previewMenuItem.setText("Tracés");
        repaint();
    }
    
    if (actionCommand.equals("Tracés")){
        M.previewmode=false;
        previewMenuItem.setText("Prévisualisation");
        repaint();
    }
    
    if (actionCommand.equals("Dom. de Voronoï")){
        if (currentDrawable!=null){
            if (currentDrawable.Type==CIRCLE){
                Circle C=(Circle) currentDrawable;
                Polygone P=C.voronoi(M.utox);
                M.add(P);
            }
        }
        if ((currentDrawable==null)||(currentDrawable.Type!=CIRCLE)){
            updatestatus(WAITING_FOR_VORONOI1);
        }
        repaint();
    }
    
    if (actionCommand.equals("Triang. de Delaunay")){
        if (currentDrawable!=null){
            if (currentDrawable.Type==CIRCLE){
                Circle C=(Circle) currentDrawable;
                Segment[] S=C.delaunay(M.utox);
                for (int i=0;i<S.length; i++){
                    M.add(S[i]);
                }
            }
        }
        if ((currentDrawable==null)||(currentDrawable.Type!=CIRCLE)){
            updatestatus(WAITING_FOR_DELAUNAY1);
        }
        repaint();
    }
    
    if (actionCommand.equals("Confondre points")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_MERGE);
        M.setPartialSelectionMode(true);
        repaint();
    }
    
    if (actionCommand.equals("Test")){
        /*
        int s=M.drawables.size();
        Circle C2=(Circle) M.drawables.elementAt(s-1);
        Circle C1=(Circle) M.drawables.elementAt(s-2);
        Polygone P=(Polygone) M.drawables.elementAt(s-3);
        StraightLine L=StraightLine.lineBisector(C1.P1, C2.P1);
        M.drawables.setElementAt(P.intersect(L, C1.P1),s-3);
        repaint();
        */
        updatestatus(WAITING_FOR_TEST1);
        /*
        Circle C=(Circle) M.drawables.peek();
        Polygone P=C.voronoi(M.utox);
        M.add(P);
        
        /*
        Segment S=(Segment) M.drawables.elementAt(M.drawables.size()-2);
        Polygone P=(Polygone) M.drawables.elementAt(M.drawables.size()-3);
        Polygone newPoly=P.intersect(new StraightLine(S), C.P1);
        if (newPoly!=null){
            M.add(newPoly);
        }
        repaint();
*/
    }
    
    if (actionCommand.equals("Test1")){
        /*
        Circle C=null;
        for (int i=M.drawables.size()-1; i>0; i--){
            Drawable D=M.drawables.elementAt(i);
            System.out.println("Drawable "+i+". Type="+D.Type);
            if (D.Type==CIRCLE){
                C=(Circle) D;
                break;
            }
        }
        Stack<MPoint> P=C.transformsInRange(M.utox);
                for (int i=0; i<P.size(); i++){
                    Segment S=new Segment(P.elementAt(i),P.elementAt(i),M.subgroup);
                    S.propagateOne();
                    M.add(S);
                }
                mp.voronoiPolygon=C.initialVoronoiPolygone(P);
                mp.voronoiPointSet=P;
                mp.voronoiCtr=0;
                M.setSelectionMode(true);
                M.selectAll();
        */
        updatestatus(WAITING_FOR_TEST3);
    }
    
    
    if (actionCommand.equals("Test2")){
        Circle C=null;
        for (int i=M.drawables.size()-1; i>0; i--){
            Drawable D=M.drawables.elementAt(i);
            System.out.println("Drawable "+i+". Type="+D.Type);
            if (D.Type==CIRCLE){
                C=(Circle) D;
                break;
            }
        }
        Stack<MPoint> P=C.transformsInRange(M.utox);
                for (int i=0; i<P.size(); i++){
                    Segment S=new Segment(P.elementAt(i),P.elementAt(i),M.subgroup);
                    S.propagateOne();
                    M.add(S);
                }
                mp.voronoiPolygon=C.initialVoronoiPolygone(P);
                mp.voronoiPointSet=P;
                mp.voronoiCtr=0;
                M.setSelectionMode(true);
                M.selectAll();
        updatestatus(WAITING_FOR_TEST4);
    }
    
    
    if (actionCommand.equals("Vers TikZ ...")){
        Rectangle rect=mp.getBounds();
        MPoint P1=new MPoint(0, (float) rect.getHeight());
        MPoint P2=new MPoint((float) rect.getWidth(),0);
        M.xtou.transform(P1,P1);
        M.xtou.transform(P2, P2);
        float width=(float) (P2.getX()-P1.getX());
        float factor=12/width;
        String str="\\begin{tikzpicture}[scale="+factor+"] \n";
        str=str+"\\clip ("+P1.getX()+","+P1.getY()+") rectangle ("+P2.getX()+","+P2.getY()+") ;\n";
        str=str+"\\draw ("+P1.getX()+","+P1.getY()+") rectangle ("+P2.getX()+","+P2.getY()+") ;\n";
        Drawable.initializeWidthFactor(M.xtou);
        Drawable.widthfactor=Drawable.widthfactor*factor;
        str=str+M.tikzorbit(M.utox,rect);
        str=str+"\\end{tikzpicture}";
        TextDisplayDialog.showDialog(fr,fr,"Export vers TikZ", str,5, 40);
    }
    
    
    if (actionCommand.equals("Quitter")){
        quitApplication();
    }
    
    //System.out.println("actionCommand="+actionCommand);
    
    Subgroup sgr=getSubgroupFromName(actionCommand);
    //System.out.println("sgr:"+sgr);
    //System.out.println(ms.toString());
    if (sgr!=null){
        Motive mot=ms.MotiveFromGroup(sgr.group);
        if (mot!=null){
            mot.setSubgroup(sgr);
        }
        if (mot==null){
            mot=new Motive(getBounds(),sgr);
            ms.add(mot);
        }
        System.out.println("mot.subgroup.name="+mot.subgroup.name);
        changeCurrentMotive(mot);
        repaint();
    }
    
    Motive mot=getMotiveFromTitle(actionCommand);
    if (mot!=null){
        changeCurrentMotive(mot);
        repaint();
    }
    
    if (actionCommand.equals("Copier motif")){
        copiedMotive=M.copy();
    }
    
    if (actionCommand.equals("Coller motif")){
        if (copiedMotive!=null){
            ms.add(copiedMotive);
            changeCurrentMotive(copiedMotive);
            repaint();
        }
    }
    
    if (actionCommand.equals("Couper motif")){
        if (ms.size()>1){
            copiedMotive=M;
            ms.removeElement(M);
            changeCurrentMotive(ms.peek());
            repaint();
        }
    }
    
    if (actionCommand.equals("Toutes les répliques")){
        if (currentDrawable!=null){
            currentDrawable.propagateAll();
        }
        M.propagateAllSelection();
        repaint();
    }
    
    if (actionCommand.equals("Sans répliques")){
        if (currentDrawable!=null){
            currentDrawable.propagateOne();
        }
        M.propagateOneSelection();
        repaint();
    }
    
    if (actionCommand.equals("Adopter sous-groupe")){
        if (currentDrawable!=null){
            currentDrawable.setSubgroup(M.subgroup);
        }
        M.setSelectionSubgroup(M.subgroup);
        repaint();
    }
    
    if (actionCommand.equals("Segment")){
        updatestatus(WAITING_FOR_SEGMENT);
    }
    
    if (actionCommand.equals("Loupe")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_GLASS1);
    }
    
    if (actionCommand.equals("Sélecteur")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_SELECTION);
        M.setSelectionMode(true);
        //System.out.println("currentDrawable:"+currentDrawable);
        if (currentDrawable!=null){
            M.initializeSelection(currentDrawable);
        }
        repaint();
    }
    
    if (actionCommand.equals("Insérer point")){
        M.oldstatus=M.status;
        M.setSelectionMode(true);
        //System.out.println("currentDrawable:"+currentDrawable);
        if (currentDrawable!=null){
            M.initializeSelection(currentDrawable);
        }
        updatestatus(WAITING_FOR_INSERTION);
        repaint();
    }
    
    if (actionCommand.equals("Enlever point")){
        M.oldstatus=M.status;
        M.setSelectionMode(true);
        System.out.println("currentDrawable:"+currentDrawable);
        if (currentDrawable!=null){
            M.initializeSelection(currentDrawable);
        }
        updatestatus(WAITING_FOR_DELETION);
        repaint();
    }
    
    if (actionCommand.equals("Continuer courbe")){
        M.oldstatus=M.status;
        M.setSelectionMode(true);
        System.out.println("currentDrawable:"+currentDrawable);
        if (currentDrawable!=null){
            M.initializeSelection(currentDrawable);
        }
        updatestatus(WAITING_FOR_CONTINUE);
        repaint();
    }
    
    if (actionCommand.equals("Sélecteur partiel")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_PARTIAL_SELECTION);
        M.setPartialSelectionMode(true);
        repaint();
    }
    
    if  (actionCommand.equals("Homothétie")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_HOMOTHETY1);
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            } 
            M.initializeLimboDrawables();
        }
    }
    
    if  (actionCommand.equals("Rotation")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_ROTATION1);
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            } 
            M.initializeLimboDrawables();
        }
    }
    
    if  (actionCommand.equals("Symétrie")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_SYMMETRY1);
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            } 
            M.initializeLimboDrawables();
        }
    }
    
    if  (actionCommand.equals("Glissement")){
        M.oldstatus=M.status;
        updatestatus(WAITING_FOR_GLIDE1);
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            } 
            M.initializeLimboDrawables();
        }
    }
    
    
    
    if (actionCommand.equals("Couper")){
        System.out.println("CurrentDrawable:"+currentDrawable);
        System.out.println("selectedDrawables.size():"+M.selectedDrawables.size());
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            }
            M.copiedDrawables.removeAllElements();
            Drawable D;
            for (int i=0;i<M.selectedDrawables.size();i++){
                D=M.selectedDrawables.elementAt(i);
                D.setSelectionMode(false);
                D.setPartialSelectionMode(false);
                M.copiedDrawables.add(D);
            }
            M.selectedDrawables.removeAllElements();
            for (int i=0; i<M.copiedDrawables.size();i++){
                M.drawables.removeElement(M.copiedDrawables.elementAt(i));
            }
            currentDrawable=M.getCurrentDrawable();
            switch (M.status) {
                case WAITING_FOR_POLYGON2:
                    updatestatus(WAITING_FOR_POLYGON1);
                    break;
                case WAITING_FOR_BEZIER2:
                case WAITING_FOR_BEZIER3:
                case WAITING_FOR_BEZIER4:
                case WAITING_FOR_BEZIER5:
                    updatestatus(WAITING_FOR_BEZIER1);
            }
        }
        repaint();
    }
    
    if (actionCommand.equals("Copier")){
        System.out.println("CurrentDrawable:"+currentDrawable);
        System.out.println("selectedDrawables.size():"+M.selectedDrawables.size());
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            }
            Drawable D;
            Drawable Dcopy;
            M.copiedDrawables.removeAllElements();
            for (int i=0;i<M.selectedDrawables.size();i++){
                D=M.selectedDrawables.elementAt(i);
                Dcopy=D.copy();
                Dcopy.setSelectionMode(false);
                Dcopy.setSelection(false);
                Dcopy.setPartialSelectionMode(false);
                Dcopy.setPartialSelection(false);
                M.copiedDrawables.add(Dcopy);
            }
        }
        repaint();
    }
    
    if (actionCommand.equals("Coller")){
        //System.out.println("copiedDrawables.size()="+copiedDrawables.size());
        //System.out.println("M.drawables.size()="+M.drawables.size());
        if (M.copiedDrawables!=null){
            Drawable D;
            for (int i=0;i<M.copiedDrawables.size();i++){
                D=M.copiedDrawables.elementAt(i).copy();
                if (M.status==WAITING_FOR_SELECTION){
                    D.setSelectionMode(true);
                    D.setSelection(true);
                }
                if (M.status==WAITING_FOR_PARTIAL_SELECTION){
                    D.setPartialSelectionMode(true);
                    D.setPartialSelection(false);
                    D.selectedPoint=null;
                }
                M.drawables.add(D);
                if (M.status==WAITING_FOR_SELECTION){
                    M.selectedDrawables.add(D);
                }
            }
            //System.out.println("M.drawables.size()="+M.drawables.size());
            //System.out.println("M.selectedDrawables.size()="+M.selectedDrawables.size());
            currentDrawable=M.getCurrentDrawable();
        }
        repaint();
    }
    
    
    if (actionCommand.equals("Prélever")){
        System.out.println("CurrentDrawable:"+currentDrawable);
        System.out.println("selectedDrawables.size():"+M.selectedDrawables.size());
        if (currentDrawable!=null){
            if (M.selectedDrawables.size()==0){
                M.selectedDrawables.add(currentDrawable);
            }
            Drawable D;
            Drawable Dcopy;
            extractedDrawables.removeAllElements();
            for (int i=0;i<M.selectedDrawables.size();i++){
                D=M.selectedDrawables.elementAt(i);
                Dcopy=D.copy();
                Dcopy.setSelectionMode(false);
                Dcopy.setSelection(false);
                Dcopy.setPartialSelectionMode(false);
                Dcopy.setPartialSelection(false);
                extractedDrawables.add(Dcopy);
            }
        }
    }
    
    if (actionCommand.equals("Insérer")){
        //System.out.println("copiedDrawables.size()="+copiedDrawables.size());
        //System.out.println("M.drawables.size()="+M.drawables.size());
        if (extractedDrawables.size()>0){
            Drawable D;
            for (int i=0;i<extractedDrawables.size();i++){
                D=extractedDrawables.elementAt(i).copy();
                if (M.status==WAITING_FOR_SELECTION){
                    D.setSelectionMode(true);
                    D.setSelection(true);
                }
                if (M.status==WAITING_FOR_PARTIAL_SELECTION){
                    D.setPartialSelectionMode(true);
                    D.setPartialSelection(false);
                    D.selectedPoint=null;
                }
                D.setSubgroup(M.getSubgroup());
                M.drawables.add(D);
                if (M.status==WAITING_FOR_SELECTION){
                    M.selectedDrawables.add(D);
                }
            }
            //System.out.println("M.drawables.size()="+M.drawables.size());
            //System.out.println("M.selectedDrawables.size()="+M.selectedDrawables.size());
            currentDrawable=M.getCurrentDrawable();
        }
        repaint();
    }
    
    if (actionCommand.equals("Rétablir")){
        if (M.historyDrawable.size()>0){
            currentDrawable=M.historyDrawable.pop();
            boolean waspopped=M.historyPopped.pop().booleanValue();
            if (!waspopped){
                M.popDrawable();
            }
            M.add(currentDrawable);
            updatestatus(M.historyStatus.pop().intValue());
        }
    }
    
    if (actionCommand.equals("Effacer dernier trait")){
        System.out.println("currentDrawable: "+currentDrawable);
        if (currentDrawable!=null){
            M.historyDrawable.add(currentDrawable.copy());
            M.historyStatus.add(new Integer(M.status));
            
            int oldtype=currentDrawable.Type;
            currentDrawable.removeLastStroke();
            if (currentDrawable.isempty()){
                M.historyPopped.add(new Boolean(true));
                currentDrawable=M.popDrawable();
                switch (oldtype){
                case SEGMENT: 
                    updatestatus(WAITING_FOR_SEGMENT);
                    break;
                case POLYGON:
                    updatestatus(WAITING_FOR_POLYGON1);
                    break;
                case BEZIERPATH:
                    updatestatus(WAITING_FOR_BEZIER1);
                    break;
                case CIRCLE:
                    updatestatus(WAITING_FOR_CIRCLE);
                    break;
                case TEXT:
                    updatestatus(WAITING_FOR_TEXT);
                }
            }
            else {
                M.historyPopped.add(new Boolean(false));
                //System.out.println("Current drawable is not empty");
                switch (oldtype){
                    case SEGMENT:
                        updatestatus(WAITING_FOR_SEGMENT);
                        break;
                    case POLYGON:
                        updatestatus(WAITING_FOR_POLYGON2);
                        break;
                    case BEZIERPATH:
                        int s=((BezierPath) currentDrawable).getStatus();
                        updatestatus(s);
                        //System.out.println("Status updated to:"+s);
                        break;
                    case CIRCLE:
                        updatestatus(WAITING_FOR_CIRCLE);
                    break;
                    case TEXT:
                        updatestatus(WAITING_FOR_TEXT);
                }
            }
        }
        //System.out.println("M.drawables.size()="+M.drawables.size());
        //System.out.println("currentDrawable.size()="+currentDrawable.size());
        repaint();
    }
    
    if (actionCommand.equals("Effacer le dessin")){
        System.out.println("M.drawables.size()="+M.drawables.size());
        if (M.drawables.size()>0){
            M.drawables.removeAllElements();
        }
        M.clearSelection();
        M.clearLimboDrawables();
        mp.voronoiPolygon=null;
        currentDrawable=null;
        updatestatus(WAITING_FOR_SEGMENT);
        repaint();
    }
    
    if (actionCommand.equals("Diminuer")){
       Rectangle R=getBounds();
       MPoint P=new MPoint(R.height/2, R.width/2);
       M.xtou.transform(P, P);
       AffineTransform homotethy= homotethy((float) 1.5, P);
       M.xtou.preConcatenate(homotethy);
       M.utox=new AffineTransform(M.xtou);
       try {
           M.utox.invert();
       }
       catch (NoninvertibleTransformException  e){
	    System.out.println("Agrandir: "+e.getMessage());
	}
       repaint();
    }
    
    if (actionCommand.equals("Agrandir")){
       Rectangle R=getBounds();
       MPoint P=new MPoint(R.height/2, R.width/2);
       M.xtou.transform(P, P);
       AffineTransform homotethy= homotethy((float) 2/3, P);
       M.xtou.preConcatenate(homotethy);
       M.utox=new AffineTransform(M.xtou);
       try {
           M.utox.invert();
       }
       catch (NoninvertibleTransformException  e){
	    System.out.println("Agrandir: "+e.getMessage());
	}
       repaint();
    }
    
    if (actionCommand.equals("Dom. fond.")){
        M.togglefunddom();
        repaint();
    }
    
    if (actionCommand.equals("Carte des sym.")){
        M.togglesymchart();
        repaint();
    }
    
    if (actionCommand.equals("Export group data ...")){
        TextDisplayDialog.showDialog(fr,fr,"Group data", groupdata(),5, 40);
    }
    
    if (actionCommand.equals("Export symmetry chart data ...")){
        String s=symchartdata();
        //System.out.println("symchartdata="+s);
        TextDisplayDialog.showDialog(fr,fr,"Sym. chart data", s ,5,50);
    }
    
    if (actionCommand.equals("Convert motives to sym. charts")){
        Motive Mi;
        for (int i=0; i<ms.motives.size();i++){
            Mi=ms.motives.elementAt(i);
            Mi.makedrawablesgray();
            Mi.symchart=new Motive(Mi);
            Mi.symchart.funddomon=false;
            //System.out.println("Sym. chart drawables:"+Mi.symchart.drawables.size());
            Mi.drawables.removeAllElements();
            Mi.symcharton=true;
            groups[Mi.group.number].symchart=Mi.symchart;
        }
        repaint();
    }
    
    if (actionCommand.equals("Dashed")){
        if (currentDrawable!=null){
            currentDrawable.toggledashed();
        }
        repaint();
    }
    
    if (actionCommand.equals("Nouveau motif")){
        mot=new Motive(getBounds(), M.getSubgroup());
        ms.add(mot);
        changeCurrentMotive(mot);
        System.out.println("Nouveau motif créé: "+M.title+". Nombre total :"+ms.size());
        repaint();
    }
    
    if (actionCommand.equals("Titre ...")){
        String s=(String)JOptionPane.showInputDialog(fr, "Rentrez le nouveau titre du motif:");
        if (s!=null){
            M.title=s;
        }
        updateFrameTitle();
        updateMenus();
    }
    
    if (actionCommand.equals("A propos ...")){
            Runtime runtime=Runtime.getRuntime();
            long maxMemory=runtime.maxMemory();
            long allocatedMemory=runtime.totalMemory();
            long freeMemory=runtime.freeMemory();
            
		 JOptionPane.showMessageDialog(this, 
					       "Cristal\n" +
                 "24 janvier 2018\n\nBernhard Keller\n" +
                 "Université Paris Diderot - Paris 7\nInstitut de mathématiques de Jussieu - PRG\n\n"+
                         "Mémoire libre : "+freeMemory/1024 +"\n"+
                         "Mémoire allouée : "+allocatedMemory/1024+"\n"+
                         "Mémoire maximale : "+maxMemory/1024+"\n"+
                         "Mémoire libre totale : "+(freeMemory+(maxMemory-allocatedMemory))/1024);
    }
    
    if (actionCommand.equals("Magnétisme ...")){
        String s=(String)JOptionPane.showInputDialog(fr, "Nombre de sous-divisions actuel : "+Math.round(1/gridsize) +"\n"+
                "Rentrez le nouveau nombre de sous-divisions :");
        if (s!=null){
            if (s.length()>0){
                int n=Integer.parseInt(s);
                gridsize=1/(float) n;
                //System.out.println("n="+n+" gridsize="+gridsize);
            }
        }
    }
    
    if (actionCommand.equals("Crayon tracés ...")){
        String s=(String)JOptionPane.showInputDialog(fr, "Largeur du crayon actuelle : "+Drawable.penwidth +"\n"+
                "Rentrez la nouvelle largeur du crayon.");
        if (s!=null){
            if (s.length()>0){
                float w=Float.parseFloat(s);
                Drawable.penwidth=w;
            }
        }
        repaint();
    }
    
    if (actionCommand.equals("Diam. marques ...")){
        String s=(String)JOptionPane.showInputDialog(fr, "Diamètre des marques actuel : "+Drawable.markdiam +"\n"+
                "Rentrez le nouveau diamètre des marques.");
        if (s!=null){
            if (s.length()>0){
                float d=Float.parseFloat(s);
                Drawable.markdiam=d;
            }
        }
        repaint();
    }
    
    if (actionCommand.equals("Centrer")){
        MPoint C1=M.getBarycenter();
        if (C1==null){
            if (currentDrawable==null){
                return;
            }
            else {
                C1=currentDrawable.barycenter();
            }
        }
        M.centerAt(C1);
        repaint();
    }
    
    if (actionCommand.equals("Tourner ...")){
        RotationDialog rd=new RotationDialog(fr);
        float angle=rd.getAngle();
        AffineTransform rot=Utils.rotation(new MPoint(0,0), angle);
        M.xtou.preConcatenate(rot);
        M.utox=new AffineTransform(M.xtou);
        try {
            M.utox.invert();
        }
        catch (NoninvertibleTransformException  e){
            System.out.println("Tourner ... : "+e.getMessage());
        }
        repaint();
    }
    
   }
   
   
    
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        
        if (pi >= 1) {
                return Printable.NO_SUCH_PAGE;
        }
        
	Graphics2D g2d = (Graphics2D) g;
        
        Rectangle rc=g2d.getClipBounds();
        rc.setLocation(0,0);
        Rectangle rq=this.getBounds();
        rq.setLocation(0,0);
        
        //printrect(g2d,rc);
        
        double cw=rc.getWidth()/rq.getWidth();
        double ch=rc.getHeight()/rq.getHeight();
        double c=cw;
        if (ch<=cw){c=ch;}
        
        double gtx= rc.getCenterX()-c*rq.getCenterX();
        double gty= rc.getCenterY()-c*rq.getCenterY();
        
        g2d.translate(gtx,gty);
        g2d.scale(c,c);
    
        //printrect(g2d,rq);

	M.draworbit(g2d, M.utox, g2d.getClipBounds(),false);
          //System.out.println("qs is null. Drawing quiver via S.Q.drawQuiver(g2dtop)");
	return Printable.PAGE_EXISTS;
    }
    
    private void Print(){
	//if (printJob!=null){
	if (false){
             try {
                printJob.print();  
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
         }
         else {
	     //System.out.println("printJob is null.");
         printJob = PrinterJob.getPrinterJob();
         printJob.setPrintable(this);
         if (printJob.printDialog()) {
            try {
                printJob.print();  
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
         }
        }
     }
    
    private void SaveIt(){
        if (fileChooser==null){return;}
        File file = fileChooser.getSelectedFile();
        if (file==null){return;}
        fileName=file.getName();
        //System.out.println("Saved "+fileName);
        FileWriter out=null;
        try{
            out = new FileWriter(file);
            }
        catch (IOException e) {
            System.out.println("File not found."); 
        }

        BufferedWriter w=null;
        ms.setCurrentMotive(M);
        try{
            if (out!=null){
            w = new BufferedWriter(out);
            Utils.writeboolean(w,"Multiple mode on", multiplemodeon);
            Utils.writeint(w, "Number of rows", nbrows);
            Utils.writeint(w, "Number of columnes", nbcols);
            ms.write(w);
            w.flush();
            }
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
        updateFrameTitle();
        changesMade=false;
    }
    
    private void Save(){
        if (fileName.equals("")){
            SaveAs();
        }
        else 
        {
            SaveIt();
        } 
    }
    
    private void SaveAs(){
        File file=null;
        
        int ans=JOptionPane.YES_OPTION;
        int returnVal;
        do {
            returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                //System.out.println("Saving: " + file.getName() + ".");
                if (file.exists()){
                    ans=JOptionPane.showConfirmDialog(this,"Le fichier existe. Le remplacer ?");
                }
            }
        } while ((ans==JOptionPane.NO_OPTION) & (returnVal!=JFileChooser.CANCEL_OPTION));
        if (ans==JOptionPane.YES_OPTION){
            SaveIt();
        }
     }
    
   


    
   public String groupdata(){
       String s="";
       String s1="";
       Subgroup[] sg;
       AffineTransform[] T;
       for (int i=0; i<17; i++){
           s=s+"groups["+i+"]=new Group();";
           s=s+"groups["+i+"].name="+"\""+groups[i].name + "\";\n";
           sg=groups[i].subgroups;
           s=s+"groups["+i+"].subgroups = new Subgroup["+sg.length+"];\n";
           for (int j=0; j<sg.length; j++){
               s1="groups["+i+"].subgroups["+j+"]";
               s=s+s1+"=new Subgroup();\n";
               s=s+s1+".group=groups["+i+"];\n";
               s=s+s1+".number="+sg[j].number+";\n";
               s=s+s1+".name=\""+sg[j].name+"\";\n";
               s=s+s1+".uvec=new float[] {"+sg[j].uvec[0]+"f,"+sg[j].uvec[1]+"f};\n";
               s=s+s1+".vvec=new float[] {"+sg[j].vvec[0]+"f,"+sg[j].vvec[1]+"f};\n";
               T=groups[i].subgroups[j].T;
               s=s+s1+".T=new AffineTransform["+T.length+"];\n";
               double[] matrix=new double[6];
               for (int k=0;k<T.length;k++){
                   T[k].getMatrix(matrix);
                   s=s+s1+".T["+k+"]=new AffineTransform("+matrix[0]+","+matrix[1]+","+matrix[2]+","+matrix[3]+","+matrix[4]+","+matrix[5]+");\n";
               }
               MPoint[] fd=groups[i].subgroups[j].funddom;
               s=s+s1+".funddom=new MPoint["+fd.length+"];\n";
               for (int k=0;k<fd.length;k++){
                   s=s+s1+".funddom["+k+"]=new MPoint("+ (float) fd[k].getX()+"f,"+(float) fd[k].getY()+"f);\n";
               }
           }  
       }
       return s;
    }
    
    public String symchartdata(){
       Motive S;
       String s="Motive S; Subgroup sgr; MPoint P1; MPoint P2;\n";
       for (int i=0;i<17;i++){
           S=groups[i].symchart;
           if (S!=null){
               s=s+"sgr=groups["+i+"].subgroups[0];\n";
               s=s+"groups["+i+"].symchart=new Motive(getBounds(), sgr);\n";
               s=s+"S=groups["+i+"].symchart;\n";
               s=s+"S.funddomon=false;\n";
               for (int j=0; j<S.drawables.size();j++){
                   s=s+S.drawables.elementAt(j).datastring();
                   s=s+"S.drawables.add(new Segment(P1,P2,sgr));\n";
                   s=s+"S.drawables.peek().setColor(Color.gray);\n";
                   if (S.drawables.elementAt(j).isdashed){
                       System.out.println("Group "+i+" Element "+j+" is dashed");
                       s=s+"S.drawables.peek().toggledashed();\n";
                   }
               } 
           }
       }
       return s;
   }
    
    private void initializesymcharts(){
        Motive S; Subgroup sgr; MPoint P1; MPoint P2;
        sgr=groups[0].subgroups[0];
        groups[0].symchart=new Motive(getBounds(), sgr);
        S=groups[0].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.0733, (float) 0.3533);
        P2=new MPoint((float) 0.0667, (float) 0.8867);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.1467, (float) 0.9467);
        P2=new MPoint((float) 0.02, (float) 1.32);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.0733, (float) 0.8733);
        P2=new MPoint((float) 1.02, (float) 0.16);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 1.02, (float) 0.16);
        P2=new MPoint((float) 1.4, (float) 1.36);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 1.4, (float) 1.36);
        P2=new MPoint((float) -1.0533, (float) 1.56);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.0533, (float) 1.56);
        P2=new MPoint((float) -1.1533, (float) 0.96);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[1].subgroups[0];
        groups[1].symchart=new Motive(getBounds(), sgr);
        S=groups[1].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.28, (float) 1.0533);
        P2=new MPoint((float) -0.94, (float) 0.9133);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.94, (float) 0.9133);
        P2=new MPoint((float) -0.9133, (float) 1.0267);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.7133, (float) 0.12);
        P2=new MPoint((float) -1.4, (float) 0.06);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.4, (float) 0.06);
        P2=new MPoint((float) -1.42, (float) -0.0933);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.14, (float) 0.1133);
        P2=new MPoint((float) 0.1, (float) 0.06);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.1, (float) 0.06);
        P2=new MPoint((float) 0.0, (float) -0.08);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.4467, (float) 1.18);
        P2=new MPoint((float) 0.56, (float) 0.98);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.56, (float) 0.98);
        P2=new MPoint((float) 0.5067, (float) 0.9267);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[2].subgroups[0];
        groups[2].symchart=new Motive(getBounds(), sgr);
        S=groups[2].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.1333, (float) 0.7867);
        P2=new MPoint((float) -0.9133, (float) 0.5533);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.1267, (float) 0.1667);
        P2=new MPoint((float) 0.0533, (float) -0.0133);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0933, (float) 1.36);
        P2=new MPoint((float) 0.08, (float) 1.12);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[3].subgroups[0];
        groups[3].symchart=new Motive(getBounds(), sgr);
        S=groups[3].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.2267, (float) 1.2333);
        P2=new MPoint((float) -0.8667, (float) 1.04);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.12, (float) 1.06);
        P2=new MPoint((float) 0.0, (float) 0.9533);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.0, (float) 0.9533);
        P2=new MPoint((float) 0.06, (float) 1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.1533, (float) 0.2);
        P2=new MPoint((float) 0.06, (float) 0.0067);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[4].subgroups[0];
        groups[4].symchart=new Motive(getBounds(), sgr);
        S=groups[4].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -0.1067, (float) 0.22);
        P2=new MPoint((float) 0.1133, (float) 0.0467);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.1133, (float) 0.76);
        P2=new MPoint((float) -0.9333, (float) 0.56);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.6133, (float) 0.9933);
        P2=new MPoint((float) -0.5333, (float) 0.7867);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.5333, (float) 0.7867);
        P2=new MPoint((float) -0.4333, (float) 0.88);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[5].subgroups[0];
        groups[5].symchart=new Motive(getBounds(), sgr);
        S=groups[5].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -2.0, (float) 1.1507);
        P2=new MPoint((float) -2.0822, (float) 0.9452);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0822, (float) 0.9452);
        P2=new MPoint((float) -1.9658, (float) 0.9521);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.3219, (float) -0.1575);
        P2=new MPoint((float) -1.9932, (float) 0.1575);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.9932, (float) 0.1575);
        P2=new MPoint((float) -1.8288, (float) 0.0137);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.0, (float) -1.0);
        P2=new MPoint((float) -1.0, (float) 0.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -2.0, (float) 0.5);
        P2=new MPoint((float) 0.0, (float) 0.5);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        sgr=groups[6].subgroups[0];
        groups[6].symchart=new Motive(getBounds(), sgr);
        S=groups[6].symchart;
        S.funddomon=false;
        P1=new MPoint((float) 2.0, (float) 0.0);
        P2=new MPoint((float) 0.0, (float) 0.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) 2.0, (float) 1.0);
        P2=new MPoint((float) 0.0, (float) 1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        sgr=groups[7].subgroups[0];
        groups[7].symchart=new Motive(getBounds(), sgr);
        S=groups[7].symchart;
        S.funddomon=false;
        P1=new MPoint((float) 0.9932, (float) -0.7483);
        P2=new MPoint((float) 2.9966, (float) -0.7483);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -2.0068, (float) -1.4983);
        P2=new MPoint((float) 1.9795, (float) -1.4983);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[8].subgroups[0];
        groups[8].symchart=new Motive(getBounds(), sgr);
        S=groups[8].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.0073, (float) 0.0024);
        P2=new MPoint((float) -1.0073, (float) 0.7561);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -3.0049, (float) 0.7561);
        P2=new MPoint((float) -2.0024, (float) 0.7561);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -2.0024, (float) 0.0024);
        P2=new MPoint((float) -0.0195, (float) 0.0024);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0024, (float) 1.5024);
        P2=new MPoint((float) -2.0024, (float) 0.0024);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0024, (float) 1.7512);
        P2=new MPoint((float) -1.8122, (float) 1.3342);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -3.1, (float) 0.9098);
        P2=new MPoint((float) -2.9244, (float) 0.7707);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.9244, (float) 0.7707);
        P2=new MPoint((float) -2.9829, (float) 0.6537);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.1268, (float) 0.1854);
        P2=new MPoint((float) -1.9, (float) -0.0049);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[9].subgroups[0];
        groups[9].symchart=new Motive(getBounds(), sgr);
        S=groups[9].symchart;
        S.funddomon=false;
        P1=new MPoint((float) 1.7146, (float) 0.2024);
        P2=new MPoint((float) 2.1683, (float) 0.1073);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 2.1683, (float) 0.1073);
        P2=new MPoint((float) 2.0366, (float) -0.1488);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.1366, (float) 1.2049);
        P2=new MPoint((float) 0.1122, (float) 1.0585);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.1122, (float) 1.0585);
        P2=new MPoint((float) 0.0024, (float) 0.8683);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 2.0, (float) -0.4927);
        P2=new MPoint((float) 2.0, (float) 0.0195);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -0.0049, (float) -0.4927);
        P2=new MPoint((float) -0.0049, (float) 0.0049);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -0.0085, (float) -0.489);
        P2=new MPoint((float) 3.9976, (float) -0.489);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[10].subgroups[0];
        groups[10].symchart=new Motive(getBounds(), sgr);
        S=groups[10].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -0.0033, (float) 2.0);
        P2=new MPoint((float) 3.9967, (float) 2.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0033, (float) 1.0);
        P2=new MPoint((float) 3.9967, (float) 1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[11].subgroups[0];
        groups[11].symchart=new Motive(getBounds(), sgr);
        S=groups[11].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -0.2533, (float) 1.7);
        P2=new MPoint((float) 0.1967, (float) 2.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 1.7567, (float) 1.79);
        P2=new MPoint((float) 2.0967, (float) 2.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 1.7667, (float) 1.27);
        P2=new MPoint((float) 2.0967, (float) 0.93);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.1533, (float) 1.19);
        P2=new MPoint((float) 0.0967, (float) 0.99);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0033, (float) 1.0);
        P2=new MPoint((float) 1.9967, (float) 1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0033, (float) 1.0);
        P2=new MPoint((float) -0.0033, (float) 2.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0033, (float) 2.0);
        P2=new MPoint((float) 1.9967, (float) 2.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[12].subgroups[0];
        groups[12].symchart=new Motive(getBounds(), sgr);
        S=groups[12].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.0035, (float) 0.0023);
        P2=new MPoint((float) -0.0023, (float) 1.0035);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0959, (float) 0.0351);
        P2=new MPoint((float) 0.0444, (float) 0.0304);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.5029, (float) 0.5029);
        P2=new MPoint((float) 0.4936, (float) 0.5029);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -1.0035, (float) 0.1988);
        P2=new MPoint((float) -0.924, (float) -0.0725);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[13].subgroups[0];
        groups[13].symchart=new Motive(getBounds(), sgr);
        S=groups[13].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.0, (float) -1.0);
        P2=new MPoint((float) 0.0, (float) -1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.8836, (float) -0.8973);
        P2=new MPoint((float) -0.7808, (float) -1.2123);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.1438, (float) 0.0616);
        P2=new MPoint((float) -0.9863, (float) -0.0685);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.1575, (float) -0.0068);
        P2=new MPoint((float) 0.1096, (float) -0.1027);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.0, (float) -1.0);
        P2=new MPoint((float) 0.5, (float) -0.5);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) 0.0, (float) -2.0068);
        P2=new MPoint((float) 0.0, (float) -2.0068);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.0, (float) -2.0);
        P2=new MPoint((float) 0.0, (float) -1.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.0, (float) -1.0);
        P2=new MPoint((float) 0.0, (float) 0.0);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        sgr=groups[14].subgroups[0];
        groups[14].symchart=new Motive(getBounds(), sgr);
        S=groups[14].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -1.0023, (float) -1.7318);
        P2=new MPoint((float) -0.0068, (float) -1.7273);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.0068, (float) -1.7273);
        P2=new MPoint((float) 0.4932, (float) -0.8591);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.9977, (float) -1.5909);
        P2=new MPoint((float) -0.8977, (float) -1.5364);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) 0.0023, (float) -1.0091);
        P2=new MPoint((float) 0.0023, (float) -1.0091);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.0977, (float) -1.1);
        P2=new MPoint((float) 0.2477, (float) -1.0091);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.5841, (float) -0.7182);
        P2=new MPoint((float) -0.4841, (float) -0.9682);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -0.5023, (float) -0.8636);
        P2=new MPoint((float) -0.0068, (float) -0.8591);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        P1=new MPoint((float) -0.5068, (float) -0.8591);
        P2=new MPoint((float) -0.5068, (float) -1.7273);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        sgr=groups[15].subgroups[0];
        groups[15].symchart=new Motive(getBounds(), sgr);
        S=groups[15].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -2.0, (float) 0.2945);
        P2=new MPoint((float) -1.8394, (float) 0.0054);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.3333, (float) 0.5837);
        P2=new MPoint((float) -0.9237, (float) 0.5033);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) 0.0054);
        P2=new MPoint((float) 0.0, (float) 0.0054);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.502, (float) 0.8728);
        P2=new MPoint((float) -0.506, (float) 0.8728);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
        sgr=groups[16].subgroups[0];
        groups[16].symchart=new Motive(getBounds(), sgr);
        S=groups[16].symchart;
        S.funddomon=false;
        P1=new MPoint((float) -2.3288, (float) -0.5205);
        P2=new MPoint((float) -2.3288, (float) -0.5205);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) -1.1507);
        P2=new MPoint((float) -1.0, (float) -0.5753);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) 0.0);
        P2=new MPoint((float) -1.0, (float) -0.5753);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) 0.0);
        P2=new MPoint((float) -1.0, (float) 0.5822);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) -0.2877);
        P2=new MPoint((float) -1.9452, (float) -0.0411);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.1027, (float) -1.0959);
        P2=new MPoint((float) -1.8425, (float) -0.911);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -1.2329, (float) -0.589);
        P2=new MPoint((float) -1.0548, (float) -0.5548);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        P1=new MPoint((float) -2.0, (float) -0.5753);
        P2=new MPoint((float) -0.5, (float) 0.2945);
        S.drawables.add(new Segment(P1,P2,sgr));
        S.drawables.peek().setColor(Color.gray);
        S.drawables.peek().toggledashed();
    }
    
    private void initializegroups(){
        
        groups[0]=new Group();groups[0].name="p1";
        groups[0].subgroups = new Subgroup[3];
        groups[0].subgroups[0]=new Subgroup();
        groups[0].subgroups[0].group=groups[0];
        groups[0].subgroups[0].number=0;
        groups[0].subgroups[0].name="p1";
        groups[0].subgroups[0].uvec=new float[] {3.0f,0.0f};
        groups[0].subgroups[0].vvec=new float[] {1.0f,2.0f};
        groups[0].subgroups[0].T=new AffineTransform[1];
        groups[0].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[0].subgroups[0].funddom=new MPoint[5];
        groups[0].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[0].subgroups[0].funddom[1]=new MPoint(3.0f,0.0f);
        groups[0].subgroups[0].funddom[2]=new MPoint(4.0f,2.0f);
        groups[0].subgroups[0].funddom[3]=new MPoint(1.0f,2.0f);
        groups[0].subgroups[0].funddom[4]=new MPoint(0.0f,0.0f);
        groups[0].subgroups[1]=new Subgroup();
        groups[0].subgroups[1].group=groups[0];
        groups[0].subgroups[1].number=1;
        groups[0].subgroups[1].name="p1(2)p1";
        groups[0].subgroups[1].uvec=new float[] {6.0f,0.0f};
        groups[0].subgroups[1].vvec=new float[] {1.0f,2.0f};
        groups[0].subgroups[1].T=new AffineTransform[1];
        groups[0].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[0].subgroups[1].funddom=new MPoint[5];
        groups[0].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[0].subgroups[1].funddom[1]=new MPoint(6.0f,0.0f);
        groups[0].subgroups[1].funddom[2]=new MPoint(7.0f,2.0f);
        groups[0].subgroups[1].funddom[3]=new MPoint(1.0f,2.0f);
        groups[0].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[0].subgroups[2]=new Subgroup();
        groups[0].subgroups[2].group=groups[0];
        groups[0].subgroups[2].number=2;
        groups[0].subgroups[2].name="p1(4)p1";
        groups[0].subgroups[2].uvec=new float[] {6.0f,0.0f};
        groups[0].subgroups[2].vvec=new float[] {2.0f,4.0f};
        groups[0].subgroups[2].T=new AffineTransform[1];
        groups[0].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[0].subgroups[2].funddom=new MPoint[5];
        groups[0].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[0].subgroups[2].funddom[1]=new MPoint(6.0f,0.0f);
        groups[0].subgroups[2].funddom[2]=new MPoint(8.0f,4.0f);
        groups[0].subgroups[2].funddom[3]=new MPoint(2.0f,4.0f);
        groups[0].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[1]=new Group();groups[1].name="p2";
        groups[1].subgroups = new Subgroup[3];
        groups[1].subgroups[0]=new Subgroup();
        groups[1].subgroups[0].group=groups[1];
        groups[1].subgroups[0].number=0;
        groups[1].subgroups[0].name="p2";
        groups[1].subgroups[0].uvec=new float[] {3.0f,0.0f};
        groups[1].subgroups[0].vvec=new float[] {1.0f,2.0f};
        groups[1].subgroups[0].T=new AffineTransform[2];
        groups[1].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[1].subgroups[0].T[1]=new AffineTransform(-1.0,0.0,0.0,-1.0,4.0,2.0);
        groups[1].subgroups[0].funddom=new MPoint[5];
        groups[1].subgroups[0].funddom[0]=new MPoint(1.0f,2.0f);
        groups[1].subgroups[0].funddom[1]=new MPoint(0.0f,0.0f);
        groups[1].subgroups[0].funddom[2]=new MPoint(3.0f,0.0f);
        groups[1].subgroups[0].funddom[3]=new MPoint(2.0f,1.0f);
        groups[1].subgroups[0].funddom[4]=new MPoint(1.0f,2.0f);
        groups[1].subgroups[1]=new Subgroup();
        groups[1].subgroups[1].group=groups[1];
        groups[1].subgroups[1].number=1;
        groups[1].subgroups[1].name="p2(2)p2";
        groups[1].subgroups[1].uvec=new float[] {3.0f,0.0f};
        groups[1].subgroups[1].vvec=new float[] {2.0f,4.0f};
        groups[1].subgroups[1].T=new AffineTransform[2];
        groups[1].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[1].subgroups[1].T[1]=new AffineTransform(-1.0,0.0,0.0,-1.0,5.0,4.0);
        groups[1].subgroups[1].funddom=new MPoint[5];
        groups[1].subgroups[1].funddom[0]=new MPoint(2.0f,4.0f);
        groups[1].subgroups[1].funddom[1]=new MPoint(0.0f,0.0f);
        groups[1].subgroups[1].funddom[2]=new MPoint(3.0f,0.0f);
        groups[1].subgroups[1].funddom[3]=new MPoint(2.5f,2.0f);
        groups[1].subgroups[1].funddom[4]=new MPoint(2.0f,4.0f);
        groups[1].subgroups[2]=new Subgroup();
        groups[1].subgroups[2].group=groups[1];
        groups[1].subgroups[2].number=2;
        groups[1].subgroups[2].name="p1(2)p2";
        groups[1].subgroups[2].uvec=new float[] {3.0f,0.0f};
        groups[1].subgroups[2].vvec=new float[] {1.0f,2.0f};
        groups[1].subgroups[2].T=new AffineTransform[1];
        groups[1].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[1].subgroups[2].funddom=new MPoint[5];
        groups[1].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[1].subgroups[2].funddom[1]=new MPoint(3.0f,0.0f);
        groups[1].subgroups[2].funddom[2]=new MPoint(4.0f,2.0f);
        groups[1].subgroups[2].funddom[3]=new MPoint(1.0f,2.0f);
        groups[1].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[2]=new Group();groups[2].name="p3";
        groups[2].subgroups = new Subgroup[3];
        groups[2].subgroups[0]=new Subgroup();
        groups[2].subgroups[0].group=groups[2];
        groups[2].subgroups[0].number=0;
        groups[2].subgroups[0].name="p3";
        groups[2].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[2].subgroups[0].vvec=new float[] {1.0f,1.73205f};
        groups[2].subgroups[0].T=new AffineTransform[3];
        groups[2].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[2].subgroups[0].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,2.0,0.0);
        groups[2].subgroups[0].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,1.0,1.73205);
        groups[2].subgroups[0].funddom=new MPoint[5];
        groups[2].subgroups[0].funddom[0]=new MPoint(2.0f,0.0f);
        groups[2].subgroups[0].funddom[1]=new MPoint(2.0f,1.1547f);
        groups[2].subgroups[0].funddom[2]=new MPoint(1.0f,1.73205f);
        groups[2].subgroups[0].funddom[3]=new MPoint(1.0f,0.57735f);
        groups[2].subgroups[0].funddom[4]=new MPoint(2.0f,0.0f);
        groups[2].subgroups[1]=new Subgroup();
        groups[2].subgroups[1].group=groups[2];
        groups[2].subgroups[1].number=1;
        groups[2].subgroups[1].name="p1(3)p3";
        groups[2].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[2].subgroups[1].vvec=new float[] {1.0f,1.73205f};
        groups[2].subgroups[1].T=new AffineTransform[1];
        groups[2].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[2].subgroups[1].funddom=new MPoint[5];
        groups[2].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[2].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[2].subgroups[1].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[2].subgroups[1].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[2].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[2].subgroups[2]=new Subgroup();
        groups[2].subgroups[2].group=groups[2];
        groups[2].subgroups[2].number=2;
        groups[2].subgroups[2].name="p3(3)p3";
        groups[2].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[2].subgroups[2].vvec=new float[] {2.0f,3.4641f};
        groups[2].subgroups[2].T=new AffineTransform[3];
        groups[2].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[2].subgroups[2].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,18.0,3.4641);
        groups[2].subgroups[2].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,6.0,17.3205);
        groups[2].subgroups[2].funddom=new MPoint[5];
        groups[2].subgroups[2].funddom[0]=new MPoint(4.0f,6.9282f);
        groups[2].subgroups[2].funddom[1]=new MPoint(6.0f,5.7735f);
        groups[2].subgroups[2].funddom[2]=new MPoint(8.0f,6.9282f);
        groups[2].subgroups[2].funddom[3]=new MPoint(6.0f,8.0829f);
        groups[2].subgroups[2].funddom[4]=new MPoint(4.0f,6.9282f);
        groups[3]=new Group();groups[3].name="p4";
        groups[3].subgroups = new Subgroup[4];
        groups[3].subgroups[0]=new Subgroup();
        groups[3].subgroups[0].group=groups[3];
        groups[3].subgroups[0].number=0;
        groups[3].subgroups[0].name="p4";
        groups[3].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[3].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[3].subgroups[0].T=new AffineTransform[4];
        groups[3].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[3].subgroups[0].T[1]=new AffineTransform(0.0,1.0,-1.0,0.0,2.0,0.0);
        groups[3].subgroups[0].T[2]=new AffineTransform(-1.0,0.0,0.0,-1.0,2.0,2.0);
        groups[3].subgroups[0].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,0.0,2.0);
        groups[3].subgroups[0].funddom=new MPoint[5];
        groups[3].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[0].funddom[1]=new MPoint(1.0f,0.0f);
        groups[3].subgroups[0].funddom[2]=new MPoint(1.0f,1.0f);
        groups[3].subgroups[0].funddom[3]=new MPoint(0.0f,1.0f);
        groups[3].subgroups[0].funddom[4]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[1]=new Subgroup();
        groups[3].subgroups[1].group=groups[3];
        groups[3].subgroups[1].number=1;
        groups[3].subgroups[1].name="p2(2)p4";
        groups[3].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[3].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[3].subgroups[1].T=new AffineTransform[2];
        groups[3].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[3].subgroups[1].T[1]=new AffineTransform(-1.0,0.0,0.0,-1.0,2.0,2.0);
        groups[3].subgroups[1].funddom=new MPoint[4];
        groups[3].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[3].subgroups[1].funddom[2]=new MPoint(0.0f,2.0f);
        groups[3].subgroups[1].funddom[3]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[2]=new Subgroup();
        groups[3].subgroups[2].group=groups[3];
        groups[3].subgroups[2].number=2;
        groups[3].subgroups[2].name="p1(4)p4";
        groups[3].subgroups[2].uvec=new float[] {2.0f,0.0f};
        groups[3].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[3].subgroups[2].T=new AffineTransform[1];
        groups[3].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[3].subgroups[2].funddom=new MPoint[5];
        groups[3].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[2].funddom[1]=new MPoint(2.0f,0.0f);
        groups[3].subgroups[2].funddom[2]=new MPoint(2.0f,2.0f);
        groups[3].subgroups[2].funddom[3]=new MPoint(0.0f,2.0f);
        groups[3].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[3].subgroups[3]=new Subgroup();
        groups[3].subgroups[3].group=groups[3];
        groups[3].subgroups[3].number=3;
        groups[3].subgroups[3].name="p4(2)p4";
        groups[3].subgroups[3].uvec=new float[] {2.0f,2.0f};
        groups[3].subgroups[3].vvec=new float[] {-2.0f,2.0f};
        groups[3].subgroups[3].T=new AffineTransform[4];
        groups[3].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[3].subgroups[3].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,10.0,-2.0);
        groups[3].subgroups[3].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,12.0,8.0);
        groups[3].subgroups[3].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,2.0,10.0);
        groups[3].subgroups[3].funddom=new MPoint[5];
        groups[3].subgroups[3].funddom[0]=new MPoint(6.0f,4.0f);
        groups[3].subgroups[3].funddom[1]=new MPoint(8.0f,6.0f);
        groups[3].subgroups[3].funddom[2]=new MPoint(6.0f,8.0f);
        groups[3].subgroups[3].funddom[3]=new MPoint(4.0f,6.0f);
        groups[3].subgroups[3].funddom[4]=new MPoint(6.0f,4.0f);
        groups[4]=new Group();groups[4].name="p6";
        groups[4].subgroups = new Subgroup[5];
        groups[4].subgroups[0]=new Subgroup();
        groups[4].subgroups[0].group=groups[4];
        groups[4].subgroups[0].number=0;
        groups[4].subgroups[0].name="p6";
        groups[4].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[4].subgroups[0].vvec=new float[] {1.0f,1.73205f};
        groups[4].subgroups[0].T=new AffineTransform[6];
        groups[4].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[4].subgroups[0].T[1]=new AffineTransform(0.5,0.86603,-0.86603,0.5,0.0,0.0);
        groups[4].subgroups[0].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,0.0,0.0);
        groups[4].subgroups[0].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,0.0,0.0);
        groups[4].subgroups[0].T[4]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,0.0,0.0);
        groups[4].subgroups[0].T[5]=new AffineTransform(0.5,-0.86603,0.86603,0.5,0.0,0.0);
        groups[4].subgroups[0].funddom=new MPoint[5];
        groups[4].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[4].subgroups[0].funddom[1]=new MPoint(1.0f,0.0f);
        groups[4].subgroups[0].funddom[2]=new MPoint(1.0f,0.57735f);
        groups[4].subgroups[0].funddom[3]=new MPoint(0.5f,0.86603f);
        groups[4].subgroups[0].funddom[4]=new MPoint(0.0f,0.0f);
        groups[4].subgroups[1]=new Subgroup();
        groups[4].subgroups[1].group=groups[4];
        groups[4].subgroups[1].number=1;
        groups[4].subgroups[1].name="p1(6)p6";
        groups[4].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[4].subgroups[1].vvec=new float[] {1.0f,1.73205f};
        groups[4].subgroups[1].T=new AffineTransform[1];
        groups[4].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[4].subgroups[1].funddom=new MPoint[5];
        groups[4].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[4].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[4].subgroups[1].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[4].subgroups[1].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[4].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[4].subgroups[2]=new Subgroup();
        groups[4].subgroups[2].group=groups[4];
        groups[4].subgroups[2].number=2;
        groups[4].subgroups[2].name="p3(2)p6";
        groups[4].subgroups[2].uvec=new float[] {2.0f,0.0f};
        groups[4].subgroups[2].vvec=new float[] {1.0f,1.73205f};
        groups[4].subgroups[2].T=new AffineTransform[3];
        groups[4].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[4].subgroups[2].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,6.0,10.39231);
        groups[4].subgroups[2].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-6.0,10.39231);
        groups[4].subgroups[2].funddom=new MPoint[5];
        groups[4].subgroups[2].funddom[0]=new MPoint(1.0f,19.05256f);
        groups[4].subgroups[2].funddom[1]=new MPoint(2.0f,18.47521f);
        groups[4].subgroups[2].funddom[2]=new MPoint(3.0f,19.05256f);
        groups[4].subgroups[2].funddom[3]=new MPoint(2.0f,19.62991f);
        groups[4].subgroups[2].funddom[4]=new MPoint(1.0f,19.05256f);
        groups[4].subgroups[3]=new Subgroup();
        groups[4].subgroups[3].group=groups[4];
        groups[4].subgroups[3].number=3;
        groups[4].subgroups[3].name="p2(3)p6";
        groups[4].subgroups[3].uvec=new float[] {2.0f,0.0f};
        groups[4].subgroups[3].vvec=new float[] {1.0f,1.73205f};
        groups[4].subgroups[3].T=new AffineTransform[2];
        groups[4].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[4].subgroups[3].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,2.0,51.96153);
        groups[4].subgroups[3].funddom=new MPoint[4];
        groups[4].subgroups[3].funddom[0]=new MPoint(1.0f,1.73205f);
        groups[4].subgroups[3].funddom[1]=new MPoint(3.0f,1.73205f);
        groups[4].subgroups[3].funddom[2]=new MPoint(2.0f,3.4641f);
        groups[4].subgroups[3].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[4].subgroups[4]=new Subgroup();
        groups[4].subgroups[4].group=groups[4];
        groups[4].subgroups[4].number=4;
        groups[4].subgroups[4].name="p6(4)p6";
        groups[4].subgroups[4].uvec=new float[] {4.0f,0.0f};
        groups[4].subgroups[4].vvec=new float[] {2.0f,3.4641f};
        groups[4].subgroups[4].T=new AffineTransform[6];
        groups[4].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[4].subgroups[4].T[1]=new AffineTransform(0.5,0.86603,-0.86603,0.5,-1.0,1.73205);
        groups[4].subgroups[4].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,-3.0,1.73205);
        groups[4].subgroups[4].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-4.0,-0.0);
        groups[4].subgroups[4].T[4]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-3.0,-1.73205);
        groups[4].subgroups[4].T[5]=new AffineTransform(0.5,-0.86603,0.86603,0.5,-1.0,-1.73205);
        groups[4].subgroups[4].funddom=new MPoint[5];
        groups[4].subgroups[4].funddom[0]=new MPoint(-2.0f,0.0f);
        groups[4].subgroups[4].funddom[1]=new MPoint(0.0f,0.0f);
        groups[4].subgroups[4].funddom[2]=new MPoint(0.0f,1.1547f);
        groups[4].subgroups[4].funddom[3]=new MPoint(-1.0f,1.73205f);
        groups[4].subgroups[4].funddom[4]=new MPoint(-2.0f,0.0f);
        groups[5]=new Group();groups[5].name="pgg";
        groups[5].subgroups = new Subgroup[4];
        groups[5].subgroups[0]=new Subgroup();
        groups[5].subgroups[0].group=groups[5];
        groups[5].subgroups[0].number=0;
        groups[5].subgroups[0].name="pgg";
        groups[5].subgroups[0].uvec=new float[] {4.0f,0.0f};
        groups[5].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[5].subgroups[0].T=new AffineTransform[4];
        groups[5].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[5].subgroups[0].T[1]=new AffineTransform(-1.0,0.0,0.0,-1.0,4.0,2.0);
        groups[5].subgroups[0].T[2]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,1.0);
        groups[5].subgroups[0].T[3]=new AffineTransform(-1.0,0.0,0.0,1.0,2.0,1.0);
        groups[5].subgroups[0].funddom=new MPoint[5];
        groups[5].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[0].funddom[1]=new MPoint(2.0f,0.0f);
        groups[5].subgroups[0].funddom[2]=new MPoint(2.0f,1.0f);
        groups[5].subgroups[0].funddom[3]=new MPoint(0.0f,1.0f);
        groups[5].subgroups[0].funddom[4]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[1]=new Subgroup();
        groups[5].subgroups[1].group=groups[5];
        groups[5].subgroups[1].number=1;
        groups[5].subgroups[1].name="p1(2)pgg";
        groups[5].subgroups[1].uvec=new float[] {4.0f,0.0f};
        groups[5].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[5].subgroups[1].T=new AffineTransform[1];
        groups[5].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[5].subgroups[1].funddom=new MPoint[5];
        groups[5].subgroups[1].funddom[0]=new MPoint(0.0f,2.0f);
        groups[5].subgroups[1].funddom[1]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[1].funddom[2]=new MPoint(4.0f,0.0f);
        groups[5].subgroups[1].funddom[3]=new MPoint(4.0f,2.0f);
        groups[5].subgroups[1].funddom[4]=new MPoint(0.0f,2.0f);
        groups[5].subgroups[2]=new Subgroup();
        groups[5].subgroups[2].group=groups[5];
        groups[5].subgroups[2].number=2;
        groups[5].subgroups[2].name="pgg<3>pgg";
        groups[5].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[5].subgroups[2].vvec=new float[] {0.0f,6.0f};
        groups[5].subgroups[2].T=new AffineTransform[4];
        groups[5].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[5].subgroups[2].T[1]=new AffineTransform(-1.0,0.0,0.0,-1.0,4.0,6.0);
        groups[5].subgroups[2].T[2]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,3.0);
        groups[5].subgroups[2].T[3]=new AffineTransform(-1.0,0.0,0.0,1.0,6.0,-3.0);
        groups[5].subgroups[2].funddom=new MPoint[5];
        groups[5].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[2].funddom[1]=new MPoint(2.0f,0.0f);
        groups[5].subgroups[2].funddom[2]=new MPoint(2.0f,3.0f);
        groups[5].subgroups[2].funddom[3]=new MPoint(0.0f,3.0f);
        groups[5].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[3]=new Subgroup();
        groups[5].subgroups[3].group=groups[5];
        groups[5].subgroups[3].number=3;
        groups[5].subgroups[3].name="p1(4)pgg";
        groups[5].subgroups[3].uvec=new float[] {4.0f,0.0f};
        groups[5].subgroups[3].vvec=new float[] {0.0f,2.0f};
        groups[5].subgroups[3].T=new AffineTransform[1];
        groups[5].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[5].subgroups[3].funddom=new MPoint[5];
        groups[5].subgroups[3].funddom[0]=new MPoint(0.0f,0.0f);
        groups[5].subgroups[3].funddom[1]=new MPoint(4.0f,0.0f);
        groups[5].subgroups[3].funddom[2]=new MPoint(4.0f,6.0f);
        groups[5].subgroups[3].funddom[3]=new MPoint(0.0f,6.0f);
        groups[5].subgroups[3].funddom[4]=new MPoint(0.0f,0.0f);
        groups[6]=new Group();groups[6].name="pg";
        groups[6].subgroups = new Subgroup[3];
        groups[6].subgroups[0]=new Subgroup();
        groups[6].subgroups[0].group=groups[6];
        groups[6].subgroups[0].number=0;
        groups[6].subgroups[0].name="pg";
        groups[6].subgroups[0].uvec=new float[] {4.0f,0.0f};
        groups[6].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[6].subgroups[0].T=new AffineTransform[2];
        groups[6].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[6].subgroups[0].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,2.0);
        groups[6].subgroups[0].funddom=new MPoint[3];
        groups[6].subgroups[0].funddom[0]=new MPoint(0.0f,2.0f);
        groups[6].subgroups[0].funddom[1]=new MPoint(0.0f,0.0f);
        groups[6].subgroups[0].funddom[2]=new MPoint(2.0f,0.0f);
        groups[6].subgroups[1]=new Subgroup();
        groups[6].subgroups[1].group=groups[6];
        groups[6].subgroups[1].number=1;
        groups[6].subgroups[1].name="pg(2)pg";
        groups[6].subgroups[1].uvec=new float[] {4.0f,0.0f};
        groups[6].subgroups[1].vvec=new float[] {0.0f,4.0f};
        groups[6].subgroups[1].T=new AffineTransform[2];
        groups[6].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[6].subgroups[1].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,4.0);
        groups[6].subgroups[1].funddom=new MPoint[3];
        groups[6].subgroups[1].funddom[0]=new MPoint(0.0f,4.0f);
        groups[6].subgroups[1].funddom[1]=new MPoint(0.0f,0.0f);
        groups[6].subgroups[1].funddom[2]=new MPoint(2.0f,0.0f);
        groups[6].subgroups[2]=new Subgroup();
        groups[6].subgroups[2].group=groups[6];
        groups[6].subgroups[2].number=2;
        groups[6].subgroups[2].name="p1(2)pg";
        groups[6].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[6].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[6].subgroups[2].T=new AffineTransform[1];
        groups[6].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[6].subgroups[2].funddom=new MPoint[5];
        groups[6].subgroups[2].funddom[0]=new MPoint(0.0f,2.0f);
        groups[6].subgroups[2].funddom[1]=new MPoint(0.0f,0.0f);
        groups[6].subgroups[2].funddom[2]=new MPoint(4.0f,0.0f);
        groups[6].subgroups[2].funddom[3]=new MPoint(4.0f,2.0f);
        groups[6].subgroups[2].funddom[4]=new MPoint(0.0f,2.0f);
        groups[7]=new Group();groups[7].name="cm";
        groups[7].subgroups = new Subgroup[4];
        groups[7].subgroups[0]=new Subgroup();
        groups[7].subgroups[0].group=groups[7];
        groups[7].subgroups[0].number=0;
        groups[7].subgroups[0].name="cm";
        groups[7].subgroups[0].uvec=new float[] {2.0f,-1.5f};
        groups[7].subgroups[0].vvec=new float[] {2.0f,1.5f};
        groups[7].subgroups[0].T=new AffineTransform[2];
        groups[7].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[7].subgroups[0].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,0.0);
        groups[7].subgroups[0].funddom=new MPoint[4];
        groups[7].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[0].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[7].subgroups[0].funddom[2]=new MPoint(4.0f,0.0f);
        groups[7].subgroups[0].funddom[3]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[1]=new Subgroup();
        groups[7].subgroups[1].group=groups[7];
        groups[7].subgroups[1].number=1;
        groups[7].subgroups[1].name="p1(2)cm";
        groups[7].subgroups[1].uvec=new float[] {2.0f,-1.5f};
        groups[7].subgroups[1].vvec=new float[] {2.0f,1.5f};
        groups[7].subgroups[1].T=new AffineTransform[1];
        groups[7].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[7].subgroups[1].funddom=new MPoint[5];
        groups[7].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[1].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[7].subgroups[1].funddom[2]=new MPoint(4.0f,0.0f);
        groups[7].subgroups[1].funddom[3]=new MPoint(2.0f,1.5f);
        groups[7].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[2]=new Subgroup();
        groups[7].subgroups[2].group=groups[7];
        groups[7].subgroups[2].number=2;
        groups[7].subgroups[2].name="pg(2)cm";
        groups[7].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[7].subgroups[2].vvec=new float[] {0.0f,3.0f};
        groups[7].subgroups[2].T=new AffineTransform[2];
        groups[7].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[7].subgroups[2].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,4.5);
        groups[7].subgroups[2].funddom=new MPoint[5];
        groups[7].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[2].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[7].subgroups[2].funddom[2]=new MPoint(4.0f,0.0f);
        groups[7].subgroups[2].funddom[3]=new MPoint(2.0f,1.5f);
        groups[7].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[7].subgroups[3]=new Subgroup();
        groups[7].subgroups[3].group=groups[7];
        groups[7].subgroups[3].number=3;
        groups[7].subgroups[3].name="pm(2)cm";
        groups[7].subgroups[3].uvec=new float[] {4.0f,0.0f};
        groups[7].subgroups[3].vvec=new float[] {0.0f,3.0f};
        groups[7].subgroups[3].T=new AffineTransform[2];
        groups[7].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[7].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,-6.0);
        groups[7].subgroups[3].funddom=new MPoint[5];
        groups[7].subgroups[3].funddom[0]=new MPoint(4.0f,-3.0f);
        groups[7].subgroups[3].funddom[1]=new MPoint(8.0f,-3.0f);
        groups[7].subgroups[3].funddom[2]=new MPoint(8.0f,-1.5f);
        groups[7].subgroups[3].funddom[3]=new MPoint(4.0f,-1.5f);
        groups[7].subgroups[3].funddom[4]=new MPoint(4.0f,-3.0f);
        groups[8]=new Group();groups[8].name="cmm";
        groups[8].subgroups = new Subgroup[9];
        groups[8].subgroups[0]=new Subgroup();
        groups[8].subgroups[0].group=groups[8];
        groups[8].subgroups[0].number=0;
        groups[8].subgroups[0].name="cmm";
        groups[8].subgroups[0].uvec=new float[] {2.0f,-1.5f};
        groups[8].subgroups[0].vvec=new float[] {2.0f,1.5f};
        groups[8].subgroups[0].T=new AffineTransform[4];
        groups[8].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[0].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,0.0);
        groups[8].subgroups[0].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[0].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,0.0,0.0);
        groups[8].subgroups[0].funddom=new MPoint[4];
        groups[8].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[0].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[8].subgroups[0].funddom[2]=new MPoint(2.0f,0.0f);
        groups[8].subgroups[0].funddom[3]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[1]=new Subgroup();
        groups[8].subgroups[1].group=groups[8];
        groups[8].subgroups[1].number=1;
        groups[8].subgroups[1].name="p1(4)cmm";
        groups[8].subgroups[1].uvec=new float[] {2.0f,-1.5f};
        groups[8].subgroups[1].vvec=new float[] {2.0f,1.5f};
        groups[8].subgroups[1].T=new AffineTransform[1];
        groups[8].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[1].funddom=new MPoint[5];
        groups[8].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[1].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[8].subgroups[1].funddom[2]=new MPoint(4.0f,0.0f);
        groups[8].subgroups[1].funddom[3]=new MPoint(2.0f,1.5f);
        groups[8].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[2]=new Subgroup();
        groups[8].subgroups[2].group=groups[8];
        groups[8].subgroups[2].number=2;
        groups[8].subgroups[2].name="p2(2)cmm";
        groups[8].subgroups[2].uvec=new float[] {2.0f,-1.5f};
        groups[8].subgroups[2].vvec=new float[] {2.0f,1.5f};
        groups[8].subgroups[2].T=new AffineTransform[2];
        groups[8].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[2].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-12.0,24.0);
        groups[8].subgroups[2].funddom=new MPoint[5];
        groups[8].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[2].funddom[1]=new MPoint(2.0f,-1.5f);
        groups[8].subgroups[2].funddom[2]=new MPoint(4.0f,0.0f);
        groups[8].subgroups[2].funddom[3]=new MPoint(2.0f,1.5f);
        groups[8].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[8].subgroups[3]=new Subgroup();
        groups[8].subgroups[3].group=groups[8];
        groups[8].subgroups[3].number=3;
        groups[8].subgroups[3].name="pg(4)cmm";
        groups[8].subgroups[3].uvec=new float[] {4.0f,0.0f};
        groups[8].subgroups[3].vvec=new float[] {0.0f,3.0f};
        groups[8].subgroups[3].T=new AffineTransform[2];
        groups[8].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,-7.5);
        groups[8].subgroups[3].funddom=new MPoint[5];
        groups[8].subgroups[3].funddom[0]=new MPoint(4.0f,2.25f);
        groups[8].subgroups[3].funddom[1]=new MPoint(6.0f,2.25f);
        groups[8].subgroups[3].funddom[2]=new MPoint(6.0f,5.25f);
        groups[8].subgroups[3].funddom[3]=new MPoint(4.0f,5.25f);
        groups[8].subgroups[3].funddom[4]=new MPoint(4.0f,2.25f);
        groups[8].subgroups[4]=new Subgroup();
        groups[8].subgroups[4].group=groups[8];
        groups[8].subgroups[4].number=4;
        groups[8].subgroups[4].name="pm(4)cmm";
        groups[8].subgroups[4].uvec=new float[] {4.0f,0.0f};
        groups[8].subgroups[4].vvec=new float[] {0.0f,3.0f};
        groups[8].subgroups[4].T=new AffineTransform[2];
        groups[8].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[4].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,9.0);
        groups[8].subgroups[4].funddom=new MPoint[5];
        groups[8].subgroups[4].funddom[0]=new MPoint(-2.0f,3.0f);
        groups[8].subgroups[4].funddom[1]=new MPoint(2.0f,3.0f);
        groups[8].subgroups[4].funddom[2]=new MPoint(2.0f,4.5f);
        groups[8].subgroups[4].funddom[3]=new MPoint(-2.0f,4.5f);
        groups[8].subgroups[4].funddom[4]=new MPoint(-2.0f,3.0f);
        groups[8].subgroups[5]=new Subgroup();
        groups[8].subgroups[5].group=groups[8];
        groups[8].subgroups[5].number=5;
        groups[8].subgroups[5].name="cm(2)cmm";
        groups[8].subgroups[5].uvec=new float[] {2.0f,-1.5f};
        groups[8].subgroups[5].vvec=new float[] {2.0f,1.5f};
        groups[8].subgroups[5].T=new AffineTransform[2];
        groups[8].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[5].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,9.0);
        groups[8].subgroups[5].funddom=new MPoint[4];
        groups[8].subgroups[5].funddom[0]=new MPoint(-2.0f,4.5f);
        groups[8].subgroups[5].funddom[1]=new MPoint(2.0f,4.5f);
        groups[8].subgroups[5].funddom[2]=new MPoint(0.0f,6.0f);
        groups[8].subgroups[5].funddom[3]=new MPoint(-2.0f,4.5f);
        groups[8].subgroups[6]=new Subgroup();
        groups[8].subgroups[6].group=groups[8];
        groups[8].subgroups[6].number=6;
        groups[8].subgroups[6].name="pgg(2)cmm";
        groups[8].subgroups[6].uvec=new float[] {4.0f,0.0f};
        groups[8].subgroups[6].vvec=new float[] {0.0f,3.0f};
        groups[8].subgroups[6].T=new AffineTransform[4];
        groups[8].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[6].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,-7.5);
        groups[8].subgroups[6].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,10.0,1.5);
        groups[8].subgroups[6].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,12.0,-9.0);
        groups[8].subgroups[6].funddom=new MPoint[5];
        groups[8].subgroups[6].funddom[0]=new MPoint(4.0f,2.25f);
        groups[8].subgroups[6].funddom[1]=new MPoint(6.0f,2.25f);
        groups[8].subgroups[6].funddom[2]=new MPoint(6.0f,5.25f);
        groups[8].subgroups[6].funddom[3]=new MPoint(4.0f,5.25f);
        groups[8].subgroups[6].funddom[4]=new MPoint(4.0f,2.25f);
        groups[8].subgroups[7]=new Subgroup();
        groups[8].subgroups[7].group=groups[8];
        groups[8].subgroups[7].number=7;
        groups[8].subgroups[7].name="pmm(2)cmm";
        groups[8].subgroups[7].uvec=new float[] {4.0f,0.0f};
        groups[8].subgroups[7].vvec=new float[] {0.0f,3.0f};
        groups[8].subgroups[7].T=new AffineTransform[4];
        groups[8].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[7].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,-3.0);
        groups[8].subgroups[7].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,8.0,0.0);
        groups[8].subgroups[7].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,8.0,-3.0);
        groups[8].subgroups[7].funddom=new MPoint[5];
        groups[8].subgroups[7].funddom[0]=new MPoint(2.0f,-3.0f);
        groups[8].subgroups[7].funddom[1]=new MPoint(4.0f,-3.0f);
        groups[8].subgroups[7].funddom[2]=new MPoint(4.0f,-1.5f);
        groups[8].subgroups[7].funddom[3]=new MPoint(2.0f,-1.5f);
        groups[8].subgroups[7].funddom[4]=new MPoint(2.0f,-3.0f);
        groups[8].subgroups[8]=new Subgroup();
        groups[8].subgroups[8].group=groups[8];
        groups[8].subgroups[8].number=8;
        groups[8].subgroups[8].name="cmm(4)cmm";
        groups[8].subgroups[8].uvec=new float[] {4.0f,-3.0f};
        groups[8].subgroups[8].vvec=new float[] {4.0f,3.0f};
        groups[8].subgroups[8].T=new AffineTransform[4];
        groups[8].subgroups[8].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[8].subgroups[8].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,9.0);
        groups[8].subgroups[8].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,4.0,0.0);
        groups[8].subgroups[8].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,4.0,9.0);
        groups[8].subgroups[8].funddom=new MPoint[4];
        groups[8].subgroups[8].funddom[0]=new MPoint(-2.0f,4.5f);
        groups[8].subgroups[8].funddom[1]=new MPoint(2.0f,1.5f);
        groups[8].subgroups[8].funddom[2]=new MPoint(2.0f,4.5f);
        groups[8].subgroups[8].funddom[3]=new MPoint(-2.0f,4.5f);
        groups[9]=new Group();groups[9].name="pmg";
        groups[9].subgroups = new Subgroup[8];
        groups[9].subgroups[0]=new Subgroup();
        groups[9].subgroups[0].group=groups[9];
        groups[9].subgroups[0].number=0;
        groups[9].subgroups[0].name="pmg";
        groups[9].subgroups[0].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[0].T=new AffineTransform[4];
        groups[9].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[0].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,1.0);
        groups[9].subgroups[0].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,4.0,2.0);
        groups[9].subgroups[0].T[3]=new AffineTransform(-1.0,-0.0,-0.0,1.0,4.0,1.0);
        groups[9].subgroups[0].funddom=new MPoint[5];
        groups[9].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[0].funddom[1]=new MPoint(4.0f,0.0f);
        groups[9].subgroups[0].funddom[2]=new MPoint(4.0f,0.5f);
        groups[9].subgroups[0].funddom[3]=new MPoint(0.0f,0.5f);
        groups[9].subgroups[0].funddom[4]=new MPoint(0.0f,1.0f);
        groups[9].subgroups[1]=new Subgroup();
        groups[9].subgroups[1].group=groups[9];
        groups[9].subgroups[1].number=1;
        groups[9].subgroups[1].name="p1(4)pmg";
        groups[9].subgroups[1].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[1].T=new AffineTransform[1];
        groups[9].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[1].funddom=new MPoint[5];
        groups[9].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[1].funddom[1]=new MPoint(4.0f,0.0f);
        groups[9].subgroups[1].funddom[2]=new MPoint(4.0f,2.0f);
        groups[9].subgroups[1].funddom[3]=new MPoint(0.0f,2.0f);
        groups[9].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[2]=new Subgroup();
        groups[9].subgroups[2].group=groups[9];
        groups[9].subgroups[2].number=2;
        groups[9].subgroups[2].name="p2(2)pmg";
        groups[9].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[2].T=new AffineTransform[2];
        groups[9].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[2].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,4.0,28.0);
        groups[9].subgroups[2].funddom=new MPoint[5];
        groups[9].subgroups[2].funddom[0]=new MPoint(0.0f,17.0f);
        groups[9].subgroups[2].funddom[1]=new MPoint(4.0f,17.0f);
        groups[9].subgroups[2].funddom[2]=new MPoint(4.0f,18.0f);
        groups[9].subgroups[2].funddom[3]=new MPoint(0.0f,18.0f);
        groups[9].subgroups[2].funddom[4]=new MPoint(0.0f,17.0f);
        groups[9].subgroups[3]=new Subgroup();
        groups[9].subgroups[3].group=groups[9];
        groups[9].subgroups[3].number=3;
        groups[9].subgroups[3].name="pg(2)pmg";
        groups[9].subgroups[3].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[3].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[3].T=new AffineTransform[2];
        groups[9].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[3].T[1]=new AffineTransform(-1.0,0.0,0.0,1.0,0.0,1.0);
        groups[9].subgroups[3].funddom=new MPoint[5];
        groups[9].subgroups[3].funddom[0]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[3].funddom[1]=new MPoint(4.0f,0.0f);
        groups[9].subgroups[3].funddom[2]=new MPoint(4.0f,0.5f);
        groups[9].subgroups[3].funddom[3]=new MPoint(0.0f,0.5f);
        groups[9].subgroups[3].funddom[4]=new MPoint(0.0f,1.0f);
        groups[9].subgroups[4]=new Subgroup();
        groups[9].subgroups[4].group=groups[9];
        groups[9].subgroups[4].number=4;
        groups[9].subgroups[4].name="pm(2)pmg";
        groups[9].subgroups[4].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[4].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[4].T=new AffineTransform[2];
        groups[9].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[4].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,15.0);
        groups[9].subgroups[4].funddom=new MPoint[5];
        groups[9].subgroups[4].funddom[0]=new MPoint(4.0f,6.5f);
        groups[9].subgroups[4].funddom[1]=new MPoint(8.0f,6.5f);
        groups[9].subgroups[4].funddom[2]=new MPoint(8.0f,7.5f);
        groups[9].subgroups[4].funddom[3]=new MPoint(4.0f,7.5f);
        groups[9].subgroups[4].funddom[4]=new MPoint(4.0f,6.5f);
        groups[9].subgroups[5]=new Subgroup();
        groups[9].subgroups[5].group=groups[9];
        groups[9].subgroups[5].number=5;
        groups[9].subgroups[5].name="cm<4>pmg";
        groups[9].subgroups[5].uvec=new float[] {4.0f,-2.0f};
        groups[9].subgroups[5].vvec=new float[] {4.0f,2.0f};
        groups[9].subgroups[5].T=new AffineTransform[2];
        groups[9].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[5].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,25.0);
        groups[9].subgroups[5].funddom=new MPoint[4];
        groups[9].subgroups[5].funddom[0]=new MPoint(12.0f,12.5f);
        groups[9].subgroups[5].funddom[1]=new MPoint(20.0f,12.5f);
        groups[9].subgroups[5].funddom[2]=new MPoint(16.0f,14.5f);
        groups[9].subgroups[5].funddom[3]=new MPoint(12.0f,12.5f);
        groups[9].subgroups[6]=new Subgroup();
        groups[9].subgroups[6].group=groups[9];
        groups[9].subgroups[6].number=6;
        groups[9].subgroups[6].name="pgg(2)pmg";
        groups[9].subgroups[6].uvec=new float[] {8.0f,0.0f};
        groups[9].subgroups[6].vvec=new float[] {0.0f,2.0f};
        groups[9].subgroups[6].T=new AffineTransform[4];
        groups[9].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,8.0);
        groups[9].subgroups[6].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,4.0,19.0);
        groups[9].subgroups[6].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,12.0,1.0);
        groups[9].subgroups[6].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,16.0,18.0);
        groups[9].subgroups[6].funddom=new MPoint[5];
        groups[9].subgroups[6].funddom[0]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[6].funddom[1]=new MPoint(4.0f,0.0f);
        groups[9].subgroups[6].funddom[2]=new MPoint(4.0f,2.0f);
        groups[9].subgroups[6].funddom[3]=new MPoint(0.0f,2.0f);
        groups[9].subgroups[6].funddom[4]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[7]=new Subgroup();
        groups[9].subgroups[7].group=groups[9];
        groups[9].subgroups[7].number=7;
        groups[9].subgroups[7].name="pmg(3)pmg";
        groups[9].subgroups[7].uvec=new float[] {4.0f,0.0f};
        groups[9].subgroups[7].vvec=new float[] {0.0f,6.0f};
        groups[9].subgroups[7].T=new AffineTransform[4];
        groups[9].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[9].subgroups[7].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,13.0);
        groups[9].subgroups[7].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,12.0,3.0);
        groups[9].subgroups[7].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,12.0,10.0);
        groups[9].subgroups[7].funddom=new MPoint[5];
        groups[9].subgroups[7].funddom[0]=new MPoint(0.0f,0.0f);
        groups[9].subgroups[7].funddom[1]=new MPoint(4.0f,0.0f);
        groups[9].subgroups[7].funddom[2]=new MPoint(4.0f,2.0f);
        groups[9].subgroups[7].funddom[3]=new MPoint(0.0f,2.0f);
        groups[9].subgroups[7].funddom[4]=new MPoint(0.0f,0.0f);
        groups[10]=new Group();groups[10].name="pm";
        groups[10].subgroups = new Subgroup[5];
        groups[10].subgroups[0]=new Subgroup();
        groups[10].subgroups[0].group=groups[10];
        groups[10].subgroups[0].number=0;
        groups[10].subgroups[0].name="pm";
        groups[10].subgroups[0].uvec=new float[] {4.0f,0.0f};
        groups[10].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[10].subgroups[0].T=new AffineTransform[2];
        groups[10].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[10].subgroups[0].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,0.0);
        groups[10].subgroups[0].funddom=new MPoint[4];
        groups[10].subgroups[0].funddom[0]=new MPoint(4.0f,1.0f);
        groups[10].subgroups[0].funddom[1]=new MPoint(0.0f,1.0f);
        groups[10].subgroups[0].funddom[2]=new MPoint(0.0f,0.0f);
        groups[10].subgroups[0].funddom[3]=new MPoint(4.0f,0.0f);
        groups[10].subgroups[1]=new Subgroup();
        groups[10].subgroups[1].group=groups[10];
        groups[10].subgroups[1].number=1;
        groups[10].subgroups[1].name="p1(2)pm";
        groups[10].subgroups[1].uvec=new float[] {4.0f,0.0f};
        groups[10].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[10].subgroups[1].T=new AffineTransform[1];
        groups[10].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[10].subgroups[1].funddom=new MPoint[5];
        groups[10].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[10].subgroups[1].funddom[1]=new MPoint(4.0f,0.0f);
        groups[10].subgroups[1].funddom[2]=new MPoint(4.0f,2.0f);
        groups[10].subgroups[1].funddom[3]=new MPoint(0.0f,2.0f);
        groups[10].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[10].subgroups[2]=new Subgroup();
        groups[10].subgroups[2].group=groups[10];
        groups[10].subgroups[2].number=2;
        groups[10].subgroups[2].name="pg(2)pm";
        groups[10].subgroups[2].uvec=new float[] {8.0f,0.0f};
        groups[10].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[10].subgroups[2].T=new AffineTransform[2];
        groups[10].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[10].subgroups[2].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,4.0,16.0);
        groups[10].subgroups[2].funddom=new MPoint[5];
        groups[10].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[10].subgroups[2].funddom[1]=new MPoint(4.0f,0.0f);
        groups[10].subgroups[2].funddom[2]=new MPoint(4.0f,2.0f);
        groups[10].subgroups[2].funddom[3]=new MPoint(0.0f,2.0f);
        groups[10].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[10].subgroups[3]=new Subgroup();
        groups[10].subgroups[3].group=groups[10];
        groups[10].subgroups[3].number=3;
        groups[10].subgroups[3].name="pm(2)pm";
        groups[10].subgroups[3].uvec=new float[] {4.0f,0.0f};
        groups[10].subgroups[3].vvec=new float[] {0.0f,4.0f};
        groups[10].subgroups[3].T=new AffineTransform[2];
        groups[10].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[10].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,14.0);
        groups[10].subgroups[3].funddom=new MPoint[5];
        groups[10].subgroups[3].funddom[0]=new MPoint(8.0f,3.0f);
        groups[10].subgroups[3].funddom[1]=new MPoint(12.0f,3.0f);
        groups[10].subgroups[3].funddom[2]=new MPoint(12.0f,5.0f);
        groups[10].subgroups[3].funddom[3]=new MPoint(8.0f,5.0f);
        groups[10].subgroups[3].funddom[4]=new MPoint(8.0f,3.0f);
        groups[10].subgroups[4]=new Subgroup();
        groups[10].subgroups[4].group=groups[10];
        groups[10].subgroups[4].number=4;
        groups[10].subgroups[4].name="cm(2)pm";
        groups[10].subgroups[4].uvec=new float[] {4.0f,-2.0f};
        groups[10].subgroups[4].vvec=new float[] {4.0f,2.0f};
        groups[10].subgroups[4].T=new AffineTransform[2];
        groups[10].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[10].subgroups[4].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,14.0);
        groups[10].subgroups[4].funddom=new MPoint[4];
        groups[10].subgroups[4].funddom[0]=new MPoint(-10.0f,7.0f);
        groups[10].subgroups[4].funddom[1]=new MPoint(-2.0f,7.0f);
        groups[10].subgroups[4].funddom[2]=new MPoint(-6.0f,9.0f);
        groups[10].subgroups[4].funddom[3]=new MPoint(-10.0f,7.0f);
        groups[11]=new Group();groups[11].name="pmm";
        groups[11].subgroups = new Subgroup[6];
        groups[11].subgroups[0]=new Subgroup();
        groups[11].subgroups[0].group=groups[11];
        groups[11].subgroups[0].number=0;
        groups[11].subgroups[0].name="pmm";
        groups[11].subgroups[0].uvec=new float[] {4.0f,0.0f};
        groups[11].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[11].subgroups[0].T=new AffineTransform[4];
        groups[11].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[0].T[1]=new AffineTransform(-1.0,0.0,0.0,1.0,4.0,0.0);
        groups[11].subgroups[0].T[2]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,2.0);
        groups[11].subgroups[0].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,4.0,2.0);
        groups[11].subgroups[0].funddom=new MPoint[5];
        groups[11].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[0].funddom[1]=new MPoint(2.0f,0.0f);
        groups[11].subgroups[0].funddom[2]=new MPoint(2.0f,1.0f);
        groups[11].subgroups[0].funddom[3]=new MPoint(0.0f,1.0f);
        groups[11].subgroups[0].funddom[4]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[1]=new Subgroup();
        groups[11].subgroups[1].group=groups[11];
        groups[11].subgroups[1].number=1;
        groups[11].subgroups[1].name="p1(4)pmm";
        groups[11].subgroups[1].uvec=new float[] {4.0f,0.0f};
        groups[11].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[11].subgroups[1].T=new AffineTransform[1];
        groups[11].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[1].funddom=new MPoint[5];
        groups[11].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[1].funddom[1]=new MPoint(4.0f,0.0f);
        groups[11].subgroups[1].funddom[2]=new MPoint(4.0f,2.0f);
        groups[11].subgroups[1].funddom[3]=new MPoint(0.0f,2.0f);
        groups[11].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[2]=new Subgroup();
        groups[11].subgroups[2].group=groups[11];
        groups[11].subgroups[2].number=2;
        groups[11].subgroups[2].name="p2(2)pmm";
        groups[11].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[11].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[11].subgroups[2].T=new AffineTransform[2];
        groups[11].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[2].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,12.0,22.0);
        groups[11].subgroups[2].funddom=new MPoint[5];
        groups[11].subgroups[2].funddom[0]=new MPoint(12.0f,16.0f);
        groups[11].subgroups[2].funddom[1]=new MPoint(14.0f,16.0f);
        groups[11].subgroups[2].funddom[2]=new MPoint(14.0f,18.0f);
        groups[11].subgroups[2].funddom[3]=new MPoint(12.0f,18.0f);
        groups[11].subgroups[2].funddom[4]=new MPoint(12.0f,16.0f);
        groups[11].subgroups[3]=new Subgroup();
        groups[11].subgroups[3].group=groups[11];
        groups[11].subgroups[3].number=3;
        groups[11].subgroups[3].name="pg(4)pmm";
        groups[11].subgroups[3].uvec=new float[] {8.0f,0.0f};
        groups[11].subgroups[3].vvec=new float[] {0.0f,2.0f};
        groups[11].subgroups[3].T=new AffineTransform[2];
        groups[11].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,4.0,18.0);
        groups[11].subgroups[3].funddom=new MPoint[5];
        groups[11].subgroups[3].funddom[0]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[3].funddom[1]=new MPoint(4.0f,0.0f);
        groups[11].subgroups[3].funddom[2]=new MPoint(4.0f,2.0f);
        groups[11].subgroups[3].funddom[3]=new MPoint(0.0f,2.0f);
        groups[11].subgroups[3].funddom[4]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[4]=new Subgroup();
        groups[11].subgroups[4].group=groups[11];
        groups[11].subgroups[4].number=4;
        groups[11].subgroups[4].name="pm(2)pmm";
        groups[11].subgroups[4].uvec=new float[] {4.0f,0.0f};
        groups[11].subgroups[4].vvec=new float[] {0.0f,2.0f};
        groups[11].subgroups[4].T=new AffineTransform[2];
        groups[11].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[4].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,18.0);
        groups[11].subgroups[4].funddom=new MPoint[5];
        groups[11].subgroups[4].funddom[0]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[4].funddom[1]=new MPoint(4.0f,0.0f);
        groups[11].subgroups[4].funddom[2]=new MPoint(4.0f,1.0f);
        groups[11].subgroups[4].funddom[3]=new MPoint(0.0f,1.0f);
        groups[11].subgroups[4].funddom[4]=new MPoint(0.0f,0.0f);
        groups[11].subgroups[5]=new Subgroup();
        groups[11].subgroups[5].group=groups[11];
        groups[11].subgroups[5].number=5;
        groups[11].subgroups[5].name="cm(4)pmm";
        groups[11].subgroups[5].uvec=new float[] {4.0f,-2.0f};
        groups[11].subgroups[5].vvec=new float[] {4.0f,2.0f};
        groups[11].subgroups[5].T=new AffineTransform[2];
        groups[11].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[11].subgroups[5].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,10.0);
        groups[11].subgroups[5].funddom=new MPoint[4];
        groups[11].subgroups[5].funddom[0]=new MPoint(0.0f,5.0f);
        groups[11].subgroups[5].funddom[1]=new MPoint(8.0f,5.0f);
        groups[11].subgroups[5].funddom[2]=new MPoint(4.0f,7.0f);
        groups[11].subgroups[5].funddom[3]=new MPoint(0.0f,5.0f);
        groups[12]=new Group();groups[12].name="p4g";
        groups[12].subgroups = new Subgroup[12];
        groups[12].subgroups[0]=new Subgroup();
        groups[12].subgroups[0].group=groups[12];
        groups[12].subgroups[0].number=0;
        groups[12].subgroups[0].name="p4g";
        groups[12].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[0].T=new AffineTransform[8];
        groups[12].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[0].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,2.0,0.0);
        groups[12].subgroups[0].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,2.0,2.0);
        groups[12].subgroups[0].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-0.0,2.0);
        groups[12].subgroups[0].T[4]=new AffineTransform(-0.0,-1.0,-1.0,0.0,1.0,1.0);
        groups[12].subgroups[0].T[5]=new AffineTransform(1.0,0.0,0.0,-1.0,1.0,1.0);
        groups[12].subgroups[0].T[6]=new AffineTransform(-0.0,1.0,1.0,0.0,1.0,1.0);
        groups[12].subgroups[0].T[7]=new AffineTransform(-1.0,-0.0,-0.0,1.0,1.0,1.0);
        groups[12].subgroups[0].funddom=new MPoint[4];
        groups[12].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[0].funddom[1]=new MPoint(1.0f,0.0f);
        groups[12].subgroups[0].funddom[2]=new MPoint(0.0f,1.0f);
        groups[12].subgroups[0].funddom[3]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[1]=new Subgroup();
        groups[12].subgroups[1].group=groups[12];
        groups[12].subgroups[1].number=1;
        groups[12].subgroups[1].name="p1(8)p4g";
        groups[12].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[1].T=new AffineTransform[1];
        groups[12].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[1].funddom=new MPoint[5];
        groups[12].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[12].subgroups[1].funddom[2]=new MPoint(2.0f,2.0f);
        groups[12].subgroups[1].funddom[3]=new MPoint(0.0f,2.0f);
        groups[12].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[2]=new Subgroup();
        groups[12].subgroups[2].group=groups[12];
        groups[12].subgroups[2].number=2;
        groups[12].subgroups[2].name="p2(4)p4g";
        groups[12].subgroups[2].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[2].T=new AffineTransform[2];
        groups[12].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[2].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,-3.0,3.0);
        groups[12].subgroups[2].funddom=new MPoint[4];
        groups[12].subgroups[2].funddom[0]=new MPoint(-3.0f,2.0f);
        groups[12].subgroups[2].funddom[1]=new MPoint(-1.0f,2.0f);
        groups[12].subgroups[2].funddom[2]=new MPoint(-1.0f,4.0f);
        groups[12].subgroups[2].funddom[3]=new MPoint(-3.0f,2.0f);
        groups[12].subgroups[3]=new Subgroup();
        groups[12].subgroups[3].group=groups[12];
        groups[12].subgroups[3].number=3;
        groups[12].subgroups[3].name="pg<4>p4g";
        groups[12].subgroups[3].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[3].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[3].T=new AffineTransform[2];
        groups[12].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,1.0,27.0);
        groups[12].subgroups[3].funddom=new MPoint[5];
        groups[12].subgroups[3].funddom[0]=new MPoint(4.5f,13.5f);
        groups[12].subgroups[3].funddom[1]=new MPoint(5.5f,13.5f);
        groups[12].subgroups[3].funddom[2]=new MPoint(5.5f,15.5f);
        groups[12].subgroups[3].funddom[3]=new MPoint(4.5f,15.5f);
        groups[12].subgroups[3].funddom[4]=new MPoint(4.5f,13.5f);
        groups[12].subgroups[4]=new Subgroup();
        groups[12].subgroups[4].group=groups[12];
        groups[12].subgroups[4].number=4;
        groups[12].subgroups[4].name="pm<8>p4g";
        groups[12].subgroups[4].uvec=new float[] {2.0f,2.0f};
        groups[12].subgroups[4].vvec=new float[] {-2.0f,2.0f};
        groups[12].subgroups[4].T=new AffineTransform[2];
        groups[12].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[4].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,5.0,-5.0);
        groups[12].subgroups[4].funddom=new MPoint[5];
        groups[12].subgroups[4].funddom[0]=new MPoint(10.0f,5.0f);
        groups[12].subgroups[4].funddom[1]=new MPoint(12.0f,7.0f);
        groups[12].subgroups[4].funddom[2]=new MPoint(11.0f,8.0f);
        groups[12].subgroups[4].funddom[3]=new MPoint(9.0f,6.0f);
        groups[12].subgroups[4].funddom[4]=new MPoint(10.0f,5.0f);
        groups[12].subgroups[5]=new Subgroup();
        groups[12].subgroups[5].group=groups[12];
        groups[12].subgroups[5].number=5;
        groups[12].subgroups[5].name="cm<4>p4g";
        groups[12].subgroups[5].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[5].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[5].T=new AffineTransform[2];
        groups[12].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[5].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,1.0,-1.0);
        groups[12].subgroups[5].funddom=new MPoint[4];
        groups[12].subgroups[5].funddom[0]=new MPoint(12.0f,11.0f);
        groups[12].subgroups[5].funddom[1]=new MPoint(14.0f,11.0f);
        groups[12].subgroups[5].funddom[2]=new MPoint(14.0f,13.0f);
        groups[12].subgroups[5].funddom[3]=new MPoint(12.0f,11.0f);
        groups[12].subgroups[6]=new Subgroup();
        groups[12].subgroups[6].group=groups[12];
        groups[12].subgroups[6].number=6;
        groups[12].subgroups[6].name="pgg(4)p4g";
        groups[12].subgroups[6].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[6].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[6].T=new AffineTransform[4];
        groups[12].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[6].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,1.0,9.0);
        groups[12].subgroups[6].T[2]=new AffineTransform(-1.0,0.0,0.0,1.0,-5.0,1.0);
        groups[12].subgroups[6].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-4.0,8.0);
        groups[12].subgroups[6].funddom=new MPoint[5];
        groups[12].subgroups[6].funddom[0]=new MPoint(-2.0f,7.0f);
        groups[12].subgroups[6].funddom[1]=new MPoint(-1.0f,7.0f);
        groups[12].subgroups[6].funddom[2]=new MPoint(-1.0f,8.0f);
        groups[12].subgroups[6].funddom[3]=new MPoint(-2.0f,8.0f);
        groups[12].subgroups[6].funddom[4]=new MPoint(-2.0f,7.0f);
        groups[12].subgroups[7]=new Subgroup();
        groups[12].subgroups[7].group=groups[12];
        groups[12].subgroups[7].number=7;
        groups[12].subgroups[7].name="pmg<4>p4g";
        groups[12].subgroups[7].uvec=new float[] {2.0f,2.0f};
        groups[12].subgroups[7].vvec=new float[] {-2.0f,2.0f};
        groups[12].subgroups[7].T=new AffineTransform[4];
        groups[12].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[7].T[1]=new AffineTransform(0.0,-1.0,-1.0,0.0,7.0,7.0);
        groups[12].subgroups[7].T[2]=new AffineTransform(0.0,1.0,1.0,0.0,13.0,-11.0);
        groups[12].subgroups[7].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,18.0,-6.0);
        groups[12].subgroups[7].funddom=new MPoint[5];
        groups[12].subgroups[7].funddom[0]=new MPoint(4.5f,-3.5f);
        groups[12].subgroups[7].funddom[1]=new MPoint(5.5f,-2.5f);
        groups[12].subgroups[7].funddom[2]=new MPoint(4.5f,-1.5f);
        groups[12].subgroups[7].funddom[3]=new MPoint(3.5f,-2.5f);
        groups[12].subgroups[7].funddom[4]=new MPoint(4.5f,-3.5f);
        groups[12].subgroups[8]=new Subgroup();
        groups[12].subgroups[8].group=groups[12];
        groups[12].subgroups[8].number=8;
        groups[12].subgroups[8].name="pmm(2)p4g";
        groups[12].subgroups[8].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[8].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[8].T=new AffineTransform[4];
        groups[12].subgroups[8].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[8].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,-3.0,3.0);
        groups[12].subgroups[8].T[2]=new AffineTransform(0.0,-1.0,-1.0,0.0,7.0,7.0);
        groups[12].subgroups[8].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,4.0,10.0);
        groups[12].subgroups[8].funddom=new MPoint[4];
        groups[12].subgroups[8].funddom[0]=new MPoint(-1.0f,2.0f);
        groups[12].subgroups[8].funddom[1]=new MPoint(1.0f,2.0f);
        groups[12].subgroups[8].funddom[2]=new MPoint(0.0f,3.0f);
        groups[12].subgroups[8].funddom[3]=new MPoint(-1.0f,2.0f);
        groups[12].subgroups[9]=new Subgroup();
        groups[12].subgroups[9].group=groups[12];
        groups[12].subgroups[9].number=9;
        groups[12].subgroups[9].name="cmm(2)p4g";
        groups[12].subgroups[9].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[9].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[9].T=new AffineTransform[4];
        groups[12].subgroups[9].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[9].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,-3.0,3.0);
        groups[12].subgroups[9].T[2]=new AffineTransform(0.0,-1.0,-1.0,0.0,9.0,9.0);
        groups[12].subgroups[9].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,6.0,12.0);
        groups[12].subgroups[9].funddom=new MPoint[5];
        groups[12].subgroups[9].funddom[0]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[9].funddom[1]=new MPoint(2.0f,0.0f);
        groups[12].subgroups[9].funddom[2]=new MPoint(2.0f,2.0f);
        groups[12].subgroups[9].funddom[3]=new MPoint(0.0f,2.0f);
        groups[12].subgroups[9].funddom[4]=new MPoint(0.0f,0.0f);
        groups[12].subgroups[10]=new Subgroup();
        groups[12].subgroups[10].group=groups[12];
        groups[12].subgroups[10].number=10;
        groups[12].subgroups[10].name="p4(2)p4g";
        groups[12].subgroups[10].uvec=new float[] {2.0f,0.0f};
        groups[12].subgroups[10].vvec=new float[] {0.0f,2.0f};
        groups[12].subgroups[10].T=new AffineTransform[4];
        groups[12].subgroups[10].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[10].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,4.0,16.0);
        groups[12].subgroups[10].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-12.0,20.0);
        groups[12].subgroups[10].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-16.0,4.0);
        groups[12].subgroups[10].funddom=new MPoint[5];
        groups[12].subgroups[10].funddom[0]=new MPoint(-6.0f,10.0f);
        groups[12].subgroups[10].funddom[1]=new MPoint(-5.0f,10.0f);
        groups[12].subgroups[10].funddom[2]=new MPoint(-5.0f,11.0f);
        groups[12].subgroups[10].funddom[3]=new MPoint(-6.0f,11.0f);
        groups[12].subgroups[10].funddom[4]=new MPoint(-6.0f,10.0f);
        groups[12].subgroups[11]=new Subgroup();
        groups[12].subgroups[11].group=groups[12];
        groups[12].subgroups[11].number=11;
        groups[12].subgroups[11].name="p4g<9>p4g";
        groups[12].subgroups[11].uvec=new float[] {6.0f,0.0f};
        groups[12].subgroups[11].vvec=new float[] {0.0f,6.0f};
        groups[12].subgroups[11].T=new AffineTransform[8];
        groups[12].subgroups[11].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[12].subgroups[11].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,-0.0,4.0);
        groups[12].subgroups[11].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-4.0,4.0);
        groups[12].subgroups[11].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-4.0,-0.0);
        groups[12].subgroups[11].T[4]=new AffineTransform(-0.0,-1.0,-1.0,-0.0,3.0,3.0);
        groups[12].subgroups[11].T[5]=new AffineTransform(-1.0,-0.0,0.0,1.0,-1.0,3.0);
        groups[12].subgroups[11].T[6]=new AffineTransform(0.0,1.0,1.0,0.0,-1.0,7.0);
        groups[12].subgroups[11].T[7]=new AffineTransform(1.0,0.0,-0.0,-1.0,3.0,7.0);
        groups[12].subgroups[11].funddom=new MPoint[4];
        groups[12].subgroups[11].funddom[0]=new MPoint(-2.0f,-1.0f);
        groups[12].subgroups[11].funddom[1]=new MPoint(1.0f,-1.0f);
        groups[12].subgroups[11].funddom[2]=new MPoint(1.0f,2.0f);
        groups[12].subgroups[11].funddom[3]=new MPoint(-2.0f,-1.0f);
        groups[13]=new Group();groups[13].name="p4m";
        groups[13].subgroups = new Subgroup[13];
        groups[13].subgroups[0]=new Subgroup();
        groups[13].subgroups[0].group=groups[13];
        groups[13].subgroups[0].number=0;
        groups[13].subgroups[0].name="p4m";
        groups[13].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[0].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[0].T=new AffineTransform[8];
        groups[13].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[0].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,2.0,0.0);
        groups[13].subgroups[0].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,2.0,2.0);
        groups[13].subgroups[0].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-0.0,2.0);
        groups[13].subgroups[0].T[4]=new AffineTransform(-0.0,1.0,1.0,0.0,0.0,0.0);
        groups[13].subgroups[0].T[5]=new AffineTransform(1.0,0.0,0.0,-1.0,-0.0,2.0);
        groups[13].subgroups[0].T[6]=new AffineTransform(-0.0,-1.0,-1.0,0.0,2.0,2.0);
        groups[13].subgroups[0].T[7]=new AffineTransform(-1.0,0.0,0.0,1.0,2.0,-0.0);
        groups[13].subgroups[0].funddom=new MPoint[4];
        groups[13].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[0].funddom[1]=new MPoint(1.0f,0.0f);
        groups[13].subgroups[0].funddom[2]=new MPoint(1.0f,1.0f);
        groups[13].subgroups[0].funddom[3]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[1]=new Subgroup();
        groups[13].subgroups[1].group=groups[13];
        groups[13].subgroups[1].number=1;
        groups[13].subgroups[1].name="p1(8)p4m";
        groups[13].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[1].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[1].T=new AffineTransform[1];
        groups[13].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[1].funddom=new MPoint[5];
        groups[13].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[13].subgroups[1].funddom[2]=new MPoint(2.0f,2.0f);
        groups[13].subgroups[1].funddom[3]=new MPoint(0.0f,2.0f);
        groups[13].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[2]=new Subgroup();
        groups[13].subgroups[2].group=groups[13];
        groups[13].subgroups[2].number=2;
        groups[13].subgroups[2].name="pg<8>p4m";
        groups[13].subgroups[2].uvec=new float[] {4.0f,0.0f};
        groups[13].subgroups[2].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[2].T=new AffineTransform[2];
        groups[13].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[2].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,2.0,14.0);
        groups[13].subgroups[2].funddom=new MPoint[5];
        groups[13].subgroups[2].funddom[0]=new MPoint(2.0f,2.0f);
        groups[13].subgroups[2].funddom[1]=new MPoint(4.0f,2.0f);
        groups[13].subgroups[2].funddom[2]=new MPoint(4.0f,4.0f);
        groups[13].subgroups[2].funddom[3]=new MPoint(2.0f,4.0f);
        groups[13].subgroups[2].funddom[4]=new MPoint(2.0f,2.0f);
        groups[13].subgroups[3]=new Subgroup();
        groups[13].subgroups[3].group=groups[13];
        groups[13].subgroups[3].number=3;
        groups[13].subgroups[3].name="pm<4>p4m";
        groups[13].subgroups[3].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[3].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[3].T=new AffineTransform[2];
        groups[13].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,16.0);
        groups[13].subgroups[3].funddom=new MPoint[5];
        groups[13].subgroups[3].funddom[0]=new MPoint(4.0f,12.0f);
        groups[13].subgroups[3].funddom[1]=new MPoint(6.0f,12.0f);
        groups[13].subgroups[3].funddom[2]=new MPoint(6.0f,13.0f);
        groups[13].subgroups[3].funddom[3]=new MPoint(4.0f,13.0f);
        groups[13].subgroups[3].funddom[4]=new MPoint(4.0f,12.0f);
        groups[13].subgroups[4]=new Subgroup();
        groups[13].subgroups[4].group=groups[13];
        groups[13].subgroups[4].number=4;
        groups[13].subgroups[4].name="cm<4>p4m";
        groups[13].subgroups[4].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[4].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[4].T=new AffineTransform[2];
        groups[13].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[4].T[1]=new AffineTransform(0.0,-1.0,-1.0,0.0,20.0,20.0);
        groups[13].subgroups[4].funddom=new MPoint[4];
        groups[13].subgroups[4].funddom[0]=new MPoint(6.0f,12.0f);
        groups[13].subgroups[4].funddom[1]=new MPoint(8.0f,12.0f);
        groups[13].subgroups[4].funddom[2]=new MPoint(6.0f,14.0f);
        groups[13].subgroups[4].funddom[3]=new MPoint(6.0f,12.0f);
        groups[13].subgroups[5]=new Subgroup();
        groups[13].subgroups[5].group=groups[13];
        groups[13].subgroups[5].number=5;
        groups[13].subgroups[5].name="pgg(4)p4m";
        groups[13].subgroups[5].uvec=new float[] {2.0f,2.0f};
        groups[13].subgroups[5].vvec=new float[] {-2.0f,2.0f};
        groups[13].subgroups[5].T=new AffineTransform[4];
        groups[13].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[5].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,4.0,-2.0);
        groups[13].subgroups[5].T[2]=new AffineTransform(0.0,-1.0,-1.0,0.0,2.0,4.0);
        groups[13].subgroups[5].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,8.0,0.0);
        groups[13].subgroups[5].funddom=new MPoint[4];
        groups[13].subgroups[5].funddom[0]=new MPoint(6.0f,1.0f);
        groups[13].subgroups[5].funddom[1]=new MPoint(6.0f,3.0f);
        groups[13].subgroups[5].funddom[2]=new MPoint(4.0f,3.0f);
        groups[13].subgroups[5].funddom[3]=new MPoint(6.0f,1.0f);
        groups[13].subgroups[6]=new Subgroup();
        groups[13].subgroups[6].group=groups[13];
        groups[13].subgroups[6].number=6;
        groups[13].subgroups[6].name="pmg<4>p4m";
        groups[13].subgroups[6].uvec=new float[] {2.0f,2.0f};
        groups[13].subgroups[6].vvec=new float[] {-2.0f,2.0f};
        groups[13].subgroups[6].T=new AffineTransform[4];
        groups[13].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[6].T[1]=new AffineTransform(0.0,1.0,1.0,0.0,0.0,0.0);
        groups[13].subgroups[6].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,2.0,4.0);
        groups[13].subgroups[6].T[3]=new AffineTransform(-0.0,-1.0,-1.0,0.0,4.0,2.0);
        groups[13].subgroups[6].funddom=new MPoint[5];
        groups[13].subgroups[6].funddom[0]=new MPoint(1.0f,0.0f);
        groups[13].subgroups[6].funddom[1]=new MPoint(3.0f,2.0f);
        groups[13].subgroups[6].funddom[2]=new MPoint(2.5f,2.5f);
        groups[13].subgroups[6].funddom[3]=new MPoint(0.5f,0.5f);
        groups[13].subgroups[6].funddom[4]=new MPoint(1.0f,0.0f);
        groups[13].subgroups[7]=new Subgroup();
        groups[13].subgroups[7].group=groups[13];
        groups[13].subgroups[7].number=7;
        groups[13].subgroups[7].name="pmm(2)p4m";
        groups[13].subgroups[7].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[7].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[7].T=new AffineTransform[4];
        groups[13].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[7].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,2.0);
        groups[13].subgroups[7].T[2]=new AffineTransform(-1.0,-0.0,-0.0,1.0,0.0,0.0);
        groups[13].subgroups[7].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,0.0,2.0);
        groups[13].subgroups[7].funddom=new MPoint[5];
        groups[13].subgroups[7].funddom[0]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[7].funddom[1]=new MPoint(1.0f,0.0f);
        groups[13].subgroups[7].funddom[2]=new MPoint(1.0f,1.0f);
        groups[13].subgroups[7].funddom[3]=new MPoint(0.0f,1.0f);
        groups[13].subgroups[7].funddom[4]=new MPoint(0.0f,0.0f);
        groups[13].subgroups[8]=new Subgroup();
        groups[13].subgroups[8].group=groups[13];
        groups[13].subgroups[8].number=8;
        groups[13].subgroups[8].name="cmm(2)p4m";
        groups[13].subgroups[8].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[8].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[8].T=new AffineTransform[4];
        groups[13].subgroups[8].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[8].T[1]=new AffineTransform(0.0,-1.0,-1.0,0.0,8.0,8.0);
        groups[13].subgroups[8].T[2]=new AffineTransform(0.0,1.0,1.0,0.0,-6.0,6.0);
        groups[13].subgroups[8].T[3]=new AffineTransform(-1.0,0.0,0.0,-1.0,2.0,14.0);
        groups[13].subgroups[8].funddom=new MPoint[4];
        groups[13].subgroups[8].funddom[0]=new MPoint(0.0f,6.0f);
        groups[13].subgroups[8].funddom[1]=new MPoint(1.0f,7.0f);
        groups[13].subgroups[8].funddom[2]=new MPoint(0.0f,8.0f);
        groups[13].subgroups[8].funddom[3]=new MPoint(0.0f,6.0f);
        groups[13].subgroups[9]=new Subgroup();
        groups[13].subgroups[9].group=groups[13];
        groups[13].subgroups[9].number=9;
        groups[13].subgroups[9].name="p4(2)p4m";
        groups[13].subgroups[9].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[9].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[9].T=new AffineTransform[4];
        groups[13].subgroups[9].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[9].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,20.0,14.0);
        groups[13].subgroups[9].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,6.0,34.0);
        groups[13].subgroups[9].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-14.0,20.0);
        groups[13].subgroups[9].funddom=new MPoint[5];
        groups[13].subgroups[9].funddom[0]=new MPoint(4.0f,21.0f);
        groups[13].subgroups[9].funddom[1]=new MPoint(5.0f,21.0f);
        groups[13].subgroups[9].funddom[2]=new MPoint(5.0f,22.0f);
        groups[13].subgroups[9].funddom[3]=new MPoint(4.0f,22.0f);
        groups[13].subgroups[9].funddom[4]=new MPoint(4.0f,21.0f);
        groups[13].subgroups[10]=new Subgroup();
        groups[13].subgroups[10].group=groups[13];
        groups[13].subgroups[10].number=10;
        groups[13].subgroups[10].name="p4g(2)p4m";
        groups[13].subgroups[10].uvec=new float[] {2.0f,2.0f};
        groups[13].subgroups[10].vvec=new float[] {-2.0f,2.0f};
        groups[13].subgroups[10].T=new AffineTransform[8];
        groups[13].subgroups[10].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[10].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,-24.0,-30.0);
        groups[13].subgroups[10].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,6.0,-54.0);
        groups[13].subgroups[10].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,30.0,-24.0);
        groups[13].subgroups[10].T[4]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,-56.0);
        groups[13].subgroups[10].T[5]=new AffineTransform(-0.0,1.0,1.0,0.0,32.0,-30.0);
        groups[13].subgroups[10].T[6]=new AffineTransform(-1.0,-0.0,-0.0,1.0,6.0,2.0);
        groups[13].subgroups[10].T[7]=new AffineTransform(0.0,-1.0,-1.0,-0.0,-26.0,-24.0);
        groups[13].subgroups[10].funddom=new MPoint[4];
        groups[13].subgroups[10].funddom[0]=new MPoint(2.0f,-28.0f);
        groups[13].subgroups[10].funddom[1]=new MPoint(3.0f,-29.0f);
        groups[13].subgroups[10].funddom[2]=new MPoint(4.0f,-28.0f);
        groups[13].subgroups[10].funddom[3]=new MPoint(2.0f,-28.0f);
        groups[13].subgroups[11]=new Subgroup();
        groups[13].subgroups[11].group=groups[13];
        groups[13].subgroups[11].number=11;
        groups[13].subgroups[11].name="p4m(2)p4m";
        groups[13].subgroups[11].uvec=new float[] {2.0f,2.0f};
        groups[13].subgroups[11].vvec=new float[] {-2.0f,2.0f};
        groups[13].subgroups[11].T=new AffineTransform[8];
        groups[13].subgroups[11].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[11].T[1]=new AffineTransform(-0.0,1.0,-1.0,-0.0,-8.0,12.0);
        groups[13].subgroups[11].T[2]=new AffineTransform(-1.0,-0.0,0.0,-1.0,-20.0,4.0);
        groups[13].subgroups[11].T[3]=new AffineTransform(0.0,-1.0,1.0,0.0,-12.0,-8.0);
        groups[13].subgroups[11].T[4]=new AffineTransform(-1.0,-0.0,-0.0,1.0,-20.0,0.0);
        groups[13].subgroups[11].T[5]=new AffineTransform(0.0,-1.0,-1.0,-0.0,-8.0,-8.0);
        groups[13].subgroups[11].T[6]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,4.0);
        groups[13].subgroups[11].T[7]=new AffineTransform(-0.0,1.0,1.0,0.0,-12.0,12.0);
        groups[13].subgroups[11].funddom=new MPoint[4];
        groups[13].subgroups[11].funddom[0]=new MPoint(-10.0f,0.0f);
        groups[13].subgroups[11].funddom[1]=new MPoint(-9.0f,1.0f);
        groups[13].subgroups[11].funddom[2]=new MPoint(-10.0f,2.0f);
        groups[13].subgroups[11].funddom[3]=new MPoint(-10.0f,0.0f);
        groups[13].subgroups[12]=new Subgroup();
        groups[13].subgroups[12].group=groups[13];
        groups[13].subgroups[12].number=12;
        groups[13].subgroups[12].name="p2(4)p4m";
        groups[13].subgroups[12].uvec=new float[] {2.0f,0.0f};
        groups[13].subgroups[12].vvec=new float[] {0.0f,2.0f};
        groups[13].subgroups[12].T=new AffineTransform[2];
        groups[13].subgroups[12].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[13].subgroups[12].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,6.0,8.0);
        groups[13].subgroups[12].funddom=new MPoint[5];
        groups[13].subgroups[12].funddom[0]=new MPoint(-1.0f,3.0f);
        groups[13].subgroups[12].funddom[1]=new MPoint(0.0f,3.0f);
        groups[13].subgroups[12].funddom[2]=new MPoint(0.0f,4.0f);
        groups[13].subgroups[12].funddom[3]=new MPoint(-1.0f,4.0f);
        groups[13].subgroups[12].funddom[4]=new MPoint(-1.0f,3.0f);
        groups[14]=new Group();groups[14].name="p6m";
        groups[14].subgroups = new Subgroup[15];
        groups[14].subgroups[0]=new Subgroup();
        groups[14].subgroups[0].group=groups[14];
        groups[14].subgroups[0].number=0;
        groups[14].subgroups[0].name="p6m";
        groups[14].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[0].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[0].T=new AffineTransform[12];
        groups[14].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[0].T[1]=new AffineTransform(0.5,0.86603,-0.86603,0.5,0.0,0.0);
        groups[14].subgroups[0].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[0].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,0.0,0.0);
        groups[14].subgroups[0].T[4]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[0].T[5]=new AffineTransform(0.5,-0.86603,0.86603,0.5,0.0,0.0);
        groups[14].subgroups[0].T[6]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[0].T[7]=new AffineTransform(-0.5,0.86603,0.86603,0.5,0.0,0.0);
        groups[14].subgroups[0].T[8]=new AffineTransform(-1.0,-0.0,-0.0,1.0,0.0,0.0);
        groups[14].subgroups[0].T[9]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,0.0,0.0);
        groups[14].subgroups[0].T[10]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[0].T[11]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,0.0);
        groups[14].subgroups[0].funddom=new MPoint[4];
        groups[14].subgroups[0].funddom[0]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[0].funddom[1]=new MPoint(1.0f,0.0f);
        groups[14].subgroups[0].funddom[2]=new MPoint(1.0f,0.57735f);
        groups[14].subgroups[0].funddom[3]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[1]=new Subgroup();
        groups[14].subgroups[1].group=groups[14];
        groups[14].subgroups[1].number=1;
        groups[14].subgroups[1].name="p1(12)p6m";
        groups[14].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[1].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[1].T=new AffineTransform[1];
        groups[14].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[1].funddom=new MPoint[5];
        groups[14].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[14].subgroups[1].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[14].subgroups[1].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[14].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[2]=new Subgroup();
        groups[14].subgroups[2].group=groups[14];
        groups[14].subgroups[2].number=2;
        groups[14].subgroups[2].name="p2(6)p6m";
        groups[14].subgroups[2].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[2].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[2].T=new AffineTransform[2];
        groups[14].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[2].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,7.0,8.66025);
        groups[14].subgroups[2].funddom=new MPoint[4];
        groups[14].subgroups[2].funddom[0]=new MPoint(1.0f,1.73205f);
        groups[14].subgroups[2].funddom[1]=new MPoint(3.0f,1.73205f);
        groups[14].subgroups[2].funddom[2]=new MPoint(2.0f,3.4641f);
        groups[14].subgroups[2].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[14].subgroups[3]=new Subgroup();
        groups[14].subgroups[3].group=groups[14];
        groups[14].subgroups[3].number=3;
        groups[14].subgroups[3].name="pg<12>p6m";
        groups[14].subgroups[3].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[3].vvec=new float[] {-1.0f,1.73205f};
        groups[14].subgroups[3].T=new AffineTransform[2];
        groups[14].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[3].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,1.0,1.73205);
        groups[14].subgroups[3].funddom=new MPoint[5];
        groups[14].subgroups[3].funddom[0]=new MPoint(3.0f,1.73205f);
        groups[14].subgroups[3].funddom[1]=new MPoint(5.0f,1.73205f);
        groups[14].subgroups[3].funddom[2]=new MPoint(6.0f,3.4641f);
        groups[14].subgroups[3].funddom[3]=new MPoint(4.0f,3.4641f);
        groups[14].subgroups[3].funddom[4]=new MPoint(3.0f,1.73205f);
        groups[14].subgroups[4]=new Subgroup();
        groups[14].subgroups[4].group=groups[14];
        groups[14].subgroups[4].number=4;
        groups[14].subgroups[4].name="pm<12>p6m";
        groups[14].subgroups[4].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[4].vvec=new float[] {-1.0f,1.73205f};
        groups[14].subgroups[4].T=new AffineTransform[2];
        groups[14].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[4].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,-1.0,1.73205);
        groups[14].subgroups[4].funddom=new MPoint[5];
        groups[14].subgroups[4].funddom[0]=new MPoint(4.0f,3.4641f);
        groups[14].subgroups[4].funddom[1]=new MPoint(7.0f,5.19615f);
        groups[14].subgroups[4].funddom[2]=new MPoint(6.5f,6.06217f);
        groups[14].subgroups[4].funddom[3]=new MPoint(3.5f,4.33012f);
        groups[14].subgroups[4].funddom[4]=new MPoint(4.0f,3.4641f);
        groups[14].subgroups[5]=new Subgroup();
        groups[14].subgroups[5].group=groups[14];
        groups[14].subgroups[5].number=5;
        groups[14].subgroups[5].name="cm<6>p6m";
        groups[14].subgroups[5].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[5].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[5].T=new AffineTransform[2];
        groups[14].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[5].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[5].funddom=new MPoint[4];
        groups[14].subgroups[5].funddom[0]=new MPoint(6.0f,3.4641f);
        groups[14].subgroups[5].funddom[1]=new MPoint(8.0f,3.4641f);
        groups[14].subgroups[5].funddom[2]=new MPoint(9.0f,5.19615f);
        groups[14].subgroups[5].funddom[3]=new MPoint(6.0f,3.4641f);
        groups[14].subgroups[6]=new Subgroup();
        groups[14].subgroups[6].group=groups[14];
        groups[14].subgroups[6].number=6;
        groups[14].subgroups[6].name="pgg<6>p6m";
        groups[14].subgroups[6].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[6].vvec=new float[] {-1.0f,1.73205f};
        groups[14].subgroups[6].T=new AffineTransform[4];
        groups[14].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[6].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,4.0,-3.4641);
        groups[14].subgroups[6].T[2]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,7.0,5.19615);
        groups[14].subgroups[6].T[3]=new AffineTransform(-1.0,0.0,-0.0,-1.0,12.0,-1.0E-5);
        groups[14].subgroups[6].funddom=new MPoint[5];
        groups[14].subgroups[6].funddom[0]=new MPoint(2.75f,-3.03109f);
        groups[14].subgroups[6].funddom[1]=new MPoint(4.25f,-2.16506f);
        groups[14].subgroups[6].funddom[2]=new MPoint(3.75f,-1.29904f);
        groups[14].subgroups[6].funddom[3]=new MPoint(2.25f,-2.16506f);
        groups[14].subgroups[6].funddom[4]=new MPoint(2.75f,-3.03109f);
        groups[14].subgroups[7]=new Subgroup();
        groups[14].subgroups[7].group=groups[14];
        groups[14].subgroups[7].number=7;
        groups[14].subgroups[7].name="pmg<6>p6m";
        groups[14].subgroups[7].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[7].vvec=new float[] {-1.0f,1.73205f};
        groups[14].subgroups[7].T=new AffineTransform[4];
        groups[14].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[7].T[1]=new AffineTransform(-1.0,-0.0,0.0,-1.0,8.0,-3.4641);
        groups[14].subgroups[7].T[2]=new AffineTransform(0.5,0.86603,0.86603,-0.5,4.0,-6.9282);
        groups[14].subgroups[7].T[3]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,4.0,3.4641);
        groups[14].subgroups[7].funddom=new MPoint[4];
        groups[14].subgroups[7].funddom[0]=new MPoint(2.0f,-3.4641f);
        groups[14].subgroups[7].funddom[1]=new MPoint(5.0f,-1.73205f);
        groups[14].subgroups[7].funddom[2]=new MPoint(3.0f,-1.73205f);
        groups[14].subgroups[7].funddom[3]=new MPoint(2.0f,-3.4641f);
        groups[14].subgroups[8]=new Subgroup();
        groups[14].subgroups[8].group=groups[14];
        groups[14].subgroups[8].number=8;
        groups[14].subgroups[8].name="pmm<6>p6m";
        groups[14].subgroups[8].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[8].vvec=new float[] {-1.0f,1.73205f};
        groups[14].subgroups[8].T=new AffineTransform[4];
        groups[14].subgroups[8].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[8].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,2.0,-3.4641);
        groups[14].subgroups[8].T[2]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,0.0,0.0);
        groups[14].subgroups[8].T[3]=new AffineTransform(-1.0,0.0,-0.0,-1.0,2.0,-3.4641);
        groups[14].subgroups[8].funddom=new MPoint[5];
        groups[14].subgroups[8].funddom[0]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[8].funddom[1]=new MPoint(1.5f,0.86602f);
        groups[14].subgroups[8].funddom[2]=new MPoint(1.0f,1.73205f);
        groups[14].subgroups[8].funddom[3]=new MPoint(-0.5f,0.86602f);
        groups[14].subgroups[8].funddom[4]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[9]=new Subgroup();
        groups[14].subgroups[9].group=groups[14];
        groups[14].subgroups[9].number=9;
        groups[14].subgroups[9].name="cmm<3>p6m";
        groups[14].subgroups[9].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[9].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[9].T=new AffineTransform[4];
        groups[14].subgroups[9].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[9].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[9].T[2]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,3.0,1.73205);
        groups[14].subgroups[9].T[3]=new AffineTransform(-1.0,0.0,-0.0,-1.0,3.0,1.73205);
        groups[14].subgroups[9].funddom=new MPoint[4];
        groups[14].subgroups[9].funddom[0]=new MPoint(-1.0f,1.73205f);
        groups[14].subgroups[9].funddom[1]=new MPoint(0.5f,2.59807f);
        groups[14].subgroups[9].funddom[2]=new MPoint(0.0f,3.4641f);
        groups[14].subgroups[9].funddom[3]=new MPoint(-1.0f,1.73205f);
        groups[14].subgroups[10]=new Subgroup();
        groups[14].subgroups[10].group=groups[14];
        groups[14].subgroups[10].number=10;
        groups[14].subgroups[10].name="p3(4)p6m";
        groups[14].subgroups[10].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[10].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[10].T=new AffineTransform[3];
        groups[14].subgroups[10].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[10].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,8.0,6.9282);
        groups[14].subgroups[10].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-2.0,10.3923);
        groups[14].subgroups[10].funddom=new MPoint[5];
        groups[14].subgroups[10].funddom[0]=new MPoint(0.0f,6.9282f);
        groups[14].subgroups[10].funddom[1]=new MPoint(1.0f,7.50555f);
        groups[14].subgroups[10].funddom[2]=new MPoint(1.0f,8.66025f);
        groups[14].subgroups[10].funddom[3]=new MPoint(0.0f,8.0829f);
        groups[14].subgroups[10].funddom[4]=new MPoint(0.0f,6.9282f);
        groups[14].subgroups[11]=new Subgroup();
        groups[14].subgroups[11].group=groups[14];
        groups[14].subgroups[11].number=11;
        groups[14].subgroups[11].name="p3m1(2)p6m";
        groups[14].subgroups[11].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[11].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[11].T=new AffineTransform[6];
        groups[14].subgroups[11].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[11].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,11.0,12.12435);
        groups[14].subgroups[11].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-5.0,15.58845);
        groups[14].subgroups[11].T[3]=new AffineTransform(0.5,0.86603,0.86603,-0.5,-7.0,12.12435);
        groups[14].subgroups[11].T[4]=new AffineTransform(-1.0,0.0,0.0,1.0,4.0,-0.0);
        groups[14].subgroups[11].T[5]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,9.0,15.58845);
        groups[14].subgroups[11].funddom=new MPoint[4];
        groups[14].subgroups[11].funddom[0]=new MPoint(3.0f,12.12435f);
        groups[14].subgroups[11].funddom[1]=new MPoint(4.0f,12.7017f);
        groups[14].subgroups[11].funddom[2]=new MPoint(3.0f,13.27905f);
        groups[14].subgroups[11].funddom[3]=new MPoint(3.0f,12.12435f);
        groups[14].subgroups[12]=new Subgroup();
        groups[14].subgroups[12].group=groups[14];
        groups[14].subgroups[12].number=12;
        groups[14].subgroups[12].name="p31m(2)p6m";
        groups[14].subgroups[12].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[12].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[12].T=new AffineTransform[6];
        groups[14].subgroups[12].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[12].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,19.99999,17.3205);
        groups[14].subgroups[12].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-4.99999,25.98075);
        groups[14].subgroups[12].T[3]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,20.99999,12.12436);
        groups[14].subgroups[12].T[4]=new AffineTransform(1.0,0.0,0.0,-1.0,-1.00001,29.44485);
        groups[14].subgroups[12].T[5]=new AffineTransform(-0.5,0.86603,0.86603,0.5,-4.99999,1.73204);
        groups[14].subgroups[12].funddom=new MPoint[5];
        groups[14].subgroups[12].funddom[0]=new MPoint(3.0f,15.58845f);
        groups[14].subgroups[12].funddom[1]=new MPoint(3.5f,16.45448f);
        groups[14].subgroups[12].funddom[2]=new MPoint(3.0f,16.74315f);
        groups[14].subgroups[12].funddom[3]=new MPoint(2.5f,16.45448f);
        groups[14].subgroups[12].funddom[4]=new MPoint(3.0f,15.58845f);
        groups[14].subgroups[13]=new Subgroup();
        groups[14].subgroups[13].group=groups[14];
        groups[14].subgroups[13].number=13;
        groups[14].subgroups[13].name="p6(2)p6m";
        groups[14].subgroups[13].uvec=new float[] {2.0f,0.0f};
        groups[14].subgroups[13].vvec=new float[] {1.0f,1.73205f};
        groups[14].subgroups[13].T=new AffineTransform[6];
        groups[14].subgroups[13].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[13].T[1]=new AffineTransform(0.5,0.86603,-0.86603,0.5,20.99999,5.19615);
        groups[14].subgroups[13].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,26.99999,25.98075);
        groups[14].subgroups[13].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,12.0,41.5692);
        groups[14].subgroups[13].T[4]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-9.0,36.37305);
        groups[14].subgroups[13].T[5]=new AffineTransform(0.5,-0.86603,0.86603,0.5,-14.99999,15.58845);
        groups[14].subgroups[13].funddom=new MPoint[4];
        groups[14].subgroups[13].funddom[0]=new MPoint(4.0f,20.7846f);
        groups[14].subgroups[13].funddom[1]=new MPoint(5.0f,21.36195f);
        groups[14].subgroups[13].funddom[2]=new MPoint(4.0f,21.9393f);
        groups[14].subgroups[13].funddom[3]=new MPoint(4.0f,20.7846f);
        groups[14].subgroups[14]=new Subgroup();
        groups[14].subgroups[14].group=groups[14];
        groups[14].subgroups[14].number=14;
        groups[14].subgroups[14].name="p6m<3>p6m";
        groups[14].subgroups[14].uvec=new float[] {3.0f,1.73205f};
        groups[14].subgroups[14].vvec=new float[] {0.0f,3.4641f};
        groups[14].subgroups[14].T=new AffineTransform[12];
        groups[14].subgroups[14].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[14].T[1]=new AffineTransform(0.5,0.86603,-0.86603,0.5,0.0,0.0);
        groups[14].subgroups[14].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[14].T[3]=new AffineTransform(-1.0,-0.0,0.0,-1.0,0.0,0.0);
        groups[14].subgroups[14].T[4]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[14].T[5]=new AffineTransform(0.5,-0.86603,0.86603,0.5,0.0,0.0);
        groups[14].subgroups[14].T[6]=new AffineTransform(-0.5,0.86603,0.86603,0.5,0.0,0.0);
        groups[14].subgroups[14].T[7]=new AffineTransform(-1.0,0.0,0.0,1.0,0.0,0.0);
        groups[14].subgroups[14].T[8]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,0.0,0.0);
        groups[14].subgroups[14].T[9]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[14].T[10]=new AffineTransform(1.0,-0.0,-0.0,-1.0,0.0,0.0);
        groups[14].subgroups[14].T[11]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[14].subgroups[14].funddom=new MPoint[4];
        groups[14].subgroups[14].funddom[0]=new MPoint(0.0f,0.0f);
        groups[14].subgroups[14].funddom[1]=new MPoint(1.5f,0.86602f);
        groups[14].subgroups[14].funddom[2]=new MPoint(1.0f,1.73205f);
        groups[14].subgroups[14].funddom[3]=new MPoint(0.0f,0.0f);
        groups[15]=new Group();groups[15].name="p31m";
        groups[15].subgroups = new Subgroup[8];
        groups[15].subgroups[0]=new Subgroup();
        groups[15].subgroups[0].group=groups[15];
        groups[15].subgroups[0].number=0;
        groups[15].subgroups[0].name="p31m";
        groups[15].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[0].vvec=new float[] {1.0f,1.73205f};
        groups[15].subgroups[0].T=new AffineTransform[6];
        groups[15].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[0].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,2.0,0.0);
        groups[15].subgroups[0].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,1.0,1.73205);
        groups[15].subgroups[0].T[3]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,3.0,1.73205);
        groups[15].subgroups[0].T[4]=new AffineTransform(1.0,0.0,0.0,-1.0,-1.0,1.73205);
        groups[15].subgroups[0].T[5]=new AffineTransform(-0.5,0.86603,0.86603,0.5,1.0,-1.73205);
        groups[15].subgroups[0].funddom=new MPoint[4];
        groups[15].subgroups[0].funddom[0]=new MPoint(1.0f,0.57735f);
        groups[15].subgroups[0].funddom[1]=new MPoint(2.0f,0.0f);
        groups[15].subgroups[0].funddom[2]=new MPoint(1.0f,1.73205f);
        groups[15].subgroups[0].funddom[3]=new MPoint(1.0f,0.57735f);
        groups[15].subgroups[1]=new Subgroup();
        groups[15].subgroups[1].group=groups[15];
        groups[15].subgroups[1].number=1;
        groups[15].subgroups[1].name="p1(6)p31m";
        groups[15].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[1].vvec=new float[] {1.0f,1.73205f};
        groups[15].subgroups[1].T=new AffineTransform[1];
        groups[15].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[1].funddom=new MPoint[5];
        groups[15].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[15].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[15].subgroups[1].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[15].subgroups[1].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[15].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[15].subgroups[2]=new Subgroup();
        groups[15].subgroups[2].group=groups[15];
        groups[15].subgroups[2].number=2;
        groups[15].subgroups[2].name="pg<6>p31m";
        groups[15].subgroups[2].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[2].vvec=new float[] {2.0f,3.4641f};
        groups[15].subgroups[2].T=new AffineTransform[2];
        groups[15].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[2].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,1.0,1.73205);
        groups[15].subgroups[2].funddom=new MPoint[5];
        groups[15].subgroups[2].funddom[0]=new MPoint(-1.0f,1.73205f);
        groups[15].subgroups[2].funddom[1]=new MPoint(0.0f,0.0f);
        groups[15].subgroups[2].funddom[2]=new MPoint(1.0f,1.73205f);
        groups[15].subgroups[2].funddom[3]=new MPoint(0.0f,3.4641f);
        groups[15].subgroups[2].funddom[4]=new MPoint(-1.0f,1.73205f);
        groups[15].subgroups[3]=new Subgroup();
        groups[15].subgroups[3].group=groups[15];
        groups[15].subgroups[3].number=3;
        groups[15].subgroups[3].name="pm<6>p31m";
        groups[15].subgroups[3].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[3].vvec=new float[] {2.0f,3.4641f};
        groups[15].subgroups[3].T=new AffineTransform[2];
        groups[15].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[3].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,3.4641);
        groups[15].subgroups[3].funddom=new MPoint[5];
        groups[15].subgroups[3].funddom[0]=new MPoint(-3.0f,1.73205f);
        groups[15].subgroups[3].funddom[1]=new MPoint(-1.0f,1.73205f);
        groups[15].subgroups[3].funddom[2]=new MPoint(0.0f,3.4641f);
        groups[15].subgroups[3].funddom[3]=new MPoint(-2.0f,3.4641f);
        groups[15].subgroups[3].funddom[4]=new MPoint(-3.0f,1.73205f);
        groups[15].subgroups[4]=new Subgroup();
        groups[15].subgroups[4].group=groups[15];
        groups[15].subgroups[4].number=4;
        groups[15].subgroups[4].name="cm<3>p31m";
        groups[15].subgroups[4].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[4].vvec=new float[] {1.0f,1.73205f};
        groups[15].subgroups[4].T=new AffineTransform[2];
        groups[15].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[4].T[1]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,3.4641);
        groups[15].subgroups[4].funddom=new MPoint[4];
        groups[15].subgroups[4].funddom[0]=new MPoint(-3.0f,1.73205f);
        groups[15].subgroups[4].funddom[1]=new MPoint(-1.0f,1.73205f);
        groups[15].subgroups[4].funddom[2]=new MPoint(-2.0f,3.4641f);
        groups[15].subgroups[4].funddom[3]=new MPoint(-3.0f,1.73205f);
        groups[15].subgroups[5]=new Subgroup();
        groups[15].subgroups[5].group=groups[15];
        groups[15].subgroups[5].number=5;
        groups[15].subgroups[5].name="p3(2)p31m";
        groups[15].subgroups[5].uvec=new float[] {2.0f,0.0f};
        groups[15].subgroups[5].vvec=new float[] {1.0f,1.73205f};
        groups[15].subgroups[5].T=new AffineTransform[3];
        groups[15].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[5].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,3.0,1.73205);
        groups[15].subgroups[5].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,0.0,3.4641);
        groups[15].subgroups[5].funddom=new MPoint[5];
        groups[15].subgroups[5].funddom[0]=new MPoint(1.0f,1.73205f);
        groups[15].subgroups[5].funddom[1]=new MPoint(2.0f,1.1547f);
        groups[15].subgroups[5].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[15].subgroups[5].funddom[3]=new MPoint(2.0f,2.3094f);
        groups[15].subgroups[5].funddom[4]=new MPoint(1.0f,1.73205f);
        groups[15].subgroups[6]=new Subgroup();
        groups[15].subgroups[6].group=groups[15];
        groups[15].subgroups[6].number=6;
        groups[15].subgroups[6].name="p3m1<3>p31m";
        groups[15].subgroups[6].uvec=new float[] {3.0f,-1.73205f};
        groups[15].subgroups[6].vvec=new float[] {3.0f,1.73205f};
        groups[15].subgroups[6].T=new AffineTransform[6];
        groups[15].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[6].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,9.0,5.19615);
        groups[15].subgroups[6].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,0.0,10.3923);
        groups[15].subgroups[6].T[3]=new AffineTransform(1.0,0.0,0.0,-1.0,0.0,10.3923);
        groups[15].subgroups[6].T[4]=new AffineTransform(-0.5,0.86603,0.86603,0.5,0.0,-0.0);
        groups[15].subgroups[6].T[5]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,9.0,5.19615);
        groups[15].subgroups[6].funddom=new MPoint[4];
        groups[15].subgroups[6].funddom[0]=new MPoint(-1.0f,5.19615f);
        groups[15].subgroups[6].funddom[1]=new MPoint(1.0f,5.19615f);
        groups[15].subgroups[6].funddom[2]=new MPoint(0.0f,6.9282f);
        groups[15].subgroups[6].funddom[3]=new MPoint(-1.0f,5.19615f);
        groups[15].subgroups[7]=new Subgroup();
        groups[15].subgroups[7].group=groups[15];
        groups[15].subgroups[7].number=7;
        groups[15].subgroups[7].name="p31m<4>p31m";
        groups[15].subgroups[7].uvec=new float[] {4.0f,0.0f};
        groups[15].subgroups[7].vvec=new float[] {2.0f,3.4641f};
        groups[15].subgroups[7].T=new AffineTransform[6];
        groups[15].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[15].subgroups[7].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,4.0,-0.0);
        groups[15].subgroups[7].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,2.0,3.4641);
        groups[15].subgroups[7].T[3]=new AffineTransform(-0.5,-0.86603,-0.86603,0.5,6.0,3.4641);
        groups[15].subgroups[7].T[4]=new AffineTransform(1.0,0.0,0.0,-1.0,-2.0,3.4641);
        groups[15].subgroups[7].T[5]=new AffineTransform(-0.5,0.86603,0.86603,0.5,2.0,-3.4641);
        groups[15].subgroups[7].funddom=new MPoint[4];
        groups[15].subgroups[7].funddom[0]=new MPoint(2.0f,1.1547f);
        groups[15].subgroups[7].funddom[1]=new MPoint(4.0f,0.0f);
        groups[15].subgroups[7].funddom[2]=new MPoint(2.0f,3.4641f);
        groups[15].subgroups[7].funddom[3]=new MPoint(2.0f,1.1547f);
        groups[16]=new Group();groups[16].name="p3m1";
        groups[16].subgroups = new Subgroup[8];
        groups[16].subgroups[0]=new Subgroup();
        groups[16].subgroups[0].group=groups[16];
        groups[16].subgroups[0].number=0;
        groups[16].subgroups[0].name="p3m1";
        groups[16].subgroups[0].uvec=new float[] {2.0f,0.0f};
        groups[16].subgroups[0].vvec=new float[] {1.0f,1.73205f};
        groups[16].subgroups[0].T=new AffineTransform[6];
        groups[16].subgroups[0].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[0].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[16].subgroups[0].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,2.0,-0.0);
        groups[16].subgroups[0].T[3]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,1.0,1.73205);
        groups[16].subgroups[0].T[4]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,1.0,1.73205);
        groups[16].subgroups[0].T[5]=new AffineTransform(-1.0,0.0,0.0,1.0,2.0,-0.0);
        groups[16].subgroups[0].funddom=new MPoint[4];
        groups[16].subgroups[0].funddom[0]=new MPoint(4.0f,2.3094f);
        groups[16].subgroups[0].funddom[1]=new MPoint(5.0f,2.88675f);
        groups[16].subgroups[0].funddom[2]=new MPoint(4.0f,3.4641f);
        groups[16].subgroups[0].funddom[3]=new MPoint(4.0f,2.3094f);
        groups[16].subgroups[1]=new Subgroup();
        groups[16].subgroups[1].group=groups[16];
        groups[16].subgroups[1].number=1;
        groups[16].subgroups[1].name="p1(6)p3m1";
        groups[16].subgroups[1].uvec=new float[] {2.0f,0.0f};
        groups[16].subgroups[1].vvec=new float[] {1.0f,1.73205f};
        groups[16].subgroups[1].T=new AffineTransform[1];
        groups[16].subgroups[1].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[1].funddom=new MPoint[5];
        groups[16].subgroups[1].funddom[0]=new MPoint(0.0f,0.0f);
        groups[16].subgroups[1].funddom[1]=new MPoint(2.0f,0.0f);
        groups[16].subgroups[1].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[16].subgroups[1].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[16].subgroups[1].funddom[4]=new MPoint(0.0f,0.0f);
        groups[16].subgroups[2]=new Subgroup();
        groups[16].subgroups[2].group=groups[16];
        groups[16].subgroups[2].number=2;
        groups[16].subgroups[2].name="pg<6>p3m1";
        groups[16].subgroups[2].uvec=new float[] {3.0f,1.73205f};
        groups[16].subgroups[2].vvec=new float[] {-1.0f,1.73205f};
        groups[16].subgroups[2].T=new AffineTransform[2];
        groups[16].subgroups[2].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[2].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,1.0,1.73205);
        groups[16].subgroups[2].funddom=new MPoint[5];
        groups[16].subgroups[2].funddom[0]=new MPoint(0.0f,0.0f);
        groups[16].subgroups[2].funddom[1]=new MPoint(2.0f,0.0f);
        groups[16].subgroups[2].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[16].subgroups[2].funddom[3]=new MPoint(1.0f,1.73205f);
        groups[16].subgroups[2].funddom[4]=new MPoint(0.0f,0.0f);
        groups[16].subgroups[3]=new Subgroup();
        groups[16].subgroups[3].group=groups[16];
        groups[16].subgroups[3].number=3;
        groups[16].subgroups[3].name="pm<6>p3m1";
        groups[16].subgroups[3].uvec=new float[] {3.0f,1.73205f};
        groups[16].subgroups[3].vvec=new float[] {-1.0f,1.73205f};
        groups[16].subgroups[3].T=new AffineTransform[2];
        groups[16].subgroups[3].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[3].T[1]=new AffineTransform(0.5,0.86602,0.86602,-0.5,1.0,-1.73205);
        groups[16].subgroups[3].funddom=new MPoint[5];
        groups[16].subgroups[3].funddom[0]=new MPoint(4.0f,2.3094f);
        groups[16].subgroups[3].funddom[1]=new MPoint(7.0f,4.04145f);
        groups[16].subgroups[3].funddom[2]=new MPoint(7.0f,5.19615f);
        groups[16].subgroups[3].funddom[3]=new MPoint(4.0f,3.4641f);
        groups[16].subgroups[3].funddom[4]=new MPoint(4.0f,2.3094f);
        groups[16].subgroups[4]=new Subgroup();
        groups[16].subgroups[4].group=groups[16];
        groups[16].subgroups[4].number=4;
        groups[16].subgroups[4].name="cm<3>p3m1";
        groups[16].subgroups[4].uvec=new float[] {2.0f,0.0f};
        groups[16].subgroups[4].vvec=new float[] {1.0f,1.73205f};
        groups[16].subgroups[4].T=new AffineTransform[2];
        groups[16].subgroups[4].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[4].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,-1.0,1.73205);
        groups[16].subgroups[4].funddom=new MPoint[5];
        groups[16].subgroups[4].funddom[0]=new MPoint(3.0f,5.19615f);
        groups[16].subgroups[4].funddom[1]=new MPoint(4.0f,4.6188f);
        groups[16].subgroups[4].funddom[2]=new MPoint(5.0f,5.19615f);
        groups[16].subgroups[4].funddom[3]=new MPoint(5.0f,6.35085f);
        groups[16].subgroups[4].funddom[4]=new MPoint(3.0f,5.19615f);
        groups[16].subgroups[5]=new Subgroup();
        groups[16].subgroups[5].group=groups[16];
        groups[16].subgroups[5].number=5;
        groups[16].subgroups[5].name="p3(2)p3m1";
        groups[16].subgroups[5].uvec=new float[] {2.0f,0.0f};
        groups[16].subgroups[5].vvec=new float[] {1.0f,1.73205f};
        groups[16].subgroups[5].T=new AffineTransform[3];
        groups[16].subgroups[5].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[5].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,24.0,3.4641);
        groups[16].subgroups[5].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,9.0,22.51665);
        groups[16].subgroups[5].funddom=new MPoint[5];
        groups[16].subgroups[5].funddom[0]=new MPoint(12.0f,11.547f);
        groups[16].subgroups[5].funddom[1]=new MPoint(13.0f,10.96965f);
        groups[16].subgroups[5].funddom[2]=new MPoint(14.0f,11.547f);
        groups[16].subgroups[5].funddom[3]=new MPoint(13.0f,12.12435f);
        groups[16].subgroups[5].funddom[4]=new MPoint(12.0f,11.547f);
        groups[16].subgroups[6]=new Subgroup();
        groups[16].subgroups[6].group=groups[16];
        groups[16].subgroups[6].number=6;
        groups[16].subgroups[6].name="p3m1<4>p3m1";
        groups[16].subgroups[6].uvec=new float[] {4.0f,0.0f};
        groups[16].subgroups[6].vvec=new float[] {2.0f,3.4641f};
        groups[16].subgroups[6].T=new AffineTransform[6];
        groups[16].subgroups[6].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[6].T[1]=new AffineTransform(0.5,0.86603,0.86603,-0.5,0.0,0.0);
        groups[16].subgroups[6].T[2]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,2.0,-0.0);
        groups[16].subgroups[6].T[3]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,1.0,1.73205);
        groups[16].subgroups[6].T[4]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,1.0,1.73205);
        groups[16].subgroups[6].T[5]=new AffineTransform(-1.0,0.0,0.0,1.0,2.0,-0.0);
        groups[16].subgroups[6].funddom=new MPoint[4];
        groups[16].subgroups[6].funddom[0]=new MPoint(3.0f,4.04145f);
        groups[16].subgroups[6].funddom[1]=new MPoint(5.0f,5.19615f);
        groups[16].subgroups[6].funddom[2]=new MPoint(3.0f,6.35085f);
        groups[16].subgroups[6].funddom[3]=new MPoint(3.0f,4.04145f);
        groups[16].subgroups[7]=new Subgroup();
        groups[16].subgroups[7].group=groups[16];
        groups[16].subgroups[7].number=7;
        groups[16].subgroups[7].name="p31m(3)p3m1";
        groups[16].subgroups[7].uvec=new float[] {3.0f,-1.73205f};
        groups[16].subgroups[7].vvec=new float[] {3.0f,1.73205f};
        groups[16].subgroups[7].T=new AffineTransform[6];
        groups[16].subgroups[7].T[0]=new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[7].T[1]=new AffineTransform(-0.5,0.86603,-0.86603,-0.5,-0.0,3.4641);
        groups[16].subgroups[7].T[2]=new AffineTransform(-0.5,-0.86603,0.86603,-0.5,-3.0,1.73205);
        groups[16].subgroups[7].T[3]=new AffineTransform(-1.0,0.0,0.0,1.0,0.0,0.0);
        groups[16].subgroups[7].T[4]=new AffineTransform(0.5,-0.86603,-0.86603,-0.5,-0.0,3.4641);
        groups[16].subgroups[7].T[5]=new AffineTransform(0.5,0.86603,0.86603,-0.5,-3.0,1.73205);
        groups[16].subgroups[7].funddom=new MPoint[5];
        groups[16].subgroups[7].funddom[0]=new MPoint(1.0f,1.73205f);
        groups[16].subgroups[7].funddom[1]=new MPoint(2.0f,1.1547f);
        groups[16].subgroups[7].funddom[2]=new MPoint(3.0f,1.73205f);
        groups[16].subgroups[7].funddom[3]=new MPoint(1.0f,2.88675f);
        groups[16].subgroups[7].funddom[4]=new MPoint(1.0f,1.73205f);
        for (int i=0;i<17;i++){
            groups[i].number=i;
        }
    }
}
