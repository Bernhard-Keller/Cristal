
import java.util.*;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.CloneNotSupportedException;




public class Motive {
    
   public static final int FUNDDOM=-1;
   public static final int SEGMENT=0;
   public static final int POLYGON=1;
   public static final int BEZIERPATH=2;
   public static final int CIRCLE=3;
   public static final int BLOB=4;
   public static final int SNAKE=5;
   public static final int TEXT=6;
   
   public static final int xeps=4;
   
    
    String title;
    Stack<Drawable> drawables;
    Group  group;
    Subgroup subgroup;
    static int number=1;
    
    Rectangle rect;
    Drawable currentDrawable=null;
    
    int status;
    int oldstatus;
    
    boolean funddomon;
    Funddom funddom;
    
    boolean symcharton;
    Motive symchart;
    
    boolean hidegroup;
    
    boolean previewmode;
    boolean selectionmodeon;
    boolean partialselectionmodeon;
    Drawable partiallyselecteddrawable;
    Stack<Drawable> selectedDrawables;
    Stack<Drawable> limboDrawables;
    
    AffineTransform utox, xtou;
    
    Stack<Drawable> copiedDrawables;
    Stack<Drawable> historyDrawable;
    Stack<Integer> historyStatus;
    Stack<Boolean> historyPopped;
    
    Attribute currentattr=new Attribute();
    
    Snake snake;
    
    Font textfont;
    String textstring;
    
    public Motive(Rectangle argrect){
        subgroup=CrystalDrawing.groups[0].subgroups[0];
        group=subgroup.group;
        rect=argrect;
        drawables=new Stack();
        selectedDrawables=new Stack();
        limboDrawables=new Stack();
        
        copiedDrawables=new Stack();
        historyDrawable=new Stack();
        historyStatus=new Stack();
        historyPopped=new Stack();
        
        funddomon=true;
        funddom=new Funddom(subgroup.funddom,subgroup);
        symchart=group.symchart;
        symcharton=false;
        selectionmodeon=false;
        partialselectionmodeon=false;
        
        textfont=new Font("TimesRoman", Font.PLAIN, 12);
        textstring="";
        
        initializextou(-2,2,-2,2, rect);
    }
    
    

    public Motive(Rectangle argrect, Subgroup sgr){
        //System.out.println("Calling Motive(cd,sgr). Number="+number);
        subgroup=sgr;
        group=subgroup.group;
        rect=argrect;
        title=group.name+"_"+number;
        number++;
        drawables=new Stack();
        selectedDrawables=new Stack();
        limboDrawables=new Stack();
        copiedDrawables=new Stack();
        historyDrawable=new Stack();
        historyStatus=new Stack();
        historyPopped=new Stack();
        funddomon=true;
        funddom=new Funddom(subgroup.funddom,subgroup);
        symchart=group.symchart;
        symcharton=false;
        selectionmodeon=false;
        partialselectionmodeon=false;
        textfont=new Font("TimesRoman", Font.PLAIN, 12);
        textstring="";
        initializextou(-2,2,-2,2,rect);
    }
    
    public Motive(Rectangle argrect, Subgroup sgr, int nber){
        number=nber;
        subgroup=sgr;
        group=subgroup.group;
        rect=argrect;
        title=group.name+"_"+number;
        number++;
        drawables=new Stack();
        selectedDrawables=new Stack();
        limboDrawables=new Stack();
        copiedDrawables=new Stack();
        historyDrawable=new Stack();
        historyStatus=new Stack();
        historyPopped=new Stack();
        funddomon=true;
        funddom=new Funddom(subgroup.funddom,subgroup);
        symchart=group.symchart;
        symcharton=false;
        selectionmodeon=false;
        partialselectionmodeon=false;
        textfont=new Font("TimesRoman", Font.PLAIN, 12);
        textstring="";
        initializextou(-2,2,-2,2,rect);
    }
    
