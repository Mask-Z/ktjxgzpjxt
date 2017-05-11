package com.aixuexiao.web.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.aixuexiao.dao.StudentDao;
import com.aixuexiao.dao.StudentMessageDao;
import com.aixuexiao.model.*;
import com.aixuexiao.service.ImageMessageService;
import com.aixuexiao.service.StudentService;
import com.aixuexiao.util.MyLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aixuexiao.service.WeixinService;
import com.aixuexiao.util.WeixinUtil;

@Controller()
public class WeixinController {

    private static final String TOKEN = "weixinCourse";

    public static int pagesize = 10;

    @Resource(name = "weixinService")
    private WeixinService weixinService;

    @Resource(name = "studentService")
    private StudentService studentService;

    @Resource(name = "studentMessageDao")
    private StudentMessageDao studentMessageDao;

    @Resource(name = "studentDao")
    private StudentDao studentDao;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String test(HttpServletRequest request) {
        return weixinService.getStudentMessageHistoryByStudentId(30202);
    }

    /**
     * 根据微信号判断是否关联过学号
     *
     * @param fromUserName
     * @param studentid
     * @param process
     * @return
     */

    public boolean isConnected(String fromUserName, int studentid, String process) {
        Student student = studentDao.findStudentById(studentid);
        if (null != student.getFromusername() && student.getFromusername().equals(fromUserName)) {
            MyLogger.info("恭喜!😄遍历到微信号: " + student.getFromusername());
            return true;
        }
        if (process.equals("绑定")) {
            student.setFromusername(fromUserName);
            student.setFlag(1);
            studentDao.updateStudent(student);
        }
        MyLogger.info("未遍历到该微信号");
        return false;
    }

