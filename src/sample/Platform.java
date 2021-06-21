package sample;

import java.util.ArrayList;

public class Platform extends Box {
    Platform( double x, double y, double sizex, double sizey, double friction) {
        super(0, x, y, sizex, sizey,0,friction, false );
        xy.setX(xy.getX()+vel.getX());
        xy.setY(xy.getY()+vel.getY());
    }

    @Override
    public void update(ArrayList<Entity> entities) {
        for (Entity en:entities){
            if(!interacted.contains(en)){
                if (en instanceof Box && ( collisioncheck(en) || en.collisioncheck(this)) &&
                        ((en.blockedy == 0 && !toside(((Box) en))) || (en.blockedx == 0 && toside(((Box) en))))) {    // the last part check if there has been interaction with a platfrom on relevant axes
                    collidebox((Box) en);
                    interacted.add(en);
                }
            }
        }
        pforces.setX(0);
        pforces.setY(0);
        move();
    }


    public void applyforce(Entity entity, double x, double y){
        entity.forces.add(new PVector(x, y));
    }

    public void collidebox(Box en){ //  handel collision

        String side = whichside(en);

        PVector forcebackx = new PVector( en.vel.getX() * en.mass * (en.getBounciness()) + en.pforces.getX(),0);
        PVector forcebacky = new PVector( 0,this.vel.getY()* en.mass * (en.getBounciness()) + en.pforces.getY()); //todo fit to relative velocity

        applyFriction(en, side);
        if(side.equals("left") && en.vel.getX() > vel.getX()){
            en.vel.setX(0);
            stuckright(en,forcebackx);
        } else if (side.equals("right") && en.vel.getX() < vel.getX()){
            en.vel.setX(0);
            stuckleft(en,forcebackx);
        } else if (side.equals("down")&& (en.vel.getY() + en.pforces.getY()/en.mass < vel.getY())){
            en.vel.setY(0);
            stuckup(en,forcebacky);
        } else if (side.equals("up") && (en.vel.getY() + en.pforces.getY()/en.mass > vel.getY())) {//(en.vel.getY() > vel.getY() || en.addAllForces().getY()/en.mass > this.vel.getY())){
            stuckdown(en, forcebacky);
            cascadeCollision(en);
        }
//        applyFriction(en, side);
    }
}
