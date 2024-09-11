package com.example.quiz10.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.Quiz;

//�u���~�Ӥ@��Quiz�A�ѤU�����D
public class CreateUpdateReq extends Quiz{
	
	@Valid
	private List<Ques> quesList;//��@�i�ݨ��ӻ��@�i�ݨ����h�إi��A��List
	
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
