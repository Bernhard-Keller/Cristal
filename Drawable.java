
import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;

abstract class Drawable implements Cloneable {
    int Type; 
    Subgroup subgroup;
    
    Stack<int[]> propagation=null;
   
    static float penwidth, markdiam, widthfactor;
    Color color;
    boolean isdashed;
    Attribute attr;
    
    boolean selectionmodeon=false;
    boolean partialselectionmodeon=false;
    boolean isselected=false;
    boolean ispartiallyselected=false;
    MPoint selectedPoint;
    
    boolean isinbackground;
    
    String lastReadLine;
    
    public static void initializeWidthFactor(AffineTransform xtou){
        MPoint P0=new MPoint(0,0);
        MPoint P1=new MPoint(10,0);
        xtou.transform(P0,P0);
        xtou.transform(P1,P1);
        //System.out.println("P0: "+P0);
        //System.out.println("P1: "+P1);
        widthfactor=(float) (P1.getX()-P0.getX())*2.83465f;
        //System.out.println("width factor: "+widthfactor);
    }
    
    abstract MPoint nearPoint(MPoint Pt, float dist);
    
    abstract double[] minimalDistance(MPoint Pt, MPoint closestPoint);
    
    public void propagateAll(){
        propagation=null;
    }
    
    public void propagateOne(){
        int[] code={subgroup.getGroup().number, subgroup.number, 0, 0,0};
        propagation=null;
        togglePropagation(code);
    }
    
    
    public void setSubgroup(Subgroup sgr){
        subgroup=sgr;
    }

    
    abstract int weight();
    
