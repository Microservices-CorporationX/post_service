package faang.school.postservice.docs.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Operation(summary = "Update post", description = "Returns updated post")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful"),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                        value = "Post author id must be the same as user id"
                )
        )),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                        value = "User or project not found"
                )
        ))
})
public @interface UpdatePostDoc { }
