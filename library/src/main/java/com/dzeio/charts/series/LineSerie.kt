package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.dzeio.charts.ChartView
import kotlin.math.abs

class LineSerie(
    private val view: ChartView
) : BaseSerie(view) {

    private companion object {
        const val TAG = "Charts/LineSerie"
    }

    init {
        view.series.add(this)
    }

    val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#64B5F6")
        strokeWidth = 5f
    }

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private var entriesCurrentY: HashMap<Double, AnimationProgress> = hashMapOf()

    override fun onDraw(canvas: Canvas, drawableSpace: RectF): Boolean {
        val displayedEntries = getDisplayedEntries()

        var previousPosX: Float? = null
        var previousPosY: Float? = null

        var needUpdate = false

        val iterator = entriesCurrentY.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next().key
            if (displayedEntries.find { it.x == key } == null) iterator.remove()
        }

        val zero = view.yAxis.getPositionOnRect(0f, drawableSpace)

        for (entry in displayedEntries) {
            if (entriesCurrentY[entry.x] == null) {
                entriesCurrentY[entry.x] = AnimationProgress(zero)
            }

            // calculated height in percent from 0 to 100
            var top = view.yAxis.getPositionOnRect(entry, drawableSpace)

            // change value with the animator
            if (!entriesCurrentY[entry.x]!!.finished) {
                val newY = view.animator.updateValue(top, entriesCurrentY[entry.x]!!.value, zero)
                if (!needUpdate && top != newY) {
                    needUpdate = true
                }
                entriesCurrentY[entry.x]!!.finished = top == newY
                top = newY
                entriesCurrentY[entry.x]!!.value = top
            }

            val posX = (
                view.xAxis.getPositionOnRect(entry, drawableSpace) +
                view.xAxis.getEntryWidth(drawableSpace) / 2f
            ).toFloat()

            // handle color recoloration
            val paint = Paint(linePaint)

            if (entry.color != null) {
                paint.color = entry.color!!
            }

            val doDraw = drawableSpace.contains(posX, top) ||
                (
                    previousPosX != null &&
                    previousPosY != null &&
                    drawableSpace.contains(previousPosX, previousPosY)
                ) || (
                    previousPosX != null &&
                    previousPosY != null &&
                    posX < drawableSpace.right && (
                        top <= drawableSpace.top &&
                        previousPosY >= drawableSpace.bottom ||
                        top >= drawableSpace.top &&
                        previousPosY <= drawableSpace.bottom
                    )
                )

            // draw smol point
            if (drawableSpace.contains(posX, top)) {
                canvas.drawCircle(posX, top, paint.strokeWidth, paint)
            }

            // draw line
            if (doDraw && previousPosY != null && previousPosX != null) {
                var startX = previousPosX
                var startY = previousPosY
                var stopX = posX
                var stopY = top
                val debugPaint = Paint(linePaint)

                val py = previousPosY
                val px = previousPosX
                val dy = abs(py - top)
                val dx = abs(posX - px)

                if (previousPosX < drawableSpace.left) {
                    val ratio = dy / dx

                    val dcx = abs(px)
                    val dcy = dcx * ratio

                    val ny = if (startY > stopY) py - dcy else py + dcy
                    startY = ny
                    startX = drawableSpace.left
                    debugPaint.color = Color.YELLOW
                } else if (posX > drawableSpace.right) {
                    val ratio = dy / dx

                    val dcx = posX - drawableSpace.right
                    val dcy = dcx * ratio

                    val ny = if (py > top) top + dcy else top - dcy
                    stopY = ny
                    stopX = drawableSpace.right
                    debugPaint.color = Color.GRAY
                }

                if (
                    startX == previousPosX &&
                    (previousPosY > drawableSpace.bottom || previousPosY < drawableSpace.top)
                ) {
                    val dvb = if (top > py) top else drawableSpace.bottom - top

                    val ratio = dx / dy

                    val dcy = dy - dvb
                    val dcx = dcy * ratio

                    val nx = px + dcx
                    startX = nx
                    startY = if (top > py) drawableSpace.top else drawableSpace.bottom
                    debugPaint.color = Color.BLUE
                }
                if (top > drawableSpace.bottom) {
                    val ratio = dx / dy
                    val dcy = drawableSpace.bottom - py
                    val dcx = dcy * ratio
                    stopX = px + dcx
                    stopY = drawableSpace.bottom
                    if (startX != previousPosX) {
                        debugPaint.color = Color.GREEN
                    } else {
                        debugPaint.color = Color.RED
                    }
                }
                canvas.drawLine(startX, startY, stopX, stopY, if (view.debug) debugPaint else linePaint)
            }
            previousPosX = posX
            previousPosY = top
        }

        return needUpdate
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }
}
