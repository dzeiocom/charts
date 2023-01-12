package com.dzeio.charts

import com.dzeio.charts.series.SerieInterface

/**
 * A Base entry for any charts
 */
data class Entry(
    var x: Double,
    var y: Float,
    var color: Int? = null,
    var serie: SerieInterface? = null
)
