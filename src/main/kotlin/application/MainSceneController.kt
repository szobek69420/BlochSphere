package main.kotlin.application

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.Accordion
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import main.kotlin.elements.BetterButton
import main.kotlin.quantum.Qubit
import main.kotlin.view.CircuitView
import main.kotlin.view.SphereView


class MainSceneController {

    @FXML
    lateinit var root:AnchorPane;

    @FXML
    lateinit var toolboxAccordion: Accordion;

    @FXML
    lateinit var sphereContainer: AnchorPane;


    @FXML
    lateinit var circuitContainer:Pane;
    @FXML
    lateinit var normieContainer:FlowPane;
    @FXML
    lateinit var phaseContainer:FlowPane;

    @FXML
    lateinit var circuitContainerScrollPaneContainer:AnchorPane;



    lateinit var sphereSubScene: SubScene;
    lateinit var objectParent:Group;

    lateinit var sphereView:SphereView;

    lateinit var circuitView:CircuitView;

    lateinit var exitButton:BetterButton;//ide kell a lateinit
    lateinit var rescaleButton:BetterButton;
    lateinit var minimizeButton:BetterButton;

    fun initScene()
    {
        toolboxAccordion.minWidth=0.0;
        toolboxAccordion.maxWidth=300.0;
        toolboxAccordion.maxHeight=500.0;

        sphereContainer.children.clear();

        objectParent=Group();
        sphereSubScene = SubScene(objectParent, sphereContainer.width, sphereContainer.width, true, SceneAntialiasing.BALANCED);
        sphereSubScene.fill= Color.BLACK;

        sphereContainer.children.add(sphereSubScene);

        sphereView= SphereView(sphereSubScene, sphereContainer);

        //resize children as well
        sphereContainer.layoutBoundsProperty().addListener() { _, oldValue, newValue ->
            if (oldValue.width != newValue.width || oldValue.height != newValue.height) {
                sphereSubScene.width=newValue.width;
                sphereSubScene.height=newValue.height;

                sphereView.onResize();
            }
        }

        //circuit view
        circuitContainerScrollPaneContainer.maxHeight=200.0;
        circuitView= CircuitView(circuitContainer,normieContainer,phaseContainer,root){q->onCircuitValueChange(q)};

        circuitContainerScrollPaneContainer.layoutBoundsProperty().addListener(){_,oldValue,newValue->
            if(oldValue.height!=newValue.height||oldValue.width!=newValue.width)
            {
                circuitView.circuitContainerWidth=newValue.width;
                circuitView.circuitContainerHeight=newValue.height;
                circuitView.renderCircuit();
            }
        }

        //so that the scrollpane scrolls horizontally on mouse scroll
        (circuitContainerScrollPaneContainer.children[0] as ScrollPane).onScroll= EventHandler { event ->
            if(event!=null)
                (circuitContainerScrollPaneContainer.children[0] as ScrollPane).hvalue-=10.0*event.deltaY/((circuitContainerScrollPaneContainer.children[0] as ScrollPane).content as Pane).width;
        }
    }

    private fun onCircuitValueChange(value: Qubit)
    {
        sphereView.setValue(value);
    }
}