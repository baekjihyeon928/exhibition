package com.exhibition.modules.main;

import com.exhibition.modules.account.AccountRepository;
import com.exhibition.modules.account.CurrentAccount;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.exhibit.Exhibit;
import com.exhibition.modules.exhibit.ExhibitRepository;
import com.exhibition.modules.exhibit.ExhibitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ExhibitRepository exhibitRepository;
    private final AccountRepository accountRepository;
    private final ExhibitService exhibitService;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model){
        if(account != null && !account.isProfessor){//학생 계정
            //Account accountLoaded = accountRepository.findAccountWithTagsAndMajorsById(account.getIdx());
            Account accountLoaded = accountRepository.findAccountWithMajorsByIdx(account.getIdx());
            model.addAttribute(accountLoaded);
            //model.addAttribute("account",account);
            //model.addAttribute("exhibitListLogin", exhibitRepository.findAccountByMajor(accountLoaded.getMajors()));
            model.addAttribute("exhibitList", exhibitRepository.findFirst30ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
            model.addAttribute("exhibitOwnerOf",
                    exhibitRepository.findByOwnersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";
        }

        if(account != null && account.isProfessor){ //교수님 계정
            /**Account accountLoaded = accountRepository.findAccountWithStudentsByIdx(account.getIdx());
            model.addAttribute("exhibitList", exhibitRepository.findByStudent(
                    accountLoaded.getStudents()));**/
            model.addAttribute("account",account);
            model.addAttribute("exhibitList", exhibitRepository.findFirst30ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
            return "index-after-proflogin";
        }

        model.addAttribute("exhibitList", exhibitRepository.findFirst30ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
        return "index";
    }


    @GetMapping("/login")
    public String login(){
        return "login";
    }


    /**검색**/
    @GetMapping("/search/exhibit")
    public String searchExhibit(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                                      Pageable pageable) {
        Page<Exhibit> exhibitPage = exhibitRepository.findByKeyword(keyword, pageable);
        model.addAttribute("exhibitPage", exhibitPage);
        model.addAttribute("keyword", keyword);
        return "search";
    }





}
