package edu.upenn.sprout.controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import static play.libs.Json.toJson;

/**
 * @author jtcho
 */
public class RandomController extends Controller {

  @Inject
  public RandomController() {}

  public static Result random() {
    return ok(toJson(Math.random()));
  }

}
