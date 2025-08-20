package TOTs.OOTDay.sms;

import TOTs.OOTDay.sms.exception.SmsCodeMismatch;
import TOTs.OOTDay.sms.exception.SmsCodeNotSent;
import TOTs.OOTDay.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${solapi.sender}")
    private String sender;

    private final DefaultMessageService messageService;

    // 회원가입 인증번호 저장
    private final Map<String, String> signupVerificationMap = new ConcurrentHashMap<>(); // 인증번호 임시 저장
    private final Map<String, Boolean> signupVerifiedPhoneMap = new ConcurrentHashMap<>(); // 인증 완료 전화번호 저장

    // 아이디/비밀번호 찾기 인증번호
    private final Map<String, String> findAccountVerificationMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> findAccountVerifiedPhoneMap = new ConcurrentHashMap<>();

    // 인증번호 생성
    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6자리
    }

    // 회원가입 시 인증번호 전송
    public void sendSignupVerificationCode(String phoneNumber) {
        String code = generateCode();
        signupVerificationMap.put(phoneNumber, code); // 코드 저장
        sendSms(phoneNumber, "[OOTDay] 인증번호 : " + code);
    }

    // 아이디/비밀번호 찾기 시 인증번호 전송
    public void sendFindAccountVerificationCode(String phoneNumber) {
        String code = generateCode();
        findAccountVerificationMap.put(phoneNumber, code);
        sendSms(phoneNumber, "[OOTDay] 인증번호 : " + code);
    }
/*
    // 메시지 전송
    private void sendSms(String to, String text){
        try {
            // 메시지 객체 생성
            Message message = new Message(); // 메시지 인스턴스
            message.setFrom(sender); // 발신번호
            message.setTo(to); // 수신번호
            message.setText(text); // 메시지 내용

            // 요청 객체
            SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);

            //SDK 호출 -> 실제 발송
            SingleMessageSentResponse response = messageService.sendOne(request);

            // 확인
            System.out.println("[Solapi] : " + response.getMessageId() + ", status : " + response.getStatusMessage()); // 확인 해봐야함.
        } catch (Exception e) {
            throw new RuntimeException("문자 전송 실패 : " + e.getMessage(), e);
        }
    }
    */

    // 메시지로 전송
    private void sendSms(String to, String text) {
        try {
            Message m = new Message();
            m.setFrom(sender != null ? sender.replaceAll("\\D", "") : null);
            m.setTo(to != null ? to.replaceAll("\\D", "") : null);
            m.setText(text);

            var res = messageService.sendOne(new SingleMessageSendingRequest(m));
            System.out.println("[Solapi] = " + res.getMessageId() + ", status = " + res.getStatusMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("문자 전송 실패: " + e.getMessage(), e);
        }
    }



    // 회원가입 시 인증번호 검증
    public void verifySignupCode(String phoneNumber, String inputCode) {
        String savedCode = signupVerificationMap.get(phoneNumber);

        if (savedCode == null) {
            throw new SmsCodeNotSent();
        }
        if (!inputCode.equals(savedCode)) {
            throw new SmsCodeMismatch();
        }
        signupVerifiedPhoneMap.put(phoneNumber, true);
        signupVerificationMap.remove(phoneNumber);
    }

    // 회원가입 시 인증 여부 확인용 메서드
    public boolean isSignupVerified(String phoneNumber) {
        return signupVerifiedPhoneMap.getOrDefault(phoneNumber, false);
    }

    // 회원가입 완료 후 인증상태 및 코드 삭제 (중복 가입/보안)
    public void clearSignupVerification(String phoneNumber) {
        signupVerifiedPhoneMap.remove(phoneNumber);
        signupVerificationMap.remove(phoneNumber);
    }

    // 아이디/비밀번호 찾기 시 인증번호 검증
    public boolean verifyFindAccountCode(String phoneNumber, String inputCode) {
        String savedCode = findAccountVerificationMap.get(phoneNumber);
        boolean isCorrect = inputCode.equals(savedCode);
        if (isCorrect) {
            findAccountVerifiedPhoneMap.put(phoneNumber, true);
            findAccountVerificationMap.remove(phoneNumber);
        }
        return isCorrect;
    }

    // 아이디/비밀번호 찾기 시 인증 여부 확인
    public boolean isFindAccountVerified(String phoneNumber) {
        return findAccountVerifiedPhoneMap.getOrDefault(phoneNumber, false);
    }

    // 아이디/비밀번호 찾기 인증 완료 후 코드 삭제
    public void clearFindAccountVerification(String phoneNumber) {
        findAccountVerifiedPhoneMap.remove(phoneNumber);
        findAccountVerificationMap.remove(phoneNumber);
    }
}