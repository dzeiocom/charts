package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.utils.drawDottedLine
import kotlin.math.roundToInt

class YAxis(
    private val view: ChartViewInterface
) : YAxisInterface {

    override var enabled = true

    override val textLabel = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FC496D")
        textSize = 30f
        textAlign = Paint.Align.LEFT
    }

    override val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
    }

    override val goalLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        strokeWidth = 4f
    }

    var onValueFormat: (value: Float) -> String = { it -> it.roundToInt().toString() }

    override var labelCount = 5

    private var min: Float? = 0f
    private var max: Float? = null

    @Deprecated("use the new global function", replaceWith = ReplaceWith("YAxisInterface.addLine"))
    override var drawZeroLine: Boolean = false
        set(value) {
            addLine(0f, Line())
            field = value
        }

    override var scrollEnabled: Boolean = false

    private val rect = Rect()

    override fun setYMin(yMin: Float?): YAxisInterface {
        min = yMin
        return this
    }

    override fun setYMax(yMax: Float?): YAxisInterface {
        max = yMax
        return this
    }

    override fun getYMax(): Float {
        if (max != null) {
            return max!!
        }
        if (view.series.isEmpty()) {
            return this.lines.keys.maxOrNull() ?: 0f
        }
        val seriesMax = view.series
            .maxOf { serie ->
                if (serie.getDisplayedEntries().isEmpty()) {
                    return@maxOf 0f
                }
                return@maxOf serie.getDisplayedEntries().maxOf { entry -> entry.y }
            }
        return seriesMax
    }

    override fun getYMin(): Float {
        if (min != null) {
            return min!!
        }
        if (view.series.isEmpty()) {
            return this.lines.keys.minOrNull() ?: 0f
        }
        return view.series
            .minOf { serie ->
                if (serie.getDisplayedEntries().isEmpty()) {
                    return@minOf 0f
                }
                return@minOf serie.getDisplayedEntries().minOf { entry -> entry.y }
            }
    }

    override fun onDraw(canvas: Canvas, space: RectF): Float {
        if (!enabled) {
            return 0f
        }

        val min = getYMin()
        val max = getYMax() - min
        val bottom = space.bottom
        var maxWidth = 0f

        val increment = space.height() / (labelCount - 1)
        val valueIncrement = max / (labelCount - 1)
        for (index in 0 until labelCount) {
            val text = onValueFormat(min + (valueIncrement * index))
            textLabel.getTextBounds(text, 0, text.length, rect)
            maxWidth = maxWidth.coerceAtLeast(rect.width().toFloat())

            val posY = bottom - index * increment

            canvas.drawText(
                text,
                space.width() - rect.width().toFloat(),
                (posY + rect.height() / 2).coerceAtLeast(rect.height().toFloat()),
                textLabel
            )
//            canvas.drawDottedLine(0f, posY, canvas.width.toFloat(), posY, 40f, linePaint)
            canvas.drawLine(space.left, posY, space.right - maxWidth - 32f, posY, linePaint)
        }

        for ((y, settings) in lines) {
            val pos = ((1 - y - min / (getYMax() - min)) * space.height() + space.top)
            if (settings.dotted) {
                canvas.drawDottedLine(
                    0f,
                    pos,
                    space.right - maxWidth - 32f,
                    pos,
                    space.right / 20,
                    settings.paint ?: linePaint
                )
            } else {
                canvas.drawLine(
                    0f,
                    pos,
                    space.right - maxWidth - 32f,
                    pos,
                    settings.paint ?: linePaint
                )
            }
        }

        return maxWidth + 32f
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }

    private val lines: HashMap<Float, Line> = hashMapOf()

    override fun addLine(y: Float, line: Line) {
        lines[y] = line
    }

    override fun removeLine(y: Float) {
        lines.remove(y)
    }

    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    override fun setGoalLine(height: Float?) {
        if (height != null) {
            addLine(height, Line(true))
        }
    }
}
