package main.kotlin.elements

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color


class BetterButton(): AnchorPane() {

    var normalColour:Color=Color(1.0,1.0,1.0,1.0);
    var hoverColour:Color=Color(0.8,0.8,0.8,1.0);
    var pressedColour:Color=Color(0.6,0.6,0.6,1.0);

    var image:ImageView;

    var callback:(()->Unit)?=null;

    init{
        changeBackgroundColour(normalColour);

        val handler:BetterButtonMouseHandler= BetterButtonMouseHandler(this);
        onMouseDragged=handler;
        onMousePressed=handler;
        onMouseReleased=handler;
        onMouseEntered=handler;
        onMouseExited=handler;

        image=ImageView();
        this.children.add(image);
        image.isPreserveRatio=true;

        layoutBoundsProperty().addListener() { _, _, newValue ->
            image.fitWidth=newValue.width;
            image.fitHeight=newValue.height;
        }
    }

    fun changeColours(_normalColour:Color,_hoverColour:Color,_pressedColour:Color)
    {
        normalColour=_normalColour;
        hoverColour=_hoverColour;
        pressedColour=_pressedColour;
        (onMouseDragged as BetterButtonMouseHandler).refreshColours();
    }

    fun changeImage(imagePath:String)
    {
        val sus:Image=Image(this.javaClass.getResourceAsStream(imagePath));
        image.image=sus;
    }

    private fun changeBackgroundColour(colour: Color)
    {
        background=Background(BackgroundFill(colour, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private class BetterButtonMouseHandler(val button:BetterButton): EventHandler<MouseEvent>
    {
        var inPress:Boolean=false;
        var inArea:Boolean=false;

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType) {
                MouseEvent.MOUSE_ENTERED-> inArea=true;
                MouseEvent.MOUSE_EXITED-> inArea=false;
                MouseEvent.MOUSE_PRESSED -> inPress=true;
                MouseEvent.MOUSE_RELEASED -> {
                    inPress=false;
                    if(button.callback!=null)
                        button.callback!!();
                }
            }

            refreshColours();
        }

        fun refreshColours()
        {
            if(inPress)
                button.changeBackgroundColour(button.pressedColour);
            else if(inArea)
                button.changeBackgroundColour(button.hoverColour);
            else
                button.changeBackgroundColour(button.normalColour);
        }
    }
}