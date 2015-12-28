package defaultPackage;




import extended.qualityIndicator.MetricsUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import utils.Utils;

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
/**
 *
 * @author Horia Calborean
 */
public class ComputeMetrics {

    public static void main(String[] args) throws IOException {


        String folderPath;
        int nrOfobejctives = 2;
        int populationSize = 100;
        if (args.length < 3) {
            System.out.println("Specify path to folder");
            folderPath = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            System.out.println("Specify nr of objectives");
            nrOfobejctives = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
            System.out.println("Specify the population size");
            populationSize = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
        } else {
            folderPath = args[0];
            nrOfobejctives = Integer.parseInt(args[1]);
            populationSize = Integer.parseInt(args[1]);
        }
        LinkedList<File> listOfPopulationFiles = MetricsUtil.getListOfFiles(folderPath, "filled");
        LinkedList<File> listOfOffspringFiles = MetricsUtil.getListOfFiles(folderPath, "offspring");
        MetricsUtil.writeFilenames(folderPath, listOfPopulationFiles);
        File metricsFolder = new File(folderPath + System.getProperty("file.separator") + "metrics" + System.currentTimeMillis());
        if (metricsFolder.mkdir()) {
            LinkedList parsedFiles = MetricsUtil.parseFiles(nrOfobejctives, populationSize, listOfPopulationFiles);
            MetricsUtil.computeMetrics(nrOfobejctives, populationSize, metricsFolder, parsedFiles);
            MetricsUtil.computeUniqueIndividuals(populationSize, listOfPopulationFiles.get(0), listOfOffspringFiles, metricsFolder,"unique.csv");
            MetricsUtil.computeUniqueIndividuals(populationSize, listOfPopulationFiles.get(0), listOfPopulationFiles, metricsFolder,"population_increase.csv");
        } else {
            System.out.println("Directory was not created");
        }
    }

   
}
