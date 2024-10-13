package main.kotlin.application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Window() : Application() {

    override fun start(primaryStage: Stage?) {
        var root: Parent =FXMLLoader.load(this.javaClass.getResource("/layouts/MainScene.fxml"));
        var scene: Scene=Scene(root);
        var stage: Stage=Stage();

        stage.scene=scene;
        stage.show();
    }
}