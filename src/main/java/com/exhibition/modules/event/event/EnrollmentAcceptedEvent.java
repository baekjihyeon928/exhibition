package com.exhibition.modules.event.event;


import com.exhibition.modules.event.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent{

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "스터디 참가 신청을 확인했습니다. 스터디에 참석하세요.");
    }

}
