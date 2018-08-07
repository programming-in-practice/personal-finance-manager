package com.pfm.account;

import static com.pfm.config.MessagesProvider.ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_BALANCE;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_ANDRZEJ_BALANCE_1_000_000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_JACEK_BALANCE_1000;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_LUKASZ_BALANCE_1124;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MARCIN_BALANCE_10_99;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_MARIUSZ_BALANCE_200;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_RAFAL_BALANCE_0;
import static com.pfm.helpers.TestAccountProvider.ACCOUNT_SLAWEK_BALANCE_9;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.IntegrationTestsBase;
import java.math.BigDecimal;
import org.junit.Test;

public class AccountControllerIntegrationTest extends IntegrationTestsBase {

  @Test
  public void shouldAddAccount() throws Exception {
    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(ACCOUNT_JACEK_BALANCE_1000))))
        .andExpect(status().isOk());
  }

  @Test
  @Parameters(method = "emptyAccountNameParameters")
  public void shouldReturnErrorCausedByEmptyNameField(String name, BigDecimal balance)
      throws Exception {
    AccountRequest accountRequest = AccountRequest.builder().name(name).balance(balance).build();

    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_NAME))))
        .andExpect(jsonPath("$[1]", is(getMessage(EMPTY_ACCOUNT_BALANCE))));
  }

  @SuppressWarnings("unused")
  private Collection<Object[]> emptyAccountNameParameters() {
    return Arrays.asList(new Object[][]{
        {"", null},
        {" ", null},
        {"    ", null},
    });
  }

  @Test
  public void shouldGetAccountById() throws Exception {
    long accountId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_LUKASZ_BALANCE_1124));

    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void shouldReturnErrorCausedByNotExistingId() throws Exception {
    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetAllAccounts() throws Exception {
    callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_ANDRZEJ_BALANCE_1_000_000));
    callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_SLAWEK_BALANCE_9));

    mockMvc
        .perform(get(ACCOUNTS_SERVICE_PATH))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Sebastian Revolut USD")))
        .andExpect(jsonPath("$[0].balance", is("1000000.00")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].name", is("Cash")))
        .andExpect(jsonPath("$[1].balance", is("9.00")));
  }

  @Test
  public void shouldUpdateAccount() throws Exception {
    long accountId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0));

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(ACCOUNT_MARIUSZ_BALANCE_200))))
        .andDo(print())
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Mateusz mBank saving account")))
        .andExpect(jsonPath("$.balance", is(equalTo("200.00"))));
  }

  @Test
  public void shouldUpdateAccountWithUpdatedAccountSameNameAsBefore() throws Exception {
    long accountId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0));
    AccountRequest updatedAccount = AccountRequest.builder()
        .name(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0).getName())
        .balance(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0).getBalance()
            .add(BigDecimal.TEN)).build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andDo(print())
        .andExpect(status().isOk());

    mockMvc.perform(get(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name",
            is(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0).getName())))
        .andExpect(jsonPath("$.balance", is(equalTo("10.00"))));
  }

  @Test
  public void shouldReturnErrorCauseByDuplicatedNameWhileUpdatingAccount() throws Exception {
    callRestServiceToAddAccountAndReturnId(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0));
    long accountJacekId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_JACEK_BALANCE_1000));
    AccountRequest updatedAccount = AccountRequest.builder()
        .name(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0).getName())
        .balance(ACCOUNT_JACEK_BALANCE_1000.getBalance()).build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountJacekId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]",
            is(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  @Test
  public void shouldReturnErrorCauseByNullName() throws Exception {
    callRestServiceToAddAccountAndReturnId(ACCOUNT_ADAM_BALANCE_0);
    long accountJacekId = callRestServiceToAddAccountAndReturnId(ACCOUNT_JACEK_BALANCE_1000);

    Account updatedAccount = Account.builder()
        .name(null)
        .balance(ACCOUNT_JACEK_BALANCE_1000.getBalance())
        .build();

    mockMvc.perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountJacekId)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(updatedAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]",
            is(getMessage(EMPTY_ACCOUNT_NAME))));
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInUpdateMethod() throws Exception {

    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0))))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCauseByNotValidAccountUpdateMethod() throws Exception {
    long accountId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0));
    AccountRequest accountToUpdate = AccountRequest.builder()
        .name("")
        .balance(convertAccountToAccountRequest(ACCOUNT_RAFAL_BALANCE_0).getBalance())
        .build();

    mockMvc
        .perform(put(ACCOUNTS_SERVICE_PATH + "/" + accountId)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountToUpdate)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldDeleteAccount() throws Exception {
    long accountId = callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_MARCIN_BALANCE_10_99));

    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + accountId))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorCauseByNotExistingIdInDeleteMethod() throws Exception {
    callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_MARCIN_BALANCE_10_99));

    mockMvc
        .perform(delete(ACCOUNTS_SERVICE_PATH + "/" + NOT_EXISTING_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnErrorCausedByExistingAccountName() throws Exception {
    callRestServiceToAddAccountAndReturnId(
        convertAccountToAccountRequest(ACCOUNT_LUKASZ_BALANCE_1124));

    mockMvc.perform(post(ACCOUNTS_SERVICE_PATH)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(convertAccountToAccountRequest(ACCOUNT_LUKASZ_BALANCE_1124))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

  private AccountRequest convertAccountToAccountRequest(Account account) {
    return AccountRequest.builder().name(account.getName()).balance(account.getBalance()).build();

  }
}