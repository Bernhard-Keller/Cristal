

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.Font;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernhard
 */
public class Texte extends Drawable {
    MPoint P;
    String textstring;
    Font font;
    
    public Texte(){
        attr=new Attribute();
        Type=CrystalDrawing.TEXT;
    }
    
    public Texte(float x, float y, Subgroup sg, AffineTransform xtou, Font argfont, String argtext){
        attr=new Attribute();
        Type=CrystalDrawing.TEXT;
        subgroup=sg;
        P=new MPoint(x,y);
        xtou.transform(P,P);
        color=Color.black;
        isdashed=false;
        font=argfont;
        textstring=argtext;
    }
    
    
    public void read(BufferedReader r){
        super.read(r);
        try {
            if (lastReadLine.equals("//Propagation")){
                r.readLine();
            }
            P=Utils.readpoint(r);
            r.readLine();
            String str=r.readLine();
            if (str.equals("null")){
                selectedPoint=null;
            } 
            else {
                selectedPoint=P;
            }
            textstring=Utils.readstring(r);
            String fontname=Utils.readstring(r);
            int fontsize=Utils.readint(r);
            int fontstyle=Utils.readint(r);
            font=new Font(fontname, fontstyle, fontsize);
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    public void write(BufferedWriter out){
        super.write(out);
        try {
            out.write("//Location"); out.newLine();
            Utils.writepoint(out,P);
            out.write("//Selected point"); out.newLine();
            if (selectedPoint==null){
                out.write("null"); out.newLine();
            }
            else if (selectedPoint==P){
                out.write("1"); out.newLine();
            }
            out.write("//Text string"); out.newLine();
            Utils.writestring(out, textstring);
            out.write("//Font name"); out.newLine();
            Utils.writestring(out, font.getFontName());
            Utils.writeint(out, "Font size", font.getSize());
            Utils.writeint(out, "Font style", font.getStyle());
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public void setLocation(MPoint argP){
        P.setLocation(argP);
    }
    
    public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
        closestPoint.setLocation(P);
        double[] res=new double[1];
        res[0]=P.distance(Pt);
        return res;
    }
    
    public void unselectPoints(){
        P.setSelection(false);
    }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){
       if (P.isselected){
           Points.add(P);
       }
    }
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        if (isinbackground){
            return false;
        }
        boolean found=false;
        if (!shiftdown){
            selectedPoint=null;
            P.setSelection(false);
        }
        Shape sh=getMarkShape(utox, P);
        if (sh.contains(x,y)){
            selectedPoint=P;
            P.setSelection(true);
            found=true;
        }
        if (!shiftdown){
            ispartiallyselected=found;
        }
        return found;
    }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return;
        }
        boolean found=false;
        MPoint Q=new MPoint();
        utox.transform(P,Q);
        if (rect.contains(Q)){
            P.setSelection(true);
            found=true;
        }
        if (found){
            this.setPartialSelection(true);
        }
    }
    
    public int weight(){
        return 1;
    }
    
    public MPoint nearPoint(MPoint Pt, float dist){
       MPoint N=null;
       if (Pt.distance(P)<dist){
           N=P;
       }
       if (N!=null){
           N=(MPoint) N.clone();
       }
       return N;
   }
    
    public Texte transform(AffineTransform T){
        return null;
    }
    
    
    public void removeLastStroke(){
        selectedPoint=P;
    }
    
   public MPoint hitPoint(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return null;
        }
        MPoint R=null;
        MPoint Q=new MPoint();
        utox.transform(P, Q);
        if (rect.contains(Q)){
            R=P;
        }
        return R;
    }
    
    public MPoint hitPoint(int x, int y, AffineTransform utox){
        if (isinbackground){
            return null;
        }
        MPoint R=null;
        Shape sh=null;
        sh=getMarkShape(utox,P);
        if (sh.contains(x,y)){
            R=P;
        }
        return R;
    }
    
    public MPoint hitPointAll(int x, int y, AffineTransform utox){
        return hitPoint(x,y,utox);
    }
    
    public boolean isempty(){
        return (selectedPoint==P);
    }
    
    public int size(){
        return 1;
    }
    
    public String datastring(){
        return "";
    }
    
    protected Object clone(){
        Texte N=(Texte) super.clone();
        N.P=(MPoint) P.clone();
        N.textstring=textstring;
        N.font=font;
        return N;
    }
    
    public Texte copy(){
        return (Texte) clone();
    }
    
    public MPoint barycenter(){
        double x=P.getX();
        double y=P.getY();
        return new MPoint((float) x,(float) y);
    }
    
    public Shape getShape(AffineTransform utox) {
        MPoint R=new MPoint();
        utox.transform(P,R);
        float x=(float) R.getX();
        float y=(float) R.getY();
        float r=2*markdiam;
        Ellipse2D e=new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
        return e;
    }
    
    
    public String tikzorbit(AffineTransform utox, Rectangle R, boolean previewmode){
        String str="";
        if (previewmode){
            str=str+tikzcolor(attr.contourcolor);
        }
        else {
            str=str+tikzcolor(color);
        }
        str=str+"\\draw[color=mycolor] ("+P.getX()+","+P.getY()+") node[above right]{"+textstring+"};\n";
        if ((selectionmodeon)||(partialselectionmodeon)){
            str=str+tikzmark(P, Color.white);
        }
        if (ispartiallyselected){
            if (P.isSelected()){
                str=str+tikzmark(P,Color.black);
            }
        }
        if (isselected){
            str=str+tikzmark(P, Color.black);
            //System.out.println("tikzmark drawn.");
        }
        return str;
    }
    
    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        MPoint R=new MPoint();
        utox.transform(P,R);
        if (previewmode){
            g.setColor(attr.contourcolor);
        }
        else {
            g.setColor(color);
        }
        g.setFont(font);
        g.drawString(textstring,(int) R.getX(), (int) R.getY());
        if ((selectionmodeon)||(partialselectionmodeon)){
            drawmark(g,utox,P, Color.white);
        }
        if (ispartiallyselected){
            if (P.isSelected()){
                drawmark(g,utox,P,Color.black);
            }
        }
        if (isselected){
            drawmark(g,utox,P, Color.black);
            //System.out.println("Text is selected.");
        }
    }
    
    
    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        //System.out.println("Modify selection: selected point " + selectedPoint);
        MPoint R=new MPoint(x,y);
        xtou.transform(R, selectedPoint);
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        xtou.transform(R, P);
    }
    
   public void removeSelectedPoint(){textstring="";}
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        Shape sh=getMarkShape(utox, P);
        if (sh.contains(x,y)){
            selectedPoint=P;
            found=true;
        }   
        return found;
    }
    
    
    
    public void translate(float dx, float dy){
        P.setLocation(P.getX()+dx, P.getY()+dy);
    }
    
    public void translatePartialSelection(float dx, float dy, boolean altdown){
        if (P.isSelected()){
            P.setLocation(P.getX()+dx, P.getY()+dy);
        }
    }
    
}



    
    
    
    



