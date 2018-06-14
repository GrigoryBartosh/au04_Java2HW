package ru.spbau.gbarto.server.ui;

import java.io.*;
import java.util.ArrayList;

class MetricsList {
    private static final String FOLDER = "results";

    private String architecture;
    private String parameterName;
    private String metricsName;
    private int x;
    private int from;
    private int to;
    private int step;
    private int parameter1;
    private int parameter2;

    private ArrayList<Double> list = new ArrayList<>();

    MetricsList(String architecture, String parameterName, String metricsName, int x, int from, int to, int step, int parameter1, int parameter2) {
        this.architecture = architecture;
        this.parameterName = parameterName;
        this.metricsName = metricsName;
        this.x = x;
        this.from = from;
        this.to = to;
        this.step = step;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    private String getNextLine(BufferedReader bufferedReader) throws IOException {
        String res = bufferedReader.readLine();

        if (res == null) {
            throw new IOException();
        }

        return res;
    }

    MetricsList(String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            architecture = getNextLine(bufferedReader);
            parameterName = getNextLine(bufferedReader);
            metricsName = getNextLine(bufferedReader);
            x = Integer.parseInt(getNextLine(bufferedReader));
            from = Integer.parseInt(getNextLine(bufferedReader));
            to = Integer.parseInt(getNextLine(bufferedReader));
            step = Integer.parseInt(getNextLine(bufferedReader));
            parameter1 = Integer.parseInt(getNextLine(bufferedReader));
            parameter2 = Integer.parseInt(getNextLine(bufferedReader));

            int cnt = (int)Math.ceil((double)(to - from + 1) / step);
            for (int i = 0; i < cnt; i++) {
                list.add(Double.parseDouble(getNextLine(bufferedReader)));
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + fileName);
            System.exit(1);
        }
    }

    void add(Double val) {
        list.add(val);
    }

    void save() {
        File file = new File(FOLDER);
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.err.print("Could not create folder: " + FOLDER);
                return;
            }
        }

        String fileName = architecture + "_" + parameterName + "_" + metricsName + ".txt";
        try (PrintWriter writer = new PrintWriter(FOLDER + "/" + fileName)) {
            writer.println(architecture);
            writer.println(parameterName);
            writer.println(metricsName);
            writer.println(x);
            writer.println(from);
            writer.println(to);
            writer.println(step);
            writer.println(parameter1);
            writer.println(parameter2);

            for (Double val : list) {
                writer.println(val);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not write file: " + fileName);
        }
    }

    String getArchitecture() {
        return architecture;
    }

    String getParameterName() {
        return parameterName;
    }

    String getMetricsName() {
        return metricsName;
    }

    int getX() {
        return x;
    }

    int getFrom() {
        return from;
    }

    int getTo() {
        return to;
    }

    int getStep() {
        return step;
    }

    int getParameter1() {
        return parameter1;
    }

    int getParameter2() {
        return parameter2;
    }

    double getValue(int i) {
        return list.get(i);
    }
}
