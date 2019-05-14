package src.com.iff615.intelektika;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Classification {

    private static final String DATA_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/solar-flare/flare.data2";
    private static final int SEGMENT_COUNT = 10;

    public static void main(String[] args) {
        new Classification().run();
    }

    private void run() {
        List<SolarFlare> allFlares = readAllFlares();
        for (int segment = 0; segment < SEGMENT_COUNT; segment++) {
            System.out.println("--- SEGMENT " + (segment + 1) + " ---");
            List<SolarFlare> flares = getTestingFlares(allFlares, segment);
        }

    }

    private List<SolarFlare> readAllFlares() {
        List<SolarFlare> flares = new ArrayList<>();
        try {
            URL url = new URL(DATA_URL);
            Scanner scanner = new Scanner(url.openStream());
            scanner.useDelimiter("\n");
            // first line is comment, skipping it
            scanner.next();
            while (scanner.hasNext()) {
                String[] parts = scanner.next().split(" ");
                flares.add(adaptDataToSolarFlare(parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flares;
    }

    private List<SolarFlare> getTestingFlares(List<SolarFlare> allFlares, int segment) {
        List<SolarFlare> flares = new ArrayList<>();

        return flares;
    }

    private SolarFlare adaptDataToSolarFlare(String[] data) {
        ZurichClass zurichClass = ZurichClass.valueOf(data[0]);
        SpotSize size = SpotSize.valueOf(data[1]);
        SpotDistribution distribution = SpotDistribution.valueOf(data[2]);
        Activity activity = Activity.values()[Integer.parseInt(data[3]) - 1];
        Evolution evolution = Evolution.values()[Integer.parseInt(data[4]) - 1];
        int activityCode = Integer.parseInt(data[5]);
        boolean complex = data[6] == "1";
        boolean becameComplex = data[7] == "1";
        Area area = Area.values()[Integer.parseInt(data[8]) - 1];
        Area largestSpotArea = Area.values()[(Integer.parseInt(data[9]) - 1) <= 5 ? 0 : 1];
        int cCount = Integer.parseInt(data[10]);
        int mCount = Integer.parseInt(data[11]);
        int xCount = Integer.parseInt(data[12]);
        return new SolarFlare(zurichClass, size, distribution, activity, evolution,
                activityCode, complex, becameComplex, area, largestSpotArea, cCount, mCount, xCount);
    }

}
