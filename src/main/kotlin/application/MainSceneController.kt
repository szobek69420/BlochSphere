package main.kotlin.application

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.Accordion
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import main.kotlin.elements.BetterButton
import main.kotlin.quantum.Qubit
import main.kotlin.view.CircuitView
import main.kotlin.view.SphereView


class MainSceneController {

    @FXML
    lateinit var applicationRoot:AnchorPane;

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

    //rescale things
    @FXML
    lateinit var menuBarDraggable:AnchorPane;
    @FXML
    lateinit var closeButton: AnchorPane;
    @FXML
    lateinit var rescaleButton:AnchorPane;
    @FXML
    lateinit var minimizeButton:AnchorPane;



    lateinit var sphereSubScene: SubScene;
    lateinit var objectParent:Group;

    lateinit var sphereView:SphereView;

    lateinit var circuitView:CircuitView;



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
        circuitView= CircuitView(circuitContainer,normieContainer,phaseContainer,applicationRoot){q->onCircuitValueChange(q)};

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

        //setting up rescale things
        initMenuBarButtons();
    }

    fun changeRescaleImage(isMaximized:Boolean)
    {
        val imagePath:String=if(isMaximized) "/sprites/rescale2.png" else "/sprites/rescale1.png";
        (rescaleButton.children[0] as ImageView).image=Image(this.javaClass.getResourceAsStream(imagePath));
    }

    private fun onCircuitValueChange(value: Qubit)
    {
        sphereView.setValue(value);
    }

    private fun initMenuBarButtons()
    {
        val mh_close=MenuBarButtonMouseHandler(closeButton,Color.TRANSPARENT,Color.RED,Color(1.0,0.0,0.0,0.7)){Window.mainWindow?.closeApplication()}
        val mh_rescale=MenuBarButtonMouseHandler(rescaleButton,Color.TRANSPARENT,Color(0.6,0.6,0.6,1.0),Color(0.6,0.6,0.6,0.7)){Window.mainWindow?.rescaleApplication()}
        val mh_minimize=MenuBarButtonMouseHandler(minimizeButton,Color.TRANSPARENT,Color(0.6,0.6,0.6,1.0),Color(0.6,0.6,0.6,0.7)){Window.mainWindow?.minimizeApplication()}

        val handlers= arrayOf(mh_close,mh_rescale,mh_minimize);
        val images= arrayOf(closeButton.children[0] as ImageView,rescaleButton.children[0] as ImageView, minimizeButton.children[0] as ImageView);

        for(i in 0..2)
        {
            images[i].onMouseEntered=handlers[i];
            images[i].onMouseExited=handlers[i];
            images[i].onMousePressed=handlers[i];
            images[i].onMouseReleased=handlers[i];
        }
    }

    private class MenuBarButtonMouseHandler(val background:AnchorPane, val normalColour:Color, val hoverColour:Color, val pressedColour:Color, val onClick:()->Unit):EventHandler<MouseEvent>{
        private var isEntered=false;
        private var isPressed=false;

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType)
            {
                MouseEvent.MOUSE_ENTERED->{
                    isEntered=true;

                    if(isPressed)
                        background.background= Background(BackgroundFill(pressedColour, CornerRadii.EMPTY, Insets.EMPTY));
                    else
                        background.background= Background(BackgroundFill(hoverColour, CornerRadii.EMPTY, Insets.EMPTY));
                }
                MouseEvent.MOUSE_EXITED->{
                    isEntered=false;
                    background.background= Background(BackgroundFill(normalColour, CornerRadii.EMPTY, Insets.EMPTY));
                }
                MouseEvent.MOUSE_PRESSED->{
                    isPressed=true;
                    background.background= Background(BackgroundFill(pressedColour, CornerRadii.EMPTY, Insets.EMPTY));
                }
                MouseEvent.MOUSE_RELEASED->{
                    isPressed=false;
                    background.background= Background(BackgroundFill(normalColour, CornerRadii.EMPTY, Insets.EMPTY));//direkt normalcoloru

                    if(isEntered)
                    {
                        isEntered=false;
                        onClick();
                    }
                }
            }
        }

    }
}