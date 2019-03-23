
import java.awt.Color;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Subgroup {
     Group group;
     String name;
     int number;
     float[] uvec, vvec;
     AffineTransform[] T;
     MPoint[] funddom;
     
    public void setNumber(int n){
        number=n;
    }
    public int getNumber(){
        return number;
    }
    
    public void setGroup(Group gr){
        group=gr;
    }
    public Group getGroup(){
        return group;
    }
     
    public void read(BufferedReader in){

        String str;
	String patternstr=",";
	String[] fields;
        try {
            name=in.readLine();
            //System.out.println("name="+name);
            str=in.readLine();
            //System.out.println(str);
            
            str=in.readLine();
            fields=str.split(patternstr);
            uvec=new float[2];
            vvec=new float[2];
            uvec[0]=java.lang.Float.parseFloat(fields[0]);
            vvec[0]=java.lang.Float.parseFloat(fields[1]);
            //System.out.println("xvec[0]="+xvec[0]+" yvec[0]="+yvec[0]);
            str=in.readLine();
            fields=str.split(patternstr);
            uvec[1]=java.lang.Float.parseFloat(fields[0]);
            vvec[1]=java.lang.Float.parseFloat(fields[1]);
            //System.out.println("xvec[1]="+xvec[1]+" yvec[1]="+yvec[1]);
            
            str=in.readLine();
            //System.out.println("str="+str);
            int trafonber=Integer.parseInt(str.replace(" ", ""));
            //System.out.println("trafonber="+trafonber);
            T=new AffineTransform[trafonber];
            double m00,m01,m02,m10,m11,m12;
            for (int i=0; i<trafonber; i++){
                str=in.readLine();
                //System.out.println("str="+str);
                fields=str.split(patternstr);
                m02=Double.parseDouble(fields[0]);
                m00=Double.parseDouble(fields[1]);
                m01=Double.parseDouble(fields[2]);
                str=in.readLine();
                //System.out.println("str="+str);
                fields=str.split(patternstr);
                m12=Double.parseDouble(fields[0]);
                m10=Double.parseDouble(fields[1]);
                m11=Double.parseDouble(fields[2]);
                //System.out.println("m00,m01,m02,m10,m11,m12:"+m00+" "+m01+" "+m02+" "+m10+" "+m11+" "+m12);
                T[i]=new AffineTransform(m00,m10,m01,m11,m02,m12);
                //System.out.println("T["+i+"]:"+T[i]);
            }
            
            str=in.readLine();
            //System.out.println("str="+str);
            int pointnber=Integer.parseInt(str.replace(" ",""));
            //System.out.println("pointnber="+pointnber);
            funddom=new MPoint[pointnber];
            float x,y;
            for (int i=0;i<pointnber;i++){
                str=in.readLine();
                //System.out.println("str="+str);
                fields=str.split(patternstr);
                x=Float.parseFloat(fields[0]);
                y=Float.parseFloat(fields[1]);
                //System.out.println("x="+x+" y="+y);
                funddom[i]=new MPoint(x,y);
                //System.out.println(funddom[i]);
            }
            
            in.readLine();
        }
        catch (Exception e) 
        {
            //System.out.println(e.getMessage());
        }
    }
}
    
