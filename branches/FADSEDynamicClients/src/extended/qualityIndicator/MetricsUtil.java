/*
 * This file is part of the FADSE tool.
 * 
 *   Authors: Horia Andrei Calborean {horia.calborean at ulbsibiu.ro}
 *   Copyright (c) 2009-2010
 *   All rights reserved.
 * 
 *   Redistribution and use in source and binary forms, with or without modification,
 *   are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * 
 *   The names of its contributors NOT may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *   ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *   OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package extended.qualityIndicator;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Horia Calborean
 */
public class MetricsUtil {

    public static void computeUniqueIndividuals(int populationSize, File firstPopulation, LinkedList<File> listOfOffspringFiles, File metricsFolder, String fileName) throws IOException {
        Set<String> uniqueInd = new HashSet<String>();

        String fPath = metricsFolder.getAbsolutePath() + System.getProperty("file.separator");
        FileWriter uniqueFile = new FileWriter(fPath + fileName);
        BufferedWriter outUnique = new BufferedWriter(uniqueFile);

        BufferedReader input = new BufferedReader(new FileReader(firstPopulation));
        String line = null; //not declared within while loop
        int lineCounter = 0;
        outUnique.write("Total individuals");
        outUnique.write(",");
        outUnique.write("New individuals");
        outUnique.newLine();
        while ((line = input.readLine()) != null && lineCounter < populationSize) {
            uniqueInd.add(line);
            lineCounter++;
        }
        int previousSize = uniqueInd.size();
        outUnique.write(String.valueOf(uniqueInd.size()));
        outUnique.write(",");
        outUnique.write(String.valueOf(uniqueInd.size()));
        outUnique.newLine();
        for (int i = 0; i < listOfOffspringFiles.size(); i++) {
            input = new BufferedReader(new FileReader(listOfOffspringFiles.get(i)));
            line = null; //not declared within while loop
            lineCounter = 0;
            while ((line = input.readLine()) != null && lineCounter < populationSize) {
                uniqueInd.add(line);
                lineCounter++;
            }
            outUnique.write(String.valueOf(uniqueInd.size()));
            outUnique.write(",");
            outUnique.write(String.valueOf(uniqueInd.size() - previousSize));
            outUnique.newLine();
            previousSize = uniqueInd.size();
        }
        outUnique.flush();
        outUnique.close();
    }

    public static void writeFilenames(String folderPath, LinkedList<File> listOfPopulationFiles) throws IOException {
        FileWriter filesFile = new FileWriter(folderPath + System.getProperty("file.separator") + "files.csv");
        BufferedWriter outFiles = new BufferedWriter(filesFile);
        outFiles.write("Filenames");
        outFiles.newLine();
        for (int i = 0; i < listOfPopulationFiles.size(); i++) {
            outFiles.write(listOfPopulationFiles.get(i).getName().substring(0, listOfPopulationFiles.get(i).getName().length() - 4));
            outFiles.newLine();
        }
        outFiles.close();
    }

    public static void computeMetrics(int nrOfobejctives, int populationSize, File metricsFolder, LinkedList parsedFiles) throws FileNotFoundException, IOException {
        double[] maxObjectives = getmaxObjectives(nrOfobejctives, parsedFiles);
        //we have all the required values
        //compute hypervolume for all the files
        computeHypervolumeAndSevenPoint(nrOfobejctives, populationSize, maxObjectives, metricsFolder, "hypervolume.csv", "7point.csv", parsedFiles);
    }

