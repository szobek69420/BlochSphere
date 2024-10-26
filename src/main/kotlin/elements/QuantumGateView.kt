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

        private var xImage: Image?=null;
        private var yImage:Image?=null;
        private var zImage:Image?=null;
        private var hImage:Image?=null;
        private var sImage:Image?=null;
        private var sAdjImage:Image?=null;
        private var tImage:Image?=null;
        private var tAdjImage:Image?=null;
        private var pImage:Image?=null;
    }

    private val image: ImageView;
    private val background:Rectangle;

    init{
        background=Rectangle(SCALE,SCALE);
        background.fill=_fill;
        background.arcWidth=20.0;
        background.arcHeight=20.0;

        image=ImageView();
        image.image=getImageFromGate(text);
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
        when(gate)
        {
            "X"->{
                if(xImage==null)
                    xImage=Image(this.javaClass.getResourceAsStream("/sprites/x_gate.png"));
                return xImage;
            }
            "Y"->{
                if(yImage==null)
                    yImage=Image(this.javaClass.getResourceAsStream("/sprites/y_gate.png"));
                return yImage;
            }
            "Z"->{
                if(zImage==null)
                    zImage=Image(this.javaClass.getResourceAsStream("/sprites/z_gate.png"));
                return zImage;
            }
            "H"->{
                if(hImage==null)
                    hImage=Image(this.javaClass.getResourceAsStream("/sprites/h_gate.png"));
                return hImage;
            }
            "S"->{
                if(sImage==null)
                    sImage=Image(this.javaClass.getResourceAsStream("/sprites/s_gate.png"));
                return sImage;
            }
            "S_ADJ"->{
                if(sAdjImage==null)
                    sAdjImage=Image(this.javaClass.getResourceAsStream("/sprites/s_adj_gate.png"));
                return sAdjImage;
            }
            "T"->{
                if(tImage==null)
                    tImage=Image(this.javaClass.getResourceAsStream("/sprites/t_gate.png"));
                return tImage;
            }
            "T_ADJ"->{
                if(tAdjImage==null)
                    tAdjImage=Image(this.javaClass.getResourceAsStream("/sprites/t_adj_gate.png"));
                return tAdjImage;
            }
            "P"->{
                if(pImage==null)
                    pImage=Image(this.javaClass.getResourceAsStream("/sprites/p_gate.png"));
                return pImage;
            }

            else->{
                return null;
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