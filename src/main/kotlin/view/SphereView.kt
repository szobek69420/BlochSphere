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
import javafx.scene.shape.CullFace
import javafx.scene.shape.MeshView
import javafx.scene.shape.Shape3D
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import main.kotlin.elements.Billboard
import main.kotlin.elements.BillboardVertical
import main.kotlin.elements.Line3D
import main.kotlin.maths.Complex
import main.kotlin.quantum.Qubit
import kotlin.math.abs
import kotlin.math.atan2


//if you want the view to work fully, the given subscene must be a child of an anchorpane
class SphereView(var subScene:SubScene, var subSceneParent: AnchorPane) {
    private data class Axis(val line:Line3D, val cone:MeshView)

    private class QubitArrow() : Group()
    {
        private var line:Line3D
        private var cone:Sphere

        init{
            line=Line3D(Point3D(0.0,0.0,0.0),Point3D(0.0,1.0,0.0),0.015)
            line.apply {
                val mat:PhongMaterial=PhongMaterial(Color.WHITE)
                material=mat
            }

            cone=Sphere()
            cone.apply {
                transforms.add(Translate(0.0,-1.0,0.0))
                transforms.add(Scale(0.03,0.03,0.03))

                val mat:PhongMaterial=PhongMaterial(Color.WHITE)
                material=mat
            }

            children.addAll(cone,line)
        }
    }

    private class QubitInfoDisplay(val subScene: SubScene):AnchorPane()
    {
        private val alpha:Label=Label()
        private val beta:Label=Label()
        private val polar:Label=Label()
        private val azimuth:Label=Label()

        init{
            if(subScene.root is Group)
            {
                this.children.addAll(alpha,beta, polar, azimuth)

                for(label in arrayOf(alpha,beta,polar,azimuth))
                {
                    label.style="-fx-font-size: 18px;"
                    label.textFill=Color.WHITE
                }

                refreshDisplay(Qubit(Complex(1.0), Complex(0.0)))
                onResize()
            }
        }

        fun refreshDisplay(value:Qubit)
        {
            val polar:Double=2.0*Math.toDegrees(atan2(value.b.length().toDouble(),value.a.length().toDouble()))
            val azimuth:Double=Math.toDegrees(value.b.phase().toDouble()-value.a.phase().toDouble())


            alpha.text=String.format("Alpha: %.2f%s%.2fj",value.a.rl, if (value.a.img>=0.0f) "+" else "", value.a.img)
            beta.text=String.format("Beta:  %.2f%s%.2fj",value.b.rl, if (value.b.img>=0.0f) "+" else "", value.b.img)

            this.polar.text= String.format("Polar:   %.2f°",polar)
            this.azimuth.text=String.format("Azimuth: %.2f°",azimuth)
        }

        fun onResize()
        {
            this.translateX=0.0
            this.translateY=subScene.height-60.0

            this.width=500.0
            this.height=60.0

            alpha.translateX=10.0
            alpha.translateY=this.height-55.0
            beta.translateX=10.0
            beta.translateY=this.height-30.0

            polar.translateX=250.0
            polar.translateY=this.height-55.0
            azimuth.translateX=250.0
            azimuth.translateY=this.height-30.0
        }
    }

    private class QubitInvalidValueDisplay():AnchorPane(){
        private val text1:Label
        private val text2:Label

        init{
            text1=Label("Invalid start input")
            text1.style="-fx-font-size: 12px;"
            text1.textFill=Color.RED

            text2=Label("The normalized values are displayed")
            text2.style="-fx-font-size: 12px;"
            text2.textFill=Color.RED

            this.children.addAll(text1,text2)

            onResize()
        }

        fun onResize()
        {
            this.translateX=0.0
            this.translateY=0.0
            this.width=200.0
            this.height=30.0

            text1.translateX=10.0
            text1.translateY=5.0

            text2.translateX=10.0
            text2.translateY=20.0
        }
    }

    private var value:Qubit=Qubit(Complex(1.0,0.0),Complex(0.0,0.0))

    private var sphere: MeshView
    private var sphereFlipped:MeshView
    private var arrow:QubitArrow
    private var light: LightBase
    private val axes:Array<Axis?> = Array<Axis?>(3){_->null}
    private val axisLabels:Array<BillboardVertical?> = Array<BillboardVertical?>(3){ _ ->null }
    private var qubitInfo:QubitInfoDisplay
    private var qubitError:QubitInvalidValueDisplay

    private var camera: PerspectiveCamera
    private var cameraHRot:Double=-145.0
    private var cameraVRot:Double=-30.0
    private var cameraDistance:Double=5.0

