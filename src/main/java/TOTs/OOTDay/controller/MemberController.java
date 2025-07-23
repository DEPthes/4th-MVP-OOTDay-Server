package TOTs.OOTDay.controller;


import TOTs.OOTDay.dto.MemberJoinDTO;
import TOTs.OOTDay.dto.NameValidationRequest;
import TOTs.OOTDay.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<UUID> join(@RequestBody MemberJoinDTO joinDTO) {
        UUID id = memberService.join(joinDTO); // 회원가입 후 UUID 받아옴.
        return ResponseEntity.ok(id);
    }

    // 이름 유효성 검사
    @PostMapping("/validate-name")
    public ResponseEntity<String> validateName(@RequestBody NameValidationRequest request) {
        memberService.validateName(request.getName());
        return ResponseEntity.ok("가능한 이름입니다.");
    }
}
