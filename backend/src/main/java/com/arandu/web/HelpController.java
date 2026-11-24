package com.arandu.web;

import com.arandu.dto.HelpDtos;
import com.arandu.security.UserPrincipal;
import com.arandu.service.HelpService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelpController {

    private final HelpService helpService;

    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    @GetMapping("/help")
    public HelpDtos.HelpSearchResponse help(
            @RequestParam Long courseId,
            @RequestParam(required = false) String q) {
        return helpService.search(courseId, q);
    }

    @PostMapping("/tutor-questions")
    public HelpDtos.TutorQuestionResponse askTutor(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody HelpDtos.TutorQuestionRequest request) {
        return helpService.askTutor(principal.getId(), request);
    }

    @GetMapping("/tutor-questions/mine")
    public List<HelpDtos.TutorQuestionResponse> myQuestions(@AuthenticationPrincipal UserPrincipal principal) {
        return helpService.myQuestions(principal.getId());
    }
}
