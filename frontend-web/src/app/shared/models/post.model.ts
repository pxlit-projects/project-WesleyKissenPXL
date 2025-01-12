export class Post {
    id: string;
    title: string;
    content: string;
    author: string;
    timeOfCreation: Date;
    status: 'CONCEPT' | 'POSTED' | 'WAITING_FOR_APPROVEL' | 'REJECTED'; // Assuming these are the possible statuses
    rejectionReason?: string; // Optional property
  
    constructor(
      id: string,
      title: string,
      content: string,
      author: string,
      timeOfCreation: Date,
      status: 'CONCEPT' | 'POSTED' | 'WAITING_FOR_APPROVEL' | 'REJECTED',
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
  