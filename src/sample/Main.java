package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;

//todo idea: save  location of every shape on a grid using
// hashmaps containing arrays, check collision with adjacent locations arrays

public class Main extends Application {
    public static ArrayList<Platform> plist = new ArrayList<Platform>();
    public static ArrayList<Entity> enlist = new ArrayList<Entity>();
    public static ArrayList<Particle> prlist = new ArrayList<Particle>();
    public static ArrayList<ParticleEmitter> prelist = new ArrayList<ParticleEmitter>();
    public static ArrayList<ForceEmitter> felist = new ArrayList<ForceEmitter>();
    public static ArrayList<Shape> sprlist = new ArrayList<Shape>();
    public static double Camerax = 0;
    public static double Cameray = 0;
    public static double Zoom  = 1;

    boolean Fkey = false;

    boolean Wkey = false;
    boolean Akey = false;
    boolean Skey = false;
    boolean Dkey = false;

    boolean Ikey = false;
    boolean Okey = false;
    boolean Kkey = false;
    public static StackPane canvaspane;
    Stage currentstage;

    private static int time = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        currentstage = primaryStage;

        enlistadd(new sample.Box(30,-100,-100,100,100,0.1,0.5,false), Color.RED);
        enlistadd(new sample.Box(30,-100,-300,150,100,0.4,0.4,false), Color.BLACK);
        enlistadd(new sample.Box(30,-100,-900,170,90,0.2,0.4,false), Color.BLUE);

        ArrayList<Particle> prremovelater = new ArrayList<Particle>();

        plistadd(new Platform(0,590,2000,500, 1),Color.SALMON);
        plistadd(new Platform(200,460,90,300, 1),Color.DARKSLATEBLUE);
        plistadd(new Platform(280,460,90,2050, 1),Color.DARKKHAKI);

        canvaspane = new StackPane();

