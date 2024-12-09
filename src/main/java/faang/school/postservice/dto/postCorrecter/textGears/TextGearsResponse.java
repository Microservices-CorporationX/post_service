package faang.school.postservice.dto.postCorrecter.textGears;

import faang.school.postservice.dto.postCorrecter.PostCorrecterResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextGearsResponse implements PostCorrecterResponse {
    private final boolean status;
    private final Response response;

    public static record Response(String corrected) {
    }

    @Override
    public String getCorrectedPost() {
        return response.corrected();
    }

    @Override
    public boolean isSuccess() {
        return status;
    }
}