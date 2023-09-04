package com.dzeio.charts.axis

import android.graphics.RectF

sealed interface XAxisInterface : AxisInterface<Double> {

    /**
     * the "width" of the graph
     *
     * if not set it will be `XMax - XMin`
     *
     * ex: to display a 7 days graph history with x values being timestamp in secs, use 7*24*60*60
     */
    var dataWidth: Double?

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
     * return the height of the XAxis (available after first draw)
     */
    fun getHeight(): Float?
}
