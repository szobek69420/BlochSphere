package main.kotlin.elements

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class QuantumGateViewBackground(val index:Int, private val _height:Double):Rectangle(QuantumGateView.SCALE+25.0,_height) {
    init {
        fill= Color.BLUEVIOLET;
    }
}