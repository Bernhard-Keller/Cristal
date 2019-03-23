
import java.awt.Color;
import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedWriter;

public class Attribute implements Cloneable {
    boolean contourpresent, fillpresent, isdashed;
    float contourwidth;
    Color contourcolor, fillcolor;
    

public Attribute(){
    contourpresent=true;
    fillpresent=true;
    isdashed=false;
    contourwidth=2;
    contourcolor=Color.blue;
    fillcolor=Color.yellow;
}

public void write(BufferedWriter out){
    Utils.writeboolean(out,"Contour present", contourpresent);
    Utils.writefloat(out,"Contour width",contourwidth);
    Utils.writecolor(out,"Contour color", contourcolor);
    Utils.writeboolean(out,"Is dashed", isdashed);
    Utils.writeboolean(out, "Fill present", fillpresent);
    Utils.writecolor(out,"Fill color", fillcolor);
}

public Object clone(){
    Attribute a=new Attribute();
    a.contourpresent=contourpresent;
    a.fillpresent=fillpresent;
    a.isdashed=isdashed;
    a.contourwidth=contourwidth;
    a.contourcolor=new Color(contourcolor.getRed(), contourcolor.getGreen(), contourcolor.getBlue());
    a.fillcolor=new Color(fillcolor.getRed(), fillcolor.getGreen(), fillcolor.getBlue());
    return a;
}



public void read(BufferedReader r){
    contourpresent=Utils.readboolean(r);
    contourwidth=Utils.readfloat(r);
    contourcolor=Utils.readcolor(r);
    isdashed=Utils.readboolean(r);
    fillpresent=Utils.readboolean(r);
    fillcolor=Utils.readcolor(r);
}

public BasicStroke getContourStroke(){
    BasicStroke stroke=null;
    if (isdashed){
       stroke=new BasicStroke(contourwidth,       // Width
               BasicStroke.CAP_SQUARE,    // End cap
               BasicStroke.JOIN_MITER,    // Join style
               10.0f,                     // Miter limit
               new float[] {6.0f,12.0f}, // Dash pattern
               0.0f);  
       //System.out.println("Dashed stroke chosen");
    } 
    else {
       stroke=new BasicStroke(contourwidth);
   }
   return stroke;
}

public String toString(){
    String str=new String("Contour present:"+contourpresent+" Fill present:"+fillpresent+"\n");
    str=str+"Contour color: "+contourcolor.getRed()+" "+contourcolor.getGreen()+" "+contourcolor.getBlue()+"\n";
    str=str+"Contour width: "+contourwidth+"\n";
    str=str+"Fill color   : "+fillcolor.getRed()+" "+fillcolor.getGreen()+" "+fillcolor.getBlue()+"\n";
    return str;
}



}
