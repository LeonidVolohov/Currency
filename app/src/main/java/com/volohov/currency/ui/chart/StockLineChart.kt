package com.volohov.currency.ui.chart

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.volohov.currency.R
import java.math.BigDecimal

class StockLineChart(lineChart: LineChart) {
    private var localLineChart = lineChart

    init {
        setChartSetting()
    }

    private fun setChartSetting() {
        this.localLineChart.setTouchEnabled(true)
        this.localLineChart.setPinchZoom(true)
        this.localLineChart.setDrawGridBackground(true)
        this.localLineChart.setBorderColor(Color.GRAY)
        this.localLineChart.setBorderWidth(5f)
        this.localLineChart.description.text = ""

        this.localLineChart.legend.isEnabled = true
        this.localLineChart.legend.form = Legend.LegendForm.LINE
        this.localLineChart.legend.textSize = 16f
        this.localLineChart.legend.formSize = 12f
    }

    fun setXAxis(dateList: List<String>?) {
        this.localLineChart.xAxis.position = XAxis.XAxisPosition.TOP
        this.localLineChart.xAxis.setDrawGridLines(false)
        this.localLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
        this.localLineChart.xAxis.textSize = 10f
        this.localLineChart.xAxis.labelCount = 4
    }

    fun getLineData(entries: List<Double>, baseCurrency: String, targetCurrency: String): LineDataSet {
        val localEntry = ArrayList<Entry>()
        var iter = -1.0f
        for (item in entries) {
            iter += 1.0f
            localEntry.add(Entry(iter, BigDecimal(item).setScale(5, BigDecimal.ROUND_HALF_EVEN).toFloat()))
        }

        val lineChartData = LineDataSet(localEntry, "$baseCurrency to $targetCurrency")
        lineChartData.lineWidth = 4f
        lineChartData.setDrawCircles(false)
        lineChartData.setDrawCircleHole(false)
        lineChartData.valueTextSize = 0f
        lineChartData.color = R.color.purple_500

        return lineChartData
    }

    fun getPredictionLineData(predictionEntries: List<Double>): LineDataSet {
        val localPredictionEntry = ArrayList<Entry>()
        var iter = -1.0f
        for (item in predictionEntries) {
            iter += 1.0f
            localPredictionEntry.add(
                    Entry(
                            iter,
                            BigDecimal(item).setScale(5, BigDecimal.ROUND_HALF_EVEN).toFloat()
                    )
            )
        }

        val lineChartPredictionData = LineDataSet(localPredictionEntry, "")
        lineChartPredictionData.lineWidth = 2f
        lineChartPredictionData.setDrawCircles(false)
        lineChartPredictionData.setDrawCircleHole(false)
        lineChartPredictionData.valueTextSize = 0f
        lineChartPredictionData.color = R.color.purple_500
        lineChartPredictionData.enableDashedLine(10f, 10f, 0f)

        return lineChartPredictionData
    }

    fun displayChart(lineChartData: LineDataSet) {
        this.localLineChart.onTouchListener.setLastHighlighted(null)
        this.localLineChart.highlightValues(null)
        this.localLineChart.data = LineData(lineChartData)
        this.localLineChart.notifyDataSetChanged()
        this.localLineChart.invalidate()
    }

    fun displayPredictionChart(
            lineChartData: LineDataSet,
            lineChartPredictionData: LineDataSet
    ) {
        val chartData = LineData()
        lineChartPredictionData.setDrawFilled(true)
        lineChartPredictionData.fillAlpha = 128
        chartData.addDataSet(lineChartData)
        chartData.addDataSet(lineChartPredictionData)

        this.localLineChart.onTouchListener.setLastHighlighted(null)
        this.localLineChart.highlightValues(null)
        this.localLineChart.data = chartData
        this.localLineChart.notifyDataSetChanged()
        this.localLineChart.invalidate()
    }

    fun displayPredictionChartTransparent(
            lineChartData: LineDataSet,
            lineChartPredictionDataMin: LineDataSet,
            lineChartPredictionDataMax: LineDataSet
    ) {
        val chartData = LineData()
        lineChartPredictionDataMax.setDrawFilled(true)
        lineChartPredictionDataMax.fillAlpha = 96
        lineChartPredictionDataMin.setDrawFilled(true)
        lineChartPredictionDataMin.fillColor = Color.TRANSPARENT
        chartData.addDataSet(lineChartData)
        chartData.addDataSet(lineChartPredictionDataMax)
        chartData.addDataSet(lineChartPredictionDataMin)

        this.localLineChart.onTouchListener.setLastHighlighted(null)
        this.localLineChart.highlightValues(null)
        this.localLineChart.data = chartData
        this.localLineChart.notifyDataSetChanged()
        this.localLineChart.invalidate()
    }

    fun clearChart() {
        this.localLineChart.invalidate()
        this.localLineChart.clear()
    }
}
