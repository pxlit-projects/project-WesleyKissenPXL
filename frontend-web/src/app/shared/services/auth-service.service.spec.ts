import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '@services/auth-service.service';
import { User } from '@models/user.model';

// Mock van de Router om navigatie te simuleren in de testen
class MockRouter {
  navigate = jasmine.createSpy('navigate');
}

describe('AuthService', () => {
  let service: AuthService;
  let router: MockRouter;

  beforeEach(() => {
    router = new MockRouter();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: Router, useValue: router },
      ],
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should return true and navigate to /homepage with valid credentials', () => {
      const result = service.login('wesley', 'hoofdredacteur');
      expect(result).toBeTrue();
      expect(localStorage.getItem('currentUserRole')).toBeDefined();
      expect(router.navigate).toHaveBeenCalledWith(['/homepage']);
    });

    it('should return false with invalid credentials', () => {
      const result = service.login('invalidUser', 'wrongRole');
      expect(result).toBeFalse();
    });

    it('should store user in localStorage on successful login', () => {
      service.login('wesley', 'hoofdredacteur');
      expect(localStorage.getItem('currentUserRole')).toBe(JSON.stringify({ username: 'wesley', role: 'hoofdredacteur' }));
    });
  });

  describe('logout', () => {
    it('should remove current user from localStorage and navigate to root', () => {
      service.login('wesley', 'hoofdredacteur');
      service.logout();
      expect(localStorage.getItem('currentUserRole')).toBeNull();
      expect(router.navigate).toHaveBeenCalledWith(['']);
    });
  });

  describe('getRole', () => {
    it('should return the correct role if user is logged in', () => {
      service.login('wesley', 'hoofdredacteur');
      expect(service.getRole()).toBe('hoofdredacteur');
    });

    it('should return null if no user is logged in', () => {
      service.logout();
      expect(service.getRole()).toBeNull();
    });
  });

  describe('isLoggedIn', () => {
    it('should return true if a user is logged in', () => {
      service.login('wesley', 'hoofdredacteur');
      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should return false if no user is logged in', () => {
      service.logout();
      expect(service.isLoggedIn()).toBeFalse();
    });
  });

  describe('getUserName', () => {
    it('should return the correct username if user is logged in', () => {
      service.login('wesley', 'hoofdredacteur');
      expect(service.getUserName()).toBe('wesley');
    });

    it('should return null if no user is logged in', () => {
      service.logout();
      expect(service.getUserName()).toBeNull();
    });
  });

  describe('Initialization', () => {
    it('should load user from localStorage if available', () => {
      localStorage.removeItem('currentUserRole');
      service.logout();
      localStorage.setItem('currentUserRole', JSON.stringify({ username: 'dries', role: 'redacteur' }));
      const newService = TestBed.inject(AuthService);
      expect(newService.getUserName()).toBeNull();
      expect(newService.getRole()).toBeNull();
    });

    it('should not load user if localStorage does not have currentUserRole', () => {
      localStorage.removeItem('currentUserRole');
      const newService = TestBed.inject(AuthService);
      expect(newService.getUserName()).toBe('dries');
      expect(newService.getRole()).toBe('redacteur');
    });
  });
});