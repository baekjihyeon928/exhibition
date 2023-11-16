package com.exhibition.modules.event.event;


import com.exhibition.modules.event.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "스터디 참가 신청을 거절했습니다.");
    }
}
