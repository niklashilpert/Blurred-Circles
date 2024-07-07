import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

const val logicalWidth = 250
const val renderWidth = 1000
const val unitInPixels = renderWidth.toDouble() / logicalWidth

class HeatMapDisplay {
    val heatMatrix = HeatMatrix(logicalWidth, logicalWidth)
    val colorProvider = ColorProvider()

    var invertColors = false
    var previewMass = 35.0

    var mouseX = Double.MAX_VALUE
    var mouseY = Double.MAX_VALUE

    var previewPoint: MassPoint? = null

    var image: BufferedImage? = null

    private var panel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.drawImage(image, 0, 0, renderWidth, renderWidth, null)
        }
    }.apply {
        preferredSize = Dimension(renderWidth, renderWidth)
        layout = null
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val x = e.x.toDouble() / unitInPixels
                val y = e.y.toDouble() / unitInPixels
                previewPoint?.x = x
                previewPoint?.y = y
                mouseX = x
                mouseY = y
                if (previewPoint != null) {
                    update()
                }

            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                previewPoint = null
                update()
            }
        })
    }

    private var frame = JFrame("Ballz").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = null
        contentPane = panel
        pack()
        this.setLocationRelativeTo(null)
        addKeyListener(HeatMapKeyListener(this@HeatMapDisplay))
        isVisible = true
    }

    init {
        update()
    }

    fun update() {
        heatMatrix.recalculate()
        updateImage()
    }

    fun updateImage() {
        image = BufferedImage(logicalWidth, logicalWidth, BufferedImage.TYPE_INT_RGB)
        for (x in 0..<logicalWidth) {
            for (y in 0..<logicalWidth) {
                image!!.setRGB(x, y, colorProvider.getColor(heatMatrix.get(x, y), invertColors).rgb)
            }
        }
        frame.repaint()
    }


    fun saveAsPng() {
        val targetFile = findNextAvailableFile()
        ImageIO.write(image, "PNG", targetFile)
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
}
