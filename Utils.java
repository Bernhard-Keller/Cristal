
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;




public class Utils {
    
    public static final String crs="crs";
    
    public static AffineTransform translation(MPoint P1, MPoint P2){
        double tx=P2.getX()-P1.getX();
        double ty=P2.getY()-P1.getY();
        return new AffineTransform(1,0,0,1,tx,ty);
    }
    
    public static AffineTransform glideReflection(MPoint P1, MPoint P2){
        double a=P2.getX()-P1.getX();
        double b=P2.getY()-P1.getY();
        double d=P1.distance(P2);
        a=a/d;
        b=b/d;
        double a11=1-2*b*b;
        double a21=2*a*b;
        double a12=2*a*b;
        double a22=1-2*a*a;
        double tx=P2.getX()-(a11*P1.getX()+a12*P1.getY());
        double ty=P2.getY()-(a21*P1.getX()+a22*P1.getY());
        return new AffineTransform(a11,a21,a12,a22,tx,ty);
    }
    
    public static AffineTransform symmetry(MPoint P1, MPoint P2){
        double a=P2.getX()-P1.getX();
        double b=P2.getY()-P1.getY();
        double d=P1.distance(P2);
        a=a/d;
        b=b/d;
        double a11=1-2*b*b;
        double a21=2*a*b;
        double a12=2*a*b;
        double a22=1-2*a*a;
        double tx=P1.getX()-(a11*P1.getX()+a12*P1.getY());
        double ty=P1.getY()-(a21*P1.getX()+a22*P1.getY());
        return new AffineTransform(a11,a21,a12,a22,tx,ty);
    }
    
    public static AffineTransform rotation(MPoint fixPoint, float angle){
        double c=Math.cos(Math.PI*angle/180);
        double s=Math.sin(Math.PI*angle/180);
        double tx=fixPoint.getX()-(c*fixPoint.getX()-s*fixPoint.getY());
        double ty=fixPoint.getY()-(s*fixPoint.getX()+c*fixPoint.getY());
        return new AffineTransform(c,s,-s,c,tx,ty);
    }
    
    public static AffineTransform rotation(MPoint fixPoint, MPoint P1, MPoint P2){
    //System.out.println("Distance between P1 and P2:"+P1.distance(P2));
    double x1=P1.getX();
    double y1=P1.getY();
    double x2=P2.getX();
    double y2=P2.getY();
    double x0=fixPoint.getX();
    double y0=fixPoint.getY();
    double v1x=x1-x0;
    double v1y=y1-y0;
    double length=Math.sqrt((v1x*v1x+v1y*v1y));
    if (length<0.00001){
        return null;
    }
    v1x=v1x/length;
    v1y=v1y/length;
    double v2x=x2-x0;
    double v2y=y2-y0;
    length=Math.sqrt(v2x*v2x+v2y*v2y);
    if (length<0.00001){
        return null;
    }
    v2x=v2x/length;
    v2y=v2y/length;
    MPoint P1p=new MPoint((float) v1x,(float) v1y);
    MPoint P2p=new MPoint((float) v2x,(float) v2y);
    //System.out.println("v1x,v1y,v2x,v2y:"+v1x+","+v1y+","+v2x+","+v2y);
    //System.out.println("Distance between the normed vectors of P1-P0 and P2-P0: "+P1p.distance(P2p));
    double qx=v2x*v1x+v2y*v1y;
    double qy=-v2x*v1y+v2y*v1x;
    //System.out.println("qx, qy:"+qx+", "+qy);
    AffineTransform T0=new AffineTransform(qx,qy,-qy,qx,0,0);
    MPoint P3=new MPoint(0,0);
    T0.transform(fixPoint, P3);
    double tx=fixPoint.getX()-P3.getX();
    double ty=fixPoint.getY()-P3.getY();
    AffineTransform T= new AffineTransform(qx,qy,-qy,qx,tx,ty);
    //System.out.println("Angle: "+180*Math.acos(qx)/Math.PI);
    return T;
}
    
    public static AffineTransform homothety(MPoint center, MPoint fromPoint, MPoint toPoint){
        double d1=center.distance(fromPoint);
        double d2=center.distance(toPoint);
        double ratio=d2/d1;
        double tx= center.getX()- ratio*center.getX();
        double ty= center.getY()- ratio*center.getY();
        return new AffineTransform(ratio, 0, 0, ratio, tx,ty);
    }
    
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
    
    static public MPoint pointbarycenter(Stack<MPoint> Points){
       if (Points.size()==0){
           return null;
       }
       float sumx=0;
       float sumy=0;
       MPoint P;
       for (int i=0; i<Points.size();i++){
           P=Points.elementAt(i);
           sumx=(float) (sumx+P.getX());
           sumy=(float) (sumy+P.getY());
       }
       int n=Points.size();
       return new MPoint(sumx/n, sumy/n);
   }
   
    
    static public MPoint barycenter(Stack<Drawable> Drawables){
       if (Drawables.size()==0){
           return null;
       }
       float sumx=0;
       float sumy=0;
       int totalweight=0;
       Drawable D;
       MPoint Bd;
       int weight=0;
       for (int i=0; i<Drawables.size();i++){
           D=Drawables.elementAt(i);
           Bd=D.barycenter();
           weight=D.weight();
           sumx=(float) (sumx+weight*Bd.getX());
           sumy=(float) (sumy+weight*Bd.getY());
           totalweight=totalweight+weight;
       }
       return new MPoint(sumx/totalweight, sumy/totalweight);
   }
   
    
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static AffineTransform groupElement(int[] code){
        Subgroup localsubgroup=CrystalDrawing.groups[code[0]].subgroups[code[1]];
        AffineTransform T=(AffineTransform) localsubgroup.T[code[2]].clone();
        MPoint P=new MPoint(code[3],code[4]);
        float v00=localsubgroup.uvec[0];
        float v10=localsubgroup.uvec[1];
        float v01=localsubgroup.vvec[0];
        float v11=localsubgroup.vvec[1];
        AffineTransform vtou=new AffineTransform(v00,v10,v01,v11,0,0);
        vtou.transform(P, P);
        AffineTransform trans=new AffineTransform(1,0,0,1,P.getX(), P.getY());
        T.preConcatenate(trans);
        return T;
    }
    