    public void write(BufferedWriter out){
        try {
            out.write("//Type"); out.newLine();
            out.write(""+Type); out.newLine();
            out.write("//Group"); out.newLine();
            out.write(""+subgroup.group.number); out.newLine();
            out.write("//Subgroup"); out.newLine();
            out.write(""+subgroup.number); out.newLine();
            Utils.writecolor(out,"Color",color);
            Utils.writeboolean(out,"Is dashed", isdashed);
            Utils.writeboolean(out,"Selection mode on", selectionmodeon);
            Utils.writeboolean(out,"Partial selection mode on", partialselectionmodeon);
            Utils.writeboolean(out,"Is selected", isselected);
            Utils.writeboolean(out,"Is partially selected", ispartiallyselected);
            Utils.writeboolean(out,"Is in background", isinbackground);
            out.write("//Attribute"); out.newLine();
            attr.write(out);
            if (propagation!=null){
                out.write("//Propagation"); out.newLine();
                out.write("//Number of elements"); out.newLine();
                out.write(""+propagation.size()); out.newLine();
                int[] code;
                for (int i=0; i<propagation.size(); i++){
                    code=propagation.elementAt(i);
                    out.write(code[0]+" "+code[1]+" "+code[2]+" "+code[3]+" "+code[4]);
                    out.newLine();
                }
            }
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public void read(BufferedReader r){
        int gn=Utils.readint(r);
        int sgn=Utils.readint(r);
        subgroup=CrystalDrawing.groups[gn].subgroups[sgn];
        color=Utils.readcolor(r);
        isdashed=Utils.readboolean(r);
        selectionmodeon=Utils.readboolean(r);
        partialselectionmodeon=Utils.readboolean(r);
        isselected=Utils.readboolean(r);
        ispartiallyselected=Utils.readboolean(r);
        isinbackground=Utils.readboolean(r);
        try {
            r.readLine();
            attr=new Attribute();
            attr.read(r);
            String str=r.readLine();
            lastReadLine=str;
            if (str.equals("//Propagation")){
                propagation=new Stack();
                int n=Utils.readint(r);
                int[] code=new int[5];
                for (int i=0; i<n; i++){
                    code=Utils.readintarray(r);
                    propagation.add(code);
                }
            }
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    public boolean equalcodes(int[] c1, int[] c2){
        if (c1.length!=c2.length){
            return false;
        }
        boolean equality=true;
        for (int i=0; i<c1.length; i++){
            if (c1[i]!=c2[i]){
                equality=false;
            }
        }
        return equality;
    }

    public void togglePropagation(int[] code){
        if (propagation==null){
            propagation=new Stack();
        }
        int i=0;
        boolean found=false;
        while ((!found) && i<propagation.size()){
            if (equalcodes(code, propagation.elementAt(i))){
                found=true;
            }
            i++;
        }
        if (found){
            propagation.removeElementAt(i-1);
            return;
        }
        else {
            propagation.add(code);
        }
    }
    
    abstract Shape getShape(AffineTransform utox);
    
    public String tikzcolor(Color color){
        int r=color.getRed();
        int g=color.getGreen();
        int b=color.getBlue();
        return "\\definecolor{mycolor}{RGB}{"+r+","+g+","+b+"}\n";
    }
    
    public void draw(Graphics2D g, AffineTransform utox, boolean previewmode){
       Shape sh=getShape(utox);
       if (isinbackground){
           //System.out.println("Drawing in background");
           g.setStroke(new BasicStroke(1));
           g.setPaint(Color.gray);
           g.draw(sh);
       } else
       {
       if (!previewmode){
           BasicStroke stroke=null;
           if (isdashed){
               stroke=new BasicStroke(penwidth,       // Width
                       BasicStroke.CAP_SQUARE,    // End cap
                       BasicStroke.JOIN_MITER,    // Join style
                       10.0f,                     // Miter limit
                       new float[] {6.0f,12.0f}, // Dash pattern
                       0.0f);  
               //System.out.println("Dashed stroke chosen");
           } 
           else {
               stroke=new BasicStroke(penwidth);
           }
           g.setStroke(stroke);
           g.setPaint(color);
           g.draw(sh);
       }
       if (previewmode){
           //System.out.println("attr="+attr);
           if (attr.fillpresent){
               g.setPaint(attr.fillcolor);
               g.fill(sh);
           }
           if (attr.contourpresent){
               //System.out.println("Is dashed:"+attr.isdashed);
               g.setStroke(attr.getContourStroke());
               g.setPaint(attr.contourcolor);
               g.draw(sh);
           }
       }
    }
}


    
    public void addPropagation(int[] code){
        if (propagation==null){
            propagation=new Stack();
        }
        propagation.add(code);
    }
    
    abstract Drawable transform(AffineTransform T);
    
    //abstract Drawable transform(AffineTransform T);
    
   
    
    public void setPenwidth(float w){
        penwidth=w;
    }
    
    public void setMarkdiam(float d){
        markdiam=d;
    }
    
    abstract int size();
    
    public void setColor(Color col){
        color=col;
    }
        
    
    public void setSelection(boolean sel){
        isselected=sel;
    }
    
    public boolean getSelection(){
        return isselected;
    }
    
    public void setPartialSelection(boolean sel){
        ispartiallyselected=sel;
    }
    
    abstract void getPartiallySelectedPoints(Stack<MPoint> Points);
    
    abstract void unselectPoints();
    
    abstract void makePartialSelection(Rectangle rect, AffineTransform utox);
    
    public void setSelectionMode(boolean sel){
        selectionmodeon=sel;
    }
    
    public void setPartialSelectionMode(boolean sel){
        partialselectionmodeon=sel;
    }
   
    public void setAttribute(Attribute a){
        attr=a;
    }
    
    public Attribute getAttribute(){
        return attr;
    }
    
    public void toggledashed(){
        if (isdashed){
            isdashed=false;
        } else {
            isdashed=true;
        }
    }
    
    public void togglebackground(){
        if (isinbackground){
            isinbackground=false;
        } else {
            isinbackground=true;
            isselected=false;
            ispartiallyselected=false;
            selectionmodeon=false;
            partialselectionmodeon=false;
        }
    }
    
    protected Object clone(){
        Drawable D2=null;
        try {
            D2=(Drawable) super.clone();
        }
        catch (CloneNotSupportedException e){
            System.out.println("Clone not supported exception:"+e.getMessage());
        }
        Drawable D1=this;
        D2.color=D1.color;
        D2.isdashed=D1.isdashed;
        D2.selectionmodeon=D1.selectionmodeon;
        D2.isinbackground=D1.isinbackground;
        D2.partialselectionmodeon=D1.partialselectionmodeon;
        D2.isselected=D1.isselected;
        D2.ispartiallyselected=D1.ispartiallyselected;
        if (D1.selectedPoint!=null){
            D2.selectedPoint=(MPoint) D1.selectedPoint.clone();
        }
        else {
            D2.selectedPoint=null;
        }
        D2.attr=(Attribute) D1.attr.clone();
        if (propagation==null){
            D2.propagation=null;
        }
        else {
            D2.propagation=new Stack();
            for (int i=0; i<propagation.size();i++){
                int[] oldcode=propagation.elementAt(i);
                int[] newcode=new int[5];
                for (int j=0;j<5;j++){
                    newcode[j]=oldcode[j];
                }
                D2.propagation.add(newcode);
            }
        }
        return D2;
    }
    
    public String toString(){
        return "";
    }
    
    abstract Drawable copy();
    
    abstract void removeLastStroke();
    
    abstract boolean isempty();
    
    abstract MPoint barycenter();
    
    abstract MPoint hitPoint(int x, int y, AffineTransform utox);
    
    abstract MPoint hitPointAll(int x, int y, AffineTransform utox);
    
    abstract MPoint hitPoint(Rectangle rect, AffineTransform utox);
    
    abstract void translate(float dx, float dy);
    
    abstract void translatePartialSelection(float dx, float dy, boolean altdown);
    
    public Shape getMarkShape(AffineTransform utox, MPoint P){
        MPoint R=new MPoint();
        utox.transform(P,R);
        float x=(float) R.getX();
        float y=(float) R.getY();
        float r=markdiam;
        Ellipse2D e=new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
        return e;
    }
    
    public String tikzmark(MPoint P, Color color){
        String str="";
        str=str+tikzcolor(color);
        str=str+"\\fill[color=mycolor] ("+P.getX()+","+P.getY()+") circle ("+widthfactor*markdiam/2+"pt) ;\n";
        str=str+"\\draw ("+P.getX()+","+P.getY()+") circle ("+widthfactor*markdiam/2+"pt) ;\n";
        //System.out.println("MPoint: "+P);
        return str;
    }
    
    public static void drawmark(Graphics2D g, AffineTransform utox, MPoint P, Color color){
        //System.out.println("Drawing mark at point: "+P);
        MPoint R=new MPoint();
        utox.transform(P,R);
        float x=(float) R.getX();
        float y=(float) R.getY();
        float r=markdiam;
        Ellipse2D e=new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
        g.setPaint(color);
        g.fill(e);
        BasicStroke stroke=new BasicStroke(penwidth);
        g.setStroke(stroke);
        g.setPaint(Color.black);
        g.draw(e);
    }
    
    public String tikzline(MPoint P1, MPoint P2, Color color){
        String str=tikzcolor(color);
        str=str+"\\draw[color=mycolor] ("+P1.getX()+","+P1.getY()+") -- ("+P2.getX()+","+P2.getY()+");\n";
        //str=str+"% Here are the tangents.\n";
        return str;
    }
    
    public void drawline(Graphics2D g, AffineTransform utox, MPoint P1, MPoint P2, Color color){
        MPoint R1=new MPoint();
        MPoint R2=new MPoint();
        utox.transform(P1,R1);
        utox.transform(P2,R2);
        g.setPaint(color);
        //System.out.println("penwidth="+penwidth);
        BasicStroke stroke=new BasicStroke(penwidth);
        g.setStroke(stroke);
        g.draw(new Line2D.Double(R1,R2));
    }
   
    abstract void draworbit(Graphics2D g, AffineTransform utox, Rectangle R, boolean previewmode);
    
    abstract String tikzorbit(AffineTransform utox, Rectangle R, boolean previewmode);
    
    public int getType(){
        return Type;
    }
    
    public void setType(int s){
        Type=s;
    }
    
    abstract String datastring();
    
    
    abstract boolean makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown);
    
    abstract boolean chooseSelection(int x, int y, AffineTransform utox);
    
    abstract void removeSelectedPoint();
    
    abstract void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown);
    
    abstract void modify(int x, int y, AffineTransform xtou);
    
    public float latticeDiam(){
        float v00=subgroup.uvec[0];
        float v10=subgroup.uvec[1];
        float v01=subgroup.vvec[0];
        float v11=subgroup.vvec[1];
        MPoint O=new MPoint(0,0);
        MPoint P1=new MPoint(v00,v10);
        MPoint P2=new MPoint(v01,v11);
        return (float) Math.max(O.distance(P1), O.distance(P2));
    }
   
    
    public AffineTransform transformvtou(MPoint P){
        float v00=subgroup.uvec[0];
        float v10=subgroup.uvec[1];
        float v01=subgroup.vvec[0];
        float v11=subgroup.vvec[1];
        float v02=(float) P.getX();
        float v12=(float) P.getY();
        AffineTransform vtou=new AffineTransform(v00,v10,v01,v11,v02,v12);
        return vtou;
    }
    
    public AffineTransform transformutov(MPoint P){
        float v00=subgroup.uvec[0];
        float v10=subgroup.uvec[1];
        float v01=subgroup.vvec[0];
        float v11=subgroup.vvec[1];
        float v02=(float) P.getX();
        float v12=(float) P.getY();
        AffineTransform vtou=new AffineTransform(v00,v10,v01,v11,v02,v12);
        AffineTransform utov=new AffineTransform(vtou);
        try{
            utov.invert();
        }
        catch (NoninvertibleTransformException  e){
	    System.out.println("vtou: "+e.getMessage());
        }
        return utov;
    }
    
    public int[] uvrange(MPoint P, AffineTransform utox, Rectangle rect){
        
        AffineTransform xtou=new AffineTransform(utox);
        try{
            xtou.invert();
        }
        catch (NoninvertibleTransformException  e){
	    System.out.println("utox: "+e.getMessage());
        }
        
        MPoint[] M=new MPoint[4];
        
        float w=rect.width;
        float h=rect.height;
        
        for (int i=0;i<4; i++){
            M[i]=new MPoint(0,0);
        }
        
        xtou.transform(new MPoint(0,0), M[0]);
        xtou.transform(new MPoint(w,0), M[1]);
        xtou.transform(new MPoint(0,h), M[2]);
        xtou.transform(new MPoint(h,w), M[3]);
        
        AffineTransform utov=transformutov(P);
        
        for (int i=0; i<4; i++){
            utov.transform(M[i],M[i]);
        }
        
        double v0min=0;
        double v0max=0;
        double v1min=0;
        double v1max=0;
        
        for (int i=0; i<4; i++){
            if (M[i].getX()<v0min){v0min=M[i].getX();}
            if (M[i].getX()>v0max){v0max=M[i].getX();}
            if (M[i].getY()<v1min){v1min=M[i].getY();}
            if (M[i].getY()>v1max){v1max=M[i].getY();}
            }
        
        int[] res=new int[4];
        res[0]=(int) Math.floor(v0min); 
        res[1]=(int) Math.ceil(v0max);
        res[2]=(int) Math.floor(v1min);
        res[3]=(int) Math.ceil(v1max);
        
        return res;
    }
}
    