export class Notificatie {
    id: string;
    postTitle: string;
    postId: string;
    status: string;
    rejectionReason: string;
  
    constructor(id: string, postTitle: string, postId: string, status: string, rejectionReason: string) {
      this.id = id;
      this.postTitle = postTitle;
      this.postId = postId;
      this.status = status;
      this.rejectionReason = rejectionReason;
    }
  }
  