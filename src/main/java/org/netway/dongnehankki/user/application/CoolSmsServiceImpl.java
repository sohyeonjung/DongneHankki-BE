package org.netway.dongnehankki.user.application;

import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.netway.dongnehankki.user.exception.InvalidAuthCodeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
public class CoolSmsServiceImpl implements CoolSmsService{

    private static final String SMS_AUTH_PREFIX = "smsAuth:";
    private static final Duration SMS_AUTH_EXPIRATION = Duration.ofMinutes(5);

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    private DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    public CoolSmsServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void initialize() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
    }

    private String generateAuthCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }


    private void saveAuthCodeToRedis(String phoneNumber, String authCode) {
        redisTemplate.opsForValue().set(SMS_AUTH_PREFIX + phoneNumber, authCode, SMS_AUTH_EXPIRATION);
    }

    @Async("smsExecutor")
    public void sendSms(String to) {
        String authCode = generateAuthCode();
        saveAuthCodeToRedis(to, authCode);

        Message message = new Message();
        message.setFrom(this.fromPhoneNumber);
        message.setTo(to);
        message.setText("[동네한끼] 인증번호는 [" + authCode + "] 입니다. 정확히 입력해주세요.");

        SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);

        this.messageService.sendOne(request);
    }

    public boolean verifyAuthCode(String phoneNumber, String authCode) {
        String key = SMS_AUTH_PREFIX + phoneNumber;
        String storedAuthCode = redisTemplate.opsForValue().get(key);

        if (storedAuthCode != null && storedAuthCode.equals(authCode)) {
            redisTemplate.delete(key);
            return true;
        } else {
            throw new InvalidAuthCodeException();
        }
    }
}

