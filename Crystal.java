
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;



public class Crystal {

    public Crystal(){
     
    }
    
    
    public static void main(String[] args) {
        
        Crystal c=new Crystal();
        CrystalDrawing cd=new CrystalDrawing(new Dimension(900,600));
        
        
        JFrame frame = new JFrame("Cristal");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(cd);
        
        cd.setFrame(frame);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Fichier");
        
        JMenuItem menuItem=new JMenuItem("Ouvrir ...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Rajouter ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Enregistrer");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Enregistrer sous ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Imprimer ...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_P, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Vers TikZ ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Quitter",
                         KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
                        

        menuBar.add(menu);
        
        menu=new JMenu("Edition");
        
        menuItem = new JMenuItem("Effacer dernier trait");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Rétablir");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        
        menuItem = new JMenuItem("Couper");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_X, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Copier");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Coller");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_V, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Sélectionner tout");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Effacer le dessin");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Prélever");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Insérer");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Rapprocher");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Eloigner");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Premier plan");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Arrière plan");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        menu=new JMenu("Outil");
        
        menuItem = new JMenuItem("Segment");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Polygone");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("C. de Bézier");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Cercle");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Texte ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        
        
        menuItem = new JMenuItem("Sélecteur");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_E, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Sélecteur partiel");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        
        
        menuItem = new JMenuItem("Propagateur");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Homothétie");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Rotation");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Symétrie");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Glissement");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Insérer point");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Enlever point");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Continuer courbe");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Confondre points");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        menu=new JMenu("Groupe");
        
        for (int i=0;i<17;i++){
            JMenu subMenu=new JMenu(cd.groups[i].name);
            
            for (int j=0; j<cd.groups[i].subgroups.length;j++){
                menuItem=new JMenuItem(cd.groups[i].subgroups[j].name);
                menuItem.addActionListener(cd);
                subMenu.add(menuItem);
            }
            menu.add(subMenu);
        }
        
        menuBar.add(menu);
        
        menu=new JMenu("Vue");
        
        menuItem = new JMenuItem("Redessiner");
        menuItem.addActionListener(cd);
        //menu.add(menuItem);
        
        menuItem = new JMenuItem("Prévisualisation");
        menuItem.addActionListener(cd);
        cd.setPreviewMenuItem(menuItem);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Dom. fond.");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_F, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Carte des sym.");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_R, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Agrandir");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_G);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Diminuer");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_D, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Centrer");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Tourner ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Translater");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Loupe");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_L, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        
        menuBar.add(menu);
        
        menu=new JMenu("Attributs");
        
        menuItem = new JMenuItem("Attr. sél. ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Attr. outil ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Attr. texte ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Copier attr.");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Coller attr.");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Premier/arrière plan");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Sans répliques");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Toutes les répliques");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Adopter sous-groupe");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        menu=new JMenu("Motifs");
        
        menuItem=new JMenuItem("Nouveau motif");
        menuItem.addActionListener(cd);
        cd.setNewMotiveItem(menuItem);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Titre ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Multiples ...");
        cd.setMultipleMotivesItem(menuItem);
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Copier motif");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Coller motif");
        menuItem.addActionListener(cd);
        cd.setPasteMotiveItem(menuItem);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Couper motif");
        menuItem.addActionListener(cd);
        cd.setCutMotiveItem(menuItem);
        menu.add(menuItem);
        
        //menu.addSeparator();
        
        cd.setMotiveMenu(menu);
        cd.updateMotiveMenu();
        cd.updateFrameTitle();
        
        menuBar.add(menu);
        
        menu=new JMenu("Spécial");
        
        menuItem=new JMenuItem("Magnétisme ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Crayon tracés ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Diam. marques ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Cacher le groupe");
        menuItem.addActionListener(cd);
        cd.setHideGroupItem(menuItem);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Serpent ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Dom. de Voronoï");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Triang. de Delaunay");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("A propos ...");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        menu=new JMenu("Divers");
        
        menuItem=new JMenuItem("Export group data ...");
        menuItem.addActionListener(cd);
        menuItem.setIcon(new ImageIcon());
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Export symmetry chart data ...");
        menuItem.addActionListener(cd);
        menuItem.setIcon(new ImageIcon());
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Convert motives to sym. charts");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Dashed");
        menuItem.addActionListener(cd);
        menuItem.setIcon(new ImageIcon());
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Test");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Test1");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuItem=new JMenuItem("Test2");
        menuItem.addActionListener(cd);
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        Box statusPanel = new Box(BoxLayout.X_AXIS);
        JLabel statusLabel= new JLabel("");
        cd.setstatusLabel(statusLabel);
        statusPanel.add(statusLabel);
    
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(cd);
        frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        
        Drawable.penwidth=1;
        Drawable.markdiam=4;
        
        cd.initializextou();
        cd.updatestatus(CrystalDrawing.WAITING_FOR_SEGMENT);
        cd.repaint();
        }
}
