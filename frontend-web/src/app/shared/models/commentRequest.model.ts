export class CommentRequest {
    content: string;
    author: string;
  
    constructor(content: string, author: string) {
      this.content = content;
      this.author = author;
    }
  }