package be.pxl.services.repository;


import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> getPostsByStatus(Status status);
}
