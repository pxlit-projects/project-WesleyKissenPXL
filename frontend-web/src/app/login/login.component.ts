import { Component, inject } from '@angular/core';
import { AuthService } from '@services/auth-service.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  // Dependency injection for services and FormBuilder
  authService: AuthService = inject(AuthService);
  fb: FormBuilder = inject(FormBuilder);

  // Reactive form for login
  loginForm = this.fb.group({
    username: ['', Validators.required],
    role: ['redacteur', Validators.required]
  });

  login(): void {
    if (this.loginForm.valid) {
      const username = this.loginForm.get('username')?.value;
      const role = this.loginForm.get('role')?.value;
      this.authService.login(username!, role!);
    }
  }

  isFormInvalid() {
    return this.loginForm.invalid;
  }
}