    //接收微信公众号接收的消息，处理后再做相应的回复
    @RequestMapping(value = "/weixin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String replyMessage(HttpServletRequest request) {
        //仅处理微信服务端发的请求
        if (checkWeixinReques(request)) {
            Map<String, String> requestMap = WeixinUtil.parseXml(request);
            Message message = WeixinUtil.mapToMessage(requestMap);
            weixinService.addMessage(message);//保存接受消息到数据库
            String replyContent = Reply.WELCOME_CONTENT;
            String type = message.getMsgType();
            //获取微信用户id
            String fromUserName = message.getFromUserName();
            //如果微信号已关联过学号

            //判断
            if (type.equals(Message.TEXT)) {//仅处理文本回复内容
                String content = message.getContent();//消息内容
                String[] cs = content.split("_");//消息内容都以下划线_分隔
                if (cs.length == 2) {
                    try {
                        int studentid = Integer.parseInt(cs[0]);//学生编号
                        String process = cs[1];//操作
                        if (isConnected(fromUserName, studentid, process)) {
                            Student student = studentDao.findStudentById(studentid);
                            if (student.getFlag() == 1) {
                                student.setFlag(2);
                                studentDao.updateStudent(student);
                                replyContent = "绑定学号成功!\n\n\n" + Reply.ERROR_CONTENT;
                            } else if ("图文".equals(process)) {
                                //根据学号来获取班级号
                                if (null == student) {
                                    MyLogger.info("未查到该班级");
                                    replyContent = Reply.ERROR_CONTENT;
                                } else {
                                    int classid = student.getClassid();
                                    MyLogger.info(classid + "");
                                    Reply reply = new Reply();
                                    reply.setToUserName(message.getFromUserName());
                                    reply.setFromUserName(message.getToUserName());
                                    reply.setCreateTime(new Date());
                                    reply.setMsgType(Reply.NEWS);
                                    List<Article> articleList = new ArrayList<>();
                                    Article article = new Article();
                                    article.setTitle("考试成绩");
                                    article.setDescription(classid + "班英语成绩");
                                    article.setPicUrl("http://localhost:8080/aixuexiao/assets/img/bg1.jpg");
                                    article.setUrl("http://localhost:8080/aixuexiao/changda/echats?classid=" + classid);
                                    articleList.add(article);
                                    reply.setArticleCount(articleList.size());
                                    reply.setArticles(articleList);
                                    reply.setContent("");
                                    weixinService.addReply(reply);//保存回复消息到数据库
                                    String back = WeixinUtil.replyToXml(reply);
                                    MyLogger.info(back);
                                    return back;
                                }
                            } else {
                                replyContent = getProcess(studentid, process);
                            }
                        } else {
//                            if (!replyContent.startsWith(" "))
                                replyContent = " 你还未绑定学号,请回复以下格式消息绑定学号 : 学号_绑定(如:3011_绑定)\n\n注意 : 一个微信号只能绑定一个学号! 且不可解绑!!!";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        replyContent = Reply.ERROR_CONTENT;
                    }

                }
            }

            //拼装回复消息
            Reply reply = new Reply();
            reply.setToUserName(fromUserName);
            reply.setFromUserName(message.getToUserName());
            reply.setCreateTime(new Date());
            reply.setMsgType(Reply.TEXT);
            reply.setContent(replyContent);
            weixinService.addReply(reply);//保存回复消息到数据库
            //将回复消息序列化为xml形式
            MyLogger.info("序列化前: "+reply.getContent());
            String back = WeixinUtil.replyToXml(reply);
            MyLogger.info("序列化后: "+back);
            return back;
        } else {
            return "error";
        }
    }

    /**
     * 控制逻辑重构
     *
     * @param studentid
     * @param process
     * @return
     */
    public String getProcess(int studentid, String process) {
        String replyContent = "";
        try {
//            studentid = Integer.parseInt(cs[0]);
            if ("考试".equals(process)) {
                replyContent = weixinService.getSingleExamMarkStringByStudentId(studentid);
            } else if ("考试历史".equals(process)) {
                replyContent = weixinService.getExamMarkHistoryStringByStudentId(studentid);
            } else if ("留言".equals(process)) {
                replyContent = weixinService.getSingleStudentMessageByStudentId(studentid);
            } else if ("留言历史".equals(process)) {
                replyContent = weixinService.getStudentMessageHistoryByStudentId(studentid);
            } else if ("动态".equals(process)) {
                replyContent = weixinService.getSingleClassesNewsByStudentId(studentid);
            } else if ("动态历史".equals(process)) {
                replyContent = weixinService.getClassesNewsHistoryByStudentId(studentid);
            } else if ("班级成绩".equals(process)) {
                replyContent = weixinService.test(studentid);
            }

        } catch (NumberFormatException e) {
            replyContent = Reply.ERROR_CONTENT;
        }
        return replyContent;
    }

    //微信公众平台验证url是否有效使用的接口
    @RequestMapping(value = "/weixin", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String initWeixinURL(HttpServletRequest request) {
        String echostr = request.getParameter("echostr");
        if (checkWeixinReques(request) && echostr != null) {
            return echostr;
        } else {
            return "error";
        }
    }


    /**
     * 根据token计算signature验证是否为weixin服务端发送的消息
     */
    private static boolean checkWeixinReques(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        if (signature != null && timestamp != null && nonce != null) {
            String[] strSet = new String[]{TOKEN, timestamp, nonce};
            java.util.Arrays.sort(strSet);
            String key = "";
            for (String string : strSet) {
                key = key + string;
            }
            String pwd = WeixinUtil.sha1(key);
            return pwd.equals(signature);
        } else {
            return false;
        }
    }

    /**
     * 收到消息列表页面
     */
    @RequestMapping(value = "/manager/messages", method = RequestMethod.GET)
    public ModelAndView listMessage(String pagenum) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("sidebar", "messages");
        mv.setViewName("messages");
        int num = 1;
        if (null != pagenum) {
            num = Integer.parseInt(pagenum);
        }
        List<Message> list = weixinService.listMessage((num - 1) * pagesize, pagesize);
        mv.addObject("messageList", list);
        mv.addObject("pagenum", num);
        mv.addObject("length", list.size());
        return mv;
    }


    /**
     * 回复消息列表页面
     */
    @RequestMapping(value = "/manager/replys", method = RequestMethod.GET)
    public ModelAndView listReply(String pagenum) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("sidebar", "replys");
        mv.setViewName("replys");
        int num = 1;
        if (null != pagenum) {
            num = Integer.parseInt(pagenum);
        }
        List<Reply> list = weixinService.listReply((num - 1) * pagesize, pagesize);
        mv.addObject("replyList", list);
        mv.addObject("pagenum", num);
        mv.addObject("length", list.size());
        return mv;
    }


}
