package com.example.quiz10.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.Quiz;

//只能繼承一個Quiz，剩下的問題
public class CreateUpdateReq extends Quiz{
	
	@Valid
	private List<Ques> quesList;//對一張問卷來說一張問卷有多種可能，用List
	
	public CreateUpdateReq() {
		super();
	}
	
	public CreateUpdateReq(String name , String description, LocalDate startDate,  LocalDate endDate, boolean published) {
		super(name, description, startDate, endDate, published);
	}

	public List<Ques> getQuesList() {
		return quesList;
	}

	public void setQuesList(List<Ques> quesList) {
		this.quesList = quesList;
	}
	
	
}
