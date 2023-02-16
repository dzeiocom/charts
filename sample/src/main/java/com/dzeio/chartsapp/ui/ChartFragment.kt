package com.dzeio.chartsapp.ui

import android.graphics.Color
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
import com.dzeio.chartsapp.databinding.FragmentChartBinding
import com.dzeio.chartsapp.utils.MaterialUtils
import com.dzeio.chartsapp.utils.Utils
import kotlin.math.roundToInt
import kotlin.random.Random

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null

    private val args: ChartFragmentArgs by navArgs()

    private var numberOfValues = 5

    private lateinit var chart: ChartViewInterface
    private val binding: FragmentChartBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = binding.chart
        addSerie()

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
            numberOfValues++
            chart.refresh()
        }

        binding.removeValue.setOnClickListener {
            chart.series.forEach {
                it.entries.removeLastOrNull()
            }
            numberOfValues--
            chart.refresh()
        }

        binding.addSerie.setOnClickListener {
            addSerie()
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

        binding.switchAnimations.setOnCheckedChangeListener { _, isChecked ->
            chart.animator.enabled = isChecked
        }

        binding.switchXAxis.setOnCheckedChangeListener { _, isChecked ->
            chart.xAxis.enabled = isChecked
            chart.refresh()
        }

        binding.switchYAxis.setOnCheckedChangeListener { _, isChecked ->
            chart.yAxis.enabled = isChecked
            chart.refresh()
        }

        binding.sliderXAxis.addOnChangeListener { _, value, _ ->
            chart.xAxis.labelCount = value.roundToInt()
            chart.refresh()
        }

        binding.sliderYAxis.addOnChangeListener { _, value, _ ->
            chart.yAxis.labelCount = value.roundToInt()
            chart.refresh()
        }

        binding.switchXAxisScrollable.setOnCheckedChangeListener { _, isChecked ->
            chart.xAxis.scrollEnabled = isChecked
            if (isChecked) {
                chart.xAxis.dataWidth = binding.sliderXAxisScroll.value.toDouble()
                binding.sliderXAxisScroll.visibility = View.VISIBLE
            } else {
                chart.xAxis.dataWidth = null
                chart.xAxis.setCurrentMin(0.0)
                binding.sliderXAxisScroll.visibility = View.GONE
            }
            chart.refresh()
        }

        binding.sliderXAxisScroll.visibility = View.GONE
        binding.sliderXAxisScroll.addOnChangeListener { _, value, _ ->
            if (chart.xAxis.dataWidth != null) {
                chart.xAxis.dataWidth = value.toDouble()
                chart.refresh()
            }
        }

        if (args.chartType === "linechart") {
            binding.lineItem.visibility = View.VISIBLE

            binding.lineDisplayLines.setOnCheckedChangeListener { _, isChecked ->
                chart.series.forEach { (it as LineSerie).displayLines = isChecked }
                chart.refresh()
            }
            binding.lineDisplayPoints.setOnCheckedChangeListener { _, isChecked ->
                chart.series.forEach { (it as LineSerie).displayPoints = isChecked }
                chart.refresh()
            }
            binding.lineDotted.setOnCheckedChangeListener { _, isChecked ->
                chart.series.forEach { (it as LineSerie).dotted = isChecked }
                chart.refresh()
            }
        }
    }

    private var lastGenerated = 0
    private fun addSerie(): SerieInterface {
        val toGet = if (args.chartType == null) {
            if (lastGenerated == 0) {
                "barchart"
            } else {
                "linechart"
            }
        } else {
            args.chartType
        }
        if (lastGenerated++ >= 1) {
            lastGenerated = 0
        }
        val serie = if (toGet === "barchart") {
            BarSerie(chart).apply {
                barPaint.color = randomColor()
            }
        } else {
            LineSerie(chart).apply {
                linePaint.color = randomColor()
            }
        }
        serie.entries = Utils.generateRandomDataset(numberOfValues)
        return serie
    }

    private fun randomColor(): Int {
        return Color.argb(
            255,
            Random.nextInt(),
            Random.nextInt(),
            Random.nextInt()
        )
    }
}
