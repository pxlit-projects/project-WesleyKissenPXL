import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

interface User {
  userName: string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUser: User | null = null;
  private readonly STORAGE_KEY = 'currentUser';

  constructor(private router: Router) {
    this.initializeUser();
  }

  // Initialize user from localStorage
  private initializeUser(): void {
    const storedUser = localStorage.getItem(this.STORAGE_KEY);
    this.currentUser = storedUser ? JSON.parse(storedUser) : null;
  }

  // Persist the current user to localStorage
  private persistUser(): void {
    if (this.currentUser) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.currentUser));
    } else {
      localStorage.removeItem(this.STORAGE_KEY);
    }
  }

  // Login user and navigate to home
  login(userName: string, role: string): void {
    this.currentUser = { userName, role };
    this.persistUser();
    this.router.navigate(['/homepage']);
  }

  // Logout user and navigate to login page
  logout(): void {
    this.currentUser = null;
    this.persistUser();
    this.router.navigate(['/login']);
  }

  // Get current user
  getUser(): User | null {
    return this.currentUser;
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return Boolean(this.currentUser);
  }

  // Check if current user has a specific role
  hasRole(role: string): boolean {
    return this.currentUser?.role === role;
  }
}

