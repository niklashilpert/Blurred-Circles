import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.abs

var invertColors = false

val colorProvider = ColorProvider()

const val logicalWidth = 500
const val renderWidth = 1000
const val unitInPixels = renderWidth / logicalWidth

var mouseX = Double.MAX_VALUE
var mouseY = Double.MAX_VALUE

var currentMass = 35.0

var mouseBall = Ball(mouseX, mouseY, currentMass)

var heatCircles = arrayListOf(
    mouseBall
)

var repaintTimer = Timer(1000 / 60) { frame.repaint() }.also { it.start() }

var panel = object : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        paintCircles(g)
    }
}.apply {
    preferredSize = Dimension(renderWidth, renderWidth)
    layout = null
    addMouseMotionListener(object : MouseAdapter() {
        override fun mouseMoved(e: MouseEvent) {
            val x = e.x.toDouble() / unitInPixels
            val y = e.y.toDouble() / unitInPixels
            mouseBall.x = x
            mouseBall.y = y
            mouseX = x
            mouseY = y
        }
    })

    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            heatCircles.add(Ball(mouseX, mouseY, currentMass))
            heatCircles.remove(mouseBall)

        }
    })
}

var frame = JFrame("Ballz").apply {
    defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    layout = null
    contentPane = panel
    pack()
    this.setLocationRelativeTo(null)
    addKeyListener(object : KeyListener {
        override fun keyTyped(e: KeyEvent) {}

        override fun keyPressed(e: KeyEvent) {
            if (e.keyCode == KeyEvent.VK_W) {
                currentMass += 2.0
                println("Mass changed: $currentMass")
                mouseBall.mass = currentMass
            } else if (e.keyCode == KeyEvent.VK_S) {
                currentMass -= 2.0
                println("Mass changed: $currentMass")
                mouseBall.mass = currentMass
            } else if (e.keyCode == KeyEvent.VK_N) {
                if (heatCircles.contains(mouseBall)) {
                    heatCircles.remove(mouseBall)
                } else {
                    heatCircles.add(mouseBall)
                }
            } else if (e.keyCode == KeyEvent.VK_D) {
                saveAsPng()
            } else if (e.keyCode == KeyEvent.VK_I) {
                invertColors = !invertColors
            } else if (e.keyCode == KeyEvent.VK_UP) {
               // positiveBlurThreshold += .05
            } else if (e.keyCode == KeyEvent.VK_DOWN) {
               // positiveBlurThreshold -= .05
            }

        }

        override fun keyReleased(e: KeyEvent) {}
    })
}

fun paintCircles(g: Graphics) {
    val startTime = System.currentTimeMillis()
    //g.color = colorProvider.getBaseColor(invertColors)
    //g.drawRect(0, 0, renderWidth, renderWidth)
    for (x in 0..<logicalWidth) {
        for (y in 0..<logicalWidth) {
            g.color = colorProvider.getColorAt(x.toDouble(), y.toDouble(), ::heat, invertColors)
            g.fillRect(x * unitInPixels, y * unitInPixels, unitInPixels, unitInPixels)
        }
    }
    val endTime = System.currentTimeMillis()
    println("Drawing took ${endTime-startTime} millis")
}

/**
 * Returns the "heat" value of the provided point by calculating distance^2 from
 * every existing circle and multiplying it by some value derived from the mass.
 * These results are added together and returned.
 */
fun heat(x: Double, y: Double): Double {
    var result = 0.0
    heatCircles.forEach {
        val distSq = distanceSquared(x, y, it.x, it.y)
        if (distSq == 0.0) {
            return it.mass
        } else {
            result += it.mass / abs(it.mass) * it.mass * it.mass / distSq
        }
    }
    return result
}

/**
 * Calculates the square of the distance between the two provided points.
 */
fun distanceSquared(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)
}

fun saveAsPng() {
    val img = BufferedImage(renderWidth, renderWidth, BufferedImage.TYPE_INT_RGB)
    paintCircles(img.graphics)
    var targetFile = findNextAvailableFile()
    ImageIO.write(img, "PNG", targetFile)
}

fun findNextAvailableFile(): File {
    var index = 0
    var targetFile: File
    do {
        targetFile = File("output$index.png")
        index++
    } while (targetFile.isFile)
    return targetFile
}

