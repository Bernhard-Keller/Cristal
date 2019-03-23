import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;


public class Polygone extends Drawable{
    Stack<MPoint> P;
    
public Polygone(){
    Type=CrystalDrawing.POLYGON;
}
    
public Polygone(float x1, float y1, float x2, float y2, Subgroup sg, AffineTransform xtou, Attribute at){
        Type=CrystalDrawing.POLYGON;
        subgroup=sg;
        MPoint P1=new MPoint(x1,y1);
        xtou.transform(P1,P1);
        MPoint P2=new MPoint(x2,y2);
        xtou.transform(P2,P2);
        color=Color.black;
        isdashed=false;
        attr=(Attribute) at.clone();
        P=new Stack();
        P.add(P1);
        P.add(P2);
        selectedPoint=P2;
    }    

public Polygone(MPoint[] Pi, Subgroup sg){
        Type=CrystalDrawing.POLYGON;
        subgroup=sg;
        P=new Stack();
        for (int i=0; i<Pi.length;i++){
            P.add(Pi[i]);
        }
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }

public Polygone(Stack<MPoint> Pi, Subgroup sg){
        Type=CrystalDrawing.POLYGON;
        subgroup=sg;
        P=Pi;
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }

public Polygone copy(MPoint[] Pi){
    Polygone N=copy();
    N.P=new Stack();
    for (int i=0; i<Pi.length;i++){
        N.P.add(Pi[i]);
    }
    return N;
}

public String toString(){
    String str="";
    for (int i=0;i<P.size();i++){
        str=str+P.elementAt(i)+" ";
    }
    return str;
}

public void unselectPoints(){
    for (int i=0; i<P.size();i++){
        P.elementAt(i).setSelection(false);
    }
}

public int weight(){
    return P.size();
}

public MPoint nearPoint(MPoint Pt, float dist){
       MPoint N=null;
       int i=0;
       while ((i<P.size())&&(N==null)){
           if (Pt.distance(P.elementAt(i))<dist){
               N=P.elementAt(i);
           }
           i++;
       }
       if (N!=null){
           N=(MPoint) N.clone();
       }
       return N;
   }

public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
       double[] res=new double[2];
       double distmin=Pt.distance(P.elementAt(0));
       MPoint Pmin=P.elementAt(0).copy();
       int imin=0;
       MPoint Q=new MPoint();
       MPoint P1, P2;
       for (int i=0;i<P.size();i++){
           P1=P.elementAt(i);
           if (i+1<P.size()){
                P2=P.elementAt(i+1);
           }
           else {
                P2=P.elementAt(0);
           }
           for (float t=0; t<1; t=t+0.01f){
               Q.setLocation((1-t)*P1.getX()+t*P2.getX(),(1-t)*P1.getY()+t*P2.getY());
               if (Pt.distance(Q)<distmin){
                   Pmin=Q.copy();
                   distmin=Pt.distance(Q);
                   imin=i;
               }
           }
       }
       res[0]=distmin;
       res[1]=imin;
       closestPoint.setLocation(Pmin);
       return res;
   }

