package com.appleyk.service;

public interface QuestionService {

	  void showDictPath();
	  int init() throws Exception;
	  String answer(String question) throws Exception;
	 Object answer2(String question) throws Exception;

}
