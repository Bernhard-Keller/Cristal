
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class FontDialog extends JDialog implements ActionListener {
    
  JLabel sampleText;
  JComboBox fontComboBox;
  JComboBox sizeComboBox;
  JCheckBox boldCheck, italCheck;
  String[] fonts;
  Font font;
  
  public Font getFont(){
      return font;
  }
  
  public void actionPerformed(ActionEvent e) {
      System.out.println("ActionEvent");
      updateText();
    }
  
  public void updateText() {
      String name = (String) fontComboBox.getSelectedItem();
      Integer size = (Integer) sizeComboBox.getSelectedItem();

      int style;
      if (boldCheck.isSelected() && italCheck.isSelected())
        style = Font.BOLD | Font.ITALIC;
      else if (boldCheck.isSelected())
        style = Font.BOLD;
      else if (italCheck.isSelected())
        style = Font.ITALIC;
      else
        style = Font.PLAIN;

      font = new Font(name, style, size.intValue());
      sampleText.setFont(font);
    }
  
  public FontDialog(Frame fr, Font argfont) {
      
    super(fr, "Attributs texte", true);
    font=argfont;
    System.out.println("Font style: "+font.getStyle());
    
    this.setSize(600, 150);
    JPanel topPanel=new JPanel();
    topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    sampleText = new JLabel("Exemple de texte");
    sampleText.setFont(font);
    topPanel.add(sampleText);
    this.add(topPanel, BorderLayout.NORTH);
    //GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    //fonts = g.getAvailableFontFamilyNames();
    fonts=new String[5];
    fonts[0]="Courier";
    fonts[1]="Dialog";
    fonts[2]="Helvetica";
    fonts[3]="SansSerif";
    fonts[4]="Serif";

    JPanel controlPanel = new JPanel();
    controlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    fontComboBox = new JComboBox(fonts);
    controlPanel.add(new JLabel("Famille : "));
    controlPanel.add(fontComboBox);

    Integer[] sizes = { 7, 8, 9, 10, 11, 12, 14, 18, 20, 22, 24, 36 };

    sizeComboBox = new JComboBox(sizes);
    controlPanel.add(new JLabel("Taille : "));
    controlPanel.add(sizeComboBox);

    boldCheck = new JCheckBox("Gras");
    boldCheck.setSelected(font.isBold());
    controlPanel.add(boldCheck);
    
    italCheck = new JCheckBox("Ital");
    italCheck.setSelected(font.isItalic());
    controlPanel.add(italCheck);
    
    this.add(controlPanel, BorderLayout.SOUTH);
    
    System.out.println("Dialog font family: "+font.getFamily());
    System.out.println("Dialog font size: "+font.getSize());
    
    sizeComboBox.setSelectedItem(new Integer(font.getSize()));
    fontComboBox.setSelectedItem(font.getFamily());
    
    fontComboBox.addActionListener(this);
    sizeComboBox.addActionListener(this);
    boldCheck.addActionListener(this);
    italCheck.addActionListener(this);
    
    updateText();
     
    setLocationRelativeTo(fr);
    this.setVisible(true);
  }
}
