import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomepageComponent } from './homepage/homepage.component';
import {RoleGuard} from './role.guard';
import { PostsComponent } from './core/posts/posts.component';
import { ConceptComponent } from './conceptsPage/concepts.component';
import { EditConceptComponent } from './conceptsPage/editconcept/edit-concept.component';
import { ApprovalComponent } from './approvalPage/approvals.component';
import { NotificationsComponent } from './notificationPage/notifications.component';
import { EditNotificationComponent } from './notificationPage/edit-notification/edit-notification.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'homepage', component: HomepageComponent , canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur', 'gebruiker'] }},
  { path: 'posts', component: PostsComponent , canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur'] }},
  { path: 'concepts', component: ConceptComponent , canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur'] }},
  { path: 'concepts/:id', component: EditConceptComponent, canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur']}},
  { path: 'waitingapproval', component: ApprovalComponent, canActivate: [RoleGuard], data: { roles: ['hoofdredacteur']}},
  { path: 'notifications', component: NotificationsComponent, canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur']}},
  { path: 'notifications/:id', component: EditNotificationComponent, canActivate: [RoleGuard], data: { roles: ['hoofdredacteur', 'redacteur']}},
];
