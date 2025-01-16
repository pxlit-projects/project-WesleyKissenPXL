import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CreatePostComponent } from './create-post.component';
import { PostRequest } from '@models/postRequest.model';

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, CreatePostComponent], // Voeg de component hier toe aan imports
    }).compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should create a form with title, content, and author fields', () => {
    const form = component.postForm;
    expect(form.contains('title')).toBeTruthy();
    expect(form.contains('content')).toBeTruthy();
    expect(form.contains('author')).toBeTruthy();
  });

  it('should mark the form as invalid if required fields are empty', () => {
    component.postForm.setValue({ title: '', content: '', author: '' });
    expect(component.postForm.invalid).toBeTruthy();
  });

  it('should mark the form as valid when required fields are filled', () => {
    component.postForm.setValue({ title: 'Test Post', content: 'Test Content', author: 'Test Author' });
    expect(component.postForm.valid).toBeTruthy();
  });

  it('should emit createPost event with correct data on submit', fakeAsync(() => {
    spyOn(component.createPost, 'emit');
    component.postForm.setValue({ title: 'Test Title', content: 'Test Content', author: 'Test Author' });
    component.onSubmit();
    tick();
    const emittedValue: PostRequest = { title: 'Test Title', content: 'Test Content', author: 'Test Author' };
    expect(component.createPost.emit).toHaveBeenCalledWith(emittedValue);
  }));

  it('should emit createConceptPost event with correct data when adding as concept', fakeAsync(() => {
    spyOn(component.createConceptPost, 'emit');
    component.postForm.setValue({ title: 'Test Title', content: 'Test Content', author: 'Test Author' });
    component.addAsConcept();
    tick();
    const emittedValue: PostRequest = { title: 'Test Title', content: 'Test Content', author: 'Test Author' };
    expect(component.createConceptPost.emit).toHaveBeenCalledWith(emittedValue);
  }));

  it('should call createPost.emit with correct form data when form is valid', fakeAsync(() => {
    spyOn(component.createPost, 'emit');
    component.postForm.setValue({ title: 'Valid Title', content: 'Valid Content', author: 'Valid Author' });
    component.onSubmit();
    tick();
    expect(component.createPost.emit).toHaveBeenCalledWith({
      title: 'Valid Title',
      content: 'Valid Content',
      author: 'Valid Author'
    });
  }));

  it('should call createConceptPost.emit with correct form data when adding as concept', fakeAsync(() => {
    spyOn(component.createConceptPost, 'emit');
    component.postForm.setValue({ title: 'Concept Title', content: 'Concept Content', author: 'Concept Author' });
    component.addAsConcept();
    tick();
    expect(component.createConceptPost.emit).toHaveBeenCalledWith({
      title: 'Concept Title',
      content: 'Concept Content',
      author: 'Concept Author'
    });
  }));
});