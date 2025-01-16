import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PostService } from '@services/post-service.service';

@Component({
  selector: 'app-conceptpage',
  templateUrl: './concepts.component.html',
})
export class ConceptComponent implements OnInit {
  concepts: any[] = [];

  constructor(private router: Router, private postService: PostService) {}

  ngOnInit(): void {
    this.loadConcepts();
  }

  loadConcepts(): void {
    this.postService.getConceptPosts().subscribe(
      (data) => {
        this.concepts = data;
      },
      (error) => {
        console.error('Fout bij het laden van concepten:', error);
      }
    );
  }

  editConcept(id: string): void {
    this.router.navigate([`/concepts/${id}`]);
}
}