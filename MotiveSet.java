import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.awt.*;


public class MotiveSet {
    Stack<Motive> motives;
    Motive currentMotive;
    
    public Motive getCurrentMotive(){
        return currentMotive;
    }
    
    public void setCurrentMotive(Motive currentM){
        currentMotive=currentM;
    }
    
    public void write(BufferedWriter out){
        try{
            out.write("//Number of motives"); out.newLine();
            out.write(""+size()); out.newLine();
            out.write("//Current motive"); out.newLine();
            int cter=motives.indexOf(currentMotive);
            out.write(""+cter); out.newLine();
            out.write("//Motives"); out.newLine();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        for (int i=0; i<size(); i++){
            motives.elementAt(i).write(out);
        }
    }
    
    
    public int size(){
        return motives.size();
    }
    
    public void removeElement(Motive m){
        motives.removeElement(m);
    }
    
    public Motive peek(){
        return motives.peek();
    }
    
    
    public MotiveSet(){
        motives=new Stack();
    }
    
    public void add(Motive m){
        motives.add(m);
    }
    
    public void clear(){
        motives.clear();
    }
    
    public void read(BufferedReader r){
        String str;
        try {
            str=r.readLine();
            //System.out.println(str);
            str=r.readLine();
            //System.out.println(str);
            int nberMotives=Integer.parseInt(str);
            //System.out.println("nberMotives="+nberMotives);
            str=r.readLine();
            //System.out.println(str);
            str=r.readLine();
            //System.out.println(str);
            int currentMotiveNumber=Integer.parseInt(str);
            //System.out.println("currentMotiveNumber="+currentMotiveNumber);
            str=r.readLine();
            //System.out.println("Last string read in MotiveSet:"+str);
            Motive M;
            for (int i=0; i<nberMotives; i++){
                M=new Motive(new Rectangle(900,600));
                M.read(r);
                System.out.println("Motive "+i+":");
                for (int j=0; j<M.drawables.size();j++){
                    System.out.println("Drawable "+j+":\n"+M.drawables.elementAt(j));
                }
                motives.add(M);
            }
            r.close();
            currentMotive=motives.elementAt(currentMotiveNumber);
        }
        catch (IOException e){
            System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    
    public Motive MotiveFromGroup(Group gr){
        Motive returnMotive=null;
        for (int i=0;i<motives.size();i++){
            if (motives.elementAt(i).group==gr){
                returnMotive=motives.elementAt(i);
            }
        }
        return returnMotive;
    }
    
    public String toString(){
        String s="Motives: ";
        for (int i=0; i<motives.size(); i++){
            s=s+motives.elementAt(i).title+" ";
        }
        return s;
    }
}
