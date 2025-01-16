import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    const userInfo = JSON.parse(localStorage.getItem('currentUserRole') || '[]');

    if (requiredRoles.includes(userInfo.role)){
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}