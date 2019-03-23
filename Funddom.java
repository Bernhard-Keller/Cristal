
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
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
public class Funddom extends Drawable {
    MPoint[] P;
  
    public int weight(){
        return 1;
    }
    
    public Funddom transform(AffineTransform T){
        return null;
    }
    
    
    public MPoint nearPoint(MPoint Pt, float dist){
        return null;
    }
    
    public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
        return null;
    }
    
    public void unselectPoints(){ }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){ }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){ }
    
    public int size(){
        return P.length;
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
    
    public Funddom(MPoint[] argP, Subgroup sgr){
        Type=CrystalDrawing.FUNDDOM;
        subgroup=sgr;
        P=argP;
        attr=new Attribute();
    }
    
    protected Object clone(){
        Funddom N=(Funddom) super.clone();
        N.P=new MPoint[P.length];
        for (int i=0;i<P.length;i++){
            N.P[i]=(MPoint) P[i].clone();
        }
        return N;
    }
    
    public Funddom copy(){
        return (Funddom) clone();
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
    
    public void removeSelectedPoint(){ }
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        return false;
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        
    }
    
    public MPoint barycenter(){
        double x=0;
        double y=0;
        for (int i=0;i<P.length;i++){
            x=x+P[i].getX();
            y=y+P[i].getY();
        }
        x=x/P.length;
        y=y/P.length;
        return new MPoint((float) x, (float) y);
    }

    public Shape getShape(AffineTransform utox) {
        Polygon poly=new Polygon();
        MPoint R=new MPoint();
        for (int i=0; i<P.length; i++){
            utox.transform(P[i], R);
            poly.addPoint((int) R.getX(), (int) R.getY());
        }
        return poly;
    }
    
    public void draw(Graphics2D g, AffineTransform utox){
        Shape sh=getShape(utox);
        g.setPaint(Color.gray);
        g.draw(sh);
        //drawmark(g,utox,P1);
        //drawmark(g,utox,P2);
    }
    
    public String tikztranslationorbit(AffineTransform utox, Rectangle rect){
        String str="";
        
        int[] range;
        int ulo=0;
        int uhi=0;
        int vlo=0;
        int vhi=0;
        
        for (int i=0;i<P.length;i++){
            range=uvrange(P[i],utox,rect);
            ulo=Math.min(range[0],ulo);
            uhi=Math.max(range[1],uhi);
            vlo=Math.min(range[2],vlo);
            vhi=Math.max(range[3],vhi);
        }
        
        MPoint V=new MPoint();
        MPoint U=new MPoint();
        AffineTransform vtou=new AffineTransform();
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou=transformvtou(P[0]);
               vtou.transform(V,U);
               str=str+"\\draw[color=gray] ("+U.getX()+","+U.getY()+") ";
               for (int ctr=1; ctr<P.length;ctr++){
                    vtou=transformvtou(P[ctr]);
                    vtou.transform(V,U);
                    str=str+ "-- ("+U.getX()+","+U.getY()+") ";
                    //System.out.println("Added point "+X);
               }
               str=str+" -- cycle ;\n";
            }
        }
        return str;
    }
    
    public void drawtranslationorbit(Graphics2D g, AffineTransform utox, Rectangle rect){
        
        int[] range;
        int ulo=0;
        int uhi=0;
        int vlo=0;
        int vhi=0;
        
        for (int i=0;i<P.length;i++){
            range=uvrange(P[i],utox,rect);
            ulo=Math.min(range[0],ulo);
            uhi=Math.max(range[1],uhi);
            vlo=Math.min(range[2],vlo);
            vhi=Math.max(range[3],vhi);
        }
        
        AffineTransform xtou=new AffineTransform(utox);
        try{
            xtou.invert();
        }
        catch (NoninvertibleTransformException  e){
	    System.out.println("utox: "+e.getMessage());
        }
        
        MPoint V=new MPoint();
        MPoint U=new MPoint();
        MPoint X=new MPoint();
        AffineTransform vtou=new AffineTransform();
        
        
        GeneralPath path;
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               path=new GeneralPath();
               vtou=transformvtou(P[0]);
               vtou.transform(V,U);
               utox.transform(U,X);
               path.moveTo(X.getX(), X.getY());
               for (int ctr=1; ctr<P.length;ctr++){
                    vtou=transformvtou(P[ctr]);
                    vtou.transform(V,U);
                    utox.transform(U,X);
                    path.lineTo(X.getX(), X.getY());
                    //System.out.println("Added point "+X);
               }
               g.setPaint(Color.gray);
               g.draw(path);
               //System.out.println("Drawing translated fund. domain. poly:"+poly);
            }
        }
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle rect, boolean previewmode){
        String str="";
        MPoint[] R=new MPoint[P.length];
        AffineTransform[] T= subgroup.T;
        for(int i=0; i<T.length; i++){
            for (int ctr=0; ctr<P.length; ctr++){
                R[ctr]=new MPoint();
                T[i].transform(P[ctr],R[ctr]);
            }
            Funddom F=new Funddom(R, subgroup);
            str=str+F.tikztranslationorbit(utox,rect);
        }
        return str;
    }

    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        MPoint[] R=new MPoint[P.length];
        AffineTransform[] T= subgroup.T;
        for(int i=0; i<T.length; i++){
            g.setStroke(new BasicStroke(penwidth));
            for (int ctr=0; ctr<P.length; ctr++){
                R[ctr]=new MPoint();
                T[i].transform(P[ctr],R[ctr]);
            }
            Funddom F=new Funddom(R, subgroup);
            F.drawtranslationorbit(g, utox,rect);
        }
    }
    
}
