package main.kotlin.application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Window() : Application() {

    lateinit var controller: MainSceneController;

    override fun start(primaryStage: Stage?) {
        var loader:FXMLLoader=FXMLLoader(this.javaClass.getResource("/layouts/MainScene.fxml"));
        var root: Parent =loader.load();
        var scene: Scene=Scene(root);
        var stage: Stage=Stage();

        controller=loader.getController();

        stage.scene=scene;
        stage.title="Glock sphere";
        stage.show();
    }
}