    public static MPoint readpoint(BufferedReader r){
        String str="";
        MPoint P=new MPoint();
        try {
          str=r.readLine();
          String[] fields=str.split(" ");
          float x=(float) Double.parseDouble(fields[0]);
          float y=(float) Double.parseDouble(fields[1]);
          P=new MPoint(x,y);
          if (fields.length==3){
              if (fields[2].equals("1")){
                  P.setSelection(true);
              }
              else {
                  P.setSelection(false);
              }
          }
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
        return P;
    }
    
    public static int[] readintarray(BufferedReader r){
        String str;
        int[] arr=null;
        try {
            str=r.readLine();
            String[] fields=str.split(" ");
            arr=new int[fields.length];
            for (int i=0; i<arr.length; i++){
                arr[i]=Integer.parseInt(fields[i]);
            }
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
        return arr;
    }
    
    public static String readstring(BufferedReader r){
        String str="";
        try {
            str=r.readLine();
            System.out.println("readstring: "+str);
            str=r.readLine();
            System.out.println("readstring: "+str);
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
        return str;
    }
    
    public static String readstringnoline(BufferedReader r){
        String str="";
        try {
            str=r.readLine();
            System.out.println("readstring: "+str);
        }
        catch (IOException e){
                System.out.println("I/O exception: "+ e.getMessage());
        }
        return str;
    }
    
    
    public static Color readcolor(BufferedReader r){
        Color color=null;
        try {
            String str=r.readLine();
            System.out.println("readcolor: "+str);
            str=r.readLine();
            System.out.println("readcolor: "+str);
            String[] fields=str.split(" ");
            int red=Integer.parseInt(fields[0]);
            int g=Integer.parseInt(fields[1]);
            int b=Integer.parseInt(fields[2]);
            color=new Color(red,g,b);
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        return color;
    }
    
    public static int readint(BufferedReader r){
        int n=0;
        try {
            String str=r.readLine();
            System.out.println("readint:"+str);
            str=r.readLine();
            System.out.println("readint:"+str);
            n=Integer.parseInt(str);
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        //System.out.println("Integer read: "+n);
        return n;
    }
    
    public static float readfloat(BufferedReader r){
        float x=0;
        try {
            String str=r.readLine();
            System.out.println("readfloat: "+str);
            str=r.readLine();
            System.out.println("readfloat: "+str);
            x=Float.parseFloat(str);
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        //System.out.println("Float read: "+x);
        return x;
    }
    
    public static float readfloatnoline(BufferedReader r){
        float x=0;
        try {
            String str=r.readLine();
            System.out.println("readfloatnoline: "+str);
            x=Float.parseFloat(str);
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        //System.out.println("Float read: "+x);
        return x;
    }
    
    public static boolean readboolean(BufferedReader r){
        boolean b=true;
        try {
            String str=r.readLine();
            //System.out.println(str);
            str=r.readLine();
            //System.out.println(str);
            if (str.equals("1")){
                b=true;
            } else {
                b=false;
            }
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        //System.out.println("Value read: "+b);
        return b;
    }
    
    public static AffineTransform readAffineTransform(BufferedReader r){
        double[] m=new double[6];
        try {
            String str=r.readLine();
            System.out.println(str);
            str=r.readLine();
            System.out.println(str);
            String[] fields=str.split(" ");
            for (int i=0; i<6; i++){
                m[i]=Double.parseDouble(fields[i]);
            }
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
        AffineTransform T=new AffineTransform(m[0],m[1],m[2],m[3],m[4],m[5]);
        //System.out.println("Transform read:"+T);
        return T;
    }
    
    public static void writecolor(BufferedWriter out, String name, Color color){
        try {
            out.write("//"+name); out.newLine();
            out.write(""+color.getRed()+" "+color.getGreen()+" "+color.getBlue());
            out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writeboolean(BufferedWriter out, String name, boolean bool){
        try {
            out.write("//"+name); out.newLine();
            if (bool){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writestring(BufferedWriter out, String str){
        try {
            out.write(str); out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writepoint(BufferedWriter out, MPoint P){
        String str=P.getX()+" "+P.getY();
        if (P.isSelected()){
            str=str+" 1";
        }
        else {
            str=str+" 0";
        }
        try {
            out.write(str); out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writeint(BufferedWriter out, String name, int n){
        try {
            out.write("//"+name); out.newLine();
            out.write(""+n); out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writefloat(BufferedWriter out, String name, float x){
        try {
            out.write("//"+name); out.newLine();
            out.write(""+x); out.newLine();
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
    }
    
    public static void writeAffineTransform(BufferedWriter out, AffineTransform T){
        double[] coeff=new double[6];
        T.getMatrix(coeff);
        try {
            for (int i=0; i<6; i++){
                out.write(""+coeff[i]+" ");
            }
            out.newLine();
        }
        catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
    
}
