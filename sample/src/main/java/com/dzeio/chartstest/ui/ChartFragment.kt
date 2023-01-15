package com.dzeio.chartstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
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
    private lateinit var serie: SerieInterface
    private val binding: FragmentChartBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = binding.chart
        serie = if (args.chartType === "barchart") { BarSerie(chart) } else { LineSerie(chart) }

        MaterialUtils.materielTheme(chart, requireView())

        serie.entries = Utils.generateRandomDataset(5)
        chart.refresh()

        binding.addValue.setOnClickListener {
            serie.entries.add(
                Entry(
                    serie.entries.size.toDouble(),
                    Random.nextInt(0, 100).toFloat()
                )
            )
            chart.refresh()
        }

        binding.removeValue.setOnClickListener {
            serie.entries.removeLast()
            chart.refresh()
        }
    }

}
