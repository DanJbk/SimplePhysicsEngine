package sample;

import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import static sample.Main.Zoom;

public class Box extends Entity {
    private PVector borders = new PVector(0, 0);    // width and height
    private PVector[] corners = new PVector[4];                 // keeps track of positions of corners

    Box(int mass, double x, double y, double sizex, double sizey) {
        super(mass, x, y);
        borders.setX(sizex);
        borders.setY(sizey);
        updatecorners();
    }

    Box(int mass, double x, double y, double sizex, double sizey, double bounce,double friction,boolean bouncable) {
        super(mass, x, y, bounce, friction, bouncable);
        borders.setX(sizex);
        borders.setY(sizey);
        updatecorners();
    }

    Box(int mass, double x, double y, double sizex, double sizey, double bounce) {
        super(mass, x, y, bounce);
        borders.setX(sizex);
        borders.setY(sizey);
        updatecorners();
    }

    public PVector getBorders() {
        return borders;
    }

    public void updatecorners() {
        double x = this.xy.getX();
        double y = this.xy.getY();
        double sizex = this.borders.getX();
        double sizey = this.borders.getY();

        corners[0] = new PVector((x - sizex / 2), y - sizey / 2);   // top left (i think)
        corners[1] = new PVector((x + sizex / 2), y - sizey / 2);   //top right (i think)
        corners[2] = new PVector(x + (sizex / 2), y + sizey / 2);   // bottom right (i think)
        corners[3] = new PVector(x - sizex / 2, y + sizey / 2); // bottom left (i think)
    }

    public PVector[] getCorners() {
        return corners;
    }

    @Override
    public void update(ArrayList<Entity> entities) {
        super.update(entities);
        updatecorners();
    }

    @Override
    public boolean collisioncheck(Entity e) {
        if (e instanceof Box) {
            return collisioncheck((Box) e);
        }

        if (e instanceof Ball) {    // unused currently
            return collisioncheck((Ball) e);
        }
        return false;
    }

