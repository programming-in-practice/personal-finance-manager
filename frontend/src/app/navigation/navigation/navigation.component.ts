import {Component, OnInit} from '@angular/core';
import {User} from '../../_models';
import {AuthenticationService, UserService} from '../../_services';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  currentUser: User = new User();
  users: User[] = [];

  constructor(private userService: UserService, private authenticationService: AuthenticationService) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    authenticationService.userChanged.subscribe(user => {
      this.currentUser = user;
    });
  }

  ngOnInit() {
    this.loadAllUsers();
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
  }

  login() {
    // this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
  }

  logout() {
    this.authenticationService.logout();
    // this.currentUser = new User();
  }

  deleteUser(id: number) {
    this.userService.delete(id).pipe(first()).subscribe(() => {
      this.loadAllUsers();
    });
  }

  isUserLoggedIn() {
    return this.currentUser != null && this.currentUser.id != null;
  }

  private loadAllUsers() {
    this.userService.getAll().pipe(first()).subscribe(users => {
      this.users = users;
    });
  }

}
