
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;

class Segment extends Drawable{
    MPoint P1,P2;
    
    public Segment(){
        Type=CrystalDrawing.SEGMENT;
    }
    
    
    public Segment(float x1, float y1, float x2, float y2, Subgroup sg, AffineTransform xtou, Attribute at){
        Type=CrystalDrawing.SEGMENT;
        subgroup=sg;
        P1=new MPoint(x1,y1);
        xtou.transform(P1,P1);
        P2=new MPoint(x2,y2);
        xtou.transform(P2,P2);
        color=Color.black;
        isdashed=false;
        attr=(Attribute) at.clone();
        //System.out.println("Attribute in new Segment:\n"+attr.toString());
    }
    
    public Segment(MPoint argP1, MPoint argP2, Subgroup sg){
        Type=CrystalDrawing.SEGMENT;
        subgroup=sg;
        P1=(MPoint) argP1.clone();
        P2=(MPoint) argP2.clone();
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }
    
    public Segment(Point2D.Float P1arg, Point2D.Float P2arg, Subgroup sg){
        Type=CrystalDrawing.SEGMENT;
        subgroup=sg;
        P1=new MPoint(P1arg);
        P2=new MPoint(P2arg);
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }
    
    public Segment(StraightLine L, Subgroup sg){
        Type=CrystalDrawing.SEGMENT;
        subgroup=sg;
        P1=new MPoint(L.P1);
        P2=new MPoint(L.P2);
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }
    
    public void unselectPoints(){
        P1.setSelection(false);
        P2.setSelection(false);
    }
    
    public void setAttribute(Attribute a){
        attr=(Attribute) a.clone();
    }
    
    public Segment copy(MPoint argP1, MPoint argP2){
        Segment N=copy();
        N.P1=argP1;
        N.P2=argP2;
        return N;
    }
    
   public int weight(){
       return 2;
   }
   
