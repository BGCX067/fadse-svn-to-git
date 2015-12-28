/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.gap;

import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author jahrralf
 */
class GAPDirectoryDustman {

    private static LinkedList<File> history = new LinkedList<File>();

    static void register(File benchmarkDirectory) {
        history.addFirst(benchmarkDirectory);
        System.out.println("Dustman here, new directory has been added, history is now " + history.size());

        if (history.size() > 64) {
            System.out.println("Dustman here, I am going to delete directory " + benchmarkDirectory);
            File item = history.pollLast();
            deleteDirectory(item);
        }
    }

    private static boolean deleteDirectory(File path) {
        boolean result = false;
        try {
            if (path.exists()) {
                File[] files = path.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            result = path.delete();
        } catch (Exception e) {
            System.out.println("Dustman here, Exception while deleting: " + e.getMessage());
        }
        return result;
    }
}
