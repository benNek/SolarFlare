package src.com.iff615.intelektika;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SolarFlaresRegression {

    // Koeficientu kiekis
    public static final int N = 10;

    // Flares klasiu kiekis (C, M, X), NEKEISTI!
    public static final int FLARES_COUNT = 3;

    private static final String DATA_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/solar-flare/flare.data2";
    private static final int SEGMENT_COUNT = 10;

    public static void main(String[] args) {
        new SolarFlaresRegression().run();
    }

    private void run() {
        List<SolarFlare> allFlares = readAllFlares();
        double totalAccuracy = 0;
        for (int segment = 0; segment < SEGMENT_COUNT; segment++) {
            System.out.println("--- SEGMENT " + (segment + 1) + " ---");
            List<SolarFlare> trainingFlares = getTrainingFlares(allFlares, segment);
            MultipleLinearRegression regression = new MultipleLinearRegression(trainingFlares);

            List<SolarFlare> testingFlares = getTestingFlares(allFlares, segment);
            AtomicInteger correctGuesses = new AtomicInteger();
            testingFlares
                    .forEach(flare -> {
                        for (int flareClass = 0; flareClass < FLARES_COUNT; flareClass++) {
                            int count = (int) Math.round(regression.beta(flareClass, 0) * flare.getZurichClass()
                                    + regression.beta(flareClass, 1) * flare.getSpotSize()
                                    + regression.beta(flareClass, 2) * flare.getDistribution()
                                    + regression.beta(flareClass, 3) * flare.getActivity()
                                    + regression.beta(flareClass, 4) * flare.getEvolution()
                                    + regression.beta(flareClass, 5) * flare.getActivityCode()
                                    + regression.beta(flareClass, 6) * flare.isComplex()
                                    + regression.beta(flareClass, 7) * flare.isBecameComplex()
                                    + regression.beta(flareClass, 8) * flare.getArea()
                                    + regression.beta(flareClass, 9) * flare.getLargestSpotArea());
                            flare.setGuessedCount(flareClass, count);

                            if (count == flare.getCount(flareClass)) {
                                correctGuesses.getAndIncrement();
                            }
                        }
                    });
            double accuracy = round((double) correctGuesses.get() / (allFlares.size() * FLARES_COUNT) * 100);
            System.out.println("Accuracy: " + accuracy + "%, R2: " + round(regression.R2()));
            totalAccuracy += accuracy;
        }
        totalAccuracy /= SEGMENT_COUNT;
        System.out.println("\nTotal accuracy: " + totalAccuracy + "%");

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

    private List<SolarFlare> getTrainingFlares(List<SolarFlare> allFlares, int segment) {
        int count = allFlares.size() / SEGMENT_COUNT;
        Stream<SolarFlare> before = allFlares.stream()
                .limit(count * segment);
        Stream<SolarFlare> after = allFlares.stream()
                .skip(count * (segment + 1));
        return Stream.concat(before, after).collect(Collectors.toList());
    }

    private List<SolarFlare> getTestingFlares(List<SolarFlare> allFlares, int segment) {
        int count = allFlares.size() / SEGMENT_COUNT;
        int skip = count * segment;
        return allFlares.stream().skip(skip).limit(count).collect(Collectors.toList());
    }

    private SolarFlare adaptDataToSolarFlare(String[] data) {
        int zurichClass = ZurichClass.valueOf(data[0]).ordinal();
        int size = SpotSize.valueOf(data[1]).ordinal();
        int distribution = SpotDistribution.valueOf(data[2]).ordinal();
        int activity = Integer.parseInt(data[3]);
        int evolution = Integer.parseInt(data[4]);
        int activityCode = Integer.parseInt(data[5]);
        int complex = Integer.parseInt(data[6]);
        int becameComplex = Integer.parseInt(data[7]);
        int area = Integer.parseInt(data[8]);
        int largestSpotArea = Integer.parseInt(data[9]) <= 5 ? 1 : 2;
        int cCount = Integer.parseInt(data[10]);
        int mCount = Integer.parseInt(data[11]);
        int xCount = Integer.parseInt(data[12]);
        return new SolarFlare(zurichClass, size, distribution, activity, evolution,
                activityCode, complex, becameComplex, area, largestSpotArea, new int[]{cCount, mCount, xCount});
    }

    private double round(double x) {
        return Math.round(x * 1000.0) / 1000.0;
    }

}
