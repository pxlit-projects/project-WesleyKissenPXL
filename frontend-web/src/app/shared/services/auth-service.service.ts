import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '@models/user.model'; // Zorg ervoor dat dit pad correct is

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUser: User | null = null;
  private readonly STORAGE_KEY = 'currentUserRole';

  // Hardcoded lijst van gebruikers
  private readonly users: User[] = [
    new User('wesley', 'hoofdredacteur'),
    new User('dries', 'redacteur'),
    new User('tom', 'gebruiker'),
  ];

  constructor(private router: Router) {
    // Probeer de gebruiker te laden bij het initialiseren
    const savedUser = localStorage.getItem(this.STORAGE_KEY);
    if (savedUser) {
      this.currentUser = JSON.parse(savedUser) as User;
    }
  }

  login(userName: string, role: string): boolean {
    // Controleer of de gebruiker bestaat in de hardcoded lijst
    const user = this.users.find(
      (u) => u.username === userName && u.role === role
    );

    if (user) {
      this.currentUser = user;
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.currentUser)); // Sla op in localStorage
      this.router.navigate(['/homepage']);
      return true; 
    } else {
      return false;
    }
  }

  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.STORAGE_KEY); // Verwijder uit localStorage
    this.router.navigate(['']);
  }

  getRole(): string | null {
    return this.currentUser?.role || null;
  }

  isLoggedIn(): boolean {
    return this.currentUser !== null;
  }

  getUserName(): string | null {
    return this.currentUser?.username || null;
  }
}
