package com.exhibition.modules.exhibit;

import com.exhibition.modules.account.UserAccount;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.professor.Professor;
import com.exhibition.modules.student.Student;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Exhibit {

    @Id
    @GeneratedValue
    private Long id; //인덱스

    @ManyToMany //제작자가 여러명일수도 있다
    private Set<Account> owners = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @ManyToMany //작품 평가 교수님
    private Set<Account> professors = new HashSet<>();

    private String name; //작품제작자

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription; //제목아래 짧은 소개

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription; //내용


    @Lob @Basic(fetch = FetchType.EAGER)
    private String image; //대표이미지

    @ManyToMany
    private Set<Tag> tags = new HashSet<>(); //관심태크

    @ManyToMany
    private Set<Major> majors = new HashSet<>();

    /**@ManyToMany
    private Set<Professor> professors = new HashSet<>();

    @ManyToMany //평가 학생 여러명
    private Set<Student> students = new HashSet<>(); **/

    private LocalDateTime publishedDateTime = LocalDateTime.now(); //공개한시간

    private LocalDateTime closedDateTime; //종료한시간

    private LocalDateTime recruitingUpdatedDateTime; //모집시간

    private boolean published = true; //공개유무

    private boolean recruiting; //인원모집유무

    private boolean closed; //종료유무

    private boolean useImage; //대표이미지 사용유무

    public void addOwner(Account account) {
        this.owners.add(account);
    }//제작자추가

    public void addProfessor(Account account) {
        this.professors.add(account);
    }//제작자추가

    public String getImage() {
        return image != null ? image : "/images/default_image.jpg";
    }

    public boolean isOwner(UserAccount userAccount) {
        return this.owners.contains(userAccount.getAccount());
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("작품을 공개할 수 없는 상태입니다. 수정된 모습을 기다려주세요:)");
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("작품 공개를 종료할 수 없습니다. 작품을 공개하지 않았거나 이미 종료된 작품품입니다");
        }
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public boolean isOwnedBy(Account account) {
        return this.getOwners().contains(account);
    }

    public boolean isRemovable() {
        return !this.published;
    }

    /**
    public void addPath() {
        this.path = id.toString();
    }**/

    private boolean passed; //통과1

    public boolean noPassed; //불통과

    public boolean add; //보완

    public void pass() {
        if(!this.passed)
        this.passed=true;
    }

    public void noPass() {
        if(!this.noPassed)
            this.noPassed=true;
    }

    public void noPassToPass() {
        this.noPassed=false;
        this.passed=true;
    }

    public void add() {
        if(!this.add)
            this.add=true;
    }

    public void addToPass() {
        this.passed=true;
        this.add=false;
    }

    public void addToNoPass() {
        this.noPassed=true;
        this.add=false;
    }

    private boolean passedTwo; //통과2

    public boolean noPassedTwo; //불통과

    public boolean addTwo; //보완

    public void passTwo() {
        if(!this.passedTwo)
            this.passedTwo=true;
    }

    public void noPassTwo() {
        if(!this.noPassedTwo)
            this.noPassedTwo=true;
    }

    public void noPassTwoToPass() {
        this.noPassedTwo=false;
        this.passedTwo=true;
    }

    public void addTwo() {
        if(!this.addTwo)
            this.addTwo=true;
    }

    public void addTwoToPass() {
        this.passedTwo=true;
        this.addTwo=false;
    }

    public void addTwoToNoPass() {
        this.noPassedTwo=true;
        this.addTwo=false;
    }

    private boolean passedThree; //통과3

    public boolean noPassedThree; //불통과

    public boolean addThree; //보완

    public void passThree() {
        if(!this.passedThree)
            this.passedThree=true;
    }

    public void noPassThree() {
        if(!this.noPassedThree)
            this.noPassedThree=true;
    }

    public void noPassThreeToPass() {
        this.noPassedThree=false;
        this.passedThree=true;
    }

    public void addThree() {
        if(!this.addThree)
            this.addThree=true;
    }

    public void addThreeToPass() {
        this.passedThree=true;
        this.addThree=false;
    }

    public void addThreeToNoPass() {
        this.noPassedThree=true;
        this.addThree=false;
    }

    private int memberCount;

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.owners.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isOwners(UserAccount userAccount) {
        return this.owners.contains(userAccount.getAccount());
    }

    public void addMemember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published && this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }
}
