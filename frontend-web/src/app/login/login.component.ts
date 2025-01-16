import { AuthService } from '@services/auth-service.service';
import {FormsModule} from '@angular/forms';
import {Component} from '@angular/core';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
 imports: [
  FormsModule
 ]
})



export class LoginComponent {
  userName: string = '';
  role: string = '';
  errorMessage: string = '';
  showerror: boolean = false;

  constructor(private authService: AuthService) {}

  onLogin(): void {
    if (this.userName && this.role) {
      if(!this.authService.login(this.userName, this.role)){
        this.showerror = true;
        this.errorMessage = 'Invalid username or role.';
        return;
      }
      this.errorMessage = '';
    } else {
      this.showerror = true;
      this.errorMessage = 'Please enter both username and role.';
    }
  }
}