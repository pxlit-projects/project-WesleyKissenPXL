import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ConceptComponent } from './concepts.component';
import { PostService } from '@services/post-service.service';

describe('ConceptComponent', () => {
  let component: ConceptComponent;
  let fixture: ComponentFixture<ConceptComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getConceptPosts']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ConceptComponent], // Voeg het component toe aan imports
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConceptComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('loadConcepts', () => {
    it('should load concepts and assign them to the component', () => {
      const mockConcepts = [
        {
          id: '1',
          title: 'Concept 1',
          content: 'Content 1',
          author: 'Author 1',
          timeOfCreation: new Date(),
          status: 'CONCEPT' as const,
        },
      ];
      postService.getConceptPosts.and.returnValue(of(mockConcepts));

      component.loadConcepts();

      expect(postService.getConceptPosts).toHaveBeenCalled();
      expect(component.concepts).toEqual(mockConcepts);
    });

    it('should handle errors when loading concepts', () => {
      const error = 'Error loading concepts';
      postService.getConceptPosts.and.returnValue(throwError(error));
      spyOn(console, 'error');

      component.loadConcepts();

      expect(postService.getConceptPosts).toHaveBeenCalled();
      expect(component.concepts).toEqual([]);
      expect(console.error).toHaveBeenCalledWith('Fout bij het laden van concepten:', error);
    });
  });

  describe('editConcept', () => {
    it('should navigate to the edit concept page with the correct ID', () => {
      const conceptId = '123';
      component.editConcept(conceptId);

      expect(router.navigate).toHaveBeenCalledWith([`/concepts/${conceptId}`]);
    });
  });
});