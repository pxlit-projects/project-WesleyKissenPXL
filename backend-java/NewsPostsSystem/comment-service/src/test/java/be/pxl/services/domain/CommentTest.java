package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testCommentBuilderAndGetters() {
        UUID id = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String content = "This is a test comment.";
        String author = "Test Author";

        Comment comment = Comment.builder()
                .id(id)
                .postId(postId)
                .content(content)
                .author(author)
                .build();

        assertNotNull(comment);
        assertEquals(id, comment.getId());
        assertEquals(postId, comment.getPostId());
        assertEquals(content, comment.getContent());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Comment comment = new Comment();

        assertNotNull(comment);

        UUID id = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String content = "Another test comment.";
        String author = "Another Author";

        comment.setId(id);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setAuthor(author);

        assertEquals(id, comment.getId());
        assertEquals(postId, comment.getPostId());
        assertEquals(content, comment.getContent());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String content = "Constructor test comment.";
        String author = "Constructor Author";

        Comment comment = new Comment(id, postId, content, author);

        assertNotNull(comment);
        assertEquals(id, comment.getId());
        assertEquals(postId, comment.getPostId());
        assertEquals(content, comment.getContent());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String content = "Equality test comment.";
        String author = "Equality Author";

        Comment comment1 = new Comment(id, postId, content, author);
        Comment comment2 = new Comment(id, postId, content, author);

        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());

        comment2.setAuthor("Different Author");
        assertNotEquals(comment1, comment2);
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        String content = "ToString test comment.";
        String author = "ToString Author";

        Comment comment = new Comment(id, postId, content, author);

        String expected = "Comment(id=" + id + ", postId=" + postId + ", content=" + content + ", author=" + author + ")";
        assertEquals(expected, comment.toString());
    }
}
