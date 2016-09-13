package edu.upenn.sprout.db;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jtcho
 */
@Entity
@Table(name = "Accounts")
public class Account {

  private Long id;
  private String email;
  private Date createdOn;

  public Account() {}

  public Account(String email) {
    this.email = email;
    this.createdOn = new Date(System.currentTimeMillis());
  }

  @Id
  @GeneratedValue(generator="increment")
  @GenericGenerator(name="increment", strategy="increment")
  public Long getId() {
    return id;
  }

}
