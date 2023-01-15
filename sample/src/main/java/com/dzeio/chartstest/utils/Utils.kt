package com.dzeio.chartstest.utils

import com.dzeio.charts.Entry
import kotlin.random.Random.Default.nextInt

object Utils {
    fun generateRandomDataset(size: Int = 100, min: Int = 0, max: Int = 100, xStep: Int = 1): ArrayList<Entry> {
        val dataset: ArrayList<Entry> = ArrayList()
        for (i in 0 until size) {
            dataset.add(
                Entry(
                    (i * xStep).toDouble(),
                    nextInt(min, max).toFloat()
                )
            )
        }
        return dataset
    }
}
