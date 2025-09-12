package org.netway.dongnehankki.user.application;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.user.exception.InvalidAuthCodeException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoolSmsServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private DefaultMessageService defaultMessageService;

    @InjectMocks
    private CoolSmsService coolSmsService;

    @BeforeEach
    void 테스트_준비() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        ReflectionTestUtils.setField(coolSmsService, "apiKey", "testApiKey");
        ReflectionTestUtils.setField(coolSmsService, "apiSecret", "testApiSecret");
        ReflectionTestUtils.setField(coolSmsService, "fromPhoneNumber", "01012345678");

        ReflectionTestUtils.setField(coolSmsService, "messageService", defaultMessageService);
    }

    // @Test
    // void sendSms_인증번호_생성_저장_메시지_전송_확인() {
    //     String toPhoneNumber = "01098765432";
    //     SingleMessageSentResponse mockResponse = mock(SingleMessageSentResponse.class);
    //     when(defaultMessageService.sendOne(any(SingleMessageSendingRequest.class))).thenReturn(mockResponse);
    //
    //     SingleMessageSentResponse response = coolSmsService.sendSms(toPhoneNumber);
    //
    //     assertNotNull(response);
    //     verify(valueOperations, times(1)).set(
    //             startsWith("smsAuth:"),
    //             anyString(),
    //             any(Duration.class)
    //     );
    //
    //     ArgumentCaptor<SingleMessageSendingRequest> requestCaptor = ArgumentCaptor.forClass(SingleMessageSendingRequest.class);
    //     verify(defaultMessageService, times(1)).sendOne(requestCaptor.capture());
    //     SingleMessageSendingRequest capturedRequest = requestCaptor.getValue();
    //     assertNotNull(capturedRequest);
    //     assertEquals(toPhoneNumber, capturedRequest.getMessage().getTo());
    //     assertEquals("01012345678", capturedRequest.getMessage().getFrom());
    //     assertTrue(capturedRequest.getMessage().getText().contains("[동네한끼] 인증번호는 ["));
    //     assertTrue(capturedRequest.getMessage().getText().contains("] 입니다. 정확히 입력해주세요."));
    //
    //     String messageText = capturedRequest.getMessage().getText();
    //     int secondBracketStart = messageText.indexOf("[", messageText.indexOf("[") + 1);
    //     int secondBracketEnd = messageText.indexOf("]", secondBracketStart);
    //     String extractedAuthCode = messageText.substring(secondBracketStart + 1, secondBracketEnd);
    //     verify(valueOperations, times(1)).set(
    //             eq("smsAuth:" + toPhoneNumber),
    //             eq(extractedAuthCode),
    //             any(Duration.class)
    //     );
    // }

    @Test
    void verifyAuthCode_인증번호_일치시_true_반환_Redis_삭제_확인() {
        String phoneNumber = "01012345678";
        String authCode = "123456";
        when(valueOperations.get("smsAuth:" + phoneNumber)).thenReturn(authCode);

        boolean result = coolSmsService.verifyAuthCode(phoneNumber, authCode);

        assertTrue(result);
        verify(redisTemplate, times(1)).delete("smsAuth:" + phoneNumber);
    }

    @Test
    void verifyAuthCode_인증번호_불일치시_예외_발생_확인() {
        String phoneNumber = "01012345678";
        String authCode = "123456";
        String storedAuthCode = "654321";
        when(valueOperations.get("smsAuth:" + phoneNumber)).thenReturn(storedAuthCode);

        assertThrows(InvalidAuthCodeException.class,() -> {
            coolSmsService.verifyAuthCode(phoneNumber, authCode);
        });

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void verifyAuthCode_Redis에_인증번호_없을시_예외_발생_확인() {
        String phoneNumber = "01012345678";
        String authCode = "123456";
        when(valueOperations.get("smsAuth:" + phoneNumber)).thenReturn(null);

        assertThrows(InvalidAuthCodeException.class, () -> {
            coolSmsService.verifyAuthCode(phoneNumber, authCode);
        });

        verify(redisTemplate, never()).delete(anyString());
    }
}
