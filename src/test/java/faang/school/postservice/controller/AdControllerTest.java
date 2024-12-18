package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.AdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {AdController.class, AdController.class})
public class AdControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;

    private static final String AD_BUY_URL = "/ad/buy";

    @Test
    void testBuyAdSuccess() throws Exception {
        Long postId = 1L;
        Long userId = 2L;
        BigDecimal paymentAmount = new BigDecimal("100.00");
        Long adDuration = 30L;

        mockMvc.perform(post(AD_BUY_URL)
                        .param("postId", postId.toString())
                        .param("userId", userId.toString())
                        .param("paymentAmount", paymentAmount.toString())
                        .param("adDuration", adDuration.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adService, times(1))
                .getUserWhoBuyAd(postId, userId, paymentAmount, adDuration);
    }
}
