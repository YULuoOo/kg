package com.appleyk.controller;

import com.alibaba.fastjson.JSONObject;
import com.appleyk.node.company;
import com.appleyk.repository.CompanyRepository;
import com.appleyk.repository.QuestionRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json4s.jackson.Json;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appleyk.service.QuestionService;

import java.util.ArrayList;
import java.util.List;

import static org.apache.arrow.flatbuf.Type.Map;

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
	public String nodes() throws Exception {
		String ret = "{\"1\": {\"name\": \"祝九胜\",\"type\": \"1\"},\"2\": {\"name\": \"吴嘉宁\",\"type\": \"1\"},\"3\": {\"name\": \"郁亮\",\"type\": \"1\"},\"4\": {\"name\": \"朱旭\",\"type\": \"1\"},\"5\": {\"name\": \"李强\",\"type\": \"1\"},\"6\": {\"name\": \"郑英\",\"type\": \"1\"},\"7\": {\"name\": \"王文金\",\"type\": \"1\"},\"8\": {\"name\": \"刘姝威\",\"type\": \"1\"},\"9\": {\"name\": \"解冻\",\"type\": \"1\"},\"10\": {\"name\": \"陈贤军\",\"type\": \"1\"},\"11\": {\"name\": \"张旭\",\"type\": \"1\"},\"12\": {\"name\": \"康典\",\"type\": \"1\"},\"13\": {\"name\": \"孙盛典\",\"type\": \"1\"},\"14\": {\"name\": \"周清平\",\"type\": \"1\"},\"15\": {\"name\": \"林茂德\",\"type\": \"1\"},\"16\": {\"name\": \"万科企业股份有限公司\",\"type\": \"1\"}}";
		System.out.println(ret);
		return ret;
	}
	@RequestMapping("/edge")
	public String edges() throws Exception {
		String ret = "[{ \"source\":1, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":2, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":3, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":4, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":5, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":6, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":7, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":8, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":9, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":10, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":11, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":12, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":13, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":14, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"},{ \"source\":15, \"target\": 16, \"rela\": \"work_in\",\"type\": \"1\"}]";
		System.out.println(ret);
		return ret;
	}

	@RequestMapping("/query2")
	public Object query2(@RequestParam(value = "question") String question) throws Exception {
		List<String> answer = questService.answer2(question);
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		if(answer.size() == 1){
			nodes.append('{' + "\"1\": {\"name\": \"").append(answer.get(0)).append("\",\"type\": \"1\"},\n").append('}');
		} else {
			nodes.append('{');
			edges.append("[\n");
			int size = answer.size();
			boolean flag = answer.get(size-1).equals("to");
			for(int i =0;i<size-2;i++){
				nodes.append("\"").append(i+1).append("\": {\"name\": \"").append(answer.get(i)).append("\",\"type\": \"1\"},\n");
			}
			for(int i =0;i<size-3;i++){
				if(flag)
					edges.append("{ \"source\":").append(i+1).append(", \"target\": ").append(size-2).append(", \"rela\": \"").append(answer.get(size-2)).append("\",\"type\": \"1\"},\n");
				else
					edges.append("{ \"source\":").append(size-2).append(", \"target\": ").append(i+1).append(", \"rela\": \"").append(answer.get(size-2)).append("\",\"type\": \"1\"},\n");

			}
			nodes.append('}');
			edges.append(']');
		}
		System.out.println(nodes);
		System.out.println(edges);

		JSONObject json = new JSONObject();
		json.put("nodes", JSON.parse(nodes.toString()));
		json.put("edges", JSON.parse(edges.toString()));
		System.out.println(json);

		return json;
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
