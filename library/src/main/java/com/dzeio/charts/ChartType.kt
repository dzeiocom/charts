package com.dzeio.charts

enum class ChartType {

    /**
     * Basic Chart where items go over the other
     */
    BASIC,

    /**
     * each series are next to each other nicely
     */
    GROUPED,

    /**
     * WILL NOT DO ANYTHING CURRENTLY
     *
     * Each series are stacked over the other one
     */
    STACKED
}