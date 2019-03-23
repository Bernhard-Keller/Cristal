
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class MPoint extends Point2D.Float implements Cloneable {
    boolean isselected;
    
    public MPoint(){
        super();
        isselected=false;
    }
    
    public MPoint(float x, float y){
        super(x,y);
        isselected=false;
    }
    
    public MPoint (Point2D.Float P){
        super(P.x,P.y);
        isselected=false;
    }
    
    public double getX(){
        return Utils.round(super.getX(), 4);
    }
    
    public double getY(){
        return Utils.round(super.getY(),4);
    }
    
    public String toString(){
        String str="MPoint["+getX()+", "+getY()+"]";
        return str;
    }

    
    public Object clone(){
        MPoint P=new MPoint((float) getX(), (float) getY());
        P.isselected=isselected;
        return P;
    }
    
    public MPoint copy(){
        return (MPoint) clone();
    }
    
    public boolean isSelected(){
        return isselected;
    }
    
    public void setSelection(boolean b){
        isselected=b;
    }
    
    
}
