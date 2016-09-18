package edu.upenn.sprout.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import edu.upenn.sprout.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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

  public F.Promise<Result> create() {
    JsonNode node = request().body().asJson();
    String email = node.get("email").asText();
    String name = node.get("name").asText();

    return asPromise(accountService.createAccount(email, name));
  }

  /**
   *
   * @param future
   * @param <T>
   * @return
   */
  public static <T> F.Promise<T> asPromise(CompletableFuture<T> future) {
    F.RedeemablePromise<T> promise = F.RedeemablePromise.empty();
    future.whenCompleteAsync((res, err) -> {
      if (err != null) {
        promise.failure(err);
      }
      else {
        promise.success(res);
      }
    });
    return promise;
  }

}
