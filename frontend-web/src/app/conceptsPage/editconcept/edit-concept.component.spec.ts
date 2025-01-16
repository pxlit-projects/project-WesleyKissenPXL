import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { EditConceptComponent } from './edit-concept.component';
import { PostService } from '@services/post-service.service';
import { FormsModule } from '@angular/forms';
import { UpdatePostRequest } from '@models/updatePostRequest.model';

describe('EditConceptComponent', () => {
  let component: EditConceptComponent;
  let fixture: ComponentFixture<EditConceptComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let router: jasmine.SpyObj<Router>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let route: ActivatedRoute;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', [
      'getConceptById',
      'updateConcept',
      'publishConcept',
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [EditConceptComponent, FormsModule],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { id: 'test-concept-id' },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditConceptComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    snackBar = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    route = TestBed.inject(ActivatedRoute);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize conceptId and load the concept', () => {
      spyOn(component, 'loadConcept');
      component.ngOnInit();
      expect(component.conceptId).toBe('test-concept-id');
      expect(component.loadConcept).toHaveBeenCalled();
    });
  });

  describe('loadConcept', () => {
    it('should load the concept successfully', () => {
      const mockConcept: UpdatePostRequest = {
        title: 'Test Title',
        author: 'Test Author',
        content: 'Test content',
      };

      postService.getConceptById.and.returnValue(of(mockConcept));
      component.loadConcept();
      expect(component.concept).toEqual(mockConcept);
    });

    it('should handle error when loading the concept', () => {
      postService.getConceptById.and.returnValue(throwError('Error loading concept'));
      spyOn(console, 'error');
      component.loadConcept();
      expect(console.error).toHaveBeenCalledWith('Fout bij het laden van het concept:', 'Error loading concept');
    });
  });

  describe('saveAsConcept', () => {
    it('should save the concept successfully', () => {
      postService.updateConcept.and.returnValue(of(null));
      component.saveAsConcept();
      expect(snackBar.open).toHaveBeenCalledWith('Concept change saved succesfully!', 'Close', { duration: 3000 });
      expect(router.navigateByUrl).toHaveBeenCalledWith('/concepts');
    });

    it('should handle error when saving the concept', () => {
      postService.updateConcept.and.returnValue(throwError('Error saving concept'));
      component.saveAsConcept();
      expect(snackBar.open).toHaveBeenCalledWith('Something went wrong when trying to save the updated concept', 'Close', { duration: 3000 });
    });
  });

  describe('publish', () => {
    it('should publish the concept successfully', () => {
      postService.publishConcept.and.returnValue(of(null));
      component.publish();
      expect(snackBar.open).toHaveBeenCalledWith('Concept emited successfully!', 'Close', { duration: 3000 });
      expect(router.navigateByUrl).toHaveBeenCalledWith('/homepage');
    });

    it('should handle error when publishing the concept', () => {
      postService.publishConcept.and.returnValue(throwError('Error publishing concept'));
      component.publish();
      expect(snackBar.open).toHaveBeenCalledWith('Something went wrong when trying to publish the concept', 'Close', { duration: 3000 });
    });
  });
});