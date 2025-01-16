import { inject, Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.development"
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { Comment } from "@models/comment.model";
import { CommentRequest } from "@models/commentRequest.model";
import { UpdateCommentRequest } from "@models/updateCommentRequest.model";
import { AuthService } from "./auth-service.service";

@Injectable({
    providedIn: 'root'
})

export class CommentService {
    url = environment.commentApiUrl;
    http: HttpClient = inject(HttpClient);
    private authService: AuthService = inject(AuthService);
  
    getComments() : Observable<Comment[]> {
        const role = this.authService.getRole() || 'gebruiker'; 
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Role': role,
          });
        const url = `${this.url}/getAllComments`;
        return this.http.get<Comment[]>(url, {headers});
  }

  addComment(postId: string, newComment: CommentRequest) {
    const role = this.authService.getRole() || 'gebruiker'; 
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Role': role,
          });
    const url = `${this.url}/${postId}/addComment`;
    return this.http.post<Comment[]>(url, newComment, {headers});
  }


  // Update een comment
  updateComment(commentId: string, updatedComment: UpdateCommentRequest): Observable<Comment> {
    const role = this.authService.getRole() || 'gebruiker'; 
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Role': role,
          });
    return this.http.put<Comment>(`${this.url}/${commentId}/change`, updatedComment, {headers});
  }

  // Verwijder een comment
  deleteComment(commentId: string): Observable<void> {
    const role = this.authService.getRole() || 'gebruiker'; 
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Role': role,
          });
    return this.http.delete<void>(`${this.url}/${commentId}/deleteComment`, {headers});
  }

}