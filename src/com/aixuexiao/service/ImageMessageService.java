package com.aixuexiao.service;

import com.aixuexiao.model.Article;
import com.aixuexiao.resopnseMessage.MyArticle;
import com.aixuexiao.util.*;
import com.aixuexiao.resopnseMessage.NewsMessage;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/10 0010.
 */
@Service("imageMessageService")
public class ImageMessageService {
    /**
     *   创建图文消息
     */
    public  String  createPic(Map<String, String> requestMap)  {
        // xml请求解析
//        Map<String, String> requestMap = null;
//        try {
//            requestMap = MessageUtil.parseXml(request);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 发送方帐号（open_id）
        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
//        String msgType = requestMap.get("MsgType");

        NewsMessage message=new NewsMessage();
        message.setToUserName(fromUserName);
        message.setFromUserName(toUserName);
        message.setCreateTime(new Date().getTime());
        message.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
        message.setFuncFlag(0);
        List<MyArticle> articleList = new ArrayList<>();
        MyArticle article=new MyArticle();
        article.setTitle("考试成绩");
        article.setDescription("301班英语成绩");
//        article.setPicUrl("http://localhost:8080/aixuexiao/assets/img/bg1.jpg");
        article.setPicUrl("http://5afbaf5d.ngrok.io/aixuexiao/assets/img/bg1.jpg");
//        article.setUrl("http://localhost:8080/aixuexiao/changda/echats");
        article.setUrl("http://5afbaf5d.ngrok.io/aixuexiao/changda/echats");
        articleList.add(article);
        message.setArticleCount(articleList.size());
        message.setArticles(articleList);
        return MessageUtil.newsMessageToXml(message);

    }
}
