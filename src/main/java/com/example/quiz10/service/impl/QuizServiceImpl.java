package com.example.quiz10.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.constants.SelectType;
import com.example.quiz10.entity.Feedback;
import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.Quiz;
import com.example.quiz10.entity.User;
import com.example.quiz10.repository.FeedbackDao;
import com.example.quiz10.repository.QuesDao;
import com.example.quiz10.repository.QuizDao;
import com.example.quiz10.repository.UserDao;
import com.example.quiz10.service.ifs.QuizService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillingReq;
import com.example.quiz10.vo.QuizRes;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;
import com.example.quiz10.vo.StatisticsVo;
import com.example.quiz10.vo.UserReq;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;
	@Autowired
	private QuesDao quesDao;
	@Autowired
	private FeedbackDao feedbackDao;

	// 這裡是處理create
	// @Transactional import 的 library， javax 和 springframework 都可以使用
	// 兩者差異可參照 PPT spring boot_02 @Transactional 部分
	@Transactional
	@Override
	public BasicRes create(CreateUpdateReq req) {

		// 基本的屬性已經交由@Valid
		// 開始時間不能比結束時間晚
		if (req.getStartDate().isAfter(req.getStartDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}

		List<Ques> queslist = req.getQuesList();

		for (Ques item : queslist) {

			if (item.getType().equalsIgnoreCase((SelectType.SINGLE.getType()))
					|| item.getType().equalsIgnoreCase((SelectType.MULTI.getType()))) {

				// 確定單選或多選，選項就必須要有值
				// 前面有驚嘆號代表否定的意思
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		;

		Quiz res = quizDao.save(
				new Quiz(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished()));

		// 直接saveAll會有問題，資料庫的quiz_id不會有值，所以要把上面那行的回傳值接回來取得裡面的quiz_id
		// 因為Quiz中的id是Ai自動生成的流水號，要讓quizDao執行save後可以把該id的值回傳
		// 必須要在Quiz此Entity中將資料型態為int的屬性Id加上@GeneratedValue(strategy =
		// GenerationType.IDENTITY)
		queslist.forEach(item -> {
			item.setQuizId(res.getId());
		});
		quesDao.saveAll(req.getQuesList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

	}
//===========================================================
//===========================================================

	// 這裡是處理update
	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {

		// 基本的屬性已經交由@Valid
		// 開始時間不能比結束時間晚

		if (req.getStartDate().isAfter(req.getStartDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}

		List<Ques> queslist = req.getQuesList();

		for (Ques item : queslist) {

			// 每一個問題都要檢查問題中的quizId和問卷中的Id是否相符
			if (item.getQuizId() != req.getId()) {
				return new BasicRes(ResMessage.QUIZ_ID_NOT_MATCH.getCode(), ResMessage.QUIZ_ID_NOT_MATCH.getMessage());
			}

			if (item.getType().equalsIgnoreCase((SelectType.SINGLE.getType()))
					|| item.getType().equalsIgnoreCase((SelectType.MULTI.getType()))) {
				// 確定單選或多選，選項就必須要有值
				// 前面有驚嘆號代表否定的意思
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		;

		// 檢查此問卷是否存在
		if (!(req.getId() <= 0 || quizDao.existsById(req.getId()))) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		quizDao.save(new Quiz(req.getId(), req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(),
				req.isPublished()));

		// 刪除此張問就所有舊有的問題
		quesDao.deleteByQuizId(req.getId());
		// 新增此張問卷更新後所有的問題
		quesDao.saveAll(req.getQuesList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// ===========================================================
	// ===========================================================

	// 這裡是處理delete
	@Override
	public BasicRes delete(DeleteReq req) {
		// 進行中的問卷不能刪除: 要找出idList不能包含進行中的參數
		// 進行中的問卷條件1.已發布2.當前時間大於等於開始日期3.當前時間小於等於結束日期

		boolean res = quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(//
				req.getQuizIdList(), LocalDate.now(), LocalDate.now());

		// 等於刪除的問卷裡有正在進行中的
		if (res) {
			return new BasicRes(ResMessage.QUIZ_IN_PROGRESS.getCode(), ResMessage.QUIZ_IN_PROGRESS.getMessage());
		}

		quizDao.deleteAllById(req.getQuizIdList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

//===========================================================
//===========================================================	
//這裡是處理search

	@Override
	public SearchRes search(SearchReq req) {

		String quizName = req.getQuizName();
		LocalDate startDate = req.getStartDate();
		LocalDate endDate = req.getEndDate();

		// quizName變為空字串原因是，containing的值代空字串時會撈全部資料
		// 全空白也當成是非搜尋條件之一
		if (!StringUtils.hasText(quizName)) {
			quizName = "";
		}

		if (startDate == null) {
			startDate = LocalDate.of(1900, 1, 1);
		}

		if (endDate == null) {
			endDate = LocalDate.of(2999, 1, 1);
		}

		// ==========================================================================

		List<Quiz> searchQuizRes = quizDao
				.findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName, startDate, endDate);

		// ==========================================================================

		// 方法1:連接資料庫的次數太多，先用quizDao撈quizId，之後再用for迴圈根據quizId撈ques資料

		List<QuizRes> quizResList = new ArrayList<>();
		searchQuizRes.forEach(item -> {
			int quizId = item.getId();
			List<Ques> quesList = quesDao.findByQuizId(quizId);
			// 建立QuizRes放相對的Quiz和對應的List<>
			QuizRes quizRes = new QuizRes();
			quizRes.setId(item.getId());
			quizRes.setName(item.getName());
			quizRes.setDescription(item.getDescription());
			quizRes.setStartDate(item.getStartDate());
			quizRes.setEndDate(item.getEndDate());
			quizRes.setPublished(item.isPublished());
			quizRes.setQuesList(quesList);

			quizResList.add(quizRes);
		});

		// ==========================================================================

		// 方法2 先用quizDao撈quizId，之後再用quesDao一次撈quizIdList的資料

		List<Integer> quizIdList = new ArrayList<>();
		searchQuizRes.forEach(item -> {
			quizIdList.add(item.getId());
		});

		List<Ques> quesList = quesDao.findByQuizIdIn(quizIdList);
		List<QuizRes> quizResList2 = new ArrayList<>();

		searchQuizRes.forEach(quizItem -> {
			List<Ques> finalQuesList = new ArrayList<>();
			quesList.forEach(quesItem -> {
				if (quizItem.getId() == quesItem.getQuizId()) {
					finalQuesList.add(quesItem);
				}
			});

			QuizRes quizRes = new QuizRes();
			quizRes.setId(quizItem.getId());
			quizRes.setName(quizItem.getName());
			quizRes.setDescription(quizItem.getDescription());
			quizRes.setStartDate(quizItem.getStartDate());
			quizRes.setEndDate(quizItem.getEndDate());
			quizRes.setPublished(quizItem.isPublished());
			quizRes.setQuesList(finalQuesList);
			quizResList2.add(quizRes);
		});

		return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quizResList2, quizIdList);
	}

	// ===========================================================
	// ===========================================================
	// 這裡是處理fillin

	@Override
	public BasicRes fillin(FillingReq req) {

		// 先檢查req中的List<Feedback>，所有Feedback的quizId和email都是一樣的
		// 就是quizId和email只會有一張
		List<Feedback> feedbackList = req.getFeedbackList();
		Set<Integer> quizIdSet = new HashSet<>();
		Set<String> emailSet = new HashSet<>();
		for (Feedback feedback : feedbackList) {
			// 第1步檢查先加入set以利迴圈外面判斷
			quizIdSet.add(feedback.getQuizId());
			emailSet.add(feedback.getEmail());
		}

		// 第1步
		// 因為Set的值不會有重複，所以檢查送過來的問卷的quizId 和 email 的set大小是否是超過1個
		// 所以兩個set的大小都不是1的話就表示quizId或email不一致
		// 檢查Feedback中所有quizId email都是一樣的
		if (quizIdSet.size() != 1 || emailSet.size() != 1) {
			return new BasicRes(ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getCode(),
					ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getMessage());
		}

		// 第2步
		// 檢查順便判斷同一個quizId和Email是否存在於database
		// 檢查同一個email+quizId是否已存在(同一個email已田過同一張問卷)
		int quizId = req.getFeedbackList().get(0).getQuizId();

		if (feedbackDao.existsByQuizIdAndEmail(quizId, feedbackList.get(0).getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATE.getCode(), ResMessage.EMAIL_DUPLICATE.getMessage());
		}

		// 第3步
		// 檢查判斷是否可填寫狀態，1.已發布 2.填寫時間大於startDate 3.填寫時間小於endDate
		if (!quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
				List.of(feedbackList.get(0).getQuizId()), LocalDate.now(), LocalDate.now())) {

			return new BasicRes(ResMessage.CANNOT_FIILIN_QUIZ.getCode(), ResMessage.CANNOT_FIILIN_QUIZ.getMessage());
		}

		// 第4步
		// 檢查必填時，要有答案
		// 檢查單選時不能有多個答案
		// 檢查非文字時，答案和選項要一致
		List<Ques> quesList = quesDao.findByQuizId(quizId);

		// 檢查問題和答案筆數是否一樣
		if (feedbackList.size() != quesList.size()) {
			return new BasicRes(ResMessage.FIILIN_INCOMPLETE.getCode(), //
					ResMessage.FIILIN_INCOMPLETE.getMessage());
		}

		// 先蒐集必填的quId、單選的quId、問題的編號
		Set<Integer> necessaryQuIds = new HashSet<>();
		Set<Integer> singleQuIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Ques quesItem : quesList) {
			quIds.add(quesItem.getId());
			if (quesItem.isNecessary()) {
				necessaryQuIds.add(quesItem.getId());
			}
			if (quesItem.getType().equalsIgnoreCase(SelectType.SINGLE.getType())) {
				singleQuIds.add(quesItem.getId());
			}
		}

		// 檢查單選時不能有多個答案
		// 檢查必填時要有答案
		// 先將題號與對應的答案蒐集，在來後續筆對答案與選項用
		Map<Integer, List<String>> quIdAnsMap = new HashMap<>();

		for (Feedback feedback : feedbackList) {
			int quId = feedback.getQuId();
			// 檢查答案的題號是否和問題題號相符
			if (!quIds.contains(quId)) { // 前面有驚嘆號代表答案題號不包含在問題題號中
				return new BasicRes(ResMessage.QUID_MISMATCH.getCode(), ResMessage.QUID_MISMATCH.getMessage());
			}
			// 檢查必填且要有答案
			// 必填但沒答案
			if (necessaryQuIds.contains(quId) && !StringUtils.hasText(feedback.getAns())) {
				return new BasicRes(ResMessage.FIILININ_IS_NECESSARY.getCode(),
						ResMessage.FIILININ_IS_NECESSARY.getMessage());
			}
			// 檢查單選時不能有多個答案
			List<String> ansList = List.of(feedback.getAns().split(";"));// 切出來是陣列，把陣列轉成list
			if (singleQuIds.contains(quId) && ansList.size() > 1) {
				return new BasicRes(ResMessage.QUES_IS_SINGLE_CHOICE.getCode(),
						ResMessage.QUES_IS_SINGLE_CHOICE.getMessage());
			}
			// 先將題號與對應的答案蒐集，在來後續筆對答案與選項用
			quIdAnsMap.put(quId, ansList);
		}

		// 檢查非文字時，答案和選項要一致
		// 檢查單選多選時，答案要有在選項裡面
		for (Ques item : quesList) {
			String type = item.getType();
			if (!type.equalsIgnoreCase(SelectType.TEXT.getType())) {
				int quId = item.getId();
				// quIdAnsMap.get(item.getId()): 是根據key取得map中對應的value
				List<String> ansList = quIdAnsMap.get(quId); // 根據key取value
				List<String> optionList = List.of(item.getOptions().split(";"));// 切出來後轉成List

				// 相同的問題，判斷答案是否與選項一致
				if (!optionList.containsAll(ansList) && ansList.isEmpty()) {
					return new BasicRes(ResMessage.OPTION_ANSWER_MISMATCH.getCode(),
							ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
				}
			}
		}

		feedbackDao.saveAll(feedbackList);

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		// 先從ques撈取非文字類型的問題來統計
		Optional<Quiz> op = quizDao.findById(quizId);
		if (op.isEmpty()) {
			return new StatisticsRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		String quizName = quiz.getName();

		// 先從 ques撈取非文字類型的問題(文字類型的問題不列入統計)
		List<Ques> quesList = quesDao.findByQuizIdAndTypeNot(quizId, SelectType.TEXT.getType());
		// 蒐集單選或多選問題的Id
		List<Integer> quIdList = new ArrayList<>();
		// 先收集問題編號和選項，用來確認feedback中的答案一定會與問題選項一致，避免feedback中的選項沒出現在問卷中的選項
		// 問題編號 //選項 //次數
		Map<Integer, Map<String, Integer>> quIdOptionCountMap = new HashMap<>();
		List<StatisticsVo> statisticsList = new ArrayList<>();
		for (Ques item : quesList) {
			quIdList.add(item.getId());
			Map<String, Integer> optionCountMap = new HashMap<>();
			List<String> optionList = List.of(item.getOptions().split(";"));
			for (String option : optionList) {
				optionCountMap.put(option, 0);
			}
			quIdOptionCountMap.put(item.getId(), optionCountMap);
			StatisticsVo vo = new StatisticsVo();
			vo.setQuId(item.getId());
			vo.setQu(item.getQu());
			statisticsList.add(vo);
		}

		// =================================
		// 上面for迴圈執行後是以下狀況
		// 問題編號 //選項 //次數
		// Map<Integer, Map<String, Integer>>quIdOptionCountMap = new HashMap<>();
		// 1 蛋餅 0
		// 1 三明治 0
		// 1 飯糰 0
		// 1 漢堡 0
		// =================================
		// 避免送到database的選項沒有出現在問卷選項裡

		List<Feedback> feedbackList = feedbackDao.findByQuizIdAndQuIdIn(quizId, quIdList);

		
		for (Feedback feedback: feedbackList){
			//  根據問題編號把選項次數的map從quIdOptionCountMap中取出
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(feedback.getQuId());
			 if (optionCountMap != null) {  // 檢查 optionCountMap 是否為 null
			//  將feedback中ans字串轉乘List
			List<String>ansList = List.of(feedback.getAns().split(";"));
			for (String ans: ansList) {
                if (optionCountMap.containsKey(ans)) {  // 檢查 ans 是否存在於 optionCountMap 中
				//根據選項取出對應的次數
				int count = optionCountMap.get(ans);
				//  將原本次數+1
				count++;
				//  把相同的選項-次數 放回到map中
				optionCountMap.put(ans, count);
			}
		}
			//  更新quIdOptionCountMap中的資料
			quIdOptionCountMap.put(feedback.getQuId(), optionCountMap);
		}
		}
//		for (Feedback feedback : feedbackList) {
//			StatisticsVo vo = new StatisticsVo();
//			Map<String, Integer> optionCountMap = new HashMap<>();
//			List<String> ansList = List.of(feedback.getAns().split(";"));
//			System.out.println(ansList);
//
//			for (String ans : ansList) {
//				// 判斷map中是否已經有選項key
//				if (optionCountMap.containsKey(ans)) {
//					// 選項已經存在於map中，將其對應的次數取出
//					// 將原本的次數+1
//					int count = optionCountMap.get(ans);
//					count++;
//					// 把相同的選項 次數放回到map中
//					optionCountMap.put(ans, count);
//					// 跳過當次迴圈
//					continue;
//				}
//				optionCountMap.put(ans, 1);
//
//			}
//			;
//
//		}
//		;

		for (StatisticsVo statistics : statisticsList) {
			int quId = statistics.getQuId();
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(quId);
			statistics.setOptionCountMap(optionCountMap);
			// 上面3行程式碼可用下面一行表示
			// item.setOptionCountMap(quIdOptionCountMap.get(item.getQuId()));
		}

		return new StatisticsRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quizName,
				statisticsList);

		// =================================
//		for (Feedback feedback: feedbackList){
//			StatisticsVo vo = new StatisticsVo();
//			Map<String, Integer>optionCountMap = new HashMap<>();
//			List<String> ansList = List.of(feedback.getAns().split(","));
//			for(String ans: ansList){
//				//判斷map中是否已經有選項key
//				if(optionCountMap.containsKey(ans)) {
//					//選項已經存在於map中，將其對應的次數取出
//					//將原本的次數+1
//					int count = optionCountMap.get(ans);
//					count++;
//					//把相同的選項 次數放回到map中
//					optionCountMap.put(ans, count);
//					//跳過當次迴圈
//					continue;
//				}
//				optionCountMap.put(ans,1);
//
//			};
//			
//		}; 

	}

	@Override
	public FeedbackRes feedback(int quizId) {
		if (quizId <= 0) {
			return new FeedbackRes(ResMessage.NO_QUIZID.getCode(), ResMessage.NO_QUIZID.getMessage(),
					new ArrayList<>());
		}

		List<Feedback> res = feedbackDao.findByQuizId(quizId);

		return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
	}

}
