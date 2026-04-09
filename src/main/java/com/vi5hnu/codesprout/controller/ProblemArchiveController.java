package com.vi5hnu.codesprout.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.CreateProblemTagDto;
import com.vi5hnu.codesprout.models.ProblemInfo;
import com.vi5hnu.codesprout.models.ProblemInfoWithPath;
import com.vi5hnu.codesprout.models.UpdateProblemDto;
import com.vi5hnu.codesprout.models.UpdateTagDto;
import com.vi5hnu.codesprout.services.problemArchive.ProblemArchiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1")
@RequiredArgsConstructor
public class ProblemArchiveController {
    private final ProblemArchiveService problemArchiveService;

    @GetMapping(path = "problem/all/info")
    ResponseEntity<Map<String,Object>> getProblemsInfo(
            @RequestParam(name = "pageNo",  required = false, defaultValue = "1")  int pageNo,
            @RequestParam(name = "pageSize",required = false, defaultValue = "20") int pageSize,
            @RequestParam(name = "language",   required = false) ProblemLanguage language,
            @RequestParam(name = "difficulty", required = false) ProblemDifficulty difficulty,
            @RequestParam(name = "search",     required = false) String search) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                    "data", this.problemArchiveService.getProblems(pageNo, pageSize, language, difficulty, search)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage() != null ? e.getMessage() : "Failed to load problems"));
        }
    }

    @GetMapping(path = "problem/{slug}/file-url")
    ResponseEntity<Map<String,Object>> getProblemFileUrl(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                    "url", this.problemArchiveService.getProblemPresignedUrl(slug)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping(path = "problem/{slug}")
    ResponseEntity<Map<String,Object>> getProblemBySlug(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                    "data", this.problemArchiveService.getProblemBySlug(slug)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to load problem"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping(path = "tags/info")
    ResponseEntity<Map<String,Object>> getProblemTags(
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.getProblemTags(pageNo,pageSize)));
    }

    @GetMapping(path = "tag/{tagId}/problems")
    ResponseEntity<Map<String,Object>> getTagProblems(
            @PathVariable(name = "tagId",required = true) String tagId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.getTagProblems(tagId,pageNo,pageSize)));
    }

    @GetMapping(path = "tag/problems")
    ResponseEntity<Map<String,Object>> getTagsProblems(
            @RequestParam(name = "tagId", required = true) List<String> tags,
            @RequestParam(name = "pageNo",  required = false, defaultValue = "1")  int pageNo,
            @RequestParam(name = "pageSize",required = false, defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", this.problemArchiveService.getTagsProblems(tags, pageNo, pageSize)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "problem/new",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<Map<String,Object>> createProblem(@Valid  @RequestPart("problemInfo") ProblemInfo problem,@RequestPart(value = "file",required = false) MultipartFile file,@RequestPart(value = "file_path",required = false) String filePath) {
        try{
            if((file==null && filePath==null) || (file!=null && filePath!=null)) throw new Exception("either file or file-path is required");
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblem(problem,file,filePath)));
        }catch (IOException e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Failed to upload file"));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "admin/problem/{id}")
    ResponseEntity<Map<String,Object>> getProblemById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", this.problemArchiveService.getProblemById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to load problem"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path = "admin/problem/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> updateProblem(@PathVariable("id") String id, @RequestBody UpdateProblemDto dto) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", this.problemArchiveService.updateProblem(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "admin/problem/{id}")
    ResponseEntity<Map<String,Object>> deleteProblem(@PathVariable("id") String id) {
        try {
            this.problemArchiveService.deleteProblem(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "create/tag",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> createTag(@Valid  @RequestBody() CreateProblemTagDto tagInfo) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblemTag(tagInfo)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(path = "admin/tag/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> updateTag(@PathVariable("id") String id, @RequestBody UpdateTagDto dto) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", this.problemArchiveService.updateTag(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "admin/tag/{id}")
    ResponseEntity<Map<String,Object>> deleteTag(@PathVariable("id") String id) {
        try {
            this.problemArchiveService.deleteTag(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "admin/tag/{tagId}/problem/{problemId}")
    ResponseEntity<Map<String,Object>> removeProblemFromTag(@PathVariable("tagId") String tagId, @PathVariable("problemId") String problemId) {
        this.problemArchiveService.removeProblemFromTag(tagId, problemId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "add-to/tag/{tagId}/problem/{problemId}")
    ResponseEntity<Map<String,Object>> addProblemToTag(@PathVariable("tagId") String tagId,@PathVariable("problemId") String problemId) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.addProblemToTag(tagId,problemId)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "problem/bulk/new",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> createBulkProblem(@Valid  @RequestBody() List<ProblemInfoWithPath> problems) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblems(problems)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "add-to/tag/{tagId}",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> addAllProblemToTag(@PathVariable("tagId") String tagId,@RequestBody() List<String> problemIds) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.addAllProblemsToTag(tagId,problemIds)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }
}
