package com.dzeio.charts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dzeio.charts.axis.XAxis
import com.dzeio.charts.axis.YAxis
import com.dzeio.charts.components.ChartScroll
import com.dzeio.charts.series.SerieInterface

class ChartView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs), ChartViewInterface {

    private companion object {
        const val TAG = "Charts/ChartView"
    }

    override var type: ChartType = ChartType.BASIC

    override var debug: Boolean = false

    override val xAxis = XAxis(this)

    override val yAxis = YAxis(this)

    override var series: ArrayList<SerieInterface> = arrayListOf()

    override var padding: Float = 8f

    private val scroller = ChartScroll(this).apply {
        var lastMovementX = 0.0
        var lastMovementY = 0f
        setOnChartMoved { movementX, movementY ->
            if (xAxis.scrollEnabled) {
                xAxis.x += (movementX - lastMovementX) * xAxis.getDataWidth() / width
                lastMovementX = movementX.toDouble()
            }

            if (yAxis.scrollEnabled) {
                val currentYMax = yAxis.getYMax()
                val currentYMin = yAxis.getYMin()
                val change = (movementY - lastMovementY) * (currentYMax - currentYMin) / height
                yAxis.setYMax(currentYMax + change)
                yAxis.setYMin(currentYMin + change)
                lastMovementY = movementY
            }

            refresh()
        }
//        setOnZoomChanged {
//            Log.d(TAG, "New Zoom: $it")
//            zoom = (it * 1.2).toFloat()
//            refresh()
//        }
    }

//    val animator: Runnable = object : Runnable {
//        override fun run() {
//            var needNewFrame = false
//            for (serie in series) {
//                val result = serie.onUpdate()
//                if (result) {
//                    needNewFrame = true
//                }
//            }
//            if (needNewFrame) {
//                postDelayed(this, animation.getDelay().toLong())
//            }
//            invalidate()
//        }
//    }

    // rect used for calculations
    private val rect = RectF()

    // stroke used while in debug
    private val debugStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.parseColor("#654321")
    }

    override fun refresh() {
        // run Axis logics
        xAxis.refresh()
        yAxis.refresh()

        // run series logic
        for (serie in series) {
            serie.refresh()
        }

        // invalidate the view
        invalidate()
//        removeCallbacks(animator)
//        post(animator)
    }

    override fun onDraw(canvas: Canvas) {

        // don't draw anything if everything is empty
        if (series.isEmpty() || series.maxOf { it.entries.size } == 0) {
            super.onDraw(canvas)
            return
        }

        if (debug) {
            // draw corners
            canvas.drawRect(rect.apply {
                set(
                    padding / 2,
                    padding / 2,
                    width.toFloat() - padding / 2,
                    height.toFloat() - padding / 2
                )
            }, debugStrokePaint)
        }


        var bottom = xAxis.getHeight() ?: 0f

        // right distance from the yAxis
        val rightDistance = yAxis.onDraw(canvas, rect.apply {
            set(padding, padding, width.toFloat() - padding, height.toFloat() - bottom - padding)
        })

        bottom = xAxis.onDraw(canvas, rect.apply {
            set(padding, 0f, width.toFloat() - rightDistance - padding, height.toFloat() - padding)
        })

        // chart draw rectangle
        rect.apply {
            set(
                padding,
                padding,
                width.toFloat() - padding - rightDistance,
                height - bottom - padding
            )
        }

        if (type == ChartType.STACKED) {
            for (serie in series.reversed()) {
                serie.onDraw(canvas, rect)
            }
        } else {
            for (serie in series) {
                serie.onDraw(canvas, rect)
            }
        }
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        return scroller.onTouchEvent(event)
    }

    override fun getDataset(): ArrayList<Entry> {
        val data: ArrayList<Entry> = arrayListOf()
        for (serie in series) {
            data.addAll(serie.entries)
        }
        data.sortBy { it.x }
        return data.filterIndexed { index, entry -> data.indexOf(entry) == index } as ArrayList<Entry>
    }
}
