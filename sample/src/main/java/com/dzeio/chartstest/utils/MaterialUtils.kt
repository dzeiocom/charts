package com.dzeio.chartstest.utils

import android.view.View
import com.dzeio.charts.ChartView
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.series.BarSerie
import com.dzeio.charts.series.LineSerie
import com.google.android.material.color.MaterialColors


object MaterialUtils {
    fun materielTheme(chart: ChartViewInterface, view: View) {

        chart.yAxis.apply {
            textLabel.color =
                MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimaryContainer)
            linePaint.color =
                MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimaryContainer)
            goalLinePaint.color =
                MaterialColors.getColor(view, com.google.android.material.R.attr.colorError)
        }

        chart.xAxis.textPaint.color =
            MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimaryContainer)
            chart.annotator.apply {
                backgroundPaint.color = MaterialColors.getColor(view, com.google.android.material.R.attr.colorBackgroundFloating)
                titlePaint.color = MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimaryContainer)
                subTitlePaint.color = MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimaryContainer)
            }

        for (serie in chart.series) {
            if (serie is BarSerie) {
                serie.apply {
                    barPaint.color =
                        MaterialColors.getColor(view, com.google.android.material.R.attr.colorPrimary)
                    textPaint.color =
                        MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimary)
                    textExternalPaint.color =
                        MaterialColors.getColor(view, com.google.android.material.R.attr.colorPrimary)

                }
            } else if (serie is LineSerie) {
                serie.apply {
                    linePaint.color =
                        MaterialColors.getColor(view, com.google.android.material.R.attr.colorPrimary)
                    textPaint.color =
                        MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimary)

                }
            }
        }
    }
}
