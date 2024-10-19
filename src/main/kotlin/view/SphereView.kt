package main.kotlin.view

import javafx.geometry.Point3D
import javafx.scene.*
import javafx.scene.shape.Shape3D
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate


class SphereView(var subScene:SubScene) {
    var camera: PerspectiveCamera;
    var sphere: Shape3D;
    var light: LightBase;

    init{
        sphere=Sphere();
        sphere.apply{
            transforms.clear();
            transforms.addAll(Translate(0.0,0.0,10.0));
        }

        camera = PerspectiveCamera(true);
        camera.apply {
            nearClip=1.0;
            farClip=100.0;

            fieldOfView=45.0;

            var direction: Point3D = Point3D(sphere.translateX-camera.translateX,sphere.translateY-camera.translateY,sphere.translateZ-camera.translateZ);
            direction.normalize();
            var rotation: Rotate = Rotate(0.0, Point3D(0.0, 1.0, 0.0));
            var translate: Translate = Translate(0.0,0.0,0.0);
            transforms.clear();
            transforms.addAll(rotation,translate);
        }

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
        }
        else
            System.err.println("SphereView: subScene.root must be or derived from Group to produce desirable results");
    }
}