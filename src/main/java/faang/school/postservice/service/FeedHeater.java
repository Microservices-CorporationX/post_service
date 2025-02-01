package faang.school.postservice.service;

import org.springframework.scheduling.annotation.Async;

public class FeedHeater {

    @Async(value = "feedHeaterPool")
    public void start() {

    }
}
