export class Comment {
  id: string;
  comment: string;
  author: string;
  postId: string;

  constructor(id: string, comment: string, author: string, postId: string) {
    this.id = id;
    this.comment = comment;
    this.author = author;
    this.postId = postId;
  }
}
