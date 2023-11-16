package com.exhibition.modules.major;

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
public class MajorService {

    private final MajorRepository majorRepository;

    @PostConstruct
    private void initMajorData() throws IOException{
        if(majorRepository.count() == 0){
            Resource resource = new ClassPathResource("sku_major.csv");
            List<Major> majorList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Major.builder().collage(split[0]).department(split[1]).build();
                    }).collect(Collectors.toList());
            majorRepository.saveAll(majorList);
        }
    }
}
