import {Component, OnInit} from '@angular/core';
import {Account} from '../account';
import {AccountService} from '../account-service/account.service';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';
import {TranslateService} from '@ngx-translate/core';
import {CurrencyService} from '../currency-service/currency.service';
import {Currency} from '../currency';

const maxAccountBalance = Number.MAX_SAFE_INTEGER;
const minAccountBalance = Number.MIN_SAFE_INTEGER;

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})

export class AccountsComponent extends Sortable implements OnInit {
  supportedCurrencies: Currency[];
  accounts: Account[] = [];
  addingMode = false;
  newAccount: Account = new Account();

  constructor(
    private accountService: AccountService,
    private currencyService: CurrencyService,
    private alertService: AlertsService,
    private translate: TranslateService) {
    super('name');
  }

  ngOnInit() {
    this.getAccounts();
    this.getCurrencies(); // TODO - call in parallel
  }

  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => {
        this.accounts = accounts;
      });
  }

  getCurrencies(): void {
    this.currencyService.getCurrencies()
      .subscribe(currencies => {
        this.supportedCurrencies = currencies;
        this.newAccount.currency = this.supportedCurrencies[0];
      });
  }

  deleteAccount(account) {
    if (confirm(this.translate.instant('message.wantDeleteAccount'))) {
      this.accountService.deleteAccount(account.id)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.accountDeleted'));
          const index: number = this.accounts.indexOf(account);
          if (index !== -1) {
            this.accounts.splice(index, 1);
          }
        });
    }
  }

  onShowEditMode(account: Account) {
    account.editMode = true;
    account.editedAccount = new Account();
    account.editedAccount.name = account.name;
    account.editedAccount.balance = account.balance;

    // need to set exactly same object
    for (const currency of this.supportedCurrencies) {
      if (currency.name === account.currency.name) {
        account.editedAccount.currency = currency;
        break;
      }
    }
  }

  onEditAccount(account: Account) {
    if (!this.validateAccount(account.editedAccount)) {
      return;
    }
    const editedAccount: Account = new Account();
    editedAccount.id = account.id;
    editedAccount.name = account.editedAccount.name;
    editedAccount.balance = account.editedAccount.balance;
    editedAccount.currency = account.editedAccount.currency;

    this.accountService.editAccount(editedAccount)
      .subscribe(() => {
        this.alertService.success(this.translate.instant('message.accountEdited'));
        Object.assign(account, editedAccount);
        // TODO - get object from server
      });
  }

  onAddAccount() {
    if (!this.validateAddingAccount(this.newAccount)) {
      return;
    }

    this.accountService.addAccount(this.newAccount)
      .subscribe(id => {
        this.alertService.success(this.translate.instant('message.accountAdded'));
        this.newAccount.id = id;

        // TODO - get object from server
        this.accounts.push(this.newAccount);
        this.addingMode = false;
        this.newAccount = new Account();
        this.newAccount.currency = this.supportedCurrencies[0];
      });
  }

  onRefreshAccounts() {
    this.getAccounts();
  }

  validateAccount(account: Account): boolean {
    if ((account.name == null || account.name.trim() === '')
      && (!account.balance)) { // TODO change validation to validate all at once, not break on error
      this.alertService.error(this.translate.instant('message.accountNameEmpty'));
      this.alertService.error(this.translate.instant('message.accountBalanceEmpty'));
      return false;
    }
    if (account.name == null || account.name === '') {
      this.alertService.error(this.translate.instant('message.accountNameEmpty'));
      return false;
    }
    if (account.name.length > 100) {
      this.alertService.error(this.translate.instant('message.accountNameTooLong'));
      return false;
    }

    if (account.balance == null) {
      this.alertService.error(this.translate.instant('message.accountBalanceEmpty'));
      return false;
    }

    if (!isNumeric(account.balance)) {
      this.alertService.error(this.translate.instant('message.balanceNotCorrect'));
      return false;
    }

    if (account.balance > maxAccountBalance) {
      this.alertService.error(this.translate.instant('message.balanceTooBig'));
      return false;
    }

    if (account.balance < minAccountBalance) {
      this.alertService.error(this.translate.instant('message.balanceTooLow'));
      return false;
    }

    return true;
  }

  validateAddingAccount(accountToValidate: Account): boolean {
    if (!this.validateAccount(accountToValidate)) {
      return false;
    }

    if (this.accounts.filter(account => account.name.toLocaleLowerCase() === accountToValidate.name.toLocaleLowerCase()).length > 0) {
      this.alertService.error(this.translate.instant('message.accountNameExists'));
      return false;
    }
    return true;
  }

  allAccountsBalance() {
    let sum = 0;

    for (let i = 0; i < this.accounts.length; ++i) {
      sum += +this.accounts[i].balance * +this.accounts[i].currency.exchangeRate;
    }

    return sum;
  }

}
