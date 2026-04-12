package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.ProblemHint;
import com.vi5hnu.codesprout.models.ProblemHintDto;
import com.vi5hnu.codesprout.repository.ProblemHintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemHintService {

    private final ProblemHintRepository hintRepository;

    @Transactional(readOnly = true)
    public List<ProblemHintDto> getHints(String problemId) {
        return hintRepository.findByProblemIdOrderByHintOrderAsc(problemId)
                .stream().map(ProblemHintDto::from).toList();
    }

    @Transactional
    public ProblemHintDto addHint(String problemId, String content) {
        long nextOrder = hintRepository.findByProblemIdOrderByHintOrderAsc(problemId).size();
        ProblemHint hint = ProblemHint.builder()
                .problemId(problemId)
                .hintOrder((int) nextOrder)
                .content(content)
                .build();
        return ProblemHintDto.from(hintRepository.save(hint));
    }

    @Transactional
    public ProblemHintDto updateHint(String hintId, String content) {
        ProblemHint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new RuntimeException("Hint not found"));
        hint.setContent(content);
        return ProblemHintDto.from(hintRepository.save(hint));
    }

    @Transactional
    public void deleteHint(String hintId) {
        hintRepository.deleteById(hintId);
    }
}
