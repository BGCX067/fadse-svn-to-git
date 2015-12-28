/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.tem;

import extended.problems.simulators.gap.LruFileCache;

/**
 *
 * @author jahrralf
 */
public class TemDirectoryDustman extends LruFileCache {
    private static TemDirectoryDustman instance = new TemDirectoryDustman();

    public static TemDirectoryDustman getInstance() {
        return instance;
    }

    private TemDirectoryDustman() {
        super(5);
    }
}
