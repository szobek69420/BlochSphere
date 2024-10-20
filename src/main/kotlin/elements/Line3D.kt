package main.kotlin.elements

import javafx.geometry.Point3D
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class Line3D(var start:Point3D,var end:Point3D, var width:Double) : Cylinder() {

    var phonkMaterial: PhongMaterial;

    init{
        phonkMaterial= PhongMaterial();
        phonkMaterial.apply {
            diffuseColor=Color(1.0,1.0,1.0,1.0);
        }
        material=phonkMaterial;

        transforms.clear();
        updatePosition();
    }

    //do not clear transforms
    fun updatePosition()
    {
        var hRot:Double=0.0;
        var vRot:Double=0.0;

        if(abs(start.x-end.x)<0.000001&&abs(start.z-end.z)<0.000001)
        {
            if(start.z>end.z)
                vRot=180.0;
        }
        else
        {
            val hProj:Point3D=Point3D(end.x-start.x,0.0,end.z-start.z);
            hRot=Math.toDegrees(atan2(hProj.x,hProj.z));
            if(hProj.z<0.0)
                hRot+= PI;

            vRot=Math.toDegrees(atan2(end.y-start.y,hProj.magnitude()))-90.0;
        }

        //check for prefix
        do{
            if(transforms.size<7)
                break;

            if(transforms[transforms.size-1] !is Translate||transforms[transforms.size-2]!is Translate)
                break;

            val postfix1:Translate=transforms[transforms.size-2] as Translate;
            if(postfix1.x!=-69420.0||postfix1.y!=-69420.0||postfix1.z!=-69420.0)
                break;
            val postfix2:Translate=transforms[transforms.size-1] as Translate;
            if(postfix2.x!=69420.0||postfix2.y!=69420.0||postfix2.z!=69420.0)
                break;

            transforms.remove(transforms.size-7,transforms.size);
        }while(false);

        val length:Double=Point3D(end.x-start.x,end.y-start.y,end.z-start.z).magnitude();
        transforms.add(Translate(start.x,start.y,start.z));
        transforms.add(Rotate(hRot,Point3D(0.0,1.0,0.0)));
        transforms.add(Rotate(vRot,Point3D(1.0,0.0,0.0)));
        transforms.add(Translate(0.0,-length*0.5,0.0));
        transforms.add(Scale(width,length*0.5,width));//azert length*0.5, mert alapbol 2 hosszu

        //egy postfix, hogy tudjam ellenorizni, hogy kivul nem clearelte vki a transformokat
        transforms.add(Translate(-69420.0,-69420.0,-69420.0));
        transforms.add(Translate(69420.0,69420.0,69420.0));
    }
}