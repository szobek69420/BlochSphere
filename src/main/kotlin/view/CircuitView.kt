package main.kotlin.view

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import main.kotlin.elements.*
import main.kotlin.maths.Complex
import main.kotlin.maths.Matrix
import main.kotlin.quantum.Qubit
import kotlin.math.cos
import kotlin.math.sin

class CircuitView(val circuitContainer: Pane, val normieContainer:FlowPane, val phaseContainer:FlowPane, val customPhaseContainer:FlowPane, val overlay:AnchorPane, val onValueChange:(Qubit)->Unit) {
    private val gatesInCircuit:ArrayList<String> = ArrayList<String>()

    private val value:Qubit=Qubit(Complex(1.0),Complex(0.0));

    private val valueAdjuster:QuantumBitValueAdjuster= QuantumBitValueAdjuster(value){value->
        this.value.a=value.a
        this.value.b=value.b
        calculateValue()
    };

    private val customPhaseText: Text
    private val customPhaseSlider:Slider

    //drag data
    private var inDrag:Boolean=false
    private var draggedText:String="TEMP"//the temporary value, that is automatically removed from the circuit on rerender if there is no drag in progress
    private var startIndex:Int=-1//the original index of the grabbed gate, -1 if it is not from the circuit
    private var targetIndex:Int=-1
    private var dragStart:Long=0

    private var selectedComponent:QuantumGateView?=null//if a gate is selected in the circuit, then it doesn't destroy the grabbed node, so that the mouseevent lives on
    private var draggedGate:QuantumGateView?=null//the very useless gate that will be shown besides the mouse

    private var selectedIndex:Int=-1


    //the width and height of the parent scrollpane, as these values don't cascade
    var circuitContainerWidth:Double=0.0
    var circuitContainerHeight:Double=0.0

    init{
        renderCircuit()

        fillGateRegistry()

        customPhaseText=customPhaseContainer.parent.childrenUnmodifiable[1] as Text

        customPhaseSlider=customPhaseContainer.parent.childrenUnmodifiable[2] as Slider
        customPhaseSlider.adjustValue(69.0)
        customPhaseSlider.valueProperty().addListener(){ _, oldValue, newValue ->
            if(oldValue!=newValue)
            {
                customPhaseContainer.children.clear();
                customPhaseContainer.children.add(QuantumGateView("P|${customPhaseSlider.value}",-1, Color(0.96,0.59,0.11,1.0),{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}));
                customPhaseText.text="Phase angle: ${String.format("%.2f",customPhaseSlider.value)}°"
            }
        }
        customPhaseSlider.adjustValue(0.0)
    }

