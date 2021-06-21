package sample;

public class PVector {
    double[] xy = {0,0};

    PVector(double x, double y){
        this.xy[0] = x;
        this.xy[1] = y;
    }

    PVector(PVector vactor){
        this(vactor.getX(),vactor.getY());
    }

    public void changeto(PVector vector){
        setX(vector.getX());
        setY(vector.getY());
    }

    public double getX() {
        return xy[0];
    }

    public double getY() {
        return xy[1];
    }

    public void setX(double x) {
        this.xy[0] = x;
    }

    public void setY(double y) {
        this.xy[1] = y;
    }

    public double getsize(){
        double x = getX();
        double y = getY();
        return Math.sqrt((x*x)+(y*y));
    }

    public PVector normalize(){
        //double size = Math.abs(x)+Math.abs(y);
        double size = getsize();
        return new PVector(getX()/size,getY()/size);
    }

    public PVector addvector(PVector f){
        return new PVector(this.getX() + f.getX(),this.getY() + f.getY());
    }
    public PVector subvector(PVector f){
        return new PVector(this.getX() - f.getX(),this.getY() - f.getY());
    }

    public PVector multvector(double num){
        return new PVector((getX()*num),(getY()*num));
    }

    public PVector multvector(PVector vec){
        return new PVector((getX()*vec.getX()),(getY()*vec.getY()));
    }

    @Override
    public String toString() {
        return "X[" + this.getX() + "] Y[" + this.getY() + "]";
    }
}
