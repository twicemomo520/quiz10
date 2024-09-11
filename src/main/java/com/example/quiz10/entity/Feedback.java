package com.example.quiz10.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "feedback")
@IdClass(value = FeedbackId.class)
public class Feedback {
	
	@Min(value = 1, message = "Quiz Id cannot be less than 1")
	@Id
	@Column(name="quiz_id")
	private int quizId;
	
	@Min(value = 1, message = "Qu Id cannot be less than 1")
	@Id
	@Column(name="quId")
	private int quId;
	
	@NotBlank(message = "Name cannot be null or empty")
	@Column(name="name")
	private String name;
	
	@Column(name="phone")
	private String phone;
	
	@NotBlank(message = "Email cannot be null or empty")
	@Id
	@Column(name="email")
	private String email;
	
	@Min(value = 0, message = "Age cannot be negative")
	@Column(name="age")
	private int age;
	
	//格式是送過來之前已經由陣列轉字串
	//答案有多個時用分號串多個答案
	@Column(name="ans")
	private String ans;
	
	@Column(name="fillin_data_time")
	private LocalDateTime fillinDateTime;



	
	public Feedback() {
		super();
	}
	
	public Feedback(int quizId,  int quId, String name, String phone, String email, int age, String ans,  LocalDateTime fillinDateTime) {
		super();
		this.quizId = quizId;
		this.quId = quId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.ans = ans;
		this.fillinDateTime = fillinDateTime;

	}
	
	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAns() {
		return ans;
	}

	public void setAns(String ans) {
		this.ans = ans;
	}

	public LocalDateTime getFillinDateTime() {
		return fillinDateTime;
	}

	public void setFillinDateTime(LocalDateTime fillinDateTime) {
		this.fillinDateTime = fillinDateTime;
	}
	
}
