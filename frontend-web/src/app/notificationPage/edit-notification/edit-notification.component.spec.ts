import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditNotificationComponent } from './edit-notification.component';
import { PostService } from '@services/post-service.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { UpdatePostRequest } from '@models/updatePostRequest.model';

describe('EditNotificationComponent', () => {
  let component: EditNotificationComponent;
  let fixture: ComponentFixture<EditNotificationComponent>;
  let mockPostService: jasmine.SpyObj<PostService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockActivatedRoute: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(() => {
    mockPostService = jasmine.createSpyObj('PostService', ['getConceptById', 'publishConcept']);
    mockRouter = jasmine.createSpyObj('Router', ['navigateByUrl']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockActivatedRoute = jasmine.createSpyObj('ActivatedRoute', ['snapshot'], {
      snapshot: { params: { id: '123' } },
    });

    TestBed.configureTestingModule({
      imports: [EditNotificationComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });

    fixture = TestBed.createComponent(EditNotificationComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load concept data on ngOnInit', () => {
    const mockConcept: UpdatePostRequest = { title: 'Title', author: 'Author', content: 'Content' };
    mockPostService.getConceptById.and.returnValue(of(mockConcept));

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.concept).toEqual(mockConcept);
  });

  it('should log error when loading concept fails', () => {
    spyOn(console, 'error');
    mockPostService.getConceptById.and.returnValue(throwError(() => new Error('Error loading concept')));

    component.loadNotificationPost();

    expect(console.error).toHaveBeenCalledWith('Fout bij het laden van het concept:', jasmine.any(Error));
  });
});