package com.exhibition.modules.exhibit;

import com.exhibition.modules.account.Account;
import com.exhibition.modules.exhibit.event.ExhibitEvaluatedEvent;
import com.exhibition.modules.exhibit.event.ExhibitUpdateEvent;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.exhibit.event.ExhibitCreatedEvent;
import com.exhibition.modules.exhibit.form.ExhibitDescriptionForm;
import com.exhibition.modules.major.Major;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.exhibition.modules.exhibit.form.ExhibitForm.VALID_PATH_PATTERN;


@Service
@Transactional
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository repository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**새로운 작품생성**/
    public Exhibit createNewExhibit(Exhibit exhibit, Account account) {
        Exhibit newExhibit = repository.save(exhibit);
        newExhibit.addOwner(account);
        newExhibit.setName(account.getName());
        //newExhibit.addPath();
        newExhibit.setMajors(account.getMajors());
        eventPublisher.publishEvent(new ExhibitCreatedEvent(newExhibit)); //작품전시 알림
        return newExhibit;
    }

    /**작품수정**/
    public Exhibit getExhibitToUpdate(Account account, String path) {
        Exhibit exhibit = this.getExhibit(path);
        checkIfOwner(account, exhibit); //작품을 수정할 수 있는건 소유자뿐이기때문에 확인
        return exhibit;
    }

    /**작품 가져오기**/
    public Exhibit getExhibit(String path) {
        Exhibit exhibit = this.repository.findByPath(path);
        checkIfExistingExhibit(path, exhibit); //작품이 존재하는지
        return exhibit;
    }


    public void updateExhibitDescription(Exhibit exhibit, ExhibitDescriptionForm exhibitDescriptionForm) {
        modelMapper.map(exhibitDescriptionForm, exhibit);
        //eventPublisher.publishEvent(new ExhibitUpdateEvent(exhibit, "작품 소개를 수정했습니다."));
    }



    public void updateExhibitImage(Exhibit exhibit, String image) {
        exhibit.setImage(image);
    }

    public void enableExhibitImage(Exhibit exhibit) {
        exhibit.setUseImage(true);
    }

    public void disableExhibitImage(Exhibit exhibit) {
        exhibit.setUseImage(false);
    }

    public void addTag(Exhibit exhibit, Tag tag) {
        exhibit.getTags().add(tag);
    }

    public void removeTag(Exhibit exhibit, Tag tag) {
        exhibit.getTags().remove(tag);
    }

    public void addMajor(Exhibit exhibit, Major major) {
        exhibit.getMajors().add(major);
    }

    public void removeMajor(Exhibit exhibit, Major major) {
        exhibit.getMajors().remove(major);
    }

    public Exhibit getExhibitToUpdateTag(Account account, String path) {
        Exhibit exhibit = repository.findExhibitWithTagsByPath(path);
        checkIfExistingExhibit(path, exhibit);
        checkIfOwner(account, exhibit);
        return exhibit;
    }


    public Exhibit getExhibitToUpdateMajor(Account account, String path) {
        Exhibit exhibit = repository.findExhibitWithMajorsByPath(path);
        checkIfExistingExhibit(path, exhibit);
        checkIfOwner(account, exhibit);
        return exhibit;
    }

    public Exhibit getExhibitToUpdateStatus(Account account, String path) {
        Exhibit exhibit = repository.findExhibitWithOwnersByPath(path);
        checkIfExistingExhibit(path, exhibit);
        checkIfOwner(account, exhibit);
        return exhibit;
    }

    public Exhibit getExhibitToUpdatePass(Account account, String path) {
        Exhibit exhibit = repository.findExhibitOnlyByPath(path);
        checkIfExistingExhibit(path, exhibit);
        return exhibit;
    }

    private void checkIfOwner(Account account, Exhibit exhibit) {
        if (!exhibit.isOwnedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }


    private void checkIfExistingExhibit(String path, Exhibit exhibit) {
        if (exhibit == null) {
            throw new IllegalArgumentException(path + "에 해당하는 작품이 없습니다.");
        }
    }

    public void publish(Exhibit exhibit) {
        exhibit.publish();
        //this.eventPublisher.publishEvent(new ExhibitCreatedEvent(exhibit)); //작품전시 알림
    }

    public void close(Exhibit exhibit) {
        exhibit.close();
        //eventPublisher.publishEvent(new ExhibitUpdateEvent(exhibit, "작품전시를 종료했습니다."));
    }


    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !repository.existsByPath(newPath);
    }

    public void updateExhibitPath(Exhibit exhibit, String newPath) {
        exhibit.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateExhibitTitle(Exhibit exhibit, String newTitle) {
        exhibit.setTitle(newTitle);
    }

    public void remove(Exhibit exhibit) {
        if (exhibit.isRemovable()) {
            repository.delete(exhibit);
        } else {
            throw new IllegalArgumentException("작품 삭제할 수 없습니다.");
        }
    }

    public Exhibit getExhibitToEnroll(String path) {
        Exhibit exhibit = repository.findExhibitOnlyByPath(path);
        checkIfExistingExhibit(path, exhibit);
        return exhibit;
    }


    public void pass(Exhibit exhibit) {
        exhibit.pass();
    }

    public void noPass(Exhibit exhibit) {
        exhibit.noPass();
    }

    public void add(Exhibit exhibit) { exhibit.add(); }

    public void addToPass(Exhibit exhibit) {
        exhibit.addToPass();
    }

    public void addToNoPass(Exhibit exhibit) {
        exhibit.addToNoPass();
    }

    public void noPassToPass(Exhibit exhibit) {
        exhibit.noPassToPass();
    }

    public void passTwo(Exhibit exhibit) {
        exhibit.passTwo();
    }

    public void noPassTwo(Exhibit exhibit) {
        exhibit.noPassTwo();
    }

    public void addTwo(Exhibit exhibit) {
        exhibit.addTwo();
    }

    public void addTwoToPass(Exhibit exhibit) {
        exhibit.addTwoToPass();
    }

    public void addTwoToNoPass(Exhibit exhibit) {
        exhibit.addTwoToNoPass();
    }

    public void noPassTwoToPass(Exhibit exhibit) {
        exhibit.noPassTwoToPass();
    }

    public void passThree(Exhibit exhibit) {
        exhibit.passThree();
        this.eventPublisher.publishEvent(new ExhibitEvaluatedEvent(exhibit, "작품평가가 완료되었습니다."));
    }

    public void noPassThree(Exhibit exhibit) {
        exhibit.noPassThree();
        this.eventPublisher.publishEvent(new ExhibitEvaluatedEvent(exhibit, "작품평가가 완료되었습니다."));
    }

    public void addThree(Exhibit exhibit) {
        exhibit.addThree();
    }

    public void addThreeToPass(Exhibit exhibit) {
        exhibit.addThreeToPass();
    }

    public void addThreeToNoPass(Exhibit exhibit) {
        exhibit.addThreeToNoPass();
    }

    public void noPassThreeToPass(Exhibit exhibit) {
        exhibit.noPassThreeToPass();
    }

    public void startRecruit(Exhibit exhibit) {
        exhibit.startRecruit();
        eventPublisher.publishEvent(new ExhibitUpdateEvent(exhibit, "스터디원 모집을 시작합니다."));
    }

    public void stopRecruit(Exhibit exhibit) {
        exhibit.stopRecruit();
        eventPublisher.publishEvent(new ExhibitUpdateEvent(exhibit, "스터디원 모집을 중단했습니다."));
    }

    public void addMember(Exhibit exhibit, Account account) {
        exhibit.addMemember(account);
    }

    public void removeMember(Exhibit exhibit, Account account) {
        exhibit.removeMember(account);
    }

    /**public void evaluateEvent(Exhibit exhibit) {
        if((exhibit.isPassed()|| exhibit.isNoPassed()) && (exhibit.isPassedTwo() || exhibit.isNoPassedTwo()) && (exhibit.isPassedThree() || exhibit.isNoPassedThree())){
        //this.eventPublisher.publishEvent(new ExhibitEvaluatedEvent(exhibit, "작품평가가 완료되었습니다."));
        }
    }**/

    /**
    public void updateExhibitFile(Exhibit exhibit, String fileName) {
        exhibit.setFileName(fileName);
    }**/
}
