package app.cryptocize.com.cryptocize.models;

import org.joda.money.Money;

/**
 * Created by choyiny on 16/9/2017.
 */

public class AccountInformation {
  private Money btc;
  private String name;
  private String email;

  public Money getBtc() {
    return btc;
  }

  public void setBtc(Money btc) {
    this.btc = btc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
