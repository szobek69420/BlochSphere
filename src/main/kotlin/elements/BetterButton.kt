package main.kotlin.elements

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment


class BetterButton(var text:String): AnchorPane() {

    var textColour:Color=Color(0.0,0.0,0.0,1.0);

    var normalColour:Color=Color(1.0,1.0,1.0,1.0);
    var hoverColour:Color=Color(0.8,0.8,0.8,1.0);
    var pressedColour:Color=Color(0.6,0.6,0.6,1.0);

    var textComponent:Label;

    var callback:(()->Unit)?=null;

    init{
        textComponent=Label(text);
        children.add(textComponent);

        textComponent.textFill=textColour;
        textComponent.alignment= Pos.CENTER;
        textComponent.textAlignment=TextAlignment.CENTER;

        changeBackgroundColour(normalColour);

        val handler:BetterButtonMouseHandler= BetterButtonMouseHandler(this);
        onMouseDragged=handler;
        onMousePressed=handler;
        onMouseReleased=handler;
        onMouseEntered=handler;
        onMouseExited=handler;

        layoutBoundsProperty().addListener() { _, _, newValue ->
            textComponent.minWidth=newValue.width;
            textComponent.minHeight=newValue.height;

        }
    }

    fun changeColours(_textColour:Color,_normalColour:Color,_hoverColour:Color,_pressedColour:Color)
    {
        textColour=_textColour;
        textComponent.textFill=textColour;

        normalColour=_normalColour;
        hoverColour=_hoverColour;
        pressedColour=_pressedColour;
        (onMouseDragged as BetterButtonMouseHandler).refreshColours();
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