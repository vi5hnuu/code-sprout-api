package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.UserNote;
import com.vi5hnu.codesprout.models.UserNoteDto;
import com.vi5hnu.codesprout.repository.UserNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final UserNoteRepository noteRepository;

    @Transactional(readOnly = true)
    public Optional<UserNoteDto> getNote(String userId, String problemId) {
        return noteRepository.findByUserIdAndProblemId(userId, problemId)
                .map(UserNoteDto::from);
    }

    @Transactional
    public UserNoteDto upsert(String userId, String problemId, String content) {
        UserNote note = noteRepository.findByUserIdAndProblemId(userId, problemId)
                .orElseGet(() -> UserNote.builder()
                        .userId(userId)
                        .problemId(problemId)
                        .build());
        note.setContent(content);
        return UserNoteDto.from(noteRepository.save(note));
    }

    @Transactional
    public void delete(String userId, String problemId) {
        noteRepository.deleteByUserIdAndProblemId(userId, problemId);
    }
}
