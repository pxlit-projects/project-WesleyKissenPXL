import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostService } from '@services/post-service.service';
import { PostsComponent } from './posts.component';
import { CreatePostComponent } from './create-post/create-post.component';
import { of, throwError } from 'rxjs';
import { PostRequest } from '@models/postRequest.model';

describe('PostsComponent', () => {
  let component: PostsComponent;
  let fixture: ComponentFixture<PostsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['createpost', 'createConceptPost']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);

    await TestBed.configureTestingModule({
      imports: [CreatePostComponent, PostsComponent],  // Voeg PostsComponent hier toe aan imports
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PostsComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    snackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call postService.createpost and navigate on success in processAdd', fakeAsync(() => {
    const post: PostRequest = { title: 'Test Post', content: 'Test Content', author: 'Test Author' };
    postService.createpost.and.returnValue(of(null));  // Simulating a successful response

    component.processAdd(post);
    tick();

    expect(postService.createpost).toHaveBeenCalledWith(post);
    expect(snackBar.open).toHaveBeenCalledWith('Post Made succesfully!', 'Close', { duration: 3000 });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/homepage');
  }));

  it('should show an error snackbar when processAdd fails', fakeAsync(() => {
    const post: PostRequest = { title: 'Test Post', content: 'Test Content', author: 'Test Author' };
    postService.createpost.and.returnValue(throwError('error'));  // Simulating an error response

    component.processAdd(post);
    tick();

    expect(postService.createpost).toHaveBeenCalledWith(post);
    expect(snackBar.open).toHaveBeenCalledWith('Something went wrong, try again', 'Close', { duration: 3000 });
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  }));

  it('should call postService.createConceptPost and navigate on success in processAddConcept', fakeAsync(() => {
    const concept: PostRequest = { title: 'Test Concept', content: 'Test Content', author: 'Test Author' };
    postService.createConceptPost.and.returnValue(of(null));  // Simulating a successful response

    component.processAddConcept(concept);
    tick();

    expect(postService.createConceptPost).toHaveBeenCalledWith(concept);
    expect(snackBar.open).toHaveBeenCalledWith('Concept succesfully saved!', 'Close', { duration: 3000 });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/homepage');
  }));

  it('should show an error snackbar when processAddConcept fails', fakeAsync(() => {
    const concept: PostRequest = { title: 'Test Concept', content: 'Test Content', author: 'Test Author' };
    postService.createConceptPost.and.returnValue(throwError('error'));  // Simulating an error response

    component.processAddConcept(concept);
    tick();

    expect(postService.createConceptPost).toHaveBeenCalledWith(concept);
    expect(snackBar.open).toHaveBeenCalledWith('Something went wrong when trying to save the concept', 'Close', { duration: 3000 });
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  }));
});