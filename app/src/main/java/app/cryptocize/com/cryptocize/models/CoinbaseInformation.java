package app.cryptocize.com.cryptocize.models;

import java.io.Serializable;

/**
 * Created by choyiny on 16/9/2017.
 */

public class CoinbaseInformation implements Serializable {


  public String coinbaseUserName = "";


  public CoinbaseInformation(String username) {
    this.coinbaseUserName = username;
  }

  public String getName() {
    return coinbaseUserName;
  }
}
