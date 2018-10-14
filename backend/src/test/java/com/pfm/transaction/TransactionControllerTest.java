package com.pfm.transaction;

import static com.pfm.test.helpers.TestTransactionProvider.carTransactionRequestWithNoAccountAndNoCategory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.account.AccountService;
import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {

  @Mock
  private TransactionValidator transactionValidator;

  @Mock
  private CategoryService categoryService;

  @Mock
  private AccountService accountService;

  @InjectMocks
  private TransactionController transactionController;

  @Test
  public void shouldReturnExceptionCausedByNotExistingCategoryIdAndNotExistingAccountId() {
    //given
    long notExistingId = 52342L;

    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.getAccountPriceEntries().get(0).setAccountId(notExistingId);
    transactionRequestToAdd.setCategoryId(notExistingId);

    when(transactionValidator.validate(transactionRequestToAdd)).thenReturn(new ArrayList<>());
    when(categoryService.getCategoryById(notExistingId)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd);
    });
    assertThat(exception.getMessage(), is(equalTo("Provided category id: " + notExistingId + " does not exist in the database")));
  }

  @Test
  public void shouldReturnExceptionCausedByNotExistingCategoryId() {
    //given
    long notExistingId = 24235232L;

    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.setCategoryId(notExistingId);

    when(transactionValidator.validate(transactionRequestToAdd)).thenReturn(new ArrayList<>());
    when(categoryService.getCategoryById(notExistingId)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd);
    });
    assertThat(exception.getMessage(), is(equalTo("Provided category id: " + notExistingId + " does not exist in the database")));
  }

  @Test
  public void shouldReturnExceptionCausedByNotExistingExistingAccountId() {
    //given
    long notExistingId = 987654L;
    long existingId = 1L;

    TransactionRequest transactionRequestToAdd = carTransactionRequestWithNoAccountAndNoCategory();
    transactionRequestToAdd.getAccountPriceEntries().get(0).setAccountId(notExistingId);
    transactionRequestToAdd.setCategoryId(existingId);

    when(transactionValidator.validate(transactionRequestToAdd)).thenReturn(new ArrayList<>());
    when(categoryService.getCategoryById(existingId)).thenReturn(Optional.of(new Category()));
    when(accountService.getAccountById(notExistingId)).thenReturn(Optional.empty());

    //when
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      transactionController.addTransaction(transactionRequestToAdd);
    });
    assertThat(exception.getMessage(), is(equalTo("Provided account id: " + notExistingId + " does not exist in the database")));
  }
}
