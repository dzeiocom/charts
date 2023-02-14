package com.dzeio.chartstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dzeio.charts.ChartType
import com.dzeio.charts.ChartViewInterface
import com.dzeio.charts.Entry
import com.dzeio.charts.series.BarSerie
import com.dzeio.charts.series.LineSerie
import com.dzeio.charts.series.SerieInterface
import com.dzeio.chartstest.databinding.FragmentChartBinding
import com.dzeio.chartstest.utils.MaterialUtils
import com.dzeio.chartstest.utils.Utils
import kotlin.random.Random

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null

    private val args: ChartFragmentArgs by navArgs()

    private lateinit var chart: ChartViewInterface
    private val binding: FragmentChartBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = binding.chart
        val baseSerie = addSerie()
        baseSerie.entries = Utils.generateRandomDataset(5)

        MaterialUtils.materielTheme(chart, requireView())

        chart.refresh()

        binding.addValue.setOnClickListener {
            chart.series.forEach {
                it.entries.add(
                    Entry(
                        it.entries.size.toDouble(),
                        Random.nextInt(0, 100).toFloat()
                    )
                )
            }
            chart.refresh()
        }

        binding.removeValue.setOnClickListener {
            chart.series.forEach {
                it.entries.removeLastOrNull()
            }
            chart.refresh()
        }

        binding.addSerie.setOnClickListener {
            val serie = addSerie()
            serie.entries = Utils.generateRandomDataset(chart.series[0].entries.size)
            chart.series.add(addSerie())
            chart.refresh()
        }

        binding.removeSerie.setOnClickListener {
            chart.series.removeLastOrNull()
            chart.refresh()
        }

        binding.switchSubtype.setOnClickListener {
            when (chart.type) {
                ChartType.BASIC -> {
                    chart.type = ChartType.GROUPED
                    binding.switchSubtype.setText("Grouped Chart")
                }
                ChartType.GROUPED -> {
                    chart.type = ChartType.STACKED
                    binding.switchSubtype.setText("Stacked Chart")
                }
                else -> {
                    chart.type = ChartType.BASIC
                    binding.switchSubtype.setText("Basic Chart")
                }
            }
            chart.refresh()
        }
    }

    private fun addSerie(): SerieInterface {
        return if (args.chartType === "barchart") { BarSerie(chart) } else { LineSerie(chart) }
    }
}
