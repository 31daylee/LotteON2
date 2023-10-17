package co.kr.lotte.controller.member;

import co.kr.lotte.dto.member.MemberDTO;
import co.kr.lotte.entity.member.MemberTermsEntity;
import co.kr.lotte.service.member.MemberService;
import groovy.util.logging.Log4j2;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/member/login")
    public String login(String success, Model model) {
        model.addAttribute("success", success);
        return "/member/login";
    }

    @GetMapping("/member/join")
    public String join() {
        return "/member/join";
    }

    @GetMapping("/member/signup")
    public String signup(@RequestParam String type, Model model) {

        model.addAttribute("type", type);
        MemberTermsEntity terms = memberService.findByTerms();
        model.addAttribute("terms", terms);
        return "/member/signup";
    }

    @GetMapping("/member/register")
    public String register(String type) {

        if (type.equals("normal"))
            return "/member/register";
        else if (type.equals("seller"))
            return "redirect:/member/registerSeller";
        return "/member/register";
    }
    @PostMapping("/member/register")
    public String register(MemberDTO dto, HttpServletRequest request) {

        dto.setRegip(request.getRemoteAddr());
        memberService.save(dto);
        return "redirect:/member/login";
    }

    @GetMapping("member/registerSeller")
    public String registerSeller() {
        return "/member/registerSeller";
    }
}
