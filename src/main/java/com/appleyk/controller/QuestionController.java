package com.appleyk.controller;

import com.appleyk.node.company;
import com.appleyk.repository.CompanyRepository;
import com.appleyk.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appleyk.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/rest/appleyk/question")
public class QuestionController {
	
	@Autowired
	QuestionService questService;

	@Autowired
	QuestionRepository questionRepository;

	@Autowired
	CompanyRepository companyRepository;

	@RequestMapping("/query")
	public String query(@RequestParam(value = "question") String question) throws Exception {
		return questService.answer(question);
	}
	@RequestMapping("/node")
	public String node() throws Exception {
		String ret = '[' +
				"\"1\": {\"name\": \"数据结构\",\"type\": \"学科\"},\n" +
				"\"2\": { \"name\": \"二叉树\", \"type\": \"知识点\"}\n" +
				']';
		return ret;
	}
	@RequestMapping("/edge")
	public String edge() throws Exception {
		String ret = "[\n" +
				"{ \"source\": 1, \"target\": 2, \"rela\": \"包含\", \"type\": \"包含关系\" }\n" +
				']';
		return ret;
	}

	@RequestMapping("/query2")
	public Object query2(@RequestParam(value = "question") String question) throws Exception {
		return questService.answer2(question);
	}
	@RequestMapping("/get")
	public List<company> getCompany(@RequestParam(value="name") String name){
		return companyRepository.getCompanyInfo(name);
	}
	@RequestMapping("/path")
	public void checkPath(){
		questService.showDictPath();
	}
}
