package main.kotlin.elements

import javafx.geometry.Point3D
import javafx.scene.Camera
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import kotlin.math.PI
import kotlin.math.atan2

//while the Billboard class implements a billboard, that is always facing in the direction of the camera, the BillboardVertical also ensures that the Billboard always appears vertical on the screen
class BillboardVertical(texturePath:String) : Billboard(texturePath){
    override fun update(cameraToWorld: Transform) {
        super.update(cameraToWorld)

        val worldPos=this.localToSceneTransform.transform(Point3D.ZERO)
        val cameraPos=cameraToWorld.transform(Point3D.ZERO)

        val delta= Point3D(cameraPos.x-worldPos.x,cameraPos.y-worldPos.y,cameraPos.z-worldPos.z).normalize()

        //calculating the camera's local basis
        val temp=cameraToWorld.transform(Point3D(0.0,1.0,0.0))
        val cameraUp=Point3D(temp.x-cameraPos.x,temp.y-cameraPos.y,temp.z-cameraPos.z).normalize()
        val temp2=cameraToWorld.transform(Point3D(1.0,0.0,0.0))
        val cameraRight=Point3D(temp2.x-cameraPos.x,temp2.y-cameraPos.y,temp2.z-cameraPos.z).normalize()

        val temp3=localToSceneTransform.transform(Point3D.ZERO)
        val temp4=localToSceneTransform.transform(Point3D(0.0,1.0,0.0))
        val billboardUp=Point3D(temp4.x-temp3.x,temp4.y-temp3.y,temp4.z-temp3.z).normalize()

        val roll= atan2(cameraRight.dotProduct(billboardUp),-cameraUp.dotProduct(billboardUp))+PI //azert kell a pi mert igy jon ki

        transforms.add(Rotate(Math.toDegrees(roll),Point3D(0.0,0.0,1.0)))
    }
}