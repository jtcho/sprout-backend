package edu.upenn.sprout.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import edu.upenn.sprout.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import static edu.upenn.sprout.util.FutureUtils.asPromise;

import javax.inject.Inject;

/**
 * @author jtcho
 * @version 2016.09.17
 */
public class AccountController extends Controller {

  private final Logger LOG = LoggerFactory.getLogger(AccountController.class);
  private final AccountService accountService;

  @Inject
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  /**
   * Creates an instance of an Account and saves it in the database.
   *
   * @return an unresolved promise enclosing the result of the query
   */
  public F.Promise<Result> create() {
    JsonNode node = request().body().asJson();
    String email = node.get("email").asText();
    String name = node.get("name").asText();

    return asPromise(accountService.createAccount(email, name));
  }

}
