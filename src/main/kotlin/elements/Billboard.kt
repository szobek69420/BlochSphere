package main.kotlin.elements

import javafx.geometry.Point3D
import javafx.scene.Camera
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Mesh
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2

//it only works if no Rotate transform is applied on the object
open class Billboard(val texturePath:String): MeshView() {

    val myTransforms:ArrayList<Transform> = ArrayList<Transform>()

    init{
        val mesh:TriangleMesh= TriangleMesh()
        mesh.vertexFormat= VertexFormat.POINT_TEXCOORD
        mesh.points.addAll(
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f,
            0.5f,0.5f,0.0f,
            -0.5f,0.5f,0.0f
        );
        mesh.texCoords.addAll(
            1.0f,0.0f,
            0.0f, 0.0f,
            0.0f,1.0f,
            1.0f,1.0f
        )
        mesh.faces.addAll(0,0,1,1,2,2,2,2,3,3,0,0)


        this.mesh=mesh

        val mat:PhongMaterial= PhongMaterial(Color.WHITE)
        mat.diffuseMap= Image(this.javaClass.getResourceAsStream(texturePath))
        this.material=mat
    }

    open fun update(cameraToWorld:Transform)
    {
        transforms.clear()
        transforms.addAll(myTransforms)
        transforms.addAll(myTransforms.filterIsInstance<Rotate>().reversed().map{ r -> Rotate(-r.angle,r.axis)})

        val worldPos=this.localToSceneTransform.transform(Point3D.ZERO)
        val cameraPos=cameraToWorld.transform(Point3D.ZERO)

        val delta=Point3D(cameraPos.x-worldPos.x,cameraPos.y-worldPos.y,cameraPos.z-worldPos.z).normalize()

        val pitch= asin(-delta.y)
        var yaw= atan2(-delta.z,delta.x)+0.5* PI

        transforms.add(Rotate(Math.toDegrees(yaw),Point3D(0.0,1.0,0.0)))
        transforms.add(Rotate(Math.toDegrees(pitch), Point3D(1.0,0.0,0.0)))
    }
}