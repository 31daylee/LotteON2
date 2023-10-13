package co.kr.lotte.controller.cs;

import co.kr.lotte.dto.cs.BoardDTO;
import co.kr.lotte.entity.cs.BoardTypeEntity;
import co.kr.lotte.service.CsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Log4j2
@Controller
public class CsController {

    @Autowired
    private CsService csService;

    @GetMapping("/cs/index")
    public String index() {
        return "/cs/index";
    }
    @GetMapping("/cs/faq/list")
    public String faqList() {
        return "/cs/faq/list";
    }

    @GetMapping("/cs/faq/view")
    public String faqView() {
        return "/cs/faq/view";
    }

    @GetMapping("/cs/notice/list")
    public String noticeList() {
        return "/cs/notice/list";
    }

    @GetMapping("/cs/notice/view")
    public String noticeView() {
        return "/cs/notice/view";
    }

    @GetMapping("/cs/qna/list")
    public String qnaList() {
        return "/cs/qna/list";
    }

    @GetMapping("/cs/qna/view")
    public String qnaView() {
        return "/cs/qna/view";
    }

    @GetMapping("/cs/qna/write")
    public String qnaWrite(Model model, String cate) {
        cate="member";
        List<BoardTypeEntity> boardTypeEntities = csService.findByCate(cate);
        model.addAttribute(boardTypeEntities);

        return "/cs/qna/write";
    }

    @PostMapping("cs/qna/write")
    public String qnaWrite(HttpServletRequest request, BoardDTO dto)  {
        log.info(dto.toString());
        csService.save(dto);
        return "redirect:/cs/qna/list";
    }



}
