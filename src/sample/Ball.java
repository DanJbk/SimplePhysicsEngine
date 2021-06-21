package sample;

import java.util.ArrayList;

import static sample.Main.calcdistance;

public class Ball extends Entity {
    private double radius;
    Ball(int mass, double x, double y, double radius) {
        this(mass, x, y, radius, 1);
    }
    Ball(int mass, double x, double y, double radius, double bounce) {
        super(mass, x, y, bounce);
        this.radius = radius;
    }

    @Override
    public boolean collisioncheck(Entity e) {
        if(e instanceof Box){
            return collisioncheck((Box)e);
        } else if(e instanceof Ball){
            return collisioncheck((Ball)e);
        }
        return false;
    }

    @Override
    public void tpdecide(Entity e) {

    }

    @Override
    public void separate(Entity en) {

    }

    public boolean collisioncheck(Box e) {

        for(PVector corner : e.getCorners()){
            if(calcdistance(xy, corner) < radius){
                return true;
            }
        }
        return calcdistance(xy, e.xy) < (radius + e.getBorders().getX()) && calcdistance(xy, e.xy) < (radius + e.getBorders().getY());
    }

    public boolean collisioncheck(Ball e){
        return calcdistance(e.xy, xy) < radius || calcdistance(e.xy, xy) < e.radius;
    }
}
