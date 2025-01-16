import { Component, EventEmitter, inject, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostRequest } from '@models/postRequest.model';

@Component({
  selector: 'app-create-post',
  imports: [ReactiveFormsModule],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
  fb: FormBuilder = inject(FormBuilder);
    @Output() createPost = new EventEmitter<PostRequest>();
    @Output() createConceptPost = new EventEmitter<PostRequest>();

    postForm: FormGroup = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      author: ['', Validators.required]
    });

    onSubmit() {
      const newPost: PostRequest = {
        ...this.postForm.value
      };
      this.createPost.emit(newPost);
    }

    addAsConcept() {
      const conceptPost: PostRequest = {
        ...this.postForm.value
      };
      this.createConceptPost.emit(conceptPost);
    }
}