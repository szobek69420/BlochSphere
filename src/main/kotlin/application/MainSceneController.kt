package main.kotlin.application

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.MenuBar
import javafx.scene.layout.*
import javafx.scene.paint.Color
import main.kotlin.elements.BetterButton
import main.kotlin.view.SphereView


class MainSceneController {
    @FXML
    lateinit var sphereContainer: AnchorPane;

    @FXML
    lateinit var menuContainer:HBox;
    @FXML
    lateinit var menuBar:MenuBar;


    lateinit var sphereSubScene: SubScene;
    lateinit var objectParent:Group;

    lateinit var sphereView:SphereView;

    lateinit var exitButton:BetterButton;//ide kell a lateinit
    lateinit var rescaleButton:BetterButton;
    lateinit var minimizeButton:BetterButton;

    fun initScene()
    {
        sphereContainer.children.clear();

        addButtons();
        menuBar.background= Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))

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

    }

    fun closeWindow()
    {
        Window.mainWindow?.closeApplication();
    }

    fun minimizeWindow()
    {
        Window.mainWindow?.minimizeApplication();
    }

    fun rescaleWindow()
    {
        Window.mainWindow?.rescaleApplication();
    }

    fun changeRescaleImage(isMaximized:Boolean)
    {
        if(isMaximized)
            rescaleButton.changeImage("/sprites/rescale2.png");
        else
            rescaleButton.changeImage("/sprites/rescale1.png");
    }

    private fun addButtons()
    {
        minimizeButton = BetterButton();
        minimizeButton.callback= {minimizeWindow();};
        minimizeButton.minWidth=30.0;
        minimizeButton.minHeight=menuContainer.height;
        minimizeButton.changeColours(Color.TRANSPARENT,Color.AZURE,Color.AZURE);
        minimizeButton.changeImage("/sprites/minimize.png");
        HBox.setHgrow(minimizeButton,Priority.NEVER);
        menuContainer.children.add(minimizeButton);

        rescaleButton = BetterButton();
        rescaleButton.callback= {rescaleWindow();};
        rescaleButton.minWidth=30.0;
        rescaleButton.minHeight=menuContainer.height;
        rescaleButton.changeColours(Color.TRANSPARENT,Color.DEEPSKYBLUE,Color.DEEPSKYBLUE);
        rescaleButton.changeImage("/sprites/rescale1.png");
        HBox.setHgrow(rescaleButton,Priority.NEVER);
        menuContainer.children.add(rescaleButton);

        exitButton = BetterButton();
        exitButton.callback= {closeWindow();};
        exitButton.minWidth=30.0;
        exitButton.minHeight=menuContainer.height;
        exitButton.changeColours(Color.TRANSPARENT,Color.RED,Color.RED);
        exitButton.changeImage("/sprites/x.png");
        HBox.setHgrow(exitButton,Priority.NEVER);
        menuContainer.children.add(exitButton);
    }
}