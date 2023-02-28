package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.dzeio.charts.ChartType
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.Entry
import com.dzeio.charts.utils.drawDottedLine
import kotlin.math.roundToInt
import kotlin.math.sign

class YAxis(
    private val view: ChartViewInterface
) : YAxisInterface {

    override var enabled = true

    override val textLabel = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.LEFT
    }

    override val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }

    override val goalLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        strokeWidth = 4f
    }

    override var onValueFormat: (value: Float) -> String = { it -> it.roundToInt().toString() }

    override var labelCount = 5

    private var min: Float? = null
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

        val max = this.lines.keys.maxOrNull()

        if (view.series.isEmpty()) {
            return max ?: 0f
        }

        if (view.type == ChartType.STACKED) {
            val nList: ArrayList<Float> = arrayListOf()

            for (serie in view.series) {
                val size = serie.entries.size
                while (nList.size < size) {
                    nList.add(0f)
                }
                for (index in 0 until serie.entries.size) {
                    val entry = serie.entries[index]
                    if (sign(entry.y) <= 0f && nList[index] > 0f) {
                        continue
                    } else if (nList[index] < 0f && entry.y > 0f) {
                        nList[index] = entry.y
                        continue
                    }
                    nList[index] += entry.y
                }
            }

            val localMax = nList.maxOf { it }
            if (max == null) return localMax
            return if (localMax > max) localMax else max
        }
        val seriesMax = view.series
            .maxOf { serie ->
                if (serie.getDisplayedEntries().isEmpty()) {
                    return@maxOf 0f
                }
                return@maxOf serie.getDisplayedEntries().maxOf { entry -> entry.y }
            }
        if (max == null) return seriesMax
        return if (seriesMax > max) seriesMax else max
    }

    override fun getYMin(): Float {
        if (min != null) {
            return min!!
        }

        val min = this.lines.keys.minOrNull()

        if (view.series.isEmpty()) {
            return min ?: 0f
        }

        if (view.type == ChartType.STACKED) {
            val nList: ArrayList<Float> = arrayListOf()

            for (serie in view.series) {
                val size = serie.entries.size
                while (nList.size < size) {
                    nList.add(0f)
                }
                for (index in 0 until serie.entries.size) {
                    val entry = serie.entries[index]
                    if (sign(entry.y) >= 0f && nList[index] < 0f) {
                        continue
                    } else if (nList[index] > 0f && entry.y < 0f) {
                        nList[index] = entry.y
                        continue
                    }
                    nList[index] += entry.y
                }
            }

            val localMin = nList.minOf { it }

            if (min == null) return localMin
            return if (localMin < min) localMin else min
        }
        val localMin = view.series
            .minOf { serie ->
                if (serie.getDisplayedEntries().isEmpty()) {
                    return@minOf 0f
                }
                return@minOf serie.getDisplayedEntries().minOf { entry -> entry.y }
            }
        if (min == null) return localMin
        return if (localMin < min) localMin else min
    }

    override fun onDraw(canvas: Canvas, space: RectF): Float {
        if (!enabled) {
            return 0f
        }

        val min = getYMin()
        val max = getYMax() - min
        var maxWidth = 0f

        val valueIncrement = max / (labelCount - 1).coerceAtLeast(1)
        for (index in 0 until labelCount) {
            val value = min + (valueIncrement * index)

            // Ignore line if there is already one on the exact same point
            if (lines.containsKey(value)) {
                continue
            }

            val text = onValueFormat(min + (valueIncrement * index))
            textLabel.getTextBounds(text, 0, text.length, rect)
            maxWidth = maxWidth.coerceAtLeast(rect.width().toFloat())

            val posY = getPositionOnRect(value, space)

            canvas.drawText(
                text,
                space.width() - rect.width().toFloat(),
                (posY + rect.height() / 2).coerceAtLeast(rect.height().toFloat()),
                textLabel
            )
            canvas.drawLine(space.left, posY, space.right - maxWidth - 32f, posY, linePaint)
        }

        for ((y, settings) in lines) {
            val pos = getPositionOnRect(y, space)
            val text = onValueFormat(y)
            textLabel.getTextBounds(text, 0, text.length, rect)
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
            canvas.drawText(
                text,
                space.width() - rect.width().toFloat(),
                (pos + rect.height() / 2).coerceAtLeast(rect.height().toFloat()),
                textLabel
            )
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

    override fun addLine(y: Float) {
        this.addLine(y, Line())
    }

    override fun removeLine(y: Float) {
        lines.remove(y)
    }

    override fun clearLines() {
        lines.clear()
    }

    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    override fun setGoalLine(height: Float?) {
        if (height != null) {
            addLine(height, Line(true))
        }
    }

    override fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Float {
        if (view.type == ChartType.STACKED) {
            val serie = view.series.find { it.entries.contains(entry) }
            val index = view.series.indexOf(serie)
            return getPositionOnRect(entry, drawableSpace, index)
        }
        return getPositionOnRect(entry.y, drawableSpace)
    }

    private fun getPositionOnRect(entry: Entry, drawableSpace: RectF, index: Int): Float {
        if (index > 0) {
            val entry2 = view.series[index - 1].entries.find { it.x == entry.x }
            if (entry2 != null) {
                // make a new """Entry""" containing the new Y
                val isReverse = sign(entry2.y) != sign(entry.y)
                val tmp = Entry(entry.x, if (isReverse) entry.y else entry.y + entry2.y)
                return getPositionOnRect(tmp, drawableSpace, index - 1)
            }
        }
        return getPositionOnRect(entry.y, drawableSpace)
    }

    override fun getPositionOnRect(point: Float, drawableSpace: RectF): Float {
        val min = getYMin()
        val max = getYMax()

        return (1 - (point - min) / (max - min)) *
            drawableSpace.height() +
            drawableSpace.top
    }
}
