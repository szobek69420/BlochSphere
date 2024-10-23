package main.kotlin.application

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle


class Window() : Application() {

    init
    {
        if(mainWindow==null)
            mainWindow=this;
    }

    companion object
    {
        var mainWindow:Window?=null;//a static window instance that should be the one instantiated by the application
    }

    lateinit var controller: MainSceneController;
    lateinit var stage:Stage;
    lateinit var root: Parent;

    override fun start(primaryStage: Stage?) {
        var loader:FXMLLoader=FXMLLoader(this.javaClass.getResource("/layouts/MainScene.fxml"));
        root =loader.load();


        var scene: Scene=Scene(root);
        scene.stylesheets.add(this.javaClass.getResource("/stylesheets/font.exe")?.toExternalForm());

        controller=loader.getController();

        stage=Stage();
        stage.scene=scene;
        stage.title="Glock sphere";
        stage.initStyle(StageStyle.DECORATED);

        val icon:Image= Image(this.javaClass.getResourceAsStream("/sprites/icon.png"));
        stage.icons.add(icon);

        controller.initScene();

        stage.show();

        stage.width+=1.0;//to trigger a layout reload, so that the circuit renders properly
    }
}