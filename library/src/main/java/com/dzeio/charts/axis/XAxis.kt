package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.Entry
import kotlin.math.roundToInt

class XAxis(
    private val view: ChartViewInterface
) : XAxisInterface {

    private companion object {
        const val TAG = "Charts/XAxis"
    }

    override var x: Double = 0.0
        set(value) {
            val max = getXMax() - getDataWidth()
            val min = getXMin()
            if (value > max && min <= max) {
                field = max
                return
            }

            if (value < min) {
                field = min
                return
            }

            field = value
        }

    override var enabled = true

    override var dataWidth: Double? = null
        get() = field ?: getXMax()

    override var labelCount: Int = 2

    var spacing = 16.0

    override val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FC496D")
        textSize = 30f
        textAlign = Paint.Align.LEFT
    }

    private val rect = Rect()

    override fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Double {
        return translatePositionToRect(entry.x, drawableSpace)
    }

    fun translatePositionToRect(value: Double, drawableSpace: RectF): Double {
        return drawableSpace.width() * (value - x) / getDataWidth()
    }

    override fun getXMax(): Double {
        return view.series.maxOf { serie ->
            if (serie.entries.isEmpty()) {
                return 0.0
            }
            serie.entries.maxOf { entry -> entry.x }
        }
    }

    override fun getXMin(): Double {
        return view.series.minOf { serie ->
            if (serie.entries.isEmpty()) {
                return 0.0
            }
            serie.entries.minOf { entry -> entry.x }
        }
    }

    var onValueFormat: (value: Double) -> String = { it -> it.roundToInt().toString() }

    override fun onDraw(canvas: Canvas, space: RectF): Float {
        if (!enabled) {
            return 0f
        }

        var maxHeight = 0f

        val graphIncrement = space.width() / (labelCount - 1)
        val valueIncrement = (getDataWidth() / (labelCount - 1)).toDouble()
        for (index in 0 until labelCount) {
            val text = onValueFormat(x + valueIncrement * index)
            textPaint.getTextBounds(text, 0, text.length, rect)
            maxHeight = maxHeight.coerceAtLeast(rect.height().toFloat() + 1)

            var xPos = space.left + graphIncrement * index

            if (xPos + rect.width() > space.right) {
                xPos = space.right - rect.width()
            }

            canvas.drawText(
                text,
                xPos,
                space.bottom,
                textPaint
            )
        }
        return maxHeight + 32f
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }

    override fun getEntryWidth(drawableSpace: RectF): Double {
        var smallest = Double.MAX_VALUE
        val dataset = view.getDataset()
        for (idx in 0 until dataset.size - 1) {
            val distance = dataset[idx + 1].x - dataset[idx].x
            if (smallest > distance && distance > 0.0) {
                smallest = distance
            }
        }

        return clamp(drawableSpace.width() * smallest / getDataWidth() - spacing, 1.0, drawableSpace.width().toDouble())
    }


    override fun getDataWidth(): Double {
        // TODO: handle the auto dataWidth better
        return dataWidth ?: getXMax()
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        return if (value < min) min else if (value > max) max else value
    }
}
