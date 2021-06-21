package sample;

import java.util.concurrent.ThreadLocalRandom;

import static sample.Main.*;

public class ParticleEmitter extends Entity{

    PVector randomvector;
    PVector prforces;
    PVector xy;
    double lifetime;
    int pmass;
    double plifetime;
    int spawnnum;
    double sizediff;
    double collision;
    String color;


    ParticleEmitter(double x, double y, double lifetime, String color){
        this(x,y,lifetime, new PVector(0,0), 0, color);
    }

    ParticleEmitter(double x, double y, double lifetime, PVector randomvector, double collision, String color){
        this(x,y,lifetime,randomvector,new PVector(0,0), collision, color);
    }

    ParticleEmitter(double x, double y, double lifetime, PVector randomvector, PVector forces, double collision, String color){
        this( x,  y,  lifetime,100, randomvector, forces, collision,0, color);
    }

    ParticleEmitter(double x, double y, double lifetime, double plifetime, PVector randomvector, PVector forces, double collision,double sizediff, String color){
        this(x,y,lifetime,plifetime,randomvector,forces,collision,1,sizediff,1,color);
    }

    ParticleEmitter(double x, double y, double lifetime, double plifetime, PVector randomvector, PVector forces, double collision,int pmass, double sizediff, int spawnnum, String color){
        super(1,x,y);
        this.xy = new PVector(x,y);
        this.lifetime = lifetime;
        this.plifetime = plifetime;
        this.randomvector = randomvector;
        this.collision = collision;
        this.prforces = forces;
        this.sizediff = sizediff;
        this.color = color;
        this.spawnnum = spawnnum;
        this.pmass = pmass;
    }

    public void update(){
        for (int i = 0; i < spawnnum; i ++) {
            emmit();
        }

        lifetime--;
        updatepose();
    }

    public void emmit(){
        int index = prlist.size();
        int randomNum = ThreadLocalRandom.current().nextInt((int) -randomvector.getX(), (int) randomvector.getX() + 1);
        int randomNum2 = ThreadLocalRandom.current().nextInt((int) -randomvector.getY(), (int) randomvector.getY() + 1);

        prlist.add(new Particle(this.xy.getX(), this.xy.getY(), plifetime, sizediff,10,pmass,collision, color));

        prlist.get(index).vel.setX(randomNum/10.0);
        prlist.get(index).vel.setY(randomNum2/10.0);
        prlist.get(index).aforces.add(prforces);
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
