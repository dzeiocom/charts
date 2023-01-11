package com.dzeio.chartstest.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dzeio.charts.ChartType
import com.dzeio.charts.ChartView
import com.dzeio.charts.Entry
import com.dzeio.charts.series.BarSerie
import com.dzeio.charts.series.LineSerie
import com.dzeio.chartstest.databinding.FragmentMainBinding
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt
import kotlin.random.Random

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chartGrouped.apply {
            // setup the Serie
            val serie1 = BarSerie(this)
            val serie2 = BarSerie(this)

            // transform the chart into a grouped chart
            type = ChartType.GROUPED

            // utils function to use Material3 auto colors
            materielTheme(this, requireView())
            serie2.barPaint.color = Color.RED

            // give the serie it's entries
            serie1.entries = generateRandomDataset(10)
            serie2.entries = generateRandomDataset(10)

            // refresh the Chart
            refresh()
        }

        binding.chartLine.apply {
            // setup the Serie
            val serie = LineSerie(this)

            // utils function to use Material3 auto colors
            materielTheme(this, requireView())

            // give the serie its entries
            serie.entries = generateRandomDataset(10)

            // refresh the Chart
            refresh()
        }

        binding.chartBar.apply {
            // setup the Serie
            val serie = BarSerie(this)

            // utils function to use Material3 auto colors
            materielTheme(this, requireView())

            // give the serie its entries
            serie.entries = generateRandomDataset(10)

            // refresh the Chart
            refresh()
        }

        binding.chartCustomization.apply {
            // setup the Series
            val serie1 = BarSerie(this)
            val serie2 = LineSerie(this)

            // utils function to use Material3 auto colors
            materielTheme(this, requireView())

            // give the series their entries
            serie2.entries = generateRandomDataset(20, -50, 50)
            serie1.entries = generateRandomDataset(20, -50, 50).apply {
                for (idx in 0 until size) {
                    val compared = serie2.entries[idx]
                    val toCompare = this[idx]
                    if (compared.y > toCompare.y) {
                        toCompare.color = Color.RED
                    } else {
                        toCompare.color = Color.GREEN
                    }
                }
            }

//            serie1.textExternalPaint = Color.WHITE

            // make the lineSerie red
            serie2.linePaint.color = Color.WHITE

            // strokeWidth also control the points width
            serie2.linePaint.strokeWidth = 10f

            yAxis.apply {
                // Enable vertical scrolling
                scrollEnabled = true

                // change the number of labels
                labelCount = 11

                // change how labels are displayed
                onValueFormat = { "${it.roundToInt()}g"}

                // change labels colors
                textLabel.color = Color.WHITE


                // change line color
                linePaint.color = Color.WHITE

                // change the min/max high
                setYMin(-20f)
                setYMax(20f)
            }

            xAxis.apply {
                // Enable horizontal scrolling
                scrollEnabled = true

                // set the width of the datas
                dataWidth = 10.0

                // change the number of labels displayed
                labelCount = 5

                // change the spacing between values (it can be overriden if size to to small)
                spacing = 8.0

                // set the offset in data (use with [dataWidth])
                x = 5.0
            }

            // refresh the Chart
            refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Generate a random dataset
     */
    private fun generateRandomDataset(size: Int = 100, min: Int = 0, max: Int = 100): ArrayList<Entry> {
        val dataset: ArrayList<Entry> = arrayListOf()

        for (i in 0 .. size) {
            dataset.add(Entry(
                i.toDouble(),
                Random.nextInt(min, max).toFloat()
            ))
        }

        return dataset
    }

    /**
     * Apply Material3 theme to a [ChartView]
     */
    private fun materielTheme(chart: ChartView, view: View) {

        chart.apply {
            yAxis.apply {
                textLabel.color = MaterialColors.getColor(
                    view,
                    com.google.android.material.R.attr.colorOnPrimaryContainer
                )
                linePaint.color = MaterialColors.getColor(
                    view,
                    com.google.android.material.R.attr.colorOnPrimaryContainer
                )
                goalLinePaint.color = MaterialColors.getColor(
                    view,
                    com.google.android.material.R.attr.colorError
                )
            }

            xAxis.apply {
                textPaint.color = MaterialColors.getColor(
                    view,
                    com.google.android.material.R.attr.colorOnPrimaryContainer
                )
            }

            for (serie in series) {
                if (serie is BarSerie) {
                    serie.apply {
                        barPaint.color = MaterialColors.getColor(
                            view,
                            com.google.android.material.R.attr.colorPrimary
                        )
                        textPaint.color = MaterialColors.getColor(
                            view,
                            com.google.android.material.R.attr.colorOnPrimary
                        )
                        textExternalPaint.color = MaterialColors.getColor(
                            view,
                            com.google.android.material.R.attr.colorPrimary
                        )
                    }
                } else if (serie is LineSerie) {
                    serie.apply {
                        linePaint.color = MaterialColors.getColor(
                            view,
                            com.google.android.material.R.attr.colorPrimary
                        )
                        textPaint.color = MaterialColors.getColor(
                            view,
                            com.google.android.material.R.attr.colorOnPrimary
                        )
                    }
                }
            }
        }
    }
}