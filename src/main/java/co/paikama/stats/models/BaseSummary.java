package co.paikama.stats.models;

public class BaseSummary {

    protected double sum;
    protected double avg;
    protected double max;
    protected double min;
    protected long count;

    public BaseSummary() {
        sum = 0.0D;
        avg = 0.0D;
        max = 0.0D;
        min = 0.0D;
        count = 0L;
    }

    public BaseSummary(double sum, double avg, double max, double min, long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        return avg;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }
}
