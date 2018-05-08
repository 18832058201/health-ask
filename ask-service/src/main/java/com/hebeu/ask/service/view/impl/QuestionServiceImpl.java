package com.hebeu.ask.service.view.impl;

import com.hebeu.ask.dao.QuestionMapper;
import com.hebeu.ask.dao.UserMapper;
import com.hebeu.ask.model.po.Question;
import com.hebeu.ask.model.po.QuestionExample;
import com.hebeu.ask.model.po.User;
import com.hebeu.ask.model.vo.QuestionVo;
import com.hebeu.ask.service.view.QuestionService;
import com.hebeu.ask.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : chenDeHua
 * Time   : 2018/3/10 下午7:22
 * Desc   :
 **/
@Service
public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有的问题
     *
     * @return 返回所有的问题集合
     */
    @Override
    public List<Question> queryAllQuestion() {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdIsNotNull();
        return questionMapper.selectByExample(questionExample);
    }

    /**
     * 更新浏览次数
     *
     * @param id 问题id
     */
    @Override
    public void updateViews(Integer id, Question question) {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdEqualTo(id);
        question.setViews(question.getViews() + 1);
        questionMapper.updateByExampleSelective(question,questionExample);
    }


    /**
     * 根据id查询问题详情
     *
     * @param id 问题id
     * @return 返回查询的问题详情
     */
    @Override
    public QuestionVo queryDetailById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);

        QuestionVo questionVo = new QuestionVo();
        BeanUtils.copyProperties(question, questionVo);
        questionVo.setCreateTime(DateUtil.dateToString(DateUtil.yyyy_MM_dd_HH_mm_ss, question.getCreatedAt()));

        // 设置用户姓名
        User user = userMapper.selectByPrimaryKey(question.getUserId());
        questionVo.setUserName(user.getName());

        return questionVo;
    }

}
