import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotificationsComponent } from './notifications.component';
import { PostService } from '@services/post-service.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { Notificatie } from '@models/notificatie.model';

describe('NotificationsComponent', () => {
  let component: NotificationsComponent;
  let fixture: ComponentFixture<NotificationsComponent>;
  let mockPostService: jasmine.SpyObj<PostService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(() => {
    mockPostService = jasmine.createSpyObj('PostService', ['getNotifications']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [NotificationsComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: Router, useValue: mockRouter },
      ],
    });

    fixture = TestBed.createComponent(NotificationsComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load notifications on ngOnInit', () => {
    const mockNotifications: Notificatie[] = [
      new Notificatie('1', 'Post Title 1', '101', 'REJECTED', 'Reason 1'),
      new Notificatie('2', 'Post Title 2', '102', 'ACCEPTED', ''),
    ];
    
    mockPostService.getNotifications.and.returnValue(of(mockNotifications));

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.notifications).toEqual(mockNotifications);
  });

  it('should handle notification click and navigate if status is REJECTED', () => {
    const notification: Notificatie = new Notificatie('1', 'Post Title 1', '101', 'REJECTED', 'Reason 1');
    
    component.handleNotificationClick(notification);

    expect(mockRouter.navigate).toHaveBeenCalledWith([`/notifications/${notification.postId}`]);
  });

  it('should not navigate if notification status is not REJECTED', () => {
    const notification: Notificatie = new Notificatie('2', 'Post Title 2', '102', 'ACCEPTED', '');
    
    component.handleNotificationClick(notification);

    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });
});