package com.pfm.account.performance;

import static com.pfm.helpers.TestUsersProvider.userMarian;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;
import com.pfm.account.AccountRequest;
import com.pfm.auth.User;
import com.pfm.auth.UserDetails;
import com.pfm.currency.Currency;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// ENHANCEMENT add tests for other services
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class InvoicePerformanceTestBase {

  static final int THREAD_COUNT = 24;

  private static final String ACCOUNTS_SERVICE_PATH = "http://localhost:%d/accounts";
  private static final String CURRENCIES_SERVICE_PATH = "http://localhost:%d/currencies";

  private static final String USERS_SERVICE_PATH = "http://localhost:%d/users";

  @Qualifier("pfmObjectMapper")
  @Autowired
  protected ObjectMapper mapper;

  final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());

  String token;

  @LocalServerPort
  private int port;

  private Account[] getAccounts() {

    return given()
        .when()
        .header("Authorization", token)
        .get(invoiceServicePath())
        .getBody()
        .as(Account[].class);
  }

  BigDecimal getRandomBalance() {
    return BigDecimal.valueOf((long) (Math.random() * Integer.MAX_VALUE)).setScale(2, RoundingMode.CEILING);
  }

  String getRandomName() {
    return UUID.randomUUID().toString();
  }

  String invoiceServicePath(long id) {
    return invoiceServicePath() + "/" + id;
  }

  private String invoiceServicePath() {
    return String.format(ACCOUNTS_SERVICE_PATH, port);
  }

  private String currenciesServicePath() {
    return String.format(CURRENCIES_SERVICE_PATH, port);
  }

  @PostConstruct
  public void before() throws Exception {
    final User defaultUser = userMarian();

    given()
        .contentType(ContentType.JSON)
        .body(defaultUser)
        .post(usersServicePath() + "/register");
    token = authenticateUserAndgetAccessToken(defaultUser);

    Currency[] currencies = getCurrencies();

    for (int i = 0; i < 10; ++i) {
      Account account = addAndReturnAccount(currencies);

      accounts.add(account);
    }
  }

  protected Currency[] getCurrencies() {
    return given()
        .header("Authorization", token)
        .when()
        .get(currenciesServicePath())
        .getBody()
        .as(Currency[].class);
  }

  Account addAndReturnAccount(Currency[] currencies) {
    AccountRequest accountRequest = AccountRequest.builder()
        .name(UUID.randomUUID().toString())
        .balance(getRandomBalance())
        .currencyId(currencies[0].getId())
        .build();

    String response = given()
        .contentType(ContentType.JSON)
        .header("Authorization", token)
        .body(accountRequest)
        .when()
        .post(invoiceServicePath())
        .getBody()
        .asString();

    long accountId = Long.parseLong(response);

    return given()
        .header("Authorization", token)
        .when()
        .get(invoiceServicePath() + "/" + accountId)
        .getBody()
        .as(Account.class);
  }

  @AfterEach
  public void afterCheck() {
    accounts.sort((first, second) -> (int) (first.getId() - second.getId()));

    Account[] accountsFromService = getAccounts();
    assertThat(accountsFromService.length, is(accounts.size()));

    int index = 0;
    for (Account account : accountsFromService) {
      assertThat(account, is(equalTo(accounts.get(index++))));
    }

    for (Account account : accountsFromService) {
      given()
          .when()
          .header("Authorization", token)
          .delete(invoiceServicePath(account.getId()));
    }

    assertThat(getAccounts().length, is(0));
  }

  private UserDetails jsonToAuthResponse(String jsonAuthResponse) throws Exception {
    return mapper.readValue(jsonAuthResponse, UserDetails.class);
  }

  private String json(Object object) throws Exception {
    return mapper.writeValueAsString(object);
  }

  private String authenticateUserAndgetAccessToken(User user) throws Exception {
    String response = given()
        .contentType(ContentType.JSON)
        .body(json(user))
        .post(usersServicePath() + "/authenticate")
        .getBody()
        .print();

    return jsonToAuthResponse(response).getAccessToken();
  }

  AccountRequest convertAccountToAccountRequest(Account account) {
    return AccountRequest.builder()
        .name(account.getName())
        .balance(account.getBalance())
        .currencyId(account.getCurrency().getId())
        .build();
  }

  private String usersServicePath() {
    return String.format(USERS_SERVICE_PATH, port);
  }

  protected void runInMultipleThreads(Runnable task) throws InterruptedException {
    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; ++i) {
      threadPool.submit(task);
    }

    threadPool.shutdown();
    threadPool.awaitTermination(30, TimeUnit.SECONDS);
  }

}
