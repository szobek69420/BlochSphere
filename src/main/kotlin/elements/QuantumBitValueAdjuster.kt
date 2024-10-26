package main.kotlin.elements

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import main.kotlin.maths.Complex
import main.kotlin.quantum.Qubit



class QuantumBitValueAdjuster(private val startValue:Qubit, val onValueChange:(Qubit)->Unit): Group() {

    companion object{
        val WIDTH=260.0;
        val HEIGHT=90.0;

        private var plusImage: Image?=null;
        private var jImage:Image?=null;
        private var cat0Image:Image?=null;
        private var cat1Image:Image?=null;
    }

    private val background: Rectangle;

    private val real1:TextField;
    private val img1:TextField;
    private val real2:TextField;
    private val img2:TextField;

    private val cat0:ImageView;
    private val cat1:ImageView;
    private val plus1:ImageView;
    private val plus2:ImageView;
    private val j1:ImageView;
    private val j2:ImageView;

    init {
        importImages();

        background= Rectangle(WIDTH, HEIGHT);
        background.fill= Color(0.2,0.2,0.2,1.0);
        background.arcWidth=20.0;
        background.arcHeight=20.0;


        real1= TextField();
        real1.text=startValue.a.rl.toString();
        real1.initStyle(false);
        real1.setOrientation(70.0,30.0,65.0,10.0);
        real1.textProperty().addListener(){_,_,_-> convertUserInput()};

        img1=TextField();
        img1.text=startValue.a.img.toString();
        img1.initStyle(false);
        img1.setOrientation(70.0,30.0,160.0,10.0);
        img1.textProperty().addListener(){_,_,_-> convertUserInput()};

        real2= TextField();
        real2.text=startValue.b.rl.toString();
        real2.initStyle(false);
        real2.setOrientation(70.0,30.0,65.0,50.0);
        real2.textProperty().addListener(){_,_,_-> convertUserInput()};

        img2=TextField();
        img2.text=startValue.b.img.toString();
        img2.initStyle(false);
        img2.setOrientation(70.0,30.0,160.0,50.0);
        img2.textProperty().addListener(){_,_,_-> convertUserInput()};


        cat0= ImageView(cat0Image);
        cat0.fitWidth=50.0;
        cat0.fitHeight=25.0;
        cat0.translateX=10.0;
        cat0.translateY=12.5;

        cat1= ImageView(cat1Image);
        cat1.fitWidth=50.0;
        cat1.fitHeight=25.0;
        cat1.translateX=10.0;
        cat1.translateY=52.5;

        plus1= ImageView(plusImage);
        plus1.fitWidth=25.0;
        plus1.fitHeight=25.0;
        plus1.translateX=135.0;
        plus1.translateY=12.5;

        plus2= ImageView(plusImage);
        plus2.fitWidth=25.0;
        plus2.fitHeight=25.0;
        plus2.translateX=135.0;
        plus2.translateY=52.5;


        j1= ImageView(jImage);
        j1.fitWidth=25.0;
        j1.fitHeight=25.0;
        j1.translateX=230.0;
        j1.translateY=12.5;

        j2= ImageView(jImage);
        j2.fitWidth=25.0;
        j2.fitHeight=25.0;
        j2.translateX=230.0;
        j2.translateY=52.5;

        this.children.addAll(background,real1,img1,real2,img2,cat0,cat1,plus1,plus2,j1,j2);
    }

    private fun importImages()
    {
        if(plusImage==null)
            plusImage=Image(this.javaClass.getResourceAsStream("/sprites/plus.png"));
        if(jImage==null)
            jImage=Image(this.javaClass.getResourceAsStream("/sprites/j.png"));
        if(cat0Image==null)
            cat0Image=Image(this.javaClass.getResourceAsStream("/sprites/cat0.png"));
        if(cat1Image==null)
            cat1Image=Image(this.javaClass.getResourceAsStream("/sprites/cat1.png"));
    }

    private fun TextField.setOrientation(width:Double,height:Double, offsetX:Double, offsetY:Double)
    {
        this.minWidth=width;
        this.maxWidth=width;
        this.minHeight=height;
        this.maxHeight=height;

        this.translateX=offsetX;
        this.translateY=offsetY;
    }

    private fun TextField.initStyle(error:Boolean)
    {
        val r=255;
        val g=if(error) 0 else 209;
        val b=0;

        val textFieldStyle="""
            -fx-font-size: 22px; 
            -fx-padding: 3px;
            -fx-text-alignment:right; 
            -fx-background-color: transparent;
            -fx-border-color: rgb(${r},${g},${b});
            -fx-border-width: 3px;
            -fx-border-radius: 10px;
            -fx-text-fill: white;""".trimIndent()
        this.style=textFieldStyle;
        this.alignment=Pos.CENTER_RIGHT;
    }

    private fun convertUserInput() {
        val value:Qubit=Qubit(Complex(0.0f),Complex(0.0f));

        try{
            value.a.rl=real1.text.toFloat();
            real1.initStyle(false);
        }
        catch(ex:Exception)
        {
            value.a.rl=0.0f;
            real1.initStyle(true);
        }

        try{
            value.a.img=img1.text.toFloat();
            img1.initStyle(false);
        }
        catch(ex:Exception)
        {
            value.a.img=0.0f;
            img1.initStyle(true);
        }

        try{
            value.b.rl=real2.text.toFloat();
            real2.initStyle(false);
        }
        catch(ex:Exception)
        {
            value.b.rl=0.0f;
            real2.initStyle(true);
        }

        try{
            value.b.img=img2.text.toFloat();
            img2.initStyle(false);
        }
        catch(ex:Exception)
        {
            value.b.img=0.0f;
            img2.initStyle(true);
        }

        onValueChange(value);
    }
}