package com.exhibition.modules.major;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Major findByCollageAndDepartment(String collage, String department);
}
