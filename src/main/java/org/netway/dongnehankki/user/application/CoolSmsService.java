package org.netway.dongnehankki.user.application;

public interface CoolSmsService {
    void sendSms(String to);
    boolean verifyAuthCode(String phoneNumber, String authCode);

}
