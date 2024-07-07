import kotlin.math.abs

class HeatMatrix(val width: Int, val height: Int) {
    private var massPoints = arrayListOf<MassPoint>()
    private var heatMatrix = Array(height) { DoubleArray(width) }

    fun contains(circle: MassPoint): Boolean {
        return massPoints.contains((circle))
    }

    fun removeMassPoint(point: MassPoint) {
        massPoints.remove(point)
        recalculate()
    }

    fun createMassPoint(x: Int, y: Int, mass: Double): MassPoint {
        val circle = MassPoint(x.toDouble(), y.toDouble(), mass)
        massPoints.add(circle)
        recalculate()
        return circle
    }

    fun recalculate() {
        val startTime = System.currentTimeMillis()
        for (y in 0..heatMatrix.size-1) {
            for (x in 0..heatMatrix.size-1) {
                heatMatrix[y][x] = heat(x.toDouble(), y.toDouble())
            }
        }
        val endTime = System.currentTimeMillis()
        println("Recalculation took ${endTime - startTime} ms")
    }

    fun get(x: Int, y: Int): Double {
        return heatMatrix[y][x]
    }

    /**
     * Returns the "heat" value of the provided point by calculating distance^2 from
     * every existing circle and multiplying it by some value derived from the mass.
     * These results are added together and returned.
     */
    private fun heat(x: Double, y: Double): Double {
        var result = 0.0
        massPoints.forEach {
            val distSq = distanceSquared(x, y, it.x, it.y)
            if (distSq == 0.0) {
                return it.mass
            } else {
                result += it.mass / abs(it.mass) * it.mass * it.mass / distSq
            }
        }
        return result
    }

    private fun distanceSquared(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)
    }
}