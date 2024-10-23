package main.kotlin.view

import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import main.kotlin.elements.QuantumGateView
import main.kotlin.elements.QuantumGateViewBackground
import main.kotlin.elements.ResizableAnchorPane

class CircuitView(val circuitContainer:AnchorPane, val operationContainer:FlowPane, val overlay:AnchorPane) {
    private val gatesInCircuit:ArrayList<String> = ArrayList<String>();

    init{
        gatesInCircuit.add("sus");
        gatesInCircuit.add("amogus");

        circuitContainer.layoutBoundsProperty().addListener(){_,oldValue,newValue->
            if(oldValue.height!=newValue.height)
                renderCircuit();
        }

        renderCircuit();
    }

    fun renderCircuit()
    {
        circuitContainer.children.clear();

        var offset=10.0;
        var index:Int=0;
        for (bill in gatesInCircuit)
        {
            //add background
            val bg:QuantumGateViewBackground= QuantumGateViewBackground(index,circuitContainer.height){i,selected->onGateSlotSelected(i,selected);}
            circuitContainer.children.add(bg);
            AnchorPane.setLeftAnchor(bg,offset);
            AnchorPane.setBottomAnchor(bg,0.0);

            //add gate
            val gate:QuantumGateView=QuantumGateView(bill);
            circuitContainer.children.add(gate);
            AnchorPane.setLeftAnchor(gate,offset+12.5);
            AnchorPane.setBottomAnchor(gate,0.5*(circuitContainer.height-gate.height));

            offset+=75.0;
            index++;
        }
    }

    //if the user hovers a moved gate over a gate slot
    fun onGateSlotSelected(index:Int,isSelected:Boolean)
    {
        println(index.toString()+" "+isSelected.toString());
    }
}