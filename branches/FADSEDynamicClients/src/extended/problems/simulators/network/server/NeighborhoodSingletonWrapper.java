/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators.network.server;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Radu
 */
/**
 * A static container for a single instance of the Singleton
 */
final public class NeighborhoodSingletonWrapper {   

   /**
    * A reference to a possibly alternate factory.
    */

   static private NeighborhoodSingletonFactory _factory = null;

   /**
    * A reference to the current instance.
    */
   static private INeighborhood _instance = null;


   /**
    * This is the default factory method.
    * It is called to create a new Singleton when
    * a new instance is needed and _factory is null.
    */
   static private INeighborhood makeInstance() throws ParserConfigurationException {
       String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        String defaultXmlFilePath = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "neighborConfig.xml";
      return new DynamicNeighborhood(defaultXmlFilePath);
   }

   /**
    * This is the accessor for the Singleton.
    */
   static public synchronized INeighborhood getInstance() throws ParserConfigurationException {
      if(null == _instance) {
            try {
                _instance = (null == _factory) ? makeInstance() : _factory.MakeInstance();
            } catch (Exception ex) {
                Logger.getLogger(NeighborhoodSingletonWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
      }
      return _instance;
   }

   /**
    * Sets the factory method used to create new instances.
    * You can set the factory method to null to use the default method.
    * @param factory The Singleton factory
    */
   static public synchronized void setFactory(NeighborhoodSingletonFactory factory) {
      _factory = factory;
   }

   /**
    * Sets the current Singleton instance.
    * You can set this to null to force a new instance to be created the
    * next time instance() is called.
    * @param instance The Singleton instance to use.
    */
   static public synchronized void setInstance(INeighborhood instance) {
      _instance = instance;
   }

}
