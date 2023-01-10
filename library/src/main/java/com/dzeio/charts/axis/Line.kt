package com.dzeio.charts.axis

import android.graphics.Paint

data class Line (
    /**
     * is the bar dotted
     */
    var dotted: Boolean = false,

    /**
     * Custom Paint
     */
    var paint: Paint? = null
)