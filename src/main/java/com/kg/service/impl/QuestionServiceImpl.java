package com.kg.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.kg.repository.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.kg.process.ModelProcess;
import com.kg.service.QuestionService;
import com.hankcs.hanlp.dictionary.CustomDictionary;

@Service
@Primary
public class QuestionServiceImpl implements QuestionService {

	@Value("${rootDirPath}")
	private String rootDictPath;

	@Value("${HanLP.CustomDictionary.path.companyDict}")
	private String companyDictPath;

	@Value("${HanLP.CustomDictionary.path.conceptDict}")
	private String conceptDictPath;

	@Value("${HanLP.CustomDictionary.path.productDict}")
	private String productDictPath;



	@Autowired
	private Driver driver;

	private ModelProcess queryProcess;

	@Override
	public void showDictPath() {
		System.out.println("HanLP分词字典及自定义问题模板根目录：" + rootDictPath);
	}

	@Override
	public int init() throws Exception {
		queryProcess = new ModelProcess(rootDictPath);

		loadCompanyDict(companyDictPath);

		loadConceptDict(conceptDictPath);

		loadProductDict(productDictPath);
		return 1;
	}

	@Override
	public String answer(String question) throws Exception {
		return null;
	}

	@Override
	public JSONObject question(String question) throws Exception {

		if(queryProcess == null){
			this.init();
		}
		ArrayList<String> reStrings = queryProcess.analyQuery(question);
		int modelIndex = Integer.valueOf(reStrings.get(0));
		JSONObject answer = new JSONObject();
		String s1 = "";
		String s2 = "";
		/**
		 * 匹配问题模板
		 */
		switch (modelIndex) {
			case 0:
				/**
				 * nof 的 代码
				 */
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.get_code(s1);
				break;
			case 1:
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.get_c_info(s1);
				break;
			case 2:
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.get_eng(s1);
				break;
			case 3:
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.work_in(s1);
				break;
			case 4:
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.stock_holder(s1);
				break;
			case 5:
				s1 = reStrings.get(1);
				System.out.println(s1);
				answer = driver.person_company_industry(s1);
				break;
			case 6:
				s1 = reStrings.get(1);
				s2 = reStrings.get(2);

				System.out.println(s1+" "+s2);
				answer = driver.underwrite_concept_industry(s1,s2);
				break;
			case 7:
				s1 = reStrings.get(1);

				System.out.println(s1);
				answer = driver.business_income_rate_highest(s1);
				break;
			case 8:
				s1 = reStrings.get(1);

				System.out.println(s1);
				answer = driver.same_date_shangshi(s1);
				break;
			case 9:
				s1 = reStrings.get(1);

				System.out.println(s1);
				answer = driver.product_same_position(s1);
				break;
			default:
				break;
		}

		System.out.println(answer.toString());
		return answer;
	}


	/**
	 * 加载自定义字典
	 *
	 * @param path
	 */
	public void loadCompanyDict(String path) {

		File file = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			addCustomDictionary(br, 4);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	public void loadConceptDict(String path) {

		File file = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			addCustomDictionary(br, 5);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}
	public void loadProductDict(String path) {

		File file = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			addCustomDictionary(br, 6);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}


	/**
	 * 添加自定义分词及其词性，注意数字0表示频率，不能没有
	 *
	 * @param br
	 * @param type
	 */
	public void addCustomDictionary(BufferedReader br, int type) {

		String word;
		try {
			while ((word = br.readLine()) != null) {
				switch (type) {
				/**
				 * 设置公司名词 词性 == noc 0
				 */
				case 4:
					CustomDictionary.add(word, "noc 0");
					break;
				case 5:
					CustomDictionary.add(word, "cc 0");
					break;
				case 6:
					CustomDictionary.add(word, "pr 0");
					break;
				default:
					break;
				}
			}
			br.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
