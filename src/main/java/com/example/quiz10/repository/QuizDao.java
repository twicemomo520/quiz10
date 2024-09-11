package com.example.quiz10.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.quiz10.entity.Quiz;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer>{
	
	//Published: 直接在published後面給定true所以不用再給boolean Published值
	public boolean existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
			List<Integer> idList, LocalDate now1, LocalDate now2);
	
	//問卷名稱是模糊比對所以用containong
	public List<Quiz> findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
			String quizName, LocalDate startDate, LocalDate endDate);
}
