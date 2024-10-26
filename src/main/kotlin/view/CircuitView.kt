package main.kotlin.view

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import main.kotlin.elements.QuantumBitValueAdjuster
import main.kotlin.elements.QuantumGateView
import main.kotlin.elements.QuantumGateViewBackground
import main.kotlin.elements.ResizableAnchorPane
import main.kotlin.maths.Complex
import main.kotlin.maths.Matrix
import main.kotlin.quantum.Qubit

class CircuitView(val circuitContainer:AnchorPane, val normieContainer:FlowPane, val phaseContainer:FlowPane, val overlay:AnchorPane, val onValueChange:(Qubit)->Unit) {
    private val gatesInCircuit:ArrayList<String> = ArrayList<String>();

    private val value:Qubit=Qubit(Complex(1.0f),Complex(0.0f));

    private val valueAdjuster:QuantumBitValueAdjuster= QuantumBitValueAdjuster(value){value->
        this.value.a=value.a;
        this.value.b=value.b;
        calculateValue();
    };

    //drag data
    private var inDrag:Boolean=false;
    private var draggedText:String="TEMP";//the temporary value, that is automatically removed from the circuit on rerender if there is no drag in progress
    private var startIndex:Int=-1;//the original index of the grabbed gate, -1 if it is not from the circuit
    private var targetIndex:Int=-1;
    private var dragStart:Long=0;

    private var selectedComponent:QuantumGateView?=null;//if a gate is selected in the circuit, then it doesn't destroy the grabbed node, so that the mouseevent lives on
    private var draggedGate:QuantumGateView?=null;//the very useless gate that will be shown besides the mouse

    init{
        circuitContainer.layoutBoundsProperty().addListener(){_,oldValue,newValue->
            if(oldValue.height!=newValue.height)
                renderCircuit();
        }

        renderCircuit();

        fillGateRegistry();
    }

    private fun fillGateRegistry()
    {
        for(gate in arrayOf("X","Y","Z","H"))
            normieContainer.children.add(QuantumGateView(gate,-1, Color.YELLOW,{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}));

        for(gate in arrayOf("S","S_ADJ","T","T_ADJ"))
            phaseContainer.children.add(QuantumGateView(gate,-1, Color.YELLOW,{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}));
    }

    fun renderCircuit()
    {
        if(selectedComponent==null)
            circuitContainer.children.clear();
        else
        {
            var i:Int=0;
            while(circuitContainer.children.size>i)
            {
                if(circuitContainer.children[i]===selectedComponent)
                {
                    i++;
                    continue;
                }

                circuitContainer.children.removeAt(i);
            }
        }


        if(inDrag&&targetIndex!=-1)
            gatesInCircuit.add(targetIndex,"TEMP");

        //render wire
        val wire= Rectangle(QuantumBitValueAdjuster.WIDTH+gatesInCircuit.size*(QuantumGateView.SCALE+25.0)+20.0,5.0);
        wire.arcWidth=3.0;
        wire.arcHeight=3.0;
        wire.fill=Color.WHITE;
        AnchorPane.setLeftAnchor(wire,10.0);
        AnchorPane.setBottomAnchor(wire,0.5*(circuitContainer.height-2.5));
        circuitContainer.children.add(wire);

        //re-add value adjuster
        circuitContainer.children.add(valueAdjuster);
        AnchorPane.setLeftAnchor(valueAdjuster,10.0);
        AnchorPane.setBottomAnchor(valueAdjuster,0.5*(circuitContainer.height-QuantumBitValueAdjuster.HEIGHT));


        //add gates
        var offset=QuantumBitValueAdjuster.WIDTH+20.0;
        var index:Int=0;
        for (bill in gatesInCircuit)
        {
            //add background
            val bg:QuantumGateViewBackground= QuantumGateViewBackground(index,circuitContainer.height);
            circuitContainer.children.add(bg);
            AnchorPane.setLeftAnchor(bg,offset);
            AnchorPane.setBottomAnchor(bg,0.0);

            //add gate
            var colour:Color=Color.YELLOW;
            if(bill=="TEMP")
                colour=Color.DEEPSKYBLUE;
            var text=if(bill=="TEMP")"" else bill;

            val gate:QuantumGateView=QuantumGateView(text.substringBefore('|'), index, colour,{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);});
            circuitContainer.children.add(gate);
            AnchorPane.setLeftAnchor(gate,offset+12.5);
            AnchorPane.setBottomAnchor(gate,0.5*(circuitContainer.height-QuantumGateView.SCALE));


            offset+=QuantumGateView.SCALE+25.0;
            index++;
        }


