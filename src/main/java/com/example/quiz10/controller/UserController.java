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
		session.setMaxInactiveInterval(300); //  可以設置只維持10秒鐘，預設30分鐘1800秒，設置0或負數就永不過期
		BasicRes res = userService.login(req);
		if (res.getCode() == 200) {
			//  若登入成功，把使用者名稱暫存在session中
			//  每個client與server之間的session都不一樣
			session.setAttribute("user_name", req.getName());
		}
		return res;
	}
	
	//因為沒有req所以用GetMapping
	@GetMapping(value = "user/logout")
	public BasicRes logout(HttpSession session) {
		//logout就是要讓彼此之間通訊用的session失效
		session.invalidate();
		return new BasicRes(ResMessage.SUCCESS.getCode(), //)
				ResMessage.SUCCESS.getMessage());
	}
}
