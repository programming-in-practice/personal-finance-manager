import {browser, by, element} from 'protractor';
import {NavigationBar} from './NavigationBar.po';

export class TransactionAndFilterPage {

  navigationBar = new NavigationBar();

   ngSelectNth(elementId, optionIndex) {
  		element(by.id(elementId)).click();
  		element(by.id(elementId)).all(by.css('.ng-option')).get(optionIndex).click();
   }

  async navigateTo() {
    return this.navigationBar.transactionLink().click();
  }

  addTransactionButton() {
    return element(by.id('AddTransactionBtn'));
  }

  refreshTransactionsButton() {
    return element(by.id('RefreshTransactionsBtn'));
  }

  dateHeader() {
    return element(by.id('DateHeader'));
  }

  descriptionHeader() {
    return element(by.id('NameHeader'));
  }

  priceHeader() {
    return element(by.id('PriceHeader'));
  }

  accountHeader() {
    return element(by.id('AccountHeader'));
  }

  categoryHeader() {
    return element(by.id('CategoryHeader'));
  }

  newTransactionDateInput() {
    return element(by.id('NewTransactionDateInput'));
  }

  newTransactionDescriptionInput() {
    return element(by.id('NewTransactionDescriptionInput'));
  }

  newTransactionPriceInput() {
    return element.all(by.id('NewTransactionPriceInput'));
  }

  newTransactionAccountSelects() {
    return element.all(by.id('newTransactionAccountSelects'));
  }

  newTransactionCategorySelectOption(optionIndex) {
    return this.ngSelectNth("newTransactionCategorySelect", optionIndex);
  }

  newTransactionSaveButton() {
    return element(by.id('newTransactionSaveButton'));
  }

  editTransactionDateInput() {
    return element(by.id('EditTransactionDateInput'));
  }

  editTransactionDescriptionInput() {
    return element(by.id('EditTransactionDescriptionInput'));
  }

  editTransactionPriceInput() {
    return element.all(by.id('EditTransactionPriceInput'));
  }

  editTransactionAccountSelects() {
    return element.all(by.id('EditTransactionAccountSelects'));
  }

  editTransactionCategorySelectOption(optionIndex) {
      return this.ngSelectNth("EditTransactionCategorySelect", optionIndex);
    }

  editTransactionSaveButton() {
    return element(by.id('EditTransactionSaveBtn'));
  }

  transactionRows() {
    return element.all(by.id('TransactionRow'));
  }

  optionsButton(row) {
    return row.element(by.id('OptionsBtn'));
  }

  deleteButton(row) {
    return row.element(by.id('DeleteBtn'));
  }

  editButton(row) {
    return row.element(by.id('EditBtn'));
  }

  alert() {
    return element.all(by.id('Alert'));
  }

  assertDate(date) {
    expect(element(by.id('DateReadOnly')).getText()).toEqual(date);
  }

  assertDescription(description) {
    expect(element(by.id('DescriptionReadOnly')).getText()).toEqual(description);
  }

  assertCategory(name) {
    expect(element(by.id('CategoryReadOnly')).getText()).toEqual(name);
  }

  assertPrices(priceOne, priceTwo) {
    expect(element.all(by.id('PricesReadOnly')).get(0).getText()).toEqual(priceOne);

    if (priceTwo !== null) {
      expect(element.all(by.id('PricesReadOnly')).get(1).getText()).toEqual(priceTwo);
    }
  }

  assertAccounts(accountOne, accountTwo) {
    expect(element.all(by.id('AccountsReadOnly')).get(0).getText()).toEqual(accountOne);

    if (accountTwo !== null) {
      expect(element.all(by.id('AccountsReadOnly')).get(1).getText()).toEqual(accountTwo);
    }
  }

  addTransaction(date, description, priceOne, priceTwo, accountNameOne, accountNameTwo, categoryIndex) {
    this.navigateTo();
    this.addTransactionButton().click();

    this.newTransactionDateInput().clear();
    this.newTransactionDateInput().sendKeys(date);

    this.newTransactionDescriptionInput().sendKeys(description);

    this.newTransactionPriceInput().get(0).clear();
    this.newTransactionPriceInput().get(0).sendKeys(priceOne);

    if (priceTwo !== null) {
      this.newTransactionPriceInput().get(1).clear();
      this.newTransactionPriceInput().get(1).sendKeys(priceTwo);
    }

    this.newTransactionAccountSelects().get(0).element(by.cssContainingText('option', accountNameOne)).click();

    if (accountNameTwo !== null) {
      this.newTransactionAccountSelects().get(1).element(by.cssContainingText('option', accountNameTwo)).click();
    }

    this.newTransactionCategorySelectOption(categoryIndex);

    this.newTransactionSaveButton().click();
  }

  updateTransaction(row, date, description, priceOne, priceTwo, accountNameOne, accountNameTwo, categoryIndex) {
    this.navigateTo();
    this.optionsButton(row).click();
    this.editButton(row).click();
    this.editTransactionDateInput().sendKeys(date);
    this.editTransactionDescriptionInput().clear();
    this.editTransactionDescriptionInput().sendKeys(description);

    this.editTransactionPriceInput().get(0).clear();
    this.editTransactionPriceInput().get(0).sendKeys(priceOne);

    if (priceTwo !== null) {
      this.editTransactionPriceInput().get(1).clear();
      this.editTransactionPriceInput().get(1).sendKeys(priceTwo);
    }

    this.editTransactionAccountSelects().get(0).element(by.cssContainingText('option', accountNameOne)).click();

    if (accountNameTwo !== null) {
      this.editTransactionAccountSelects().get(1).element(by.cssContainingText('option', accountNameTwo)).click();
    }

    this.editTransactionCategorySelectOption(categoryIndex);

    this.editTransactionSaveButton().click();
  }

  deleteTransaction(row) {
    this.optionsButton(row).click();
    this.deleteButton(row).click();

    browser.switchTo().alert().accept();
  }

  async removeAllTransactions() {
    let numberOfTransactions = await this.transactionRows().count();

    while (numberOfTransactions > 0) {
      this.deleteTransaction(this.transactionRows().first());

      numberOfTransactions = await this.transactionRows().count();
    }

    expect(this.transactionRows().count()).toEqual(0);
  }

  addFilterButton() {
    return element(by.id('AddFilterBtn'));
  }

  updateFilterButton() {
    return element(by.id('UpdateFilterBtn'));
  }

  resetFilterButton() {
    return element(by.id('ResetFilterBtn'));
  }

  deleteFilterButton() {
    return element(by.id('DeleteFilterBtn'));
  }

}
