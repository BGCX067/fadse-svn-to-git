/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package defaultPackage;

/**
 *
 * @author Radu
 */
public class Boot100Clients implements Runnable {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Boot100Clients boot = new Boot100Clients();
            Thread thread = new Thread(boot);
            thread.start();
        }
    }

    // This method is called when the thread runs
    public void run() {
        String[] args = new String[1];
        BootClient.main(args);
    }
}
