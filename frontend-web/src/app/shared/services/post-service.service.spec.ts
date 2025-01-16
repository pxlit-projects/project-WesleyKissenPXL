import { TestBed } from '@angular/core/testing';
import { PostService } from './post-service.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth-service.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment.development';
import { Post } from '@models/post.model';
import { PostRequest } from '@models/postRequest.model';
import { UpdatePostRequest } from '@models/updatePostRequest.model';
import { Notificatie } from '@models/notificatie.model';
import { of } from 'rxjs';

// Mock AuthService
class MockAuthService {
  getRole() {
    return 'gebruiker'; // mock role for standard user
  }
}

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let authService: MockAuthService;
  const apiUrl = environment.postApiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PostService,
        { provide: AuthService, useClass: MockAuthService },
      ],
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as any; // Typecasting to MockAuthService
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getPosts', () => {
    it('should fetch all posted posts and return them', () => {
      const mockPosts: Post[] = [
        new Post('1', 'Post 1', 'Content 1', 'Author 1', new Date(), 'POSTED'),
        new Post('2', 'Post 2', 'Content 2', 'Author 2', new Date(), 'POSTED'),
      ];

      service.getPosts().subscribe((posts) => {
        expect(posts.length).toBe(2);
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllPosted`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle error when fetching posts fails', () => {
      service.getPosts().subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllPosted`);
      req.flush('Error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getConceptPosts', () => {
    it('should fetch all concept posts and return them', () => {
      const mockPosts: Post[] = [
        new Post('1', 'Concept Post 1', 'Content 1', 'Author 1', new Date(), 'CONCEPT'),
        new Post('2', 'Concept Post 2', 'Content 2', 'Author 2', new Date(), 'CONCEPT'),
      ];

      service.getConceptPosts().subscribe((posts) => {
        expect(posts.length).toBe(2);
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${apiUrl}/getAllConcepts`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });
  });

  describe('createpost', () => {
    it('should create a new post', () => {
      const newPostRequest: PostRequest = {
        title: 'New Post',
        content: 'New post content',
        author: 'Author 1',
      };

      service.createpost(newPostRequest).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/add`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newPostRequest);
      req.flush({});
    });
  });

  describe('createConceptPost', () => {
    it('should create a new concept post', () => {
      const newPostRequest: PostRequest = {
        title: 'Concept Post',
        content: 'Concept post content',
        author: 'Author 2',
      };

      service.createConceptPost(newPostRequest).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/addAsConcept`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newPostRequest);
      req.flush({});
    });
  });

  describe('getConceptById', () => {
    it('should fetch a concept post by ID', () => {
      const conceptId = '1';
      const mockPost = new Post(conceptId, 'Concept Post', 'Content', 'Author', new Date(), 'CONCEPT');

      service.getConceptById(conceptId).subscribe((post) => {
        expect(post).toEqual(mockPost);
      });

      const req = httpMock.expectOne(`${apiUrl}/getConceptPost/${conceptId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPost);
    });
  });

  describe('getNotifications', () => {
    it('should fetch notifications and return them', () => {
      const mockNotifications: Notificatie[] = [
        { id: '1', rejectionReason: 'Notification 1', postId: '2' , status: 'REJECTED', postTitle: 'Notification failed'},
        { id: '2', rejectionReason: 'Notification 2', postId: '5' , status: 'REJECTED', postTitle: 'Notification failed'},
      ];

      service.getNotifications().subscribe((notifications) => {
        expect(notifications.length).toBe(2);
        expect(notifications).toEqual(mockNotifications);
      });

      const req = httpMock.expectOne(`${apiUrl}/notifications`);
      expect(req.request.method).toBe('GET');
      req.flush(mockNotifications);
    });
  });

  describe('updateConcept', () => {
    it('should update a concept post', () => {
      const conceptId = '1';
      const updateRequest: UpdatePostRequest = { title: 'Updated Concept Post', content: 'Updated content' };

      service.updateConcept(conceptId, updateRequest).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/change/${conceptId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush({});
    });
  });

  describe('publishConcept', () => {
    it('should publish a concept post', () => {
      const conceptId = '1';
      const updateRequest: UpdatePostRequest = { title: 'Published Concept Post', content: 'Published content' };

      service.publishConcept(conceptId, updateRequest).subscribe((response) => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/conceptPosted/${conceptId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush({});
    });
  });
})