   public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
       double dist=Pt.distance(P1);
       MPoint Pmin=P1.copy();
       MPoint Q=new MPoint();
       for (float t=0; t<1; t=t+0.01f){
           Q.setLocation((1-t)*P1.getX()+t*P2.getX(),(1-t)*P1.getY()+t*P2.getY());
           if (Pt.distance(Q)<dist){
               Pmin=Q.copy();
               dist=Pt.distance(Q);
           }
       }
       closestPoint.setLocation(Pmin);
       double[] res=new double[2];
       res[0]=dist;
       res[1]=0;
       return res;
   }
    
   public MPoint nearPoint(MPoint Pt, float dist){
       MPoint N=null;
       if (Pt.distance(P1)<dist){
           N=P1;
       }
       if (Pt.distance(P2)<dist){
           N=P2;
       }
       if (N!=null){
           N=(MPoint) N.clone();
       }
       return N;
   }
   
   
    
    public void read(BufferedReader r){
        super.read(r);
        try {
            if (lastReadLine.equals("//Propagation")){
                r.readLine();
            }
            P1=Utils.readpoint(r);
            P2=Utils.readpoint(r);
            r.readLine();
            String str=r.readLine();
            if (str.equals("null")){
                selectedPoint=null;
            } 
            else {
                int n=Integer.parseInt(str);
                System.out.println("Selected point:"+n);
                if (n==1){
                    selectedPoint=P1;
                }
                else {
                    selectedPoint=P2;
                }
            }
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    public void write(BufferedWriter out){
        super.write(out);
        try {
            out.write("//Extremities"); out.newLine();
            Utils.writepoint(out,P1);
            Utils.writepoint(out,P2);
            out.write("//Selected point"); out.newLine();
            if (selectedPoint==null){
                out.write("null"); out.newLine();
            }
            else if (selectedPoint==P1){
                out.write("1"); out.newLine();
            }
            else if (selectedPoint==P2){
                out.write("2"); out.newLine();
            }
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public void removeLastStroke(){
        P2=P1;
        selectedPoint=P1;
    }
    
   
    
    public MPoint hitPoint(int x, int y, AffineTransform utox){
        if (isinbackground){
            return null;
        }
        MPoint R=null;
        Shape sh=null;
        sh=getMarkShape(utox,P1);
        if (sh.contains(x,y)){
            R=P1;
        }
        sh=getMarkShape(utox,P2);
        if (sh.contains(x,y)){
            R=P2;
        }
        return R;
    }
    
    public MPoint hitPointAll(int x, int y, AffineTransform utox){
        return hitPoint(x,y,utox);
    }
    
    public MPoint hitPoint(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return null;
        }
        MPoint R=null;
        MPoint P=new MPoint();
        utox.transform(P1, P);
        if (rect.contains(P)){
            R=P1;
        }
        utox.transform(P2, P);
        if (rect.contains(P)){
            R=P2;
        }
        return R;
    }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){
        if (P1.isselected){
            Points.add(P1);
        }
        if (P2.isselected){
            Points.add(P2);
        }
    }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return;
        }
        boolean found=false;
        MPoint P=new MPoint();
        utox.transform(P1,P);
        if (rect.contains(P)){
            P1.setSelection(true);
            found=true;
        }
        if (!found){
            utox.transform(P2,P);
            if (rect.contains(P)){
                P2.setSelection(true);
                found=true;
            }
        }
        if (found){
            this.setPartialSelection(true);
        }
    }
    
    public boolean isempty(){
        if (P1==P2){
            return true;
        }
        else {
            return false;
        }
    }
    
    public int size(){
        if (P1==P2){
            return 1;
        }
        else {
            return 2;
        }
    }
    
    public String datastring(){
        String s="P1=new MPoint((float) "+(float) P1.getX()+", (float) "+(float) P1.getY()+");\n";
        s=s+"P2=new MPoint((float) "+(float) P2.getX()+", (float) "+(float) P2.getY()+");\n";
        return s;
    }
    
    public Object clone(){
        Segment S=(Segment) super.clone();
        S.P1=(MPoint) P1.clone();
        S.P2=(MPoint) P2.clone();
        return S;
    }
    
    public Segment copy(){
        return (Segment) clone();
    }
    
    public String toString(){
        String str;
        str="P1: "+P1.getX()+", "+P1.getY()+"\n";
        str=str+"P2: "+P2.getX()+", "+P2.getY()+"\n";
        return str;
    }
    
    public MPoint barycenter(){
        double x=(P1.getX()+P2.getX())/2;
        double y=(P1.getY()+P2.getY())/2;
        return new MPoint((float) x,(float) y);
    }
    
    public Shape getShape(AffineTransform utox) {
        Polygon poly=new Polygon();
        MPoint R=new MPoint();
        utox.transform(P1,R);
        poly.addPoint((int) R.getX(), (int) R.getY());
        R=new MPoint();
        utox.transform(P2,R);
        poly.addPoint((int) R.getX(), (int) R.getY());
        return poly;
    }
    
    public String tikztranslationorbit(AffineTransform utox, Rectangle rect,boolean previewmode){
        String str="";
       
        int[] range1=uvrange(P1,utox,rect);
        int[] range2=uvrange(P2,utox,rect);
        
        int ulo=Math.min(range1[0], range2[0]);
        int uhi=Math.max(range1[1],range2[1]);
        int vlo=Math.min(range1[2],range2[2]);
        int vhi=Math.max(range1[3],range2[3]);
        
        AffineTransform vtou1=transformvtou(P1);
        AffineTransform vtou2=transformvtou(P2);
        
        MPoint V=new MPoint();
        MPoint U1=new MPoint();
        MPoint U2=new MPoint();
        Segment S;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou1.transform(V,U1);
               vtou2.transform(V,U2);
               S=copy(U1,U2);
               str=str+S.tikz(previewmode);
            }
        }
        return str;
    }
    
    
    public void drawtranslationorbit(Graphics2D g, AffineTransform utox, Rectangle rect,boolean previewmode){
        
        int[] range1=uvrange(P1,utox,rect);
        int[] range2=uvrange(P2,utox,rect);
        
        int ulo=Math.min(range1[0], range2[0]);
        int uhi=Math.max(range1[1],range2[1]);
        int vlo=Math.min(range1[2],range2[2]);
        int vhi=Math.max(range1[3],range2[3]);
        
        AffineTransform vtou1=transformvtou(P1);
        AffineTransform vtou2=transformvtou(P2);
        
        MPoint V=new MPoint();
        MPoint U1=new MPoint();
        MPoint U2=new MPoint();
        Segment S;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou1.transform(V,U1);
               vtou2.transform(V,U2);
               S=copy(U1,U2);
               S.draw(g, utox, previewmode);
            }
        }
    }
    
    public Segment transform(AffineTransform T){
        Segment S=copy();
        MPoint R1=new MPoint();
        MPoint R2=new MPoint();
        T.transform(P1,R1);
        T.transform(P2,R2);
        S.P1=R1;
        S.P2=R2;
        return S;
    }
    
    
    
    
    
    public String tikz(boolean previewmode){
        String str="";
        if (!previewmode){
            str=str+tikzcolor(color);
            String dash;
            if (isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
            str=str+"\\draw[color=mycolor"+dash+"] ("+P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+") ;\n";
        }
        else {
            str=str+tikzcolor(attr.contourcolor);
            String dash;
            if (attr.isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
            str=str+"\\draw[line width="+attr.contourwidth*widthfactor+", color=mycolor"+dash+"]  (" +
                    P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+") ;\n";
        }
        return str;
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle rect, boolean previewmode){
        String str="";
        Segment S=null;
        if (propagation!=null){
            for (int i=0;i<propagation.size();i++){
                AffineTransform T=Utils.groupElement(propagation.elementAt(i));
                S=transform(T);
                str=str+S.tikz(previewmode);
            }
        } 
        else {
            AffineTransform[] T= subgroup.T;
            for(int i=0; i<T.length; i++){
                S=transform(T[i]);
                str=str+S.tikztranslationorbit(utox, rect, previewmode);
            }
        }
        if ((selectionmodeon)||(partialselectionmodeon)){
            str=str+tikzmark(P1, Color.white);
            str=str+tikzmark(P2, Color.white);
        }
        if (ispartiallyselected){
            if (P1.isSelected()){
                str=str+tikzmark(P1, Color.black);
            }
            if (P2.isSelected()){
                str=str+tikzmark(P2, Color.black);
            }
        }
        if (isselected){
            str=str+tikzmark(P1, Color.black);
            str=str+tikzmark(P2, Color.black);
            str=str+"\\draw[line width="+2*penwidth*widthfactor+"]  (" +
                    P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+") ;\n";
        }
        return str;
    }
    
    public void drawlabel(Graphics2D g, AffineTransform utox, String label){
        MPoint R=new MPoint();
        utox.transform(P1, R);
        g.drawString(label,(int) R.getX(), (int) R.getY());
    }
    
    
    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        Segment S=null;
        //System.out.println("Is in background: "+isinbackground);
        if (propagation!=null){
            //System.out.println("Drawing with propagation. Size: "+propagation.size());
            for (int i=0;i<propagation.size();i++){
                AffineTransform T=Utils.groupElement(propagation.elementAt(i));
                //System.out.println("Transform "+i+": "+T);
                S=transform(T);
                S.draw(g,utox,previewmode);
            }
        } 
        else {
            AffineTransform[] T= subgroup.T;
            for(int i=0; i<T.length; i++){
                S=transform(T[i]);
                //System.out.println("Attribute of transformed segment:\n"+S.attr);
                S.drawtranslationorbit(g, utox,rect, previewmode);
                //S.drawlabel(g,utox,""+i);
            }
        }
        //System.out.println("Drawable:"+this);
        //System.out.println("Selectionmodeon:"+selectionmodeon+" isselected:"+isselected);
        if ((selectionmodeon)||(partialselectionmodeon)){
            drawmark(g,utox,P1, Color.white);
            drawmark(g,utox,P2, Color.white);
        }
        if (ispartiallyselected){
            if (P1.isSelected()){
                drawmark(g,utox,P1, color);
            }
            if (P2.isSelected()){
                drawmark(g,utox,P2, color);
            }
        }
        if (isselected){
            drawmark(g,utox,P1, color);
            drawmark(g,utox,P2, color);
            Shape sh=getShape(utox);
            g.setColor(color);
            g.setStroke(new BasicStroke(2*penwidth));
            g.draw(sh);
        }
    }
    
    
    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        //System.out.println("Modify selection: selected point " + selectedPoint);
        MPoint R=new MPoint(x,y);
        xtou.transform(R, selectedPoint);
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        xtou.transform(R, P2);
    }
    
  
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        if (isinbackground){
            return false;
        }
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        if (!shiftdown){
            selectedPoint=null;
            P1.setSelection(false);
            P2.setSelection(false);
        }
        Shape sh=getMarkShape(utox, P1);
        if (sh.contains(x,y)){
            selectedPoint=P1;
            P1.setSelection(true);
            found=true;
        }   
        sh=getMarkShape(utox, P2);
        if (sh.contains(x,y)){
            selectedPoint=P2;
            P2.setSelection(true);
            found=true;
        }
        if (!shiftdown){
            ispartiallyselected=found;
        }
        return found;
    }
    
    public void removeSelectedPoint(){
        if (selectedPoint==P1){
            P2=P1;
        }
        if (selectedPoint==P2){
            P1=P2;
        }
    }
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        Shape sh=getMarkShape(utox, P1);
        if (sh.contains(x,y)){
            selectedPoint=P1;
            found=true;
        }   
        sh=getMarkShape(utox, P2);
        if (sh.contains(x,y)){
            selectedPoint=P2;
            found=true;
        }
        return found;
    }
    
    public void translate(float dx, float dy){
        P1.setLocation(P1.getX()+dx, P1.getY()+dy);
        P2.setLocation(P2.getX()+dx, P2.getY()+dy);
    }
    
    public void translatePartialSelection(float dx, float dy, boolean altdown){
        if (P1.isSelected()){
            P1.setLocation(P1.getX()+dx, P1.getY()+dy);
        }
        if (P2.isSelected()){
            P2.setLocation(P2.getX()+dx, P2.getY()+dy);
        }
    }
}

