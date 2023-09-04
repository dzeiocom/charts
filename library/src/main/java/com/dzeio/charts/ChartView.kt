package com.dzeio.charts

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dzeio.charts.axis.XAxis
import com.dzeio.charts.axis.YAxis
import com.dzeio.charts.components.Animation
import com.dzeio.charts.components.Annotation
import com.dzeio.charts.components.ChartScroll
import com.dzeio.charts.series.SerieInterface

class ChartView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs), ChartViewInterface {

    private companion object {
        const val TAG = "ChartView"
    }

    override val animator: Animation = Animation()

    override val annotator: Annotation = Annotation(this)

    override var type: ChartType = ChartType.BASIC

    override var debug: Boolean = false

    override val xAxis = XAxis(this)

    override val yAxis = YAxis(this)

    override var series: ArrayList<SerieInterface> = arrayListOf()

    override var padding: Float = 8f

    private var runUpdates = true

    init {
        viewTreeObserver.addOnScrollChangedListener {
            val actualPosition = Rect()
            val isGlobalVisible = getGlobalVisibleRect(actualPosition)
            val screen = Rect(
                0,
                0,
                Resources.getSystem().displayMetrics.widthPixels,
                Resources.getSystem().displayMetrics.heightPixels
            )
            val displayed = isShown && isGlobalVisible && Rect.intersects(actualPosition, screen)

            if (!displayed) {
                if (!runUpdates) {
                    return@addOnScrollChangedListener
                }
                for (serie in series) {
                    serie.resetAnimation()
                    refresh()
                    runUpdates = false
                }
            } else if (!runUpdates) {
                runUpdates = true
                refresh()
            } else if (annotator.entry != null && annotator.hideOnScroll) {
                annotator.entry = null
                refresh()
            }
        }
    }

    private val scroller = ChartScroll(this).apply {
        var lastMovementX = 0.0
        var lastMovementY = 0f
        setOnChartMoved { movementX, movementY ->
            if (getDataset().isEmpty()) {
                return@setOnChartMoved
            }
            if (xAxis.scrollEnabled) {
                val currentXMax = xAxis.getCurrentMax()
                val currentXMin = xAxis.getCurrentMin()
                val change = (movementX.toDouble() - lastMovementX) * (currentXMax - currentXMin) / width
                xAxis.setCurrent(
                    currentXMin + change,
                    currentXMax + change
                )
                lastMovementX = movementX.toDouble()
            }

            if (yAxis.scrollEnabled) {
                val currentYMax = yAxis.getCurrentMax()
                val currentYMin = yAxis.getCurrentMin()
                val change = (movementY - lastMovementY) * (currentYMax - currentYMin) / height
                yAxis.setCurrent(currentYMin + change, currentYMax + change)
                lastMovementY = movementY
            }

            refresh()
        }
        setOnToggleScroll {
            // note: true == no scroll
            parent?.requestDisallowInterceptTouchEvent(
                !it && (yAxis.scrollEnabled || xAxis.scrollEnabled || xAxis.zoomEnabled || yAxis.zoomEnabled)
            )
        }
        setOnChartClick { x, y ->
            if (getDataset().isEmpty()) {
                return@setOnChartClick
            }
            // Log.d("Chart clicked at", "$x, $y")
            val dataset = series.map { it.getDisplayedEntries() }.reduce { acc, entries ->
                acc.addAll(entries)
                return@reduce acc
            }
            val entrySize = xAxis.getEntryWidth(seriesRect)
            val clickPos = x
            var entryFound = false
            for (entry in dataset) {
                val posX = xAxis.getPositionOnRect(entry, seriesRect)
                // Log.d("pouet", "$posX, $clickPos, ${posX + entrySize}")
                if (posX <= clickPos && clickPos <= posX + entrySize) {
                    // Log.d("entry found!", "$entry")
                    if (annotator.entry == entry) {
                        annotator.entry = null
                    } else {
                        annotator.entry = entry
                    }
                    entryFound = true
                    break
                }
            }
            if (!entryFound && annotator.entry != null) {
                annotator.entry = null
            }
            refresh()
        }
        setOnZoomChanged { scaleX, scaleY ->
            val animationEnabled = animator.enabled
            if (animationEnabled) animator.enabled = false
            Log.d(TAG, "Zoom: Start")
            val factor = 0.05f
            if (scaleX != 0f && xAxis.zoomEnabled && xAxis.scrollEnabled) {
                Log.d(TAG, "ScaleX: scaleX")
                Log.d(TAG, "ScaleX: $scaleX")
                val xDistance = xAxis.getCurrentMax() - xAxis.getCurrentMin()
                Log.d(TAG, "ScaleX: xDistance * (scaleX * factor)")
                Log.d(TAG, "ScaleX: $xDistance * ${(scaleX * factor)}")
                val xTransformer = xDistance * (scaleX * factor)
                Log.d(TAG, "ScaleX: xTransformer")
                Log.d(TAG, "ScaleX: $xTransformer")
                val xMin = xAxis.getCurrentMin() - xTransformer
                val dataWidth = xAxis.getDataWidth() + xTransformer
                Log.d(TAG, "ScaleX: previousXMin, previousDataWidth")
                Log.d(TAG, "ScaleX: ${xAxis.getCurrentMin()}, ${xAxis.getDataWidth()}")
                Log.d(TAG, "ScaleX: xMin, dataWidth")
                Log.d(TAG, "ScaleX: $xMin, $dataWidth")
                xAxis.setCurrentMin(xMin)
                xAxis.dataWidth = dataWidth
            }
            if (scaleY != 0f && yAxis.zoomEnabled && yAxis.scrollEnabled) {
                Log.d(TAG, "ScaleY: scaleY")
                Log.d(TAG, "ScaleY: $scaleY")
                val yDistance = yAxis.getCurrentMax() - yAxis.getCurrentMin()
                Log.d(TAG, "ScaleY: yDistance * (scaleY * factor)")
                Log.d(TAG, "ScaleY: $yDistance * ${(scaleY * factor)}")
                val yTransformer = yDistance * (scaleY * factor)
                Log.d(TAG, "ScaleY: yTransformer")
                Log.d(TAG, "ScaleY: $yTransformer")
                val yMin = yAxis.getCurrentMin() - yTransformer
                val yMax = yAxis.getCurrentMax() + yTransformer
                Log.d(TAG, "ScaleY: previousYMin, previousYMax")
                Log.d(TAG, "ScaleY: ${yAxis.getCurrentMin()}, ${yAxis.getCurrentMax()}")
                Log.d(TAG, "ScaleY: yMin, yMax")
                Log.d(TAG, "ScaleY: $yMin, $yMax")
                yAxis.setCurrentMin(yMin)
                yAxis.setCurrentMax(yMax.coerceAtLeast(yMin + 1f))
            }
//            Log.d(TAG, "Zoom: Done")
//            Log.d(TAG, "[after] : tr: $scaleX, $scaleY, xMin: $xMin, xMax: $xMax, yMin: $yMin, yMax: $yMax")
//            zoom = (it * 1.2).toFloat()
            refresh()
            if (animationEnabled) animator.enabled = true
        }
    }

