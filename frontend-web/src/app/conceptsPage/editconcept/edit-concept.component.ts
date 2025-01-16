import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '@services/post-service.service';
import { FormsModule } from '@angular/forms';
import { UpdatePostRequest } from '@models/updatePostRequest.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-concept',
  imports: [FormsModule],
  templateUrl: './edit-concept.component.html',
})
export class EditConceptComponent implements OnInit {
  concept: UpdatePostRequest = {
    title: '',
    author: '',
    content: '',
  };
  conceptId: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.conceptId = this.route.snapshot.params['id'];
    this.loadConcept();
  }

  loadConcept(): void {
    this.postService.getConceptById(this.conceptId).subscribe(
      (data) => {
        this.concept = data;
      },
      (error) => {
        console.error('Fout bij het laden van het concept:', error);
      }
    );
  }

  saveAsConcept(): void {
    this.postService.updateConcept(this.conceptId, this.concept).subscribe({
        next: () => {
            this.snackBar.open('Concept change saved succesfully!', 'Close', {
                duration: 3000,
            });
            this.router.navigateByUrl('/concepts');
        },
        error: () => {
            this.snackBar.open('Something went wrong when trying to save the updated concept', 'Close', {
              duration: 3000,
            });
          },
    });
  }

  publish(): void {
    this.postService.publishConcept(this.conceptId, this.concept).subscribe({
        next: () => {
            this.snackBar.open('Concept emited successfully!', 'Close', {
                duration: 3000,
            });
            this.router.navigateByUrl('/homepage');
        },
        error: () => {
            this.snackBar.open('Something went wrong when trying to publish the concept', 'Close', {
                duration: 3000,
            });
        },
    });
  }
}