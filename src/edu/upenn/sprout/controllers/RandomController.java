package edu.upenn.sprout.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import static play.libs.Json.toJson;

/**
 * @author jtcho
 */
public class RandomController extends Controller {

  private final Logger LOG = LoggerFactory.getLogger(RandomController.class);

  public RandomController() {
  }

  public Result random() {
    LOG.info("Generating a random number...");
    return ok(toJson(Math.random()));
  }

}
