package main.kotlin.view

import com.interactivemesh.jfx.importer.obj.ObjModelImporter
import javafx.event.EventHandler
import javafx.geometry.Point3D
import javafx.scene.*
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.MeshView
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import main.kotlin.elements.Line3D
import main.kotlin.maths.Complex
import main.kotlin.quantum.Qubit
import kotlin.math.atan2


//if you want the view to work fully, the given subscene must be a child of an anchorpane
class SphereView(var subScene:SubScene, var subSceneParent: AnchorPane) {
    private data class Axis(val line:Line3D, val cone:MeshView)

    private class QubitArrow() : Group()
    {
        private var line:Line3D;
        private var cone:Sphere;

        init{
            line=Line3D(Point3D(0.0,0.0,0.0),Point3D(0.0,1.0,0.0),0.01);
            line.apply {
                val mat:PhongMaterial=PhongMaterial(Color.WHITE);
                material=mat;
            }

            cone=Sphere();
            cone.apply {
                transforms.add(Translate(0.0,-1.0,0.0));
                transforms.add(Scale(0.03,0.03,0.03));

                val mat:PhongMaterial=PhongMaterial(Color.WHITE);
                material=mat;
            }

            children.addAll(cone,line);
        }
    }

    private class QubitInfoDisplay(val subScene: SubScene):AnchorPane()
    {
        private val alpha:Label=Label();
        private val beta:Label=Label();
        private val polar:Label=Label();
        private val azimuth:Label=Label();

        init{
            if(subScene.root is Group)
            {
                refreshDisplay(Qubit(Complex(1.0f,0.0f), Complex(0.0f,0.0f)));

                this.children.addAll(alpha,beta, polar, azimuth);

                for(label in arrayOf(alpha,beta,polar,azimuth))
                {
                    label.style="-fx-font-size: 18px;";
                    label.textFill=Color.WHITE;
                }

                refreshDisplay(Qubit(Complex(1.0f), Complex(0.0f)));
                onResize();
            }
        }

        fun refreshDisplay(value:Qubit)
        {
            val polar:Double=2.0*Math.toDegrees(atan2(value.b.length().toDouble(),value.a.length().toDouble()));
            val azimuth:Double=Math.toDegrees(value.b.phase().toDouble()-value.a.phase().toDouble());


            alpha.text=String.format("Alpha: %.2f%s%.2fj",value.a.rl, if (value.a.img>=0.0f) "+" else "", value.a.img);
            beta.text=String.format("Beta:  %.2f%s%.2fj",value.b.rl, if (value.b.img>=0.0f) "+" else "", value.b.img);

            this.polar.text= String.format("Polar:   %.2f°",polar);
            this.azimuth.text=String.format("Azimuth: %.2f°",azimuth);
        }

        fun onResize()
        {
            this.width=subScene.width;
            this.height=subScene.height;

            alpha.translateX=10.0;
            alpha.translateY=this.height-55.0;
            beta.translateX=10.0;
            beta.translateY=this.height-30.0;

            polar.translateX=250.0;
            polar.translateY=this.height-55.0;
            azimuth.translateX=250.0;
            azimuth.translateY=this.height-30.0;
        }
    }

    private var value:Qubit=Qubit(Complex(0.7071f,0.0f),Complex(0.5f,-0.5f));

    private var sphere: MeshView;
    private var arrow:QubitArrow;
    private var light: LightBase;
    private lateinit var axes:Array<Axis?>;
    private var qubitInfo:QubitInfoDisplay;

    private var camera: PerspectiveCamera;
    private var cameraHRot:Double=-145.0;
    private var cameraVRot:Double=-30.0;
    private var cameraDistance:Double=5.0;

    init{
        val importer:ObjModelImporter=ObjModelImporter();
        sphere=importSphere(importer);
        importer.close();
        sphere.let{
            it.transforms.clear();
            it.transforms.addAll(Translate(0.0,0.0,0.0));

            val mat:PhongMaterial=PhongMaterial(Color(0.4,0.4,0.4,0.1));
            mat.specularColor=Color.TRANSPARENT;
            it.material=mat;

            it.blendMode=BlendMode.MULTIPLY;
        }

        arrow= QubitArrow();
        refreshValueView();

        camera = PerspectiveCamera(true);
        camera.apply {
            nearClip=1.0;
            farClip=100.0;
            fieldOfView=45.0;
        }
        updateCameraRotation();


        light = AmbientLight();

        qubitInfo= QubitInfoDisplay(subScene);
        qubitInfo.refreshDisplay(value);

        var rootGroup:Group;
        if(subScene.root is Group)
        {
            rootGroup=subScene.root as Group;

            rootGroup.children.add(light);
            rootGroup.children.add(sphere);
            rootGroup.children.add(arrow);
            initAxes(rootGroup);

            subSceneParent.children.add(qubitInfo);

            subScene.camera=camera;

            onResize();

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

    private fun refreshValueView()
    {
        val polar:Double=2.0*Math.toDegrees(atan2(value.b.length().toDouble(),value.a.length().toDouble()));
        val azimuth:Double=Math.toDegrees(value.b.phase().toDouble()-value.a.phase().toDouble());

        arrow.transforms.clear();
        arrow.transforms.add(Rotate(azimuth,Point3D(0.0,-1.0,0.0)));
        arrow.transforms.add(Rotate(polar,Point3D(0.0,0.0,1.0)));
    }

    fun setValue(value:Qubit)
    {
        this.value=value;
        refreshValueView();
        qubitInfo.refreshDisplay(value);
    }

    fun onResize()
    {
        qubitInfo.onResize();
    }

    private fun importSphere(importer:ObjModelImporter):MeshView
    {
        importer.clear();
        importer.read(this.javaClass.getResource("/models/sphere_grid.exe"));
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
        importer.read(this.javaClass.getResource("/models/cone.exe"));
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
        importer.read(this.javaClass.getResource("/models/cone.exe"));
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
        importer.read(this.javaClass.getResource("/models/cone.exe"));
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