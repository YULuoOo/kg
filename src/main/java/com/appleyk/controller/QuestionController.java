package com.appleyk.controller;

import com.appleyk.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appleyk.service.QuestionService;

@RestController
@RequestMapping("/rest/appleyk/question")
public class QuestionController {
	
	@Autowired
	QuestionService questService;

	@Autowired
	QuestionRepository questionRepository;

	@RequestMapping("/query")
	public String query(@RequestParam(value = "question") String question) throws Exception {
		return questService.answer(question);
	}

	@RequestMapping("/query2")
	public String query2(@RequestParam(value = "question") String question) throws Exception {
		return questService.answer2(question);
	}
	
	@RequestMapping("/path")
	public void checkPath(){
		questService.showDictPath();
	}
}
