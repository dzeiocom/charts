package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.dzeio.charts.ChartView
import com.dzeio.charts.utils.drawRoundRect

class BarSerie(
    private val view: ChartView
) : BaseSerie(view) {

    private companion object {
        const val TAG = "Charts/BarSerie"
    }

    init {
        view.series.add(this)
    }

    val barPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#123456")
    }

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FC496D")
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    var textExternalPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val rect = Rect()

    private var entriesCurrentY: HashMap<Double, AnimationProgress> = hashMapOf()

    override fun onDraw(canvas: Canvas, drawableSpace: RectF): Boolean {
        val displayedEntries = getDisplayedEntries()
        val barWidth = view.xAxis.getEntryWidth(drawableSpace).toFloat()

        val zero = view.yAxis.getPositionOnRect(0f, drawableSpace)

        var needUpdate = false

        val iterator = entriesCurrentY.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next().key
            if (displayedEntries.find { it.x == key } == null) iterator.remove()
        }

        for (entry in displayedEntries) {
            if (entriesCurrentY[entry.x] == null) {
                entriesCurrentY[entry.x] = AnimationProgress(zero)
            }

            // calculated height in percent from 0 to 100
            var top = view.yAxis.getPositionOnRect(entry, drawableSpace)
            var posX = view.xAxis.getPositionOnRect(
                entry,
                drawableSpace
            ).toFloat()

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

            val right = (posX + barWidth).coerceAtMost(drawableSpace.right)

            if (posX > right) {
                continue
            } else if (posX < drawableSpace.left) {
                posX = drawableSpace.left
            }

            if (right < drawableSpace.left) {
                continue
            }

            // handle color recoloration
            val paint = Paint(barPaint)

            if (entry.color != null) {
                paint.color = entry.color!!
            }

            if (entry.y < 0) {
                canvas.drawRoundRect(
                    posX,
                    zero,
                    right,
                    top,
                    0f,
                    0f,
                    32f,
                    32f,
                    paint
                )
            } else {
                canvas.drawRoundRect(
                    posX,
                    top,
                    right,
                    zero,
                    32f,
                    32f,
                    0f,
                    0f,
                    paint
                )
            }

            // handle text display
            val text = view.yAxis.onValueFormat(entry.y)

            textPaint.getTextBounds(text, 0, text.length, rect)

            val textLeft = (posX + barWidth / 2)

            if (
                // handle right side
                textLeft + rect.width() / 2 > right ||
                // handle left sie
                textLeft - rect.width() / 2 < drawableSpace.left
            ) {
                continue
            }

            val doDisplayIn =
                rect.height() < drawableSpace.bottom - top &&
                rect.width() < barWidth

            var textY = if (doDisplayIn) top + rect.height() + 16f else top - 16f

            if (textY < drawableSpace.top + rect.height()) {
                textY = drawableSpace.top + rect.height()
            }


            canvas.drawText(
                text,
                textLeft,
                textY,
                if (doDisplayIn) textPaint else textExternalPaint
            )
        }

        return needUpdate
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }
}
