package com.appleyk.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface QuestionService {

	  void showDictPath();
	  int init() throws Exception;
	  String answer(String question) throws Exception;
	 List<String> answer2(String question) throws Exception;
	JSONObject driver(String question) throws Exception;

}
