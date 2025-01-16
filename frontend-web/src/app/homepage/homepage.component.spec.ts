import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HomepageComponent } from './homepage.component';
import { AuthService } from '@services/auth-service.service';
import { PostService } from '@services/post-service.service';
import { CommentService } from '@services/comment-service.service';
import { Post } from '@models/post.model';
import { Comment } from '@models/comment.model';
import { CommentRequest } from '@models/commentRequest.model';
import { UpdateCommentRequest } from '@models/updateCommentRequest.model';

describe('HomepageComponent', () => {
  let component: HomepageComponent;
  let fixture: ComponentFixture<HomepageComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let postService: jasmine.SpyObj<PostService>;
  let commentService: jasmine.SpyObj<CommentService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole', 'getUserName', 'logout']);
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getPosts', 'filterPosts']);
    const commentServiceSpy = jasmine.createSpyObj('CommentService', ['getComments', 'addComment', 'updateComment', 'deleteComment']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [HomepageComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: PostService, useValue: postServiceSpy },
        { provide: CommentService, useValue: commentServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomepageComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    commentService = TestBed.inject(CommentService) as jasmine.SpyObj<CommentService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load posts and comments, set role and username', () => {
      const mockPosts: Post[] = [{ id: '1', title: 'Test Post', content: 'Test Content', author: 'Test Author', timeOfCreation: new Date(), status: 'POSTED'}];
      const mockComments: Comment[] = [{ id: '1', postId: '1', comment: 'Test Comment', author: 'Test Author' }];
      authService.getRole.and.returnValue('hoofdredacteur');
      authService.getUserName.and.returnValue('testuser');
      postService.getPosts.and.returnValue(of(mockPosts));
      commentService.getComments.and.returnValue(of(mockComments));

      component.ngOnInit();

      expect(component.comments).toEqual(mockComments);
      expect(component.role).toBe('hoofdredacteur');
      expect(component.username).toBe('testuser');
      expect(component.showerror).toBeTrue();
    });
  });

//   describe('onSearch', () => {

//     it('should handle error when fetching posts', () => {
//       postService.filterPosts.and.returnValue(throwError('Error fetching posts'));
//       spyOn(console, 'error');

//       component.onSearch();

//       expect(console.error).toHaveBeenCalledWith('Error fetching posts:', 'Error fetching posts');
//     });
//   });

  describe('getCommentsByPostId', () => {
    it('should return comments for a specific postId', () => {
      const postId = '1';
      const commentsForPost = [{ id: '1', postId: '1', comment: 'Test Comment', author: 'Test Author' }];
      component.comments = commentsForPost;

      const result = component.getCommentsByPostId(postId);

      expect(result).toEqual(commentsForPost);
    });
  });


  describe('onEditComment', () => {
    it('should edit a comment successfully', () => {
      const comment = { id: '1', postId: '1', comment: 'Test Comment', author: 'Test Author' };
      const updatedCommentContent = 'Updated Comment';
      spyOn(window, 'prompt').and.returnValue(updatedCommentContent);
      const updatedComment = new UpdateCommentRequest(updatedCommentContent);

      commentService.updateComment.and.returnValue(of(comment));

      component.onEditComment(comment);

      expect(commentService.updateComment).toHaveBeenCalledWith(comment.id, updatedComment);
    });

    it('should handle error when updating a comment', () => {
      const comment = { id: '1', postId: '1', comment: 'Test Comment', author: 'Test Author' };
      const updatedCommentContent = 'Updated Comment';
      spyOn(window, 'prompt').and.returnValue(updatedCommentContent);
      const updatedComment = new UpdateCommentRequest(updatedCommentContent);

      commentService.updateComment.and.returnValue(throwError('Error updating comment'));
      spyOn(console, 'error');

      component.onEditComment(comment);

      expect(console.error).toHaveBeenCalledWith('Error updating comment:', 'Error updating comment');
    });
  });

  describe('onDeleteComment', () => {

    it('should not delete a comment if user cancels', () => {
      const commentId = '1';
      spyOn(window, 'confirm').and.returnValue(false);

      component.onDeleteComment(commentId);

      expect(commentService.deleteComment).not.toHaveBeenCalled();
    });
  });

  describe('onLogout', () => {
    it('should log out and navigate to the home page', () => {
      authService.logout.and.stub();

      component.onLogout();

      expect(authService.logout).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['']);
    });
  });

  describe('navigation methods', () => {
    it('should navigate to create post page', () => {
      component.CreatePostRedirect();

      expect(router.navigate).toHaveBeenCalledWith(['/posts']);
    });

    it('should navigate to concepts page', () => {
      component.GoToPostConcepts();

      expect(router.navigate).toHaveBeenCalledWith(['/concepts']);
    });

    it('should navigate to waiting approval page', () => {
      component.GoToPostWaitingApprovals();

      expect(router.navigate).toHaveBeenCalledWith(['/waitingapproval']);
    });

    it('should navigate to notifications page', () => {
      component.GoToNotifications();

      expect(router.navigate).toHaveBeenCalledWith(['/notifications']);
    });
  });
});