package main.kotlin.application

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.Accordion
import javafx.scene.control.MenuBar
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import main.kotlin.elements.BetterButton
import main.kotlin.elements.ResizableAnchorPane
import main.kotlin.view.CircuitView
import main.kotlin.view.SphereView
import java.lang.Exception


class MainSceneController {

    @FXML
    lateinit var root:AnchorPane;

    @FXML
    lateinit var toolboxAccordion: Accordion;

    @FXML
    lateinit var sphereContainer: AnchorPane;


    @FXML
    lateinit var circuitContainer:AnchorPane;
    @FXML
    lateinit var operationContainer:FlowPane;




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
        circuitContainer.maxHeight=200.0;
        circuitView= CircuitView(circuitContainer,operationContainer,root);
    }
}