    init{
        val importer:ObjModelImporter=ObjModelImporter()
        val importerFlipped:ObjModelImporter=ObjModelImporter()
        val spheres=importSphere(importer,importerFlipped)
        importer.close()
        importerFlipped.close()

        sphere=spheres.first
        sphere.let{
            it.transforms.clear()
            it.transforms.addAll(Translate(0.0,0.0,0.0))

            val mat:PhongMaterial=PhongMaterial(Color(0.4,0.4,0.4,0.1))
            mat.specularColor=Color.TRANSPARENT
            it.material=mat

            it.blendMode=BlendMode.MULTIPLY
        }

        sphereFlipped=spheres.second
        sphereFlipped.let{
            it.transforms.clear()
            it.transforms.addAll(Translate(0.0,0.0,0.0))

            val mat:PhongMaterial=PhongMaterial(Color(0.1,0.1,0.1,0.1))
            mat.specularColor=Color.TRANSPARENT
            it.material=mat

            it.blendMode=BlendMode.MULTIPLY
        }

        arrow= QubitArrow()
        refreshValueView()

        camera = PerspectiveCamera(true)
        camera.apply {
            nearClip=1.0
            farClip=100.0
            fieldOfView=45.0
        }


        light = AmbientLight()

        qubitInfo= QubitInfoDisplay(subScene)
        qubitInfo.refreshDisplay(value)

        qubitError=QubitInvalidValueDisplay()
        qubitError.isVisible=false

        var rootGroup:Group
        if(subScene.root is Group)
        {
            rootGroup=subScene.root as Group

            rootGroup.children.add(light)
            rootGroup.children.add(sphere)
            rootGroup.children.add(sphereFlipped)
            rootGroup.children.add(arrow)
            initAxes(rootGroup)

            subSceneParent.children.add(qubitInfo)
            subSceneParent.children.add(qubitError)

            subScene.camera=camera

            onResize()
            updateCameraRotation()

            val mouseHandler:SphereViewMouseHandler= SphereViewMouseHandler(this)
            this.subScene.onMousePressed=mouseHandler
            this.subScene.onMouseReleased=mouseHandler
            this.subScene.onMouseDragged=mouseHandler

            val scrollHandler:SphereViewScrollHandler= SphereViewScrollHandler(this)
            this.subScene.onScroll=scrollHandler
        }
        else
            System.err.println("SphereView: subScene.root must be or derived from Group to produce desirable results")
    }

    private fun updateCameraRotation()
    {
        camera.transforms.clear()

        val translation:Translate= Translate(0.0,0.0,-cameraDistance)
        val rotationVertical:Rotate=Rotate(cameraVRot,Point3D(1.0,0.0,0.0))
        val rotationHorizontal:Rotate=Rotate(cameraHRot,Point3D(0.0,1.0,0.0))

        //vmiert hatulrol elorefele vegzi el a transzformaciokat
        camera.transforms.add(rotationHorizontal)
        camera.transforms.add(rotationVertical)
        camera.transforms.add(translation)

        //update billboards
        for(label in axisLabels)
            label?.update(camera.localToSceneTransform)
    }

    private fun refreshValueView()
    {
        val polar:Double=2.0*Math.toDegrees(atan2(value.b.length().toDouble(),value.a.length().toDouble()))
        val azimuth:Double=Math.toDegrees(value.b.phase().toDouble()-value.a.phase().toDouble())

        arrow.transforms.clear()
        arrow.transforms.add(Rotate(azimuth,Point3D(0.0,-1.0,0.0)))
        arrow.transforms.add(Rotate(polar,Point3D(0.0,0.0,1.0)))
    }

    fun setValue(value:Qubit)
    {
        //user input validation
        var error=false

        val pa=value.a.length()
        val pb=value.b.length()
        val qubitLength=pa*pa+pb*pb

        if(abs(1.0-qubitLength)>0.001)
        {
            error=true
            if(abs(pa)<0.000001&&abs(pb)<0.000001)
            {
                value.a=Complex(1.0)
                value.b= Complex(0.0)
            }
            else
            {

                value.a /= qubitLength
                value.b /= qubitLength
            }
        }


        this.value=value
        refreshValueView()
        qubitInfo.refreshDisplay(value)

        qubitError.isVisible=error
    }

    fun onResize()
    {
        qubitInfo.onResize()
        qubitError.onResize()
    }

    private fun importSphere(importer:ObjModelImporter,importerFlipped:ObjModelImporter):Pair<MeshView,MeshView>
    {
        importer.clear()
        importer.read(this.javaClass.getResource("/models/sphere_grid.exe"))

        importerFlipped.clear()
        importerFlipped.read(this.javaClass.getResource("/models/sphere_grid_flipped_normals.exe"));

        return Pair(importer.import[0],importerFlipped.import[0])
    }