    public boolean collisioncheck(Box e) {
        PVector[] ecorners = e.getCorners();

        for (PVector ecorner : ecorners) {
            boolean A1 = ecorner.getX() >= corners[0].getX() && ecorner.getX() <= corners[1].getX() &&  // check if corners inside shape
                    ecorner.getY() >= corners[0].getY() && ecorner.getY() <= corners[3].getY();
            if (A1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tpdecide(Entity en) {   // check for collision involving walls
        if (en instanceof Box) {
                separate(en);
                collide(en);
        }
    }

    @Override
    public void separate(Entity en) {
        Box bx = (Box)en;
        String side = whichside(bx);
        Boolean A =  side.equals("up") && vel.getY() < bx.vel.getY();   //side.equals(up)
        Boolean B =  side.equals("down") && vel.getY() > bx.vel.getY();
        Boolean C =  side.equals("right") && vel.getX() > bx.vel.getX();
        Boolean D =  side.equals("left") && vel.getX() < bx.vel.getX();
        double depthright = Math.abs((xy.getX() + getBorders().multvector(0.5).getX()) - (bx.xy.getX() - bx.getBorders().multvector(0.5).getX()));
        double depthleft = Math.abs((bx.xy.getX() + bx.getBorders().multvector(0.5).getX()) - (xy.getX() - getBorders().multvector(0.5).getX()));
        double depthdown = Math.abs((xy.getY() + getBorders().multvector(0.5).getY()) - (bx.xy.getY() - bx.getBorders().multvector(0.5).getY()));
        double depthup = Math.abs((bx.xy.getY() + bx.getBorders().multvector(0.5).getY()) - (xy.getY() - getBorders().multvector(0.5).getY()));
        PVector penetrationfix;
        if (A ||B ||C ||D) {
            switch (side) {
                case "up":
                    penetrationfix = new PVector(this.vel.getY(), bx.vel.getY()).normalize();
                    en.xy.setY(penetrationfix.getY() * -depthup + en.xy.getY());
                    xy.setY(penetrationfix.getX() * -depthup + xy.getY());
                    updatecorners();
                    bx.updatecorners();
                    break;
                case "down":
                    penetrationfix = new PVector(this.vel.getY(), bx.vel.getY()).normalize();
                    en.xy.setY(penetrationfix.getY() * -depthdown + en.xy.getY());
                    xy.setY(penetrationfix.getX() * -depthdown + xy.getY());
                    updatecorners();
                    bx.updatecorners();
                    break;
                case "right":
                    penetrationfix = new PVector(this.vel.getX(), bx.vel.getX()).normalize();
                    en.xy.setX(penetrationfix.getY() * -depthright + en.xy.getX());
                    xy.setX(penetrationfix.getX() * -depthright + xy.getX());
                    updatecorners();
                    bx.updatecorners();
                    break;
                case "left":
                    penetrationfix = new PVector(this.vel.getX(), bx.vel.getX()).normalize();
                    en.xy.setX(penetrationfix.getY() *  -depthleft + en.xy.getX());
                    xy.setX(penetrationfix.getX() * -depthleft + xy.getX());
                    updatecorners();
                    bx.updatecorners();
                    break;
            }
        }
    }

    public void stuckdown(Box en, PVector forcebacky){
        PVector forcev = minimanlvel(en,forcebacky,en.pforces); //todo fit forcev and new vel to relative velocity

        en.xy.setY(getCorners()[0].getY() - (en.getBorders().getY() / 2));
        en.updatecorners();
        en.vel.setY(0);
        en.blockedy = -1;
        en.forces.add(forcev.multvector(-1));
    }

    public void stuckup(Box en, PVector forcebacky){
        en.xy.setY(getCorners()[3].getY() + en.getBorders().getY() / 2);
        en.updatecorners();
        en.blockedy = 1;
        en.forces.add(forcebacky.multvector(-1));
    }
    public void stuckright(Box en, PVector forcebackx){
        en.xy.setX(getCorners()[0].getX() - en.getBorders().getX() / 2);
        en.updatecorners();
        en.blockedx = 1;
        en.forces.add(forcebackx.multvector(-1));
    }
    public void stuckleft(Box en, PVector forcebackx){
        en.xy.setX(getCorners()[1].getX() + en.getBorders().getX() / 2);
        en.updatecorners();
        en.forces.add(forcebackx.multvector(-1));
        en.blockedx = -1;
    }

    public void teleportout2(Box en){

        /** Idea: set touchingxy to 0 at start of every update then test for it within update
         *  the issue is with entities falling into others while their blockedy is incorrectly 0, due to the current object being previous in list
         *  this change would keep the status delay to only one frame
         * **/

        double bump = Math.abs(getBounciness() - en.getBounciness());
        PVector forcebackx = new PVector(en.vel.getX() * en.mass * bump, 0);    // opposing force right/left
        PVector forcebacky = new PVector(0, en.vel.getY() * en.mass * bump + en.pforces.getY());    // opposing force up/down
        String side = whichside(en);

        boolean fromright = side.equals("right") && en.vel.getX() < vel.getX();
        boolean fromleft = side.equals("left") && en.vel.getX() > vel.getX();

        if(blockedy !=0 && (fromright || fromleft)){
            collide(en);
            this.vel.setY(0);
        }

        if (blockedy == -1 && side.equals("up")) {                                        // collision when blocked below
            //stuckdown(en,forcebacky);                                                               //
            //applyFriction(en,side);
            //collide(en);
        } else if (blockedy == 1 && side.equals("down")) {                                  // collision when blocked above
            //stuckup(en,forcebacky);
            //applyFriction(en,side);
        } else if (blockedx == 1 && side.equals("left")) {                                        // collision when blocked to left
            stuckright(en,forcebackx);
            applyFriction(en,side);
        } else if (blockedx == -1 && side.equals("right")) {                                  // collision when blocked right
            stuckleft(en,forcebackx);
            applyFriction(en,side);
        }
    }

    public boolean toside(Box en){  //return whether a box is to the side
        PVector enBorders = en.getBorders();
        double enleft = en.xy.getX() - (enBorders.getX() / 2);                  //positions of different sides
        double enright = en.xy.getX() + (enBorders.getX() / 2);
        double enup = en.xy.getY() - (enBorders.getY() / 2);
        double endown = en.xy.getY() + (enBorders.getY() / 2);

        double right = xy.getX() + (borders.getX() / 2);
        double left = xy.getX() - (borders.getX() / 2);
        double up = xy.getY() - (borders.getY() / 2);
        double down = xy.getY() + (borders.getY() / 2);

        boolean toside = (Math.abs(right - enleft) < Math.abs(up - endown) && Math.abs(right - enleft) < Math.abs(down - enup)) ||
                (Math.abs(left - enright) < Math.abs(up - endown) && Math.abs(left - enright) < Math.abs(down - enup));

        return toside;
    }

    public String whichside(Box en) {                                                        // get position of other box relative to self, works when items are close
        PVector enBorders = en.getBorders();
        double enleft = en.xy.getX() - (enBorders.getX() / 2);                  //positions of different sides
        double enright = en.xy.getX() + (enBorders.getX() / 2);
        double enup = en.xy.getY() - (enBorders.getY() / 2);
        double endown = en.xy.getY() + (enBorders.getY() / 2);

        double right = xy.getX() + (borders.getX() / 2);
        double left = xy.getX() - (borders.getX() / 2);
        double up = xy.getY() - (borders.getY() / 2);
        double down = xy.getY() + (borders.getY() / 2);

        boolean totheright = Math.abs(right - enleft) < Math.abs(left - enright);
        boolean above = Math.abs(up - endown) < Math.abs(down - enup);

        boolean toside = (Math.abs(right - enleft) < Math.abs(up - endown) && Math.abs(right - enleft) < Math.abs(down - enup)) ||
                (Math.abs(left - enright) < Math.abs(up - endown) && Math.abs(left - enright) < Math.abs(down - enup));

        if(toside){
            if(totheright){
                return "right";
            } else {
                return "left";
            }
        } else {
            if(above){
                return "up";
            } else {
                return "down";
            }
        }
    }

    @Override
    public void moves(){
        super.moves();
        ((Rectangle)myshape).setWidth(this.borders.getX()*(Zoom));
        ((Rectangle)myshape).setHeight(this.borders.getY()*(Zoom));
    }

    @Override
    public void collide(Entity obj){    // todo apply friction
        String side = whichside((Box) obj);
        int sign = 1;                                                                                                // sign and touching is to  signal where the object was touched relative to  the other
        if (side.equals("left") || side.equals("right")){
            collidex(obj);
            if (side.equals("left")){
                sign*= -1;
            }
            setTouchingx(sign);
            obj.setTouchingx(sign*-1);

        } else  if (side.equals("up") || side.equals("down")){
            collidey(obj);
            if (side.equals("down")){
                sign *= -1;
            }
            setTouchingy(sign);
            obj.setTouchingy(sign*-1);
        }
    }

    public void apply2wayfriction(Entity en, String side){  // todo complete
        if (side.equals("left") || side.equals("right")){


        } else  if (side.equals("up") || side.equals("down")){
            double forceeffect = Math.abs(en.pforces.getY() -pforces.getY());            //
            double fconst = ruthness * en.ruthness;                                     // friction constant is multiplication of the roughness
            double vel = en.vel.getX() - this.vel.getX();                               //relative speed


            PVector dir = (new PVector(fconst * forceeffect * -1 * (vel/Math.abs(vel)),0));  // apply kinetic friction against direction of movement
            if(Math.abs(dir.getX()/en.mass) <= Math.abs(vel)) {   // if grater then, object make Akey complete stop
                en.forces.add(dir);
                forces.add(dir.multvector(-1));
            } else{
                en.forces.add(new PVector(vel *  -1 * en.mass,0));
                forces.add(new PVector(vel *  1* en.mass,0));
            }
        }
    }

    public void cascadeCollision(Box en) {   // prevent objects from intersecting while on ground, for now only works on downward direction

        ArrayList<Entity> removelater = new ArrayList<Entity>();

        if (en.blockedy < 0) {
            for (Entity enn : en.within) {
                if (!(enn instanceof Platform) && en.whichside((Box) enn).equals("up") && !enn.equals(en)) { // if enn above en and enn is physical object

                    double bump = Math.abs(enn.getBounciness() - en.getBounciness());
                    PVector forcebacky = new PVector(0, enn.vel.getY() * enn.mass * bump +enn.pforces.getY()); // bounce force todo fit to relative velocity

                    if (enn.blockedy == 0) {
                        en.applyFriction(enn, "up");
                        if ((enn.vel.getY() + enn.pforces.getY()/enn.mass > en.vel.getY())){    // if future velocity of enn larger than velocity of en
                            en.stuckdown((Box) enn, forcebacky);
                        }
                    }
                    removelater.add(enn); // for removing from 'within' list
                    en.blockedy = 2;
                    //en.setInteracted(enn);
                }
            }

            for (Entity enn : removelater) { // items no longer intersecting
                enn.within.remove(en);
                en.within.remove(enn);
                if (enn.within.size() > 0) {
                    cascadeCollision((Box) enn); // recursion for the next object
                }
            }
        }
    }

    public PVector minimanlvel(Entity en, PVector forcebacky, PVector pforces){ // if velocity 'up' less than  2, set object y velocity to zero todo change so not true for 'down direction;
        if (Math.abs(en.vel.getY()) < 2){
            return(new PVector(0,pforces.getY()));
        }
        return forcebacky;
    }
}
