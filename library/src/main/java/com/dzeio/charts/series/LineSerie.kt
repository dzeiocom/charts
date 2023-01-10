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

    override fun onDraw(canvas: Canvas, drawableSpace: RectF) {
        val displayedEntries = getDisplayedEntries()
        displayedEntries.sortBy { it.x }
        val max = view.yAxis.getYMax()

        var previousPosX: Float? = null
        var previousPosY: Float? = null

        for (entry in displayedEntries) {
            // calculated height in percent from 0 to 100
            val top = (1 - entry.y / max) * drawableSpace.height() + drawableSpace.top
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
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }
}
