
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
import java.util.*;

class Circle extends Drawable{
    MPoint P1,P2;
    
    public Circle(){
        Type=CrystalDrawing.CIRCLE;
    }
          
    public Circle(float x1, float y1, float x2, float y2, Subgroup sg, AffineTransform xtou, Attribute at){
        Type=CrystalDrawing.CIRCLE;
        subgroup=sg;
        P1=new MPoint(x1,y1);
        xtou.transform(P1,P1);
        P2=new MPoint(x2,y2);
        xtou.transform(P2,P2);
        color=Color.black;
        isdashed=false;
        attr=(Attribute) at.clone();
    }
    
    public Circle(MPoint argP1, MPoint argP2, Subgroup sg){
        Type=CrystalDrawing.CIRCLE;
        subgroup=sg;
        P1=(MPoint) argP1.clone();
        P2=(MPoint) argP2.clone();
        color=Color.black;
        markdiam=3;
        isdashed=false;
        attr=new Attribute();
    }
    
    public Circle (MPoint argP1, float radius, Color argcolor, Subgroup sg){
        Type=CrystalDrawing.CIRCLE;
        subgroup=sg;
        P1=(MPoint) argP1.clone();
        color=argcolor;
        P2=new MPoint();
        P2.setLocation(P1.x+radius, P1.y);
        attr=new Attribute();
    }
    
    public void unselectPoints(){
        P1.setSelection(false);
        P2.setSelection(false);
    }
    
    public double[] minimalDistance(MPoint Pt, MPoint closestPoint){
        double dist=Pt.distance(P1);
        closestPoint.setLocation(P1);
        MPoint M=new MPoint();
        double d;
        float r=(float) P1.distance(P2);
        for (float t=0; t<Math.PI*2; t=t+0.01f){
            M.setLocation(P1.x+r*Math.cos(t), P1.y+r*Math.sin(t));
            d=M.distance(Pt);
            if (d<dist){
                dist=d;
                closestPoint.setLocation(M);
            }
        }
        double[] res=new double[1];
        res[0]=dist;
        return res;
    }
    
    public int weight(){
    return 1;
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
    
    public Circle transform(AffineTransform T){
        MPoint R1=new MPoint();
        MPoint R2=new MPoint();
        T.transform(P1,R1);
        T.transform(P2,R2);
        Circle C=new Circle(R1,R2,subgroup);
        C.setAttribute(attr);
        C.isdashed=isdashed;
        C.color=color;
        C.isinbackground=isinbackground;
        return C;
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
        utox.transform(P2,P);
        if (rect.contains(P)){
            P2.setSelection(true);
            found=true;
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
        return "";
    }
    
    protected Object clone(){
        Circle C=(Circle) super.clone();
        C.P1=(MPoint) P1.clone();
        C.P2=(MPoint) P2.clone();
        return C;
    }
    
    public Circle copy(){
        return (Circle) clone();
    }
    
    public MPoint barycenter(){
        return P1;
    }
    
    public Stack<MPoint> translatesInRange(AffineTransform utox, MPoint center){
        Stack<MPoint> res=new Stack();
        
        AffineTransform vtou=transformvtou(P1);
        float latdiam=latticeDiam();
        
        MPoint V=new MPoint();
        MPoint U=new MPoint();
        MPoint X=new MPoint();
        
        int ulo=-3;
        int uhi=3;
        int vlo=-3;
        int vhi=3;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou.transform(V,U);
               utox.transform(U, X);
               boolean alreadyFound=false;
               for (int k=0;k<res.size();k++){
                   if (res.elementAt(k).distance(U)<0.001f){
                   alreadyFound=true;
                   }
                }
               if ((!alreadyFound)&&(center.distance(U)<2*latdiam)){
                    res.add(U.copy());
               }
            }
        }
        return res;
    }
    
