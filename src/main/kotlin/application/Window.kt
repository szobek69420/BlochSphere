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
            mainWindow=this
    }

    companion object
    {
        var mainWindow:Window?=null//a static window instance that should be the one instantiated by the application
    }

    lateinit var controller: MainSceneController
    lateinit var stage:Stage
    lateinit var root: Parent

    override fun start(primaryStage: Stage?) {
        var loader:FXMLLoader=FXMLLoader(this.javaClass.getResource("/layouts/MainScene.exe"))
        root =loader.load()


        var scene: Scene=Scene(root)
        scene.stylesheets.add(this.javaClass.getResource("/stylesheets/globus.exe")?.toExternalForm())

        controller=loader.getController()

        stage=Stage()
        stage.scene=scene
        stage.title="Morbisoft's Glock Sphere"
        stage.initStyle(StageStyle.UNDECORATED)

        val icon:Image= Image(this.javaClass.getResourceAsStream("/sprites/icon.png"))
        stage.icons.add(icon)

        controller.initScene()

        stage.show()

        stage.width+=1.0//to trigger a layout reload, so that the circuit renders properly



        initWindowManager()
    }

    fun closeApplication()
    {
        stage.close()
    }

    fun minimizeApplication()
    {
        stage.isIconified=true
    }

    fun isApplicationMaximized():Boolean
    {
        return (controller.menuBarDraggable.onMouseDragged as MouseEventHandler).screenState==MouseEventHandler.ScreenState.FULL
    }


    fun rescaleApplication()
    {
        val handler:MouseEventHandler=controller.menuBarDraggable.onMouseDragged as MouseEventHandler

        if(handler.screenState==MouseEventHandler.ScreenState.FULL)
            handler.desiredScreenState=MouseEventHandler.ScreenState.NORMAL
        else
            handler.desiredScreenState=MouseEventHandler.ScreenState.FULL

        handler.adjustScreenState(null);
        controller.changeRescaleImage(handler.screenState==MouseEventHandler.ScreenState.FULL)//change the icon of the rescale button
    }

    private fun onApplicationRescale(handler:MouseEventHandler)
    {
        controller.changeRescaleImage(handler.screenState==MouseEventHandler.ScreenState.FULL)
    }


    private fun initWindowManager()
    {
        val mouseHandler:MouseEventHandler=MouseEventHandler(stage){event->onApplicationRescale(event)}//a stage show utan kell letrehozni, mert konstruktorban lekeri a stage mereteit
        controller.menuBarDraggable.onMousePressed=mouseHandler
        controller.menuBarDraggable.onMouseDragged=mouseHandler
        controller.menuBarDraggable.onMouseReleased=mouseHandler

        val leftRescaleHandler=MouseRescaleHandler(Cursor.H_RESIZE,1.0,0.0,-1.0,0.0)
        controller.leftResize.onMouseEntered=leftRescaleHandler
        controller.leftResize.onMouseExited=leftRescaleHandler
        controller.leftResize.onMouseDragged=leftRescaleHandler
        controller.leftResize.onMousePressed=leftRescaleHandler

        val rightRescaleHandler=MouseRescaleHandler(Cursor.H_RESIZE,0.0,0.0,1.0,0.0)
        controller.rightResize.onMouseEntered=rightRescaleHandler
        controller.rightResize.onMouseExited=rightRescaleHandler
        controller.rightResize.onMouseDragged=rightRescaleHandler
        controller.rightResize.onMousePressed=rightRescaleHandler

        val bottomRescaleHandler=MouseRescaleHandler(Cursor.V_RESIZE,0.0,0.0,0.0,1.0)
        controller.bottomResize.onMouseEntered=bottomRescaleHandler
        controller.bottomResize.onMouseExited=bottomRescaleHandler
        controller.bottomResize.onMouseDragged=bottomRescaleHandler
        controller.bottomResize.onMousePressed=bottomRescaleHandler
    }

    //needed to move around my borderless window
    class MouseEventHandler(private val stage:Stage, private val onRescale:((MouseEventHandler)->Unit)?) : EventHandler<MouseEvent>
    {
        private var offsetX:Double=0.0
        private var offsetY:Double=0.0

        private var previousOffsetX:Double=0.0
        private var previousOffsetY:Double=0.0
        private var previousWidth:Double=0.0
        private var previousHeight:Double=0.0

        //where does the screen orient itself
        enum class ScreenState{
            NORMAL, FULL, LEFT, RIGHT
        };

        var screenState:ScreenState=ScreenState.NORMAL;
        var desiredScreenState:ScreenState=ScreenState.NORMAL;
        private val SCREEN_STAGE_CHANGE_THRESHOLD:Double=2.0//if the mouse is within 2 pixels of the screens top/left/right side, the screen will want to change state

        init{
            previousWidth=stage.width;
            previousHeight=stage.height;
        }

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
                            if(stage.y>Screen.getPrimary().visualBounds.minY+SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }
                        ScreenState.LEFT->{
                            if(stage.x>Screen.getPrimary().visualBounds.minX+SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }
                        ScreenState.RIGHT->{
                            if(stage.x+stage.width<Screen.getPrimary().visualBounds.maxX-SCREEN_STAGE_CHANGE_THRESHOLD)
                                shouldRealignToNormal=true;
                        }

                        ScreenState.NORMAL -> {
                            if(event.screenY<Screen.getPrimary().visualBounds.minY+SCREEN_STAGE_CHANGE_THRESHOLD)
                                desiredScreenState=ScreenState.FULL;
                            else if(event.screenX<Screen.getPrimary().visualBounds.minX+SCREEN_STAGE_CHANGE_THRESHOLD)
                                desiredScreenState=ScreenState.LEFT;
                            else if(event.screenX>Screen.getPrimary().visualBounds.maxX-SCREEN_STAGE_CHANGE_THRESHOLD)
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

                        onRescale?.invoke(this);
                    }

                    stage.x=event.screenX-offsetX;
                    stage.y=event.screenY-offsetY;
                }
                MouseEvent.MOUSE_PRESSED->{
                    offsetX=event.sceneX;
                    offsetY=event.sceneY;
                }
                MouseEvent.MOUSE_RELEASED->{
                    adjustScreenState(event);
                }
            }
        }


        fun adjustScreenState(event: MouseEvent?)
        {
            if(desiredScreenState==screenState)
                return;

            if(event!=null)
            {
                previousOffsetX=event.sceneX
                previousOffsetY=event.sceneY

                previousWidth=stage.width
                previousHeight=stage.height
            }
            else
            {
                previousOffsetX=stage.width*0.5
                previousOffsetY=20.0
            }

            when(desiredScreenState)
            {
                ScreenState.FULL->{
                    stage.x=Screen.getPrimary().visualBounds.minX
                    stage.y=Screen.getPrimary().visualBounds.minY
                    stage.width=Screen.getPrimary().visualBounds.minX+Screen.getPrimary().visualBounds.width
                    stage.height=Screen.getPrimary().visualBounds.minY+Screen.getPrimary().visualBounds.height
                }
                ScreenState.LEFT->{
                    stage.x=Screen.getPrimary().visualBounds.minX
                    stage.y=Screen.getPrimary().visualBounds.minY
                    stage.width=Screen.getPrimary().visualBounds.width*0.5
                    stage.height=Screen.getPrimary().visualBounds.height
                }
                ScreenState.RIGHT->{
                    stage.x=0.5*(Screen.getPrimary().visualBounds.maxX-Screen.getPrimary().visualBounds.minX)
                    stage.y=Screen.getPrimary().visualBounds.minY
                    stage.width=Screen.getPrimary().visualBounds.width*0.5
                    stage.height=Screen.getPrimary().visualBounds.height
                }

                ScreenState.NORMAL -> {
                    stage.width=previousWidth
                    stage.height=previousHeight
                    stage.x=Screen.getPrimary().visualBounds.minX+Screen.getPrimary().visualBounds.width*0.5-stage.width*0.5
                    stage.y=Screen.getPrimary().visualBounds.minY+Screen.getPrimary().visualBounds.height*0.5-stage.height*0.5

                    screenState=ScreenState.NORMAL
                }
            }

            screenState=desiredScreenState

            onRescale?.invoke(this)
        }
    }

    class MouseRescaleHandler(val cursor:Cursor, val offsetX:Double, val offsetY:Double, val scaleX:Double, val scaleY:Double) : EventHandler<MouseEvent>
    {
        private var prevScreenX:Double=0.0
        private var prevScreenY:Double=0.0

        override fun handle(event: MouseEvent?) {
            if(event==null||Window.mainWindow==null||Window.mainWindow!!.isApplicationMaximized())
                return

            when(event.eventType)
            {
                MouseEvent.MOUSE_ENTERED->Window.mainWindow!!.stage.scene.cursor=cursor
                MouseEvent.MOUSE_EXITED->Window.mainWindow!!.stage.scene.cursor=Cursor.DEFAULT;
                MouseEvent.MOUSE_PRESSED->{prevScreenX=event.screenX; prevScreenY=event.screenY}
                MouseEvent.MOUSE_DRAGGED->{
                    val deltaX=event.screenX-prevScreenX
                    val deltaY=event.screenY-prevScreenY

                    val width=Window.mainWindow!!.stage.width+deltaX*scaleX
                    val height=Window.mainWindow!!.stage.height+deltaY*scaleY

                    if(width>200.0)
                    {
                        Window.mainWindow!!.stage.x+=deltaX*offsetX
                        Window.mainWindow!!.stage.width=width
                    }
                    if(height>200.0)
                    {
                        Window.mainWindow!!.stage.y+=deltaY*offsetY
                        Window.mainWindow!!.stage.height=height
                    }

                    prevScreenX=event.screenX
                    prevScreenY=event.screenY
                }
            }
        }

    }
}