import java.awt.Color
import kotlin.math.abs

class ColorProvider {
    private val backgroundColor: Color get() = Color(0, 0, 0)
    private val positiveHeatColor: Color get() = Color(255, 255, 255)
    private val negativeHeatColor: Color get() = Color(0, 0, 255)

    private val blurThreshold = 0.5
    private val solidThreshold = 1.4


    private fun calculateAlpha(heat: Double): Double {
        val absHeat = abs(heat)
        return if (absHeat in blurThreshold..solidThreshold) {
            ((absHeat - blurThreshold) / (solidThreshold - blurThreshold))
        } else if (absHeat > solidThreshold){
            1.0
        } else {
            0.0
        }
    }

    private fun setAlpha(color: Color, heat: Double): Color {
        val alpha = calculateAlpha((heat))
        if (alpha == 0.0) {
            return color
        }
        return Color(
            (color.red * alpha).toInt(),
            (color.green * alpha).toInt(),
            (color.blue * alpha).toInt()
        )
    }
    private fun invert(color: Color) = Color(0xffffff - color.rgb)

    /**
     * Determines the color of the pixel at x and y by using the heat function's result. If 'invert' is true, the color is inverted.
     */
    fun getColor(heatValue: Double, invert: Boolean): Color {
        val color = if (heatValue > blurThreshold) {
            setAlpha(positiveHeatColor, heatValue)
        } else if (heatValue < -blurThreshold) {
            setAlpha(negativeHeatColor, heatValue)
        } else {
            setAlpha(backgroundColor, 0.0)
        }

        return if (invert) {
            invert(color)
        } else {
            color
        }
    }

    fun getBaseColor(invert: Boolean): Color {
        return if (invert) {
            invert(backgroundColor)
        } else {
            backgroundColor
        }
    }
}