        // all of this is actions for when you press a button
        canvaspane.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{
           PVector mousepose = mouseWinPose();
           mousepose.setY(mousepose.getY()-(13*(1/Zoom)));

            ArrayList<Entity> springarr2 =new ArrayList<>();

            enlistadd(new sample.Box(50,mousepose.getX()+50,mousepose.getY()+50,90,60,0.5,0.5,false), Color.GREENYELLOW);
            enlist.get(enlist.size()-1).vel.setY(-5);
            enlist.get(enlist.size()-1).vel.setX(16);
            canvaspane.getChildren().add(enlist.get(enlist.size()-1).myshape);

            PVector rnd = new PVector(60,90);
            PVector force = new PVector(0,-1);
            prelist.add(new ParticleEmitter(mousepose.getX(),mousepose.getY()-1,1, 9900, rnd, force,1,10, -0.006, 30,"LIGHTGREEN"));

            springarr2.add(enlist.get(enlist.size()-2));
            //springarr2.addAll(enlist);
            felist.add(new ForceEmitter(mousepose.getX(),mousepose.getY(),300,1,1,1950,3,springarr2));
            felist.get(felist.size()-1).target = enlist.get(enlist.size()-1);

        });



        currentstage.addEventHandler(KeyEvent.ANY,e ->{
            if (e.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                switch (e.getText()) {
                    case "W":
                    case "w":
                        Wkey = true;
                        break;
                    case "S":
                    case "s":
                        Skey = true;
                        break;
                    case "A":
                    case "a":
                        Akey = true;
                        break;
                    case "D":
                    case "d":
                        Dkey = true;
                        break;
                    case "f":
                    case "F":
                        Fkey = true;
                        break;
                    case "O":
                    case "o":
                        Okey = true;
                        break;
                    case "I":
                    case "i":
                        Ikey = true;
                        break;
                    case "K":
                    case "k":
                        Kkey = true;
                        break;
                }
            } else if (e.getEventType().equals(KeyEvent.KEY_RELEASED)){
                switch (e.getText()) {
                    case "W":
                    case "w":
                        Wkey = false;
                        break;
                    case "S":
                    case "s":
                        Skey = false;
                        break;
                    case "A":
                    case "a":
                        Akey = false;
                        break;
                    case "D":
                    case "d":
                        Dkey = false;
                        break;
                    case "O":
                    case "o":
                        Okey = false;
                        break;
                    case "I":
                    case "i":
                        Ikey = false;
                        break;
                    case "K":
                    case "k":
                        Kkey = false;
                        break;
                    case "f":
                    case "F":
                        Fkey = false;
                        break;
                }
            }
        });

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(canvaspane, 1500, 700));

        for(Entity en: enlist) {
            canvaspane.getChildren().add(en.myshape);
        }

        for(Platform platform: plist) {
            canvaspane.getChildren().add(platform.myshape);
        }

        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16),
                new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent t) {


                        time++;

                        for (Entity en: enlist) {
                            en.forces.clear();
                        }

                        ArrayList<ForceEmitter> feremovelater = new ArrayList<ForceEmitter>();
                        for (ForceEmitter en:felist) {          // additional forces
                            en.update();
                            if (en.lifetime == 0){
                                feremovelater.add(en);
                            }
                        }

                        for (ForceEmitter en:feremovelater) {          // remove forces
                            en.clearindicator();
                            felist.remove(en);
                        }

                        boolean cleanpr = true;
                        for (Particle en : prlist){ // update particles
                            if(en.lifetime != 0){
                                en.update();
                                en.moves();
                                cleanpr = false;
                            } else {
                                en.myshape.setFill(Color.TRANSPARENT);
                                canvaspane.getChildren().remove(en.myshape);
                            }
                        }

                        if(cleanpr && prlist.size() > 0){   // remove particles
                            prlist.clear();
                            sprlist.clear();
                            System.out.println("cleared!" + sprlist.size() + prlist.size());
                        }

                        //--------------------------------- Entity Ground Calculations ----------------------

                        for (Entity en: enlist) {
                            en.forces.add((new PVector(0,0.5*en.mass)));    // gravity
                            //en.forces.add((new PVector(-0.5*en.mass,0)));    // gravity
                            en.bku_forces();
                        }

                        for(Entity en: enlist){                                                            // update positions of corners
                            ((Box)en).updatecorners();
                        }

                        for (Entity en : enlist) {                                                          // check which items are within others
                            en.checkwithin(enlist);
                        }

                        for(Platform en: plist){                                                            // update positions (was at end of pipeline)
                            en.moves();
                        }

                        for(Platform en: plist){                                                            // update positions
                            en.updatepose();
                            en.updatecorners();
                        }

                        for(Platform platform: plist){                                              // check collision with platforms and cascade them
                            platform.update(enlist);
                        }

                        for (Entity en : enlist) {                                                          // check collisions between boxes
                            en.update(enlist);
                        }

                        for(Entity en: enlist){                                                               // update rendered positions
                            en.moves();
                        }

                        for (Entity en : enlist) {                                                          // update current velocity from force
                            en.updatevel();
                            en.updatepose();
                        }

                        for (Entity en: enlist){                                                            // reset walls detection
                            en.setBlockedy(0);
                            en.setBlockedx(0);
                        }

                        for (Entity en: enlist){                                                            // reset collision direction detection
                            en.setTouchingx(0);
                            en.setTouchingy(0);
                        }

                        for(Entity en: enlist){                                                             // reset relationships
                            en.resetInteracted();
                        }

                        //------------------------------------------------------------------------------------------

                        for(ParticleEmitter en: prelist){                                              // emmit
                            if (en.lifetime != 0) {
                                en.update();
                            }
                        }

                        for(Platform en: plist){                                                           // reset relationships
                            en.resetInteracted();
                        }

                        //**** move player and camera ****

                        keyactions();
                    }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void keyactions(){
        if (Wkey) { movetick(0,10); }
        if (Skey) { movetick(0,-10); }
        if (Akey) { movetick(10,0); }
        if (Dkey) { movetick(-10,0); }
        if (Okey) { Zoom += -0.01;}
        if (Ikey) { Zoom += 0.01; }
        if (Kkey) { Zoom = 1; }
        if (Fkey){

            if (plist.get(0).vel.getX() > 0){
                plist.get(0).vel.setX(0);
                plist.get(0).vel.setY(0);
                return;
            }
            plist.get(0).vel.setX(0.5);
            plist.get(0).vel.setY(0.5);
        }
    }

    public void movetick(PVector vector){
        movetick(vector.getX(),vector.getY());
    }

    public void movetick(double num, double num2){
        Camerax += num*(1/Zoom);
        Cameray += num2*(1/Zoom);
    }

    public void plistadd(Platform e, Color color){
        e.setid(plist.size());
        Rectangle rect = new Rectangle(e.getBorders().getX(),e.getBorders().getY(),color);
        e.myshape = rect;
        plist.add(e);
    }

    public void enlistadd(Box e, Color color){
        e.setid(enlist.size());
        Rectangle rect = new Rectangle(e.getBorders().getX(),e.getBorders().getY(),color);
        e.myshape = rect;
        enlist.add(e);
    }

    public static double calcdistance(PVector vac, PVector vec){
        double x = Math.abs(vac.getX() - vec.getX());
        double y = Math.abs(vac.getY() - vec.getY());

        return Math.sqrt(x*x+y*y);
    }
    public PVector mouseWinPose(){
        double x =MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();
        double wx = currentstage.getX() + Camerax*Zoom;
        double wy = currentstage.getY() + Cameray*Zoom;
        double wh = currentstage.getHeight();
        double ww = currentstage.getWidth();

        PVector screenpos = new PVector(x,y);
        PVector windowpos = new PVector(wx,wy);
        PVector windowsize = new PVector(ww,wh);

        PVector mousetowindow = new PVector(screenpos.subvector(windowpos));    // coordinates relative to window
        PVector correctpose = new PVector(mousetowindow.subvector(windowsize.multvector(0.5))); // coordinates relativeto canvas
                                                                                                                                                                                  // (0,0) is at the center of  the window
        return new PVector(correctpose.multvector(1/Zoom));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
