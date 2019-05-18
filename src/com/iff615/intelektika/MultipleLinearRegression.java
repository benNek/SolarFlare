package src.com.iff615.intelektika;

import Jama.Matrix;
import Jama.QRDecomposition;

import java.util.List;

public class MultipleLinearRegression {

    private final Matrix[] beta;
    private double[] sse;
    private double[] sst;

    public MultipleLinearRegression(List<SolarFlare> flares) {
        Matrix matrixX = new Matrix(SolarFlaresRegression.getX(flares));
        beta = new Matrix[SolarFlaresRegression.FLARES_COUNT];
        sse = new double[SolarFlaresRegression.FLARES_COUNT];
        sst = new double[SolarFlaresRegression.FLARES_COUNT];
        for (int flareClass = 0; flareClass < SolarFlaresRegression.FLARES_COUNT; flareClass++) {
            double[] y = SolarFlaresRegression.getY(flareClass, flares);
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

    public double[][] beta() {
        double[][] betaValues = new double[SolarFlaresRegression.FLARES_COUNT][SolarFlaresRegression.N];
        for (int flareClass = 0; flareClass < SolarFlaresRegression.FLARES_COUNT; flareClass++) {
            for (int i = 0; i < SolarFlaresRegression.N; i++) {
                betaValues[flareClass][i] = beta(flareClass, i);
            }
        }
        return betaValues;
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
