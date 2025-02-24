package ru.corporationx.postservice.controller.ad;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ad-controller", description = "Ads")
public interface AdControllerOas {
    @Operation(summary = "Buy ad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
    })
    void buyAd(long postId);
}
