package main.kotlin.elements

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class QuantumGateView(var text:String):Rectangle(50.0,50.0) {
    init{
        fill= Color.YELLOW;
    }
}