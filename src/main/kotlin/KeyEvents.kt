import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class HeatMapKeyListener(val display: HeatMapDisplay) : KeyAdapter() {
    private val keyActionMap = HashMap<Int, (KeyEvent) -> Unit>()

    init {
        keyActionMap[KeyEvent.VK_W] = ::increaseMass
        keyActionMap[KeyEvent.VK_S] = ::decreaseMass
        keyActionMap[KeyEvent.VK_N] = ::createNewCircle
        keyActionMap[KeyEvent.VK_P] = { display.saveAsPng() }
    }

    override fun keyPressed(e: KeyEvent) {
        val action = keyActionMap[e.keyCode]
        if (action != null) {
            action.invoke(e)
            display.update()
        }
    }

    private fun createNewCircle(e: KeyEvent) {
        if (display.previewPoint == null) {
            val newMassPoint = display.heatMatrix.createMassPoint(
                display.mouseX.toInt(),
                display.mouseY.toInt(),
                display.previewMass
            )
            display.previewPoint = newMassPoint
        } else {
            display.heatMatrix.removeMassPoint(display.previewPoint!!)
            display.previewPoint = null
            display.update()
        }
    }

    fun increaseMass(e: KeyEvent) {
        display.previewMass += 2.0
        display.previewPoint?.mass = display.previewMass
        display.update()
    }
    fun decreaseMass(e: KeyEvent) {
        display.previewMass -= 2.0
        display.previewPoint?.mass = display.previewMass
        display.update()
    }

}

/*if (e.keyCode == KeyEvent.VK_W) {
                currentMass += 2.0
                println("Mass changed: $currentMass")
                mouseBall.mass = currentMass
            } else if (e.keyCode == KeyEvent.VK_S) {
                currentMass -= 2.0
                println("Mass changed: $currentMass")
                mouseBall.mass = currentMass
            } else if (e.keyCode == KeyEvent.VK_N) {
                if (heatMatrix.contains(mouseBall)) {
                    heatMatrix.remove(mouseBall)
                } else {
                    heatMatrix.add(mouseBall)
                }
            } else if (e.keyCode == KeyEvent.VK_D) {
                saveAsPng()
            } else if (e.keyCode == KeyEvent.VK_I) {
                invertColors = !invertColors
            } else if (e.keyCode == KeyEvent.VK_UP) {
               // positiveBlurThreshold += .05
            } else if (e.keyCode == KeyEvent.VK_DOWN) {
               // positiveBlurThreshold -= .05
            }*/