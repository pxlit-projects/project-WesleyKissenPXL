import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Post } from '@models/post.model';
import { Comment } from '@models/comment.model';
import { AuthService } from '@services/auth-service.service';
import { PostService } from '@services/post-service.service';
import { CommentService } from '@services/comment-service.service';
import { CommentRequest } from '@models/commentRequest.model';
import { UpdateCommentRequest } from '@models/updateCommentRequest.model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-homepage',
  imports: [FormsModule],
  templateUrl: './homepage.component.html',
})
export class HomepageComponent implements OnInit {
  role: string | null = null;
  username: string | null = null;
  showerror: boolean = false;
  posts: Post[] = [];
  comments: Comment[] = [];
  searchParams = {
    content: '',
    author: '',
    fromDate: '',
    toDate: '',
  };
  newComments: { [postId: string]: string } = {};

  constructor(private authService: AuthService, private router: Router, private postService: PostService, private commentService: CommentService) {}

  ngOnInit(): void {
    this.loadPosts();
    this.loadComments();
    this.role = this.authService.getRole();
    this.username = this.authService.getUserName();
    if (this.role === 'hoofdredacteur') {
      this.showerror = true;
    }
  }

  onSearch(): void {
    this.postService.filterPosts(this.searchParams).subscribe(
      (data) => {
        this.posts = data;
      },
      (error) => {
        console.error('Error fetching posts:', error);
      }
    );
  }

  getCommentsByPostId(postId: string): Comment[] {
    return this.comments.filter((c) => c.postId === postId);
  }

  hasComments(postId: string): boolean {
    return this.getCommentsByPostId(postId).length > 0;
  }

  loadPosts(): void {
    this.postService.getPosts().subscribe(
      (data) => {
        this.posts = data;
      },
      (error) => {
        console.error('Error loading posts:', error);
      }
    );
  }

  loadComments(): void {
    this.commentService.getComments().subscribe(
      (data) => {
        this.comments = data;
      },
      (error) => {
        console.error('Error loading comments:', error);
      }
    );
  }

  addComment(postId: string): void {
    const content = this.newComments[postId];
    const author = this.authService.getUserName();

    if (!content) {
      console.warn('Comment content is empty.');
      return;
    }

    const newComment = new CommentRequest(content, author);

    this.commentService.addComment(postId ,newComment).subscribe(
      (response) => {
        if (Array.isArray(response)) {
          this.comments.push(...response);
        } else {
          // If the response is a single comment, push it into the comments array.
          this.comments.push(response);
        }
        this.newComments[postId] = '';
      },
      (error) => {
        console.error('Error adding comment:', error);
      }
    );
    window.location.reload();
  }

  CreatePostRedirect(): void {
    this.router.navigate(['/posts']);
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['']);
  }

  GoToPostConcepts(): void {
    this.router.navigate(['/concepts']);
  }

  GoToPostWaitingApprovals(): void {
    this.router.navigate(['/waitingapproval']);
  }

  GoToNotifications(): void {
    this.router.navigate(['/notifications']);
  }

  onEditComment(comment: Comment): void {
    const newCommentContent = prompt('Edit your comment:', comment.comment);

    if (newCommentContent && newCommentContent !== comment.comment) {
      const updatedComment = new UpdateCommentRequest(newCommentContent);
      this.commentService.updateComment(comment.id, updatedComment).subscribe(
        (response) => {
          // Comment bijgewerkt, we moeten de comment in de UI bijwerken
          const updatedCommentIndex = this.comments.findIndex(c => c.id === comment.id);
          if (updatedCommentIndex !== -1) {
            this.comments[updatedCommentIndex].comment = newCommentContent;
          }
        },
        (error) => {
          console.error('Error updating comment:', error);
        }
      );
    }
  }

  onDeleteComment(commentId: string): void {
    if (confirm('Are you sure you want to delete this comment?')) {
      this.commentService.deleteComment(commentId).subscribe({
        next: () => {
            this.comments = this.comments.filter(c => c.id !== commentId);
        },
    });
    window.location.reload();
    }
  }
}