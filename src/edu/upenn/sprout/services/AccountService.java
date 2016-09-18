package edu.upenn.sprout.services;

import com.google.inject.Singleton;
import edu.upenn.sprout.db.DynamoDBClient;
import edu.upenn.sprout.models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * @author jtcho
 */
@Singleton
public class AccountService {

  private final Logger LOG = LoggerFactory.getLogger(AccountService.class);
  private static final String TABLE_NAME = "Accounts";
  private final DynamoDBClient db;

  @Inject
  public AccountService(DynamoDBClient db) {
    this.db = db;

    try {
      this.db.createTable(TABLE_NAME, "email");
      LOG.info("Created the Accounts table.");
    } catch (Exception e) {
      LOG.info("Tried to create table Accounts, but got error: " + e);
    }

    LOG.info("Registered the AccountService.");
  }

  public CompletableFuture<Result> createAccount(String email, String name) {
    CompletableFuture.completedFuture(Results.internalServerError());
    return db.putItem(TABLE_NAME, new Account(email).setName(name))
        .thenCompose(putItemResult -> CompletableFuture.completedFuture(
            Results.created("Created account with email " + email + " and name " + name + ".")));
  }

}