        if(inDrag&&targetIndex!=-1)
            gatesInCircuit.removeAt(targetIndex);
    }

    private fun onQuantumGateGrab(text:String,index:Int,event:MouseEvent)
    {
        when(event.eventType)
        {
            MouseEvent.MOUSE_PRESSED->{
                targetIndex=-1;
                startIndex=index;
                draggedText=text;
                inDrag=true;
                dragStart=System.nanoTime();

                if(startIndex!=-1)
                {
                    gatesInCircuit.removeAt(startIndex);

                    //set dragged object
                    for(c in circuitContainer.children)
                    {
                        if(c is QuantumGateView&&(c as QuantumGateView).index==startIndex)
                        {
                            selectedComponent=c;
                            c.opacity=0.0;
                        }
                    }
                }

                draggedGate= QuantumGateView(draggedText,-1,Color.YELLOW,{ _, _, _->;},{ _, _->;});
                overlay.children.add(draggedGate);

                onQuantumGateDrag(text,event);//to refresh the targetIndex
                renderCircuit();
            }

            MouseEvent.MOUSE_RELEASED->{
                inDrag=false;
                selectedComponent=null;

                if(targetIndex==-1&&System.nanoTime()-dragStart<100000000)
                    targetIndex=gatesInCircuit.size;

                if(targetIndex!=-1)
                {
                    gatesInCircuit.add(targetIndex,draggedText);
                    renderCircuit();
                }

                targetIndex=-1;
                draggedText="TEMP";

                overlay.children.remove(draggedGate);
                draggedGate=null;

                calculateValue();
            }
        }
    }

    private fun onQuantumGateDrag(text:String,event:MouseEvent)
    {
        if(!inDrag)//habar ez valszeg nem fordul elo
            return;

        if(draggedGate!=null)
        {
            draggedGate!!.layoutX = event.sceneX-0.5*QuantumGateView.SCALE;
            draggedGate!!.layoutY=event.sceneY-0.5*QuantumGateView.SCALE;
        }

        val prevTargetIndex=targetIndex;

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
            if(event.sceneX<circuitContainer.layoutBounds.minX
                ||event.sceneX>circuitContainer.layoutBounds.maxX
                ||event.sceneY<circuitContainer.layoutBounds.minY
                ||event.sceneY>circuitContainer.layoutBounds.maxY)//outside of the circuitcontainer
            {
                targetIndex=-1;
            }
            else
                targetIndex=gatesInCircuit.size;
        }


        if(targetIndex!=prevTargetIndex)
            renderCircuit();
    }

    private fun calculateValue()
    {
        var value=this.value.copy();

        for(gate in gatesInCircuit)
        {
            value=createGateMatrix(gate)*value;
        }

        onValueChange(value);
    }

    private fun createGateMatrix(gate:String):Matrix
    {
        val gateType:String=gate.substringBefore('|');
        val mat=Matrix(2);
        when(gateType)
        {
            "X"->{
                mat[1,0].rl=1.0f;
                mat[0,1].rl=1.0f;
            }
            "Y"->{
                mat[1,0].img=1.0f;
                mat[0,1].img=-1.0f;
            }
            "Z"->{
                mat[0,0].rl=1.0f;
                mat[1,1].rl=-1.0f;
            }
            "H"-> {
                mat[0, 0].rl = 0.7071068f;
                mat[0, 1].rl = 0.7071068f;
                mat[1, 0].rl = 0.7071068f;
                mat[1, 1].rl = -0.7071068f;
            }
            "S"->{
                mat[0,0].rl=1.0f;
                mat[1,1].img=1.0f;
            }
            "S_ADJ"->{
                mat[0,0].rl=1.0f;
                mat[1,1].img=-1.0f;
            }
            "T"->{
                mat[0,0].rl = 1.0f;
                mat[1,1].rl = 0.7071068f;
                mat[1,1].img = 0.7071068f;
            }
            "T_ADJ"->{
                mat[0,0].rl = 1.0f;
                mat[1,1].rl = 0.7071068f;
                mat[1,1].img = -0.7071068f;
            }
            else->{
                mat[0,0].rl=1.0f;
                mat[1,1].rl=1.0f;
            }
        }

        return mat;
    }
}