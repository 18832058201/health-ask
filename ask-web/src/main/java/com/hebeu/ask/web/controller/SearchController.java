package com.hebeu.ask.web.controller;

import com.hebeu.ask.model.po.Question;
import com.hebeu.ask.model.vo.AnswerVo;
import com.hebeu.ask.model.vo.QuestionVo;
import com.hebeu.ask.service.view.AnswerService;
import com.hebeu.ask.service.view.QuestionService;
import com.hebeu.ask.service.view.SearchEnginesService;
import com.hebeu.ask.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author : chenDeHua
 * Time   : 2018/5/6 下午6:02
 * Desc   : 搜索控制器
 **/

@Controller
@Slf4j
@RequestMapping("/")
public class SearchController {

    @Autowired
    private SearchEnginesService searchEnginesService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    /**
     * 检索接口
     *
     * @param keywords 检索关键词
     * @param pageNum  查看页码数
     * @param pageSize 查看页码大小
     * @param model    视图对象
     * @return 返回搜索页
     */
    @RequestMapping(path = "search")
    public String search(String keywords, Integer pageNum, Integer pageSize, Model model) {
        // 设置pageNum 和 pageSize的默认值
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        Pair<List<QuestionVo>, Integer> pair = searchEnginesService.search(keywords, pageNum, pageSize);
        log.debug("查询结果总数为：{}", pair.getRight());
        model.addAttribute("questionList", pair.getLeft());
        model.addAttribute("keywords", keywords);
        model.addAttribute("count", pair.getRight());
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageHtml", PageUtil.getPageHtml(pageNum, pageSize, pair.getRight(), "/search?keywords="+ keywords));
        return "view/search/index";
    }

    /**
     * 查询问题详情接口
     *
     * @param id    问题id
     * @param model 视图对象
     * @return 返回问题详情
     */
    @RequestMapping(path = "detail")
    public String queryDetail(Integer id, Model model) {
        if (id == null) {
            log.warn("参数不能为空");
            return null;
        }
        QuestionVo question = questionService.queryDetailById(id);

        // 更新浏览次数
        questionService.updateViews(id, question);
        Pair<List<AnswerVo>, AnswerVo> answerPair = answerService.queryAnswerListByQuestionId(id);
        log.debug("question:{}", question.toString());

        model.addAttribute("question", question);
//        model.addAttribute("category", )
        model.addAttribute("otherAnswerList", answerPair.getLeft());
        model.addAttribute("otherAnswerSize", answerPair.getLeft().size());
        model.addAttribute("bestAnswer", answerPair.getRight());
        return "view/question/detail";
    }

}
