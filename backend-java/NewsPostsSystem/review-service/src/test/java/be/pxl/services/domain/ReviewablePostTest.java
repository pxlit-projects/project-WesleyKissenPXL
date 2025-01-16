package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewablePostTest {

    @Test
    void testConstructorAndBuilder() {
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        String author = "Test Author";
        LocalDateTime timeOfCreation = LocalDateTime.now();
        Status status = Status.WAITING_FOR_APPROVEL;
        String rejectionReason = "Test rejection reason";

        // Using the builder
        ReviewablePost post = ReviewablePost.builder()
                .id(id)
                .title(title)
                .content(content)
                .author(author)
                .timeOfCreation(timeOfCreation)
                .status(status)
                .rejectionReason(rejectionReason)
                .build();

        assertNotNull(post);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(author, post.getAuthor());
        assertEquals(timeOfCreation, post.getTimeOfCreation());
        assertEquals(status, post.getStatus());
        assertEquals(rejectionReason, post.getRejectionReason());
    }

    @Test
    void testNoArgsConstructor() {
        // Test the no-args constructor
        ReviewablePost post = new ReviewablePost();
        assertNotNull(post);
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        String author = "Test Author";
        LocalDateTime timeOfCreation = LocalDateTime.now();
        Status status = Status.WAITING_FOR_APPROVEL;
        String rejectionReason = "Test rejection reason";

        // Test the all-args constructor
        ReviewablePost post = new ReviewablePost(id, title, content, author, timeOfCreation, status, rejectionReason);

        assertNotNull(post);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(author, post.getAuthor());
        assertEquals(timeOfCreation, post.getTimeOfCreation());
        assertEquals(status, post.getStatus());
        assertEquals(rejectionReason, post.getRejectionReason());
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        String author = "Test Author";
        LocalDateTime timeOfCreation = LocalDateTime.now();
        Status status = Status.WAITING_FOR_APPROVEL; // Adjust according to your enum
        String rejectionReason = "Test rejection reason";

        ReviewablePost post = new ReviewablePost();
        post.setId(id);
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setTimeOfCreation(timeOfCreation);
        post.setStatus(status);
        post.setRejectionReason(rejectionReason);

        // Assert each getter
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(author, post.getAuthor());
        assertEquals(timeOfCreation, post.getTimeOfCreation());
        assertEquals(status, post.getStatus());
        assertEquals(rejectionReason, post.getRejectionReason());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        String author = "Test Author";
        LocalDateTime timeOfCreation = LocalDateTime.now();
        Status status = Status.WAITING_FOR_APPROVEL;
        String rejectionReason = "Test rejection reason";

        ReviewablePost post1 = new ReviewablePost(id, title, content, author, timeOfCreation, status, rejectionReason);
        ReviewablePost post2 = new ReviewablePost(id, title, content, author, timeOfCreation, status, rejectionReason);

        // Test equality
        assertEquals(post1, post2);

        // Test hashcode
        assertEquals(post1.hashCode(), post2.hashCode());

        // Test inequality
        ReviewablePost post3 = new ReviewablePost(UUID.randomUUID(), title, content, author, timeOfCreation, status, rejectionReason);
        assertNotEquals(post1, post3);
        assertNotEquals(post1.hashCode(), post3.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        String author = "Test Author";
        LocalDateTime timeOfCreation = LocalDateTime.now();
        Status status = Status.WAITING_FOR_APPROVEL;
        String rejectionReason = "Test rejection reason";

        ReviewablePost post = new ReviewablePost(id, title, content, author, timeOfCreation, status, rejectionReason);
        String toString = post.toString();

        // Check that the toString contains the relevant fields
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(title));
        assertTrue(toString.contains(content));
        assertTrue(toString.contains(author));
        assertTrue(toString.contains(status.toString()));
    }
}
