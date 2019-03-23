


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Stack;

class Blob extends Drawable{
    MPoint P;
    
    public Blob(){
        Type=CrystalDrawing.BLOB;
        color=Color.black;
        isdashed=false;
    }
    
    public void read(BufferedReader r){
        super.read(r);
        try {
            if (lastReadLine.equals("//Propagation")){
                r.readLine();
            }
            P=Utils.readpoint(r);
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    public void write(BufferedWriter out){
        super.write(out);
        try {
            out.write("//Center of blob"); out.newLine();
            Utils.writepoint(out,P);
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    
    public Blob(float x, float y, Subgroup sg, AffineTransform xtou){
        Type=CrystalDrawing.BLOB;
        subgroup=sg;
        P=new MPoint(x,y);
        xtou.transform(P,P);
        color=Color.black;
        isdashed=false;
    }
    
    public Blob(MPoint argP, Subgroup sg){
        Type=CrystalDrawing.BLOB;
        subgroup=sg;
        P=(MPoint) argP.clone();
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){
        
    }
    
    public void setLocation(MPoint argP){
        P.setLocation(argP);
    }
    
    public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
        return null;
    }
    
    public void unselectPoints(){ }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){ }
    
    public int weight(){
        return 1;
    }
    
    public MPoint nearPoint(MPoint Pt, float dist){
        return null;
    }
    
    public Blob transform(AffineTransform T){
        return null;
    }
    
    public void transformBy(AffineTransform T){
    }
    
    public void removeLastStroke(){
        selectedPoint=P;
    }
    
   public MPoint hitPoint(Rectangle rect, AffineTransform utox){
        return null;
    }
    
    public MPoint hitPoint(int x, int y, AffineTransform utox){
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
        return true;
    }
    
    public int size(){
        return 1;
    }
    
    public String datastring(){
        return "";
    }
    
    protected Object clone(){
        Blob N=(Blob) super.clone();
        N.P=(MPoint) P.clone();
        return N;
    }
    
    public Blob copy(){
        return (Blob) clone();
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
    
    public int[] hitTranslation(int x, int y, AffineTransform utox, Rectangle rect){
        
        int[] hit=null;
        
        int[] range=uvrange(P,utox,rect);
        
        int ulo=range[0];
        int uhi=range[1];
        int vlo=range[2];
        int vhi=range[3];
        
        AffineTransform vtou=transformvtou(P);
        
        MPoint V=new MPoint();
        MPoint U=new MPoint();
        Blob B;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou.transform(V,U);
               B=new Blob(U, subgroup);
               Shape sh=B.getShape(utox);
               if (sh.contains(x,y)){
                   hit=new int[2];
                   hit[0]=i;
                   hit[1]=j;
               }
           }
        }
        return hit;
    }
    
    public Stack<int[]> hitGroupElement(int x, int y, AffineTransform utox, Rectangle rect){
        Stack<int[]> res=new Stack();
        MPoint R=new MPoint();
        AffineTransform[] T= subgroup.T;
        Blob B=null;
        int[] hit=null;
        int[] hittransl=new int[2];
        for(int i=0; i<T.length; i++){
            //System.out.println("T["+i+"]="+T[i]);
            T[i].transform(P,R);
            B=new Blob(R,subgroup);
            hittransl=B.hitTranslation(x,y, utox,rect);
            if (hittransl!=null){
                //System.out.println("Transf.:"+i+" i:"+hittransl[0]+" j:"+hittransl[1]);
                hit=new int[5];
                hit[0]=subgroup.group.number;
                hit[1]=subgroup.number;
                hit[2]=i;
                hit[3]=hittransl[0];
                hit[4]=hittransl[1];
                res.add(hit);
            }
        }
        return res;
    }
    
    
    
    public void drawtranslationorbit(Graphics2D g, AffineTransform utox, Rectangle rect,boolean previewmode){
        
        int[] range=uvrange(P,utox,rect);
        
        int ulo=range[0];
        int uhi=range[1];
        int vlo=range[2];
        int vhi=range[3];
        
        AffineTransform vtou=transformvtou(P);
        
        MPoint V=new MPoint();
        MPoint U=new MPoint();
        Blob B;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou.transform(V,U);
               B=new Blob(U, subgroup);
               Shape sh=B.getShape(utox);
               g.setPaint(Color.white);
               g.fill(sh);
               g.setStroke(new BasicStroke(penwidth));
               g.setPaint(Color.black);
               g.draw(sh);
           }
        }
    }

    public String tikzorbit(AffineTransform utox, Rectangle R, boolean previewmode){
        return "";
    }
    
    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        MPoint R=new MPoint();
        AffineTransform[] T= subgroup.T;
        Blob B=null;
        for(int i=0; i<T.length; i++){
            //System.out.println("T["+i+"]="+T[i]);
            T[i].transform(P,R);
            B=new Blob(R,subgroup);
            B.drawtranslationorbit(g, utox,rect, previewmode);
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
    
  
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        selectedPoint=null;
        Shape sh=getMarkShape(utox, P);
        if (sh.contains(x,y)){
            selectedPoint=P;
            found=true;
        }  
        ispartiallyselected=found;
        return found;
    }
    
    public void removeSelectedPoint(){ }
    
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
    
    public void translatePartialSelection(float dx, float dy, boolean altdown){ }
    
}


