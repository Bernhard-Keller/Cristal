import java.awt.geom.Point2D;
import java.math.BigDecimal;

public class StraightLine {
    MPoint P1, P2;


public StraightLine(MPoint P1, MPoint P2){
    this.P1=P1.copy();
    this.P2=P2.copy();
}

public StraightLine(Segment S){
    P1=S.P1.copy();
    P2=S.P2.copy();
}

public static float cw(MPoint A, MPoint B, MPoint C){
    float vx=(B.x-A.x);
    float vy=(B.y-A.y);
    float wx=(C.x-A.x);
    float wy=(C.y-A.y);
    float det=vx*wy-vy*wx;
    return det;
}



static StraightLine lineBisector(MPoint A, MPoint B){
    MPoint midPoint=new MPoint((A.x+B.x)/2, (A.y+B.y)/2);
    float vx=(float) (B.getX()-midPoint.getX());
    float vy=(float) (B.getY()-midPoint.getY());
    MPoint perpPoint=new MPoint(midPoint.x-vy, midPoint.y+vx);
    return new StraightLine(midPoint, perpPoint);
}

float intersectionParam(StraightLine L){
    float vx=P2.x-P1.x;
    float vy=P2.y-P1.y;
    float wx=L.P2.x-L.P1.x;
    float wy=L.P2.y-L.P1.y;
    float bx=P1.x-L.P1.x;
    float by=P1.y-L.P1.y;
    float det=(-vx)*wy+vy*wx;
    float t=(1/det)*(wy*bx-wx*by);
    return t;
}

MPoint intersectionPoint(StraightLine L){
    if (isParallel(L)){
        return null;
    }
    float t=intersectionParam(L);
    MPoint I=new MPoint((1-t)*P1.x+t*P2.x, (1-t)*P1.y+t*P2.y);
    return I;
}

float[] orthVector(){
    float vx=P2.x-P1.x;
    float vy=P2.y-P1.y;
    float norm=(float) Math.sqrt(vx*vx+vy*vy);
    vx=vx/norm;
    vy=vy/norm;
    float[] res=new float[2];
    res[0]=-vy;
    res[1]=vx;
    return res;
}

boolean isParallel(StraightLine L){
    float vx=P2.x-P1.x;
    float vy=P2.y-P1.y;
    float wx=L.P2.x-L.P1.x;
    float wy=L.P2.y-L.P1.y;
    float det=(vx*wy-vy*wx);
    return (Math.abs(det)<0.01f);
}

MPoint reflect(MPoint Q){
    float vx=P2.x-P1.x;
    float vy=P2.y-P1.y;
    float norm=(float) Math.sqrt(vx*vx+vy*vy);
    vx=vx/norm;
    vy=vy/norm;
    float wx=Q.x-P1.x;
    float wy=Q.y-P1.y;
    float swx=(-1+2*vx*vx)*wx+(2*vx*vy)*wy;
    float swy=(2*vx*vy)*wx+(-1+2*vy*vy)*wy;
    return new MPoint(P1.x+swx,P1.y+swy);
}


}
