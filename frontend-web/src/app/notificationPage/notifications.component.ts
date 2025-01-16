import { NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Notificatie } from '@models/notificatie.model';
import { PostService } from '@services/post-service.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notification.component.html',
  imports: [ NgClass],
})
export class NotificationsComponent implements OnInit {
  notifications: Notificatie[] = [];

  constructor(private router: Router, private postService: PostService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.postService.getNotifications().subscribe(
      (data) => {
        this.notifications = data;
      },
      (error) => {
        console.error('Error loading notifications:', error);
      }
    );
  }

  handleNotificationClick(notification: Notificatie): void {
    if (notification.status === 'REJECTED') {
        this.router.navigate([`/notifications/${notification.postId}`]);
    }
  }
}