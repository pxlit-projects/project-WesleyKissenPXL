import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostRequest } from '@models/postRequest.model';
import { PostService } from '@services/post-service.service';
import { CreatePostComponent } from './create-post/create-post.component';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  imports: [CreatePostComponent],
})
export class PostsComponent {
  constructor(
    private router: Router,
    private postService: PostService,
    private snackBar: MatSnackBar
  ) {}

  processAdd(post: PostRequest) {
    this.postService.createpost(post).subscribe({
      next: () => {
        this.snackBar.open('Post Made succesfully!', 'Close', {
          duration: 3000,
        });
        this.router.navigateByUrl('/homepage');
      },
      error: () => {
        this.snackBar.open('Something went wrong, try again', 'Close', {
          duration: 3000,
        });
      },
    });
  }


  processAddConcept(concept: PostRequest) {
    this.postService.createConceptPost(concept).subscribe({
      next: () => {
        this.snackBar.open('Concept succesfully saved!', 'Close', {
          duration: 3000,
        });
        this.router.navigateByUrl('/homepage');
      },
      error: () => {
        this.snackBar.open('Something went wrong when trying to save the concept', 'Close', {
          duration: 3000,
        });
      },
    });
  }
}