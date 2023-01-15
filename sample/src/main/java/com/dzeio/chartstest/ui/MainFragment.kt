package com.dzeio.chartstest.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dzeio.charts.ChartType
import com.dzeio.charts.series.BarSerie
import com.dzeio.charts.series.LineSerie
import com.dzeio.chartstest.databinding.FragmentMainBinding
import com.dzeio.chartstest.utils.MaterialUtils
import com.dzeio.chartstest.utils.Utils.generateRandomDataset
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val months = arrayListOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gotoBarchart.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToChartFragment("barchart")
            )
        }

        binding.gotoLinechart.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToChartFragment("linechart")
            )
        }

        binding.chartGrouped.apply {
            // setup the Serie
            val serie1 = BarSerie(this)
            val serie2 = BarSerie(this)

            animator.duration = 750

            // transform the chart into a grouped chart
            type = ChartType.GROUPED
            yAxis.setYMin(0f)

            // utils function to use Material3 auto colors
            MaterialUtils.materielTheme(this, requireView())
            serie2.barPaint.color = Color.RED

            // give the serie it's entries
            serie1.entries = generateRandomDataset(5)
            serie2.entries = generateRandomDataset(5)

            annotator.annotationTitleFormat = { "${it.y.roundToInt()}$" }
            annotator.annotationSubTitleFormat = { months[it.x.roundToInt()] }

            // refresh the Chart
            refresh()
        }

        binding.chartStacked.apply {
            // setup the Serie
            val serie1 = BarSerie(this)
            val serie2 = BarSerie(this)

            animator.duration = 750

            // transform the chart into a grouped chart
            type = ChartType.STACKED
            yAxis.setYMin(0f)

            // utils function to use Material3 auto colors
            MaterialUtils.materielTheme(this, requireView())
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
            MaterialUtils.materielTheme(this, requireView())

            // give the serie its entries
            serie.entries = generateRandomDataset(10)

            // refresh the Chart
            refresh()
        }

        binding.chartBar.apply {
            // setup the Serie
            val serie = BarSerie(this)
            yAxis.setYMin(0f)

            // utils function to use Material3 auto colors
            MaterialUtils.materielTheme(this, requireView())

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
            MaterialUtils.materielTheme(this, requireView())

            // give the series their entries
            val xStep = 1
            serie2.entries = generateRandomDataset(20, -50, 50, xStep)
            serie1.entries = generateRandomDataset(20, -50, 50, xStep).apply {
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
                dataWidth = 10.0 * xStep

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


}
