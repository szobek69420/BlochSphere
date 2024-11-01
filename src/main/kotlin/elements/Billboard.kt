package main.kotlin.elements

import javafx.geometry.Point3D
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Mesh
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import javafx.scene.transform.Transform

//it only works if no Rotate transform is applied on the object
class Billboard(private val texturePath:String): MeshView() {
    init{
        val mesh:TriangleMesh= TriangleMesh();
        mesh.vertexFormat= VertexFormat.POINT_TEXCOORD;
        mesh.points.addAll(
            -0.5f,-0.5f,0.0f, 0.0f,0.0f,
            0.5f,-0.5f,0.0f, 1.0f, 0.0f,
            0.5f,0.5f,0.0f, 1.0f,1.0f,
            -0.5f,0.5f,0.0f,0.0f,1.0f
        );
        mesh.faces.addAll(0,1,2,2,3,0);

        this.mesh=mesh;

        val mat:PhongMaterial= PhongMaterial(Color.WHITE);
        mat.diffuseMap= Image(this.javaClass.getResourceAsStream(texturePath));
        this.material=mat;
    }

    fun update(cameraPos:Point3D)
    {
        var worldPos=Point3D.ZERO;
        worldPos=this.localToSceneTransform.transform(worldPos);

        val delta=Point3D(cameraPos.x-worldPos.x,cameraPos.y-worldPos.y,cameraPos.z-worldPos.z).normalize();

    }
}