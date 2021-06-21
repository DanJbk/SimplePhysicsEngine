package sample;

import javafx.scene.shape.Shape;

import java.util.ArrayList;
// todo test one interaction per one frame
import static sample.Main.Zoom;


public abstract class Entity {

    protected double mass;
    private double bounciness;              // has to be between [0-1]
    boolean bouncable;

    enum side  {NONE,RIGHT,LEFT,UP,DOWN};
    int blockedx;
    int blockedy;
    int touchingx;
    int touchingy;
    double ruthness;
    Shape myshape;

    ArrayList<Entity> within = new ArrayList<Entity>(); // list of entities inside this entity
    ArrayList<PVector> forces = new ArrayList<PVector>();
    PVector pforces = new PVector(0,0);
    ArrayList<Entity> interacted;
    double friction;
    PVector vel;
    PVector pxy;
    PVector xy;
    int id;


    Entity(int mass,double x,double y){
        this(mass,x,y,0,0,false);
    }


    Entity(int mass,double x,double y,double bounce){
        this(mass,x,y);
        this.bounciness = bounce;
    }

    Entity(int mass,double x,double y,double bounce, double ruthness, boolean bouncable){
        this.mass = mass;

        xy = new PVector(x,y);
        forces = new ArrayList<PVector>();
        blockedy = 0;
        interacted = new ArrayList<>();
        pxy = new PVector(0,0);
        this.bounciness = bounce;
        this.ruthness = ruthness;
        this.bouncable = bouncable;
        this.vel = new PVector(0,0);
    }




    Entity(int mass,double x,double y,double vx, double vy,ArrayList<Entity> entities,double bouncyness){
        this(mass,x,y);
        this.bounciness = bouncyness;
        vel.changeto(new PVector(vx,vy));
    }

    public void setid(int id){
        this.id = id;
    }

    public void update(ArrayList<Entity> entities){

        // check for collision

        for(Entity e : entities){
            if(!interacted.contains(e) && id != e.id) {
                boolean collisioncheck1 = collisioncheck(e);
                boolean collisioncheck2 = e.collisioncheck(this);
                boolean collisioncheck = collisioncheck1 || collisioncheck2;

                if (collisioncheck) {
                    tpdecide(e);
                }
            }
            setInteracted(e);
        }
    }

    public void checkwithin(ArrayList<Entity> entities) {
        for (Entity e : entities) {

            if (!interacted.contains(e) && id != e.id) {
                boolean collisioncheck1 = collisioncheck(e);
                boolean collisioncheck2 = e.collisioncheck(this);
                boolean collisioncheck = collisioncheck1 || collisioncheck2;

                boolean iswithin = iswithin(e);

                if (collisioncheck && !iswithin) {
                    within.add(e);
                    e.within.add(this);
                }
                else if (!collisioncheck && iswithin){
                    within.remove(e);
                    e.within.remove(this);
                }
            }
        }
    }

    public void setInteracted(Entity e){
        interacted.add(e);
        e.interacted.add(this);
    }


    public PVector addAllForces(){
        PVector forcevector = new PVector(0,0);
        for(PVector force : forces){
            forcevector.changeto(forcevector.addvector(force));
        }
        PVector acc = new PVector(forcevector);
        return acc;
    }

    public void updatepose(){

        //updatevel();
        //forces.clear();
        //bku_forces();

        savepxy();        // save previous xy
        move();    // move related shape
    }

    public void updatevel(){
        PVector acc = new PVector(addAllForces().multvector(1/mass));

        if (mass == 0){                                      // prevent NAN speed
            savepxy();        // save previous xy in case of of Akey collision
            move();
            return;
        }
        vel.changeto(vel.addvector(acc));
    }

    public void setBlockedx(int touching) {
        this.blockedx = touching;
    }

    public void setBlockedy(int touching) {
        this.blockedy = touching;
    }

    public void setTouchingx(int touchingx){
        this.touchingx = touchingx;
    }

    public void setTouchingy(int touchingy){
        this.touchingy = touchingy;
    }

    public void collide(Entity obj){
        double massclc = (mass - obj.mass)/(mass + obj.mass);
        double massclcthis = (2*obj.mass)/(mass+obj.mass);
        double massclcobj = (2*mass)/(mass+obj.mass);

        PVector ovel = new PVector(vel);

        if(mass == obj.mass && mass == 0){  // for a rare case
        vel.changeto(obj.vel);
        obj.vel.changeto(ovel);
        return;
        }

        vel.changeto((vel.multvector(massclc)).addvector(obj.vel.multvector(massclcthis)));
        obj.vel.changeto(ovel.multvector(massclcobj).subvector(obj.vel.multvector(massclc)));

    }