public void write(BufferedWriter out){
        super.write(out);
        try {
            Utils.writeint(out, "Number of polygon points", P.size());
            out.write("//Polygon points"); out.newLine();
            for (int i=0; i<P.size(); i++){
                Utils.writepoint(out, P.elementAt(i));
            }
            out.write("//Index of selected point"); out.newLine();
            if (selectedPoint==null){
                out.write("null"); out.newLine();
            }
            else {
                int n=P.indexOf(selectedPoint);
                out.write(""+n); out.newLine();
            }
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }



public void read(BufferedReader r){
    super.read(r);
    String str;
    int n;
    try {
        if (lastReadLine.equals("//Propagation")){
            n=Utils.readint(r);
        }
        else {
            str=r.readLine();
            n=Integer.parseInt(str);
        }
        str=r.readLine();
        MPoint Q=new MPoint();
        P=new Stack();
        for (int i=0;i<n;i++){
            Q=Utils.readpoint(r);
            P.add(Q);
        }
        r.readLine();
        str=r.readLine();
        if (str.equals("null")){
            selectedPoint=null;
        } 
        else {
            n=Integer.parseInt(str);
            System.out.println("Selected point:"+n);
            if (n>=0){
                selectedPoint=P.elementAt(n);
            }
            else {
                selectedPoint=null;
            }
            }
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
    }

public void add(int x, int y, AffineTransform xtou){
    MPoint R=new MPoint(x,y);
    MPoint S=new MPoint();
    xtou.transform(R, S);
    P.add(S);
    selectedPoint=S;
}

public void removeLastStroke(){
    P.pop();
    if (P.size()>0){
        selectedPoint=P.peek();
    }
    else {
        selectedPoint=null;
    }
}

public boolean isempty(){
    if (P.size()<=1){
        return true;
    }
    else {
        return false;
    }
}

public int size(){
    return P.size();
}

public Object clone(){
    Polygone N=(Polygone) super.clone();
    N.P=new Stack();
    for (int i=0;i<P.size();i++){
        N.P.add((MPoint) P.elementAt(i).clone());
    }
    return N;
}

public Polygone copy(){
    return (Polygone) clone();
}

public void translate(float dx, float dy){
    MPoint Q;
    for (int i=0; i<P.size();i++){
        Q=P.elementAt(i);
        Q.setLocation(Q.getX()+dx, Q.getY()+dy);
    }
}

public void translatePartialSelection(float dx, float dy, boolean altdown){
    MPoint Q;
    for (int i=0; i<P.size();i++){
        Q=P.elementAt(i);
        if (Q.isSelected()){
            Q.setLocation(Q.getX()+dx, Q.getY()+dy);
        }
    }    
}

public MPoint hitPoint(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return null;
        }
        MPoint R=null;
        MPoint Pt=new MPoint();
        for (int i=0;i<P.size();i++){
            utox.transform(P.elementAt(i),Pt);
            if (rect.contains(Pt)){
                R=P.elementAt(i);
            }
        }
        return R;
    }

public MPoint hitPoint(int x, int y, AffineTransform utox){
    if (isinbackground){
            return null;
    }
    MPoint R=null;
    Shape sh=null;
    boolean found=false;
    int i=0;
    while ((i<P.size())&&(!found)){
        sh=getMarkShape(utox, P.elementAt(i));
        if (sh.contains(x,y)){
            R=P.elementAt(i);
            found=true;
        }
        i++;
    }
    return R;
}

public MPoint hitPointAll(int x, int y, AffineTransform utox){
        return hitPoint(x,y,utox);
    }

public void makePartialSelection(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return;
        }
        MPoint R=new MPoint();
        boolean found=false;
        for (int i=0; i<P.size();i++){
            utox.transform(P.elementAt(i), R);
            if (rect.contains(R)){
                P.elementAt(i).setSelection(true);
                found=true;
            }
        }
        if (found){
            setPartialSelection(true);
        }
    }
    

public String datastring(){
    return null;
}

public MPoint barycenter(){
    if (P.size()==0){return null;}
    double x=0;
    double y=0;
    for (int i=0; i<P.size();i++){
        x=x+P.elementAt(i).getX();
        y=y+P.elementAt(i).getY();
    }
    x=x/P.size();
    y=y/P.size();
    return new MPoint((float) x,(float) y);
    }

public Segment[] toSegments(){
    Segment[] seg=new Segment[P.size()];
    seg[0]=new Segment(P.elementAt(0),P.elementAt(1),subgroup);
    for (int i=1;i+1<P.size();i++){
        seg[i]=new Segment(P.elementAt(i),P.elementAt(i+1),subgroup);
    }
    seg[P.size()-1]=new Segment(P.elementAt(P.size()-1),P.elementAt(0),subgroup);
    return seg;
}
    