    public Motive(Motive M){
        subgroup=M.subgroup;
        group=M.group;
        rect=(Rectangle) M.rect.clone();
        title=M.title;
        drawables=new Stack();
        selectedDrawables=new Stack();
        limboDrawables=new Stack();
        copiedDrawables=new Stack();
        historyDrawable=new Stack();
        historyStatus=new Stack();
        historyPopped=new Stack();
        Drawable D;
        for (int i=0; i<M.drawables.size();i++){
            D= M.drawables.elementAt(i).copy();
            drawables.add(D);
        }
        for (int i=0; i<M.selectedDrawables.size();i++){
            D=selectedDrawables.elementAt(i).copy();
            selectedDrawables.add(D);
        }
        for (int i=0; i<M.copiedDrawables.size();i++){
            D=M.copiedDrawables.elementAt(i).copy();
            copiedDrawables.add(D);
        }
        for (int i=0; i<M.historyDrawable.size();i++){
            D=M.historyDrawable.elementAt(i).copy();
            historyDrawable.add(D);
        }
        for (int i=0; i<M.historyStatus.size();i++){
            int st=M.historyStatus.elementAt(i).intValue();
            historyStatus.add(new Integer(st));
        }
        for (int i=0; i<M.historyPopped.size();i++){
            boolean popped=M.historyPopped.elementAt(i).booleanValue();
            historyPopped.add(new Boolean(popped));
        }
        status=M.status;
        oldstatus=M.oldstatus;
        funddomon=M.funddomon;
        funddom=M.funddom.copy();
        symchart=group.symchart;
        symcharton=M.symcharton;
        selectionmodeon=M.selectionmodeon;
        partialselectionmodeon=M.partialselectionmodeon;
        hidegroup=M.hidegroup;
        xtou=(AffineTransform) M.xtou.clone();
        utox=(AffineTransform) M.utox.clone();
        if (M.snake!=null){
            snake=M.snake.copy();
        } 
        else {
            snake=null;
        }
        textfont=M.textfont;
        textstring=M.textstring;
    }
    
    public Motive copy(){
        Motive M=new Motive(this);
        M.title=title+"_copie";
        return M;
    }
    
    boolean isEmpty(){
        return ((drawables.size()==0)&&(snake==null));
    }
    