    public void collidex(Entity obj){
        double massclc = (mass - obj.mass)/(mass + obj.mass);
        double massclcthis = (2*obj.mass)/(mass+obj.mass);
        double massclcobj = (2*mass)/(mass+obj.mass);

        PVector ovel = new PVector(vel);

        if(mass == obj.mass && mass == 0){  // for a rare case
            vel.changeto(obj.vel);
            obj.vel.changeto(ovel);
            return;
        }

        vel.setX((vel.multvector(massclc)).addvector(obj.vel.multvector(massclcthis)).getX());
        obj.vel.setX(ovel.multvector(massclcobj).subvector(obj.vel.multvector(massclc)).getX());

    }

    public void collidey(Entity obj){
        double massclc = (mass - obj.mass)/(mass + obj.mass);
        double massclcthis = (2*obj.mass)/(mass+obj.mass);
        double massclcobj = (2*mass)/(mass+obj.mass);

        PVector ovel = new PVector(vel);

        if(mass == obj.mass && mass == 0){  // for a rare case
            vel.changeto(obj.vel);
            obj.vel.changeto(ovel);
            return;
        }

        vel.setY((vel.multvector(massclc)).addvector(obj.vel.multvector(massclcthis)).getY());
        obj.vel.setY(ovel.multvector(massclcobj).subvector(obj.vel.multvector(massclc)).getY());

    }

    public void applyFriction(Entity en, String side){

        //genapplyfriction(en,side);  // todo figure out why doesnt work when direction is -x or -y
        gfriction(en,side);
    }

    public void genapplyfriction(Entity en, String direction){

        boolean verticle = direction.equals("up") || direction.equals("down");
        double velx = en.vel.getX() - this.vel.getX();
        double vely = en.vel.getY() - this.vel.getY();
        double fconst = ruthness * en.ruthness;                                         // friction constant is multiplication of the roughness
        double vel_90d = verticle ? velx : vely;                               //relative speed
        double vel_0d = verticle ? vely : velx;                               //relative speed
        double forcedown = (verticle ? en.pforces.getY() : en.pforces.getX()) + vel_0d*vel_0d*en.mass*0.5; //todo check if last addition works out (added the kinetic energy on the downwards direction), also add bounciness effect

//        PVector frictionForce = verticle ? (new PVector(fconst * forcedown * -1 * (vel_90d/Math.abs(vel_90d)),0)) :
//                (new PVector(0,fconst * forcedown * -1 * (vel_90d/Math.abs(vel_90d))));

        double dir_90d = vel_90d > 0 ? 1 : -1;  // previously: vel_90d/Math.abs(vel_90d)
        double frictionX = verticle ? fconst * forcedown * -1 * (dir_90d) : 0;
        double frictionY = verticle ? 0 : fconst * forcedown * -1 * (dir_90d);
        PVector frictionForce = new PVector(frictionX, frictionY);                                                                 // calculate friction strength
        double FrictionValue = verticle? frictionX : frictionY;

        double sumForces_90 = verticle ? en.addAllForces().getX() : en.addAllForces().getY();                                       //
        PVector frictionVector = verticle? new PVector((vel_90d *  -1 * en.mass),0) : new PVector(0,(vel_90d *  -1 * en.mass));

        if(forcedown != 0){
            //PVector dir =  frictionForce;                                         // apply kinetic friction against direction of movement
            double futureVel_90d = ((FrictionValue)/en.mass) + ((sumForces_90)/en.mass) + vel_90d;    // Check  that velocity does not reverse
            if(Math.abs(futureVel_90d) <= Math.abs(vel_90d) && futureVel_90d/vel_90d > 0) {   // if does, make a complete stop
                en.forces.add(frictionForce);
                //forces.add(dir.multvector(-1));
            } else{
                en.forces.add(frictionVector);    // apply force
                //forces.add(new PVector(vel *  1* en.mass,0));
            }
        }
    }

