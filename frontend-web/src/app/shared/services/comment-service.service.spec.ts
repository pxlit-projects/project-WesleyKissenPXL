import { TestBed } from '@angular/core/testing';
import { CommentService } from './comment-service.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth-service.service';
import { HttpClient } from '@angular/common/http';
import { environment } from "../../../environments/environment.development";
import { Comment } from '@models/comment.model';
import { CommentRequest } from '@models/commentRequest.model';
import { UpdateCommentRequest } from '@models/updateCommentRequest.model';
import { of } from 'rxjs';

// Mock AuthService
class MockAuthService {
  getRole() {
    return 'gebruiker'; // mock role to simulate a standard user
  }
}

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  let authService: MockAuthService;
  const apiUrl = environment.commentApiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        CommentService,
        { provide: AuthService, useClass: MockAuthService },
      ],
    });

    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as any; // Typecasting to MockAuthService
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getComments', () => {
    it('should fetch all comments and return them', () => {
      const mockComments: Comment[] = [
        { id: '1', comment: 'Comment 1', author: "rick", postId: 'post1' },
        { id: '2', comment: 'Comment 2', author: "rick", postId: 'post2' },
      ];

      service.getComments().subscribe((comments) => {
        expect(comments.length).toBe(2);
        expect(comments).toEqual(mockComments);
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllComments`);
      expect(req.request.method).toBe('GET');
      req.flush(mockComments);
    });

    it('should handle error when fetching comments fails', () => {
      service.getComments().subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllComments`);
      req.flush('Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('addComment', () => {
    it('should add a comment successfully', () => {
      const postId = 'post1';
      const newComment: CommentRequest = { content: 'New comment', author: 'John'};

      service.addComment(postId, newComment).subscribe((response) => {
        expect(response).toEqual([]);
      });

      const req = httpMock.expectOne(`${apiUrl}/${postId}/addComment`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newComment);
      req.flush([]);
    });

    it('should handle error when adding a comment fails', () => {
      const postId = 'post1';
      const newComment: CommentRequest = { content: 'New comment', author: 'John'};

      service.addComment(postId, newComment).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/${postId}/addComment`);
      req.flush('Error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('updateComment', () => {

    it('should handle error when updating a comment fails', () => {
      const commentId = '1';
      const updatedComment: UpdateCommentRequest = { content: 'Updated comment' };

      service.updateComment(commentId, updatedComment).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/${commentId}/change`);
      req.flush('Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('deleteComment', () => {
    it('should delete a comment successfully', () => {
      const commentId = '1';

      service.deleteComment(commentId).subscribe((response) => {
        expect(response).toBeUndefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/${commentId}/deleteComment`);
      expect(req.request.method).toBe('DELETE');
    });

    it('should handle error when deleting a comment fails', () => {
      const commentId = '1';

      service.deleteComment(commentId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/${commentId}/deleteComment`);
      req.flush('Error', { status: 400, statusText: 'Bad Request' });
    });
  });
});