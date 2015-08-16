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
		// BiModalDist dist = new BiModalDist(40);
		// int[] data = dist.getData();
		for (int i = 0; i < data.length; i++) {
			series.add(i + 1, data[i]);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}

	private JFreeChart createChart(IntervalXYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYBarChart(
				"Voter Distribution", "X", false, "Y", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		final IntervalMarker target = new IntervalMarker(400.0, 700.0);
		target.setLabel("Target Range");
		target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		target.setLabelAnchor(RectangleAnchor.LEFT);
		target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		target.setPaint(new Color(222, 222, 255, 128));
		plot.addRangeMarker(target, Layer.BACKGROUND);
		return chart;
	}

	public static void main(final String[] args) {
		// final XYSeriesDemo3 demo = new XYSeriesDemo3("XY Series Demo 3");
		// MakeChart chart = new MakeChart("Title");
		// chart.pack();
		// RefineryUtilities.centerFrameOnScreen(chart);
		// chart.setVisible(true);

	}
}
