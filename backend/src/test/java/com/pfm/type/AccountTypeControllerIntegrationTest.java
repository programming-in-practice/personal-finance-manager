package com.pfm.type;

import static com.pfm.config.MessagesProvider.ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_ACCOUNT_TYPE_NAME;
import static com.pfm.config.MessagesProvider.getMessage;
import static com.pfm.helpers.TestUsersProvider.userMarian;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfm.account.type.AccountType;
import com.pfm.account.type.AccountTypeRequest;
import com.pfm.helpers.IntegrationTestsBase;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;

public class AccountTypeControllerIntegrationTest extends IntegrationTestsBase {

  @BeforeEach
  public void setUp() throws Exception {
    userId = callRestToRegisterUserAndReturnUserId(userMarian());
    token = callRestToAuthenticateUserAndReturnToken(userMarian());
  }

  @Test
  public void shouldAddAccountType() throws Exception {
    // given
    final List<AccountType> accountTypes = accountTypeService.getAccountTypes(userId);
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    // when
    String response =
        mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(JSON_CONTENT_TYPE)
            .content(json(accountTypeRequest)))
            .andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString();

    // then
    Long accountTypeId = Long.parseLong(response);

    accountTypes.add(AccountType.builder().name(accountTypeRequest.getName()).id(accountTypeId).build());
    accountTypes.sort(Comparator.comparing(AccountType::getName));

    mockMvc
        .perform(get(ACCOUNT_TYPE_SERVICE_PATH)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0].id", is(accountTypes.get(0).getId().intValue())))
        .andExpect(jsonPath("$[1].id", is(accountTypes.get(1).getId().intValue())))
        .andExpect(jsonPath("$[2].id", is(accountTypes.get(2).getId().intValue())))
        .andExpect(jsonPath("$[3].id", is(accountTypes.get(3).getId().intValue())))
        .andExpect(jsonPath("$[4].id", is(accountTypes.get(4).getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(accountTypes.get(0).getName())))
        .andExpect(jsonPath("$[1].name", is(accountTypes.get(1).getName())))
        .andExpect(jsonPath("$[2].name", is(accountTypes.get(2).getName())))
        .andExpect(jsonPath("$[3].name", is(accountTypes.get(3).getName())))
        .andExpect(jsonPath("$[4].name", is(accountTypes.get(4).getName())))
        .andExpect(jsonPath("$[0].userId").doesNotExist())
        .andExpect(jsonPath("$[1].userId").doesNotExist())
        .andExpect(jsonPath("$[2].userId").doesNotExist())
        .andExpect(jsonPath("$[3].userId").doesNotExist())
        .andExpect(jsonPath("$[4].userId").doesNotExist());
  }

  @ParameterizedTest
  @MethodSource("emptyAccountTypeNameParameters")
  public void shouldThrowErrorIfAccountTypeIsNull(String name) throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name(name).build();

    // when
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(EMPTY_ACCOUNT_TYPE_NAME))));
  }

  @SuppressWarnings("unused")
  private static Collection<Object[]> emptyAccountTypeNameParameters() {
    return Arrays.asList(new Object[][]{
        {""},
        {" "},
        {"    "},
        {null}
    });
  }

  @Test
    public void shouldReturnErrorCausedByExistingAccountTypeName() throws Exception {
    // given
    AccountTypeRequest accountTypeRequest = AccountTypeRequest.builder().name("AccountInvestment").build();

    // when
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();

    // then
    mockMvc.perform(post(ACCOUNT_TYPE_SERVICE_PATH)
        .header(HttpHeaders.AUTHORIZATION, token)
        .contentType(JSON_CONTENT_TYPE)
        .content(json(accountTypeRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", is(getMessage(ACCOUNT_TYPE_WITH_PROVIDED_NAME_ALREADY_EXISTS))));
  }

}