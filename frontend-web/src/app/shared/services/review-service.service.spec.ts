import { TestBed } from '@angular/core/testing';
import { ReviewService } from './review-service.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth-service.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment.development';
import { ReviewablePost } from '@models/review.models';
import { of } from 'rxjs';

// Mock AuthService
class MockAuthService {
  getRole() {
    return 'gebruiker'; // mock role to simulate a standard user
  }
}

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let authService: MockAuthService;
  const apiUrl = environment.reviewApiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ReviewService,
        { provide: AuthService, useClass: MockAuthService },
      ],
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as any; // Typecasting to MockAuthService
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getPostsWaitingForApproval', () => {
    it('should fetch all posts waiting for approval and return them', () => {
      const mockPosts: ReviewablePost[] = [
        new ReviewablePost(
          '1',
          'Post 1',
          'Content 1',
          'Author 1',
          new Date('2025-01-16T10:00:00'),
          'WAITING_FOR_APPROVAL'
        ),
        new ReviewablePost(
          '2',
          'Post 2',
          'Content 2',
          'Author 2',
          new Date('2025-01-16T10:00:00'),
          'WAITING_FOR_APPROVAL'
        ),
      ];

      service.getPostsWaitingForApproval().subscribe((posts) => {
        expect(posts.length).toBe(2);
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllReviewablePosts`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle error when fetching posts fails', () => {
      service.getPostsWaitingForApproval().subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllReviewablePosts`);
      req.flush('Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('publishPost', () => {
    it('should publish a post successfully', () => {
      const reviewId = '1';

      service.publishPost(reviewId).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/${reviewId}/publishReviewPost`);
      expect(req.request.method).toBe('PUT');
      req.flush({});
    });

    it('should handle error when publishing a post fails', () => {
      const reviewId = '1';

      service.publishPost(reviewId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/${reviewId}/publishReviewPost`);
      req.flush('Error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('rejectPost', () => {
    it('should reject a post successfully with rejection reason', () => {
      const reviewId = '1';
      const reviewMessage = { message: 'Rejecting this post.' };

      service.rejectPost(reviewId, reviewMessage).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/${reviewId}/reject`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(reviewMessage);
      req.flush({});
    });

    it('should reject a post successfully without rejection reason', () => {
      const reviewId = '2';
      const reviewMessage = {}; // Empty message object

      service.rejectPost(reviewId, reviewMessage).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/${reviewId}/reject`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(reviewMessage);
      req.flush({});
    });

    it('should handle error when rejecting a post fails', () => {
      const reviewId = '1';
      const reviewMessage = { message: 'Rejecting this post.' };

      service.rejectPost(reviewId, reviewMessage).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/${reviewId}/reject`);
      req.flush('Error', { status: 400, statusText: 'Bad Request' });
    });
  });
});