public Polygone intersect(StraightLine L, MPoint innerPoint, MotivePanel mp){
    Polygone newPoly=copy();
    Stack<MPoint> intersectionPoints=new Stack();
    Stack<Integer> intersectionIndices=new Stack();
    Stack<Float> intersectionParams=new Stack();
    for (int i=0;i+1<P.size();i++){
        StraightLine L1=new StraightLine(P.elementAt(i),P.elementAt(i+1));
        if (!L.isParallel(L1)){
            float t=L1.intersectionParam(L);
            if ((0.01<=t) && (t<=0.99)){
                MPoint I=L1.intersectionPoint(L);
                intersectionPoints.add(I);
                intersectionParams.add(new Float(t));
                intersectionIndices.add(new Integer(i));
            }
        }
    }
    StraightLine L1=new StraightLine(P.elementAt(P.size()-1),P.elementAt(0));
    if (!L.isParallel(L1)){
            float t=L1.intersectionParam(L);
            if ((0.01<=t) && (t<=0.99)){
                MPoint I=L1.intersectionPoint(L);
                intersectionPoints.add(I);
                intersectionParams.add(new Float(t));
                intersectionIndices.add(new Integer(P.size()-1));
            }
        }
    if (intersectionPoints.size()==1){
        int i1=intersectionIndices.elementAt(0).intValue();
        float t1=intersectionParams.elementAt(0).floatValue();
        //System.out.println("One intersection point. Parameters: t1="+t1+" i1="+i1);
    }
    if (intersectionPoints.size()>1){
        int i1=intersectionIndices.elementAt(0).intValue();
        int i2=intersectionIndices.elementAt(1).intValue();
        MPoint P1=intersectionPoints.elementAt(0);
        MPoint P2=intersectionPoints.elementAt(1);
        float t1=intersectionParams.elementAt(0);
        float t2=intersectionParams.elementAt(1);
        //System.out.println("Two intersection points. Parameters: t1="+t1+" t2="+t2+" i1="+i1+" i2="+i2);
        //System.out.println("Points: "+P1+", "+P2);
        Circle C1=new Circle(P1, 0.1f, Color.red, mp.M.subgroup);
        C1.propagateOne();
        Circle C2=new Circle(P2, 0.1f, Color.red, mp.M.subgroup);
        C2.propagateOne();
        mp.M.add(C1);
        mp.M.add(C2);
        if (i2+1==P.size()){
            P.add(P2);
        }
        else {
            P.add(i2+1,P2);
        }
        P.add(i1+1,P1);
        //System.out.println("Is P1 on L? "+StraightLine.cw(L.P1,L.P2,P1));
        //System.out.println("Is P2 on L? "+StraightLine.cw(L.P1, L.P2, P2));
        float cw0=StraightLine.cw(P1, P2, innerPoint);
        //System.out.println("cw(P1,P2,innerPoint)="+cw0);
        Stack<MPoint> Pnew=new Stack();
        Pnew.add(P1.copy());
        int ctr=i1+2;
        if (ctr>=P.size()){ctr=ctr-P.size();}
        while (ctr!=i1+1){
            MPoint P3=P.elementAt(ctr).copy();
            float cw1=StraightLine.cw(P1,P2,P3);
            //System.out.println("ctr: "+ctr+" Point: "+P3+ " cw="+cw1+ " d(P1,P3)="+P1.distance(P3)+" d(P2,P3)="+P2.distance(P3));
            float cw0cw1=cw0*cw1;
            if ((cw0cw1>0)||(P.elementAt(ctr)==P2)){
                Pnew.add(P3);
                //System.out.println("Added Point.");
            }
            ctr=ctr+1;
            if (ctr>=P.size()){ctr=ctr-P.size();}
        }
        //System.out.println("Nber of points in intersection: "+Pnew.size());
        newPoly=new Polygone(Pnew,subgroup);
    }
    //System.out.println("Nber of points in intersection: "+newPoly.P.size());
    //newPoly.propagateOne();
    return newPoly;
}

