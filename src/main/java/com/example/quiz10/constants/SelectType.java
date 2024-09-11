package com.example.quiz10.constants;

public enum SelectType {
	
	SINGLE( "單選"),//
	MULTI("多選"),//
	TEXT("詳述"),//

	
	SELECT_ERROR("Select type cannot be others");
	
	private String type;

	private SelectType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
//	public String parser(String input) {
//		
//		
//		return
//	}
}