    private fun fillGateRegistry()
    {
        for(gate in arrayOf("X","Y","Z","H"))
            normieContainer.children.add(QuantumGateView(gate,-1, Color(0.96,0.59,0.11,1.0),{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}))

        for(gate in arrayOf("S","S_ADJ","T","T_ADJ"))
            phaseContainer.children.add(QuantumGateView(gate,-1, Color(0.96,0.59,0.11,1.0),{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}))

        customPhaseContainer.children.add(QuantumGateView("P|0",-1, Color(0.96,0.59,0.11,1.0),{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);}))
    }

    fun renderCircuit()
    {
        if(selectedComponent==null)
            circuitContainer.children.clear()
        else
        {
            var i:Int=0
            while(circuitContainer.children.size>i)
            {
                if(circuitContainer.children[i]===selectedComponent)
                {
                    i++
                    continue
                }

                circuitContainer.children.removeAt(i)
            }
        }

        val gatesCopied:ArrayList<String> = ArrayList(gatesInCircuit)
        if(inDrag&&targetIndex!=-1)
            gatesCopied.add(targetIndex,"TEMP")

        //render wire
        val wire= Rectangle(QuantumBitValueAdjuster.WIDTH+gatesInCircuit.size*(QuantumGateView.SCALE+25.0)+25.0,5.0)
        wire.arcWidth=3.0
        wire.arcHeight=3.0
        wire.fill=Color.WHITE
        wire.translateX=10.0
        wire.translateY=0.5*(circuitContainerHeight-2.5)
        circuitContainer.children.add(wire)

        //re-add value adjuster
        circuitContainer.children.add(valueAdjuster)
        valueAdjuster.translateX=10.0
        valueAdjuster.translateY=0.5*(circuitContainerHeight-QuantumBitValueAdjuster.HEIGHT)


        //add gates
        var offset=QuantumBitValueAdjuster.WIDTH+20.0
        var index:Int=0
        for (bill in gatesCopied)
        {
            //add background
            val bg:QuantumGateViewBackground= QuantumGateViewBackground(index,circuitContainerHeight)
            circuitContainer.children.add(bg)
            bg.translateX=offset
            bg.translateY=0.0

            //add gate
            var colour:Color=if(index==selectedIndex) Color(1.0,0.416,0.0,1.0) else Color(0.96,0.59,0.11,1.0)//different background colour if the gate is selected
            if(bill=="TEMP")
                colour=Color.DEEPSKYBLUE
            var text=if(bill=="TEMP")"" else bill

            val gate:QuantumGateView=QuantumGateView(text, index, colour,{t,i,e->onQuantumGateGrab(t,i,e);}, {t,e->onQuantumGateDrag(t,e);})
            circuitContainer.children.add(gate)
            gate.translateX=offset+12.5
            gate.translateY=0.5*(circuitContainerHeight-QuantumGateView.SCALE)

            //add close thing if selected
            if(index==selectedIndex)
            {
                val gateClose=LittleCloseThing(15.0,selectedIndex){i->gatesInCircuit.removeAt(i);selectedIndex=-1;calculateValue(); renderCircuit()}
                circuitContainer.children.add(gateClose)
                gateClose.translateX=offset+5.0
                gateClose.translateY=0.5*(circuitContainerHeight-QuantumGateView.SCALE)-7.5
            }

            offset+=QuantumGateView.SCALE+25.0
            index++
        }

        //set the pane's width so that it covers the screen
        val containerWidth=if(offset+100.0>circuitContainerWidth) offset+100.0 else circuitContainerWidth
        circuitContainer.maxWidth=containerWidth
        circuitContainer.minWidth=containerWidth
        circuitContainer.prefWidth=containerWidth
    }

    private fun onQuantumGateGrab(text:String,index:Int,event:MouseEvent)
    {
        when(event.eventType)
        {
            MouseEvent.MOUSE_PRESSED->{
                selectedIndex=-1
                targetIndex=-1
                startIndex=index
                draggedText=text
                inDrag=true
                dragStart=System.nanoTime()

                if(startIndex!=-1)
                {
                    gatesInCircuit.removeAt(startIndex)

                    //set dragged object
                    for(c in circuitContainer.children)
                    {
                        if(c is QuantumGateView&&(c as QuantumGateView).index==startIndex)
                        {
                            selectedComponent=c
                            c.opacity=0.0
                        }
                    }
                }

                draggedGate= QuantumGateView(draggedText,-1,Color(0.96,0.59,0.11,1.0),{ _, _, _->;},{ _, _->;})
                overlay.children.add(draggedGate)

                onQuantumGateDrag(text,event)//to refresh the targetIndex
                renderCircuit()
            }

            MouseEvent.MOUSE_RELEASED->{
                inDrag=false
                selectedComponent=null


                if(System.nanoTime()-dragStart<200000000)//only a click
                {
                    if(startIndex==-1)//in operator view
                        targetIndex=gatesInCircuit.size
                    else//in circuit view
                    {
                        targetIndex=startIndex
                        selectedIndex=startIndex
                    }
                }

                if(targetIndex!=-1)
                {
                    gatesInCircuit.add(targetIndex,draggedText)
                    renderCircuit()
                }

                targetIndex=-1
                draggedText="TEMP"

                overlay.children.remove(draggedGate)
                draggedGate=null

                calculateValue()
            }
        }
    }

    private fun onQuantumGateDrag(text:String,event:MouseEvent)
    {
        if(!inDrag)//habar ez valszeg nem fordul elo
            return

        if(draggedGate!=null)
        {
            draggedGate!!.layoutX = event.sceneX-0.5*QuantumGateView.SCALE
            draggedGate!!.layoutY=event.sceneY-0.5*QuantumGateView.SCALE
        }

        val prevTargetIndex=targetIndex

        targetIndex=-1
        for(node: Node in circuitContainer.children)
        {
            if(node !is QuantumGateViewBackground)
                continue

            val qgvb:QuantumGateViewBackground=node as QuantumGateViewBackground
            val lower=qgvb.localToScene(Point2D(0.0,0.0))
            val upper=qgvb.localToScene(Point2D(qgvb.width,qgvb.height))

            if(event.sceneX>lower.x&&event.sceneX<upper.x&&event.sceneY>lower.y&&event.sceneY<upper.y)
                targetIndex=qgvb.index
        }

        if(targetIndex==-1)
        {
            if(event.sceneX<circuitContainer.layoutBounds.minX
                ||event.sceneX>circuitContainer.layoutBounds.maxX
                ||event.sceneY<circuitContainer.layoutBounds.minY
                ||event.sceneY>circuitContainer.layoutBounds.maxY)//outside of the circuitcontainer
            {
                targetIndex=-1
            }
            else
                targetIndex=gatesInCircuit.size
        }


        if(targetIndex!=prevTargetIndex)
            renderCircuit()
    }

    private fun calculateValue()
    {
        var value=this.value.copy()

        for(gate in gatesInCircuit)
        {
            value=createGateMatrix(gate)*value
        }

        onValueChange(value)
    }

    private fun createGateMatrix(gate:String):Matrix
    {
        val gateType:String=gate.substringBefore('|')
        val mat=Matrix(2)
        when(gateType)
        {
            "X"->{
                mat[1,0].rl=1.0
                mat[0,1].rl=1.0
            }
            "Y"->{
                mat[1,0].img=1.0
                mat[0,1].img=-1.0
            }
            "Z"->{
                mat[0,0].rl=1.0
                mat[1,1].rl=-1.0
            }
            "H"-> {
                mat[0, 0].rl = 0.7071068
                mat[0, 1].rl = 0.7071068
                mat[1, 0].rl = 0.7071068
                mat[1, 1].rl = -0.7071068
            }
            "S"->{
                mat[0,0].rl=1.0
                mat[1,1].img=1.0
            }
            "S_ADJ"->{
                mat[0,0].rl=1.0
                mat[1,1].img=-1.0
            }
            "T"->{
                mat[0,0].rl = 1.0
                mat[1,1].rl = 0.7071068
                mat[1,1].img = 0.7071068
            }
            "T_ADJ"->{
                mat[0,0].rl = 1.0
                mat[1,1].rl = 0.7071068
                mat[1,1].img = -0.7071068
            }
            "P"->{
                val angle=Math.toRadians(gate.substringAfter('|').toDouble())
                mat[0,0].rl=1.0
                mat[1,1].rl= cos(angle)
                mat[1,1].img=sin(angle)
            }
            else->{
                mat[0,0].rl=1.0
                mat[1,1].rl=1.0
            }
        }

        return mat
    }
}