public Polygone intersect(StraightLine L, MPoint innerPoint){
    Polygone newPoly=copy();
    Stack<MPoint> intersectionPoints=new Stack();
    Stack<Integer> intersectionIndices=new Stack();
    Stack<Float> intersectionParams=new Stack();
    for (int i=0;i+1<P.size();i++){
        StraightLine L1=new StraightLine(P.elementAt(i),P.elementAt(i+1));
        if (!L.isParallel(L1)){
            float t=L1.intersectionParam(L);
            if ((0.001<=t) && (t<=1.001)){
                MPoint I=L1.intersectionPoint(L);
                intersectionPoints.add(I);
                intersectionParams.add(new Float(t));
                intersectionIndices.add(new Integer(i));
            }
        }
    }
    StraightLine L1=new StraightLine(P.elementAt(P.size()-1),P.elementAt(0));
    if (!L.isParallel(L1)){
            float t=L1.intersectionParam(L);
            if ((0.001<=t) && (t<=1.001)){
                MPoint I=L1.intersectionPoint(L);
                intersectionPoints.add(I);
                intersectionParams.add(new Float(t));
                intersectionIndices.add(new Integer(P.size()-1));
            }
        }
    if (intersectionPoints.size()==1){
        int i1=intersectionIndices.elementAt(0).intValue();
        float t1=intersectionParams.elementAt(0).floatValue();
        //System.out.println("One intersection point. Parameters: t1="+t1+" i1="+i1);
    }
    if (intersectionPoints.size()>1){
        int i1=intersectionIndices.elementAt(0).intValue();
        int i2=intersectionIndices.elementAt(1).intValue();
        MPoint P1=intersectionPoints.elementAt(0);
        MPoint P2=intersectionPoints.elementAt(1);
        float t1=intersectionParams.elementAt(0);
        float t2=intersectionParams.elementAt(1);
        //System.out.println("Two intersection points. Parameters: t1="+t1+" t2="+t2+" i1="+i1+" i2="+i2);
        //System.out.println("Points: "+P1+", "+P2);
        //Circle C1=new Circle(P1, 0.1f, Color.red, mp.M.subgroup);
        //C1.propagateOne();
        //Circle C2=new Circle(P2, 0.1f, Color.red, mp.M.subgroup);
        //C2.propagateOne();
        //mp.M.add(C1);
        //mp.M.add(C2);
        if (i2+1==P.size()){
            P.add(P2);
        }
        else {
            P.add(i2+1,P2);
        }
        P.add(i1+1,P1);
        //System.out.println("Is P1 on L? "+StraightLine.cw(L.P1,L.P2,P1));
        //System.out.println("Is P2 on L? "+StraightLine.cw(L.P1, L.P2, P2));
        float cw0=StraightLine.cw(P1, P2, innerPoint);
        //System.out.println("cw(P1,P2,innerPoint)="+cw0);
        Stack<MPoint> Pnew=new Stack();
        Pnew.add(P1.copy());
        int ctr=i1+2;
        if (ctr>=P.size()){ctr=ctr-P.size();}
        while (ctr!=i1+1){
            MPoint P3=P.elementAt(ctr).copy();
            float cw1=StraightLine.cw(P1,P2,P3);
            //System.out.println("ctr: "+ctr+" Point: "+P3+ " cw="+cw1+ " d(P1,P3)="+P1.distance(P3)+" d(P2,P3)="+P2.distance(P3));
            float cw0cw1=cw0*cw1;
            if ((cw0cw1>0)||(P.elementAt(ctr)==P2)){
                Pnew.add(P3);
                //System.out.println("Added Point.");
            }
            ctr=ctr+1;
            if (ctr>=P.size()){ctr=ctr-P.size();}
        }
        //System.out.println("Nber of points in intersection: "+Pnew.size());
        newPoly=new Polygone(Pnew,subgroup);
    }
    //System.out.println("Nber of points in intersection: "+newPoly.P.size());
    //newPoly.propagateOne();
    return newPoly;
}



