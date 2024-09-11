package com.example.quiz10.constants;

public enum ResMessage {
	
	SUCCESS(200, "Success!!"),//
	DATA_ERROR(400, "Start date cannot be later than end date"),//
	OPTIONS_ERROR(400, "Single or Multi cannot be null or empty"),
	QUIZ_NOT_FOUND(400, "Quiz not found"),//
	QUIZ_ID_NOT_MATCH(400, "Quiz id not match"),//
	QUIZ_IN_PROGRESS(400, "Quiz in progress"),//
	QUIZ_ID_OR_EMAIL_INCONSISTENT(400, "Quiz id or email inconsistent"),//
	EMAIL_DUPLICATE(400, "Email duplicate"),//
	CANNOT_FIILIN_QUIZ(400, "Cannot fillin quiz"),//
	FIILIN_INCOMPLETE(400, "Fillin incomplete"),//
	FIILININ_IS_NECESSARY(400, "Fill in is necessary"),//
	QUID_MISMATCH(400, "Quid mismatch"),//
	QUES_IS_SINGLE_CHOICE(400, "Single choice ques!!"),//
	OPTION_ANSWER_MISMATCH(400, "Option answer mismatch"),//
	USER_NAME_EXISTED(400, "User name existed"),//
	USER_NAME_NOT_FOUND(400, "User name not found"),//
	USER_PASSWORD_INCONSISTENT(400, "User password inconsistent"),//
	PLEASE_LOGIN_FIRST(400, "Please login first"),//
	NO_QUIZID(400, "No quizId");
	
	
	private int code;
	
	private String message;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
