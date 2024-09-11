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
	
	
	//�n�J���\��~��ϥ�
	@PostMapping(value = "quiz/create_login")
	public BasicRes createLogin(@Valid @RequestBody  CreateUpdateReq req, HttpSession session) {
		//  �bUserController��login��k���Y���n�J���\�N�|�z�Luser_name�o��key��req����name�[�Jsession��
		//  ���T�Ȧs��session������k�OsetAttribute�A���X�h�O��getAttribute
		//  key���r��user_name�n�����̼ˤ~����X������value�A������key������value�h�Onull
//		String userName = (String) session.getAttribute("user_name");//�j���૬Object to String �A���p�G��^���Onull�j���૬�|����(nullPointerException)
		Object userName = session.getAttribute("user_name");
		if (userName == null) {
			return new BasicRes(ResMessage.PLEASE_LOGIN_FIRST.getCode(), 
					ResMessage.PLEASE_LOGIN_FIRST.getMessage());
		}
		//����p�G���ݭn�A�j���૬�A�U���o�q�ȮɥΤ���
		userName = (String) userName;
		
		return quizService.create(req);
	}
	
//	======================================================
//	======================================================

	//CreateUpdateReq���[�J�O�M����
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
	
	//���[�J�O�M����
	@PostMapping(value = "quiz/fillin")
	public BasicRes fillin(@Valid @RequestBody  FillingReq req) {
		return quizService.fillin(req);
	}
	
//	@PostMapping(value = "quiz/statistics")
//	public StatisticsRes statistics(@Valid @RequestBody int req) {
//		return quizService.statistics(req);
//	}
	
	//�~���I�s��API�A�����n�ϥ�JQuery���覡�Aquiz/statistics?quizId=�ݨ��s��(�n���ݸ�)
	//quizId���W�r�n�M��k�����Ѽ��ܼƦW�r�@��
	@PostMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam  int quizId) {
		return quizService.statistics(quizId);
	}
	
	//�~���I�s��API�A�����n�ϥ�JQuery���覡�Aquiz/statistics?quiz_id=�ݨ��s��(�n���ݸ�)
	//�]��@RequestParam��value�Oquiz_id�A�ҥH�I�s���|�ݸ��᭱���r��N�Oquiz_id
	//required�w�]�Otrue
	//defaultValue���ѼƤ����ȩ�maqpping����ɷ|���w�]��
	@PostMapping(value = "quiz/statistics1")
	public StatisticsRes statistics1(@RequestParam(value = "quiz_id", required = false, defaultValue = "0")  int quizId) {
		return quizService.statistics(quizId);
	}
	
	//�I�s��API��URL�Aquiz/feedback?quizId=�ݨ��s��
	@PostMapping(value = "quiz/feedback")
	public FeedbackRes feedback(@RequestParam  int quizId) {
		return quizService.feedback(quizId);
	}
	
}
