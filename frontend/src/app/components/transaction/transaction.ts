import {Category} from '../category/category';
import {Account} from '../account/account';
import {RecurrencePeriod} from "./recurrence-period";

export class Transaction {
  id: number;
  date: Date;
  description: string;
  category: Category;
  accountPriceEntries: AccountPriceEntry[] = [];
  editMode = false;
  isPlanned = false;
  recurrencePeriod = RecurrencePeriod.NONE;
  // recurrencePeriod: RecurrencePeriod;
  // isRecurrent = this.recurrencePeriod === undefined ? false : this.recurrencePeriod !== RecurrencePeriod.NONE;
  // isRecurrent = false;
  editedTransaction: Transaction;

}

export class AccountPriceEntry {
  account: Account;
  postTransactionAccountBalance = 0;
  price: number;
  pricePLN: number;
}