    public void write(BufferedWriter out){
        try {
            out.write("//Title"); out.newLine();
            out.write(title); out.newLine();
            out.write("//Group"); out.newLine();
            out.write(""+group.number); out.newLine();
            out.write("//Subgroup"); out.newLine();
            out.write(""+subgroup.number); out.newLine();
            out.write("//Motive counter"); out.newLine();
            out.write(""+number); out.newLine();
            out.write("//Status"); out.newLine();
            out.write(""+status); out.newLine();
            out.write("//Old status"); out.newLine();
            out.write(""+oldstatus); out.newLine();
            out.write("//Fundamental domain on"); out.newLine();
            if (funddomon){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
            out.write("//Symmetry chart on"); out.newLine();
            if (symcharton){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
            out.write("//Group hidden"); out.newLine();
            if (hidegroup){
                out.write("1");
            } 
            else {
                out.write("0");
            }
            out.newLine();
            out.write("//Preview mode"); out.newLine();
            if (previewmode){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
            out.write("//Selection mode"); out.newLine();
            if (selectionmodeon){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
            out.write("//Partial selection mode"); out.newLine();
            if (partialselectionmodeon){
                out.write("1");
            }
            else {
                out.write("0");
            }
            out.newLine();
            if (partialselectionmodeon){
                out.write("//Partially selected drawable"); out.newLine();
                out.write(""+drawables.indexOf(partiallyselecteddrawable)); out.newLine();
            }
            out.write("//Transform utox"); out.newLine();
            Utils.writeAffineTransform(out,utox);
            out.write("//Transform xtou"); out.newLine();
            Utils.writeAffineTransform(out, xtou);
            out.write("//Current attribute"); out.newLine();
            currentattr.write(out);
            out.write("//Text string"); out.newLine();
            Utils.writestring(out, textstring);
            out.write("//Font name"); out.newLine();
            Utils.writestring(out, textfont.getFontName());
            Utils.writeint(out, "Font size", textfont.getSize());
            Utils.writeint(out, "Font style", textfont.getStyle());
            
            if (snake!=null){
                out.write("//Snake"); out.newLine();
                snake.write(out);
            }
            
            out.write("//Number of drawables"); out.newLine();
            out.write(""+drawables.size()); out.newLine();
            out.write("//Drawables"); out.newLine();
            for (int i=0; i<drawables.size(); i++){
                drawables.elementAt(i).write(out);
            }
            out.write("//Index of current drawable"); out.newLine();
            out.write(""+drawables.indexOf(currentDrawable)); out.newLine();
            if (selectedDrawables!=null){
                out.write("//Number of selected drawables"); out.newLine();
                out.write(""+selectedDrawables.size()); out.newLine();
                out.write("//Selected drawables"); out.newLine();
                for (int i=0; i<selectedDrawables.size(); i++){
                    out.write(""+drawables.indexOf(selectedDrawables.elementAt(i)));
                    out.newLine();
                }
            }
        }
        catch (IOException e){
                System.out.println(e.getMessage());
            }
    }
    
    
    
    
    public void read(BufferedReader r){
        try {
            String str;
            title=Utils.readstring(r);
            int n=Utils.readint(r);
            group=CrystalDrawing.groups[n];
            n=Utils.readint(r);
            subgroup=group.subgroups[n];
            funddom=new Funddom(subgroup.funddom,subgroup);
            symchart=group.symchart;
            number=Utils.readint(r);
            status=Utils.readint(r);
            oldstatus=Utils.readint(r);
            funddomon=Utils.readboolean(r);
            symcharton=Utils.readboolean(r);
            hidegroup=Utils.readboolean(r);
            previewmode=Utils.readboolean(r);
            selectionmodeon=Utils.readboolean(r);
            partialselectionmodeon=Utils.readboolean(r);
            int indexPartiallySelectedDrawable=0;
            if (partialselectionmodeon){
                indexPartiallySelectedDrawable=Utils.readint(r);
            }
            utox=Utils.readAffineTransform(r);
            xtou=Utils.readAffineTransform(r);
            str=r.readLine();
            currentattr.read(r);
            str=r.readLine();
            if (str.equals("//Text string")) {
                textstring=Utils.readstringnoline(r);
                String fontname=Utils.readstring(r);
                int fontsize=Utils.readint(r);
                int fontstyle=Utils.readint(r);
                textfont=new Font(fontname, fontstyle, fontsize);
            }
            str=r.readLine();
            int nberdrawables;
            if (str.equals("//Snake")){
                System.out.println(str);
                snake=new Snake(subgroup,getCenter());
                str=r.readLine();
                System.out.println(str);
                str=r.readLine();
                System.out.println(str);
                snake.read(r);
                nberdrawables=Utils.readint(r);
            }
            else {
                str=r.readLine();
                nberdrawables=Integer.parseInt(str);
            }
            str=r.readLine();
            for (int i=0; i<nberdrawables;i++){
                int type=Utils.readint(r);
                switch (type){
                    case CrystalDrawing.SEGMENT:
                        Segment s=new Segment(); 
                        s.read(r);
                        add(s);
                        break;
                    case CrystalDrawing.POLYGON:
                        Polygone p=new Polygone(); 
                        p.read(r);
                        add(p);
                        break;
                    case CrystalDrawing.BEZIERPATH:
                        BezierPath bp=new BezierPath(); 
                        bp.read(r);
                        add(bp);
                        break;
                    case CrystalDrawing.CIRCLE:
                        Circle c=new Circle(); 
                        c.read(r);
                        add(c);
                        break;
                    case CrystalDrawing.BLOB:
                        Blob bl=new Blob();
                        bl.read(r);
                        add(bl);
                        break;
                    case CrystalDrawing.TEXT:
                        Texte t=new Texte();
                        t.read(r);
                        add(t);
                }
            }
            int indexofcurrentDrawable=Utils.readint(r);
            if (indexofcurrentDrawable>=0){
                currentDrawable=drawables.elementAt(indexofcurrentDrawable);
            }
            else {
                currentDrawable=null;
            }
            int nberselecteddrawables=Utils.readint(r);
            if (nberselecteddrawables>0){
                selectedDrawables=new Stack();
            }
            r.readLine();
            //System.out.println(str);
            //System.out.println("Number of selected drawables:"+nberselecteddrawables);
            for (int i=0;i<nberselecteddrawables;i++){
                str=r.readLine();
                int sd=Integer.parseInt(str);
                selectedDrawables.add(drawables.elementAt(sd));
            }
            if (partialselectionmodeon){
                if (indexPartiallySelectedDrawable>=0){
                    partiallyselecteddrawable=drawables.elementAt(indexPartiallySelectedDrawable);
                }
                else {
                    partiallyselecteddrawable=null;
                }
            }
        }
        catch (IOException e){
                    System.out.println("I/O exception: "+ e.getMessage());
        }
    }
    
    public MPoint getCenter(){
       float x=rect.width/2;
       float y=rect.height/2;
       MPoint P=new MPoint(x,y);
       xtou.transform(P, P);
       return P;
   }
    
    public float eps(){
        MPoint P0=new MPoint(0,0);
        MPoint P1=new MPoint(xeps,0);
        xtou.transform(P0, P0);
        xtou.transform(P1, P1);
        return (float) P0.distance(P1);
    }
    
    public void setPenwidth(float w){
        Drawable.penwidth=w;
    }
    
    public void setMarkdiam(float d){
        Drawable.markdiam=d;
    }
    
    public void add(Drawable d){
        drawables.add(d);
    }
    
    public void setSubgroup(Subgroup sgr){
        subgroup=sgr;
        group=subgroup.group;
        funddom=new Funddom(subgroup.funddom,subgroup);
        symchart=group.symchart;
        System.out.println("setSubgroup: "+subgroup.name);
    }
    
    public Subgroup getSubgroup(){
        return subgroup;
    }
    
    
    
     public void initializextou(float xmi, float xma, float ymi, float yma, Rectangle rect){
        float uma=(float) rect.getWidth();
        float vma=(float) rect.getHeight();
        float uc=uma/2;
        float vc=vma/2;
        float s;
        if (uma>vma){
            s=vma/(yma-ymi);
        }
        else {
            s=uma/(xma-xmi);
        }
        utox=new AffineTransform(s,0,0,-s,uc,vc);
        //System.out.println("utox: "+utox);
        xtou=new AffineTransform(utox);
        try {
            xtou.invert();
        }
        catch (NoninvertibleTransformException  e){
	    System.out.println("initializextou: "+e.getMessage());
	}
        //System.out.println("xtou: "+xtou);
   }
     
  public void initializeLimboDrawables(){
      limboDrawables.removeAllElements();
      for (int i=0;i<selectedDrawables.size();i++){
          Drawable D=selectedDrawables.elementAt(i);
          limboDrawables.add(D.copy());
      }
  }
  
  public String drawablesToString(){
      String str="";
      for (int i=0;i<drawables.size();i++){
          str=str+drawables.elementAt(i).toString();
      }
      return str;
  }
  
  public String limboDrawablesToString(){
      String str="";
      for (int i=0;i<limboDrawables.size();i++){
          str=str+limboDrawables.elementAt(i).toString();
      }
      return str;
  }
  
  public String selectedDrawablesToString(){
      String str="";
      for (int i=0;i<selectedDrawables.size();i++){
          str=str+selectedDrawables.elementAt(i).toString();
      }
      return str;
  }
  
  
     
   public boolean selectionNotEmpty(){
       return (selectedDrawables.size()>0);
   }
   
   public void setSelectionAttribute(Attribute attr){
       for (int i=0; i<selectedDrawables.size(); i++){
           selectedDrawables.elementAt(i).attr=(Attribute) attr.clone();
       }
   }
   
   public void setSelectionSubgroup(Subgroup sgr){
       for (int i=0;i<selectedDrawables.size(); i++){
           selectedDrawables.elementAt(i).setSubgroup(sgr);
       }
   }
   
   public void initializeSelection(Drawable d){
       if (d!=null){
           selectedDrawables.removeAllElements();
           selectedDrawables.add(d);
           d.isselected=true;
       }
       for (int i=0; i<drawables.size();i++){
           drawables.elementAt(i).setSelectionMode(true);
       }
       selectionmodeon=true;
   }
   
   public void toggleSelectionPropagation(Stack<int[]> hits){
       int[] hit;
       for (int i=0;i<selectedDrawables.size();i++){
           for (int j=0;j<hits.size();j++){
                selectedDrawables.elementAt(i).togglePropagation(hits.elementAt(j));
           }
       }
   }
   
   public void copyTransformedSelection(Stack<int[]> hits){
       System.out.println("copyTransformedSelection: hits.size()="+hits.size());
       Stack<Drawable> copiedDrawables=new Stack();
       for (int j=0;j<hits.size();j++){
           int[] hit=hits.elementAt(j);
           AffineTransform T=Utils.groupElement(hit);
           Drawable D;
           int n=selectedDrawables.size();
           System.out.println("j="+j);
           System.out.println("selectedDrawables.size()="+n);
           for (int i=0;i<n;i++){
               D=selectedDrawables.elementAt(i).transform(T);
               System.out.println("i="+i+" copying drawable \n"+D);
               D.propagateOne();
               System.out.println("propagation.size()="+D.propagation.size());
               copiedDrawables.add(D);
           }
       }
       System.out.println("copiedDrawables.size()="+copiedDrawables.size());
       for (int i=0;i<copiedDrawables.size();i++){
           Drawable D=copiedDrawables.elementAt(i);
           System.out.println("Adding D. D.propagation.size()="+D.propagation.size());
           add(D);
           selectedDrawables.add(D);
       }
   }
   
   public void transformSelectionBy(AffineTransform T){
       for (int i=0;i<selectedDrawables.size();i++){
           limboDrawables.setElementAt(selectedDrawables.elementAt(i).transform(T),i);
       }
   }
   
   public void addLimboDrawables(){
       for (int i=0; i<limboDrawables.size();i++){
           drawables.add(limboDrawables.elementAt(i));
           selectedDrawables.add(limboDrawables.elementAt(i));
       }
   }
   
   public void replaceSelectedDrawablesWithLimbo(){
       for (int i=0; i<selectedDrawables.size();i++){
           drawables.removeElement(selectedDrawables.elementAt(i));
           drawables.add(limboDrawables.elementAt(i));
       }
       selectedDrawables.removeAllElements();
       for (int i=0; i<limboDrawables.size();i++){
           selectedDrawables.add(limboDrawables.elementAt(i));
       }
       limboDrawables.removeAllElements();
   }
   
   public void clearLimboDrawables(){
       limboDrawables.removeAllElements();
   }
   
   public void removeBlob(){
       for (int i=0; i<drawables.size();i++){
           if (drawables.elementAt(i).Type==CrystalDrawing.BLOB){
               drawables.removeElementAt(i);
           }
       }
   }
   
   public void centerAt(MPoint C1){
        //System.out.println("Rectangle: "+rect);
        MPoint C2=new MPoint(rect.width/2, rect.height/2);
        //System.out.println("Center: "+C2);
        xtou.transform(C2,C2);
        double tx=C2.getX()-C1.getX();
        double ty=C2.getY()-C1.getY();
        AffineTransform T=new AffineTransform(1,0,0,1,-tx,-ty);
        xtou.preConcatenate(T);
        utox=new AffineTransform(xtou);
        try {
            utox.invert();
        }
        catch (NoninvertibleTransformException  e){
            System.out.println("Translater: "+e.getMessage());
        }
   }
   
   public String selectionToString(){
       String str="";
       for (int i=0; i<selectedDrawables.size();i++){
           str=str+selectedDrawables.elementAt(i).toString();
       }
       return str;
   }
   
   public MPoint getSelectionBarycenter(){
       return Utils.barycenter(selectedDrawables);
       
   }
   
   public MPoint getBarycenter(){
       return Utils.barycenter(drawables);
   }
   
   public Drawable popDrawable(){
       Drawable d=null;
       if (drawables.size()>1){
           drawables.pop();
           d=drawables.peek();
       }
       else if (drawables.size()>0){
           drawables.pop();
           d= null;
       }
       return d;
   }
   
   public void moveSelectionToBackground(){
       for (int i=0; i<selectedDrawables.size(); i++){
         Drawable D=selectedDrawables.elementAt(i);
         D.togglebackground();
       }
       selectedDrawables.removeAllElements();
   }
   
   public void bringBackgroundForward(){
       System.out.println("bringBackgroundForward");
       for (int i=0; i<drawables.size();i++){
           Drawable D=drawables.elementAt(i);
           if (D.isinbackground){
               D.togglebackground();
               if (selectionmodeon){
                   D.setSelectionMode(true);
               }
               if (partialselectionmodeon){
                   D.setPartialSelectionMode(true);
               }
           }
           //System.out.println("i: "+i+" D is in background: "+D.isinbackground);
       }
   }
    
    public void setSelectionMode(boolean sel){
        selectionmodeon=sel;
        for (int i=0;i<drawables.size();i++){
            drawables.elementAt(i).setSelectionMode(sel);
        }
    }
    
    public void setPartialSelectionMode(boolean sel){
        partialselectionmodeon=sel;
        for (int i=0;i<drawables.size();i++){
            drawables.elementAt(i).setPartialSelectionMode(sel);
        }
        System.out.println("Drawables size: "+drawables.size());
    }
    
    public void makePartialSelection(Rectangle rect, AffineTransform utox){
        for (int i=0;i<drawables.size();i++){
            drawables.elementAt(i).makePartialSelection(rect, utox);
        }
    }
    
    public Drawable makePartialSelection(int x, int y, AffineTransform utox, boolean shiftdown){
        //System.out.println("Motive choose selection: "+x+", "+y+" "+utox);
        boolean found=false;
        partiallyselecteddrawable=null;
        int i;
        if (!shiftdown){
            for (i=0;i<drawables.size();i++){
                drawables.elementAt(i).setPartialSelection(false);
                drawables.elementAt(i).unselectPoints();
            }
        }
        i=0;
        while ((i<drawables.size())&& (!found)){
            found=drawables.elementAt(i).makePartialSelection(x,y,utox, shiftdown);
            i++;
        }
        if (found){
            i--;
            drawables.elementAt(i).setPartialSelection(true);
            partiallyselecteddrawable=drawables.elementAt(i);
            System.out.println("Partially selected drawable: "+i);
        }
        return partiallyselecteddrawable;
    }
    
    public void clearSelection(){
        System.out.println("Clear selection");
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).setSelectionMode(false);
            drawables.elementAt(i).setSelection(false);        
        }
        selectedDrawables.removeAllElements();
        selectionmodeon=false;
    }
    
    public MPoint findPressedPointInSelection(MPoint Pt){
        float eps=eps();
        boolean found=false;
        MPoint res=null;
        Drawable D;
        int i=0;
        while ((i<selectedDrawables.size()) && (!found)){
            D=selectedDrawables.elementAt(i);
            double dist=D.minimalDistance(Pt, new MPoint())[0];
            if (dist<eps){
                found=true;
            }
            i++;
        }
        if (!found){
            res=null;}
        else {
            res=Pt;
        }
        return res;
    }
    
    public MPoint findPressedPointInSelection(int x, int y, AffineTransform utox){
        boolean found=false;
        int i=0;
        Drawable D;
        MPoint P=null;
        while ((i<selectedDrawables.size()) && (!found)){
            D=selectedDrawables.elementAt(i);
            P=D.hitPoint(x,y,utox);    
            if (P!=null){
                found=true;
            }
            i++;
        }
        return P;
    }
    
    public MPoint findPressedPointInPartialSelection(int x, int y, AffineTransform utox){
        boolean found=false;
        int i=0;
        Drawable D;
        MPoint P=null;
        while ((i<drawables.size()) && (!found)){
            D=drawables.elementAt(i);
            P=D.hitPointAll(x,y,utox);    
            if ((P!=null)&&(P.isSelected())){
                found=true;
            }
            i++;
        }
        if (!found){
            P=null;
        }
        return P;
    }
    
    public Drawable[] findSelectedDrawables(Rectangle rect, AffineTransform utox){
        Stack<Drawable> foundDrawables=new Stack();
        MPoint P=null;
        for (int i=0;i<drawables.size();i++){
            P=drawables.elementAt(i).hitPoint(rect, utox);
            if (P!=null){
                foundDrawables.add(drawables.elementAt(i));
            }
        }
        Drawable[] res=null;
        if (foundDrawables.size()>0){
            res=new Drawable[foundDrawables.size()];
            for (int i=0;i<foundDrawables.size();i++){
                res[i]=foundDrawables.elementAt(i);
            }
        }
        return res;
    }
    
    public Drawable findSelectedDrawable(MPoint Pt){
        float eps=eps();
        double dist;
        Drawable D=null;
        boolean found=false;
        int i=0;
        while((i<drawables.size())&&(!found)){
            D=drawables.elementAt(i);
            if (!D.isinbackground){
                double[] minD=D.minimalDistance(Pt, new MPoint());
                if (minD!=null){
                    dist=minD[0];
                    if (dist<eps){
                        found=true;
                    }
                }
            }
            i++;
        }
        if (!found){
            D=null;
        }
       return D;
    }
    
    
    public void moveSelection(MPoint fromPt, MPoint toPt){
        float dx=(float) (toPt.getX()-fromPt.getX());
        float dy=(float) (toPt.getY()-fromPt.getY());
        for (int i=0;i<selectedDrawables.size();i++){
            selectedDrawables.elementAt(i).translate(dx,dy);
        }
    }
    
    public void movePartialSelection(MPoint fromPt, MPoint toPt, boolean altdown){
        float dx=(float) (toPt.getX()-fromPt.getX());
        float dy=(float) (toPt.getY()-fromPt.getY());
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).translatePartialSelection(dx,dy,altdown);
        }
    }
    
