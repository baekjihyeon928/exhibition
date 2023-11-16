package com.exhibition.modules.professor;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfessorService{

    private final ProfessorRepository professorRepository;

    @PostConstruct
    private void initProfessorData() throws IOException {
        if(professorRepository.count() == 0){
            Resource resource = new ClassPathResource("professor.csv");
            List<Professor> professorList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Professor.builder().name(split[0]).build();
                    }).collect(Collectors.toList());
            professorRepository.saveAll(professorList);
        }
    }

}
