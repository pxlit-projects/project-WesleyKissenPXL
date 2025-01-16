export class CommentRequest {
    content: string;
    author: string | null;
  
    constructor(content: string, author: string | null) {
      this.content = content;
      this.author = author;
    }
  }