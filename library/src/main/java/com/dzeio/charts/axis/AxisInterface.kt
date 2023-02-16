package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.dzeio.charts.Entry

interface AxisInterface<T> {
    /**
     * enable/disable the display of the xAxis
     */
    var enabled: Boolean

    /**
     * paint for the lines
     */
    val linePaint: Paint

    /**
     * text Paint
     */
    val textPaint: Paint

    /**
     * if global limits are sets to true [getCurrentMin] while never be `<` to [getMin] and same for max
     */
    var keepGlobalLimits: Boolean

    /**
     * indicate the number of labels displayed
     */
    var labelCount: Int

    var onValueFormat: (value: T) -> String

    /**
     * is Horizontal Scrolling enabled
     */
    var scrollEnabled: Boolean

    /**
     * is horizontal zooming enabled
     */
    var zoomEnabled: Boolean

    /**
     * get the entry position on the rect
     *
     * @param entry the entry to place
     * @param drawableSpace the space it should go into
     *
     * @return the position of the entry on the defined axis
     */
    fun getPositionOnRect(entry: Entry, drawableSpace: RectF): T

    /**
     * get the entry position on the rect
     *
     * @param position the position on your point
     * @param drawableSpace the space it should go into
     *
     * @return the position of the entry on the defined axis
     */
    fun getPositionOnRect(position: T, drawableSpace: RectF): T

    /**
     * get the current/displayed minimum (inclusive)
     */
    fun getCurrentMin(): T

    /**
     * set the new min/max of the element
     *
     * if one or both can't be set to the value both won't be set
     */
    fun setCurrent(min: T?, max: T?): Boolean

    /**
     * set the new minimum displayed value of the axis (inclusive)
     */
    fun setCurrentMin(value: T?)

    /**
     * set the new maximum displayed value of the axis (inclusive)
     */
    fun setCurrentMax(value: T?)

    /**
     * get the current/displayed maximum (inclusive)
     */
    fun getCurrentMax(): T

    /**
     * get the maximum the axis can get to
     */
    fun getMax(): T

    /**
     * get the minimum value the axis can get to
     */
    fun getMin(): T

    /**
     * run when manually refreshing the system
     *
     * this is where the pre-logic is handled to make [onDraw] quicker
     */
    fun refresh()

    /**
     * function that draw our legend
     *
     * @param canvas the canvas to draw on
     * @param space the space where it is allowed to draw on
     *
     * @return the the width or height of the axis
     */
    fun onDraw(canvas: Canvas, space: RectF): Float
}
