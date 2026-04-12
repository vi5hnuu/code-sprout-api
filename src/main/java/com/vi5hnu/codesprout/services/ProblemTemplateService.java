package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.ProblemTemplate;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemTemplateDto;
import com.vi5hnu.codesprout.repository.ProblemTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemTemplateService {

    private final ProblemTemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public List<ProblemTemplateDto> getTemplates(String problemId) {
        return templateRepository.findByProblemId(problemId)
                .stream().map(ProblemTemplateDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProblemTemplateDto> getTemplate(String problemId, ProblemLanguage language) {
        return templateRepository.findByProblemIdAndLanguage(problemId, language)
                .map(ProblemTemplateDto::from);
    }

    @Transactional
    public ProblemTemplateDto upsert(String problemId, ProblemLanguage language, String templateCode) {
        ProblemTemplate template = templateRepository.findByProblemIdAndLanguage(problemId, language)
                .orElseGet(() -> ProblemTemplate.builder()
                        .problemId(problemId)
                        .language(language)
                        .build());
        template.setTemplateCode(templateCode);
        return ProblemTemplateDto.from(templateRepository.save(template));
    }

    @Transactional
    public void delete(String problemId, ProblemLanguage language) {
        templateRepository.findByProblemIdAndLanguage(problemId, language)
                .ifPresent(templateRepository::delete);
    }
}
