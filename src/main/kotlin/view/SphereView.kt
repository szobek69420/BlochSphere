package main.kotlin.view

import javafx.event.EventHandler
import javafx.geometry.Point3D
import javafx.scene.*
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape3D
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate


class SphereView(var subScene:SubScene) {
    var sphere: Shape3D;
    var light: LightBase;

    var camera: PerspectiveCamera;
    var cameraHRot:Double=0.0;
    var cameraVRot:Double=0.0;
    var cameraDistance:Double=10.0;

    init{
        sphere=Sphere();
        sphere.apply{
            transforms.clear();
            transforms.addAll(Translate(0.0,0.0,0.0));
        }

        camera = PerspectiveCamera(true);
        camera.apply {
            nearClip=1.0;
            farClip=100.0;
            fieldOfView=45.0;
        }
        updateCameraRotation();


        light = DirectionalLight();
        light.apply{
            var rotation: Rotate = Rotate(-30.0, Point3D(1.0,0.0,0.0));
            var rotation2:Rotate=Rotate(-30.0,Point3D(0.0,1.0,0.0));
            var translation: Translate = Translate(0.0,10.0,0.0);
            transforms.clear();
            transforms.addAll(rotation,rotation2,translation);
        }


        var rootGroup:Group;
        if(subScene.root is Group)
        {
            rootGroup=subScene.root as Group;

            rootGroup.children.add(sphere);
            rootGroup.children.add(light);

            subScene.camera=camera;

            val mouseHandler:SphereViewMouseHandler= SphereViewMouseHandler(this);
            this.subScene.onMousePressed=mouseHandler;
            this.subScene.onMouseReleased=mouseHandler;
            this.subScene.onMouseDragged=mouseHandler;
        }
        else
            System.err.println("SphereView: subScene.root must be or derived from Group to produce desirable results");
    }

    private fun updateCameraRotation()
    {
        camera.transforms.clear();

        val translation:Translate= Translate(0.0,0.0,-cameraDistance);
        val rotationVertical:Rotate=Rotate(cameraVRot,Point3D(1.0,0.0,0.0));
        val rotationHorizontal:Rotate=Rotate(cameraHRot,Point3D(0.0,1.0,0.0));

        //vmiert hatulrol elorefele vegzi el a transzformaciokat
        camera.transforms.add(rotationHorizontal);
        camera.transforms.add(rotationVertical);
        camera.transforms.add(translation);
    }


    private class SphereViewMouseHandler(val view:SphereView): EventHandler<MouseEvent>
    {
        var inPress:Boolean=false;

        var prevPosX:Double=0.0;
        var prevPosY:Double=0.0;

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType) {
                MouseEvent.MOUSE_DRAGGED-> {
                    val deltaX=event.screenX-prevPosX;
                    val deltaY=event.screenY-prevPosY;

                    view.cameraHRot+=deltaX;
                    if(view.cameraVRot<-180.0)
                        view.cameraVRot+=360.0;
                    else if(view.cameraVRot>180.0)
                        view.cameraVRot-=360.0;
                    view.cameraVRot-=deltaY;
                    if(view.cameraVRot<-88.0)
                        view.cameraVRot=-88.0;
                    else if(view.cameraVRot>88.0)
                        view.cameraVRot=88.0;

                    view.updateCameraRotation();

                    //after everything
                    prevPosX=event.screenX;
                    prevPosY=event.screenY;
                }
                MouseEvent.MOUSE_PRESSED -> {
                    inPress=true;
                    prevPosX=event.screenX;
                    prevPosY=event.screenY;
                }
                MouseEvent.MOUSE_RELEASED -> inPress=false;
            }
        }
    }
}