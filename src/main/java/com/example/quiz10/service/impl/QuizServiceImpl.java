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

	// �o�̬O�B�zcreate
	// @Transactional import �� library�A javax �M springframework ���i�H�ϥ�
	// ��̮t���i�ѷ� PPT spring boot_02 @Transactional ����
	@Transactional
	@Override
	public BasicRes create(CreateUpdateReq req) {

		// �򥻪��ݩʤw�g���@Valid
		// �}�l�ɶ�����񵲧��ɶ���
		if (req.getStartDate().isAfter(req.getStartDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}

		List<Ques> queslist = req.getQuesList();

		for (Ques item : queslist) {

			if (item.getType().equalsIgnoreCase((SelectType.SINGLE.getType()))
					|| item.getType().equalsIgnoreCase((SelectType.MULTI.getType()))) {

				// �T�w���Φh��A�ﶵ�N�����n����
				// �e������ĸ��N��_�w���N��
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		;

		Quiz res = quizDao.save(
				new Quiz(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished()));

		// ����saveAll�|�����D�A��Ʈw��quiz_id���|���ȡA�ҥH�n��W�����檺�^�ǭȱ��^�Ө��o�̭���quiz_id
		// �]��Quiz����id�OAi�۰ʥͦ����y�����A�n��quizDao����save��i�H���id���Ȧ^��
		// �����n�bQuiz��Entity���N��ƫ��A��int���ݩ�Id�[�W@GeneratedValue(strategy =
		// GenerationType.IDENTITY)
		queslist.forEach(item -> {
			item.setQuizId(res.getId());
		});
		quesDao.saveAll(req.getQuesList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

	}
//===========================================================
//===========================================================

	// �o�̬O�B�zupdate
	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {

		// �򥻪��ݩʤw�g���@Valid
		// �}�l�ɶ�����񵲧��ɶ���

		if (req.getStartDate().isAfter(req.getStartDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}

		List<Ques> queslist = req.getQuesList();

		for (Ques item : queslist) {

			// �C�@�Ӱ��D���n�ˬd���D����quizId�M�ݨ�����Id�O�_�۲�
			if (item.getQuizId() != req.getId()) {
				return new BasicRes(ResMessage.QUIZ_ID_NOT_MATCH.getCode(), ResMessage.QUIZ_ID_NOT_MATCH.getMessage());
			}

			if (item.getType().equalsIgnoreCase((SelectType.SINGLE.getType()))
					|| item.getType().equalsIgnoreCase((SelectType.MULTI.getType()))) {
				// �T�w���Φh��A�ﶵ�N�����n����
				// �e������ĸ��N��_�w���N��
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		;

		// �ˬd���ݨ��O�_�s�b
		if (!(req.getId() <= 0 || quizDao.existsById(req.getId()))) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		quizDao.save(new Quiz(req.getId(), req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(),
				req.isPublished()));

		// �R�����i�ݴN�Ҧ��¦������D
		quesDao.deleteByQuizId(req.getId());
		// �s�W���i�ݨ���s��Ҧ������D
		quesDao.saveAll(req.getQuesList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// ===========================================================
	// ===========================================================

	// �o�̬O�B�zdelete
	@Override
	public BasicRes delete(DeleteReq req) {
		// �i�椤���ݨ�����R��: �n��XidList����]�t�i�椤���Ѽ�
		// �i�椤���ݨ�����1.�w�o��2.��e�ɶ��j�󵥩�}�l���3.��e�ɶ��p�󵥩󵲧����

		boolean res = quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(//
				req.getQuizIdList(), LocalDate.now(), LocalDate.now());

		// ����R�����ݨ��̦����b�i�椤��
		if (res) {
			return new BasicRes(ResMessage.QUIZ_IN_PROGRESS.getCode(), ResMessage.QUIZ_IN_PROGRESS.getMessage());
		}

		quizDao.deleteAllById(req.getQuizIdList());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

//===========================================================
//===========================================================	
//�o�̬O�B�zsearch

	@Override
	public SearchRes search(SearchReq req) {

		String quizName = req.getQuizName();
		LocalDate startDate = req.getStartDate();
		LocalDate endDate = req.getEndDate();

		// quizName�ܬ��Ŧr���]�O�Acontaining���ȥN�Ŧr��ɷ|���������
		// ���ťդ]���O�D�j�M���󤧤@
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

		// ��k1:�s����Ʈw�����ƤӦh�A����quizDao��quizId�A����A��for�j��ھ�quizId��ques���

		List<QuizRes> quizResList = new ArrayList<>();
		searchQuizRes.forEach(item -> {
			int quizId = item.getId();
			List<Ques> quesList = quesDao.findByQuizId(quizId);
			// �إ�QuizRes��۹諸Quiz�M������List<>
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

		// ��k2 ����quizDao��quizId�A����A��quesDao�@����quizIdList�����

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
	// �o�̬O�B�zfillin

	@Override
	public BasicRes fillin(FillingReq req) {

		// ���ˬdreq����List<Feedback>�A�Ҧ�Feedback��quizId�Memail���O�@�˪�
		// �N�OquizId�Memail�u�|���@�i
		List<Feedback> feedbackList = req.getFeedbackList();
		Set<Integer> quizIdSet = new HashSet<>();
		Set<String> emailSet = new HashSet<>();
		for (Feedback feedback : feedbackList) {
			// ��1�B�ˬd���[�Jset�H�Q�j��~���P�_
			quizIdSet.add(feedback.getQuizId());
			emailSet.add(feedback.getEmail());
		}

		// ��1�B
		// �]��Set���Ȥ��|�����ơA�ҥH�ˬd�e�L�Ӫ��ݨ���quizId �M email ��set�j�p�O�_�O�W�L1��
		// �ҥH���set���j�p�����O1���ܴN���quizId��email���@�P
		// �ˬdFeedback���Ҧ�quizId email���O�@�˪�
		if (quizIdSet.size() != 1 || emailSet.size() != 1) {
			return new BasicRes(ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getCode(),
					ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getMessage());
		}

		// ��2�B
		// �ˬd���K�P�_�P�@��quizId�MEmail�O�_�s�b��database
		// �ˬd�P�@��email+quizId�O�_�w�s�b(�P�@��email�w�йL�P�@�i�ݨ�)
		int quizId = req.getFeedbackList().get(0).getQuizId();

		if (feedbackDao.existsByQuizIdAndEmail(quizId, feedbackList.get(0).getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATE.getCode(), ResMessage.EMAIL_DUPLICATE.getMessage());
		}

		// ��3�B
		// �ˬd�P�_�O�_�i��g���A�A1.�w�o�� 2.��g�ɶ��j��startDate 3.��g�ɶ��p��endDate
		if (!quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
				List.of(feedbackList.get(0).getQuizId()), LocalDate.now(), LocalDate.now())) {

			return new BasicRes(ResMessage.CANNOT_FIILIN_QUIZ.getCode(), ResMessage.CANNOT_FIILIN_QUIZ.getMessage());
		}

		// ��4�B
		// �ˬd����ɡA�n������
		// �ˬd���ɤ��঳�h�ӵ���
		// �ˬd�D��r�ɡA���שM�ﶵ�n�@�P
		List<Ques> quesList = quesDao.findByQuizId(quizId);

		// �ˬd���D�M���׵��ƬO�_�@��
		if (feedbackList.size() != quesList.size()) {
			return new BasicRes(ResMessage.FIILIN_INCOMPLETE.getCode(), //
					ResMessage.FIILIN_INCOMPLETE.getMessage());
		}

		// ���`������quId�B��諸quId�B���D���s��
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

		// �ˬd���ɤ��঳�h�ӵ���
		// �ˬd����ɭn������
		// ���N�D���P���������׻`���A�b�ӫ��򵧹ﵪ�׻P�ﶵ��
		Map<Integer, List<String>> quIdAnsMap = new HashMap<>();

		for (Feedback feedback : feedbackList) {
			int quId = feedback.getQuId();
			// �ˬd���ת��D���O�_�M���D�D���۲�
			if (!quIds.contains(quId)) { // �e������ĸ��N�����D�����]�t�b���D�D����
				return new BasicRes(ResMessage.QUID_MISMATCH.getCode(), ResMessage.QUID_MISMATCH.getMessage());
			}
			// �ˬd����B�n������
			// ������S����
			if (necessaryQuIds.contains(quId) && !StringUtils.hasText(feedback.getAns())) {
				return new BasicRes(ResMessage.FIILININ_IS_NECESSARY.getCode(),
						ResMessage.FIILININ_IS_NECESSARY.getMessage());
			}
			// �ˬd���ɤ��঳�h�ӵ���
			List<String> ansList = List.of(feedback.getAns().split(";"));// ���X�ӬO�}�C�A��}�C�নlist
			if (singleQuIds.contains(quId) && ansList.size() > 1) {
				return new BasicRes(ResMessage.QUES_IS_SINGLE_CHOICE.getCode(),
						ResMessage.QUES_IS_SINGLE_CHOICE.getMessage());
			}
			// ���N�D���P���������׻`���A�b�ӫ��򵧹ﵪ�׻P�ﶵ��
			quIdAnsMap.put(quId, ansList);
		}

		// �ˬd�D��r�ɡA���שM�ﶵ�n�@�P
		// �ˬd���h��ɡA���׭n���b�ﶵ�̭�
		for (Ques item : quesList) {
			String type = item.getType();
			if (!type.equalsIgnoreCase(SelectType.TEXT.getType())) {
				int quId = item.getId();
				// quIdAnsMap.get(item.getId()): �O�ھ�key���omap��������value
				List<String> ansList = quIdAnsMap.get(quId); // �ھ�key��value
				List<String> optionList = List.of(item.getOptions().split(";"));// ���X�ӫ��নList

				// �ۦP�����D�A�P�_���׬O�_�P�ﶵ�@�P
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
		// ���qques�����D��r���������D�Ӳέp
		Optional<Quiz> op = quizDao.findById(quizId);
		if (op.isEmpty()) {
			return new StatisticsRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		String quizName = quiz.getName();

		// ���q ques�����D��r���������D(��r���������D���C�J�έp)
		List<Ques> quesList = quesDao.findByQuizIdAndTypeNot(quizId, SelectType.TEXT.getType());
		// �`�����Φh����D��Id
		List<Integer> quIdList = new ArrayList<>();
		// ���������D�s���M�ﶵ�A�ΨӽT�{feedback�������פ@�w�|�P���D�ﶵ�@�P�A�קKfeedback�����ﶵ�S�X�{�b�ݨ������ﶵ
		// ���D�s�� //�ﶵ //����
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
		// �W��for�j������O�H�U���p
		// ���D�s�� //�ﶵ //����
		// Map<Integer, Map<String, Integer>>quIdOptionCountMap = new HashMap<>();
		// 1 �J�� 0
		// 1 �T���v 0
		// 1 ���{ 0
		// 1 �~�� 0
		// =================================
		// �קK�e��database���ﶵ�S���X�{�b�ݨ��ﶵ��

		List<Feedback> feedbackList = feedbackDao.findByQuizIdAndQuIdIn(quizId, quIdList);

		
		for (Feedback feedback: feedbackList){
			//  �ھڰ��D�s����ﶵ���ƪ�map�qquIdOptionCountMap�����X
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(feedback.getQuId());
			 if (optionCountMap != null) {  // �ˬd optionCountMap �O�_�� null
			//  �Nfeedback��ans�r���୼List
			List<String>ansList = List.of(feedback.getAns().split(";"));
			for (String ans: ansList) {
                if (optionCountMap.containsKey(ans)) {  // �ˬd ans �O�_�s�b�� optionCountMap ��
				//�ھڿﶵ���X����������
				int count = optionCountMap.get(ans);
				//  �N�쥻����+1
				count++;
				//  ��ۦP���ﶵ-���� ��^��map��
				optionCountMap.put(ans, count);
			}
		}
			//  ��squIdOptionCountMap�������
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
//				// �P�_map���O�_�w�g���ﶵkey
//				if (optionCountMap.containsKey(ans)) {
//					// �ﶵ�w�g�s�b��map���A�N����������ƨ��X
//					// �N�쥻������+1
//					int count = optionCountMap.get(ans);
//					count++;
//					// ��ۦP���ﶵ ���Ʃ�^��map��
//					optionCountMap.put(ans, count);
//					// ���L���j��
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
			// �W��3��{���X�i�ΤU���@����
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
//				//�P�_map���O�_�w�g���ﶵkey
//				if(optionCountMap.containsKey(ans)) {
//					//�ﶵ�w�g�s�b��map���A�N����������ƨ��X
//					//�N�쥻������+1
//					int count = optionCountMap.get(ans);
//					count++;
//					//��ۦP���ﶵ ���Ʃ�^��map��
//					optionCountMap.put(ans, count);
//					//���L���j��
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
