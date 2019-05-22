package src.com.iff615.intelektika;

import java.util.List;

public class SolarFlare {

    private int[] count;
    private int[] guessedCount;

    private double[] data;

    public SolarFlare(int zurichClass, int spotSize, int distribution, int activity,
                      int evolution, int activityCode, int complex, int becameComplex, int area,
                      int largestSpotArea, int[] count) {
        this.count = count;
        this.guessedCount = new int[SolarFlaresRegression.FLARES_COUNT];
        data = new double[] { zurichClass, spotSize, distribution, activity, evolution, activityCode, complex, becameComplex, area, largestSpotArea };
    }

    public int getCount(int flareClass) {
        return count[flareClass];
    }

    public void setGuessedCount(int flareClass, int guessedCount) {
        this.guessedCount[flareClass] = guessedCount;
    }

    public int getGuessedCount(int flareClass) {
        return guessedCount[flareClass];
    }

    public void reconstructData(List<Integer> ids) {
        for (int i = SolarFlaresRegression.N; i >= 0; i--) {
            if (ids.contains(i)) {
                for (int j = i + 1; j < SolarFlaresRegression.N; j++) {
                    data[j - 1] = data[j];
                }
            }
        }
    }

    public double getData(int index) {
        return data[index];
    }

    public void setData(int index, double value) {
        data[index] = value;
    }
}
