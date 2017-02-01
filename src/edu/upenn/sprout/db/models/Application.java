package edu.upenn.sprout.db.models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author jtcho
 * @version 2016.11.08
 */
@Entity
@Table(name = "applications")
public class Application extends Model {

  @Id
  private Long id;
  private Date registeredOn;

  @Constraints.Required
  @Size(max = 100)
  private String name;

}
