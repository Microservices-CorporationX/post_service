package faang.school.postservice.client;

import faang.school.postservice.dto.payment.PaymentRequest;
import faang.school.postservice.dto.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest paymentRequest);
}
