package main.kotlin.application

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.Button
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
    @FXML
    lateinit var exitButton:Button;


    lateinit var sphereSubScene: SubScene;
    lateinit var objectParent:Group;

    lateinit var sphereView:SphereView;

    fun initScene()
    {
        sphereContainer.children.clear();

        val exitButton:BetterButton= BetterButton("X");
        exitButton.callback= {closeWindow();};
        exitButton.minWidth=30.0;
        exitButton.minHeight=menuContainer.height;
        exitButton.changeColours(Color.BLACK,Color.TRANSPARENT,Color.RED,Color.RED);
        HBox.setHgrow(exitButton,Priority.NEVER);
        menuContainer.children.add(exitButton);

        menuBar.background= Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))

        objectParent=Group();
        sphereSubScene = SubScene(objectParent, sphereContainer.width, sphereContainer.width, true, SceneAntialiasing.BALANCED);
        sphereSubScene.fill= Color.BLACK;

        sphereContainer.children.add(sphereSubScene);

        sphereView= SphereView(sphereSubScene);

        //resize children as well
        sphereContainer.layoutBoundsProperty().addListener() { _, oldValue, newValue ->
            if (oldValue.width != newValue.width || oldValue.height != newValue.height) {
                sphereSubScene.width=newValue.width;
                sphereSubScene.height=newValue.height;
            }
        }

    }

    fun closeWindow()
    {
        Window.mainWindow?.closeApplication();
    }
}