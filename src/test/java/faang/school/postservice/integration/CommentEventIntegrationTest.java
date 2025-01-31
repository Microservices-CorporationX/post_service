package faang.school.postservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.service.comment.CommentService;
import org.apache.kafka.test.IntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public class CommentEventIntegrationTest  extends IntegrationTestBase {
    private static final int USER_SERVICE_PORT = 9080;

    @Autowired
    private CommentService commentService;

    private static WireMockServer wireMockServer;

    private static final Long POST_ID = 1L;
    private static final String author = """
            {
                "id": 1,
                "username": "JohnDoe",
                "email": "johndoe@example.com",
                "preference": "EMAIL"
            }
            """;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(USER_SERVICE_PORT));
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get("/users/1")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(author)));
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldSendKafkaEvent() {
        CommentRequestDto commentDto = CommentRequestDto.builder()
                .authorId(1L)
                .postId(POST_ID)
                .content("Good comment")
                .build();

        commentService.createComment(commentDto);
    }
}
