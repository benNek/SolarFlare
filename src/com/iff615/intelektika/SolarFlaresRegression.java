package src.com.iff615.intelektika;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose method: ");
        System.out.println("1 - Fully programmed method");
        System.out.println("2 - Apache commons math OLSMultipleLinearRegression method");
        int type = scanner.nextInt();

        List<SolarFlare> allFlares = readAllFlares();
        double totalAccuracy = 0;
        for (int segment = 0; segment < SEGMENT_COUNT; segment++) {
            System.out.println("--- SEGMENT " + (segment + 1) + " ---");
            List<SolarFlare> trainingFlares = getTrainingFlares(allFlares, segment);
            List<SolarFlare> testingFlares = getTestingFlares(allFlares, segment);

            int correctGuesses = 0;
            if (type == 1) {
                correctGuesses = fullyProgrammedMethod(trainingFlares, testingFlares);
            } else if (type == 2) {
                correctGuesses = getOLSENRegression(trainingFlares, testingFlares);
            }

            double accuracy = round((double) correctGuesses / (testingFlares.size() * FLARES_COUNT) * 100);
            System.out.println("Accuracy: " + accuracy);
            totalAccuracy += accuracy;
        }
        totalAccuracy /= SEGMENT_COUNT;
        System.out.println("\nTotal accuracy: " + round(totalAccuracy) + "%");

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

    private int fullyProgrammedMethod(List<SolarFlare> trainingFlares, List<SolarFlare> testingFlares) {
        MultipleLinearRegression regression = new MultipleLinearRegression(trainingFlares);
        return getCorrectGuesses(testingFlares, regression.beta());
    }

    private int getOLSENRegression(List<SolarFlare> trainingFlares, List<SolarFlare> testingFlares) {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);

        double[][] beta = new double[FLARES_COUNT][N];
        for (int flareClass = 0; flareClass < FLARES_COUNT; flareClass++) {
            regression.newSampleData(getY(0, trainingFlares), getX(trainingFlares));
            beta[flareClass] = regression.estimateRegressionParameters();
        }

        return getCorrectGuesses(testingFlares, beta);
    }

    public static double[][] getX(List<SolarFlare> flares) {
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

    public static double[] getY(int flareClass, List<SolarFlare> flares) {
        double[] y = new double[flares.size()];
        for (int i = 0; i < flares.size(); i++) {
            y[i] = flares.get(i).getCount(flareClass);
        }
        return y;
    }

    private int getCorrectGuesses(List<SolarFlare> flares, double[][] beta) {
        AtomicInteger correctGuesses = new AtomicInteger();
        flares.forEach(flare -> {
            for (int flareClass = 0; flareClass < FLARES_COUNT; flareClass++) {
                int count = getSolarFlaresCount(beta[flareClass], flare);
                flare.setGuessedCount(flareClass, count);

                if (count == flare.getCount(flareClass)) {
                    correctGuesses.getAndIncrement();
                }
            }
        });
        return correctGuesses.get();
    }

    private int getSolarFlaresCount(double[] beta, SolarFlare flare) {
        return (int) Math.round(beta[0] * flare.getZurichClass()
                + beta[1] * flare.getSpotSize()
                + beta[2] * flare.getDistribution()
                + beta[3] * flare.getActivity()
                + beta[4] * flare.getEvolution()
                + beta[5] * flare.getActivityCode()
                + beta[6] * flare.isComplex()
                + beta[7] * flare.isBecameComplex()
                + beta[8] * flare.getArea()
                + beta[9] * flare.getLargestSpotArea());
    }

}