public Shape getShape(AffineTransform utox) {
        Polygon poly=new Polygon();
        MPoint R=new MPoint();
        for (int i=0; i<P.size();i++){
            utox.transform(P.elementAt(i), R);
            poly.addPoint((int) R.getX(), (int) R.getY());
        }
        return poly;
    }

public Polygone transform(AffineTransform T){
    Polygone S=copy();
    for (int j=0; j<P.size();j++){
        T.transform(P.elementAt(j),S.P.elementAt(j));
    }
    return S;
}




public void drawtranslationorbit(Graphics2D g, AffineTransform utox, Rectangle rect,boolean previewmode){
    
    int ulo=0;
    int uhi=0;
    int vlo=0;
    int vhi=0;
    
    int[] range;
    for (int i=0; i<P.size(); i++){
        range=uvrange(P.elementAt(i),utox,rect);
        
        ulo=Math.min(range[0],ulo);
        uhi=Math.max(range[1],uhi);
        vlo=Math.min(range[2],vlo);
        vhi=Math.max(range[3],vhi);
    }
    
    AffineTransform vtou;

    MPoint V=new MPoint();
    MPoint[] U=new MPoint[P.size()];
    Polygone S;
    MPoint Pi=new MPoint();

    for (int i=ulo; i<=uhi; i++){
        for (int j=vlo; j<=vhi; j++){
           V=new MPoint(i,j);
           for (int k=0; k<P.size();k++){
               Pi=new MPoint((float) P.elementAt(k).getX(), (float) P.elementAt(k).getY());
               vtou=transformvtou(Pi);
               U[k]=new MPoint();
               vtou.transform(V, U[k]);
           }
           S=copy(U);
           S.draw(g, utox, previewmode);
        }
    }
}

public String tikztranslationorbit(AffineTransform utox, Rectangle rect,boolean previewmode){
        String str="";

        int ulo=0;
        int uhi=0;
        int vlo=0;
        int vhi=0;

        int[] range;
        for (int i=0; i<P.size(); i++){
            range=uvrange(P.elementAt(i),utox,rect);

            ulo=Math.min(range[0],ulo);
            uhi=Math.max(range[1],uhi);
            vlo=Math.min(range[2],vlo);
            vhi=Math.max(range[3],vhi);
        }

        AffineTransform vtou;

        MPoint V=new MPoint();
        MPoint[] U=new MPoint[P.size()];
        Polygone S;
        MPoint Pi=new MPoint();

        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               for (int k=0; k<P.size();k++){
                   Pi=new MPoint((float) P.elementAt(k).getX(), (float) P.elementAt(k).getY());
                   vtou=transformvtou(Pi);
                   U[k]=new MPoint();
                   vtou.transform(V, U[k]);
               }
               S=copy(U);
               str=str+S.tikz(previewmode);
            }
        }
        return str;
    }

public String tikz(boolean previewmode){
        String str="";
        if (!previewmode){
            str=str+"\\draw ("
                    + P.elementAt(0).getX()+", "+ P.elementAt(0).getY() +") ";
            for (int i=1;i<P.size();i++){
                str=str+"-- ("+P.elementAt(i).getX()+", "+P.elementAt(i).getY()+") ";
            }
            str=str+"-- cycle ;\n";
        }
        else {
            str=str+tikzcolor(attr.fillcolor);
            str=str+"\\fill[color=mycolor] ("
                    + P.elementAt(0).getX()+", "+ P.elementAt(0).getY() +") ";
            for (int i=1;i<P.size();i++){
                str=str+"-- ("+P.elementAt(i).getX()+", "+P.elementAt(i).getY()+") ";
            }
            str=str+"-- cycle ;\n";
            str=str+tikzcolor(attr.contourcolor);
            String dash;
            if (attr.isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
            str=str+"\\draw[line width="+attr.contourwidth*widthfactor+", color=mycolor"+dash+"] ("+
            + P.elementAt(0).getX()+", "+ P.elementAt(0).getY() +") ";
            for (int i=1;i<P.size();i++){
                str=str+"-- ("+P.elementAt(i).getX()+", "+P.elementAt(i).getY()+") ";
            }
            str=str+" -- cycle ;\n";
        }
        return str;
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle rect, boolean previewmode){
        String str="";
        Polygone S=null;
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
            for (int i=0;i<P.size();i++){
                str=str+tikzmark(P.elementAt(i),Color.white);
            }
        }
        if (ispartiallyselected){
            for (int i=0;i<P.size();i++){
                if (P.elementAt(i).isSelected()){
                    str=str+tikzmark(P.elementAt(i), Color.black);
                }
            }
        }
        if (isselected){
            for (int i=0;i<P.size();i++){
                str=str+tikzmark(P.elementAt(i), Color.black);
            }
            str=str+"\\draw[line width="+2*penwidth*widthfactor+"] ("
                    + P.elementAt(0).getX()+", "+ P.elementAt(0).getY() +") ";
            for (int i=1;i<P.size();i++){
                str=str+"-- ("+P.elementAt(i).getX()+", "+P.elementAt(i).getY()+") ";
            }
            str=str+"cycle ;\n";
        }
        return str;
    }
    
