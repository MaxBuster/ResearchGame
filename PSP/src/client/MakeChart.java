package client;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class MakeChart extends ApplicationFrame {
	private JFreeChart chart;

	public MakeChart(final String title, int[] chartData) {
		super(title);
		IntervalXYDataset dataset = createDataset(chartData);
		chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	public JFreeChart getChart() {
		return chart;
	}

	private IntervalXYDataset createDataset(int[] data) {
		final XYSeries series = new XYSeries("Random Data");
		for (int i = 0; i < data.length; i++) {
			series.add(i, data[i]);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}

	private JFreeChart createChart(IntervalXYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Voter Distribution", "Ideal Point", "Number of Voters", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}
}
