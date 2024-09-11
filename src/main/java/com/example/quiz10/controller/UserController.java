package com.example.quiz10.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.service.ifs.UserService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.UserReq;

@RestController
@CrossOrigin
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping(value = "user/register")
	public BasicRes register(@Valid @RequestBody  UserReq req) {
		return userService.register(req);
	}
	
	@PostMapping(value = "user/login")
	public BasicRes login(@Valid @RequestBody  UserReq req, HttpSession session) {
		session.setMaxInactiveInterval(300); //  �i�H�]�m�u����10�����A�w�]30����1800��A�]�m0�έt�ƴN�ä��L��
		BasicRes res = userService.login(req);
		if (res.getCode() == 200) {
			//  �Y�n�J���\�A��ϥΪ̦W�ټȦs�bsession��
			//  �C��client�Pserver������session�����@��
			session.setAttribute("user_name", req.getName());
		}
		return res;
	}
	
	//�]���S��req�ҥH��GetMapping
	@GetMapping(value = "user/logout")
	public BasicRes logout(HttpSession session) {
		//logout�N�O�n�����������q�T�Ϊ�session����
		session.invalidate();
		return new BasicRes(ResMessage.SUCCESS.getCode(), //)
				ResMessage.SUCCESS.getMessage());
	}
}
