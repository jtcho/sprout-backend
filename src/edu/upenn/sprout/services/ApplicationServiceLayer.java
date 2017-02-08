package edu.upenn.sprout.services;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by igorpogorelskiy on 2/7/17.
 */
@Singleton
public class ApplicationServiceLayer {

    private final Logger LOG = LoggerFactory.getLogger(ApplicationServiceLayer.class);

    /**
     * Temporary "DB" of applications. For now, simply maps the ID of the app to the app name.
     */
    private static Map<String, String> applications;

    protected ApplicationServiceLayer() {
      applications = new ConcurrentHashMap<>();
    }

    /**
     * Add the application to a "DB" of applications hosted by Sprout
     * @param ID ID of the app (should be generated at first creation)
     * @param name Name of the application to bind to
     * @return true upon success, false otherwise
     */
    protected boolean addApplicationWithID(String ID, String name) {
        if (!applications.containsKey(ID)) {
            applications.put(ID, name);
            return true;
        }
        return false;
    }

    /**
     * Get the matching app to the given ID
     * @param ID id of the app
     * @return application name or null if it does not exist
     */
    protected String getApplicationWithID(String ID) {
      return applications.get(ID);
    }

    /**
     * Determines if an application has already been registered
     * @param ID id of the application in question
     * @return true, if ID is registered, false otherwise
     */
    protected boolean isRegisteredID(String ID) {
      return applications.containsKey(ID);
    }

    /**
     * Convenience method for determining if an application with a certain name
     * has been registered already. However, this should not be used
     * unless necessary since multiple apps can have the same name and this
     * will not tell you if they are dups. Also, it must traverse the entire value
     * set of the map...
     * @param name application name
     * @return true if there exists an ID which is matched to the name, false otherwise
     */
    protected boolean isRegisteredName(String name) {
      for (Map.Entry<String, String> entry : applications.entrySet()) {
        String appName = entry.getValue();
        if (appName.equals(name)) {
          return true;
        }
      }
      return false;
    }
}