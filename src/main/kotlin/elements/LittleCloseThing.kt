package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent

class LittleCloseThing(scale:Double, private val gateIndex:Int,private val onClick:(Int)->Unit): ImageView() {
    companion object{
        private var sourceImage: Image?=null;
    }

    init{
        if(sourceImage==null)
            sourceImage=Image(this.javaClass.getResourceAsStream("/sprites/close.png"));

        image= sourceImage;

        fitWidth=scale;
        fitHeight=scale;

        val mouseHandler=MouseHandler(gateIndex,onClick);
        onMousePressed=mouseHandler;
        onMouseReleased=mouseHandler;
    }

    private class MouseHandler(private val gateIndex:Int,private val onClick:(Int)->Unit):EventHandler<MouseEvent>{

        private var pressStart:Long=0;

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType) {
                MouseEvent.MOUSE_PRESSED->pressStart=System.nanoTime();
                MouseEvent.MOUSE_RELEASED-> {
                    if(System.nanoTime()-pressStart<200000000)
                        onClick(gateIndex);
                }
            }
        }

    }
}