package edu.upenn.sprout.filters;

import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;

/**
 * @author jtcho
 * @version 2016.11.10
 */
public class Filters extends DefaultHttpFilters {
  @Inject public Filters(CORSFilter corsFilter) {
    super(corsFilter);
  }
}
