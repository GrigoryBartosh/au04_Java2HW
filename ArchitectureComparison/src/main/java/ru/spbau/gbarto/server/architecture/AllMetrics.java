package ru.spbau.gbarto.server.architecture;

public class AllMetrics {
    public Metric requestProcessing = new Metric();
    public Metric clientOnTheServer = new Metric();
    public Metric request = new Metric();

    public void add(AllMetrics other) {
        requestProcessing.add(other.requestProcessing);
        clientOnTheServer.add(other.clientOnTheServer);
        request.add(other.request);
    }

    public Metric get(int i) {
        switch (i) {
            case 0:
                return requestProcessing;

            case 1:
                return clientOnTheServer;

            default:
                return request;
        }
    }

    double[] toArray() {
        double[] array = new double[3];
        array[0] = requestProcessing.get();
        array[1] = clientOnTheServer.get();
        array[2] = request.get();

        return array;
    }
}