    public Stack<MPoint> transformsInRange(AffineTransform utox){
        Stack<MPoint> res=new Stack();
        Stack<MPoint> Ptransl;
        Circle C;
        AffineTransform[] T= subgroup.T;
            for(int i=0; i<T.length; i++){
                //System.out.println("T["+i+"]="+T[i]);
                C=transform(T[i]);
                Ptransl=C.translatesInRange(utox,P1);
                for (int j=0; j<Ptransl.size(); j++){
                    if (P1.distance(Ptransl.elementAt(j))>0.02){
                        res.add(Ptransl.elementAt(j));
                    }
                }
            }
            
        /*
        ArrayList<MPoint> list=new ArrayList(res);
        Collections.sort(list, new Comparator<MPoint>(){
            public int compare(MPoint M1, MPoint M2){
                double d1=P1.distance(M1);
                double d2=P1.distance(M2);
                if (d1==d2) return 0;
                return d1<d2 ? -1:1;
            }
        });
        res.removeAllElements();
        for (int i=0;i<list.size();i++){
            res.add(list.get(i));
        }
        */
        
        return res;
    }
    
    public Polygone initialVoronoiPolygone(Stack<MPoint> pointSet){
        
        //System.out.println("initialVoronoiPolygone");
        
        float xmi=0;
        float xma=0;
        float ymi=0;
        float yma=0;
        for (int i=0; i<pointSet.size();i++){
            MPoint Pt=pointSet.elementAt(i);
            xmi=Math.min(xmi, Pt.x);
            xma=Math.max(xma,Pt.x);
            ymi=Math.min(ymi,Pt.y);
            yma=Math.max(yma,Pt.y);
        }
        
        Stack<MPoint> P=new Stack();
        P.add(new MPoint(xmi,ymi));
        P.add(new MPoint(xma,ymi));
        P.add(new MPoint(xma,yma));
        P.add(new MPoint(xmi,yma));
        
        Polygone poly=new Polygone(P,subgroup);
        //poly.propagateOne();
        return poly;
        
    }
    
    public Polygone voronoi(AffineTransform utox){
        
        Stack<MPoint> pointSet=transformsInRange(utox);
        Polygone poly=initialVoronoiPolygone(pointSet);
        
        for (int i=0; i<pointSet.size();i++){
            StraightLine L=StraightLine.lineBisector(P1, pointSet.elementAt(i));
            //System.out.println("i: "+i+" Point: "+pointSet.elementAt(i));
            poly=poly.intersect(L,P1);
        }
        return poly;
    }
    
    public Segment[] delaunay(AffineTransform utox){
        float radius=(float) P1.distance(P2);
        Polygone poly=voronoi(utox);
        Stack<Segment> seg=new Stack();
        for (int i=0;i+1<poly.P.size();i++){
            MPoint M1=poly.P.elementAt(i);
            MPoint M2=poly.P.elementAt(i+1);
            if (M1.distance(M2)>0.02){
                StraightLine L=new StraightLine(poly.P.elementAt(i), poly.P.elementAt(i+1));
                float[] orth=L.orthVector();
                MPoint toReflect=new MPoint(P1.x-radius*orth[0],P1.y-radius*orth[1]);
                MPoint R=L.reflect(toReflect);
                seg.add(new Segment(toReflect,R,subgroup));
            }
        }
        MPoint M1=poly.P.elementAt(poly.P.size()-1);
        MPoint M2=poly.P.elementAt(0);
            if (M1.distance(M2)>0.02){
                StraightLine L=new StraightLine(M1,M2);
                float[] orth=L.orthVector();
                MPoint toReflect=new MPoint(P1.x-radius*orth[0],P1.y-radius*orth[1]);
                MPoint R=L.reflect(toReflect);
                seg.add(new Segment(toReflect,R,subgroup));
            }
        Segment[] res=new Segment[seg.size()];
        for (int i=0;i<seg.size();i++){
            res[i]=seg.elementAt(i);
        }
        return res;
    }
        
        
    
