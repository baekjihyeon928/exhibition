package com.exhibition.modules.exhibit;

import com.exhibition.modules.account.CurrentAccount;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.exhibit.form.ExhibitDescriptionForm;
import com.exhibition.modules.major.Major;
import com.exhibition.modules.major.MajorForm;
import com.exhibition.modules.major.MajorRepository;
import com.exhibition.modules.account.form.TagForm;
import com.exhibition.modules.tag.TagRepository;
import com.exhibition.modules.tag.TagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/exhibit/{path}/settings")
@RequiredArgsConstructor
public class ExhibitSettingsController {

    private final ExhibitService exhibitService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final MajorRepository majorRepository;
    private final ObjectMapper objectMapper;

    /**작품설정-작품소개수정**/
    @GetMapping("/description")
    public String viewExhibitSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        model.addAttribute(modelMapper.map(exhibit, ExhibitDescriptionForm.class));
        return "exhibit/settings/description";
    }

    @PostMapping("/description")
    public String updateExhibitInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid ExhibitDescriptionForm exhibitDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(exhibit);
            return "exhibit/settings/description";
        }

        exhibitService.updateExhibitDescription(exhibit, exhibitDescriptionForm);
        attributes.addFlashAttribute("message", "작품소개를 수정했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/description";
    }

    /**작품설정-작품이미지**/
    @GetMapping("/image")
    public String studyImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        return "exhibit/settings/image";
    }

    @PostMapping("/image")
    public String exhibitImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        exhibitService.updateExhibitImage(exhibit, image);
        attributes.addFlashAttribute("message", "작품 이미지를 수정했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/image";
    }


    @PostMapping("/image/enable")
    public String enableExhibitImage(@CurrentAccount Account account, @PathVariable String path) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        exhibitService.enableExhibitImage(exhibit);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/image";
    }

    @PostMapping("/image/disable")
    public String disableExhibitImage(@CurrentAccount Account account, @PathVariable String path) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        exhibitService.disableExhibitImage(exhibit);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/image";
    }


    /**작품설정-태그**/
    @GetMapping("/tags")
    public String exhibitTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);

        model.addAttribute("tags", exhibit.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "exhibit/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path,
                                 @RequestBody TagForm tagForm) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        exhibitService.addTag(exhibit, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        exhibitService.removeTag(exhibit, tag);
        return ResponseEntity.ok().build();
    }


    //작품설정-전공
    @GetMapping("/majors")
    public String exhibitMajorsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        model.addAttribute("majors", exhibit.getMajors().stream()
                .map(Major::toString).collect(Collectors.toList()));
        List<String> allMajors = majorRepository.findAll().stream().map(Major::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allMajors));
        return "exhibit/settings/majors";
    }





    @PostMapping("/majors/add")
    @ResponseBody
    public ResponseEntity addMajor(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody MajorForm majorForm) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateMajor(account, path);
        Major major = majorRepository.findByCollageAndDepartment(majorForm.getCollageName(), majorForm.getDepartmentName());
        if (major == null) {
            return ResponseEntity.badRequest().build();
        }

        exhibitService.addMajor(exhibit, major);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/majors/remove")
    @ResponseBody
    public ResponseEntity removeMajor(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody MajorForm majorForm) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateMajor(account, path);
        Major major = majorRepository.findByCollageAndDepartment(majorForm.getCollageName(), majorForm.getDepartmentName());
        if (major == null) {
            return ResponseEntity.badRequest().build();
        }

        exhibitService.removeMajor(exhibit, major);
        return ResponseEntity.ok().build();
    }


    /**
    @GetMapping("/upload")
    public String form(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        return "exhibit/settings/upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile multipartFile, @CurrentAccount Account account, @PathVariable String path,
                         String fileName) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        //log.info("### upload");
        File targetFile = new File("/home1/irteam/" + multipartFile.getOriginalFilename());
        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);
            e.printStackTrace();
        }

        exhibitService.updateExhibitFile(exhibit, fileName);

        return "exhibit/settings/upload";
    }**/

    /**작품설정-뷰**/
    @GetMapping("/exhibit")
    public String exhibitSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(exhibit);
        return "exhibit/settings/exhibit";
    }

    /**작품설정-공개**/
    @PostMapping("/exhibit/publish")
    public String publishExhibit(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        exhibitService.publish(exhibit);
        attributes.addFlashAttribute("message", "작품을 공개했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/exhibit";
    }

    /**작품설정-종료-종료한상태에서는 오직 삭제만가능**/
    @PostMapping("/exhibit/close")
    public String closeExhibit(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        exhibitService.close(exhibit);
        attributes.addFlashAttribute("message", "작품공개를 종료했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/exhibit";
    }

    //URL경로변경
    @PostMapping("/exhibit/path")
    public String updateExhibitPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                  Model model, RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        if (!exhibitService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(exhibit);
            model.addAttribute("exhibitPathError", "해당 전시경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "exhibit/settings/exhibit";
        }

        exhibitService.updateExhibitPath(exhibit, newPath);
        attributes.addFlashAttribute("message", "전시 경로를 수정했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/exhibit";
    }

    /**작품명변경**/
    @PostMapping("/exhibit/title")
    public String updateExhibitTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        if (!exhibitService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(exhibit);
            model.addAttribute("exhibitTitleError", "작품명을 다시 입력하세요.");
            return "exhibit/settings/exhibit";
        }

        exhibitService.updateExhibitTitle(exhibit, newTitle);
        attributes.addFlashAttribute("message", "스터디 이름을 수정했습니다.");
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/settings/exhibit";
    }


    /**작품삭제**/
    @PostMapping("/exhibit/remove")
    public String removeExhibit(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        exhibitService.remove(exhibit);
        return "redirect:/";
    }



}
