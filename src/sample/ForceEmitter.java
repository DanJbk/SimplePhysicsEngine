package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Arrays;

import static sample.Main.Zoom;

public class ForceEmitter extends Entity{
/***
self explanatory.

 mode 0, apply equal force to everything (sforce)
 mode 1, uses gravitational formula (GMm/r^2), "sforce" = G*M
 mode 2, applies force in direct collation to distance (sforce/r)
 mode 3, spring physics where (min = resting length), (max = amount of energy lost) setting max to 5-10 is recommended)
              sforce: proportional force

 min-max: normally range of effect

 ***/

    ArrayList<Entity> category = new ArrayList<Entity>();
    ArrayList<Line> indicator = new ArrayList<Line>();

    Entity target;
    double sforce;
    int lifetime;
    double min;
    double max;
    int mode;

    ForceEmitter(double x, double y, double min, double max,double sforce, int lifetime, int mode, ArrayList<Entity> catergory) {
        super(1, x, y);
        this.sforce = sforce;
        this.lifetime = lifetime;
        this.min = min;
        this.max = max;
        this.mode = mode;
        this.category.addAll(catergory);
        this.target = this;
        setid(Main.felist.size());
    }

    ForceEmitter(double x, double y,double sforce, int lifetime) {
        this(x,y,0,0,sforce,lifetime,0,new ArrayList(Arrays.asList(Main.enlist,Main.prlist)));
    }

    public void update(){
        lifetime--;
        this.xy.changeto(target.xy);
        applyforce();
        updatepose();
    }

    public void applyforce(){
        for (Entity en : category) {
                if (!en.equals(target)) {
                    switch (mode) {
                        case 0:
                            giveforce(en);
                            //updateforces(en);
                            break;
                        case 1:
                            gravity(en);
                            //updateforces(en);
                            break;
                        case 2:
                            explode(en);
                            //updateforces(en);
                            break;
                        case 3:
                            spring(en);
                            updatesprings(en);
                            break;
                        case 4:
                            rope(en);
                            updatesprings(en);
                            break;
                    }
                }
        }
    }

   /***
    min = resting length
    max = amount of energy lost every frame
    can be unreliable

    ***/
    public void spring(Entity en){   //todo work in progress

                double r = Main.calcdistance(this.xy,en.xy);

                PVector direction = new PVector(en.xy.subvector(this.xy).normalize());
                double speed = en.vel.getsize();
                double force = -sforce*(r-min);
                double sign = force/Math.abs(force);

                PVector X = direction.multvector(force).subvector(en.vel.multvector(max));

                en.forces.add(X);
                target.forces.add(X.multvector(-1));
    }


    public void giveforce(Entity en){
                double r = Main.calcdistance(this.xy,en.xy);
                if (r > min && (r < max || max == 0)) {
                    PVector direction = new PVector(en.xy.subvector(this.xy).normalize());
                    en.forces.add(direction.multvector(sforce));
                }
    }

    public void gravity(Entity en){
                PVector direction = new PVector(en.xy.subvector(this.xy).normalize());
                double r = Main.calcdistance(this.xy,en.xy);
                if (r > min && (r < max || max == 0)) {
                    direction.changeto(direction.multvector(sforce).multvector((1 / (r * r))));
                    en.forces.add(direction);
                }
    }

    public void explode(Entity en){
                PVector direction = new PVector(en.xy.subvector(this.xy).normalize());
                double r = Main.calcdistance(this.xy,en.xy);
                if (r > min && (r < max || max == 0)) {
                    direction.changeto(direction.multvector(sforce*1000).multvector((1 / r)));
                    en.forces.add(direction);
                }
    }

    public void updateforces(Entity en){
                int index = 0;
                    if (category.contains(en)){
                        index = category.indexOf(en);
                    }

                if (index > indicator.size() - 1) {
                    indicator.add(new Line(xy.getX(), xy.getY(), en.xy.getX(), en.xy.getY()));
                    indicator.get(index).setStrokeWidth(10);
                    Main.canvaspane.getChildren().add(indicator.get(indicator.size() - 1));
                }
                if (en.forces.size()>0) {
                    double fx = en.forces.get(en.forces.size() - 1).getX();
                    double fy = en.forces.get(en.forces.size() - 1).getY();

                    indicator.get(index).setStartX(en.xy.getX() + Main.Camerax);
                    indicator.get(index).setStartY(en.xy.getY() + Main.Cameray);

                    indicator.get(index).setEndX(en.xy.getX() + fx + Main.Camerax);
                    indicator.get(index).setEndY(en.xy.getY() + fy + Main.Cameray);

                    indicator.get(index).setTranslateX((en.xy.getX()*2 + fx) / 2 + Main.Camerax);
                    indicator.get(index).setTranslateY((en.xy.getY()*2 + fy) / 2 + Main.Cameray);
                    indicator.get(index).setStroke(Color.BLACK.interpolate(Color.RED,(new PVector(fx,fy).getsize())/en.mass));
                } else{
                    indicator.get(index).setStroke(Color.TRANSPARENT);
                }
    }

    public void updatesprings(Entity en){
        int index = 0;
        boolean a = true;
            if (category.contains(en)){
                index = category.indexOf(en);
            }


                if (index > indicator.size()-1){    // add indicator
                    indicator.add(new Line(xy.getX(),xy.getY(),en.xy.getX(),en.xy.getY()));
                    Main.canvaspane.getChildren().add(indicator.get(indicator.size()-1));
                }

                indicator.get(index).setStartX((xy.getX() + Main.Camerax)*Zoom);
                indicator.get(index).setStartY((xy.getY() + Main.Cameray)*Zoom);

                indicator.get(index).setEndX((en.xy.getX() + Main.Camerax)*Zoom);
                indicator.get(index).setEndY((en.xy.getY() + Main.Cameray)*Zoom);

                indicator.get(index).setTranslateX(((xy.getX() + en.xy.getX())/2 + Main.Camerax)*Zoom);
                indicator.get(index).setTranslateY(((xy.getY() + en.xy.getY())/2 +Main.Cameray)*Zoom);

                double restdist = (Main.calcdistance(xy,en.xy)-min);
                double width = 1/((restdist+min)*0.001);

                if (width < 0){ width = 60; }
                if (width > 60){ width = 60; }

                indicator.get(index).setStrokeWidth(width*Zoom);

                indicator.get(index).setStroke(Color.BLACK.interpolate(Color.RED,(-restdist/min)+0.5));
                if (restdist > 0){indicator.get(index).setStroke(Color.LAWNGREEN.interpolate(Color.BLACK,((1-(restdist/min))-0.3)));}

    }

    public void rope(Entity en){
                double r = Main.calcdistance(this.xy,en.xy);

                PVector direction = new PVector(en.xy.subvector(this.xy).normalize());
                double speed = en.vel.getsize();
                double force = -sforce*(r-min);
                double sign = force/Math.abs(force);

                PVector aforce = direction.multvector(force).subvector(en.vel.multvector(max));

                if (r<min){
                    aforce.changeto(aforce.multvector(0));
                }

                en.forces.add(aforce);
                target.forces.add(aforce.multvector(-1));
    }

    public void clearindicator(){
        for (Line ln: indicator){
            ln.setStroke(Color.TRANSPARENT);
            //indicator.clear();
        }
    }



    @Override
    public boolean collisioncheck(Entity e) {
        return false;
    }

    @Override
    public void tpdecide(Entity e) {

    }

    @Override
    public void separate(Entity en) {

    }
}
