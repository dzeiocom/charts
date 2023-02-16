package com.dzeio.charts.axis

import android.graphics.Paint

sealed interface YAxisInterface : AxisInterface<Float> {

    /**
     * Goal line paint
     */
    val goalLinePaint: Paint

    /**
     * do the Zero line gets drawn?
     */
    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    var drawZeroLine: Boolean

    /**
     * Add a Goal line
     *
     */
    @Deprecated("use the new global function", ReplaceWith("YAxisInterface.addLine"))
    fun setGoalLine(height: Float?)

    /**
     * add a line on the Chart
     *
     * @param y the line's Y position
     * @param line The line's settings
     */
    fun addLine(y: Float, line: Line)

    /**
     * add a line on the Chart
     *
     * @param y the Y position of the line
     */
    fun addLine(y: Float)

    /**
     * remove a line one the specified position
     *
     * @param y the Y position of the line
     */
    fun removeLine(y: Float)

    /**
     * Remove every lines
     */
    fun clearLines()
}
