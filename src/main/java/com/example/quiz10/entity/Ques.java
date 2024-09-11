package com.example.quiz10.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "ques")
@IdClass(value = QuesId.class)
public class Ques {
	
	@Min(value = 0, message = "QuizId cannot be less than 0")
	@Id
	@Column(name = "quiz_id")
	private int quizId;
	
	@Min(value = 1, message = "QuisId cannot be less than 1")
	@Id
	@Column(name = "id")
	private int id;
	
	@NotBlank(message = "question cannot be null or empty")
	@Column(name = "qu")
	private  String qu;
	
	@NotBlank(message = "question type cannot be null or empty")
	@Column(name = "type")
	private String type;
	

	@Column(name = "necessary")
	private boolean necessary;

	//因為詳述題沒有送東西，所以不需要Blank
	//是多個選項的字串
	@Column(name = "options")
	private String options;
	
	public Ques() {
		super();
	}
	public Ques(int quizId, int id, String qu, String type, boolean necessary, String options) {
		super();
	}
	
	
	

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQu() {
		return qu;
	}

	public void setQu(String qu) {
		this.qu = qu;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}
	
}
