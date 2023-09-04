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
        const val TAG = "XAxis"
    }

    private var x: Double = 0.0

    override var enabled = true

    override val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }

    override var dataWidth: Double? = null

    override var labelCount: Int = 2

    override var scrollEnabled: Boolean = false
    override var zoomEnabled: Boolean = false

    var spacing = 16.0

    override val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.LEFT
    }

    private val rect = Rect()

    private var height: Float? = null
    override var keepGlobalLimits = false

    override fun getHeight(): Float? {
        return height
    }

    override fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Double {
        val result = getPositionOnRect(entry.x, drawableSpace)
        if (view.type == ChartType.GROUPED) {
            val serie = view.series.find { it.entries.contains(entry) }
            val index = view.series.indexOf(serie)
            return result + getEntryWidth(drawableSpace) * index + spacing / 2 * index
        }
        return result
    }

    override fun getPositionOnRect(position: Double, drawableSpace: RectF): Double {
        return drawableSpace.left + drawableSpace.width() * (position - x) / getDataWidth()
    }

    override fun getCurrentMin(): Double {
        return this.x
    }

    override fun setCurrent(min: Double?, max: Double?): Boolean {
        val previousMin = getCurrentMin()
        val previousMax = getCurrentMax()
        setCurrentMin(min)
        if (previousMin != getCurrentMin()) {
            setCurrentMin(previousMin)
            return false
        }
        setCurrentMax(max)
        if (previousMax != getCurrentMax()) {
            setCurrentMax(previousMax)
            setCurrentMin(previousMin)
            return false
        }
        return true
    }

    override fun setCurrentMin(value: Double?) {
        if (keepGlobalLimits) {
            this.x = (value ?: 0.0).coerceIn(getMin(), getMax())
            return
        }
        this.x = value ?: 0.0
    }

    override fun setCurrentMax(value: Double?) {
        if (value == null) {
            dataWidth = null
            return
        }
        var real = value
        if (keepGlobalLimits) {
            real = value.coerceIn(getMin(), getMax())
        }
        dataWidth = real.coerceAtLeast(this.x + 1) - this.x
    }

    override fun getCurrentMax(): Double {
        return this.x + getDataWidth()
    }

    override fun getMax(): Double {
        return view.series
            .maxOf { serie ->
                if (serie.entries.isEmpty()) {
                    return@maxOf 0.0
                }
                return@maxOf serie.entries.maxOf { entry -> entry.x }
            }
    }

    override fun getMin(): Double {
        return view.series
            .minOf { serie ->
                if (serie.entries.isEmpty()) {
                    return@minOf 0.0
                }
                return@minOf serie.entries.minOf { entry -> entry.x }
            }
    }

    override var onValueFormat: (value: Double) -> String = { it -> it.roundToInt().toString() }

    override fun onDraw(canvas: Canvas, space: RectF): Float {
        if (!enabled) {
            return 0f
        }

        var maxHeight = 0f

        val valueIncrement = getDataWidth() / (labelCount - 1).coerceAtLeast(1)
        for (index in 0 until labelCount) {
            val text = onValueFormat(x + valueIncrement * index)
            textPaint.getTextBounds(text, 0, text.length, rect)
            getPositionOnRect(valueIncrement, space)
            maxHeight = maxHeight.coerceAtLeast(rect.height().toFloat() + 1)

            val xPos = getPositionOnRect(x + valueIncrement * index, space).toFloat()

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
        if (view.type == ChartType.GROUPED && view.series.size > 1) {
            return ((result - (spacing / 2 * view.series.size)) / view.series.size).coerceAtLeast(1.0)
        }

        return result
    }

    override fun getDataWidth(): Double {
        // TODO: handle the auto dataWidth better
        return dataWidth ?: (getMax() - getMin() + 1)
    }
}
