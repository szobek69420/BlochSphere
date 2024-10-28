package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

class QuantumGateView(var text:String, val index:Int, val _fill:Color, val onGrab: (String, Int, MouseEvent)->Unit, val onDrag:(String, MouseEvent)->Unit):Group() {

    companion object{
        val SCALE=75.0;

        private val xImage: Image? by lazy{ Image(Companion::class.java.getResourceAsStream("/sprites/x_gate.png")) };
        private val yImage:Image? by lazy{ Image(Companion::class.java.getResourceAsStream("/sprites/y_gate.png")) }
        private val zImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/z_gate.png")) }
        private val hImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/h_gate.png")) }
        private val sImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/s_gate.png")) }
        private val sAdjImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/s_adj_gate.png")) }
        private val tImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/t_gate.png")) }
        private val tAdjImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/t_adj_gate.png")) }
        private val pImage:Image? by lazy { Image(Companion::class.java.getResourceAsStream("/sprites/p_gate.png")) }
    }

    private val image: ImageView;
    private val background:Rectangle;

    init{
        background=Rectangle(SCALE,SCALE);
        background.fill=_fill;
        background.arcWidth=20.0;
        background.arcHeight=20.0;

        image=ImageView();
        image.image=getImageFromGate(text.substringBefore('|'));
        image.fitWidth=60.0;
        image.fitHeight=60.0;
        image.translateX=7.5;
        image.translateY=7.5;

        children.addAll(background,image);

        val mouseHandler:MouseHandler= MouseHandler(this);
        background.onMousePressed=mouseHandler;
        background.onMouseReleased=mouseHandler;
        background.onMouseDragged=mouseHandler;
        image.onMousePressed=mouseHandler;
        image.onMouseReleased=mouseHandler;
        image.onMouseDragged=mouseHandler;
    }

    private fun getImageFromGate(gate:String):Image?
    {
        return when(gate) {
            "X"-> xImage
            "Y"-> yImage
            "Z"-> zImage
            "H"-> hImage
            "S"-> sImage
            "S_ADJ"-> sAdjImage
            "T"-> tImage
            "T_ADJ"-> tAdjImage
            "P"-> pImage

            else -> {
                null
            }
        }
    }


    class MouseHandler(val view:QuantumGateView):EventHandler<MouseEvent>{
        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType){
                MouseEvent.MOUSE_PRESSED->view.onGrab(view.text,view.index,event);
                MouseEvent.MOUSE_RELEASED->view.onGrab(view.text,view.index,event);
                MouseEvent.MOUSE_DRAGGED->view.onDrag(view.text,event);
            }
        }

    }
}