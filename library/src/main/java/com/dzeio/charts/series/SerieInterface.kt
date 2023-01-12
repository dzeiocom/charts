package com.dzeio.charts.series

import android.graphics.Canvas
import android.graphics.RectF
import com.dzeio.charts.Entry
import com.dzeio.charts.axis.YAxisPosition

sealed interface SerieInterface {

    /**
     * location of the Y axis
     */
    var yAxisPosition: YAxisPosition

    /**
     * filter out out of display entries
     *
     * @return the list of entries displayed
     */
    fun getDisplayedEntries(): ArrayList<Entry>

    /**
     * set the entries for the list
     */
    var entries: ArrayList<Entry>

    /**
     * Change how the value is displayed for each elements
     */
    var formatValue: (entry: Entry) -> String


    /**
     * function that display the graph
     *
     * @param canvas the canvas to draw on
     * @param drawableSpace the space you are allowed to draw on
     * @return do the serie need to be drawn again or not
     */
    fun onDraw(canvas: Canvas, drawableSpace: RectF): Boolean

    /**
     * run when manually refreshing the system
     *
     * this is where the pre-logic is handled to make [onDraw] quicker
     */
    fun refresh()
}
