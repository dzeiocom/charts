package com.dzeio.chartsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dzeio.charts.series.BarSerie
import com.dzeio.charts.series.LineSerie
import com.dzeio.chartsapp.databinding.FragmentMainBinding
import com.dzeio.chartsapp.utils.MaterialUtils
import com.dzeio.chartsapp.utils.Utils

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
        "Dec"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        binding.gotoBarLineChart.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToChartFragment(null)
            )
        }

        binding.barchart.apply {
            BarSerie(this).apply {
                entries = Utils.generateRandomDataset(5)
            }
            MaterialUtils.materielTheme(this, requireView())
        }

        binding.linechart.apply {
            LineSerie(this).apply {
                entries = Utils.generateRandomDataset(5)
            }
            MaterialUtils.materielTheme(this, requireView())
        }

        binding.bothchart.apply {
            BarSerie(this).apply {
                entries = Utils.generateRandomDataset(5)
            }
            LineSerie(this).apply {
                entries = Utils.generateRandomDataset(5)
            }
            MaterialUtils.materielTheme(this, requireView())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
