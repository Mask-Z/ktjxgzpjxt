package com.aixuexiao.service;

import com.aixuexiao.model.Article;
import com.aixuexiao.model.Message;
import com.aixuexiao.model.Reply;
import com.aixuexiao.model.Student;
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
    public  String  createPic(Student student, Message message)  {

        // 发送方帐号（open_id）
//        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
//        String toUserName = requestMap.get("ToUserName");
        NewsMessage nmessage=new NewsMessage();
		nmessage.setToUserName(message.getFromUserName());
		nmessage.setFromUserName(message.getToUserName());
		nmessage.setCreateTime(new Date().getTime());
		nmessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
		nmessage.setFuncFlag(0);
        List<MyArticle> articleList = new ArrayList<>();
        MyArticle article=new MyArticle();
        article.setTitle("考试成绩");
        article.setDescription(student.getClassid()+"班级成绩");
        article.setPicUrl("http://localhost:8080/aixuexiao/assets/img/bg1.jpg");
//        article.setPicUrl("http://5afbaf5d.ngrok.io/aixuexiao/assets/img/bg1.jpg");
        article.setUrl("http://localhost:8080/aixuexiao/changda/echats/?classid=" + student.getClassid());
//        article.setUrl("http://5afbaf5d.ngrok.io/aixuexiao/changda/echats");
        articleList.add(article);
		nmessage.setArticleCount(articleList.size());
		nmessage.setArticles(articleList);
        return MessageUtil.newsMessageToXml(nmessage);

    }


//	public  String getImgResponse(Student student, Message message){
//		String back="";
//		int classid = student.getClassid();
//		MyLogger.info("classid  "+classid + "");
//		Reply reply = new Reply();
//		reply.setToUserName(message.getFromUserName());
//		reply.setFromUserName(message.getToUserName());
//		reply.setCreateTime(new Date());
//		reply.setMsgType(Reply.NEWS);
//		List<Article> articleList = new ArrayList<>();
//		Article article = new Article();
//		article.setTitle("考试成绩");
//		article.setDescription(classid + "班英语成绩");
//		article.setPicUrl("http://localhost:8080/aixuexiao/assets/img/bg1.jpg");
//		article.setUrl("http://localhost:8080/aixuexiao/changda/echats?classid=" + classid);
//		articleList.add(article);
//		reply.setArticleCount(articleList.size());
//		reply.setArticles(articleList);
//		reply.setContent("");
//		reply.setFuncFlag(0);
//		//weixinService.addReply(reply);//保存回复消息到数据库
//		back = WeixinUtil.replyNewsToXml(reply);
////		back = WeixinUtil.replyToXml(reply);
////        back= MessageUtil.newsMessageToXml(reply);
////        }
//		MyLogger.info(back);
//		return back;
//	}

//    public  String  createPic2(Map<String, String> requestMap)  {
//
//        // 发送方帐号（open_id）
//        String fromUserName = requestMap.get("FromUserName");
//        // 公众帐号
//        String toUserName = requestMap.get("ToUserName");
//        // 消息类型
////        String msgType = requestMap.get("MsgType");
//        Reply reply=new Reply();
//        reply.setToUserName(fromUserName);
//        reply.setFromUserName(toUserName);
//        reply.setCreateTime(new Date());
//        reply.setMsgType(Reply.NEWS);
//        List<Article> articleList=new ArrayList<>();
//        Article article=new Article();
//        article.setTitle("考试成绩");
//        article.setDescription("301班英语成绩");
//        article.setPicUrl("http://localhost:8080/aixuexiao/assets/img/bg1.jpg");
//        article.setUrl("http://localhost:8080/aixuexiao/changda/echats");
//        articleList.add(article);
//        reply.setArticleCount(articleList.size());
//        reply.setArticles(articleList);
//        return MessageUtil.newsMessageToXml(reply);
//
//    }
}