    public void selectAll(){
        setSelectionMode(true);
        selectedDrawables.removeAllElements();
        Drawable D;
        for (int i=0; i<drawables.size();i++){
            D=drawables.elementAt(i);
            D.setSelectionMode(true);
            D.setSelection(true);
            selectedDrawables.add(D);
        }
    }
    
    public void addSelection(Drawable[] D){
        if (D!=null){
            for (int i=0;i<D.length;i++){
                D[i].setSelectionMode(true);
                D[i].setSelection(true);
                selectedDrawables.add(D[i]);
            }
        }
    }
    
    public void addSelection(Drawable D){
        if (D!=null){
            D.setSelectionMode(true);
            D.setSelection(true);
            selectedDrawables.add(D);
        }
    }
    
/*
    public void modifyPartialSelection(int x, int y, AffineTransform xtou, boolean altdown){
        if (partiallyselecteddrawable!=null){
            partiallyselecteddrawable.modifyPartialSelection(x,y,xtou, altdown);
        }
    }
*/
    
    public void unselectDrawables(){
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).setSelection(false);        
        }
        selectedDrawables.removeAllElements();
    }
    
    
    
    public void unpartiallySelectDrawables(){
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).setPartialSelectionMode(false);
            drawables.elementAt(i).setPartialSelection(false);
        }
    }
    
    public void unpartiallySelectPoints(){
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).unselectPoints();
            drawables.elementAt(i).setPartialSelection(false);
        }
    }
    
    public void mergePartialSelection(){
        Stack<MPoint> partiallySelectedPoints=new Stack();
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).getPartiallySelectedPoints(partiallySelectedPoints);
        }
        MPoint barycenter=Utils.pointbarycenter(partiallySelectedPoints);
        for (int i=0; i<partiallySelectedPoints.size();i++){
            partiallySelectedPoints.elementAt(i).setLocation(barycenter);
        }
    }
    
    public void makedrawablesgray(){
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).setColor(Color.gray);
        }
    }
    
    public void setColorDrawables(Color color){
        for (int i=0;i<drawables.size();i++){
            drawables.elementAt(i).setColor(color);
        }
    }
    
    public void setColorSelectedDrawables(Color color){
        for (int i=0;i<selectedDrawables.size();i++){
            selectedDrawables.elementAt(i).setColor(color);
        }
    }
    
    public void setColorLimboDrawables(Color color){
        for (int i=0;i<limboDrawables.size();i++){
            limboDrawables.elementAt(i).setColor(color);
        }
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
    
    public void deletePoint(int x, int y){
        for (int i=0;i<drawables.size();i++){
            Drawable D=drawables.elementAt(i);
            boolean found=D.chooseSelection(x,y, utox);
            if (found){
                D.removeSelectedPoint();
                if (D.isempty()){
                    drawables.removeElement(D);
                }
            }
        }
    }
    
    public void insertPoint(MPoint Pt){
        float eps=eps();
        double dist=100000;
        double[] resdist=new double[2];
        MPoint closestPoint=new MPoint();
        int i=0;
        while ((i<drawables.size())&&(dist>eps)){
            resdist=drawables.elementAt(i).minimalDistance(Pt, closestPoint);
            dist=resdist[0];
            System.out.println("Distance: "+dist);
            i++;
        }
        i--;
        if (dist<=eps){
            Drawable D=drawables.elementAt(i);
            switch (D.Type){
                case SEGMENT:
                    Segment S=(Segment) D;
                    Segment Snew=S.copy();
                    Snew.P1=closestPoint.copy();
                    Snew.P2=S.P2.copy();
                    S.P2=closestPoint.copy();
                    add(Snew);
                    if (Snew.isselected){
                        this.addSelection(Snew);
                    }
                    break;
                case POLYGON:
                    i=(int) resdist[1];
                    Polygone Poly=(Polygone) D;
                    Poly.P.add(i+1,closestPoint.copy());
                    break;
                case BEZIERPATH:
                    i=(int) resdist[1];
                    float t= (float) resdist[2];
                    BezierPath Bez=(BezierPath) D;
                    MPoint P0=Bez.P.elementAt(3*i);
                    MPoint P1=Bez.P.elementAt(3*i+1);
                    MPoint P2=Bez.P.elementAt(3*i+2);
                    MPoint P3=Bez.P.elementAt(3*i+3);
                    float vx=-(1-t)*(1-t)*P0.x+(3*t*t-4*t+1)*P1.x+(-3*t*t+2*t)*P2.x+t*t*P3.x;
                    float vy=-(1-t)*(1-t)*P0.y+(3*t*t-4*t+1)*P1.y+(-3*t*t+2*t)*P2.y+t*t*P3.y;
                    MPoint Q=closestPoint.copy();
                    MPoint Qm1=new MPoint(Q.x-t*vx,Q.y-t*vy);
                    MPoint Qp1=new MPoint(Q.x+(1-t)*vx,Q.y+(1-t)*vy);
                    float wx=P1.x-P0.x;
                    float wy=P1.y-P0.y;
                    P1.setLocation(P0.x+t*wx,P0.y+t*wy);
                    wx=P2.x-P3.x;
                    wy=P2.y-P3.y;
                    P2.setLocation(P3.x+(1-t)*wx,P3.y+(1-t)*wy);
                    Bez.P.add(3*i+2,Qp1);
                    Bez.P.add(3*i+2,Q);
                    Bez.P.add(3*i+2,Qm1);
                    break;
            }
        }
    }
    
    public void togglefunddom(){
        if (funddomon){
            funddomon=false;
        }
        else {
            funddomon=true;
        }
    }
    
    public void togglesymchart(){
        if (symcharton){
            symcharton=false;
        }
        else {
            symcharton=true;
        }
    }
    
    public Drawable getCurrentDrawable(){
        Drawable D=null;
        if (selectedDrawables.size()>0){
            D=selectedDrawables.peek();
        } else if (currentDrawable!=null){
            D=currentDrawable;
        } else if (drawables.size()>0){
            int i=drawables.size()-1;
            while ((i>0)&&(drawables.elementAt(i).isinbackground)){
                i--;
            }
            if (i>0){
                D=drawables.elementAt(i);
            }
        }
        return D;
    }
    
    public Blob getCurrentBlob(){
        Blob B=null;
        if (drawables.size()>0){
            Drawable D=drawables.peek();
            if (D.Type==CrystalDrawing.BLOB){
                B=(Blob) D;
            }
        }
        return B;
    }
    
    public void propagateAllSelection(){
        for (int i=0; i<selectedDrawables.size(); i++){
            selectedDrawables.elementAt(i).propagateAll();
        }
    }
    
    public void propagateOneSelection(){
        for (int i=0; i<selectedDrawables.size();i++){
            selectedDrawables.elementAt(i).propagateOne();
        }
    }
    
    public MPoint nearPoint(MPoint Pt, float dist){
        MPoint N=null;
        int i=0;
        while ((i<drawables.size())&&(N==null)){
            N=drawables.elementAt(i).nearPoint(Pt, dist);
            i++;
        }
        return N;
    }
    
    public String tikzorbit(AffineTransform utox, Rectangle rect){
        String str="";
        
        if (funddomon){
                //System.out.println("Drawing fundamental domain");
                str=str+funddom.tikzorbit(utox,rect,previewmode);
        }
        
        if (symcharton){
            //System.out.println("Drawing symmetry chart");
            str=str+symchart.tikzorbit(utox, rect);
        }
        
        for (int i=0; i<drawables.size();i++){
            str=str+drawables.elementAt(i).tikzorbit(utox, rect, previewmode);
        }
        return str;
    }
    
    public void draworbit(Graphics2D g, AffineTransform utox, Rectangle rect,boolean showlimbodrawables){
        
        if (funddomon){
                //System.out.println("Drawing fundamental domain");
                funddom.draworbit(g,utox,rect,previewmode);
        }
        
        if (symcharton){
            //System.out.println("Drawing symmetry chart");
            symchart.draworbit(g, utox, rect,false);
        }
        
        //System.out.println("Drawables: \n"+drawablesToString());
        //this.setColorDrawables(Color.blue);
        for (int i=0; i<drawables.size();i++){
            drawables.elementAt(i).draworbit(g,utox, rect, previewmode);
        }
        
        //System.out.println("Limbodrawables: \n"+limboDrawablesToString());
        //this.setColorLimboDrawables(Color.red);
        if (showlimbodrawables){
            for (int i=0; i<limboDrawables.size();i++){
                limboDrawables.elementAt(i).draworbit(g, utox, rect, previewmode);
            }
        }
    }
    

}

