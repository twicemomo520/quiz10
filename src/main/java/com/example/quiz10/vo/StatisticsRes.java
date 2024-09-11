package com.example.quiz10.vo;

import java.util.List;
import java.util.Map;

public class StatisticsRes extends BasicRes{
	
	
	private String quizName;
	
	private List<StatisticsVo> statisticsList;
	
	public StatisticsRes() {
		super();
	}

	public StatisticsRes(int code, String message) {
		super(code, message);
	}

	public StatisticsRes(int code, String message, String quizName, List<StatisticsVo> statisticsList) {
		super(code, message);
		this.quizName = quizName;
		this.statisticsList = statisticsList;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public List<StatisticsVo> getStatisticsList() {
		return statisticsList;
	}

	public void setStatisticsList(List<StatisticsVo> statisticsList) {
		this.statisticsList = statisticsList;
	}




}
