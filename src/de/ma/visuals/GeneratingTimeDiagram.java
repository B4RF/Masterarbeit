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

	ChartPanel timePanel;
	ChartPanel delayPanel;
	XYSeries timeSeries;
	XYSeries delaySeries;

	int numberOfGraphs;
	long startTime;
	long lastTime;

	public GeneratingTimeDiagram() {
		timeSeries = new XYSeries("generatedGraphs");
		final XYSeriesCollection dataset1 = new XYSeriesCollection();
		dataset1.addSeries(timeSeries);
		final JFreeChart chart1 = createChart(dataset1, "Generation time", "milliseconds", "number of graphs");
		timePanel = new ChartPanel(chart1);
		timePanel.setPreferredSize(new java.awt.Dimension(500, 270));

		delaySeries = new XYSeries("generatedGraphs");
		final XYSeriesCollection dataset2 = new XYSeriesCollection();
		dataset2.addSeries(delaySeries);
		final JFreeChart chart2 = createChart(dataset2, "Generation delay", "graph", "delay");
		delayPanel = new ChartPanel(chart2);
		delayPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	}

	public void start() {
		startTime = System.nanoTime();
		lastTime = startTime;
		update(0);
	}

	public void update(int graphs) {
		long time = (System.nanoTime() - startTime) / 1000000;

		numberOfGraphs += graphs;
		timeSeries.add(time, numberOfGraphs);
		
		long tempTime = System.nanoTime();
		time = (tempTime - lastTime) / 1000000;
		lastTime = tempTime;
		
		delaySeries.add(numberOfGraphs, time);
	}

	private JFreeChart createChart(final XYDataset dataset, String headline, String xLabel, String yLabel) {

		final JFreeChart chart = ChartFactory.createXYLineChart(headline, xLabel, yLabel, dataset,
				PlotOrientation.VERTICAL, false, true, false);

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

	public Component getTimePanel() {
		return timePanel;
	}

	public Component getDelayPanel() {
		return delayPanel;
	}
}
