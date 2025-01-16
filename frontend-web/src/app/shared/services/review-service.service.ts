import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ReviewablePost } from "@models/review.models";
import { environment } from "environments/environment.development";
import { Observable } from "rxjs";
import { AuthService } from "./auth-service.service";

@Injectable({
    providedIn: 'root'
})
export class ReviewService {
    url = environment.reviewApiUrl;
    http: HttpClient = inject(HttpClient);
    private authService: AuthService = inject(AuthService);



    getPostsWaitingForApproval() : Observable<ReviewablePost[]> {
        const role = this.authService.getRole() || 'gebruiker'; 
                const headers = new HttpHeaders({
                    'Content-Type': 'application/json',
                    'Role': role,
                  });
        const url = `${this.url}/getAllReviewablePosts`;
        return this.http.get<ReviewablePost[]>(url, {headers});
    }

    publishPost(reviewId: string) : Observable<any>{
        const role = this.authService.getRole() || 'gebruiker'; 
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Role': role,
          });
        const url = `${this.url}/${reviewId}/publishReviewPost`;
        return this.http.put<any>(url, {}, { headers: headers });
    }

    rejectPost(reviewId: string, review: {message?: string; }) : Observable<any>{
         const role = this.authService.getRole() || 'gebruiker'; 
                const headers = new HttpHeaders({
                    'Content-Type': 'application/json',
                    'Role': role,
                  });
        const url = `${this.url}/${reviewId}/reject`;
        return this.http.put<any>(url, review, {headers});
    }
}