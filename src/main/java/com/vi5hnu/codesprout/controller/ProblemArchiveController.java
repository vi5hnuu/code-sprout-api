package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.dto.CreateProblem;
import com.vi5hnu.codesprout.services.problemArchive.ProblemArchiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/problem")
@RequiredArgsConstructor
public class ProblemArchiveController {
    private final ProblemArchiveService problemArchiveService;

//    @GetMapping(path = "info/{kathaId}")
//    ResponseEntity<Map<String,Object>> getVratKathaInfo(@PathVariable(name = "kathaId") String kathaId) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathInfo(kathaId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for id %s not found",kathaId)))));
//    }
//
    @GetMapping(path = "all/info")
    ResponseEntity<Map<String,Object>> getProblemsInfo(@RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo, @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.getProblems(pageNo,pageSize)));
    }

    @PostMapping(path = "new")
    ResponseEntity<Map<String,Object>> createProblem(@Valid  @RequestBody(required = true) CreateProblem problem) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.problemArchiveService.createProblem(problem)));
    }
//
//    @GetMapping(path = "id/{kathaId}")
//    ResponseEntity<Map<String,Object>> getVratKathaById(@PathVariable(name = "kathaId") String kathaId) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathaById(kathaId).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for id %s not found",kathaId)))));
//    }
//
//    @GetMapping(path = "title/{kathaTitle}")
//    ResponseEntity<Map<String,Object>> getVratKathaByTitle(@PathVariable(name = "kathaTitle") String kathaTitle) throws ApiException {
//        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.vratKathaService.getVratKathaByTitle(kathaTitle).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,String.format("katha for title %s not found",kathaTitle)))));
//    }
}
