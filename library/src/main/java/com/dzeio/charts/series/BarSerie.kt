package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.utils.drawRoundRect

class BarSerie(
    private val view: ChartViewInterface
) : BaseSerie(view) {

    private companion object {
        const val TAG = "Charts/BarSerie"
    }

    init {
        view.series.add(this)
    }

    val barPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#64B5F6")
    }

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
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
            .coerceIn(drawableSpace.top, drawableSpace.bottom)

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
                .coerceIn(drawableSpace.top, drawableSpace.bottom)
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

            var height: Float
            if (entry.y < 0) {
                height = top - zero
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
                height = zero - top
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

            if (barWidth < rect.width()) {
                continue
            }

            // text center X
            val textX = (posX + barWidth / 2)

            val doDisplayIn =
                rect.height() + 32f < height

            var textY = if (doDisplayIn) top + rect.height() + 16f else top - 16f
            if (entry.y < 0f) textY = if (doDisplayIn) top - 16f else top + rect.height() + 16f

            if (textY < drawableSpace.top + rect.height()) {
                textY = drawableSpace.top + rect.height()
            }

            if (
                // check text not overflowing on left side
                textX + barWidth / 2 > right ||
                // check text not overflowing on right side
                textX + barWidth / 2 > drawableSpace.right
            ) {
                continue
            }

            canvas.drawText(
                text,
                textX,
                textY,
                if (doDisplayIn) textPaint else textExternalPaint
            )
        }

        return needUpdate
    }

    override fun refresh() {
//        TODO("Not yet implemented")
    }

    override fun resetAnimation() {
        entriesCurrentY.clear()
    }
}
