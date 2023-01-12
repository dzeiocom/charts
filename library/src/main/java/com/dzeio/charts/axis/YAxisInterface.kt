package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.dzeio.charts.Entry

sealed interface YAxisInterface {

    /**
     * whether or not this axis is displayed
     */
    var enabled: Boolean

    /**
     * get/set the number of label of this Y axis
     *
     * the first/last labels are at the bottom/top of the chart
     */
    var labelCount: Int

    /**
     * text label paint
     */
    val textLabel: Paint

    /**
     * paint for the lines
     */
    val linePaint: Paint

    /**
     * Goal line paint
     */
    val goalLinePaint: Paint

    /**
     * is vertical scrolling enabled
     */
    var scrollEnabled: Boolean

    /**
     * do the Zero line gets drawn?
     */
    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    var drawZeroLine: Boolean

    /**
     * run when manually refreshing the system
     *
     * this is where the pre-logic is handled to make [onDraw] quicker
     */
    fun refresh()

    /**
     * override Y minimum
     *
     * @param yMin is set the min will ba at the value, if null it is calculated
     */
    fun setYMin(yMin: Float?): YAxisInterface

    /**
     * override Y maximum
     *
     * @param yMax is set the max will ba at the value, if null it is calculated
     */
    fun setYMax(yMax: Float?): YAxisInterface

    /**
     * get Y maximum
     *
     * @return the maximum value Y can get (for displayed values)
     */
    fun getYMax(): Float

    /**
     * get Y minimum
     *
     * @return the minimum value Y can get (for displayed values)
     */
    fun getYMin(): Float

    /**
     * function that draw our legend
     *
     * @param canvas the canvas to draw on
     * @param space the space where it is allowed to draw on
     *
     * @return the width of the sidebar
     */
    fun onDraw(canvas: Canvas, space: RectF): Float

    /**
     * Add a Goal line
     *
     */
    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    fun setGoalLine(height: Float?)

    /**
     * add a line on the Chart
     *
     * @param y the Y position of the line
     * @param paint the Paint of the line if you want to have a custom one
     */
    fun addLine(y: Float, line: Line)

    /**
     * remove a line one the specified position
     *
     * @param y the Y position of the line
     */
    fun removeLine(y: Float)

    /**
     * get the position of an [entry] Y position in the [drawableSpace]
     *
     * if the chart type is stacked it will automatically calculate the position depending on it
     *
     * @param entry the entry to search to position
     * @param drawableSpace the space in which it should appear
     * @return the float position (can be out of the [drawableSpace])
     */
    fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Float

    /**
     * get the position of a [point] in the [drawableSpace]
     *
     * @param point the point to search to position
     * @param drawableSpace the space in which it should appear
     * @return the float position (can be out of the [drawableSpace])
     */
    fun getPositionOnRect(point: Float, drawableSpace: RectF): Float
}
