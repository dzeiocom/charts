package com.dzeio.charts

import com.dzeio.charts.axis.XAxisInterface
import com.dzeio.charts.axis.YAxisInterface
import com.dzeio.charts.series.SerieInterface

interface ChartViewInterface {

    /**
     * Chart Type
     */
    var type: ChartType

    /**
     * Make the whole view in debug mode
     *
     * add debug texts, logs, and more
     */
    var debug: Boolean

    /**
     * the padding inside the view
     */
    var padding: Float

    /**
     * Hold metadata about the X axis
     */
    val xAxis: XAxisInterface

    /**
     * Hold informations about the Y axis
     */
    val yAxis: YAxisInterface

    /**
     * handle the series
     */
    var series: ArrayList<SerieInterface>

    /**
     * refresh and run pre-display logic the chart
     *
     * this function should be run if you change parameters in the view
     */
    fun refresh()

    /**
     * @return the whole dataset (sorted and cleaned up of dupps)
     */
    fun getDataset(): ArrayList<Entry>
}