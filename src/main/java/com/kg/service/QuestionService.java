package com.kg.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface QuestionService {

	  void showDictPath();
	  int init() throws Exception;
	  String answer(String question) throws Exception;
	JSONObject question(String question) throws Exception;

}
