package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.dzeio.charts.ChartView

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
        color = Color.parseColor("#123456")
        strokeWidth = 5f
    }

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FC496D")
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

            val posX = (drawableSpace.left +
                    view.xAxis.getPositionOnRect(entry, drawableSpace) +
                    view.xAxis.getEntryWidth(drawableSpace) / 2f).toFloat()

            // handle color recoloration
            val paint = Paint(linePaint)

            if (entry.color != null) {
                paint.color = entry.color!!
            }

            // draw smol point
            if (posX < drawableSpace.right) {
                canvas.drawCircle(posX, top, paint.strokeWidth, paint)
            }

            // draw line
            if (previousPosX != null && previousPosY != null) {
                canvas.drawLine(previousPosX, previousPosY, posX, top, paint)

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
