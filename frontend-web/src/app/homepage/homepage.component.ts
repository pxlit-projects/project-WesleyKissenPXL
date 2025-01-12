import { Component, OnInit } from '@angular/core';
import { AuthService } from '@services/auth-service.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
})
export class HomepageComponent {
  user: { userName: string; role: string } | null = null;
  isAdmin: boolean = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.isAdmin = this.authService.hasRole('admin');
  }

  logout(): void {
    this.authService.logout();
  }
}