    public Shape getShape(AffineTransform utox) {
        MPoint R1=new MPoint();
        utox.transform(P1,R1);
        MPoint R2=new MPoint();
        utox.transform(P2,R2);
        double x=R1.getX();
        double y=R1.getY();
        double r=R1.distance(R2);
        Ellipse2D e=new Ellipse2D.Double(x - r, y - r, 2*r, 2*r);
        return e;
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
        Circle S;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou1.transform(V,U1);
               vtou2.transform(V,U2);
               S=new Circle(U1, U2, subgroup);
               S.setAttribute(attr);
               S.isdashed=isdashed;
               S.color=color;
               S.isinbackground=isinbackground;
               S.draw(g, utox, previewmode);
            }
        }
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
        Circle S;
        
        for (int i=ulo; i<=uhi; i++){
            for (int j=vlo; j<=vhi; j++){
               V=new MPoint(i,j);
               vtou1.transform(V,U1);
               vtou2.transform(V,U2);
               S=new Circle(U1, U2, subgroup);
               S.setAttribute(attr);
               S.isdashed=isdashed;
               S.color=color;
               S.isinbackground=isinbackground;
               str=str+S.tikz(previewmode);
            }
        }
        return str;
    }
  
    public String tikz(boolean previewmode){
        String str="";
        if (!previewmode){
            str=str+"\\draw ("+P1.getX()+","+P1.getY()+") circle ("+Utils.round(P1.distance(P2),4)+") ;\n";
        }
        else {
            str=str+tikzcolor(attr.fillcolor);
            str=str+"\\fill[line width="+attr.contourwidth*widthfactor+", color=mycolor]  (" +
                    P1.getX()+","+P1.getY()+") circle ("+Utils.round(P1.distance(P2),4)+") ;\n";
            str=str+tikzcolor(attr.contourcolor);
            String dash;
            if (attr.isdashed){
                dash=",dashed";
            }
            else {
                dash="";
            }
            str=str+"\\draw[line width="+attr.contourwidth*widthfactor+", color=mycolor"+dash+"]  (" +
                    P1.getX()+","+P1.getY()+") circle ("+Utils.round(P1.distance(P2),4)+") ;\n";
        }
        return str;
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle rect, boolean previewmode){
        String str="";
        Circle S=null;
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
            str=str+"\\draw[line width="+2*markdiam*widthfactor+"] ("+P1.getX()+","+P1.getY()+") circle ("
                    +Utils.round(P1.distance(P2),4)+");\n";
        }
        return str;
    }
    
    public void drawlabel(Graphics2D g, AffineTransform utox, String label){
        MPoint R=new MPoint();
        utox.transform(P1, R);
        g.drawString(label,(int) R.getX(), (int) R.getY());
    }
    
    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect, boolean previewmode){
        
        Circle S;
        if (propagation!=null){
            //System.out.println("Drawing with propagation. Size: "+propagation.size());
            for (int i=0;i<propagation.size();i++){
                AffineTransform T=Utils.groupElement(propagation.elementAt(i));
                //System.out.println("Transform "+i+": "+T);
                S=transform(T);
                S.draw(g,utox,previewmode);
            }
        }
        else 
        {
            AffineTransform[] T= subgroup.T;
            for(int i=0; i<T.length; i++){
                S=transform(T[i]);
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
                drawmark(g,utox,P1,Color.black);
            }
            if (P2.isSelected()){
                drawmark(g,utox,P2,Color.black);
            }
        }
        if (isselected){
            Shape sh=getShape(utox);
            g.setStroke(new BasicStroke(2*penwidth));
            g.draw(sh);
            drawmark(g,utox,P1, Color.black);
            drawmark(g,utox,P2, Color.black);
        }
    }
    
    
    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        //System.out.println("Modify selection: selected point " + selectedPoint);
        MPoint R=new MPoint(x,y);
        xtou.transform(R, selectedPoint);
    }
    
    public void getPartiallySelectedPoints(Stack<MPoint> Points){
        if (P1.isselected){
            Points.add(P1);
        }
        if (P2.isselected){
            Points.add(P2);
        }
    }
    
    public void modify(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        xtou.transform(R, P2);
    }
    
    public void modifyCenter(int x, int y, AffineTransform xtou){
        MPoint R=new MPoint(x,y);
        MPoint P1new=new MPoint();
        xtou.transform(R, P1new);
        P2.setLocation(P2.x+(P1new.x-P1.x), P2.y+(P1new.y-P1.y));
        P1=P1new;
    }
    
  
    
    public boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        //System.out.println("Choose selection segment: "+ x+ ", "+y+ " "+utox);
        if (isinbackground){
            return false;
        }
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
    
    public String toString(){
        return "Circle: "+P1+", "+P2;
    }
    
}

