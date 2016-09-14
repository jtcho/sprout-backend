package edu.upenn.sprout.controllers;

import edu.upenn.sprout.db.Account;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 */
public class AccountController extends Controller {

  public AccountController() {
  }

  @Transactional
  public Result createAccount() {
    String email = request().body().asJson().get("email").asText();
    try {
      Account account = new Account(email);
      EntityManager em = JPA.em();
      em.persist(account);
//      em.flush();
      return created(account.toString());
    } catch (Exception e) {
      System.err.println("Received internal server error with message: " + e);
      return internalServerError(e.toString());
    }
  }

  @Transactional
  public Result getAccount(Long id) {
    try {
      Account account = JPA.em().find(Account.class, id);
      return ok(account.toString());
    } catch (Exception e) {
      System.err.println("Received internal server error with message: " + e);
      return internalServerError(e.toString());
    }
  }

}
