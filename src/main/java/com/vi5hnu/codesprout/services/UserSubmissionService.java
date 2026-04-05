package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.UserSubmission;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
import com.vi5hnu.codesprout.models.RecordSubmissionRequest;
import com.vi5hnu.codesprout.models.UserSubmissionDto;
import com.vi5hnu.codesprout.repository.UserSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSubmissionService {

    private final UserSubmissionRepository submissionRepository;

    @Transactional
    public UserSubmissionDto record(String userId, RecordSubmissionRequest req) {
        UserSubmission submission = UserSubmission.builder()
                .userId(userId)
                .problemId(req.getProblemId())
                .language(req.getLanguage())
                .status(req.getStatus())
                .isOfficial(req.isOfficial())
                .build();
        return UserSubmissionDto.from(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public Pageable<UserSubmissionDto> getMySubmissions(String userId, int pageNo, int pageSize) {
        var page = submissionRepository.findByUserIdOrderBySubmittedAtDesc(
                userId, PageRequest.of(pageNo - 1, pageSize));
        return new Pageable<>(
                page.getContent().stream().map(UserSubmissionDto::from).toList(),
                pageNo,
                page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Pageable<UserSubmissionDto> getMySubmissionsForProblem(String userId, String problemId, int pageNo, int pageSize) {
        var page = submissionRepository.findByUserIdAndProblemIdOrderBySubmittedAtDesc(
                userId, problemId, PageRequest.of(pageNo - 1, pageSize));
        return new Pageable<>(
                page.getContent().stream().map(UserSubmissionDto::from).toList(),
                pageNo,
                page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<String> getAcceptedProblemIds(String userId) {
        return submissionRepository.findAcceptedProblemIds(userId, SubmissionStatus.ACCEPTED);
    }

    @Transactional(readOnly = true)
    public boolean hasSolved(String userId, String problemId) {
        return submissionRepository.existsByUserIdAndProblemIdAndStatus(userId, problemId, SubmissionStatus.ACCEPTED);
    }
}
