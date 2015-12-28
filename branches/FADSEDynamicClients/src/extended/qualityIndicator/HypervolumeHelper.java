/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.qualityIndicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 *
 * @author Radu
 */
public class HypervolumeHelper {

    public static HypervolumeHelperResult ReadDirectories() throws IOException{
            int n = 2;
            System.out.println("Specify number of folders");
            n = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
            int nrOfobejctives = 0;
            int[] populationSizeN = new int[n];
            String[] folderPathN = new String[n];
            LinkedList<LinkedList<File>> listOfPopulationFilesN = new LinkedList<LinkedList<File>>();
            System.out.println("Specify nr of objectives ");
            nrOfobejctives = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
            for (int i = 0; i < n; i++) {
                System.out.println("Specify path to folder " + i);
                folderPathN[i] = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                System.out.println("Specify the population size " + i);
                populationSizeN[i] = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
                listOfPopulationFilesN.add(MetricsUtil.getListOfFiles(folderPathN[i], "filled"));
            }

            File metricsFolder = new File(folderPathN[0] + System.getProperty("file.separator") + "metricsComposed" + System.currentTimeMillis());
            if (metricsFolder.mkdir()) {
                LinkedList<LinkedList> parsedFilesN = new LinkedList<LinkedList>();
                LinkedList<double[]> maxObjectivesN = new LinkedList<double[]>();
                for (int i = 0; i < n; i++) {
                    parsedFilesN.add(MetricsUtil.parseFiles(nrOfobejctives, populationSizeN[i], listOfPopulationFilesN.get(i)));
                    System.out.println("Files found for folder " + i + ":" + parsedFilesN.get(i).size());
                    maxObjectivesN.add(MetricsUtil.getmaxObjectives(nrOfobejctives, parsedFilesN.get(i)));
                }
                double[] maxObjectives = new double[nrOfobejctives];
                for (int i = 0; i < nrOfobejctives; i++) {
                    maxObjectives[i] = max(maxObjectivesN, i);
                }              

                HypervolumeHelperResult result = new HypervolumeHelperResult();

                result.MaxObjectives = maxObjectives;
                result.MetricsFolder = metricsFolder;
                result.NrFolders = n;
                result.NrObjectives = nrOfobejctives;
                result.PopulationSizeN = populationSizeN;
                result.ParsedFilesN = parsedFilesN;

                return result;

            } else {
                //System.out.println("Directory was not created");
                return null;
            }       
    }

    public static double max(LinkedList<double[]> maxObjectivesN, int i) {
        double max = 0;
        for (double[] currentVec : maxObjectivesN) {
            if (currentVec[i] > max) {
                max = currentVec[i];
            }
        }
        return max;
    }
}
