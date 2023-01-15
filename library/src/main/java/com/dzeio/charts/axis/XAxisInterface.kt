package com.dzeio.charts.axis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.dzeio.charts.Entry

sealed interface XAxisInterface {

    /**
     * enable/disable the display of the xAxis
     */
    var enabled: Boolean

    /**
     * set X position
     */
    var x: Double

    /**
     * the "width" of the graph
     *
     * if not set it will be `XMax - XMin`
     *
     * ex: to display a 7 days graph history with x values being timestamp in secs, use 7*24*60*60
     */
    var dataWidth: Double?

    /**
     * text Paint
     */
    val textPaint: Paint

    /**
     * indicate the number of labels displayed
     */
    var labelCount: Int

    /**
     * is Horizontal Scrolling enabled
     */
    var scrollEnabled: Boolean

    /**
     * run when manually refreshing the system
     *
     * this is where the pre-logic is handled to make [onDraw] quicker
     */
    fun refresh()

    /**
     * get the entry position on the rect
     *
     * @return the left side of the position of the entry
     */
    fun getPositionOnRect(entry: Entry, drawableSpace: RectF): Double

    /**
     * get the maximum the X can get to
     */
    fun getXMax(): Double

    /**
     * get the minimum the X can get to
     */
    fun getXMin(): Double

    /**
     * get the size of an entry in the graph
     *
     * @return the size in [drawableSpace] px
     */
    fun getEntryWidth(drawableSpace: RectF): Double

    /**
     * return the currently used dataWidth
     */
    fun getDataWidth(): Double

    /**
     * onDraw event that will draw the XAxis
     *
     * @param canvas the canvas to draw on
     * @param space the space where it is allowed to draw
     *
     * @return the final height of the XAxis
     */
    fun onDraw(canvas: Canvas, space: RectF): Float

    /**
     * return the height of the XAxis (available after first draw)
     */
    fun getHeight(): Float?
}
