
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernhard
 */
class TextDisplayDialog extends JDialog implements ActionListener {
    protected JTextArea textArea;
    private static TextDisplayDialog dialog;
    
    
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
        }
        TextDisplayDialog.dialog.setVisible(false);
    }
    
    public static void showDialog(Component frameComp,
                                    Component locationComp,
                                    String title,String text,int height, int width) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new TextDisplayDialog(frame,
                                locationComp,
                                title,text,height,width);
        dialog.setVisible(true);
    }
    
public TextDisplayDialog(Frame frame, Component locComp, String title, String str, int height, int width) {
        super(frame, title, false);
        JButton OKButton = new JButton("OK");
        OKButton.addActionListener(this);
        
        getRootPane().setDefaultButton(OKButton);
        
        textArea = new JTextArea(height, width);
        textArea.setText(str);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.PAGE_AXIS));
        
        
        textPane.add(Box.createRigidArea(new Dimension(0,5)));
        textPane.add(scrollPane);
        textPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(OKButton);
        

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(textPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locComp);
    }
    
}
