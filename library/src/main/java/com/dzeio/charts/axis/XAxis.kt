package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.dzeio.charts.ChartType
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

            field = value.coerceIn(min, max.coerceAtLeast(min))
        }

    override var enabled = true

    override var dataWidth: Double? = null

    override var labelCount: Int = 2

    override var scrollEnabled: Boolean = false

    var spacing = 16.0

    override val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FC496D")
        textSize = 30f
        textAlign = Paint.Align.LEFT
    }

    private val rect = Rect()

    private var height: Float? = null

    override fun getHeight(): Float? {
        return height
    }

    override fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Double {
        val result = drawableSpace.left + drawableSpace.width() * (entry.x - x) / getDataWidth()
        if (view.type == ChartType.GROUPED) {
            val serie = view.series.find { it.entries.contains(entry) }
            val index = view.series.indexOf(serie)
            return result + getEntryWidth(drawableSpace) * index + spacing / 2 * index
        }
        return result
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
        val valueIncrement = getDataWidth() / (labelCount - 1)
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
        height = maxHeight + 32f
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

        val result = (drawableSpace.width() * smallest / getDataWidth() - spacing)
            .coerceIn(1.0, drawableSpace.width().toDouble())

        // handle grouped series
        if (view.type == ChartType.GROUPED) {
            return result / view.series.size - spacing / 2 * (view.series.size - 1)
        }

        return result
    }

    override fun getDataWidth(): Double {
        // TODO: handle the auto dataWidth better (still not sure it is good enough)
        return dataWidth ?: (getXMax() - getXMin() + 1)
    }

}
