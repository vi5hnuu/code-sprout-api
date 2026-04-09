package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.FileView;
import com.vi5hnu.codesprout.repository.FileViewRepository;
import com.vi5hnu.codesprout.security.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileViewService {

    private final FileViewRepository fileViewRepository;

    /**
     * Records a view asynchronously so it never blocks the response.
     *
     * @param fileId   the viewed file
     * @param viewerId authenticated user ID, or null for anonymous
     */
    @Async
    public void recordView(String fileId, String viewerId) {
        FileView view = FileView.builder()
                .fileId(fileId)
                .viewerId(viewerId)
                .ipAddress(RequestContext.getIpAddress())
                .build();
        fileViewRepository.save(view);
    }

    public long getTotalViews(String fileId) {
        return fileViewRepository.countByFileId(fileId);
    }

    public long getUniqueViews(String fileId) {
        return fileViewRepository.countUniqueViewersByFileId(fileId);
    }
}
