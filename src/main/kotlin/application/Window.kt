package main.kotlin.application

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
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

        controller=loader.getController();

        stage=Stage();
        stage.scene=scene;
        stage.title="Glock sphere";
        stage.initStyle(StageStyle.UNDECORATED);

        val mouseHandler:MouseEventHandler=MouseEventHandler(stage);
        controller.menuBar.onMousePressed=mouseHandler;
        controller.menuBar.onMouseDragged=mouseHandler;
        controller.menuBar.onMouseReleased=mouseHandler;

        controller.initScene();

        stage.show();
    }

    fun closeApplication()
    {
        stage.close();
    }

    //needed to move around my borderless window
    class MouseEventHandler(val stage:Stage) : EventHandler<MouseEvent>
    {
        var offsetX:Double=0.0;
        var offsetY:Double=0.0;

        var previousOffsetX:Double=0.0;
        var previousOffsetY:Double=0.0;
        var previousWidth:Double=0.0;
        var previousHeight:Double=0.0;

        //where does the screen orient itself
        enum class ScreenState{
            NORMAL, FULL, LEFT, RIGHT
        };

        var screenState:ScreenState=ScreenState.NORMAL;
        var desiredScreenState:ScreenState=ScreenState.NORMAL;
        val SCREEN_STAGE_CHANGE_THRESHOLD:Double=2.0;//if the mouse is within 2 pixels of the screens top/left/right side, the screen will want to change state

        override fun handle(event: MouseEvent?) {
            if(event==null)
                return;

            when(event.eventType)
            {
                MouseEvent.MOUSE_DRAGGED->{

                    var shouldRealignToNormal:Boolean=false;//if the screen is oriented somewhere but it doesn't meet the criteria for said orientation
                    when(screenState)
                    {
                        ScreenState.FULL->{
                            if(stage.y>Screen.getPrimary().bounds.minY+SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }
                        ScreenState.LEFT->{
                            if(stage.x>Screen.getPrimary().bounds.minX+SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }
                        ScreenState.RIGHT->{
                            if(stage.x+stage.width<Screen.getPrimary().bounds.maxX-SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }

                        ScreenState.NORMAL -> {
                            if(event.screenY<Screen.getPrimary().bounds.minY+SCREEN_STAGE_CHANGE_THRESHOLD)
                                desiredScreenState=ScreenState.FULL;
                            else if(event.screenX<Screen.getPrimary().bounds.minX+SCREEN_STAGE_CHANGE_THRESHOLD)
                                desiredScreenState=ScreenState.LEFT;
                            else if(event.screenX>Screen.getPrimary().bounds.maxX-SCREEN_STAGE_CHANGE_THRESHOLD)
                                desiredScreenState=ScreenState.RIGHT;
                            else
                                desiredScreenState=ScreenState.NORMAL;
                        }
                    }

                    if(shouldRealignToNormal)
                    {
                        offsetX=previousOffsetX;
                        offsetY=previousOffsetY;

                        stage.width=previousWidth;
                        stage.height=previousHeight;

                        screenState=ScreenState.NORMAL;
                    }

                    stage.x=event.screenX-offsetX;
                    stage.y=event.screenY-offsetY;
                }
                MouseEvent.MOUSE_PRESSED->{
                    offsetX=event.sceneX;
                    offsetY=event.sceneY;
                }
                MouseEvent.MOUSE_RELEASED->{
                    if(screenState!=ScreenState.NORMAL)//does not want to override the current abnormal screen state
                        return;

                    if(desiredScreenState==ScreenState.NORMAL)//nothing should be applied
                        return;

                    previousOffsetX=event.sceneX;
                    previousOffsetY=event.sceneY;
                    previousWidth=stage.width;
                    previousHeight=stage.height;

                    when(desiredScreenState)
                    {
                        ScreenState.FULL->{
                            stage.x=Screen.getPrimary().bounds.minX;
                            stage.y=Screen.getPrimary().bounds.minY;
                            stage.width=Screen.getPrimary().bounds.width;
                            stage.height=Screen.getPrimary().bounds.height;
                        }
                        ScreenState.LEFT->{
                            stage.x=Screen.getPrimary().bounds.minX;
                            stage.y=Screen.getPrimary().bounds.minY;
                            stage.width=Screen.getPrimary().bounds.width*0.5;
                            stage.height=Screen.getPrimary().bounds.height;
                        }
                        ScreenState.RIGHT->{
                            stage.x=0.5*(Screen.getPrimary().bounds.maxX-Screen.getPrimary().bounds.minX);
                            stage.y=Screen.getPrimary().bounds.minY;
                            stage.width=Screen.getPrimary().bounds.width*0.5;
                            stage.height=Screen.getPrimary().bounds.height;
                        }

                        ScreenState.NORMAL -> {}
                    }

                    screenState=desiredScreenState;
                }
            }
        }

    }
}