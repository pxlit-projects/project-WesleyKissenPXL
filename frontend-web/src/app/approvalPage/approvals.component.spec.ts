import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ApprovalComponent } from './approvals.component';
import { ReviewService } from '@services/review-service.service';
import { FormsModule } from '@angular/forms';

describe('ApprovalComponent', () => {
  let component: ApprovalComponent;
  let fixture: ComponentFixture<ApprovalComponent>;
  let reviewService: jasmine.SpyObj<ReviewService>;
  let router: jasmine.SpyObj<Router>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;

  const mockPosts = [
    {
      id: '1',
      title: 'Post 1',
      content: 'This is the content of post 1',
      author: 'Author 1',
      timeOfCreation: new Date(),
      status: 'WAITING_FOR_APPROVAL',
    },
    {
      id: '2',
      title: 'Post 2',
      content: 'This is the content of post 2',
      author: 'Author 2',
      timeOfCreation: new Date(),
      status: 'WAITING_FOR_APPROVAL',
    },
  ];

  beforeEach(async () => {
    const reviewServiceSpy = jasmine.createSpyObj('ReviewService', [
      'getPostsWaitingForApproval',
      'publishPost',
      'rejectPost',
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, ApprovalComponent],
      providers: [
        { provide: ReviewService, useValue: reviewServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
      ],
    }).compileComponents();

    

    fixture = TestBed.createComponent(ApprovalComponent);
    component = fixture.componentInstance;
    reviewService = TestBed.inject(ReviewService) as jasmine.SpyObj<ReviewService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    snackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should call loadConcepts', () => {
      spyOn(component, 'loadConcepts');
      component.ngOnInit();
      expect(component.loadConcepts).toHaveBeenCalled();
    });
  });

  describe('initializeReviewData', () => {
    it('should initialize review data for each concept', () => {
      component.waitingPosts = [
        {
            id: '1',
            title: 'Post 1',
            content: 'This is the content of post 1',
            author: 'Author 1',
            timeOfCreation: new Date(),
            status: 'WAITING_FOR_APPROVAL',
          },
          {
            id: '2',
            title: 'Post 2',
            content: 'This is the content of post 2',
            author: 'Author 2',
            timeOfCreation: new Date(),
            status: 'WAITING_FOR_APPROVAL',
          },
      ];

      component.initializeReviewData();

      expect(component.reviewData).toEqual({
        '1': { accepted: true, rejectionMessage: '' },
        '2': { accepted: true, rejectionMessage: '' },
      });
    });
  });

  describe('handleReviewChange', () => {
    it('should update review data when a post is accepted', () => {
      component.reviewData['1'] = { accepted: false, rejectionMessage: 'Some reason' };

      component.handleReviewChange('1', true);

      expect(component.reviewData['1']).toEqual({ accepted: true, rejectionMessage: '' });
    });

    it('should update review data when a post is rejected', () => {
      component.reviewData['1'] = { accepted: true, rejectionMessage: '' };

      component.handleReviewChange('1', false);

      expect(component.reviewData['1']).toEqual({ accepted: false, rejectionMessage: '' });
    });
  });

  describe('PublishPost', () => {
    it('should publish the post and navigate to homepage on success', () => {
      reviewService.publishPost.and.returnValue(of(null));

      component.PublishPost('1');

      expect(reviewService.publishPost).toHaveBeenCalledWith('1');
      expect(snackBar.open).toHaveBeenCalledWith('Post succesfully published!', 'Close', {
        duration: 3000,
      });
      expect(router.navigateByUrl).toHaveBeenCalledWith('/homepage');
    });

    it('should handle error when publishing the post', () => {
      reviewService.publishPost.and.returnValue(throwError('Error publishing'));

      component.PublishPost('1');

      expect(reviewService.publishPost).toHaveBeenCalledWith('1');
      expect(snackBar.open).toHaveBeenCalledWith(
        'Something went wrong when trying to publish the post',
        'Close',
        { duration: 3000 }
      );
    });
  });

  describe('RejectPost', () => {
    it('should reject the post and navigate to homepage on success', () => {
      reviewService.rejectPost.and.returnValue(of(null));
      component.reviewData['1'] = { accepted: false, rejectionMessage: 'Reason for rejection' };

      component.RejectPost('1');

      expect(reviewService.rejectPost).toHaveBeenCalledWith('1', { message: 'Reason for rejection' });
      expect(snackBar.open).toHaveBeenCalledWith('Post succesfully rejected!', 'Close', {
        duration: 3000,
      });
      expect(router.navigateByUrl).toHaveBeenCalledWith('/homepage');
    });

    it('should handle error when rejecting the post', () => {
      reviewService.rejectPost.and.returnValue(throwError('Error rejecting'));
      component.reviewData['1'] = { accepted: false, rejectionMessage: 'Reason for rejection' };

      component.RejectPost('1');

      expect(reviewService.rejectPost).toHaveBeenCalledWith('1', { message: 'Reason for rejection' });
      expect(snackBar.open).toHaveBeenCalledWith(
        'Something went wrong when trying to reject the post',
        'Close',
        { duration: 3000 }
      );
    });
  });
});