    public static void computeHypervolumeAndSevenPoint(int nrOfobejctives, int populationSize, double[] maxObjectives, File metricsFolder, String hypervolumeFileName, String sevenPointFileName, LinkedList parsedFiles) {
        long StartTime = System.currentTimeMillis();
        HypervolumeNoTruePareto hypervolume = new HypervolumeNoTruePareto();
        SevenPointAverageDistance sevenPointAverageDistance = new SevenPointAverageDistance();
        String fPath = metricsFolder.getAbsolutePath() + System.getProperty("file.separator");

        try {
            FileWriter hypervolumeFile = new FileWriter(fPath + hypervolumeFileName);
            FileWriter sevenPointFile = new FileWriter(fPath + sevenPointFileName);
            BufferedWriter outHyp = new BufferedWriter(hypervolumeFile);
            BufferedWriter out7P = new BufferedWriter(sevenPointFile);
            outHyp.write("Hypervolume per generation (" + fPath + hypervolumeFileName + "),");
            outHyp.write("Hypervolume for all generated individuals (" + fPath + hypervolumeFileName + ")");
            outHyp.newLine();

            out7P.write("7 Point Average Distance per generation (" + fPath + sevenPointFileName + "),");
            out7P.write("7 Point Average Distance for all generated individuals (" + fPath + sevenPointFileName + ")");
            out7P.newLine();
            double[][] allInd = (double[][]) parsedFiles.get(0);
            repairParetoOptimalSet(allInd, populationSize, nrOfobejctives);
            int progress = 0;
            for (double[][] parsedFile : (LinkedList<double[][]>) parsedFiles) {
                //repairing Pareto optimal set = removing objectives with the value 0 and replacing them with the first individual of the current pop
                repairParetoOptimalSet(parsedFile, populationSize, nrOfobejctives);
                double value = hypervolume.hypervolume(parsedFile, maxObjectives, nrOfobejctives);
                outHyp.write(String.valueOf(value));
                value = sevenPointAverageDistance.compute(parsedFile, maxObjectives, nrOfobejctives);
                out7P.write(String.valueOf(value));
                outHyp.write(",");
                out7P.write(",");
                double temp[][] = new double[allInd.length + parsedFile.length][nrOfobejctives];
                for (int i = 0; i < allInd.length; i++) {
                    System.arraycopy(allInd[i], 0, temp[i], 0, nrOfobejctives);
                }
                for (int i = 0; i < parsedFile.length; i++) {
                    System.arraycopy(parsedFile[i], 0, temp[allInd.length + i], 0, nrOfobejctives);
                }
                allInd = temp;
                System.out.println((progress / (parsedFiles.size() + 0.0)) * 100 + "%");
                value = hypervolume.hypervolume(allInd, maxObjectives, nrOfobejctives);
                outHyp.write(String.valueOf(value));
                value = sevenPointAverageDistance.compute(allInd, maxObjectives, nrOfobejctives);
                out7P.write(String.valueOf(value));
                outHyp.newLine();
                out7P.newLine();
                //generateImage(nrOfobejctives, parsedFile, maxObjectives, fPath);
                progress++;
                outHyp.flush();
                out7P.flush();
            }
            //Close the output stream
            outHyp.close();
            out7P.close();

            FileWriter fstream = new FileWriter(metricsFolder.getAbsolutePath() + System.getProperty("file.separator") + "info.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Number of files: " + parsedFiles.size());


            out.newLine();
            out.write("Time needed to compute the metrics (seconds):" + ((System.currentTimeMillis() - StartTime) / 1000.0));
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void computeHypervolumeTwoSetDifference(int nrOfobejctives, int[] populationSize, double[] maxObjectives, File metricsFolder, LinkedList<LinkedList> parsedFiles) {
        long StartTime = System.currentTimeMillis();
        HypervolumeTwoSetDiference hypervolume = new HypervolumeTwoSetDiference();
        String fPath = metricsFolder.getAbsolutePath() + System.getProperty("file.separator");

        try {
            double progress = 0;

            double fullSize =1;
            for(int i=1;i<=parsedFiles.size();i++){
                fullSize *= i;
            }

            int count = 0;

            for (int i = 0; i < parsedFiles.size(); i++) {
                for (int j = 0; j < parsedFiles.size(); j++) {
                    if (i != j) {
                        String hypervolumeFileName = "hypervolumeTwoSetDifference_" + i + "_" + j + ".csv";
                        FileWriter hypervolumeFile = new FileWriter(fPath + hypervolumeFileName);
                        BufferedWriter outHyp = new BufferedWriter(hypervolumeFile);

                        outHyp.write("Hypervolume per generation (" + fPath + hypervolumeFileName + ")");
                        outHyp.newLine();

                        LinkedList<double[][]> firstLinkedList = parsedFiles.get(i);
                        LinkedList<double[][]> secondLinkedList = parsedFiles.get(j);

                        int minSize = 0;
                        if (firstLinkedList.size() < secondLinkedList.size()) {
                            minSize = firstLinkedList.size();
                        } else {
                            minSize = secondLinkedList.size();
                        }

                        for (int currentFront = 0; currentFront < minSize; currentFront++) {                           

                            double[][] firstFront = (double[][]) firstLinkedList.get(currentFront);
                            repairParetoOptimalSet(firstFront, populationSize[i], nrOfobejctives);

                            double[][] secondFront = (double[][]) secondLinkedList.get(currentFront);
                            repairParetoOptimalSet(secondFront, populationSize[j], nrOfobejctives);

                            double firstDifference = hypervolume.hypervolumeTwoSetDifference(firstFront, secondFront, maxObjectives, nrOfobejctives);
                            double secondDifference = hypervolume.hypervolumeTwoSetDifference(secondFront, firstFront, maxObjectives, nrOfobejctives);
                            double firstHV  = hypervolume.hypervolume(firstFront, maxObjectives, nrOfobejctives);
                            double secondHV = hypervolume.hypervolume(secondFront, maxObjectives, nrOfobejctives);
                            outHyp.write(String.valueOf(firstDifference));
                            outHyp.write(","+ String.valueOf(firstHV));
                            outHyp.write(","+ String.valueOf(secondHV));
                            outHyp.write(","+ String.valueOf(secondDifference));
                            outHyp.write("\n");
                            double newProgress = progress + ((double)100/(minSize * fullSize));
                            if((int)newProgress!=(int)progress){
                                progress = newProgress;
                                System.out.println("Computing: "+(int)progress+"%");
                            }
                        }

                        //Close the output stream
                        outHyp.close();
                        count++;
                        progress = (count/fullSize *100);
                        System.out.println("Computing: "+(int)progress+"%");
                    }
                }

            }


            FileWriter fstream = new FileWriter(metricsFolder.getAbsolutePath() + System.getProperty("file.separator") + "info.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Number of files: " + parsedFiles.size());


            out.newLine();
            out.write("Time needed to compute the metrics (seconds):" + ((System.currentTimeMillis() - StartTime) / 1000.0));
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinkedList parseFiles(int nrOfobejctives, int populationSize, List<File> listOfPopulationFiles) throws FileNotFoundException, IOException {
        boolean skipFile = false;
        double[] objectives;
        LinkedList parsedFiles = new LinkedList();
        for (int i = 0; i < listOfPopulationFiles.size(); i++) {
            skipFile = false;
            if (listOfPopulationFiles.get(i).isFile()) {// one file
//                System.out.println("Computing metrics for: " + listOfFiles[i].getName());
                double[][] paretoOptimalSet = new double[populationSize][nrOfobejctives];//TODO
                BufferedReader input = new BufferedReader(new FileReader(listOfPopulationFiles.get(i)));
                String line = null; //not declared within while loop
                line = input.readLine();//skip the headder
                int lineCounter = 0;
                boolean skipLine = false;
                while ((line = input.readLine()) != null && lineCounter < populationSize) {

                    skipLine = false;
                    objectives = new double[nrOfobejctives];
                    StringTokenizer tokenizer = new StringTokenizer(line, ",");
                    try {
                        while (tokenizer.hasMoreTokens()) {
                            for (int k = 0; k < nrOfobejctives - 1; k++) {
                                objectives[k] = objectives[k + 1];//shifting the objectives values with one position
                            }
                            objectives[nrOfobejctives - 1] = Double.parseDouble(tokenizer.nextToken());//ading at the end of the array the newest value
//                        System.out.println(objectives[nrOfobejctives - 1]);
                            if (objectives[nrOfobejctives - 1] >= 1.7976931348623157E+306) {
                                skipLine = true;
                                System.out.println("Skip line:" + objectives[nrOfobejctives - 1]);
                            }

                        }
                    } catch (NumberFormatException e) {
                        skipFile = true;
                    }
                    for (int k = 0; k < nrOfobejctives; k++) {

                        if (objectives[k] <= 0) {
                            System.out.println("Skip file");
                            skipFile = true;
                        }
                    }
                    //now we shoud have in the objectives the last "nrOfObjectives" values from a line
                    //we have to determine the maximum for each objective
                    if (!skipLine) {

                        //now in objectives we have all the objectives of one line - we now have to add them to a population
//                        System.out.print(Arrays.toString(objectives) + "#");
//                        System.out.println("");
                        paretoOptimalSet[lineCounter] = objectives;
                        lineCounter++;
                    }
                }
                if (!skipFile) {
                    parsedFiles.add(paretoOptimalSet);
                } else {
                    System.out.println("Skiped file " + listOfPopulationFiles.get(i).getName() + " it contained values of 0 for objectives");
                }
            }
        }
        return parsedFiles;
    }

    public static double[] getmaxObjectives(int nrOfObjectives, LinkedList parsedFiles) {
        double[] maxObjectives = new double[nrOfObjectives];
        for (int i = 0; i < parsedFiles.size(); i++) {
            for (double[] objectives : ((double[][]) parsedFiles.get(i))) {
                for (int k = 0; k < nrOfObjectives; k++) {
                    if (objectives[k] > maxObjectives[k]) {
                        maxObjectives[k] = objectives[k];
                    }
                }
            }
        }
        return maxObjectives;
    }

    public static void generateImage(int nrOfobejctives, double[][] parsedFile, double[] maxObjectives, String fPath) throws IOException {
        if (nrOfobejctives == 2) {
            BufferedImage image;
            XYSeries series;
            XYSeriesCollection dataset;
            JFreeChart chart;
            series = new XYSeries("XYGraph");
            for (int i = 0; i < parsedFile.length; i++) {
                series.add(parsedFile[i][0], parsedFile[i][1]);

            }
            series.add(maxObjectives[0], 0);
            series.add(0, maxObjectives[1]);
            dataset = new XYSeriesCollection();
            dataset.addSeries(series);
            chart = ChartFactory.createScatterPlot("XY Chart", // Title
                    "x-axis", // x-axis Label
                    "y-axis", // y-axis Label
                    dataset, // Dataset
                    PlotOrientation.VERTICAL, // Plot Orientation
                    true, // Show Legend
                    true, // Use tooltips
                    false // Configure chart to generate URLs?
                    );
            image = chart.createBufferedImage(700, 500);
            String result = fPath + System.currentTimeMillis() + ".png";
            ChartUtilities.saveChartAsPNG(new File(result), chart, 700, 700);
        }

    }

    /**
     * repairing Pareto optimal set = removing objectives with the value 0 and replacing them with the first individual of the current pop
     * @param paretoOptimalSet
     * @param populationSize
     * @param nrOfobejctives
     */
    public static void repairParetoOptimalSet(double[][] paretoOptimalSet, int populationSize, int nrOfobejctives) {
        for (int k = 0; k < populationSize; k++) {
            for (int l = 0; l < nrOfobejctives; l++) {
                if (paretoOptimalSet[k][l] == 0) {
                    paretoOptimalSet[k][l] = paretoOptimalSet[0][l];
                }
            }
        }
    }

    public static SolutionSet readPopulation(String pathToFile, int populationSize, int nrOfObjectives) throws FileNotFoundException, IOException {
        File filePop1 = new File(pathToFile);
        SolutionSet pop = new SolutionSet(populationSize);
        BufferedReader input = new BufferedReader(new FileReader(filePop1));
        String line = null; //not declared within while loop
        line = input.readLine();//skip the headder
        int lineCounter = 0;
        boolean skipLine = false;
        while ((line = input.readLine()) != null && lineCounter < populationSize) {
            skipLine = false;
            Solution sPop1 = new Solution(nrOfObjectives);
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            try {
                while (tokenizer.hasMoreTokens()) {
                    for (int k = 0; k < nrOfObjectives - 1; k++) {
                        sPop1.setObjective(k, sPop1.getObjective(k + 1));//shifting the objectives values with one position
                    }
                    sPop1.setObjective(nrOfObjectives - 1, Double.parseDouble(tokenizer.nextToken()));//ading at the end of the array the newest value
//                        System.out.println(objectives[nrOfobejctives - 1]);
                    if (sPop1.getObjective(nrOfObjectives - 1) >= 1.7976931348623157E+306) {
                        skipLine = true;
                        System.out.println("Skip line");
                    }
                }
            } catch (NumberFormatException e) {
                skipLine = true;
            }
            for (int k = 0; k < nrOfObjectives; k++) {
                if (sPop1.getObjective(k) <= 0) {
                    System.out.println("Skip file");
                    skipLine = true;
                }
            }
            //now we shoud have in the objectives the last "nrOfObjectives" values from a line
            //we have to determine the maximum for each objective
            if (!skipLine) {
                //now in objectives we have all the objectives of one line - we now have to add them to a population
//                        System.out.print(Arrays.toString(objectives) + "#");
//                        System.out.println("");
                pop.add(sPop1);
            }
        }
        return pop;
    }

    public static LinkedList<File> getListOfFiles(String folderPath, String prefix) {
        File folder = new File(folderPath);
        File[] listOfFilesTemp = folder.listFiles();
        //sort the files
        Arrays.sort(listOfFilesTemp);
        System.out.println(listOfFilesTemp);
        LinkedList<File> listOfPopulationFiles = new LinkedList<File>();
        for (int i = 0; i < listOfFilesTemp.length; i++) {
            if (listOfFilesTemp[i].isFile() && listOfFilesTemp[i].getName().startsWith(prefix) && listOfFilesTemp[i].getName().endsWith(".csv")) {
                listOfPopulationFiles.add(listOfFilesTemp[i]);
            }
        }
        return listOfPopulationFiles;
    }
}
