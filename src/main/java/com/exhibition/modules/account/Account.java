package com.exhibition.modules.account;

import com.exhibition.modules.professor.Professor;
import com.exhibition.modules.student.Student;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "idx")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long idx; //인덱스

    @Column(unique = true) //아이디 유일
    private String id;

    @Column(unique = true) //이메일 유일
    private String email;

    private String password; //비밀번호

    private String name; //이름


    /**인증**/
    private boolean emailVerified; //이메일 인증절차 위한 값

    private String emailCheckToken; //이메일을 검증할 때 사용할 토큰 값

    private LocalDateTime joinedAt; //인증을 한 사용자가 가입된 시간


    /**프로필**/
    private String bio; //자기소개

    private String url; //웹사이트 url

    private String major; //전공

    private String interest; //관심사

    private String artName; //작품명

    private String artBio; //작품소개

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage; //프로필사진, 위의 타입들은 varchar(255)인데 프로필 사진은 더 커질 수 있기 때문에 Lob을 사용


    /**알림설정 관련**/
    private boolean exhibitCreatedByEmail; //작품이 등록 된 것을 이메일로 받을 것인가?

    private boolean exhibitCreatedByWeb = true; //작품이 등록 된 것을 웹으로 받을 것인가?

    private boolean exhibitPassedByEmail;

    private boolean exhibitPassedByWeb = true;

    public boolean isExhibitUpdatedByEmail;

    public boolean isExhibitUpdatedByWeb = true;

    public boolean isExhibitEnrollmentResultByEmail;

    public boolean isExhibitEnrollmentResultByWeb = true;

    /**관심 목록 태그 관련**/
    @ManyToMany
    private Set<Tag> tags = new HashSet<>(); //관심목록 태크

    /**학과 목록 태그 관련**/
    @ManyToMany
    private Set<Major> majors = new HashSet<>();

    /**평가 교수님 여러명**/
    @ManyToMany
    private Set<Professor> professors = new HashSet<>(); //교수님

    /**평가 학생 여러명**/
    @ManyToMany
    private Set<Student> students = new HashSet<>(); //학생

    /**이메일 토큰**/
    private LocalDateTime emailCheckTokenGeneratedAt; //이메일 토큰 생성시간

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true; //이메일 확인 완료
        this.joinedAt = LocalDateTime.now(); //가입시간 현재시간으로
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1)); //이메일 재전송 한시간에 한번씩
    }

    //그냥 회원가입하면 다 학생
    public boolean isProfessor = false;



}
