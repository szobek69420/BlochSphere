package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class QuantumGateViewBackground(val index:Int, private val _height:Double, val onMouseTresspass:(Int,Boolean)->Unit):Rectangle(75.0,_height) {
    init {
        fill= Color.BLUEVIOLET;

        val mh:MouseHandler= MouseHandler(this);
        onMouseEntered=mh;
        onMouseExited=mh;
    }

    private class MouseHandler(private val haver:QuantumGateViewBackground):EventHandler<MouseEvent>
    {
        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType)
            {
                MouseEvent.MOUSE_ENTERED->{
                    haver.onMouseTresspass(haver.index,true);
                    event.consume();
                }
                MouseEvent.MOUSE_EXITED->{
                    haver.onMouseTresspass(haver.index,false);
                    event.consume();
                }
            }
        }

    }
}