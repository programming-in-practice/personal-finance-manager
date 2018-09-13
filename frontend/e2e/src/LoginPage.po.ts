import {browser, by, element, protractor} from 'protractor';

export class LoginPage {

  navigateTo() {
    return browser.get('/login');
  }

  loginButton() {
    return element(by.id('LoginButton'));
  }

  typeUser(username: string) {
    return element(by.id('LoginUsernameInput')).sendKeys(username);
  }


  typePassword(password: string) {
    return element(by.id('LoginPasswordInput')).sendKeys(password);
  }

  loggedInUserButton() {
    return element(by.id('NavigationBarLoggedInUser2'));
  }

  async loginAs(username: string, password: string) {
    await this.typeUser(username);
    await this.typePassword(password);

    await this.loginButton().click();

    const until = protractor.ExpectedConditions;
    browser.wait(until.presenceOf(this.loggedInUserButton()), 3000, 'Waiting for login page to load');
  }

}
