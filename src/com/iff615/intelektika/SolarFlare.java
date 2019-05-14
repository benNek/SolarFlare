package src.com.iff615.intelektika;

public class SolarFlare {

    private final ZurichClass zurichClass;
    private final SpotSize spotSize;
    private final SpotDistribution distribution;
    private final Activity activity;
    private final Evolution evolution;
    private final int activityCode;
    private final boolean complex;
    private final boolean becameComplex;
    private final Area area;
    private final Area largestSpotArea;

    private int cCount;
    private int mCount;
    private int xCount;

    public SolarFlare(ZurichClass zurichClass, SpotSize spotSize, SpotDistribution distribution, Activity activity,
                      Evolution evolution, int activityCode, boolean complex, boolean becameComplex, Area area,
                      Area largestSpotArea, int cCount, int mCount, int xCount) {
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
        this.cCount = cCount;
        this.mCount = mCount;
        this.xCount = xCount;
    }

    public ZurichClass getZurichClass() {
        return zurichClass;
    }

    public SpotSize getSpotSize() {
        return spotSize;
    }

    public SpotDistribution getDistribution() {
        return distribution;
    }

    public Activity getActivity() {
        return activity;
    }

    public Evolution getEvolution() {
        return evolution;
    }

    public int getActivityCode() {
        return activityCode;
    }

    public boolean isComplex() {
        return complex;
    }

    public boolean isBecameComplex() {
        return becameComplex;
    }

    public Area getArea() {
        return area;
    }

    public Area getLargestSpotArea() {
        return largestSpotArea;
    }
}
