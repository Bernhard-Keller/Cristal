import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

 
public class RotationDialog extends JDialog implements ActionListener{
    
    JTextField angleField;
    JLabel angleLabel;
    JCheckBox copyBox;
    JButton closeButton;
    
    Boolean tocopy;
    
    public boolean getToCopy(){
      return copyBox.isSelected();
    }
    
    public float getAngle(){
        return Float.parseFloat(angleField.getText());
    }
    
    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        
        if (s.equals("Fermer")){
            setVisible(false);
        }
    }
    
    
    public RotationDialog(Frame fr){
        super(fr, "Rotation", true);
        
        closeButton=new JButton("Fermer");
        closeButton.addActionListener(this);
        
        copyBox=new JCheckBox("Copier");
        copyBox.setActionCommand("copyBox");
        
        angleLabel=new JLabel("Angle");
        angleField=new JTextField("90");
        
        JPanel panel=new JPanel(new GridLayout(0,2));
        panel.add(angleLabel);
        panel.add(angleField);
        panel.add(copyBox);
        
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        Container cont=getContentPane();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.add(panel);
        
        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(closeButton);
        
        cont.add(buttonPane);
        
        getRootPane().setDefaultButton(closeButton);
        
        pack();
        setLocationRelativeTo(fr);
	setVisible(true);
    }
}
