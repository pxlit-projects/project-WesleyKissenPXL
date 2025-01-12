export class ReviewablePost {
    id: string;
    title: string;
    content: string;
    author: string;
    timeOfCreation: Date;
    status: 'CONCEPT' | 'POSTED' | 'WAITING_FOR_APPROVAL' | 'REJECTED';
    rejectionReason?: string; 
  
    constructor(
      id: string,
      title: string,
      content: string,
      author: string,
      timeOfCreation: Date,
      status: 'CONCEPT' | 'POSTED' | 'WAITING_FOR_APPROVAL' | 'REJECTED',
      rejectionReason?: string
    ) {
      this.id = id;
      this.title = title;
      this.content = content;
      this.author = author;
      this.timeOfCreation = timeOfCreation;
      this.status = status;
      this.rejectionReason = rejectionReason;
    }
  }