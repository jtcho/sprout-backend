package edu.upenn.sprout.modules;

import com.google.inject.AbstractModule;
//import edu.upenn.sprout.db.DynamoDBClient;
//import edu.upenn.sprout.db.PostgreSQLClient;
//import edu.upenn.sprout.services.AccountService;
import edu.upenn.sprout.services.DocumentDiffPatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Guice bindings module for binding eager singletons.
 *
 * @author jtcho
 * @version 2016.09.17
 */
public class SproutServiceModule extends AbstractModule {

  private final Logger LOG = LoggerFactory.getLogger(SproutServiceModule.class);

  @Override
  protected void configure() {
    LOG.info("Configuring the SproutServiceModule.");

    bind(DocumentDiffPatchService.class).asEagerSingleton();
//    bind(PostgreSQLClient.class).asEagerSingleton();
  }

}
