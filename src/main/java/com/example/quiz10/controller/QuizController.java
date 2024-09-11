package com.example.quiz10.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.service.ifs.QuizService;
import com.example.quiz10.service.ifs.UserService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillingReq;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;
import com.example.quiz10.vo.UserReq;

@RestController
@CrossOrigin
public class QuizController {
	
	@Autowired
	private QuizService quizService;
	
	
	//登入成功後才能使用
	@PostMapping(value = "quiz/create_login")
	public BasicRes createLogin(@Valid @RequestBody  CreateUpdateReq req, HttpSession session) {
		//  在UserController的login方法中若有登入成功就會透過user_name這個key把req中的name加入session中
		//  把資訊暫存到session中的方法是setAttribute，取出則是用getAttribute
		//  key的字串user_name要完全依樣才能取出對應的value，取不到key對應的value則是null
//		String userName = (String) session.getAttribute("user_name");//強制轉型Object to String ，但如果返回的是null強制轉型會報錯(nullPointerException)
		Object userName = session.getAttribute("user_name");
		if (userName == null) {
			return new BasicRes(ResMessage.PLEASE_LOGIN_FIRST.getCode(), 
					ResMessage.PLEASE_LOGIN_FIRST.getMessage());
		}
		//之後如果有需要再強制轉型，下面這段暫時用不到
		userName = (String) userName;
		
		return quizService.create(req);
	}
	
//	======================================================
//	======================================================

	//CreateUpdateReq有加入嵌套驗證
	@PostMapping(value = "quiz/create")
	public BasicRes create(@Valid @RequestBody  CreateUpdateReq req) {
		return quizService.create(req);
	}
	
	
	@PostMapping(value = "quiz/update")
	public BasicRes update(@Valid @RequestBody  CreateUpdateReq req) {
		return quizService.update(req);
	}
	
	@PostMapping(value = "quiz/delete")
	public BasicRes delete(@Valid @RequestBody  DeleteReq req) {
		return quizService.delete(req);
	}
	
	@PostMapping(value = "quiz/search")
	public BasicRes search(@Valid @RequestBody  SearchReq req) {
		return quizService.search(req);
	}
	
	//有加入嵌套驗證
	@PostMapping(value = "quiz/fillin")
	public BasicRes fillin(@Valid @RequestBody  FillingReq req) {
		return quizService.fillin(req);
	}
	
//	@PostMapping(value = "quiz/statistics")
//	public StatisticsRes statistics(@Valid @RequestBody int req) {
//		return quizService.statistics(req);
//	}
	
	//外部呼叫此API，必須要使用JQuery的方式，quiz/statistics?quizId=問卷編號(要有問號)
	//quizId的名字要和方法中的參數變數名字一樣
	@PostMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam  int quizId) {
		return quizService.statistics(quizId);
	}
	
	//外部呼叫此API，必須要使用JQuery的方式，quiz/statistics?quiz_id=問卷編號(要有問號)
	//因為@RequestParam的value是quiz_id，所以呼叫路徑問號後面的字串就是quiz_id
	//required預設是true
	//defaultValue指參數不給值或maqpping不到時會給預設值
	@PostMapping(value = "quiz/statistics1")
	public StatisticsRes statistics1(@RequestParam(value = "quiz_id", required = false, defaultValue = "0")  int quizId) {
		return quizService.statistics(quizId);
	}
	
	//呼叫此API的URL，quiz/feedback?quizId=問卷編號
	@PostMapping(value = "quiz/feedback")
	public FeedbackRes feedback(@RequestParam  int quizId) {
		return quizService.feedback(quizId);
	}
	
}
