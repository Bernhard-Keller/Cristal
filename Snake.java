

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.util.*;
import java.io.BufferedReader;


public class Snake extends Drawable {
    Stack<Segment> S;
    float length, width,drift,nervosity;
    int lapse, colorvariability;
    boolean blackbackground;
    long seed;
    
    double angle;
    MPoint currentPoint;
    Attribute currentAttr;
    
    Random myRandom;
    
    public Snake(Subgroup sgr, MPoint P){
        Type=CrystalDrawing.SNAKE;
        subgroup=sgr;
        color=Color.black;
        attr=new Attribute();
        
        length=0.1f;
        width=4;
        drift=0.1f;
        nervosity=3;
        lapse=25;
        colorvariability=40;
        seed=0;
        blackbackground=true;
        
        S=new Stack();
        angle=0;
        myRandom=new Random(seed);
        currentPoint=P;
        currentAttr=new Attribute();
        currentAttr.contourwidth=width;
    }
    
    public Object clone(){
        Snake N=(Snake) super.clone();
        N.length=length;
        N.width=width;
        N.drift=drift;
        N.nervosity=nervosity;
        N.lapse=lapse;
        N.colorvariability=colorvariability;
        N.seed=seed;
        N.blackbackground=blackbackground;
        return N;
    }
    
    public Snake copy(){
        return (Snake) clone();
    }
    
    public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
        return null;
    }
    
    public void unselectPoints(){ }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){ }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){ }
    
    
    public void clear(MPoint P){
        S.removeAllElements();
        angle=0;
        myRandom=new Random(seed);
        currentPoint=P;
        currentAttr=new Attribute();
        currentAttr.contourwidth=width;
    }
    
    public void setSubgroup(Subgroup sgr){
        subgroup=sgr;
    }
    
    public void grow(){
        angle=drift+nervosity*Math.random();
        double x=currentPoint.getX()+length*Math.cos(angle);
        double y=currentPoint.getY()+length*Math.sin(angle);
        MPoint newPoint=new MPoint((float) x,(float) y);
        Segment seg=new Segment(currentPoint, newPoint, subgroup);
        Color c=currentAttr.contourcolor;
        int r=(c.getRed()+myRandom.nextInt(colorvariability))%256;
        int g=(c.getGreen()+myRandom.nextInt(colorvariability))%256;
        int b=(c.getBlue()+myRandom.nextInt(colorvariability))%256;
        currentAttr.contourcolor=new Color(r,b,b);
        currentAttr.contourwidth=width;
        //System.out.println("r, g, b: "+r+", "+g+", "+b);
        seg.setAttribute(currentAttr);
        currentPoint=newPoint;
        S.add(seg);
    }
    
    public void write(BufferedWriter out){
        super.write(out);
        Utils.writefloat(out, "Length", length);
        Utils.writefloat(out,"Width",width);
        Utils.writefloat(out, "Drift", drift);
        Utils.writefloat(out,"Nervosity",nervosity);
        Utils.writeint(out,"Lapse",lapse);
        Utils.writeint(out, "Color variability", colorvariability);
        Utils.writeint(out,"Color seed", (int) seed);
        Utils.writeboolean(out, "Background is black", blackbackground);
    }
    
    public void read(BufferedReader in){
        super.read(in);
        if (lastReadLine.equals("//Propagation")){
            length=Utils.readfloat(in);}
        else {
            length=Utils.readfloatnoline(in);
        }
        width=Utils.readfloat(in);
        drift=Utils.readfloat(in);
        nervosity=Utils.readfloat(in);
        lapse=Utils.readint(in);
        colorvariability=Utils.readint(in);
        seed=Utils.readint(in);
        blackbackground=Utils.readboolean(in);
    }
  
    public int weight(){
        return 1;
    }
    
    public Funddom transform(AffineTransform T){
        return null;
    }
    
    
    public MPoint nearPoint(MPoint Pt, float dist){
        return null;
    }
    
    public int size(){
        return S.size();
    }
    
    public void translate(float dx, float dy){
        
    }
    
    public void translatePartialSelection(float dx, float dy, boolean altdown){ }
    
    public MPoint hitPoint(Rectangle rect, AffineTransform utox){
        return null;
    }
    
    public MPoint hitPoint(int x, int y, AffineTransform utox){
        return null;
    }
    
    public MPoint hitPointAll(int x, int y, AffineTransform utox){
        return hitPoint(x,y,utox);
    }
    
    
    public void removeLastStroke(){
        
    }
    
    public boolean isempty(){
        return false;
    }
    
    public String datastring(){
        return "";
    }

    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){

    }
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        return false;
    }
    
    public void removeSelectedPoint(){}
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        return false;
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        
    }
    
    public MPoint barycenter(){
        return null;
    }

    public Shape getShape(AffineTransform utox) {
        return null;
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle R, boolean previewmode){
        return "";
    }

    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        for (int i=0; i<S.size();i++){
            S.elementAt(i).draworbit(g,utox,rect,previewmode);
        }
    }
    
    
    
}