    // rect used for calculations
    private val rect = RectF()
    private val seriesRect = RectF()

    // stroke used while in debug
    private val debugStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.parseColor("#654321")
    }

    override fun refresh() {
        if (!runUpdates) {
            return
        }
        // run Axis logics
        xAxis.refresh()
        yAxis.refresh()

        // run series logic
        for (serie in series) {
            serie.refresh()
        }

        // invalidate the view
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // don't draw anything if everything is empty
        if (!runUpdates || series.isEmpty() || series.maxOf { it.entries.size } == 0) {
            super.onDraw(canvas)
            return
        }

        if (debug) {
            // draw corners
            canvas.drawRect(
                rect.apply {
                    set(
                        padding / 2,
                        padding / 2,
                        width.toFloat() - padding / 2,
                        height.toFloat() - padding / 2
                    )
                },
                debugStrokePaint
            )
        }

        var bottom = xAxis.getHeight() ?: 0f

        // right distance from the yAxis
        val rightDistance = yAxis.onDraw(
            canvas,
            rect.apply {
                set(padding, padding, width.toFloat() - padding, height.toFloat() - bottom - padding)
            }
        )

        bottom = xAxis.onDraw(
            canvas,
            rect.apply {
                set(padding, 0f, width.toFloat() - rightDistance - padding, height.toFloat() - padding)
            }
        )

        // chart draw rectangle
        seriesRect.apply {
            set(
                padding,
                padding,
                width.toFloat() - padding - rightDistance,
                height - bottom - padding
            )
        }

        var needRedraw = false
        if (type == ChartType.STACKED) {
            for (serie in series.reversed()) {
                val tmp = serie.onDraw(canvas, seriesRect)
                if (tmp) {
                    needRedraw = true
                }
            }
        } else {
            for (serie in series) {
                val tmp = serie.onDraw(canvas, seriesRect)
                if (tmp) {
                    needRedraw = true
                }
            }
        }

        annotator.onDraw(canvas, seriesRect)

        if (needRedraw && runUpdates) {
            postDelayed({ this.invalidate() }, animator.getDelay().toLong())
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
