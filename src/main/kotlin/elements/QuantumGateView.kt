package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class QuantumGateView(var text:String, val _fill:Color, val isInCircuit:Boolean, val onGrab: (String, Boolean, MouseEvent)->Unit, val onDrag:(String, MouseEvent)->Unit):Rectangle(50.0,50.0) {

    init{
        fill=_fill;

        val mouseHandler:MouseHandler=MouseHandler(this);
        onMousePressed=mouseHandler;
        onMouseReleased=mouseHandler;
        onMouseDragged=mouseHandler;
    }

    class MouseHandler(val view:QuantumGateView):EventHandler<MouseEvent>{
        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType){
                MouseEvent.MOUSE_PRESSED->view.onGrab(view.text,view.isInCircuit,event);
                MouseEvent.MOUSE_RELEASED->view.onGrab(view.text,view.isInCircuit,event);
                MouseEvent.MOUSE_DRAGGED->view.onDrag(view.text,event);
            }
        }

    }
}