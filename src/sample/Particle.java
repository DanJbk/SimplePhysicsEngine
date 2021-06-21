package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

import static sample.Main.*;

public class Particle extends Ball{
    /***
     self explanatory.
     bounce <= -2 : ignore all objects
     bounce = -1 : interact with object
     bounce > -1: bounce off objects without interacting
     ***/

    Color color;
    double lifetime;
    double sizediff;
    double originalrad;
    double rad;
    ArrayList<PVector> aforces = new ArrayList<PVector>();

    Particle(double x, double y, double lifetime, double rad,String color) {
        this( x,  y, lifetime, 0,rad,1, 0,color);
    }

    Particle(double x, double y, double lifetime, double sizediff, double rad, int mass,double bounce, String color) {
        super(mass, x, y, 10, rad);
        this.color = Color.valueOf(color);
        this.lifetime = lifetime;
        this.sizediff = sizediff;
        this.id = prlist.size();
        this.originalrad = rad;
        this.rad = rad;
        this.setBounciness(bounce);
        myshape = (new Circle(10,Color.valueOf(color)));
        canvaspane.getChildren().add(myshape);
    }

    public void update(){
        lifetime--;
        forces.addAll(aforces);
        changeSize();
        if (getBounciness() >= -1) {
            collision();
        }
        updatevel();
        updatepose();
        forces.clear();
    }

    @Override
    public void moves(){
        super.moves();
        ((Circle)myshape).setRadius(Zoom*rad);
    }

    public void collision(){
        for (Entity en:enlist){
            boolean A = collisioncheck((Box)en);
            //PVector A2 = cornerCheck((Box)en);


            if(A){
                String str = whichside((Box)en);
                collide((Box)en,str);
                tpout((Box)en,str);
            }


        }
        for (Entity en:plist){
            if(collisioncheck((Box)en)){
                String str = whichside((Box)en);
                collide((Box)en,str);
                tpout((Box)en,str);
            }
        }
    }

    public void changeSize(){
        Circle cl = (Circle) myshape;
        double radius = cl.getRadius();
        cl.setRadius(radius+this.sizediff);
        rad += this.sizediff;
    }

    public void collide(Box e,String str){
        if (getBounciness() != -1 || (e instanceof  Platform)){
           if (str.equals("right") && vel.getX() > 0) {
                vel.setX(vel.getX()*-rad/originalrad*getBounciness());

            } if (str.equals("left") && vel.getX() < 0) {
                vel.setX(vel.getX()*-rad/originalrad*getBounciness());

            } if (str.equals("up") && vel.getY() < 0) {
                vel.setY(vel.getY()*-rad/originalrad*getBounciness());

            }if (str.equals("down") && vel.getY() > 0) {
                vel.setY(vel.getY()*-rad/originalrad*getBounciness());
            }
        } else{
            if ((str.equals("left") && vel.getX() < 0)||(str.equals("right") && vel.getX() > 0)) {
                collidex(e);
            } else if ((str.equals("down") && vel.getY() > 0) || (str.equals("up") && vel.getY() < 0)) {
                collidey(e);
            }
        }
    }

    public String whichside(Box en){
        PVector enBorders = en.getBorders();
        double enleft = en.xy.getX() - (enBorders.getX() / 2);                  //positions of different sides
        double enright = en.xy.getX() + (enBorders.getX() / 2);
        double enup = en.xy.getY() - (enBorders.getY() / 2);
        double endown = en.xy.getY() + (enBorders.getY() / 2);

        boolean totheright = Math.abs(xy.getX() - enleft) < Math.abs(xy.getX() - enright);
        boolean above = Math.abs(xy.getY() - endown) < Math.abs(xy.getY() - enup);

        boolean toside = (Math.abs(xy.getX() - enleft) < Math.abs(xy.getY() - endown) && Math.abs(xy.getX() - enleft) < Math.abs(xy.getY() - enup)) ||
                (Math.abs(xy.getX() - enright) < Math.abs(xy.getY() - endown) && Math.abs(xy.getX() - enright) < Math.abs(xy.getY() - enup));

        if (toside){
            if (totheright){
                return "right";
            }
            return "left";
        }
        if(above){
            return "up";
        }
        return "down";
    }

    public void tpout(Box en, String str){

        if (str.equals("down") && vel.getY() < 0){
            this.xy.setY(en.xy.getY()-en.getBorders().getY()/2);
        } if (str.equals("up") && vel.getY() > 0){
            this.xy.setY(en.xy.getY()+en.getBorders().getY()/2);
        } if (str.equals("right") && vel.getX() > 0){
            this.xy.setX(en.xy.getX()-en.getBorders().getX()/2);
        } if (str.equals("left") && vel.getX() < 0){
            this.xy.setX(en.xy.getX()+en.getBorders().getX()/2);
        }
    }

    @Override
    public boolean collisioncheck(Box e) {
        PVector[] corners = e.getCorners();

        boolean A1 =
                xy.getX() >= corners[0].getX() &&
                xy.getX()  <= corners[1].getX() &&
                xy.getY() >= corners[0].getY() &&
                xy.getY()  <= corners[3].getY();
        return A1;
    }

    public PVector cornerCheck(Box e){
        PVector[] corners = e.getCorners();
        boolean inrange = true;
        if(inrange) {
            if (rad >= calcdistance(xy, corners[0])) {
                return corners[0];
            } else if (rad >= calcdistance(xy, corners[1])) {
                return corners[1];
            } else if (rad >= calcdistance(xy, corners[2])) {
                return corners[2];
            } else if (rad >= calcdistance(xy, corners[3])) {
                return corners[3];
            }
        }
        return null;
    }

    @Override
    public void tpdecide(Entity e) {
    }
}
