package src.com.iff615.intelektika;

public class SolarFlare {

    private final int zurichClass;
    private final int spotSize;
    private final int distribution;
    private final int activity;
    private final int evolution;
    private final int activityCode;
    private final int complex;
    private final int becameComplex;
    private final int area;
    private final int largestSpotArea;
    private int[] count;
    private int[] guessedCount;

    public SolarFlare(int zurichClass, int spotSize, int distribution, int activity,
                      int evolution, int activityCode, int complex, int becameComplex, int area,
                      int largestSpotArea, int[] count) {
        this.zurichClass = zurichClass;
        this.spotSize = spotSize;
        this.distribution = distribution;
        this.activity = activity;
        this.evolution = evolution;
        this.activityCode = activityCode;
        this.complex = complex;
        this.becameComplex = becameComplex;
        this.area = area;
        this.largestSpotArea = largestSpotArea;
        this.count = count;
        this.guessedCount = new int[SolarFlaresRegression.FLARES_COUNT];
    }

    public int getZurichClass() {
        return zurichClass;
    }

    public int getSpotSize() {
        return spotSize;
    }

    public int getDistribution() {
        return distribution;
    }

    public int getActivity() {
        return activity;
    }

    public int getEvolution() {
        return evolution;
    }

    public int getActivityCode() {
        return activityCode;
    }

    public int isComplex() {
        return complex;
    }

    public int isBecameComplex() {
        return becameComplex;
    }

    public int getArea() {
        return area;
    }

    public int getLargestSpotArea() {
        return largestSpotArea;
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
}
