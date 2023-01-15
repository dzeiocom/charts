package com.dzeio.charts.components

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import com.dzeio.charts.ChartView
import com.dzeio.charts.Entry

class Annotation(
    private val view: ChartView
) {

    val backgroundPaint: Paint = Paint().apply {
        color = Color.WHITE
        setShadowLayer(12.0f, 0.0f, 0.0f, Color.GRAY)
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, this)
    }

    var annotationSubTitleFormat: (entry: Entry) -> String = { it.y.toString() }
    var annotationTitleFormat: (entry: Entry) -> String = { it.x.toString() }

    var enabled = true

    var entry: Entry? = null

    var hideOnScroll = true

    var orientation = Orientation.HORIZONTAL

    var padding: Float = 32f

    private val rect: Rect = Rect()

    val subTitlePaint: Paint = Paint().apply {
        textSize = 48.0f
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    val titlePaint: Paint = Paint().apply {
        textSize = 64.0f
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    /* compiled from: Annotation.kt */
    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private companion object {
        val TAG = Annotation::class.java.simpleName
    }

    fun onDraw(canvas: Canvas, space: RectF) {
        if (entry == null || !enabled) {
            return
        }

        val xAxis = view.xAxis
        val yAxis = view.yAxis

        val x = xAxis.getPositionOnRect(entry!!, space)

        val y = yAxis.getPositionOnRect(entry!!, space)
            .coerceIn(space.top, space.bottom)

        val xCenter = view.xAxis.getEntryWidth(space) / 2.0 + x

        val xText = annotationSubTitleFormat.invoke(entry!!)
        val yText = annotationTitleFormat.invoke(entry!!)

        titlePaint.getTextBounds(yText, 0, yText.length, rect)
        val yTextWidth = rect.width()
        val yTextHeight = rect.height()

        subTitlePaint.getTextBounds(xText, 0, xText.length, rect)
        val xTextWidth = rect.width()
        val xTextHeight = rect.height()

        var contentWitdh = Math.max(yTextWidth, xTextWidth)
        if (orientation == Orientation.HORIZONTAL) {
            contentWitdh = (yTextWidth.toFloat() + padding + xTextWidth.toFloat()).toInt()
        }

        var contentHeight = (yTextHeight.toFloat() + padding + xTextHeight.toFloat()).toInt()
        if (orientation == Orientation.HORIZONTAL) {
            contentHeight = Math.max(yTextHeight, xTextHeight)
        }

        val finalRect = RectF(
            (xCenter - (contentWitdh / 2).toDouble()).toFloat() - padding,
            space.top,
            ((contentWitdh / 2).toDouble() + xCenter).toFloat() + padding,
            padding * 2f + contentHeight.toFloat()
        )

        var reverseArrow = false
        if (y < finalRect.height() + padding * 2f) {
            finalRect.top += padding * 3f + y
            finalRect.bottom += padding * 3f + y
            reverseArrow = true
        }

        if (finalRect.left < space.left) {
            finalRect.right += space.left - finalRect.left
            finalRect.left = space.left
        } else if (finalRect.right > space.right) {
            finalRect.left -= finalRect.right - space.right
            finalRect.right = space.right
        }

        val twoPointsY = if (reverseArrow) finalRect.top + 1f else finalRect.bottom

        val p1 = PointF((xCenter - padding.toDouble()).toFloat(), twoPointsY)
        val p2 = PointF((padding.toDouble() + xCenter).toFloat(), twoPointsY)
        val p3 = PointF(xCenter.toFloat(), if (reverseArrow) { twoPointsY - padding } else { padding + twoPointsY })

        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(p1.x, p1.y)
        path.lineTo(p2.x, p2.y)
        path.lineTo(p3.x, p3.y)
        path.close()
        canvas.drawRoundRect(finalRect, 16.0f, 16.0f, backgroundPaint)
        canvas.drawPath(path, Paint(backgroundPaint).apply { clearShadowLayer() })

        if (orientation == Orientation.VERTICAL) {
            canvas.drawText(
                yText,
                finalRect.left + padding + (contentWitdh / 2).toFloat(),
                finalRect.top + padding + yTextHeight.toFloat(),
                titlePaint
            )

            canvas.drawText(
                xText,
                finalRect.left + padding + (contentWitdh / 2).toFloat(),
                finalRect.top + padding + yTextHeight.toFloat() + padding + xTextHeight.toFloat(),
                subTitlePaint
            )
        } else {
            val left = finalRect.left + padding
            canvas.drawText(
                yText,
                left + ((contentWitdh - xTextWidth).toFloat() - padding) / 2f,
                finalRect.top + padding + (contentHeight.toFloat() + padding) / 2f,
                titlePaint
            )

            canvas.drawText(
                xText,
                left + yTextWidth.toFloat() + padding + ((contentWitdh - yTextWidth).toFloat() - padding) / 2f,
                finalRect.top + padding + (contentHeight.toFloat() + padding) / 2f,
                subTitlePaint
            )
        }
    }
}
