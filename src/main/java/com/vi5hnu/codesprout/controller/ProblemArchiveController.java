package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.dto.CreateProblemTagDto;
import com.vi5hnu.codesprout.models.dto.ProblemInfo;
import com.vi5hnu.codesprout.models.dto.ProblemInfoWithPath;
import com.vi5hnu.codesprout.services.problemArchive.ProblemArchiveService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping(path = "info/{kathaId}")
//    ResponseEntity<Map<String,Object>> getVratKathaInfo(@PathVariable(name = "kathaId") String kathaId) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathInfo(kathaId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for id %s not found",kathaId)))));
//    }
//
    @GetMapping(path = "problem/all/info")
    ResponseEntity<Map<String,Object>> getProblemsInfo(
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize,
            @RequestParam(name = "language",required = false) ProblemLanguage problemType,
            @RequestParam(name = "difficultyLevel",required = false) ProblemDifficulty difficulty) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.getProblems(pageNo,pageSize,problemType,difficulty)));
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

    @PostMapping(path = "create/tag",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> createTag(@Valid  @RequestBody() CreateProblemTagDto tagInfo) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblemTag(tagInfo)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PostMapping(path = "add-to/tag/{tagId}/problem/{problemId}")
    ResponseEntity<Map<String,Object>> addProblemToTag(@PathVariable("tagId") String tagId,@PathVariable("problemId") String problemId) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.addProblemToTag(tagId,problemId)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PostMapping(path = "problem/bulk/new",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> createBulkProblem(@Valid  @RequestBody() List<ProblemInfoWithPath> problems) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblems(problems)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

    @PostMapping(path = "add-to/tag/{tagId}",consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Map<String,Object>> addAllProblemToTag(@PathVariable("tagId") String tagId,@RequestBody() List<String> problemIds) {
        try{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.addAllProblemsToTag(tagId,problemIds)));
        }catch (Exception e){
            return ResponseEntity.status(400).body(Map.of("success",false,"message","Something went wrong"));
        }
    }

//
//    @GetMapping(path = "problem/id/{kathaId}")
//    ResponseEntity<Map<String,Object>> getVratKathaById(@PathVariable(name = "kathaId") String kathaId) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathaById(kathaId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for id %s not found",kathaId)))));
//    }
//
//    @GetMapping(path = "problem/title/{kathaTitle}")
//    ResponseEntity<Map<String,Object>> getVratKathaByTitle(@PathVariable(name = "kathaTitle") String kathaTitle) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathaByTitle(kathaTitle).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for title %s not found",kathaTitle)))));
//    }
}
