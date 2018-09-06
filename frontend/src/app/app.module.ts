import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './core/app.component';
import {AccountsListComponent} from './account/accounts-list/accounts-list.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CategoriesComponent} from './category/categories/categories.component';
import {NavigationComponent} from './navigation/navigation.component';
import {AlertsComponent} from './alerts/alerts.component';
import {AlertsService} from './alerts/alerts-service/alerts.service';
import {TransactionsComponent} from './transaction/transactions/transactions.component';
import {OrderModule} from 'ngx-order-pipe';
import {routing} from './app-routing.module';
import {AuthenticationService} from './_services/authentication.service';
import {UserService} from './_services/user.service';
import {AuthGuard} from './_guards/auth.guard';
import {JwtInterceptor} from './_helpers/jwt.interceptor';
import {ErrorInterceptor} from './_helpers/error.interceptor';
import {fakeBackendProvider} from './_helpers/fake-backend';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';

@NgModule({
  declarations: [
    AppComponent,
    AccountsListComponent,
    CategoriesComponent,
    TransactionsComponent,
    NavigationComponent,
    AlertsComponent,
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    routing,
    OrderModule
  ],
  providers: [
    AuthGuard,
    AlertsService,
    AuthenticationService,
    UserService,
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},

    // provider used to create fake backend
    fakeBackendProvider
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule {
}
