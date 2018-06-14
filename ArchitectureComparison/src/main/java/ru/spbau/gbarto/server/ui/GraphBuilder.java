package ru.spbau.gbarto.server.ui;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;

class GraphBuilder {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;

    private String parameterName;
    private String metricsName;
    private int x;
    private int from;
    private int to;
    private int step;
    private int parameter1;
    private int parameter2;

    private boolean initialized = false;

    private XYChart chart;

    GraphBuilder() {
        chart = new XYChart(WIDTH, HEIGHT);
        chart.setYAxisTitle("ms");
    }

    private void initialize(MetricsList metrics) {
        parameterName = metrics.getParameterName();
        metricsName = metrics.getMetricsName();
        x = metrics.getX();
        from = metrics.getFrom();
        to = metrics.getTo();
        step = metrics.getStep();
        parameter1 = metrics.getParameter1();
        parameter2 = metrics.getParameter2();

        chart.setTitle(metricsName);
        chart.setXAxisTitle(parameterName);

        initialized = true;
    }

    private boolean isOk(MetricsList metrics) {
        return  parameterName.equals(metrics.getParameterName()) &&
                metricsName.equals(metrics.getMetricsName()) &&
                x == metrics.getX() &&
                from == metrics.getFrom() &&
                to == metrics.getTo() &&
                step == metrics.getStep() &&
                parameter1 == metrics.getParameter1() &&
                parameter2 == metrics.getParameter2();
    }

    void add(MetricsList metrics) {
        if (!initialized) {
            initialize(metrics);
        } else {
            if (!isOk(metrics)) {
                return;
            }
        }

        int cnt = (int)Math.ceil((double)(to - from + 1) / step);
        double[] xData = new double[cnt];
        double[] yData = new double[cnt];

        for (int val = from, i = 0; val <= to; val += step, i++) {
            xData[i] = val;
            yData[i] = metrics.getValue(i);
        }

        XYSeries series = chart.addSeries(metrics.getArchitecture(), xData, yData);
        series.setMarker(SeriesMarkers.NONE);
    }

    void save(String name) {
        try {
            BitmapEncoder.saveBitmap(chart, name, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            System.err.println("Could not save graph: " + name);
        }
    }
}
