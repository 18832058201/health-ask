package com.hebeu.ask.service.view.impl;

import com.hebeu.ask.dao.AnswerMapper;
import com.hebeu.ask.model.po.Answer;
import com.hebeu.ask.model.po.AnswerExample;
import com.hebeu.ask.model.vo.AnswerVo;
import com.hebeu.ask.service.view.AnswerService;
import com.hebeu.ask.util.DateUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : chenDeHua
 * Time   : 2018/5/8 下午7:36
 * Desc   :
 **/

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;
    /**
     * 根据问题id查询对应的答案集合和最佳答案
     *
     * @param questionId 问题id
     * @return 返回答案集合
     */
    @Override
    public Pair<List<AnswerVo>, AnswerVo> queryAnswerListByQuestionId(Integer questionId) {
        AnswerExample answerExample = new AnswerExample();
        AnswerExample.Criteria criteria = answerExample.createCriteria();
        criteria.andQuestionIdEqualTo(questionId);
        // 查询得到所有的回答
        List<Answer> answerList = answerMapper.selectByExampleWithBLOBs(answerExample);
        // 最佳答案
        Answer bestAnswer = answerList.stream().filter(answer -> answer.getAdoptedAt() != null).findFirst().orElse(null);

        // 其他回答
        List<AnswerVo> otherAnswerList = new ArrayList<>();
        AnswerVo bestAnswerVo = null;
        if (bestAnswer != null) {
            bestAnswerVo = answerToAnswerVo(bestAnswer);
            answerList.iterator().forEachRemaining(answer -> {
                if (!answer.getId().equals(bestAnswer.getId())){
                    otherAnswerList.add(answerToAnswerVo(answer));
                }
            });
        } else {
            // 遍历回答集合得到其他回答
            answerList.iterator().forEachRemaining(answer -> {
                otherAnswerList.add(answerToAnswerVo(answer));
            });
        }
        return Pair.of(otherAnswerList, bestAnswerVo);
    }


    /**
     * 将answer对象转成answerVo 对象
     *
     * @param answer 回答对象
     * @return 返回vo对象
     */
    @Override
    public AnswerVo answerToAnswerVo(Answer answer) {
        if (answer == null) {
            return null;
        }
        AnswerVo answerVo = new AnswerVo();
        BeanUtils.copyProperties(answer, answerVo);
        if (answer.getAdoptedAt() != null){
            answerVo.setAdoptedTime(DateUtil.dateToString(DateUtil.yyyy_MM_dd_HH_mm_ss, answerVo.getAdoptedAt()));
        }
        if (answer.getCreatedAt() != null){
            answerVo.setCreateTime(DateUtil.dateToString(DateUtil.yyyy_MM_dd_HH_mm_ss, answerVo.getCreatedAt()));
        }
        if (answer.getUpdatedAt() != null){
            answerVo.setUpdateTime(DateUtil.dateToString(DateUtil.yyyy_MM_dd_HH_mm_ss, answerVo.getUpdatedAt()));
        }
        return answerVo;
    }
}
