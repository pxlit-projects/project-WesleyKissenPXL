import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ReviewablePost } from '@models/review.models';
import { PostService } from '@services/post-service.service';
import { ReviewService } from '@services/review-service.service';

@Component({
  selector: 'app-approvalpage',
  templateUrl: './approvals.component.html',
  imports: [FormsModule]
})
export class ApprovalComponent implements OnInit {
    waitingPosts: ReviewablePost[] = [];
  reviewData: { [conceptId: string]: { accepted: boolean; rejectionMessage: string } } = {};

  constructor(private router: Router, private reviewService: ReviewService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadConcepts();
  }

  loadConcepts(): void {
    this.reviewService.getPostsWaitingForApproval().subscribe(
      (data) => {
        this.waitingPosts = data;
        this.initializeReviewData();
      },
      (error) => {
        console.error('Error loading concepts:', error);
      }
    );
  }

  initializeReviewData(): void {
    this.waitingPosts.forEach((concept) => {
      this.reviewData[concept.id] = { accepted: true, rejectionMessage: '' };
    });
  }

  handleReviewChange(conceptId: string, accepted: boolean): void {
    this.reviewData[conceptId].accepted = accepted;
    if (accepted) {
      this.reviewData[conceptId].rejectionMessage = '';
    }
  }

    PublishPost(conceptId: string): void {
        this.reviewService.publishPost(conceptId).subscribe({
            next: () => {
                this.snackBar.open('Post succesfully published!', 'Close', {
                    duration: 3000,
                });
                this.router.navigateByUrl('/homepage');
            },
            error: () => {
                this.snackBar.open('Something went wrong when trying to publish the post', 'Close', {
                  duration: 3000,
                });
              },
        });
    }
    RejectPost(conceptId: string) {
        const review = { message: this.reviewData[conceptId].rejectionMessage }; 
        this.reviewService.rejectPost(conceptId, review).subscribe({
            next: () => {
                this.snackBar.open('Post succesfully rejected!', 'Close', {
                    duration: 3000,
                });
                this.router.navigateByUrl('/homepage');
            },
            error: () => {
                this.snackBar.open('Something went wrong when trying to reject the post', 'Close', {
                    duration: 3000,
                });
            },
        });
    }
}