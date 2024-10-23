package main.kotlin.elements

import javafx.scene.layout.AnchorPane

class ResizableAnchorPane: AnchorPane() {
    fun setWidthExternal(_width:Double)
    {
        width=_width;
    }

    fun setHeightExternal(_height:Double)
    {
        width=_height;
    }
}