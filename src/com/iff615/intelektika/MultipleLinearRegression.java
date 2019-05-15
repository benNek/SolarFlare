package src.com.iff615.intelektika;

import Jama.Matrix;
import Jama.QRDecomposition;

import java.util.List;

public class MultipleLinearRegression {

    private final Matrix[] beta;
    private double[] sse;
    private double[] sst;

    public MultipleLinearRegression(List<SolarFlare> flares) {
        Matrix matrixX = new Matrix(getX(flares));
        beta = new Matrix[SolarFlaresRegression.FLARES_COUNT];
        sse = new double[SolarFlaresRegression.FLARES_COUNT];
        sst = new double[SolarFlaresRegression.FLARES_COUNT];
        for (int flareClass = 0; flareClass < SolarFlaresRegression.FLARES_COUNT; flareClass++) {
            double[] y = getY(flareClass, flares);
            int n = y.length;

            Matrix matrixY = new Matrix(y, n);

            QRDecomposition qr = new QRDecomposition(matrixX);
            beta[flareClass] = qr.solve(matrixY);

            double sum = 0.0;
            for (int i = 0; i < n; i++)
                sum += y[i];
            double mean = sum / n;

            for (int i = 0; i < n; i++) {
                double dev = y[i] - mean;
                sst[flareClass] += dev * dev;
            }

            Matrix residuals = matrixX.times(beta[flareClass]).minus(matrixY);
            sse[flareClass] = residuals.norm2() * residuals.norm2();
        }
    }

    private double[][] getX(List<SolarFlare> flares) {
        double[][] x = new double[flares.size()][SolarFlaresRegression.N];
        for (int i = 0; i < flares.size(); i++) {
            SolarFlare flare = flares.get(i);
            x[i][0] = flare.getZurichClass();
            x[i][1] = flare.getSpotSize();
            x[i][2] = flare.getDistribution();
            x[i][3] = flare.getActivity();
            x[i][4] = flare.getEvolution();
            x[i][5] = flare.getActivityCode();
            x[i][6] = flare.isComplex();
            x[i][7] = flare.isBecameComplex();
            x[i][8] = flare.getArea();
            x[i][9] = flare.getLargestSpotArea();
        }
        return x;
    }

    private double[] getY(int flareClass, List<SolarFlare> flares) {
        double[] y = new double[flares.size()];
        for (int i = 0; i < flares.size(); i++) {
            y[i] = flares.get(i).getCount(flareClass);
        }
        return y;
    }

    public double beta(int flareClass, int j) {
        return beta[flareClass].get(j, 0);
    }

    public double R2(int flareClass) {
        return 1.0 - sse[flareClass] / sst[flareClass];
    }

    public double R2() {
        double r2 = 0;
        for (int flareClass = 0; flareClass < SolarFlaresRegression.FLARES_COUNT; flareClass++) {
            r2 += 1.0 - sse[flareClass] / sst[flareClass];
        }
        return r2 / SolarFlaresRegression.FLARES_COUNT;
    }

}
