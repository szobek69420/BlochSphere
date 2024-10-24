package main.kotlin.view

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import main.kotlin.elements.QuantumGateView
import main.kotlin.elements.QuantumGateViewBackground
import main.kotlin.elements.ResizableAnchorPane

class CircuitView(val circuitContainer:AnchorPane, val operationContainer:FlowPane, val overlay:AnchorPane) {
    private val gatesInCircuit:ArrayList<String> = ArrayList<String>();

    //drag data
    private var inDrag:Boolean=false;
    private var draggedText:String="TEMP";//the temporary value, that is automatically removed from the circuit on rerender if there is no drag in progress
    private var fromCircuit:Boolean=false;
    private var targetIndex:Int=-1;

    private var asdf:QuantumGateViewBackground?=null;

    init{
        gatesInCircuit.add("sus");
        gatesInCircuit.add("amogus");

        circuitContainer.layoutBoundsProperty().addListener(){_,oldValue,newValue->
            if(oldValue.height!=newValue.height)
                renderCircuit();
        }

        renderCircuit();

        fillGateRegistry();
    }

    private fun fillGateRegistry()
    {
        val gates=arrayOf("X","Y","Z","H");

        for(gate in gates)
        {
            operationContainer.children.add(QuantumGateView(gate,Color.YELLOW,false,{t,c,e->onQuantumGateGrab(t,c,e);}, {t,e->onQuantumGateDrag(t,e);}));
        }
    }

    fun renderCircuit()
    {
        circuitContainer.children.clear();

        var offset=10.0;
        var index:Int=0;
        for (bill in gatesInCircuit)
        {
            //add background
            val bg:QuantumGateViewBackground= QuantumGateViewBackground(index,circuitContainer.height);
            circuitContainer.children.add(bg);
            AnchorPane.setLeftAnchor(bg,offset);
            AnchorPane.setBottomAnchor(bg,0.0);

            //add gate
            val gate:QuantumGateView=QuantumGateView(bill, Color.YELLOW,true,{t,c,e->onQuantumGateGrab(t,c,e);}, {t,e->onQuantumGateDrag(t,e);});
            circuitContainer.children.add(gate);
            AnchorPane.setLeftAnchor(gate,offset+12.5);
            AnchorPane.setBottomAnchor(gate,0.5*(circuitContainer.height-gate.height));

            offset+=75.0;
            index++;
        }

        asdf=circuitContainer.children[0] as QuantumGateViewBackground;
    }

    private fun onQuantumGateGrab(text:String,inCircuit:Boolean,event:MouseEvent)
    {
        if(fromCircuit==true)
            return;

        when(event.eventType)
        {
            MouseEvent.MOUSE_PRESSED->{
                targetIndex=-1;
                fromCircuit=inCircuit;
                draggedText=text;
                inDrag=true;
            }

            MouseEvent.MOUSE_RELEASED->{
                if(!fromCircuit)
                {
                    if(targetIndex!=-1)
                    {
                        gatesInCircuit.add(targetIndex,draggedText);
                        renderCircuit();
                    }
                }

                targetIndex=-1;
                draggedText="TEMP";
                inDrag=false;
            }
        }
    }

    private fun onQuantumGateDrag(text:String,event:MouseEvent)
    {
        if(!inDrag)//habar ez valszeg nem fordul elo
            return;

        targetIndex=-1;
        for(node: Node in circuitContainer.children)
        {
            if(node !is QuantumGateViewBackground)
                continue;

            val qgvb:QuantumGateViewBackground=node as QuantumGateViewBackground;
            val lower=qgvb.localToScene(Point2D(0.0,0.0));
            val upper=qgvb.localToScene(Point2D(qgvb.width,qgvb.height));

            if(event.sceneX>lower.x&&event.sceneX<upper.x&&event.sceneY>lower.y&&event.sceneY<upper.y)
                targetIndex=qgvb.index;
        }

        if(targetIndex==-1)
        {
            if(event.sceneX>circuitContainer.layoutBounds.minX
                &&event.sceneX<circuitContainer.layoutBounds.maxX
                &&event.sceneY>circuitContainer.layoutBounds.minY
                &&event.sceneY<circuitContainer.layoutBounds.maxY)
            {
                targetIndex=gatesInCircuit.size;
            }
        }
    }
}