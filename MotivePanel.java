
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class MotivePanel extends JPanel implements ComponentListener, 
                MouseMotionListener, MouseListener {
    
   public static final int WAITING_FOR_SEGMENT=0;
   public static final int WAITING_FOR_GLASS1=1;
   public static final int WAITING_FOR_GLASS2=2;
   public static final int WAITING_FOR_SELECTION=3;
   public static final int WAITING_FOR_PARTIAL_SELECTION=4;
   public static final int WAITING_FOR_POLYGON1=5;
   public static final int WAITING_FOR_POLYGON2=6;
   public static final int WAITING_FOR_BEZIER1=7;
   public static final int WAITING_FOR_BEZIER2=8;
   public static final int WAITING_FOR_BEZIER3=9;
   public static final int WAITING_FOR_BEZIER4=10;
   public static final int WAITING_FOR_BEZIER5=11;
   public static final int WAITING_FOR_CIRCLE=12;
   public static final int WAITING_FOR_TRANSLATOR=13;
   public static final int WAITING_FOR_PROPAGATOR=14;
   public static final int WAITING_FOR_SNAKE=15;
   public static final int WAITING_FOR_TEXT=16;
   public static final int WAITING_FOR_HOMOTHETY1=17;
   public static final int WAITING_FOR_HOMOTHETY2=18;
   public static final int WAITING_FOR_ROTATION1=19;
   public static final int WAITING_FOR_ROTATION2=20;
   public static final int WAITING_FOR_SYMMETRY1=21;
   public static final int WAITING_FOR_SYMMETRY2=22;
   public static final int WAITING_FOR_GLIDE1=23;
   public static final int WAITING_FOR_GLIDE2=24;
   public static final int WAITING_FOR_TEST1=27;
   public static final int WAITING_FOR_TEST2=31;
   public static final int WAITING_FOR_TEST3=32;
   public static final int WAITING_FOR_TEST4=33;
   public static final int WAITING_FOR_INSERTION=28;
   public static final int WAITING_FOR_DELETION=29;
   public static final int WAITING_FOR_CONTINUE=30;
   public static final int WAITING_FOR_VORONOI1=34;
   public static final int WAITING_FOR_VORONOI2=35;
   public static final int WAITING_FOR_DELAUNAY1=36;
   public static final int WAITING_FOR_DELAUNAY2=37;
   public static final int WAITING_FOR_MERGE=38;
   
   public static final int FUNDDOM=-1;
   public static final int SEGMENT=0;
   public static final int POLYGON=1;
   public static final int BEZIERPATH=2;
   public static final int CIRCLE=3;
   public static final int BLOB=4;
   public static final int SNAKE=5;
   public static final int TEXT=6;
    
    Motive M;
    CrystalDrawing cd;
    
    MPoint fromPoint, toPoint, fromSelectedPoint, toSelectedPoint, fromTranslatePoint, toTranslatePoint,transformationCenter;
    Point glassPoint1, glassPoint2;
    Rectangle glassRect=null;
    boolean showlimbodrawables;
    
    Polygone voronoiPolygon=null;
    Circle voronoiCircle=null;
    Stack<MPoint> voronoiPointSet=null;
    int voronoiCtr=0;
    Segment[] delaunaySegments=null;
    
    
    
    public MotivePanel(Motive argM, CrystalDrawing argcd){
        M=argM;
        cd=argcd;
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
    }
    
    
    public void paintComponent(Graphics gfx) {
        //setBackground(Color.black);
        System.out.println("Paint MotivePanel");
        super.paintComponent(gfx); 
        Graphics2D g = (Graphics2D) gfx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                   RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (M.currentDrawable==null){
            M.currentDrawable=M.getCurrentDrawable();
        }
        
        if (cd.multiplemodeon){
            //System.out.println("Multiple mode on");
            Rectangle rect=new Rectangle();
            g.getClipBounds(rect);
            rect.grow(-10,-10);
            if (M==cd.M){
                rect.grow(-2,-2);
                g.setStroke(new BasicStroke(3));
                g.draw(rect);
            }
            else {
                rect.grow(-2,-2);
                g.setStroke(new BasicStroke(1));
                g.draw(rect);
            }
            rect.grow(-2,-2);
            g.setClip(rect);
        }
        
        g.setStroke(new BasicStroke(Drawable.penwidth));
        if (glassRect!=null){
            g.setPaint(Color.gray);
            g.draw(glassRect);
        }
        
        if ((fromPoint!=null)&&(toPoint!=null)){
            Point P1=new Point();
            Point P2=new Point();
            M.utox.transform(fromPoint, P1);
            M.utox.transform(toPoint, P2);
            g.setPaint(Color.gray);
            g.drawLine(P1.x,P1.y,P2.x,P2.y);
        }
        
        if (fromPoint!=null){
            Drawable.drawmark(g, M.utox, fromPoint, Color.white);
        }
        
        if (toPoint!=null){
            Drawable.drawmark(g,M.utox,toPoint,Color.white);
        }
        
        if (transformationCenter!=null){
            Drawable.drawmark(g, M.utox, transformationCenter, Color.white);
        }
        
        if (voronoiPolygon!=null){
                voronoiPolygon.draworbit(g, M.utox, getBounds(), M.previewmode);
            }
        
        if (delaunaySegments!=null){
            for (int i=0;i<delaunaySegments.length;i++){
                delaunaySegments[i].draworbit(g,M.utox,getBounds(), M.previewmode);
            }
        }
        
        
        
        M.draworbit(g,M.utox,getBounds(),showlimbodrawables);
     }
    
    public void updateMotiveRectangle(){
        M.rect=getBounds();
    }
    
    public void componentHidden(ComponentEvent e) {
        
    }

    public void componentMoved(ComponentEvent e) {
        
    }

    public void componentResized(ComponentEvent e) {
        repaint();
    }

    public void componentShown(ComponentEvent e) {
        
    }
    

    public void mouseMoved(MouseEvent me) { }
    
    public void treatClickForSelection(int x, int y, boolean shiftdown){
        System.out.println("treatClickForSelection 1, shiftdown: "+shiftdown);
        MPoint Pt=new MPoint(x,y);
        M.xtou.transform(Pt, Pt);
        Drawable D=M.findSelectedDrawable(Pt);
        System.out.println("treatClickForSelection 2: D found: "+D);
        if (D==null){
            M.unselectDrawables();
            cd.currentDrawable=M.getCurrentDrawable();
        }
        else {
            cd.currentDrawable=D;
            if (shiftdown){
                M.addSelection(D);
            }
            else {
                M.unselectDrawables();
                M.addSelection(D);
            }
        }
    }
    
    public void mouseClicked(MouseEvent me) {
        System.out.println("Mouse clicked.");
        int x,y, x1, y1;
        x=me.getX();
        x1=x;
        y=me.getY();
        y1=y;
        
        boolean altdown=((me.getModifiersEx() & MouseEvent.ALT_DOWN_MASK)==MouseEvent.ALT_DOWN_MASK);
        boolean shiftdown=((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK);
        
        if (shiftdown){
            Point P=corrPoint(x,y);
            x=P.x;
            y=P.y;
        }
        
        switch (M.status){
            case WAITING_FOR_ROTATION1:
                //System.out.println("Waiting for rotation 1");
                transformationCenter=new MPoint(x,y);
                M.xtou.transform(transformationCenter, transformationCenter);
                if (altdown){
                    //System.out.println("Altdown");
                    RotationDialog rd=new RotationDialog(cd.fr);
                    boolean tocopy=rd.getToCopy();
                    float angle=rd.getAngle();
                    AffineTransform T=Utils.rotation(transformationCenter, angle);
                    M.transformSelectionBy(T);
                    if (tocopy){
                        M.addLimboDrawables();
                    }
                    else {
                        M.replaceSelectedDrawablesWithLimbo();
                    }
                    M.clearLimboDrawables();
                    cd.updatestatus(M.oldstatus);
                }
                else {
                    showlimbodrawables=true;
                    cd.updatestatus(WAITING_FOR_ROTATION2);
                }
                break;
            case WAITING_FOR_HOMOTHETY1:
                transformationCenter=new MPoint(x,y);
                M.xtou.transform(transformationCenter, transformationCenter);
                showlimbodrawables=true;
                cd.updatestatus(WAITING_FOR_HOMOTHETY2);
                break;
            case WAITING_FOR_PARTIAL_SELECTION:
                    cd.currentDrawable=M.makePartialSelection(x, y, M.utox, shiftdown);
                    repaint();
                    break;
            case WAITING_FOR_SELECTION:
                treatClickForSelection(x1,y1,shiftdown);
                repaint();
                break;
            case WAITING_FOR_PROPAGATOR:
                System.out.println("1 M.selectedDrawables.size()="+M.selectedDrawables.size());
                Stack<int[]> hits=cd.currentBlob.hitGroupElement(x,y,M.utox, getBounds());
                if (hits.size()>0){
                    //System.out.println("Group: "+hit[0]+" Subgroup: "+hit[1]+" Transformation: "+hit[2]+" i: "+hit[3]+" j: "+hit[4]);
                    if (!altdown){
                        M.toggleSelectionPropagation(hits);
                        repaint();
                    }
                    else {
                        System.out.println("2 M.selectedDrawables.size()="+M.selectedDrawables.size());
                        M.copyTransformedSelection(hits);
                    }
                }
                else {
                    treatClickForSelection(x,y,shiftdown);
                    MPoint B=M.getSelectionBarycenter();
                    if (B!=null){
                        cd.currentBlob.setLocation(B);
                    }
                }
                repaint();
                break;
        }
    }
    
    public Point corrPoint(int x, int y){
        MPoint X=new MPoint(x,y);
        int x1;
        int y1;
        M.xtou.transform(X,X);
        MPoint nearP=M.nearPoint(X,0.05f);
        if (nearP==null){
            AffineTransform utov=M.transformutov(new MPoint(0,0));
            AffineTransform vtou=M.transformvtou(new MPoint(0,0));
            utov.transform(X,X);
            //System.out.println("u,v not rounded: "+X.getX()+" "+X.getY());
            float gs=CrystalDrawing.gridsize;
            float v1=Math.round(((float) X.getX())/gs)*gs;
            float v2=Math.round(((float) X.getY())/gs)*gs;
            X=new MPoint(v1,v2);
            //System.out.println("u,v rounded: "+X.getX()+" "+X.getY());
            vtou.transform(X, X);
            M.utox.transform(X,X);
            x1=(int) X.getX();
            y1=(int) X.getY();
        }
        else {
            System.out.println("Near point found");
            M.utox.transform(nearP, nearP);
            x1=(int) nearP.getX();
            y1=(int) nearP.getY();
        }
        return new Point(x1,y1);
    }
    
    
    
    public void mousePressed(MouseEvent me) {
        cd.mp=this;
        cd.changeCurrentMotive(M);
        cd.changesMade=true;
        System.out.println("Mouse pressed.");
        int x=me.getX();
        int y=me.getY();
        boolean altdown=((me.getModifiersEx() & MouseEvent.ALT_DOWN_MASK)==MouseEvent.ALT_DOWN_MASK);
        boolean shiftdown=((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK);
        
        if (shiftdown){
            Point P=corrPoint(x,y);
            x=P.x;
            y=P.y;
        }
        
        switch (M.status){
            case WAITING_FOR_CONTINUE:
                MPoint Pt=new MPoint(x,y);
                M.xtou.transform(Pt, Pt);
                Drawable D=M.findSelectedDrawable(Pt);
                if (D!=null){
                    switch (D.Type){
                        case POLYGON:
                            cd.updatestatus(WAITING_FOR_POLYGON2);
                            cd.currentDrawable=D;
                            break;
                        case BEZIERPATH:
                            BezierPath Bez=(BezierPath) D;
                            System.out.println("Bez.P.size(): "+Bez.P.size());
                            if (Bez.P.size()%3==1){
                                cd.updatestatus(WAITING_FOR_BEZIER3);
                            } 
                            else if (Bez.P.size()%3==2)
                            {
                                Bez.P.pop();
                                cd.updatestatus(WAITING_FOR_BEZIER3);
                            }
                            cd.currentDrawable=D;
                            break;
                    }
                }
                break;
            case WAITING_FOR_DELETION:
                M.deletePoint(x,y);
                repaint();
                break;
            case WAITING_FOR_INSERTION:
                //System.out.println("insertPoint");
                Pt=new MPoint(x,y);
                M.xtou.transform(Pt, Pt);
                M.insertPoint(Pt);
                repaint();
                break;
            case WAITING_FOR_GLIDE1:
                fromPoint=new MPoint(x,y);
                M.xtou.transform(fromPoint, fromPoint);
                cd.updatestatus(WAITING_FOR_GLIDE2);
                showlimbodrawables=true;
                break;
            case WAITING_FOR_SYMMETRY1:
                fromPoint=new MPoint(x,y);
                M.xtou.transform(fromPoint, fromPoint);
                cd.updatestatus(WAITING_FOR_SYMMETRY2);
                showlimbodrawables=true;
                break;
            case WAITING_FOR_GLIDE2:
                toPoint=new MPoint(x,y);
                M.xtou.transform(toPoint,toPoint);
                AffineTransform T=Utils.glideReflection(fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
            case WAITING_FOR_SYMMETRY2:
                toPoint=new MPoint(x,y);
                M.xtou.transform(toPoint,toPoint);
                T=Utils.symmetry(fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
            case WAITING_FOR_ROTATION2:
            case WAITING_FOR_HOMOTHETY2:
                fromPoint=new MPoint(x,y);
                M.xtou.transform(fromPoint, fromPoint);
                toPoint=new MPoint(x,y);
                M.xtou.transform(toPoint, toPoint);
                break;
            case WAITING_FOR_SNAKE:
                cd.stopSnake();
                cd.snakedialog.close();
                M.popDrawable();
                M.snake.clear(M.getCenter());
                setBackground(Color.white);
                cd.updatestatus(M.oldstatus);
                break;
            
            case WAITING_FOR_DELAUNAY1:
            case WAITING_FOR_VORONOI1:
                voronoiCircle=new Circle(x,y,x,y,M.getSubgroup(),M.xtou, M.currentattr);
                M.drawables.add(voronoiCircle);
                cd.currentDrawable=voronoiCircle;
                repaint();
                break;
            
            case WAITING_FOR_CIRCLE:
                Circle C=new Circle(x,y,x,y,M.getSubgroup(),M.xtou, M.currentattr);
                M.drawables.add(C);
                cd.currentDrawable=C;
                repaint();
                break;
            
            
            case WAITING_FOR_TEXT:
                Texte Txt=new Texte(x,y,M.getSubgroup(), M.xtou,M.textfont, M.textstring);
                M.drawables.add(Txt);
                cd.currentDrawable=Txt;
                repaint();
                break;
            case WAITING_FOR_TRANSLATOR:
                fromTranslatePoint =new MPoint(x,y);
                M.xtou.transform(fromTranslatePoint,fromTranslatePoint);
                break;
            case WAITING_FOR_SEGMENT:
                Segment S=new Segment(x,y,x,y,M.getSubgroup(),M.xtou, M.currentattr);
                M.drawables.add(S);
                cd.currentDrawable=S;
                //System.out.println("Adding a segment");
                //System.out.println("M.drawables.size()="+M.drawables.size());
                repaint();
                break;
            case WAITING_FOR_POLYGON1:
                Polygone P=new Polygone(x,y,x,y,M.getSubgroup(),M.xtou,M.currentattr);
                M.drawables.add(P);
                cd.currentDrawable=P;
                cd.updatestatus(WAITING_FOR_POLYGON2);
                repaint();
                break;
            case WAITING_FOR_POLYGON2:
                ((Polygone) cd.currentDrawable).add(x,y,M.xtou);
                repaint();
                break;
            case WAITING_FOR_BEZIER1:
                BezierPath B=new BezierPath(x,y,x,y,M.getSubgroup(),M.xtou,M.currentattr);
                M.drawables.add(B);
                M.setSelectionMode(true);
                cd.currentDrawable=B;
                System.out.println("Added "+B);
                System.out.println("B.selectionmodeon:"+B.selectionmodeon);
                cd.updatestatus(WAITING_FOR_BEZIER2);
                repaint();
                break;
            case WAITING_FOR_BEZIER3:
                ((BezierPath) cd.currentDrawable).addtwice(x,y,M.xtou);
                repaint();
                break;
            case WAITING_FOR_BEZIER4:
                ((BezierPath) cd.currentDrawable).defSymTangentPoint(x,y,M.xtou);
                cd.updatestatus(WAITING_FOR_BEZIER5);
                repaint();
                break;
            case WAITING_FOR_GLASS1:
                glassPoint1=new Point(x,y);
                cd.updatestatus(WAITING_FOR_GLASS2);
                repaint();
                break;
            case WAITING_FOR_PARTIAL_SELECTION:
                fromSelectedPoint=M.findPressedPointInPartialSelection(x,y,M.utox);
                System.out.println("fromSelectedPoint: "+fromSelectedPoint);
                if (fromSelectedPoint==null) {
                    glassPoint1=new Point(x,y);
                }
                repaint();
                break;
            case WAITING_FOR_MERGE:
                glassPoint1=new Point(x,y);
                repaint();
            case WAITING_FOR_PROPAGATOR:
                glassPoint1=new Point(x,y);
                break;
            case WAITING_FOR_SELECTION:
                Pt=new MPoint(x,y);
                M.xtou.transform(Pt, Pt);
                fromSelectedPoint=M.findPressedPointInSelection(Pt);
                if (fromSelectedPoint!=null){
                    M.initializeLimboDrawables();
                    showlimbodrawables=false;
                }
                glassPoint1=new Point(x,y);
                break;
        }
    }
    
    private Rectangle enclosingRectangle(Point P1, Point P2){
        System.out.println("P1: "+P1+" P2: "+P2);
        int x0=Math.min(P1.x, P2.x);
        int y0=Math.min(P1.y, P2.y);
        int width = Math.abs(P1.x-P2.x);
        int height= Math.abs(P1.y-P2.y);
        Rectangle r= new Rectangle(x0,y0,width,height);
        System.out.println("Rectangle: "+r);
        return r;
    }
    
    private Point center(Rectangle r){
        int x = (int) r.x+r.width/2;
        int y = (int) r.y+r.height/2;
        return new Point(x,y);
    }
    
    public void mouseReleased(MouseEvent me) {
        System.out.println("Mouse released.");
        //System.out.println("Current drawable:"+cd.currentDrawable);
        int x=me.getX();
        int y=me.getY();
        boolean altdown=((me.getModifiersEx() & MouseEvent.ALT_DOWN_MASK)==MouseEvent.ALT_DOWN_MASK);
        boolean shiftdown=((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK);
        
        if (shiftdown){
            Point P=corrPoint(x,y);
            x=P.x;
            y=P.y;
        }
        
        switch (M.status){
            case WAITING_FOR_GLIDE2:
            case WAITING_FOR_SYMMETRY2:
            case WAITING_FOR_ROTATION2:
            case WAITING_FOR_HOMOTHETY2:
                if (altdown){
                    M.addLimboDrawables();
                }
                else {
                    M.replaceSelectedDrawablesWithLimbo();
                }
                showlimbodrawables=false;
                //System.out.println("1 Limbo drawables: \n"+M.limboDrawablesToString());
                //System.out.println("1 Selected drawables: \n"+M.selectedDrawablesToString());
                M.clearLimboDrawables();
                fromPoint=null;
                toPoint=null;
                transformationCenter=null;
                cd.currentDrawable=M.getCurrentDrawable();
                cd.updatestatus(M.oldstatus);
                repaint();
                break;
            case WAITING_FOR_PROPAGATOR:
                if (glassRect!=null){
                    M.unselectDrawables();
                    Drawable[] selD=M.findSelectedDrawables(glassRect, M.utox);
                    M.addSelection(selD);
                    glassRect=null;
                    repaint();
                } 
                break;
            case WAITING_FOR_SELECTION:
                if (glassRect!=null){
                    M.unselectDrawables();
                    Drawable[] selD=M.findSelectedDrawables(glassRect, M.utox);
                    M.addSelection(selD);
                    glassRect=null;
                    repaint();
                } 
                else {
                    if (altdown){
                        M.addLimboDrawables();
                        System.out.println("limbo drawables added");
                        M.clearLimboDrawables();
                        showlimbodrawables=false;
                    }
                }
                break;
            case WAITING_FOR_PARTIAL_SELECTION:
                if (glassRect!=null){
                    if (!shiftdown){
                        M.unpartiallySelectPoints();
                    }
                    M.makePartialSelection(glassRect, M.utox);
                    glassPoint1=null;
                    glassPoint2=null;
                    glassRect=null;
                    repaint();
                }
                break;
            case WAITING_FOR_MERGE:
                if (glassRect!=null){
                    M.makePartialSelection(glassRect, M.utox);
                    glassPoint1=null;
                    glassPoint2=null;
                    glassRect=null;
                    M.mergePartialSelection();
                    M.unpartiallySelectDrawables();
                    cd.updatestatus(M.oldstatus);
                    repaint();
                }
                break;
            case WAITING_FOR_TRANSLATOR:
                fromTranslatePoint=null;
                toTranslatePoint=null;
                cd.updatestatus(M.oldstatus);
                break;
            case WAITING_FOR_BEZIER2:
                cd.updatestatus(WAITING_FOR_BEZIER3);
                break;
            case WAITING_FOR_BEZIER3:
                //((BezierPath) cd.currentDrawable).addtwice(x, y, M.xtou);
                ((BezierPath) cd.currentDrawable).cloneLastPoint();
                cd.updatestatus(WAITING_FOR_BEZIER4);
                break;
            case WAITING_FOR_BEZIER5:
                ((BezierPath) cd.currentDrawable).addSymTangentPoint();
                cd.updatestatus(WAITING_FOR_BEZIER3);
                break;
            case WAITING_FOR_GLASS2:
                Point P1=glassPoint1;
                Point P2=glassPoint2;
                glassRect=enclosingRectangle(P1,P2);
                Rectangle bounds=getBounds();
                float scalew=bounds.width/glassRect.width;
                float scaleh=bounds.height/glassRect.height;
                float scale=Math.min(scalew, scaleh);
                Point C1=center(glassRect);
                Point C2=center(bounds);
                double tx = C2.x-scale*C1.x;
                double ty = C2.y-scale*C1.y;
                AffineTransform T=new AffineTransform(scale,0,0,scale,tx,ty);
                try {
                    T.invert();
                }
                catch (NoninvertibleTransformException  e){
                    System.out.println("T: "+e.getMessage());
                }
                M.xtou.concatenate(T);
                M.utox=new AffineTransform(M.xtou);
                try {
                    M.utox.invert();
                }
                catch (NoninvertibleTransformException  e){
                    System.out.println("M.utox: "+e.getMessage());
                }
                glassRect=null;
                cd.updatestatus(M.oldstatus);
                repaint();
                break;
                
            case WAITING_FOR_DELAUNAY1:
                Circle C=(Circle) cd.currentDrawable;
                M.add(C);
                delaunaySegments=C.delaunay(M.utox);
                cd.updatestatus(WAITING_FOR_DELAUNAY2);
                repaint();
                break;
                
            case WAITING_FOR_VORONOI1:
                C=(Circle) cd.currentDrawable;
                M.add(C);
                voronoiPolygon=C.voronoi(M.utox);
                
                cd.updatestatus(WAITING_FOR_VORONOI2);
                repaint();
                
                /*
                Stack<MPoint> P=C.transformsInRange(M.utox);
                for (int i=0; i<P.size(); i++){
                    Segment S=new Segment(P.elementAt(i),P.elementAt(i),M.subgroup);
                    S.propagateOne();
                    M.add(S);
                }
                voronoiPolygon=C.initialVoronoiPolygone(M.utox);
                voronoiPointSet=P;
                voronoiCtr=0;
                M.setSelectionMode(true);
                M.selectAll();
                */
                break;
            
            case WAITING_FOR_VORONOI2:
                M.add(voronoiPolygon);
                voronoiPolygon=null;
                cd.updatestatus(WAITING_FOR_SELECTION);
                break;
                
            case WAITING_FOR_DELAUNAY2:
                for (int i=0;i<delaunaySegments.length;i++){
                    M.add(delaunaySegments[i]);
                }
                delaunaySegments=null;
                cd.updatestatus(WAITING_FOR_SELECTION);
        }
    }
    
    

    public void mouseDragged(MouseEvent me) {
        System.out.println("MotivePanel: Mouse dragged.");
        boolean altdown=((me.getModifiersEx() & MouseEvent.ALT_DOWN_MASK)==MouseEvent.ALT_DOWN_MASK);
        boolean shiftdown=((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK);
        
        int x=me.getX();
        int y=me.getY();
        
        if (shiftdown){
            Point P=corrPoint(x,y);
            x=P.x;
            y=P.y;
        }
        
        
        switch (M.status){
            case WAITING_FOR_GLIDE2:
                MPoint P=new MPoint(x,y);
                toPoint=new MPoint();
                M.xtou.transform(P,toPoint);
                AffineTransform T=Utils.glideReflection(fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
            case WAITING_FOR_SYMMETRY2:
                P=new MPoint(x,y);
                toPoint=new MPoint();
                M.xtou.transform(P,toPoint);
                T=Utils.symmetry(fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
            case WAITING_FOR_ROTATION2:
                P=new MPoint(x,y);
                toPoint=new MPoint();
                M.xtou.transform(P,toPoint);
                T=Utils.rotation(transformationCenter, fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
            
            case WAITING_FOR_HOMOTHETY2:
                P=new MPoint(x,y);
                toPoint=new MPoint();
                M.xtou.transform(P,toPoint);
                T=Utils.homothety(transformationCenter, fromPoint, toPoint);
                M.transformSelectionBy(T);
                repaint();
                break;
        
        case WAITING_FOR_MERGE:
        case WAITING_FOR_GLASS2:
            glassPoint2=new Point(x,y);
            glassRect=enclosingRectangle(glassPoint1,glassPoint2);
            repaint();
            break;
        case WAITING_FOR_VORONOI2:
            Circle C=(Circle) cd.currentDrawable;
            P=new MPoint(x,y);
            M.xtou.transform(P, P);
            float dx=P.x-C.P1.x;
            float dy=P.y-C.P1.y;
            C.translate(dx, dy);
            voronoiPolygon=C.voronoi(M.utox);
            repaint();
            break;
        case WAITING_FOR_DELAUNAY2:
            C=(Circle) cd.currentDrawable;
            P=new MPoint(x,y);
            M.xtou.transform(P, P);
            dx=P.x-C.P1.x;
            dy=P.y-C.P1.y;
            C.translate(dx, dy);
            delaunaySegments=C.delaunay(M.utox);
            repaint();
            break;
        case WAITING_FOR_PARTIAL_SELECTION:
            if (fromSelectedPoint!=null){
                toSelectedPoint=new MPoint();
                M.xtou.transform(new MPoint(x,y),toSelectedPoint);
                M.movePartialSelection(fromSelectedPoint,toSelectedPoint, altdown);
            } 
            else if (glassPoint1!=null){
                glassPoint2=new Point(x,y);
                glassRect=enclosingRectangle(glassPoint1,glassPoint2);
            } 
            //else {
            //    M.modifyPartialSelection(x,y,M.xtou, altdown);
            //}
            repaint();
            break;
        case WAITING_FOR_SEGMENT:
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_DELAUNAY1:
        case WAITING_FOR_VORONOI1:
        case WAITING_FOR_CIRCLE:
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_POLYGON1:
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_POLYGON2:
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_BEZIER2:
            System.out.println("cd.currentDrawable.selectionmodeon:"+cd.currentDrawable.selectionmodeon);
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_BEZIER3:
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        case WAITING_FOR_BEZIER5:
            ((BezierPath) cd.currentDrawable).modifySymTangentPoint(x,y,M.xtou);
            repaint();
            break;
        case WAITING_FOR_PROPAGATOR:
        case WAITING_FOR_SELECTION:
            if (fromSelectedPoint!=null){
                toSelectedPoint=new MPoint();
                M.xtou.transform(new MPoint(x,y),toSelectedPoint);
                M.moveSelection(fromSelectedPoint,toSelectedPoint);
                fromSelectedPoint=toSelectedPoint;
                showlimbodrawables=altdown;
            } 
            else if (glassPoint1!=null){
                glassPoint2=new Point(x,y);
                glassRect=enclosingRectangle(glassPoint1,glassPoint2);
            }
            repaint();
            break;
        case WAITING_FOR_TRANSLATOR:
            if (fromTranslatePoint!=null){
                toTranslatePoint=new MPoint(x,y);
                M.xtou.transform(toTranslatePoint, toTranslatePoint);
                dx=(float) (toTranslatePoint.getX()-fromTranslatePoint.getX());
                dy=(float) (toTranslatePoint.getY()-fromTranslatePoint.getY());
                //System.out.println("dx, dy: "+dx+", "+dy);
                AffineTransform Tm=AffineTransform.getTranslateInstance(-dx,-dy);
                T=AffineTransform.getTranslateInstance(dx,dy);
                M.xtou.preConcatenate(Tm);
                M.utox.concatenate(T);
                //translateFromPoint=(MPoint) toPoint.clone();
                repaint();
            }
            break;
        /*default:
            //System.out.println("Mouse dragged. Not alt down.");
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        */        /*default:
            //System.out.println("Mouse dragged. Not alt down.");
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        */        /*default:
            //System.out.println("Mouse dragged. Not alt down.");
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        */        /*default:
            //System.out.println("Mouse dragged. Not alt down.");
            if (cd.currentDrawable!=null){cd.currentDrawable.modify(x,y,M.xtou);}
            repaint();
            break;
        */
        }
    }    
    
    public void mouseEntered(MouseEvent me) { }
    
    public void mouseExited(MouseEvent me) { }
    
    
}
