package com.example.quiz10.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.entity.User;
import com.example.quiz10.repository.UserDao;
import com.example.quiz10.service.ifs.UserService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.UserReq;

@Service
public class UserServiceImpl implements UserService{
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserDao userDao;
	
	@Override
	public BasicRes register(UserReq req) {
		//�ˬd�b���O�_�w�Q���U
		if (userDao.existsById(req.getName())) {
			return new BasicRes(ResMessage.USER_NAME_EXISTED.getCode(), //)
					ResMessage.USER_NAME_EXISTED.getMessage());
		}
		//��K�X�[�K
		String encodePws = encoder.encode(req.getPwd());
		//�N��Ʀs�i��Ʈw
		userDao.save(new User(req.getName(), encodePws));
		return new BasicRes(ResMessage.SUCCESS.getCode(), //)
					ResMessage.SUCCESS.getMessage());
	}

	@Override
	public BasicRes login(UserReq req) {
		//�ˬd�b���O�_�s�b
		Optional<User>op = userDao.findById(req.getName());
		
		if(op.isEmpty()) {
			return new BasicRes(ResMessage.USER_NAME_NOT_FOUND.getCode(), //)
					ResMessage.USER_NAME_NOT_FOUND.getMessage());
		}
		User user = op.get();
		//��K�X�ѱK�ä��
		if(!encoder.matches(req.getPwd(), user.getPwd())) {
			return new BasicRes(ResMessage.USER_PASSWORD_INCONSISTENT.getCode(), //)
					ResMessage.USER_PASSWORD_INCONSISTENT.getMessage());
		}
		
		return new BasicRes(ResMessage.SUCCESS.getCode(), //)
				ResMessage.SUCCESS.getMessage());
	}
}