public void drawlabels(Graphics2D g, AffineTransform utox){
    MPoint R=new MPoint();
    for (int i=0;i<P.size();i++){
        utox.transform(P.elementAt(i), R);
        g.drawString(""+i, R.x, R.y);
    }
}

public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
    if (propagation!=null){
        for (int i=0;i<propagation.size();i++){
                AffineTransform gel=Utils.groupElement(propagation.elementAt(i));
                //System.out.println("Transform "+i+": "+gel);
                Polygone poly=transform(gel);
                poly.draw(g,utox,previewmode);
            }
    }
    else {
        Polygone S=null;
        AffineTransform[] T= subgroup.T;
        for(int i=0; i<T.length; i++){
            S=transform(T[i]);
            S.drawtranslationorbit(g, utox,rect, previewmode);
        }
    }
    //drawlabels(g,utox);
    if ((selectionmodeon)||(partialselectionmodeon)){
        for (int i=0; i<P.size();i++){
            drawmark(g,utox,P.elementAt(i), Color.white);
        }
        if (ispartiallyselected){
            for (int i=0; i<P.size();i++){
                if (P.elementAt(i).isSelected()){
                    drawmark(g,utox,P.elementAt(i),Color.black);
                }
            }
        }
        if (isselected){
            Shape sh=getShape(utox);
            g.setStroke(new BasicStroke(2*penwidth));
            g.draw(sh);
            for (int i=0; i<P.size(); i++){
                drawmark(g,utox,P.elementAt(i), Color.black);
            }
        }
    }
}
    
    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        //System.out.println("Modify selection: selected point " + selectedPoint);
        MPoint R=new MPoint(x,y);
        xtou.transform(R, selectedPoint);
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        xtou.transform(R, P.elementAt((P.size()-1)));
    }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){
        for (int i=0; i<P.size(); i++){
            if (P.elementAt(i).isSelected()){
                Points.add(P.elementAt(i));
            }
        }
    }
  
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        if (isinbackground){
            return false;
        }
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        if (!shiftdown){
            selectedPoint=null;
            for (int i=0;i<P.size();i++){
              P.elementAt(i).setSelection(false);
            }
        }
        for (int i=0;i<P.size();i++){
            Shape sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                P.elementAt(i).setSelection(true);
            } 
        }
        int i=0;
        while ((i<P.size())&&(!found)){
            Shape sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                selectedPoint=P.elementAt(i);
                found=true;
            } 
            i++;
        }
        if (!shiftdown){
            ispartiallyselected=found;
        }
        return found;
    }
    
    public void removeSelectedPoint(){
        P.remove(selectedPoint);
    }
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        selectedPoint=null;
        int i=0;
        while ((i<P.size())&&(!found)){
            Shape sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                found=true;
                selectedPoint=P.elementAt(i);
            }  
            i++;
        }
        return found;
    }
}
