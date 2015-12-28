/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Radu
 */
public class MOResultsHelper {

    public static void main(String[] args) throws IOException, URISyntaxException {
        // DuplicateResults(new File("D:\\fadse\\output\\GAP\\100\\SPEA2"), new File("D:\\fadse\\output\\GAP\\200\\SPEA2"),"filled");  

        LinkedList<String> offsprings = new LinkedList<String>();
        offsprings.add("offMONSGAII");
        offsprings.add("offMOSPEA2");
        ComputeNumberOfGeneratedOffsprings(new File("D:\\fadse\\output\\GAP\\100\\RaduAlg"), offsprings);
    }

    public static void DuplicateResults(File inputfolder, File outputFolder, String searchPattern) throws IOException {
        outputFolder.mkdirs();
        Files.list(Paths.get(inputfolder.getAbsolutePath())).forEach(filePath -> {
            if (filePath.getFileName().toString().startsWith(searchPattern)) {
                DuplicateFile(filePath, outputFolder);
            }
        });
    }

    public static void ComputeNumberOfGeneratedOffsprings(File sourceFolder, List<String> patterns) throws IOException {
        HashMap<String, LinkedList<String>> offspingFiles = new HashMap<String, LinkedList<String>>();

        for (String pattern : patterns) {
            LinkedList<String> currentList = new LinkedList<String>();
            offspingFiles.put(pattern, currentList);

            Files.list(Paths.get(sourceFolder.getAbsolutePath())).sorted().forEach(filePath -> {
                if (filePath.getFileName().toString().startsWith(pattern)) {
                    currentList.add(filePath.toFile().toString());
                }
            });
        }

        int count = offspingFiles.get(patterns.get(0)).size();

        String newFolder = sourceFolder.toString() + "\\actualOffspings" + System.currentTimeMillis() + ".csv";
        BufferedWriter bw = Files.newBufferedWriter(Paths.get(newFolder), StandardOpenOption.CREATE);

        for (String pattern : patterns) {
            bw.append(pattern + ",");
        }

        bw.append("\n");

        for (int i = 0; i < count; i++) {
            String line = "";
            for (String pattern : patterns) {
                long nr = Files.lines(Paths.get(offspingFiles.get(pattern).get(i))).count() - 1;
                line += nr + ",";
            }
            bw.append(line + "\n");
        }
        bw.flush();
        bw.close();

        newFolder = sourceFolder.toString() + "\\actualPercentages" + System.currentTimeMillis() + ".csv";
        BufferedWriter bw1 = Files.newBufferedWriter(Paths.get(newFolder), StandardOpenOption.CREATE);

        for (String pattern : patterns) {
            bw1.append(pattern + ",");
        }

        bw1.append("\n");

        Files.list(Paths.get(sourceFolder.getAbsolutePath())).sorted().forEach(filePath -> {
            if (filePath.getFileName().toString().startsWith("metrics")) {
                try {
                    List<String> lines = Files.readAllLines(filePath);
                    String currentLine = lines.get(lines.size() - 1);
                    bw1.append(currentLine.replaceAll(";", ",") + "\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        });
        bw1.flush();
        bw1.close();
    }

    private static void DuplicateFile(Path filePath, File outputFolder) {
        try {

            String newFolder = outputFolder.toString() + "\\" + filePath.getFileName();

            BufferedWriter bw = Files.newBufferedWriter(Paths.get(newFolder), StandardOpenOption.CREATE);

            String str = Files.lines(filePath).reduce("", (x, y) -> x + "\n" + y);
            str = str.substring(1);   //remove first \n         
            WriteLine(bw, str);

            str = str.substring(str.indexOf("\n") + 1); //remove header line
            WriteLine(bw, "\n" + str);
            bw.flush();
            bw.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void WriteLine(BufferedWriter bw, String str) {
        try {
            bw.append(str);
        } catch (IOException ex) {
            Logger.getLogger(MOResultsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
