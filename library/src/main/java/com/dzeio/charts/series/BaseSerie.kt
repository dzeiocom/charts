package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.RectF
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.Entry
import com.dzeio.charts.axis.YAxisPosition
import kotlin.math.roundToInt

sealed class BaseSerie(
    private val view: ChartViewInterface
) : SerieInterface {

    private companion object {
        const val TAG = "Charts/BaseSerie"
    }

    protected data class AnimationProgress(
        var value: Float,
        var finished: Boolean = false
    )

    override var formatValue: (entry: Entry) -> String = { entry -> entry.y.roundToInt().toString()}

    override var yAxisPosition: YAxisPosition = YAxisPosition.RIGHT

    override var entries: ArrayList<Entry> = arrayListOf()
        set(values) {
            for (value in values) {
                value.serie = this
            }
            field = values
        }

    override fun getDisplayedEntries(): ArrayList<Entry> {
        val minX = view.xAxis.x
        val maxX = minX + view.xAxis.getDataWidth()

        val result: ArrayList<Entry> = arrayListOf()

        var lastIndex = -1
        for (i in 0 until entries.size) {
            val it = entries[i]
            if (it.x in minX..maxX) {
                if (result.size < 2 && i > 0) {
                    result.add(entries[i - 1])
                }
                lastIndex = i
                result.add(it)
            }
        }

        if (lastIndex < entries.size - 1) {
            result.add(entries [lastIndex + 1])
        }

        result.sortBy { it.x }
        return result
    }

    abstract override fun onDraw(canvas: Canvas, drawableSpace: RectF): Boolean
}
