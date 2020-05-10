package com.kg.controller;

import com.kg.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageIndexController {

	@Autowired
	QuestionService questService;
	@RequestMapping("/")
	public String index() throws Exception {
		questService.init();
		return "index";
	}

}
