package de.ma.visuals;

import java.awt.Color;
import java.awt.Component;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class GeneratingTimeDiagram {

	ChartPanel chartPanel;
	XYSeries series;

	int numberOfGraphs;
	long startTime;

	public GeneratingTimeDiagram() {
		series = new XYSeries("generatedGraphs");
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		final JFreeChart chart = createChart(dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		// setContentPane(chartPanel);

		startTime = System.nanoTime();
		update(0);
	}

	public void update(int graphs) {
		long time = (System.nanoTime() - startTime) / 1000000;

		numberOfGraphs += graphs;
		series.add(time, numberOfGraphs);
	}

	private JFreeChart createChart(final XYDataset dataset) {

		final JFreeChart chart = ChartFactory.createXYLineChart("Generation time", "milliseconds", "Number of Graphs",
				dataset, PlotOrientation.VERTICAL, false, true, false);

		chart.setBackgroundPaint(Color.white);
		final XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesShape(0, ShapeUtilities.createDiagonalCross(3, 0.1f));
		plot.setRenderer(renderer);

		return chart;
	}

	public Component getChartPanel() {
		return chartPanel;
	}
}
