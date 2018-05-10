package com.hebeu.ask.service.view.impl;

import com.github.pagehelper.PageHelper;
import com.hebeu.ask.dao.CategoryMapper;
import com.hebeu.ask.dao.QuestionMapper;
import com.hebeu.ask.dao.UserMapper;
import com.hebeu.ask.model.po.*;
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
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询最新问题
     *
     * @return 返回最新问题集合
     */
    @Override
    public List<Question> queryNewQuestion(Integer queryNum) {
        PageHelper.startPage(1, queryNum, "created_at desc");
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdIsNotNull();
        return questionMapper.selectByExample(questionExample);
    }

    /**
     * 查询悬赏问题
     *
     * @param queryNum 查询数量
     * @return 返回悬赏问题集合
     */
    @Override
    public List<Question> queryAwardQuestion(Integer queryNum) {
        PageHelper.startPage(1, queryNum, "created_at desc");
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdIsNotNull();
        criteria.andPriceGreaterThan(Short.parseShort("0"));
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
        questionMapper.updateByExampleSelective(question, questionExample);
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

        // 设置用户姓名和id
        User user = userMapper.selectByPrimaryKey(question.getUserId());
        questionVo.setUserName(user.getName());
        questionVo.setUserId(user.getId());

        return questionVo;
    }

    /**
     * 更新问题回答数
     *
     * @param id 问题id
     */
    @Override
    public void updateAnswers(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        question.setAnswers(question.getAnswers() + 1);

        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdEqualTo(id);

        questionMapper.updateByExampleSelective(question, questionExample);
    }

    /**
     * 查询所有的分类
     *
     * @return 返回分类集合
     */
    @Override
    public List<Category> queryCategory() {
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andIdIsNotNull();
        return categoryMapper.selectByExample(categoryExample);
    }

    /**
     * 保存问题
     *
     * @param question 问题对象
     * @return 返回操作结果
     */
    @Override
    public boolean saveQuestion(Question question) {
        return questionMapper.insertSelective(question) > 0;

    }

    /**
     * 查询未建立索引的问题
     *
     * @return 返回未建立索引的问题集合
     */
    @Override
    public List<Question> queryUnIndexQuestion() {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIndexedEqualTo(Byte.valueOf("0"));
        return questionMapper.selectByExampleWithBLOBs(questionExample);
    }

    /**
     * 根据问题id更新问题索引状态
     *
     * @param questionIds 问题id集合
     */
    @Override
    public void updateIndexed(List<Integer> questionIds) {
        QuestionExample questionExample = new QuestionExample();
        QuestionExample.Criteria criteria = questionExample.createCriteria();
        criteria.andIdIn(questionIds);
        Question question = new Question();
        question.setIndexed(Byte.valueOf("1"));
        questionMapper.updateByExampleSelective(question,questionExample);
    }

}
