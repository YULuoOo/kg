package com.appleyk.service;

import java.util.List;

public interface QuestionService {

	  void showDictPath();
	  int init() throws Exception;
	  String answer(String question) throws Exception;
	 List<String> answer2(String question) throws Exception;
	Object answer3(String question) throws Exception;

}
