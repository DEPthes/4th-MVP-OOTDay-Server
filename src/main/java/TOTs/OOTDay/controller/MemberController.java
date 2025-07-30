package TOTs.OOTDay.controller;


import TOTs.OOTDay.dto.MemberJoinDTO;
import TOTs.OOTDay.dto.MemberLoginDTO;
import TOTs.OOTDay.dto.MemberLoginResponseDTO;
import TOTs.OOTDay.dto.MemberWithdrawDTO;
import TOTs.OOTDay.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<UUID> join(@RequestBody MemberJoinDTO joinDTO) {
        UUID id = memberService.join(joinDTO); // 회원가입 후 UUID 받아옴.
        return ResponseEntity.ok(id);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDTO> login(@RequestBody MemberLoginDTO loginDTO) {
        String token = memberService.login(loginDTO); // JWT 토큰 발급

        return ResponseEntity.ok(new MemberLoginResponseDTO(token));
    }

    // 회원탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody MemberWithdrawDTO dto, @RequestHeader("Authorization") String token) {
        memberService.withdraw(dto, token); // 회원 탈퇴
        return ResponseEntity.ok("회원 탈퇴가 완료되었어요.");

    }
}
