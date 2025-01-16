import { inject, Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.development"
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { PostRequest} from "@models/postRequest.model";
import { UpdatePostRequest } from "@models/updatePostRequest.model";
import { Post } from "@models/post.model";
import { Notificatie } from "@models/notificatie.model";
import { AuthService } from "./auth-service.service";

@Injectable({
    providedIn: 'root'
})
export class PostService {
    
    url = environment.postApiUrl;
    http: HttpClient = inject(HttpClient);
    private authService: AuthService = inject(AuthService);



    getPosts() : Observable<Post[]> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/getAllPosted`;
        return this.http.get<Post[]>(url,{headers});
    }

    getConceptPosts() : Observable<Post[]> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/getAllConcepts`;
        return this.http.get<Post[]>(url, {headers});
    }


    createpost(postrequest: PostRequest): Observable<any> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/add`;
        return this.http.post(url, postrequest, {headers});
    }

    createConceptPost(postrequest: PostRequest): Observable<any> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/addAsConcept`;
        return this.http.post(url, postrequest, {headers});
    }


    getConceptById(id: string): Observable<any> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/getConceptPost/${id}`;
        return this.http.get(url, {headers});
      }

      
    loadRejectedPost(id: string): Observable<any> {
      const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
      const url = `${this.url}/getConceptPost/${id}`;
        return this.http.get(url, {headers});
    }


      getNotifications() : Observable<Notificatie[]>{
        const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/notifications`;
        return this.http.get<Notificatie[]>(url, {headers});
    }
      
      updateConcept(id: string, concept: UpdatePostRequest): Observable<any> {
        const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/change/${id}`;
        return this.http.put(url, concept, {headers});
      }
      
      publishConcept(id: string, concept: UpdatePostRequest): Observable<any> {
        const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/conceptPosted/${id}`;
        return this.http.put(url, concept, {headers});
      }
      filterPosts(searchParams: any): Observable<Post[]> {
        const role = this.authService.getRole() || 'gebruiker'; 
              const headers = new HttpHeaders({
                  'Content-Type': 'application/json',
                  'Role': role,
                });
        const url = `${this.url}/filter`;
        return this.http.get<Post[]>(url, {
          params: searchParams,
          headers,
        },);
      }
}