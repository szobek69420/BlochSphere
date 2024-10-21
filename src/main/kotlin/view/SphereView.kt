package main.kotlin.view

import com.interactivemesh.jfx.importer.obj.ObjModelImporter
import javafx.event.EventHandler
import javafx.geometry.Point3D
import javafx.scene.*
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.MeshView
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import main.kotlin.elements.Line3D


class SphereView(var subScene:SubScene) {
    private var sphere: MeshView;
    private var light: LightBase;
    private lateinit var axes:Array<Axis?>;

    private var camera: PerspectiveCamera;
    private var cameraHRot:Double=-145.0;
    private var cameraVRot:Double=-30.0;
    private var cameraDistance:Double=10.0;

    private data class Axis(val line:Line3D, val cone:MeshView)

    init{
        val importer:ObjModelImporter=ObjModelImporter();
        sphere=importSphere(importer);
        importer.close();
        sphere.let{
            it.transforms.clear();
            it.transforms.addAll(Translate(0.0,0.0,0.0));

            val diffuseMap:Image= Image(this.javaClass.getResourceAsStream("/sprites/sphere_diffuse_map.png"));
            val mat:PhongMaterial=PhongMaterial(Color(0.4,0.4,0.4,0.1));
            mat.diffuseMap=diffuseMap;
            mat.specularColor=Color.TRANSPARENT;
            it.material=mat;

            it.blendMode=BlendMode.MULTIPLY;
        }


        camera = PerspectiveCamera(true);
        camera.apply {
            nearClip=1.0;
            farClip=100.0;
            fieldOfView=45.0;
        }
        updateCameraRotation();


        light = AmbientLight();

        var rootGroup:Group;
        if(subScene.root is Group)
        {
            rootGroup=subScene.root as Group;

            rootGroup.children.add(light);
            rootGroup.children.add(sphere);
            initAxes(rootGroup);

            subScene.camera=camera;

            val mouseHandler:SphereViewMouseHandler= SphereViewMouseHandler(this);
            this.subScene.onMousePressed=mouseHandler;
            this.subScene.onMouseReleased=mouseHandler;
            this.subScene.onMouseDragged=mouseHandler;

            val scrollHandler:SphereViewScrollHandler= SphereViewScrollHandler(this);
            this.subScene.onScroll=scrollHandler;
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

    private fun importSphere(importer:ObjModelImporter):MeshView
    {
        importer.clear();
        importer.read(this.javaClass.getResource("/models/sphere_grid.sugus"));
        return importer.import[0];
    }

    private fun initAxes(root:Group)
    {
        //javafx x: x
        //javafx y: -z
        //javafx z: y

        axes=Array<Axis?>(3) { _ -> null };
        val importer:ObjModelImporter= ObjModelImporter();

        //x axis
        importer.clear();
        importer.read(this.javaClass.getResource("/models/cone.sugus"));
        val xAxisLine3D=Line3D(Point3D(-1.1,0.0,0.0),Point3D(1.1,0.0,0.0),0.01);
        xAxisLine3D.phonkMaterial.diffuseColor=Color.RED;
        root.children.add(xAxisLine3D);

        val xAxisCone:MeshView=importer.import[0];
        xAxisCone.transforms.add(Rotate(90.0,Point3D(0.0,0.0,1.0)));
        xAxisCone.transforms.add(Translate(0.0,-1.1,0.0));
        xAxisCone.transforms.add(Scale(0.03,0.03,0.03));
        xAxisCone.material=PhongMaterial(Color.RED);
        root.children.add(xAxisCone);

        axes[0]=Axis(xAxisLine3D,xAxisCone);

        //y axis
        importer.clear();
        importer.read(this.javaClass.getResource("/models/cone.sugus"));
        val yAxisLine3D=Line3D(Point3D(0.0,0.0,-1.1),Point3D(0.0,0.0,1.1),0.01);
        yAxisLine3D.phonkMaterial.diffuseColor=Color(0.0,1.0,0.0,1.0);
        root.children.add(yAxisLine3D);

        val yAxisCone:MeshView=importer.import[0];
        yAxisCone.transforms.add(Rotate(-90.0,Point3D(1.0,0.0,0.0)));
        yAxisCone.transforms.add(Translate(0.0,-1.1,0.0));
        yAxisCone.transforms.add(Scale(0.03,0.03,0.03));
        yAxisCone.material=PhongMaterial(Color(0.0,1.0,0.0,1.0));
        root.children.add(yAxisCone);

        axes[1]=Axis(yAxisLine3D,yAxisCone);

        //z axis
        importer.clear();
        importer.read(this.javaClass.getResource("/models/cone.sugus"));
        val zAxisLine3D=Line3D(Point3D(0.0,-1.1,0.0),Point3D(0.0,1.1,0.0),0.01);
        zAxisLine3D.phonkMaterial.diffuseColor=Color(0.0,0.0,1.0,1.0);
        root.children.add(zAxisLine3D);

        val zAxisCone:MeshView=importer.import[0];
        zAxisCone.transforms.add(Translate(0.0,-1.1,0.0));
        zAxisCone.transforms.add(Scale(0.03,0.03,0.03));
        zAxisCone.material=PhongMaterial(Color(0.0,0.0,1.0,1.0));
        root.children.add(zAxisCone);

        axes[2]=Axis(zAxisLine3D,zAxisCone);

        importer.close();
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

                    view.cameraHRot+=(view.cameraDistance/20.0)*deltaX;
                    if(view.cameraVRot<-180.0)
                        view.cameraVRot+=360.0;
                    else if(view.cameraVRot>180.0)
                        view.cameraVRot-=360.0;
                    view.cameraVRot-=(view.cameraDistance/20.0)*deltaY;
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

    private class SphereViewScrollHandler(val view:SphereView):EventHandler<ScrollEvent>
    {
        override fun handle(event: ScrollEvent?) {
            if(event==null)
                return;

            view.cameraDistance-=0.01*event.deltaY;
            if(view.cameraDistance>10.0)
                view.cameraDistance=10.0;
            if(view.cameraDistance<3.0)
                view.cameraDistance=3.0;

            view.updateCameraRotation();
        }

    }
}