package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

class QuantumGateView(var text:String, val index:Int, val _fill:Color, val onGrab: (String, Int, MouseEvent)->Unit, val onDrag:(String, MouseEvent)->Unit):Group() {

    companion object{
        val SCALE=75.0;
    }

    private val label:Text;
    private val background:Rectangle;

    init{
        background=Rectangle(SCALE,SCALE);
        background.fill=_fill;

        label=Text(text);
        label.textAlignment= TextAlignment.CENTER;
        label.translateX=0.5*(QuantumGateView.SCALE-label.layoutBoundsProperty().get().width);
        label.translateY=26.0;

        children.addAll(background,label);


        val mouseHandler:MouseHandler= MouseHandler(this);
        background.onMousePressed=mouseHandler;
        background.onMouseReleased=mouseHandler;
        background.onMouseDragged=mouseHandler;
        label.onMousePressed=mouseHandler;
        label.onMouseReleased=mouseHandler;
        label.onMouseDragged=mouseHandler;
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