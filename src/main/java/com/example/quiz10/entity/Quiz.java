package com.example.quiz10.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "quiz")
public class Quiz {
	
	// �]�� PK �O AI(Auto Incremental)�A�ҥH�n�[�W�� @GeneratedValue
	 // strategy: �����O AI ���ͦ�����
	 // GenerationType.IDENTITY: �N�� PK �Ʀr�Ѹ�Ʈw�Ӧ۰ʼW�[
	//GeneratedValue(strategy = GenerationType.IDENTITY) ���[���ܡA�^�ǭȴN���|��Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //�o��Id�ݩ�AI�ADao save���ɭԥi�H���ΦsId�i�h�]���|�۰ʥͦ�
	@Id
	@Column(name = "id")
	private int id;
	
	@NotBlank(message = "Quiz name cannot be null or empty")
	@Column(name = "name")
	private String name;
	
	@NotBlank(message = "Description cannot be blank")
	@Column(name = "description")
	private String description;
	
	@FutureOrPresent(message = "Start date must be in present or in future")
	@NotNull(message = "Start date cannot be null ")
	@Column(name = "start_date")
	private LocalDate startDate;
	
	@FutureOrPresent(message = "End date must be in present or in future")
	@NotNull(message = "End date cannot be null ")
	@Column(name = "end_date")
	private LocalDate endDate;
	
	//�w�]false�ҥH���μg
	@Column(name = "published")
	private boolean published;
	
	
	
	public Quiz() {
		super();
	}
	public Quiz(String name, String description, LocalDate startDate,LocalDate endDate, boolean published){
		super();
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.published = published;
	}
	
	
	public Quiz(int id, String name, String description, LocalDate startDate,LocalDate endDate, boolean published){
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.published = published;
	}
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
	

	
	
	
	
	
}
