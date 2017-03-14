package edu.upenn.sprout.filters;

import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;

/**
 * @author jtcho
 * @version 2016.11.10
 */
public class Filters extends DefaultHttpFilters {

  private CORSFilter corsFilter;
  @Inject public Filters(CORSFilter corsFilter) {
    this.corsFilter = corsFilter;
  }

  public EssentialFilter[] filters() {
    return new EssentialFilter[] { corsFilter.asJava() };
  }
}