    public void gfriction(Entity en, String direction){ //todo change the check to use relative velocity

        boolean verticle = direction.equals("up") || direction.equals("down");

        double fconst = ruthness * en.ruthness;                                             // friction constant is multiplication of the roughness
        double kconst =  0*(1 - Math.abs(en.getBounciness() - getBounciness()));   // kinetic constant, currently disabled

        double velx = en.vel.getX() - this.vel.getX();                                   // relative velocities
        double vely = en.vel.getY() - this.vel.getY();

        double vel_90d = verticle ? velx : vely;                                            //
        double vel_0d = verticle ? vely : velx;

        double forcedown = (verticle ? en.pforces.getY() : en.pforces.getX()) + vel_0d*vel_0d*en.mass*0.5*kconst;  // forcedown would be the same as the normal force

        double dir_90d = vel_90d > 0 ? 1 : -1;                                                 // current direction of movement
        double frictionX = verticle ? fconst * forcedown * -1 * (dir_90d) : 0;
        double frictionY = verticle ? 0 : fconst * forcedown * -1 * (dir_90d);

        PVector frictionForce = new PVector(frictionX, frictionY);           // calculate friction strength
        double FrictionValue = verticle? frictionX : frictionY;

        double sumForces_90 = verticle ? en.addAllForces().getX() : en.addAllForces().getY();    // this whole thing is to find if the friction is too strong
        double futureVel_90d = ((FrictionValue)/en.mass) + ((sumForces_90)/en.mass) + vel_90d;  //todo make relative
        PVector stopForce = verticle? new PVector((vel_90d *  -1 * en.mass),0) : new PVector(0,(vel_90d *  -1 * en.mass));  // todo make relative

        if(forcedown != 0){
            if(Math.abs(futureVel_90d) <= Math.abs(vel_90d) && Math.abs(futureVel_90d/vel_90d) <  1) {   // if friction too strong , make a complete stop todo make relative
                en.forces.add(frictionForce);   // apply force

            } else{
                en.forces.add(stopForce);    // make a complete stop
                //en.forces.add(frictionForce);    // make a complete stop

            }
        }
    }

    private void savepxy(){
        pxy.changeto(xy);
    }

    public void move(){
        xy.setX(xy.getX()+vel.getX());
        xy.setY(xy.getY()+vel.getY());
    }

    public void moves(){
        myshape.setTranslateX((xy.getX() + Main.Camerax)*Zoom);
        myshape.setTranslateY((xy.getY() + Main.Cameray)*Zoom);
    }

    public void oldcollide(Entity obj){                                                                // calculate new velocities
        PVector mv1 = new PVector(vel.multvector(mass));
        PVector mv2 = new PVector(obj.vel.multvector(obj.mass));
        PVector mvres = new PVector(mv1.addvector(mv2));                    //  calculate moment of system
        PVector masses = new PVector(mass,obj.mass);
        PVector mnormal = new PVector(masses.normalize());                  // calculate masses
        mnormal.changeto(oldbounce(mnormal,mvres,obj));                                      // account for bounciness
        oldsetVelocity(mvres,mnormal,obj);                                                                   // apply movement
    }

    private void oldsetVelocity(PVector mvres, PVector mnormal, Entity obj){

        vel.setX(mvres.getX() * mnormal.getX()/mass);                               //set momentum
        vel.setY(mvres.getY() * mnormal.getX()/mass);

        obj.vel.setX(mvres.getX() * mnormal.getY()/obj.mass);
        obj.vel.setY(mvres.getY() * mnormal.getY()/obj.mass);
    }

    private void settopxy(){
        xy.changeto(pxy);
    }

    public PVector oldbounce(PVector nomal, PVector mvres, Entity obj){

        double bounce = Math.abs(obj.bounciness - bounciness);          // add bounciness to the normal of the momentum transfer
        boolean A0 = xy.getX() > obj.xy.getX() && (mvres.getX()*(bounce + nomal.getX())) <= 0;
        boolean A1 = xy.getX() < obj.xy.getX() && (mvres.getX()*(bounce + nomal.getX())) >= 0;
        if(A0 || A1){
            bounce *= -1;
        }
        return new PVector(bounce + nomal.getX(),nomal.getY() - bounce);
    }

    public boolean iswithin(Entity e){
        return within.contains(e);
    }

    public void resetInteracted(){
        interacted.clear();
    }

    public void setBounciness(double bounciness) {
        this.bounciness = bounciness;
    }

    public double getBounciness() {
        return bounciness;
    }

    public void bku_forces(){
        pforces.changeto(addAllForces());
    }

    public abstract boolean collisioncheck(Entity e);
    public abstract void tpdecide(Entity e);
    public abstract void separate(Entity en);
}
