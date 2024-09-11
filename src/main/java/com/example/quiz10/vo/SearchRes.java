package com.example.quiz10.vo;

import java.util.List;

public class SearchRes extends BasicRes{
	
	private List<QuizRes> quizResList;
	private List<Integer>quizIdList;

	public SearchRes() {
		super();
	}

	public SearchRes(int code, String message) {
		super(code, message);
	}

	public SearchRes(int code, String message, List<QuizRes> quizResList, List<Integer>quizIdList) {
		super(code, message);
		this.quizResList = quizResList;
		this.quizIdList = quizIdList;
	}

	public List<QuizRes> getQuizResList() {
		return quizResList;
	}

	public void setQuizResList(List<QuizRes> quizResList) {
		this.quizResList = quizResList;
	}

	public List<Integer> getQuizIdList() {
		return quizIdList;
	}

	public void setQuizIdList(List<Integer> quizIdList) {
		this.quizIdList = quizIdList;
	}
	
	
}
