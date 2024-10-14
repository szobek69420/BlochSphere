package main.kotlin.application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

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

        controller=loader.getController();

        stage=Stage();
        stage.scene=scene;
        stage.title="Glock sphere";

        controller.initScene();

        stage.show();
    }
}