    private fun initAxes(root:Group)
    {
        //javafx x: x
        //javafx y: -z
        //javafx z: y

        val importer:ObjModelImporter= ObjModelImporter()

        //x axis
        importer.clear()
        importer.read(this.javaClass.getResource("/models/cone.exe"))
        val xAxisLine3D=Line3D(Point3D(-1.1,0.0,0.0),Point3D(1.1,0.0,0.0),0.01)
        xAxisLine3D.phonkMaterial.diffuseColor=Color.RED
        root.children.add(xAxisLine3D)

        val xAxisCone:MeshView=importer.import[0]
        xAxisCone.transforms.add(Rotate(90.0,Point3D(0.0,0.0,1.0)))
        xAxisCone.transforms.add(Translate(0.0,-1.1,0.0))
        xAxisCone.transforms.add(Scale(0.03,0.03,0.03))
        xAxisCone.material=PhongMaterial(Color.RED)
        root.children.add(xAxisCone)

        axes[0]=Axis(xAxisLine3D,xAxisCone)

        //y axis
        importer.clear();
        importer.read(this.javaClass.getResource("/models/cone.exe"))
        val yAxisLine3D=Line3D(Point3D(0.0,0.0,-1.1),Point3D(0.0,0.0,1.1),0.01)
        yAxisLine3D.phonkMaterial.diffuseColor=Color(0.0,1.0,0.0,1.0)
        root.children.add(yAxisLine3D)

        val yAxisCone:MeshView=importer.import[0]
        yAxisCone.transforms.add(Rotate(-90.0,Point3D(1.0,0.0,0.0)))
        yAxisCone.transforms.add(Translate(0.0,-1.1,0.0))
        yAxisCone.transforms.add(Scale(0.03,0.03,0.03))
        yAxisCone.material=PhongMaterial(Color(0.0,1.0,0.0,1.0))
        root.children.add(yAxisCone)

        axes[1]=Axis(yAxisLine3D,yAxisCone)

        //z axis
        importer.clear()
        importer.read(this.javaClass.getResource("/models/cone.exe"))
        val zAxisLine3D=Line3D(Point3D(0.0,-1.1,0.0),Point3D(0.0,1.1,0.0),0.01)
        zAxisLine3D.phonkMaterial.diffuseColor=Color(0.0,0.0,1.0,1.0)
        root.children.add(zAxisLine3D)

        val zAxisCone:MeshView=importer.import[0]
        zAxisCone.transforms.add(Translate(0.0,-1.1,0.0))
        zAxisCone.transforms.add(Scale(0.03,0.03,0.03))
        zAxisCone.material=PhongMaterial(Color(0.0,0.0,1.0,1.0))
        root.children.add(zAxisCone)

        axes[2]=Axis(zAxisLine3D,zAxisCone)

        importer.close()

        //axis labels

        //x axis
        val xLabel=BillboardVertical("/sprites/x_label.png")
        xLabel.myTransforms.add(Translate(1.25,-0.1,0.0))
        xLabel.myTransforms.add(Scale(0.1,0.1,0.1))
        axisLabels[0]=xLabel

        //y axis
        val yLabel=BillboardVertical("/sprites/y_label.png")
        yLabel.myTransforms.add(Translate(0.0, -0.1,1.25))
        yLabel.myTransforms.add(Scale(0.1,0.1,0.1))
        axisLabels[1]=yLabel

        //z axis
        val zLabel=BillboardVertical("/sprites/z_label.png")
        zLabel.myTransforms.add(Translate(0.0,-1.25,0.0))
        zLabel.myTransforms.add(Scale(0.1,0.1,0.1))
        axisLabels[2]=zLabel

        root.children.addAll(axisLabels);
    }


    private class SphereViewMouseHandler(val view:SphereView): EventHandler<MouseEvent>
    {
        var inPress:Boolean=false

        var prevPosX:Double=0.0
        var prevPosY:Double=0.0

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return

            when(event.eventType) {
                MouseEvent.MOUSE_DRAGGED-> {
                    val deltaX=event.screenX-prevPosX
                    val deltaY=event.screenY-prevPosY

                    view.cameraHRot+=(view.cameraDistance/20.0)*deltaX
                    if(view.cameraVRot<-180.0)
                        view.cameraVRot+=360.0
                    else if(view.cameraVRot>180.0)
                        view.cameraVRot-=360.0
                    view.cameraVRot-=(view.cameraDistance/20.0)*deltaY
                    if(view.cameraVRot<-88.0)
                        view.cameraVRot=-88.0
                    else if(view.cameraVRot>88.0)
                        view.cameraVRot=88.0

                    view.updateCameraRotation()

                    //after everything
                    prevPosX=event.screenX
                    prevPosY=event.screenY
                }
                MouseEvent.MOUSE_PRESSED -> {
                    inPress=true
                    prevPosX=event.screenX
                    prevPosY=event.screenY
                }
                MouseEvent.MOUSE_RELEASED -> inPress=false
            }
        }
    }

    private class SphereViewScrollHandler(val view:SphereView):EventHandler<ScrollEvent>
    {
        override fun handle(event: ScrollEvent?) {
            if(event==null)
                return

            view.cameraDistance-=0.01*event.deltaY
            if(view.cameraDistance>10.0)
                view.cameraDistance=10.0
            if(view.cameraDistance<3.0)
                view.cameraDistance=3.0

            view.updateCameraRotation()
        }

    }
}