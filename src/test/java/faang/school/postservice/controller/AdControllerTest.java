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
    private static final String AD_BUY_URL = "/ad/buy/{postId}";
    private static final String USER_DTO_JSON = "{\"id\": 1, \"username\": \"Name1\", \"email\": \"test1@gmail.com\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;

    @Test
    void testBuyAd() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("Name1")
                .email("test1@gmail.com")
                .build();

        when(adService.getUserWhoBuyAd(userDto, 1L)).thenReturn(userDto);
        
        mockMvc.perform(post(AD_BUY_URL, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_DTO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(adService, times(1)).getUserWhoBuyAd(userDto, 1L);
    }
}
