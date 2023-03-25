package Curves.org;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Plot {

    private final XYChart Chart;

    public Plot(int maxIteration, List<Double []> LArr, double avgFitness) {

        Chart = new XYChartBuilder().title("Performance").xAxisTitle("Iterations").yAxisTitle("AVG Fitness Score").width(550).height(350).theme(Styler.ChartTheme.Matlab).build();
        Chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        Chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
        Chart.getStyler().setMarkerSize(0);

        var arr = new double[LArr.size()][LArr.get(0).length + 1];
        arr[0][0] = avgFitness;
        arr[1][0] = avgFitness;
        arr[2][0] = avgFitness;
        arr[3][0] = avgFitness;
        arr[4][0] = avgFitness;
        arr[5][0] = avgFitness;
        arr[6][0] = avgFitness;

        var tt = Arrays.stream(LArr.get(0)).mapToDouble(Double::doubleValue).toArray();

        System.arraycopy(Arrays.stream(LArr.get(0)).mapToDouble(i -> i).toArray(), 0, arr[0], 1, LArr.get(0).length);
        System.arraycopy(Arrays.stream(LArr.get(1)).mapToDouble(i -> i).toArray(), 0, arr[1], 1, LArr.get(1).length);
        System.arraycopy(Arrays.stream(LArr.get(2)).mapToDouble(i -> i).toArray(), 0, arr[2], 1, LArr.get(2).length);
        System.arraycopy(Arrays.stream(LArr.get(3)).mapToDouble(i -> i).toArray(), 0, arr[3], 1, LArr.get(3).length);
        System.arraycopy(Arrays.stream(LArr.get(4)).mapToDouble(i -> i).toArray(), 0, arr[4], 1, LArr.get(4).length);
        System.arraycopy(Arrays.stream(LArr.get(5)).mapToDouble(i -> i).toArray(), 0, arr[5], 1, LArr.get(5).length);
        System.arraycopy(Arrays.stream(LArr.get(6)).mapToDouble(i -> i).toArray(), 0, arr[6], 1, LArr.get(6).length);

        Chart.addSeries("BCS", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[0]);
        Chart.addSeries("BCSA", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[1]);
        Chart.addSeries("IBCSA", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[2]);
        Chart.addSeries("BJAYA", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[3]);
        Chart.addSeries("IBCSA-BJAYA", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[4]);
        Chart.addSeries("IBCSA-BCS", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[5]);
        Chart.addSeries("GA", IntStream.range(0, maxIteration + 1).parallel().asDoubleStream().toArray(), arr[6]);
    }

    public void show() {
        new SwingWrapper<>(Chart).displayChart();
    }
}
