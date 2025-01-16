import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '@services/auth-service.service';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    // Create a spy object for AuthService
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);

    TestBed.configureTestingModule({
      imports: [FormsModule, LoginComponent],
      providers: [{ provide: AuthService, useValue: authSpy }],
      schemas: [NO_ERRORS_SCHEMA], // to avoid issues with unrecognized HTML elements
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call onLogin with valid inputs and succeed', () => {
    component.userName = 'testUser';
    component.role = 'admin';
    authService.login.and.returnValue(true); // Simulating a successful login

    component.onLogin();

    expect(component.showerror).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(authService.login).toHaveBeenCalledWith('testUser', 'admin');
  });

  it('should show an error message when userName or role is missing', () => {
    component.userName = '';
    component.role = '';
    component.onLogin();

    expect(component.showerror).toBeTrue();
    expect(component.errorMessage).toBe('Please enter both username and role.');
  });

  it('should show an error message when login fails (invalid username or role)', () => {
    component.userName = 'testUser';
    component.role = 'admin';
    authService.login.and.returnValue(false); // Simulating a failed login

    component.onLogin();

    expect(component.showerror).toBeTrue();
    expect(component.errorMessage).toBe('Invalid username or role.');
    expect(authService.login).toHaveBeenCalledWith('testUser', 'admin');
  });

  it('should handle missing username', () => {
    component.userName = '';
    component.role = 'admin';
    component.onLogin();

    expect(component.showerror).toBeTrue();
    expect(component.errorMessage).toBe('Please enter both username and role.');
  });

  it('should handle missing role', () => {
    component.userName = 'testUser';
    component.role = '';
    component.onLogin();

    expect(component.showerror).toBeTrue();
    expect(component.errorMessage).toBe('Please enter both username and role.');
  });
});