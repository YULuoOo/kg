package com.kg.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class ModelProcess {


	/**
	 * 分类标签号和问句模板对应表
	 */
	Map<Double, String> questionsPattern;

	/**
	 * Spark贝叶斯分类器
	 */
	NaiveBayesModel nbModel;

	DecisionTreeModel dtModel;

	/**
	 * 词语和下标的对应表   == 词汇表
	 */
	Map<String, Integer> vocabulary;

	/**
	 * 关键字与其词性的map键值对集合 == 句子抽象
	 */
	Map<String, String> abstractMap;

	/**
	 * 指定问题question及字典的txt模板所在的根目录
	 */
    String rootDirPath = "/Users/zhaoyiwei/data-kg/data";

    /**
     * 分类模板索引
     */
    int modelIndex = 0;

	public ModelProcess() throws Exception{
		questionsPattern = loadQuestionsPattern();
		vocabulary = loadVocabulary();
		nbModel = loadClassifierModel();
	}


	public ModelProcess(String rootDirPath) throws Exception{
		this.rootDirPath = rootDirPath+'/';
		questionsPattern = loadQuestionsPattern();
		vocabulary = loadVocabulary();
		nbModel = loadClassifierModel();
		//dtModel = load();
	}

	public ArrayList<String> analyQuery(String queryString) throws Exception {

		/**
		 * 打印问句
		 */
		System.out.println("原始句子："+queryString);
		System.out.println("========HanLP开始分词========");

		/**
		 * 抽象句子，利用HanPL分词，将关键字进行词性抽象
		 */
		String abstr = queryAbstract(queryString);
		System.out.println("句子抽象化结果："+abstr);// nm 的 导演 是 谁

		/**
		 * 将抽象的句子与spark训练集中的模板进行匹配，拿到句子对应的模板
		 */
		String strPatt = queryClassify(abstr);
		System.out.println("句子套用模板结果："+strPatt); // nm 制作 导演列表


		/**
		 * 模板还原成句子，此时问题已转换为我们熟悉的操作
		 */
		String finalPattern = queryExtenstion(strPatt);
		System.out.println("原始句子替换成系统可识别的结果："+finalPattern);// 但丁密码 制作 导演列表


		ArrayList<String> resultList = new ArrayList<String>();
		resultList.add(String.valueOf(modelIndex));
		String[] finalPattArray = finalPattern.split(" ");
		for (String word : finalPattArray)
			resultList.add(word);
		return resultList;
	}

	public  String queryAbstract(String querySentence) {

		// 句子抽象化
		Segment segment = HanLP.newSegment().enableCustomDictionary(true);
		List<Term> terms = segment.seg(querySentence);
		String abstractQuery = "";
		abstractMap = new HashMap<String, String>();
		int nrCount = 0; //nr 人名词性这个 词语出现的频率
		for (Term term : terms) {
			String word = term.word;
			String termStr = term.toString();
			System.out.println(termStr);
			if (termStr.contains("nr") && nrCount == 0) { //nr 人名
				abstractQuery += "nnt ";
				abstractMap.put("nnt", word);
				nrCount++;
			}else if (termStr.contains("nr") && nrCount == 1) { //nr 人名 再出现一次，改成nnr
				abstractQuery += "nnr ";
				abstractMap.put("nnr", word);
				nrCount++;
			} else if (termStr.contains("ng")) { //ng 类型
				abstractQuery += "ng ";
				abstractMap.put("ng", word);
			}
			else if (termStr.contains("noc")) { //noc 公司名
				abstractQuery += "noc ";
				abstractMap.put("noc", word);
			}
			else if (termStr.contains("m")) { //m 股票代码
				abstractQuery += "m ";
				abstractMap.put("m", word);
			}else if (termStr.contains("cc")) { //cc concept
				abstractQuery += "cc ";
				abstractMap.put("cc", word);
			}
			else if (termStr.contains("pr")) { //pr product
				abstractQuery += "pr ";
				abstractMap.put("pr", word);
			}
			else {
				abstractQuery += word + " ";
			}
		}
		System.out.println("========HanLP分词结束========");
		return abstractQuery;
	}

	public  String queryExtenstion(String queryPattern) {
		// 句子还原
		Set<String> set = abstractMap.keySet();
		for (String key : set) {
			System.out.println(key);
			/**
			 * 如果句子模板中含有抽象的词性
			 */
			if (queryPattern.contains(key)) {

				/**
				 * 则替换抽象词性为具体的值
				 */
				String value = abstractMap.get(key);
				queryPattern = queryPattern.replace(key, value);
			}
		}
		String extendedQuery = queryPattern;
		/**
		 * 当前句子处理完，抽象map清空释放空间并置空，等待下一个句子的处理
		 */
		abstractMap.clear();
		abstractMap = null;
		return extendedQuery;
	}


	/**
	 * 加载词汇表 == 关键特征 == 与HanLP分词后的单词进行匹配
	 * @return
	 */
	public  Map<String, Integer> loadVocabulary() {
		Map<String, Integer> vocabulary = new HashMap<String, Integer>();
		File file = new File(rootDirPath + "question/vocabulary.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":");
				int index = Integer.parseInt(tokens[0]);
				String word = tokens[1];
				vocabulary.put(word, index);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vocabulary;
	}

	/**
	 * 加载文件，并读取内容返回
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public  String loadFile(String filename) throws IOException {
		File file = new File(rootDirPath + filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String content = "";
		String line;
		while ((line = br.readLine()) != null) {
			/**
			 * 文本的换行符暂定用"`"代替
			 */
			content += line + "`";
		}
		/**
		 * 关闭资源
		 */
		br.close();
		return content;
	}

	/**
	 * 句子分词后与词汇表进行key匹配转换为double向量数组
	 * @param sentence
	 * @return
	 * @throws Exception
	 */
	public  double[] sentenceToArrays(String sentence) throws Exception {

		double[] vector = new double[vocabulary.size()];
		/**
		 * 模板对照词汇表的大小进行初始化，全部为0.0
		 */
		for (int i = 0; i < vocabulary.size(); i++) {
			vector[i] = 0;
		}

		/**
		 * HanLP分词，拿分词的结果和词汇表里面的关键特征进行匹配
		 */
		Segment segment = HanLP.newSegment();
		List<Term> terms = segment.seg(sentence);
		for (Term term : terms) {
			String word = term.word;
			/**
			 * 如果命中，0.0 改为 1.0
			 */
			if (vocabulary.containsKey(word)) {
				int index = vocabulary.get(word);
				//System.out.print(word);
				vector[index] = 1;
			}
		}
		//System.out.println(Arrays.toString(vector));
		return vector;
	}

	/**
	 * Spark朴素贝叶斯(naiveBayes)
	 * @return
	 * @throws Exception
	 */
	public  NaiveBayesModel loadClassifierModel() throws Exception {
		SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
		conf.set("spark.driver.allowMultipleContexts","true");
		JavaSparkContext sc = new JavaSparkContext(conf);

		//生成训练集
		List<LabeledPoint> train_list = new LinkedList<LabeledPoint>();
		String[] sentences = null;
		/**
		 * 加载已经设置好的问题模版
		 */

		String companyCodeQuestion = loadFile("question/【0】公司代码.txt");
		sentences = companyCodeQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(0.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyInfoQuestion = loadFile("question/【1】公司信息.txt");
		sentences = companyInfoQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(1.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyEnglishNameQuestion = loadFile("question/【2】公司英文名.txt");
		sentences = companyEnglishNameQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(2.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyPersonQuestion = loadFile("question/【3】公司高管.txt");
		sentences = companyPersonQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(3.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String stockHolderQuestion = loadFile("question/【4】股票股东.txt");
		sentences = stockHolderQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(4.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String personCompanyIndustry = loadFile("question/【5】高管公司行业.txt");
		sentences = personCompanyIndustry.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(5.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String underwriteConceptIndustry = loadFile("question/【6】公司主承销的股票相同概念.txt");
		sentences = underwriteConceptIndustry.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(6.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String businessIncome = loadFile("question/【7】业务收入率最高.txt");
		sentences = businessIncome.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(7.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String ss = loadFile("question/【8】同一天上市的股票.txt");
		sentences = ss.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(8.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String ss1 = loadFile("question/【9】拥有产品地区相同.txt");
		sentences = ss1.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(9.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(train_list);
		NaiveBayesModel nb_model = NaiveBayes.train(trainingRDD.rdd());
		//关闭资源
		sc.close();
		//返回分类器模型
		return nb_model;
	}

	public  DecisionTreeModel load() throws Exception {

		SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);

		/**
		 * 训练集生成
		 * labeled point 是一个局部向量，要么是密集型的要么是稀疏型的
		 * 用一个label/response进行关联。在MLlib里，labeled points 被用来监督学习算法
		 * 我们使用一个double数来存储一个label，因此我们能够使用labeled points进行回归和分类
		 */
		List<LabeledPoint> train_list = new LinkedList<LabeledPoint>();
		String[] sentences = null;


		String companyCodeQuestion = loadFile("question/【0】公司代码.txt");
		sentences = companyCodeQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(0.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyInfoQuestion = loadFile("question/【1】公司信息.txt");
		sentences = companyInfoQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(1.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyEnglishNameQuestion = loadFile("question/【2】公司英文名.txt");
		sentences = companyEnglishNameQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(2.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String companyPersonQuestion = loadFile("question/【3】公司高管.txt");
		sentences = companyPersonQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(3.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String stockHolderQuestion = loadFile("question/【4】股票股东.txt");
		sentences = stockHolderQuestion.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(4.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String personCompanyIndustry = loadFile("question/【5】高管公司行业.txt");
		sentences = personCompanyIndustry.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(5.0, Vectors.dense(array));
			train_list.add(train_one);
		}

		String underwriteConceptIndustry = loadFile("question/【6】公司主承销的股票相同概念.txt");
		sentences = underwriteConceptIndustry.split("`");
		for (String sentence : sentences) {
			double[] array = sentenceToArrays(sentence);
			LabeledPoint train_one = new LabeledPoint(6.0, Vectors.dense(array));
			train_list.add(train_one);
		}
		JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(train_list);

		//决策树
		Integer numClasses = 8;//类别数量
		Map<Integer, Integer> categoricalFeaturesInfo = new HashMap();
		String impurity = "gini";//对于分类问题，我们可以用熵entropy或Gini来表示信息的无序程度 ,对于回归问题，我们用方差(Variance)来表示无序程度，方差越大，说明数据间差异越大
		Integer maxDepth = 5;//最大树深
		Integer maxBins = 32;//最大划分数
		final DecisionTreeModel tree_model = DecisionTree.trainClassifier(trainingRDD, numClasses,categoricalFeaturesInfo, impurity, maxDepth, maxBins);//构建模型


		/**
		 * 记得关闭资源
		 */
		sc.close();

		/**
		 * 返回贝叶斯分类器
		 */
		return tree_model;

	}

	/**
	 * 加载问题模板 == 分类器标签
	 * @return
	 */
	public  Map<Double, String> loadQuestionsPattern() {
		Map<Double, String> questionsPattern = new HashMap<Double, String>();
		File file = new File(rootDirPath + "question/question_classification.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":");
				double index = Double.valueOf(tokens[0]);
				String pattern = tokens[1];
				questionsPattern.put(index, pattern);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionsPattern;
	}

	/**
	 * 贝叶斯分类器分类的结果，拿到匹配的分类标签号，并根据标签号返回问题的模板
	 * @param sentence
	 * @return
	 * @throws Exception
	 */
	public  String queryClassify(String sentence) throws Exception {

		double[] testArray = sentenceToArrays(sentence);
		Vector v = Vectors.dense(testArray);
		System.out.println(v.toString());

		/**
		 * 对数据进行预测predict
		 * 句子模板在 spark贝叶斯分类器中的索引【位置】
		 * 根据词汇使用的频率推断出句子对应哪一个模板
		 */
		double index = nbModel.predict(v);
		modelIndex = (int)index;
		System.out.println("朴素贝叶斯分类 the model index is " + index);
		Vector vRes = nbModel.predictProbabilities(v);
		int size = vRes.toArray().length;
		for(int i=0;i<size;i++)
		System.out.println("问题模板分类【"+i+"】概率："+vRes.toArray()[i]);

//		double index2 = dtModel.predict(v);
//		System.out.println("决策树 the model index is " + index2);


		return questionsPattern.get(index);
	}

	public static void main(String[] agrs) throws Exception {
		System.out.println("Hello World !");
	}
}
