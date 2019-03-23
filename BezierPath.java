
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
import java.awt.geom.GeneralPath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class BezierPath extends Drawable {

Stack<MPoint> P;
MPoint symTangentPoint=null;
int status;

public BezierPath(){
    Type=CrystalDrawing.BEZIERPATH;
}

public BezierPath(float x1, float y1, float x2, float y2, Subgroup sg, AffineTransform xtou, Attribute at){
        Type=CrystalDrawing.BEZIERPATH;
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

public BezierPath(Subgroup sg){
    Type=CrystalDrawing.BEZIERPATH;
    subgroup=sg;
    P=new Stack();
    color=Color.black;
    isdashed=false;
    attr=new Attribute();
}

public BezierPath(MPoint[] Pi, Subgroup sg){
        Type=CrystalDrawing.BEZIERPATH;
        subgroup=sg;
        P=new Stack();
        for (int i=0; i<Pi.length;i++){
            P.add(Pi[i]);
        }
        color=Color.black;
        isdashed=false;
        attr=new Attribute();
    }

public void unselectPoints(){
    for (int i=0; i<P.size();i++){
        P.elementAt(i).setSelection(false);
    }
}

public void getPartiallySelectedPoints(Stack<MPoint> Points){
        for (int i=0; 3*i<P.size(); i++){
            if (P.elementAt(3*i).isSelected()){
                Points.add(P.elementAt(i));
            }
        }
    }

public String toString(){
    String str="";
    for (int i=0; i<P.size(); i++){
        str=str+P.elementAt(i) + " ";
        if ((i%3==0)&&(i>0)){
            str=str+"\n";
        }
    }
    return str;
}

public int weight(){
    return P.size()/3;
}

public MPoint nearPoint(MPoint Pt, float dist){
       MPoint N=null;
       int i=0;
       while ((i<P.size())&&(N==null)){
           if (Pt.distance(P.elementAt(i))<dist){
               N=P.elementAt(i);
           }
           i=i+3;
       }
       if (N!=null){
           N=(MPoint) N.clone();
       }
       return N;
   }

public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
       double[] res=new double[3];
       double distmin=Pt.distance(P.elementAt(0));
       MPoint Pmin=P.elementAt(0).copy();
       int imin=0;
       float tmin=0;
       MPoint Q=new MPoint();
       MPoint P0, P1, P2, P3;
       double x,y;
       for (int i=0;i<P.size()/3;i++){
           P0=P.elementAt(3*i);
           P1=P.elementAt(3*i+1);
           P2=P.elementAt(3*i+2);
           P3=P.elementAt(3*i+3);
           for (float t=0; t<1; t=t+0.01f){
               x=(1-t)*(1-t)*(1-t)*P0.x+3*t*(1-t)*(1-t)*P1.x+3*t*t*(1-t)*P2.x+t*t*t*P3.x;
               y=(1-t)*(1-t)*(1-t)*P0.y+3*t*(1-t)*(1-t)*P1.y+3*t*t*(1-t)*P2.y+t*t*t*P3.y;
               Q.setLocation(x,y);
               if (Pt.distance(Q)<distmin){
                   Pmin=Q.copy();
                   distmin=Pt.distance(Q);
                   imin=i;
                   tmin=t;
               }
           }
       }
       res[0]=distmin;
       res[1]=imin;
       res[2]=tmin;
       closestPoint.setLocation(Pmin);
       return res;
   }

public void write(BufferedWriter out){
        super.write(out);
        try {
            Utils.writeint(out, "Status", status);
            Utils.writestring(out, "Symmetric tangent point");
            if (symTangentPoint==null){
                Utils.writestring(out,"null");
            } 
            else {
                Utils.writepoint(out, symTangentPoint);
            }
            Utils.writeint(out, "Number of Bezier points", P.size());
            out.write("//Bezier points"); out.newLine();
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
    try {
        System.out.println("Last read line: "+lastReadLine);
        if (lastReadLine.equals("//Propagation")){
            status=Utils.readint(r);
        }
        else {
            str=r.readLine();
            status=Integer.parseInt(str);
        }
        str=r.readLine();
        System.out.println(str);
        str=r.readLine();
        System.out.println(str);
        if (str.equals("null")){
            symTangentPoint=null;
        }
        else {
          String[] fields=str.split(" ");
          float x=(float) Double.parseDouble(fields[0]);
          float y=(float) Double.parseDouble(fields[1]);
          symTangentPoint=new MPoint(x,y);
        }
        int n=Utils.readint(r);
        r.readLine();
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

public void setPartialSelectionMode(boolean sel){
        partialselectionmodeon=sel;
        System.out.println("P.size()="+P.size());
    }

public void add(MPoint pt){
    P.add(pt);
}

public void add(int x, int y, AffineTransform xtou){
    MPoint R=new MPoint(x,y);
    MPoint S=new MPoint();
    xtou.transform(R, S);
    P.add(S);
    selectedPoint=S;
    System.out.println("Added point. Size="+P.size());
}

public void addtwice(int x, int y, AffineTransform xtou){
    MPoint R=new MPoint(x,y);
    MPoint S=new MPoint();
    xtou.transform(R, S);
    P.add(S);
    P.add(S);
    selectedPoint=S;
    System.out.println("Added two points. Size="+P.size());
}

public void defSymTangentPoint(int x, int y, AffineTransform xtou){
    MPoint R=new MPoint(x,y);
    xtou.transform(R,R);
    symTangentPoint=R;
    System.out.println("Defined sym. tangent point. Size="+P.size());
}

public void removeLastStroke(){
    int n=P.size();
    System.out.println("P.size() :"+P.size());
    System.out.println("P.size() mod 3: "+(n%3));
    if (P.size()==2){
        P.removeAllElements();
        status=CrystalDrawing.WAITING_FOR_BEZIER1;
    }
    if ((n%3 == 2) && (P.size()>2)){
        P.pop();
        MPoint P2=P.elementAt(size()-1);
        P.set(size()-2, (MPoint) P2.clone());
        status=CrystalDrawing.WAITING_FOR_BEZIER4;
        System.out.println("2 P.size(): "+P.size());
        System.out.println("Status: "+status);
    }
    if (n%3 ==1 ){
        P.pop();
        P.pop();
        status=CrystalDrawing.WAITING_FOR_BEZIER3;
        System.out.println("3 P.size(): "+P.size());
        System.out.println("Status: "+status);
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

protected Object clone(){
    BezierPath N=(BezierPath) super.clone();
    N.P=new Stack();
    for (int i=0;i<P.size();i++){
        N.P.add((MPoint) P.elementAt(i).clone());
    }
    N.status=status;
    if (symTangentPoint==null){
        N.symTangentPoint=null;
    } 
    else {
        symTangentPoint = (MPoint) symTangentPoint.clone();
    }
    return N;
}

public BezierPath copy(){
    return (BezierPath) clone();
}

public BezierPath copy(MPoint[] U){
    BezierPath N=copy();
    N.P=new Stack();
    for (int i=0;i<P.size();i++){
        N.P.add(U[i]);
    }
    return N;
}

public void translate(float dx, float dy){
    MPoint Q;
    for (int i=0; i<P.size();i++){
        Q=P.elementAt(i);
        Q.setLocation(Q.getX()+dx, Q.getY()+dy);
    }
}

public void translatePartialSelection(float dx, float dy, boolean altdown){
    MPoint Q, Qnew;
    for (int i=0; i<P.size();i++){
        Q=P.elementAt(i);
        Qnew=new MPoint((float) Q.getX()+dx, (float) Q.getY()+dy);
        if ((i%3==0)&&(Q.isSelected())){
            Q.setLocation(Q.getX()+dx, Q.getY()+dy);
            if (i>0){
                Q=P.elementAt(i-1);
                Q.setLocation(Q.getX()+dx, Q.getY()+dy);
            }
            if (i+1<P.size()){
                Q=P.elementAt(i+1);
                Q.setLocation(Q.getX()+dx, Q.getY()+dy);
            }
        }
        if ((i%3==1)&&(Q.isSelected())) {
            if (!altdown){
                if (i-2>0){
                    AffineTransform T=Utils.rotation(P.elementAt(i-1),Q, Qnew);
                    MPoint Pm1=P.elementAt(i-2);
                    T.transform(Pm1, Pm1);
                }
            }
            Q.setLocation(Qnew);
        }
        if ((i%3==2)&&(Q.isSelected())){
            if (!altdown){
                if (i+2<P.size()){
                    AffineTransform T=Utils.rotation(P.elementAt(i+1), Q, Qnew);
                    MPoint Pp1=P.elementAt(i+2);
                    T.transform(Pp1, Pp1);
                }
            }
            Q.setLocation(Qnew);
        }
    }
}

public MPoint hitPoint(Rectangle rect, AffineTransform utox){
    if (isinbackground){
            return null;
    }
    MPoint R=null;
    MPoint Pt=new MPoint();
    boolean found=false;
    int i=0;
    while ((i<P.size())&&(!found)){
        if ((i==1)&&(P.size()==2)){
            utox.transform(P.elementAt(i),Pt);
            if (rect.contains(Pt)){
                found=true;
                R=P.elementAt(i);
                //System.out.println("Total nber of points:"+P.size()+", found point:"+i);
            }
        }
        if (i%3==0){
            utox.transform(P.elementAt(i),Pt);
            if (rect.contains(Pt)){
                found=true;
                R=P.elementAt(i);
                //System.out.println("Total nber of points:"+P.size()+", found point:"+i);
            }
        }
        i++;
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
        if ((i==1)&&(P.size()==2)){
            sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                R=P.elementAt(i);
                found=true;
                //System.out.println("Total nber of points:"+P.size()+", found point:"+i);
            }
        }
        if (i%3==0){
            sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                R=P.elementAt(i);
                found=true;
                //System.out.println("Total nber of points:"+P.size()+", found point:"+i);
            }
        }
        i++;
    }
    return R;
}

public MPoint hitPointAll(int x, int y, AffineTransform utox){
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
            //System.out.println("Total nber of points:"+P.size()+", found point:"+i);
        }
        i++;
    }
    return R;
}

public void makePartialSelection(Rectangle rect, AffineTransform utox){
        if (isinbackground){
            return;
        }
        MPoint R=new MPoint();
        boolean found=false;
        for (int i=0; i<P.size();i++){
            utox.transform(P.elementAt(i), R);
            if ((i%3==0)&&(rect.contains(R))){
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


    
public Shape getShape(AffineTransform utox) {
    Shape sh=null;
    Polygon poly=new Polygon();
    //System.out.println("P.size()="+P.size());
    if (P.size()<=2){
       MPoint P1=(MPoint) P.elementAt(0).clone();
       MPoint P2=(MPoint) P.elementAt(1).clone();
       utox.transform(P1,P1);
       utox.transform(P2, P2);
       poly.addPoint((int) P1.getX(), (int) P1.getY());
       poly.addPoint((int) P2.getX(), (int) P2.getY());
       sh=poly;
    }
    if (P.size()>=3){
        MPoint P1, P2, P3;
        MPoint P1x=new MPoint();
        MPoint P2x=new MPoint();
        MPoint P3x=new MPoint();
        MPoint R=new MPoint();
        GeneralPath path=new GeneralPath();
        utox.transform(P.elementAt(0), R);
        path.moveTo(R.getX(), R.getY());
        for (int ctr=0; ctr<P.size()/3; ctr++){
            P1=P.elementAt(3*ctr+1);
            //System.out.println("P1:"+(3*ctr+1));
            P2=P.elementAt(3*ctr+2);
            P3=P.elementAt(3*ctr+3);
            utox.transform(P1, P1x);
            utox.transform(P2, P2x);
            utox.transform(P3,P3x);
            path.curveTo(P1x.getX(), P1x.getY(), P2x.getX(), P2x.getY(), P3x.getX(), P3x.getY());
        }
        /*int n=3*(P.size()/3-1);
        if (P.size()-n>=3){
            P1=P.elementAt(n);
            //System.out.println("P1:"+n);
            P2=P.elementAt(n+1);
            P3=P.elementAt(n+2);
            utox.transform(P1, P1x);
            utox.transform(P2, P2x);
            utox.transform(P3,P3x);
            path.curveTo(P1x.getX(), P1x.getY(), P2x.getX(), P2x.getY(), P3x.getX(), P3x.getY());
        }*/
        sh=path;
    }
    return sh;
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
    
    //System.out.println("Polygone: ulo,uhi,vlo,vhi:"+ulo+","+uhi+","+vlo+","+vhi);
      
        
        AffineTransform vtou;
        
        MPoint V=new MPoint();
        MPoint[] U=new MPoint[P.size()];
        BezierPath S;
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



public BezierPath transform(AffineTransform T){
    BezierPath S=copy();
    for (int j=0; j<P.size();j++){
        T.transform(P.elementAt(j),S.P.elementAt(j));
    }
    return S;
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
        BezierPath S;
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
        if (P.size()<=2){
           MPoint P1=P.elementAt(0);
           MPoint P2=P.elementAt(1);
           str=str+"\\draw ("+P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+");\n";
        }
        if (P.size()>=3){
            MPoint P0,P1, P2, P3;
            P0=P.elementAt(0);
            str=str+"\\draw ("+P0.getX()+","+P0.getY()+")";
            for (int ctr=0; ctr<P.size()/3; ctr++){
                P1=P.elementAt(3*ctr+1);
                P2=P.elementAt(3*ctr+2);
                P3=P.elementAt(3*ctr+3);
                str=str+".. controls ("+P1.getX()+","+P1.getY()+") and ("+P2.getX()+","+P2.getY()+") .. ";
                str=str+"("+P3.getX()+","+P3.getY()+")";
            }
            str=str+" -- cycle ;\n";
        }
    }
    else {
        if (P.size()<=2){
           MPoint P1=P.elementAt(0);
           MPoint P2=P.elementAt(1);
           str=str+tikzcolor(attr.contourcolor);
           String dash;
            if (attr.isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
           str=str+"\\draw[color=mycolor, line width="+attr.contourwidth*widthfactor+dash+"] ("+P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+");\n";
        }
        if (P.size()>=3){
            MPoint P0,P1, P2, P3;
            P0=P.elementAt(0);
            str=str+tikzcolor(attr.fillcolor);
            str=str+"\\fill[color=mycolor] ("+P0.getX()+","+P0.getY()+")";
            for (int ctr=0; ctr<P.size()/3; ctr++){
                P1=P.elementAt(3*ctr+1);
                P2=P.elementAt(3*ctr+2);
                P3=P.elementAt(3*ctr+3);
                str=str+".. controls ("+P1.getX()+","+P1.getY()+") and ("+P2.getX()+","+P2.getY()+") .. ";
                str=str+"("+P3.getX()+","+P3.getY()+")";
            }
            str=str+" -- cycle ;\n";
            P0=P.elementAt(0);
            str=str+tikzcolor(attr.contourcolor);
            String dash;
            if (attr.isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
            str=str+"\\draw[color=mycolor, line width="+attr.contourwidth*widthfactor+dash+"] ("+P0.getX()+","+P0.getY()+")";
            for (int ctr=0; ctr<P.size()/3; ctr++){
                P1=P.elementAt(3*ctr+1);
                P2=P.elementAt(3*ctr+2);
                P3=P.elementAt(3*ctr+3);
                str=str+".. controls ("+P1.getX()+","+P1.getY()+") and ("+P2.getX()+","+P2.getY()+") .. ";
                str=str+"("+P3.getX()+","+P3.getY()+")";
            }
            str=str+" -- cycle ;\n";
        }
    }
    return str;
}


public String tikzorbit(AffineTransform utox, Rectangle rect, boolean previewmode){
        String str="";
        if (propagation!=null){
            for (int i=0;i<propagation.size();i++){
                AffineTransform gel=Utils.groupElement(propagation.elementAt(i));
                //System.out.println("Transform "+i+": "+gel);
                BezierPath Bez=transform(gel);
                str=str+Bez.tikz(previewmode);
            }
        } 
        else {
            AffineTransform[] T= subgroup.T;
            for(int i=0; i<T.length; i++){
                BezierPath S=transform(T[i]);
                str=str+S.tikztranslationorbit(utox,rect, previewmode);
            }
        }
        //System.out.println("Selection mode: "+selectionmodeon);
        if (selectionmodeon){
            //System.out.println("Selection mode on for:"+toString());
            for (int i=0; i<P.size();i++){
                if (i%3==0){
                    //System.out.println("Drawing mark "+i);
                    str=str+tikzmark(P.elementAt(i), Color.white);
                }
            }
            if (P.size()==2){
                   //System.out.println("Drawing mark 1");
                   str=str+tikzmark(P.elementAt(1), Color.white);
            }
        }
        //System.out.println("Partial selection mode: "+partialselectionmodeon);
        if (partialselectionmodeon){
            for (int i=0; i<P.size()/3; i++){
                MPoint P0=P.elementAt(3*i);
                MPoint P1=P.elementAt(3*i+1);
                MPoint P2=P.elementAt(3*i+2);
                MPoint P3=P.elementAt(3*i+3);
                str=str+tikzline(P0,P1,Color.black);
                str=str+tikzline(P2,P3,Color.black);
            }
            for (int i=0; i<P.size();i++){
                str=str+tikzmark(P.elementAt(i), Color.white);
            }
        }
        if (ispartiallyselected){
            for (int i=0; i<P.size();i++){
                if (P.elementAt(i).isSelected()){
                    str=str+tikzmark(P.elementAt(i),Color.black);
                }
            }
        }
        if (isselected){
            for (int i=0; i<P.size()/3; i++){
                MPoint P0=P.elementAt(3*i);
                MPoint P1=P.elementAt(3*i+1);
                MPoint P2=P.elementAt(3*i+2);
                MPoint P3=P.elementAt(3*i+3);
                str=str+"\\draw[line width="+2*penwidth*widthfactor+"] ("+P0.getX()+","+P0.getY()+") .. controls ("+P1.getX()+","+P1.getY()+") and ("+
                        P2.getX()+","+P2.getY()+") .. ("+P3.getX()+","+P3.getY()+");\n";
            }
            for (int i=0; i<P.size(); i++){
                if (i%3==0){
                    //System.out.println("Drawing selection mark at point "+i+": "+P.elementAt(i));
                    str=str+tikzmark(P.elementAt(i), Color.black);
                }
            }
            if (P.size()==2){
                //System.out.println("Drawing selection mark at point 1 : "+P.elementAt(1));
                str=str+tikzmark(P.elementAt(1), Color.black);
            }
        }
    return str;
    }

public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
    //System.out.println("Draw orbit for Bezier path");
    //System.out.println(toString());
    if (propagation!=null){
        for (int i=0;i<propagation.size();i++){
            AffineTransform gel=Utils.groupElement(propagation.elementAt(i));
            //System.out.println("Transform "+i+": "+gel);
            BezierPath Bez=transform(gel);
            Bez.draw(g,utox,previewmode);
        }
    } 
    else {
        AffineTransform[] T= subgroup.T;
        for(int i=0; i<T.length; i++){
            BezierPath S=transform(T[i]);
            S.drawtranslationorbit(g, utox,rect, previewmode);
        }
    }
    //System.out.println("Selection mode: "+selectionmodeon);
    if (selectionmodeon){
        //System.out.println("Selection mode on for:"+toString());
        for (int i=0; i<P.size();i++){
            if (i%3==0){
                //System.out.println("Drawing mark "+i);
                drawmark(g,utox,P.elementAt(i), Color.white);
            }
        }
        if (P.size()==2){
               //System.out.println("Drawing mark 1");
               drawmark(g, utox, P.elementAt(1), Color.white);
        }
    }
    //System.out.println("Partial selection mode: "+partialselectionmodeon);
    if (partialselectionmodeon){
        for (int i=0; i<P.size()/3; i++){
            MPoint P0=P.elementAt(3*i);
            MPoint P1=P.elementAt(3*i+1);
            MPoint P2=P.elementAt(3*i+2);
            MPoint P3=P.elementAt(3*i+3);
            drawline(g,utox,P0,P1,Color.black);
            drawline(g,utox,P2,P3,Color.black);
        }
        for (int i=0; i<P.size()-1;i++){
            drawmark(g,utox,P.elementAt(i), Color.white);
        }
    }
    if (ispartiallyselected){
        for (int i=0; i<P.size()-1;i++){
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
            if (i%3==0){
                //System.out.println("Drawing selection mark at point "+i+": "+P.elementAt(i));
                drawmark(g,utox,P.elementAt(i), Color.black);
            }
        }
        if (P.size()==2){
            //System.out.println("Drawing selection mark at point 1 : "+P.elementAt(1));
            drawmark(g,utox,P.elementAt(1), Color.black);
        }
    }
    }



  
public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        //System.out.println("Modify selection: selected point " + selectedPoint);
        MPoint newSelectedPoint=new MPoint(x,y);
        xtou.transform(newSelectedPoint, newSelectedPoint);
        if (altdown){
            selectedPoint.setLocation(newSelectedPoint);
            return;
        }
        double dx=newSelectedPoint.getX()-selectedPoint.getX();
        double dy=newSelectedPoint.getY()-selectedPoint.getY();
        int i=P.indexOf(selectedPoint);
        double x0;
        double y0;
        if (i%3==0){
            for (int j=Math.max(0,i-1);j<=Math.min(P.size()-1,i+1);j++){
                x0=P.elementAt(j).getX()+dx;
                y0=P.elementAt(j).getY()+dy;
                P.elementAt(j).setLocation(x0,y0);
            }
        } 
        else if (i%3==1) {
            if (i-2>0){
                AffineTransform T=Utils.rotation(P.elementAt(i-1),selectedPoint, newSelectedPoint);
                MPoint Pm1=P.elementAt(i-2);
                T.transform(Pm1, Pm1);
            }
            selectedPoint.setLocation(newSelectedPoint);
        }
        else if (i%3==2){
            if (i+2<P.size()){
                AffineTransform T=Utils.rotation(P.elementAt(i+1), selectedPoint, newSelectedPoint);
                MPoint Pp1=P.elementAt(i+2);
                T.transform(Pp1, Pp1);
            }
            selectedPoint.setLocation(newSelectedPoint);
        }
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        xtou.transform(R, P.elementAt((P.size()-1)));
        System.out.println("Modified last point. Size="+P.size());
    }
    
    public void modifySymTangentPoint(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        if (symTangentPoint!=null){
            xtou.transform(R, symTangentPoint);
        }
        MPoint Q=P.peek();
        float x1=(float) (2*Q.getX()-symTangentPoint.getX());
        float y1=(float) (2*Q.getY()-symTangentPoint.getY());
        MPoint S=P.elementAt(P.size()-2);
        S.setLocation(x1,y1);
        System.out.println("Modified sym. tangent point. Size="+P.size());
    }
    
    public void addSymTangentPoint(){
        P.add(symTangentPoint);
        System.out.println("Added sym. tangent point. Size="+P.size());
        symTangentPoint=null;
    }
    
    public void cloneLastPoint(){
        MPoint Q=P.pop();
        Q=(MPoint) Q.clone();
        P.add(Q);
        System.out.println("Cloned last point. Size="+P.size());
    }
    
    public int getStatus(){
        return status;
    }
    
  
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        if (isinbackground){
            return false;
        }
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
                if ((!shiftdown)||(i%3==0)){
                    P.elementAt(i).setSelection(true);
                }
            } 
        }
        int i=0;
        while ((i<P.size())&&(!found)){
            Shape sh=getMarkShape(utox, P.elementAt(i));
            if (sh.contains(x,y)){
                if ((!shiftdown)||(i%3==0)){
                    selectedPoint=P.elementAt(i);
                    found=true;
                }
            } 
            i++;
        }
        if (!shiftdown){
            ispartiallyselected=found;
        }
        return found;
    }
    
    public void removeSelectedPoint(){
        MPoint P0,P1,P2,P3;
        int i=P.lastIndexOf(selectedPoint);
        System.out.println("Index of selected point: "+i);
        System.out.println("P.size(): "+P.size());
        if (i>2){
            P0=P.elementAt(i-3);
            P1=P.elementAt(i-2);
            float vx=P1.x-P0.x;
            float vy=P1.y-P0.y;
            P1.setLocation(P0.x+2*vx,P0.y+2*vy);
        }
        if (i+3<P.size()){
            P2=P.elementAt(i+2);
            P3=P.elementAt(i+3);
            float vx=P2.x-P3.x;
            float vy=P2.y-P3.y;
            P2.setLocation(P3.x+2*vx,P3.y+2*vy);
        }
        if (P.size()==2){
            P.removeElementAt(1);
        } 
        else if (i==0){
            P.removeElementAt(0);
            P.removeElementAt(0);
            P.removeElementAt(0);
        }
        else if (i/3==P.size()/3){
            if (P.size()==i+2){
                P.removeElementAt(i+1);
            }
            P.removeElementAt(i);
            P.removeElementAt(i-1);
            P.removeElementAt(i-2);
        }
        else {
            P.removeElementAt(i+1);
            P.removeElementAt(i);
            P.removeElementAt(i-1);
        }
    }
    
    public boolean chooseSelection(int x, int y, AffineTransform utox){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        boolean found=false;
        selectedPoint=null;
        int i=0;
        while ((3*i<P.size())&&(!found)){
            Shape sh=getMarkShape(utox, P.elementAt(3*i));
            if (sh.contains(x,y)){
                found=true;
                selectedPoint=P.elementAt(3*i);
            }  
            i++;
        }
        return found;
    }
}

    
    
    

