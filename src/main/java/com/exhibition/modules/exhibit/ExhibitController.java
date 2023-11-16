package com.exhibition.modules.exhibit;

import com.exhibition.modules.account.CurrentAccount;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.exhibit.form.ExhibitForm;
import com.exhibition.modules.exhibit.validator.ExhibitFormValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ExhibitController {

    private final ExhibitService exhibitService;
    private final ModelMapper modelMapper;
    private final ExhibitFormValidator exhibitFormValidator;
    private final ExhibitRepository exhibitRepository;

   //path값 중복확인
    @InitBinder("exhibitForm")
    public void exhibitFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(exhibitFormValidator);
    }

    /**작품등록**/
    @GetMapping("/new-exhibit")
    public String newExhibitForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ExhibitForm());
        return "exhibit/form";
    }

    @PostMapping("/new-exhibit")
    public String newExhibitSubmit(@CurrentAccount Account account, @Valid ExhibitForm exhibitForm, Errors errors, Model model){
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "exhibit/form";
        }
        Exhibit newExhibit = exhibitService.createNewExhibit(modelMapper.map(exhibitForm, Exhibit.class), account);
        return "redirect:/exhibit/" + URLEncoder.encode(newExhibit.getPath(), StandardCharsets.UTF_8);
    }

    /**작품조회**/
    @GetMapping("/exhibit/{path}")
    public String viewExhibit(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibit(path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        return "exhibit/view";
    }


    @GetMapping("exhibition/{path}")
    public String detailExhibit(@PathVariable String path, @CurrentAccount Account account, Model model) {
        Exhibit exhibit = exhibitService.getExhibit(path);
        model.addAttribute("exhibit",exhibit);
        model.addAttribute("account",account);
        return "exhibition";
    }

    //작품패스
    @PostMapping("/exhibition/{path}/pass")
    public String passExhibit(@CurrentAccount Account account, @PathVariable String path,
                              RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.pass(exhibit);
        attributes.addFlashAttribute("message", "작품을 통과시켰습니다.");
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스**/
    @PostMapping("/exhibition/{path}/nopass")
    public String noPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPass(exhibit);
        attributes.addFlashAttribute("message", "작품을 탈락시켰습니다.");
        //return "redirect:/evaluation/" + exhibit.getEncodedPath();
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스수정**/
    @PostMapping("/exhibition/{path}/nopassToPass")
    public String noPassToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPassToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완**/
    @PostMapping("/exhibition/{path}/add")
    public String addExhibit(@CurrentAccount Account account, @PathVariable String path,
                                RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.add(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addToPass")
    public String addToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addToNoPass")
    public String addToNoPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                        RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addToNoPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품패스2**/
    @PostMapping("/exhibition/{path}/passTwo")
    public String passTwoExhibit(@CurrentAccount Account account, @PathVariable String path,
                                 RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.passTwo(exhibit);
        attributes.addFlashAttribute("message", "작품을 통과시켰습니다.");
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스2**/
    @PostMapping("/exhibition/{path}/nopassTwo")
    public String noPassTwoExhibit(@CurrentAccount Account account, @PathVariable String path,
                                   RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPassTwo(exhibit);
        attributes.addFlashAttribute("message", "작품을 탈락시켰습니다.");
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완**/
    @PostMapping("/exhibition/{path}/addTwo")
    public String addTwoExhibit(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addTwo(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addTwoToPass")
    public String addTwoToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                   RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addTwoToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addTwoToNoPass")
    public String addTwoToNoPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                          RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addTwoToNoPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스2수정**/
    @PostMapping("/exhibition/{path}/nopassTwoToPass")
    public String noPassTwoToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPassTwoToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품패스3**/
    @PostMapping("/exhibition/{path}/passThree")
    public String passThreeExhibit(@CurrentAccount Account account, @PathVariable String path,
                                   RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.passThree(exhibit);
        attributes.addFlashAttribute("message", "작품을 통과시켰습니다.");
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스3**/
    @PostMapping("/exhibition/{path}/nopassThree")
    public String noPassThreeExhibit(@CurrentAccount Account account, @PathVariable String path,
                                     RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPassThree(exhibit);
        attributes.addFlashAttribute("message", "작품을 탈락시켰습니다.");
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완**/
    @PostMapping("/exhibition/{path}/addThree")
    public String addThreeExhibit(@CurrentAccount Account account, @PathVariable String path,
                                RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addThree(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addThreeToPass")
    public String addThreeToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                      RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addThreeToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품보완수정**/
    @PostMapping("/exhibition/{path}/addThreeToNoPass")
    public String addThreeToNoPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                        RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.addThreeToNoPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }

    /**작품논패스3수정**/
    @PostMapping("/exhibition/{path}/nopassThreeToPass")
    public String noPassThreeToPassExhibit(@CurrentAccount Account account, @PathVariable String path,
                                         RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdatePass(account, path);
        exhibitService.noPassThreeToPass(exhibit);
        return "redirect:/exhibition/" + exhibit.getEncodedPath();
    }


    /**
    @GetMapping("/exhibit/{path}/members")
    public String viewStudyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibit(path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        return "exhibit/members";
    }**/

    @GetMapping("/exhibit/{path}/join")
    public String joinExhibit(@CurrentAccount Account account, @PathVariable String path) {
        Exhibit exhibit = exhibitRepository.findExhibitWithMembersByPath(path);
        exhibitService.addMember(exhibit, account);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/members";
    }

    @GetMapping("/exhibit/{path}/leave")
    public String leaveExhibit(@CurrentAccount Account account, @PathVariable String path) {
        Exhibit exhibit = exhibitRepository.findExhibitWithMembersByPath(path);
        exhibitService.removeMember(exhibit, account);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/members";
    }
}
