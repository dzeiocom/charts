package com.dzeio.charts.components

import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.view.View

/**
 * Class handling the scroll/zoom for the library
 */
class ChartScroll(view: View) {

    /**
     * Enabled the zoom/unzoom of datas
     */
    var zoomEnabled = true

    /**
     * Enable the horizontal scroll feature
     */
    var scrollEnabled = true

    // The ‘active pointer’ is the one currently moving our object.
    private var activePointerId = INVALID_POINTER_ID

    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f

    private var posX: Float = 0f
    private var posY: Float = 0f

    private var lastZoom: Float = 100f
    private var currentZoom: Float = 0f

    private var onChartMoved: ((movementX: Float, movementY: Float) -> Unit)? = null
    fun setOnChartMoved(fn: (movementX: Float, movementY: Float) -> Unit) {
        onChartMoved = fn
    }

    private var onZoomChanged: ((scale: Float) -> Unit)? = null

    /**
     * @param fn.scale Float starting from 100%
     *
     * 99-%  zoom out,
     * 101+% zoom in
     */
    fun setOnZoomChanged(fn: (scale: Float) -> Unit) {
        onZoomChanged = fn
    }

    private val scaleGestureDetector = ScaleGestureDetector(
        view.context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (currentZoom != detector.scaleFactor) {
                    currentZoom = detector.scaleFactor
                    onZoomChanged?.invoke(lastZoom + -currentZoom + 1)
                }

                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)

                lastZoom += -currentZoom + 1
            }
        }
    )

    /**
     * Code mostly stolen from https://developer.android.com/training/gestures/scale#drag
     */
    fun onTouchEvent(ev: MotionEvent): Boolean {

        if (zoomEnabled) {
            scaleGestureDetector.onTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                onToggleScroll?.invoke(false)
                ev.actionIndex.also { pointerIndex ->
                    // Remember where we started (for dragging)
                    lastTouchX = ev.getX(pointerIndex)
                    lastTouchY = ev.getY(pointerIndex)
                }

                // Save the ID of this pointer (for dragging)
                activePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val (x: Float, y: Float) =
                    ev.findPointerIndex(activePointerId).let { pointerIndex ->
                        // Calculate the distance moved
                        ev.getX(pointerIndex) to ev.getY(pointerIndex)
                    }

                posX += x - lastTouchX
                posY += y - lastTouchY

                if (scrollEnabled) {
                    onChartMoved?.invoke(-posX, posY)
                }

                // Remember this touch position for the next move event
                lastTouchX = x
                lastTouchY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onToggleScroll?.invoke(true)
                activePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onToggleScroll?.invoke(true)
                ev.actionIndex.also { pointerIndex ->
                    ev.getPointerId(pointerIndex)
                        .takeIf { it == activePointerId }
                        ?.run {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            val newPointerIndex = if (pointerIndex == 0) 1 else 0
                            lastTouchX = ev.getX(newPointerIndex)
                            lastTouchY = ev.getY(newPointerIndex)
                            activePointerId = ev.getPointerId(newPointerIndex)
                        }
                }
            }
        }
        return true
    }

    private var onToggleScroll: ((Boolean) -> Unit)? = null

    /**
     * @param ev if input is false disable scroll
     */
    fun setOnToggleScroll(ev: (Boolean) -> Unit) {
        onToggleScroll = ev
    }
}
