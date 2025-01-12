package be.pxl.services.feign;

import be.pxl.services.controller.request.PostStatusChangedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "post-service")
public interface PostStatusChangedClient {

    @PostMapping("/post/notifications")
    void sendChangesPostStatus(@RequestBody PostStatusChangedRequest request);
}
