package com.wenjuan.controller;


import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wenjuan.bean.AvailBean;
import com.wenjuan.dao.*;
import com.wenjuan.model.*;
import com.wenjuan.service.*;
import com.wenjuan.utils.*;
import com.wenjuan.vo.ArticleCommentView;
import com.wenjuan.vo.DiaryCommentView;
import com.wenjuan.vo.UserBriefInfo;
import com.wenjuan.vo.UserHx;
import com.wowtour.jersey.api.EasemobChatGroups;
import com.wowtour.jersey.api.EasemobMessages;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO Y
 * 全部为管理员界面！！！
 * 返回值可自行修改
 */
@Controller
@RequestMapping(value = "/admin")
public class ViewAdminController {
    public static String SESSION_KEY_OPERATOR_RECORD = "admin_operation";
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();
    @Autowired
    UserMapper userMapper;
    @Autowired
    GoodMapper goodMapper;
    @Autowired
    HelpMapper helpMapper;
    @Autowired
    DiaryMapper diaryMapper;
    @Autowired
    DiaryCommentMapper diaryCommentMapper;
    @Autowired
    ScoreHistoryMapper scoreHistoryMapper;
    @Autowired
    FeedbackMapper feedbackMapper;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    ArticleCommentMapper articleCommentMapper;
    @Autowired
    UserLoginLogMapper userLoginLogMapper;
    @Autowired
    UserViewDiaryLogMapper userViewDiaryLogMapper;
    @Autowired
    UserViewArticleLogMapper userViewArticleLogMapper;
    @Autowired
    HistoryMapper HistoryMapper;
    @Autowired
    UserFollowArticleMapper userFollowArticleMapper;
    @Autowired
    AdminUndoModifyMapper adminUndoModifyMapper;


    @Autowired
    @Qualifier("userService")
    UserService userService;
    @Autowired
    @Qualifier("notifiesService")
    NotifiesService notifiesService;
    @Autowired
    @Qualifier("diaryService")
    private DiaryService diaryService;
    @Autowired
    @Qualifier("feedbackService")
    private FeedbackService feedbackService;
    @Autowired
    @Qualifier("goodDuihuanService")
    private GoodDuihuanService goodDuihuanService;
    @Autowired
    @Qualifier("userFollowDiaryService")
    private UserFollowDiaryService userFollowDiaryService;
    @Autowired
    @Qualifier("groupService")
    private GroupService groupService;
    @Autowired
    @Qualifier("articleEnableService")
    private ArticleEnableService articleEnableService;
    @Autowired
    @Qualifier("columnService")
    private ColumnService columnService;
    @Autowired
    @Qualifier("columnInfoService")
    private ColumnInfoService columnInfoService;

    //生成不重复随机数
    public static String getRandomFileName() {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        Random random = new Random();
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
        return rannum + str;// 当前时间
    }

    //图片装base64
    static String getImageBinary(String imgPath) {
        //C:\Users\Administrator\Desktop\vc2界面\积分管理-新版.png
        File f = new File("C://Users\\Administrator\\Desktop\\vc2界面\\回复反馈.jpg");
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            return encoder.encodeBuffer(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //base64转图片
    public static boolean GenerateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 未登录时显示登录界面，登录后显示欢迎界面
     * 登录验证采用/admin/login方法
     *
     * @return
     */
    public String index(HttpSession session) {
        User ad = (User) session.getAttribute("adminUser");
        if (ad == null) {
            return "/Login";
        }
        return null;
    }

    /**
     * 管理员用户添加页面，若传入id参数表示修改管理员信息
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/adminInfo", method = RequestMethod.POST)
    public void adminInfo(@RequestParam(required = false, defaultValue = "2") int adminType, @RequestParam(value = "id", required = false) Integer id, HttpServletRequest req, User adminUsers, @RequestParam(value = "pwd1", defaultValue = "0") String pwd1, @RequestParam(value = "pwd2", defaultValue = "0") String pwd2, HttpServletResponse res, HttpSession session) throws IOException, SQLException {
        this.index(session);
        User adUser = User.getCurrentUser();
        JSONObject result = new JSONObject();
        if (adUser.getRole() != 3) {
            res.getWriter().write("role");
            return;
        }


        if (pwd1.equals("0") && pwd2.equals("0")) {

            if (id == null) {
                adminUsers.setNickname(req.getParameter("nickname"));
                adminUsers.setName(req.getParameter("username"));
                adminUsers.setPassword(req.getParameter("password"));

                User user = userMapper.selectByName(req.getParameter("username").toString());
                JSONObject j = new JSONObject();
                if (user != null) {
                    if (user.isAdmin()) {
                        j.put("message", "re");
                    } else {
                        User newUserInfo = new User();
                        newUserInfo.setId(user.getId());
                        newUserInfo.setRole(2);
                        newUserInfo.setExtra("1");
                        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("添加管理员'%s'", user.getName()));
                        userService.updateByPrimaryKey(newUserInfo);
                        j.put("message", "success");
                    }
                    res.getWriter().write(j.toString());

                } else {
                    j.put("message", "success");
                    User u = userMapper.selectByName(req.getParameter("username").toString());
                    if (u != null) {
                        u.setRole(2);

                        u.setPassword(req.getParameter("password"));
                        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改'%s'管理员信息", u.getName()));
                        userMapper.updateByPrimaryKey(u);
                    } else {
                        adminUsers.setRole(adminType);
                        if (adminType == 3) {
                            adminUsers.setExtra("0");
                        } else if (adminType == 2) {
                            adminUsers.setExtra("1");
                        }
                        adminUsers.setAddBackground(true);
                        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("添加管理员'%s'", adminUsers.getName()));
                        userMapper.insert(adminUsers);
                    }
                    //添加附属属性
                    propertyController ec = new propertyController();
                    User us = userMapper.selectByName(req.getParameter("username"));
                    ec.insertUser(us.getId().toString());
                    HxUtil hu = new HxUtil();
                    hu.insertUser(adminUsers);
                    res.getWriter().write(j.toString());
                }
            } else {
                adminUsers.setId(id);
                String s = "";
                try {
                    session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改管理员'%s'的信息", adminUsers.getName()));
                    userMapper.updateByPrimaryKey(adminUsers);
                    s = "success";
                } catch (Exception e) {
                    s = "rep";
                }

                res.getWriter().write(s);
            }
        } else {
            User ad = (User) session.getAttribute("adminUser");
            if (ad.getPassword().equals(pwd1)) {
                adminUsers.setRole(adUser.getRole());
                adminUsers.setPassword(pwd2);
                adminUsers.setId(ad.getId());
                session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改管理员'%s'的信息", adminUsers.getName()));
                userMapper.updateByPrimaryKey(adminUsers);
                result.put("message", "success");
            } else {
                result.put("message", "pwderror");
            }
            res.getWriter().write(result.toString());
        }
    }

    /**
     * 管理员列表
     *
     * @throws IOException
     */
    @RequestMapping(value = "/adminList")
    public void adminList(HttpServletRequest req, HttpServletResponse res, @RequestParam(value = "wantpage", defaultValue = "1") String wantpage, Model model, HttpSession session) throws IOException {
        this.index(session);
        JSONObject result = new JSONObject();
        //分页处理
        int pagesize = 10;
        int adminCount = userMapper.adminCount();
        int pageCount = 0;
        if (adminCount > 0 && adminCount <= 10) {
            pageCount = 1;
        } else if (adminCount > 10) {
            pageCount = (adminCount - (adminCount % pagesize)) / pagesize + 1;
        } else {
            pageCount = 0;
        }
        result.put("pageCount", pageCount);
        int wp = Integer.parseInt(wantpage);

        int begin = Integer.parseInt(wantpage) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wp - 1) * pagesize);
        map.put("end", pagesize);
        result.put("nowpage", wantpage);
        req.getSession().setAttribute("nowpage", wantpage);
        req.getSession().setAttribute("pagecount", pageCount);
        List<User> adminUserFY = userService.adminUserList(map);
        result.put("extraList", adminUserFY);
        res.getWriter().write(result.toString());
    }

    /**
     * 用户列表界面
     *
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/userList")
    public ModelAndView userList(@RequestParam(required = false) String key, HttpServletResponse res, @RequestParam(value = "wantpage", defaultValue = "1") String wantpage, ModelMap modelmap, HttpSession session) throws IOException, SQLException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("grps", groupService.getExistsGroup());
        int wp = Integer.parseInt(wantpage);
        int begin = (wp - 1) * 10;
        propertyController pc = new propertyController();
        JSONObject js = pc.info(begin);
        List clist = (List) js.get("cinfo");
        List ilist = (List) js.get("info");
        List cx = (List) js.get("cx");
        int x = 0;
        List allList = new ArrayList();
        int h = 0;
        int l = 0;
        JSONObject jsonObj = new JSONObject();
        for (int i = clist.size() * h; i < ilist.size(); i++) {
            if (l == clist.size()) {
                l = 0;
            }
            String regEx = "date|day|hours|minutes|month|nanos|seconds|time|timezoneOffset|year";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(ilist.get(i).toString());
            boolean rs = m.find();
            if (rs == true) {
                JSONObject createDate = (JSONObject) ilist.get(i);
                Date tempDate = (Date) JSONObject.toBean(createDate, Date.class);
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempDate);
                try {
                    jsonObj.put(clist.get(l), createTime);
                } catch (Exception e) {
                    jsonObj.put(clist.get(l), ilist.get(i));
                }
            } else {
                jsonObj.put(clist.get(l), ilist.get(i));
            }
            if ((i + 1) % clist.size() == 0) {

                allList.add(x, jsonObj);
                x++;
                jsonObj = new JSONObject();
            }
            h++;
            l++;
        }
        JSONArray ja = new JSONArray();
        ja.add(allList);
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelmap.put("idlist", idList);
        modelAndView.addObject("all", allList);
        modelAndView.addObject("clist", cx);
        //用户总数
        JSONObject result = new JSONObject();
        int pagesize = 10;
        int UserCount = userMapper.UserCount();
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % pagesize)) / pagesize + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
       /* List li = this.quanxian();
        modelAndView.addObject("group", li);*/


        modelAndView.addObject("nowpage", wantpage);
        modelAndView.addObject("pagecount", pageCount);
//   	 modelAndView.addObject("time",li);
//     modelAndView.addObject("list",userFY);
        modelAndView.setViewName("/UserList");
        return modelAndView;
    }

    @RequestMapping("/UserSeach")
    public ModelAndView UserSeach(@RequestParam(defaultValue = " ") String key, @RequestParam(defaultValue = "1") String wantpage) throws SQLException {
        int wp = Integer.parseInt(wantpage);
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        int wantPage = Integer.parseInt(wantpage);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("begin", (wantPage - 1) * 10);
        map.put("end", 10);
        map.put("content", key);
        int UserCount = userMapper.seachCount(map);
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % 10)) / 10 + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        List<User> userFY = userMapper.seach(map);
        for (int i = 0; i < userFY.size(); i++) {
            User userInfo = userFY.get(i);

            userInfo.setColumnInfoViews(columnInfoService.selectColumnValueByUserId(userInfo.getId()));
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("type", "1");
        mav.addObject("userFY", userFY);
        mav.addObject("key", key);
        mav.addObject("nowpage", wantpage);
        mav.addObject("pagecount", pageCount);
        mav.setViewName("/UserList");
        return mav;

    }

    /**
     * 用户信息界面，未传id参数为新建用户，否则为修改用户信息
     *
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    @RequestMapping(value = "/userInfo")
    public String userInfo(@RequestParam(value = "id", required = false) Integer id, User user, HttpServletResponse res, HttpServletRequest req, HttpSession session) throws IOException, IllegalArgumentException, IllegalAccessException, SQLException {
        this.index(session);
        JSONObject result = new JSONObject();
        if (id == null) {

            String name = user.getName();
            User us = userMapper.selectByName(name);
            if (us != null) {
                result.put("message", "error");
            }
            user.setName(name);
            //添加用户todoUser
            user.setAddBackground(false);
            session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("添加用户'%s'", user.getName()));
            int i = userMapper.insert(user);
            //todo
            if (i > 0) {
                propertyController ec = new propertyController();
                ec.insertUser(user.getName());
            }
            //环信注册
            HxUtil hu = new HxUtil();
            hu.insertUser(user);
            if (i == 1) {
                result.put("message", "success");
            } else {
                result.put("message", "error");
            }
        } else {
            try {
//                String name = new String(user.getName().getBytes("iso-8859-1"), "utf-8");
                user.setName(user.getName());
            } catch (NullPointerException e) {

            }
            user.setId(id);
            //如果为积分修改就插入
            User u = userMapper.selectByPrimaryKey(id);
            ScoreHistory sh = new ScoreHistory();
            sh.setEventId(14);
            if (u.getScore() > user.getScore()) {
                sh.setScore(user.getScore() - u.getScore());
                session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改用户'%s'的积分'%s'到'%s'", u.getName(), u.getScore(), user.getScore()));
                sh.setUserId(user.getId());

               scoreHistoryMapper.insert(sh);

            } else {
                sh.setScore(user.getScore() - u.getScore());
                sh.setUserId(user.getId());

                session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改用户'%s'的积分'%s'到'%s'", u.getName(), u.getScore(), user.getScore()));
                scoreHistoryMapper.insert(sh);
            }
            //插入历史记录
            AdminUndoModify aum = new AdminUndoModify();
            User ad = (User) session.getAttribute("adminUser");

            aum.setAdminId(ad.getId());
            aum.setUserId(user.getId());
            aum.setSource(u.getScore());
            aum.setDest(sh.getScore()+u.getScore());
            adminUndoModifyMapper.insert(aum);

            int i = userMapper.updateByPrimaryKey(user);
            if (i == 1) {
                result.put("message", "success");
            } else {
                result.put("message", "error");
            }
        }
        res.getWriter().write(result.toString());
        return null;
    }

    /**
     * 用户积分修改界面，操作后需要添加操作记录进数据库
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/userScore")
    public String userScore(User user, HttpServletResponse res, HttpSession session) throws IOException {
        this.index(session);
        JSONObject result = new JSONObject();
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改用户'%s'的积分为", user.getName(), user.getScore()));

        int i = userMapper.updateByPrimaryKey(user);
        if (i == 1) {
            result.put("message", "success");
        } else {
            result.put("message", "success");
        }
        res.setContentType("text/html;charset=utf-8");
        PrintWriter out = res.getWriter();
        out.println(result);
        out.flush();
        out.close();
        return null;
    }

    /**
     * 日记列表，含可删除，查看详细信息，回复信息等按钮
     *
     * @param order 排序信息,
     *              1：按照发布时间
     *              2：发布人
     *              3：回复数排序
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/diaryList")
    public ModelAndView diaryList(@RequestParam(defaultValue = "1") String wantpage, HttpServletRequest req, @RequestParam(defaultValue = "1") String order1, HttpServletResponse res, HttpSession session) throws IOException {
        this.index(session);
        ModelAndView modelAndView = new ModelAndView();
        int order = Integer.parseInt(order1);
        Map<String, Object> map = new HashMap<String, Object>();
        if (order == 1) {
            map.put("order", "time");
        } else if (order == 2) {
            map.put("order", "reply_count");
        } else if (order == 3) {
        }
        int wantPage = Integer.parseInt(wantpage);
        int count = diaryMapper.diaryCount();
        int pagesize = 10;
        int pageCount = 0;
        if (count > 10 && count % 10 != 0) {
            pageCount = (count - (count % pagesize)) / pagesize + 1;
        } else if (count > 0 && count < 10) {
            pageCount = 1;
        } else {
            pageCount = count / 10;
        }
        modelAndView.addObject("count", pageCount);
        map.put("begin", (wantPage - 1) * pagesize);
        map.put("end", pagesize);
        List<Diary> diaryFy = new ArrayList();
        if (order != 3) {
            diaryFy = diaryMapper.diaryFy(map);
        } else {
            diaryFy = diaryMapper.diaryFyName(map);
        }
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List li = new ArrayList();
        for (Diary d : diaryFy) {
            JSONObject json = new JSONObject();
            User user = userMapper.selectByPrimaryKey(d.getAuthor());
            dateStr = sdf.format(d.getTime());
            try {
                json.put("name", user.getNickname());
            } catch (NullPointerException e) {
                json.put("name", user.getName());
            }

            json.put("time", dateStr);
            li.add(json);
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantPage - 1) * 10 + i);
        }
        modelAndView.addObject("idlist", idList);

        modelAndView.addObject("type", order);
        modelAndView.addObject("nowpage", wantPage);
        modelAndView.addObject("list", li);
        modelAndView.addObject("diarylist", diaryFy);
        modelAndView.setViewName("/DiaryList");
        return modelAndView;
    }

    /**
     * 日记详细信息
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/diaryInfo")
    public ModelAndView diaryInfo(int id, HttpServletResponse res, HttpSession session, HttpServletRequest req) throws IOException {
        this.index(session);
        Diary diaryInfo = diaryMapper.selectByPrimaryKey(id);
        List imgli = new ArrayList();

        try {
            String s = new String(diaryInfo.getPics());
            String b[] = s.split("i:");
            try {
                String a[] = b[1].split(",");
                for (int i = 0; i < a.length; i++) {
                    imgli.add(a[i]);
                }
            } catch (Exception e) {

            }
        } catch (NullPointerException e) {

        }


        String dateStr = "";
        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        JSONObject json = new JSONObject();
        User user = userMapper.selectByPrimaryKey(diaryInfo.getAuthor());
        dateStr = sdf.format(diaryInfo.getTime());
        try {
            json.put("name", user.getNickname());
        } catch (NullPointerException e) {
            json.put("name", user.getName());
        }
        json.put("time", dateStr);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("imglist", imgli);
        modelAndView.addObject("diaryInfo", diaryInfo);
        modelAndView.addObject("user", json);
        List<DiaryCommentView> dc = diaryCommentMapper.selectByDiaryId(id, false);
        List dcomment = new ArrayList();
        for (DiaryCommentView dclist : dc) {
            //名称1
            JSONObject jsobj = new JSONObject();

            if (dclist.getCommentToId() != null) {
                DiaryCommentView dy = diaryCommentMapper.selectByPrimaryKey(dclist.getCommentToId());
                if (dy != null) {
                    try {
                        dclist.setCommenterName(dy.getCommenterNickname());
                    } catch (NullPointerException e) {
                        try {
                            dclist.setCommenterName(dy.getCommenterName());

                        } catch (NullPointerException s) {
                            dclist.setCommenterName("");

                        }
                    }
                }

            }
            dclist.getContent();
            dateStr2 = sdf.format(dclist.getTime());
            jsobj.put("id", dclist.getId());
            try {
                jsobj.put("name1", dclist.getCommenterNickname());

            } catch (NullPointerException e) {
                jsobj.put("name2", dclist.getCommenterName());

            }

            try {
                jsobj.put("name2", dclist.getCommentToNickname());

            } catch (NullPointerException e) {
                jsobj.put("name2", dclist.getCommentToName());

            }
            jsobj.put("time", dateStr2);

            EmojiUtil eu = new EmojiUtil();
            jsobj.put("content", eu.emoJi(dclist.getContent(), req));
            dcomment.add(jsobj);
        }
        modelAndView.addObject("dc", dcomment);
        modelAndView.setViewName("/diaryInfo");
        return modelAndView;
    }

    /**
     * 日记搜索结果界面，内容关键字全局搜索（包括日记和回复）
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/diarySearch")
    public ModelAndView diarySearch(@RequestParam String key, @RequestParam(defaultValue = "1") int wantpage) throws IOException {
        Map<String, Object> config = new HashMap<String, Object>();
        ModelAndView modelAndView = new ModelAndView();
//        key = new String(key.getBytes("iso-8859-1"), "utf-8");
        config.put("content", key);
        config.put("begin", (wantpage - 1) * 10);
        config.put("end", 10);
        int count = diaryMapper.diarySearch2count(config);
        int pagesize = 10;
        int pageCount = 0;
        if (count > 10) {
            pageCount = (count - (count % pagesize)) / pagesize + 1;
        } else if (count > 0 && count < 10) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        modelAndView.addObject("key", key);
        modelAndView.addObject("count", pageCount);
        modelAndView.addObject("nowpage", wantpage);
        List<Diary> dl = diaryMapper.diarySearch2(config);
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List li = new ArrayList();
        for (Diary d : dl) {
            JSONObject json = new JSONObject();
            User user = userMapper.selectByPrimaryKey(d.getAuthor());
            dateStr = sdf.format(d.getTime());
            try {
                json.put("name", user.getNickname());

            } catch (NullPointerException e) {
                json.put("name", user.getName());

            }
            json.put("time", dateStr);
            li.add(json);
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantpage - 1) * 10 + i);
        }
        modelAndView.addObject("idlist", idList);
        modelAndView.addObject("type", "4");
        modelAndView.addObject("list", li);
        modelAndView.addObject("diarylist", dl);
        modelAndView.setViewName("/DiaryList");
        return modelAndView;


    }

    /**
     * 日记点赞列表
     */
    @RequestMapping(value = "/diaryPraise")
    public ModelAndView diaryPraise(int id, @RequestParam(defaultValue
            = "1") int wantpage) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", id);
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        int count = userFollowDiaryService.diaryPariseCount(id);
        int pageCount = 0;
        if (count > 10) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count > 0 && count <= 10) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }

        List<UserFollowDiary> lud = userFollowDiaryService.diaryPariseFY(map);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List li = new ArrayList();
        for (int i = 0; i < lud.size(); i++) {
            JSONObject js = new JSONObject();
            js.put("time", sdf.format(lud.get(i).getTime()));
            User user = userMapper.selectByPrimaryKey(lud.get(i).getUserId());
            js.put("realName", user.getRealname());
            try {
                js.put("User", user.getName());
            } catch (NullPointerException e) {
                js.put("User", "");
            }
            li.add(js);
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("li", li);
        mav.addObject("id", id);
        mav.addObject("nowpage", wantpage);
        mav.addObject("count", pageCount);
        mav.setViewName("/diaryFollow");
        return mav;
    }

    /**
     * 日记评论信息，可删除评论
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/diaryCommentList")
    public ModelAndView diaryCommentList(@RequestParam(value = "wantpage", defaultValue = "1") String wantpage, @RequestParam(value = "id", required = false) String id, HttpServletResponse res, HttpServletRequest req) throws IOException {
        int diarid = Integer.parseInt(id);
        int wp = Integer.parseInt(wantpage);
        JSONObject result = new JSONObject();
        ModelAndView modelAndView = new ModelAndView();
        //查看日记评论

        int pageCount = 0;

        List<DiaryCommentView> dc = diaryCommentMapper.selectByDiaryId(diarid, false);
        if (dc.size() > 10 && dc.size() % 10 == 0) {
            pageCount = dc.size() / 10;
        } else if (dc.size() > 10 && dc.size() % 10 != 0) {
            pageCount = (dc.size() - dc.size() % 10) / 10 + 1;
        } else if (dc.size() > 0 && dc.size() <= 10) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        modelAndView.addObject("nowPage", wantpage);
        modelAndView.addObject("pageCount", pageCount);

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", diarid);
        map.put("begin", (wp - 1) * 10);
        map.put("end", 10);
        List<DiaryCommentView> dcFY = diaryCommentMapper.getCommentFY(map);
        List dcomment = new ArrayList();
        String dateStr = "";
        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        for (DiaryCommentView dclist : dcFY) {
            JSONObject jsobj = new JSONObject();
            User user = userService.selectByPrimaryKey(dclist.getCommenterId());
            try {
                jsobj.put("name1", user.getNickname());

            } catch (NullPointerException e) {
                jsobj.put("name1", user.getName());

            }
            User user2 = new User();
            if (dclist.getCommentToId() != null) {
                DiaryCommentView dcs = diaryCommentMapper.selectByPrimaryKey(dclist.getCommentToId());
                try {
                    user2 = userService.selectByPrimaryKey(dcs.getCommenterId());
                } catch (Exception e) {

                }
            }
            dclist.getContent();
            dateStr2 = sdf.format(dclist.getTime());
            if (user2 != null) {
                try {
                    jsobj.put("name2", user2.getNickname());
                } catch (NullPointerException e) {
                    jsobj.put("name2", user2.getName());

                }
            }
            jsobj.put("id", dclist.getId());
            jsobj.put("time", dateStr2);
            EmojiUtil eu = new EmojiUtil();
            jsobj.put("content", eu.emoJi(dclist.getContent(), req));
            dcomment.add(jsobj);
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelAndView.addObject("idList", idList);
        modelAndView.addObject("id", id);
        modelAndView.addObject("dc", dcomment);
        modelAndView.setViewName("/DiaryComment");
        return modelAndView;
    }

    /**
     * 新建话题界面
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/articleNew")
    public void articleNew(String title, String content, HttpServletResponse res, HttpServletRequest req, HttpSession session) throws IOException {
        JSONObject result = new JSONObject();
        User ad = (User) session.getAttribute("adminUser");
//    	String t= new String(req.getParameter("title").getBytes("iso-8859-1"),"utf-8");
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("新建了话题'%s'", title));

        Article article = new Article();
        article.setAuthor(ad.getId());
        article.setTitle(title);
        article.setContent(content);
        article.setTime(new Date());
        article.setAvai(true);
        articleMapper.insert(article);
        result.put("status", "success");
        res.getWriter().write(result.toString());
    }

    /**
     * 话题搜索结果界面，内容关键字全局搜索（包括帖子和回复）
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/articleSearch")
    public String articleSearch(@RequestParam String key, @RequestParam(defaultValue = "time desc") String order, HttpServletResponse res, @RequestParam(defaultValue = "1") int wantpage, ModelMap modelmap, HttpServletRequest req) throws IOException {
//        key = new String(key.getBytes("iso-8859-1"), "utf-8");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        map.put("order", order);
        map.put("content", key);
        int count = 0;
        count = articleMapper.articleSearch2Count(map);
        if (count > 10 && count % 10 == 0) {
            count = count / 10;
        } else if (count > 10 && count % 10 != 0) {
            count = (count - count % 10) / 10 + 1;
        } else if (count > 0 && count <= 10) {
            count = 1;
        } else {
            count = 0;
        }
        if (wantpage <= 0 || wantpage > count) {
            wantpage = 1;
        }

        List<Article> articleFY = articleMapper.articleSearch2(map);
        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List li = new ArrayList();
        List<User> ul = new ArrayList();
        for (Article a : articleFY) {


            String url = a.getContent();
            url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr = URLDecoder.decode(url, "UTF-8");
            a.setContent(urlStr);

            String url2 = a.getTitle();
            url2 = url2.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr2 = URLDecoder.decode(url2, "UTF-8");
            a.setTitle(urlStr2);

            JSONObject json = new JSONObject();
            try{
                int id = a.getGroupId();
                Group group = groupService.selectByPrimaryKey(id);

                json.put("inGroup", group.getName());
            }catch (Exception e){
                json.put("inGroup","");

            }

            json.put("time", sdf.format(a.getTime()));
            User u = userMapper.selectByPrimaryKey(a.getAuthor());
            ul.add(u);
            li.add(json);
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantpage - 1) * 10 + i);
        }
        modelmap.put("group", groupService.getExistsGroupInfo(true));

        modelmap.put("idlist", idList);
        modelmap.put("nowpage", wantpage);
        modelmap.put("time", li);
        modelmap.put("user", ul);
        modelmap.put("count", count);
        modelmap.put("list", articleFY);
        modelmap.put("key", key);
        modelmap.put("type", 2);
        return "/articleList";
    }

    /**
     * 话题列表界面
     *
     * @param order 排序信息,
     *              1：按照发布时间
     *              2：发布人
     *              3：回复数排序?
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/articleList")
    public String articleList(HttpSession session, @RequestParam(defaultValue = "1") int order, @RequestParam(defaultValue = "1") int wantpage, HttpServletResponse res, ModelMap modelmap) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入了话题列表界面"));

        int count = articleMapper.articleCount();
        if (count > 10 && count % 10 == 0) {
            count = count / 10;
        } else if (count > 10 && count % 10 != 0) {
            count = (count - count % 10) / 10 + 1;
        } else if (count > 0 && count <= 10) {
            count = 1;
        } else {
            count = 0;
        }
        if (wantpage <= 0 || wantpage > count) {
            wantpage = 1;
        }
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        List<Article> articleFY = articleMapper.articleFY(map);
        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List li = new ArrayList();
        List<User> ul = new ArrayList();
        for (Article a : articleFY) {

            String url = a.getContent();
            url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr = URLDecoder.decode(url, "UTF-8");
            a.setContent(urlStr);

            String url2 = a.getTitle();
            url2 = url2.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr2 = URLDecoder.decode(url2, "UTF-8");
            a.setTitle(urlStr2);
            JSONObject json = new JSONObject();
            try{
                int id = a.getGroupId();
                Group group = groupService.selectByPrimaryKey(id);

                json.put("inGroup", group.getName());
            }catch (Exception e){
                json.put("inGroup","");

            }
            json.put("time", sdf.format(a.getTime()));
            User u = userMapper.selectByPrimaryKey(a.getAuthor());
            ul.add(u);
            li.add(json);
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantpage - 1) * 10 + i);
        }
        modelmap.put("idlist", idList);
        modelmap.put("type", 1);
        modelmap.put("nowpage", wantpage);
        modelmap.put("time", li);
        modelmap.put("user", ul);
        modelmap.put("count", count);
        modelmap.put("group", groupService.getExistsGroupInfo(true));
        modelmap.put("list", articleFY);
        return "/articleList";
    }

    /**
     * 话题详情界面
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/articleInfo")
    public ModelAndView articleInfo(HttpSession session, int id, HttpServletResponse res, ModelMap modelMap, HttpServletRequest req) throws IOException {
        Article article = articleMapper.selectByPrimaryKey(id, null);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入了话题'%s'的详情", article.getTitle()));

        String url = article.getContent();
        url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        String urlStr = URLDecoder.decode(url, "UTF-8");
        article.setContent(urlStr);

        String url2 = article.getTitle();
        url2 = url2.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        String urlStr2 = URLDecoder.decode(url2, "UTF-8");
        article.setTitle(urlStr2);
        String s = "";
        try {
            s = new String(article.getPics());
        } catch (NullPointerException e) {
            s = "";
        }
        try {
            String b[] = s.split("i:");
            s = b[1];
        } catch (Exception e) {
            s = "";
        }
        String a[] = s.split(",");
        List imgli = new ArrayList();
        for (int i = 0; i < a.length; i++) {
            imgli.add(a[i]);
        }
        int uid = article.getAuthor();
        ModelAndView modelAndView = new ModelAndView();
        if (article.getPublishBackground() == false && s.isEmpty() == false) {
            modelAndView.addObject("aimg", imgli);
        } else {
            modelAndView.addObject("aimg", "");
        }
        User user = userMapper.selectByPrimaryKey(uid);
    /*	//查看评论与回复
        Map<String,Integer> map=new HashMap<String,Integer>();
        map.put("id",id);
        map.put("begin",0);
        map.put("end",10);*/
        List<ArticleCommentView> ac = articleCommentMapper.selectByArticleId(id, false);
        if (ac != null) {

            List li = new ArrayList();
            for (ArticleCommentView aclist : ac) {
                String url3 = aclist.getContent();
                url3 = url3.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                String urlStr3 = URLDecoder.decode(url3, "UTF-8");
                aclist.setContent(urlStr3);


                JSONObject jsonobj = new JSONObject();

                ArticleCommentView acomment = articleCommentMapper.selectByPrimaryKey(aclist.getId());

                try {
                    String p = aclist.getPics();
                    String b[] = p.split("i:");
                    if (b.length > 1) {
                        String cimg = new String(b[1]);
                        String ci[] = cimg.split(",");
                        List cl = new ArrayList();
                        for (int i = 0; i < ci.length; i++) {
                            cl.add(ci[i]);
                        }
                        jsonobj.put("cl", cl);
                    }


                } catch (NullPointerException e) {
                    jsonobj.put("cl", "");
                }
                jsonobj.put("id", aclist.getId());
                User user1 = new User();
                User user2 = new User();
                try {
                    user1 = userService.selectByPrimaryKey(aclist.getCommenterId());
                    try {
                        jsonobj.put("name1", user1.getNickname());

                    } catch (NullPointerException e) {
                        jsonobj.put("name1", user1.getName());

                    }

                } catch (Exception e) {

                }
                try {
                    ArticleCommentView acomment2 = articleCommentMapper.selectByPrimaryKey(acomment.getCommentToId());

                    user2 = userService.selectByPrimaryKey(acomment2.getCommenterId());
                    try {
                        jsonobj.put("name2", user2.getNickname());

                    } catch (NullPointerException e) {
                        jsonobj.put("name2", user2.getName());

                    }

                } catch (Exception e) {

                }


                try {
                    EmojiUtil eu = new EmojiUtil();
                    jsonobj.put("content", eu.emoJi(aclist.getContent(), req));
                } catch (NullPointerException e) {
                    jsonobj.put("content", "");
                }
                String dateStr2 = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");


                jsonobj.put("time", sdf.format(aclist.getTime()));
                li.add(jsonobj);

            }
            modelAndView.addObject("ac", li);

        }

        modelAndView.addObject("article", article);
        modelAndView.addObject("user", user);
        modelAndView.addObject("list", user);
        modelAndView.setViewName("/articleInfo");
        return modelAndView;
    }

    /**
     * 话题评论列表界面
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/articleCommentList")
    public ModelAndView articleCommentList(String id, HttpServletResponse res, HttpServletRequest req, @RequestParam(defaultValue = "1") int wantpage) throws IOException {
        int articleid = Integer.parseInt(id);
        List<ArticleCommentView> li = articleCommentMapper.selectByArticleId2(articleid);
        //分页
        int pagecount = li.size();
        if (pagecount > 10 && pagecount % 10 == 0) {
            pagecount = pagecount / 10;
        } else if (pagecount > 10 && pagecount % 10 != 0) {
            pagecount = (pagecount - pagecount % 10) / 10 + 1;
        } else if (pagecount > 0 && pagecount <= 10) {
            pagecount = 1;
        } else {
            pagecount = 0;
        }
        Map<String, Integer> map = new HashMap<String, Integer>();
        ModelAndView modelAndView = new ModelAndView();
        map.put("id", articleid);
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        List<ArticleComment> liFY = articleCommentMapper.getCommentFY(map);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List list = new ArrayList();
        for (ArticleComment l : liFY) {
            String url = l.getContent();
            url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            String urlStr = URLDecoder.decode(url, "UTF-8");
            l.setContent(urlStr);
            JSONObject json = new JSONObject();
            try {
                String pics = l.getPics();
                String s = new String(pics);
                String a[] = s.split(",");
                List imgli = new ArrayList();
                for (int i = 0; i < a.length; i++) {
                    imgli.add(a[i]);
                }
                json.put("imgList", imgli);
            } catch (NullPointerException e) {
                json.put("imgList", "");
            }
            json.put("id", l.getId());
            User user = userMapper.selectByPrimaryKey(l.getCommenter());
            if (user != null) {
                try {
                    json.put("name1", user.getNickname());
                } catch (NullPointerException e) {
                    json.put("name1", user.getName());
                }
            }


            ArticleCommentView ac = articleCommentMapper.selectByPrimaryKey(l.getCommentTo());
            if (ac != null) {
                User user2 = userMapper.selectByPrimaryKey(ac.getCommenterId());
                if (user2 != null) {
                    try {
                        json.put("name2", user2.getNickname());
                    } catch (NullPointerException e) {
                        json.put("name2", user2.getRealname());
                    }
                }


            }
//匹配表情[df勾引][df抓狂][df奋斗]
//            String[] ss=str.split(";");
//    	    for(int i=0;i<ss.length;i++){
//    	    	if(!ss[i].trim().equals("")){
//    	    		EmojiTempModel ce=new EmojiTempModel(ss[i].split(":")[0].trim(),ss[i].split(":")[1].trim());
//    	    		emojiList.add(ce);
//    	    	}
//    	    }
            EmojiUtil eu = new EmojiUtil();
            json.put("content", eu.emoJi(l.getContent(), req));
            json.put("time", sdf.format(l.getTime()));
            list.add(json);
        }
        modelAndView.addObject("nowpage", wantpage);
        modelAndView.addObject("id", id);
        modelAndView.addObject("list", list);
        modelAndView.addObject("count", pagecount);
        modelAndView.addObject("liFy", liFY);
        modelAndView.setViewName("/articlecomment");
        return modelAndView;
    }

    //话题评论数据删除
    @RequestMapping("/delArticleComment")
    public void delArticleComment(int id, HttpServletResponse res, HttpSession session) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("删除了id为'%s'的回帖数据", id));

        articleCommentMapper.deleteByPrimaryKey(id);
        res.getWriter().write("success");
    }

    //导出回帖数据
    @RequestMapping("/ArticleCommentDataExport")
    public void ArticleCommentDataExport(HttpSession session, @RequestParam(defaultValue = "1") int wantpage, HttpServletResponse res) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("导出了的回帖数据"));

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        //获取评论数据
        int i = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List<ArticleComment> aclist = articleCommentMapper.CommentListFY(map);
        int pagecount = articleCommentMapper.CommentListFYCount();
        if (pagecount > 10 && pagecount % 10 == 0) {
            pagecount = pagecount / 10;
        } else if (pagecount > 10 && pagecount % 10 != 0) {
            pagecount = (pagecount - pagecount % 10) / 10 + 1;
        } else if (pagecount > 0 && pagecount <= 10) {
            pagecount = 1;
        } else {
            pagecount = 0;
        }
        List li = new ArrayList();
        for (ArticleComment ac : aclist) {
            JSONObject jo = new JSONObject();
            jo.put("articleCommentId", ac.getId());
            //顺序id
            jo.put("id", i);
            i++;
            //获取字数
            int ArticleCommentlength = articleCommentMapper.commentLength(ac.getId().toString());

            jo.put("acLength", ArticleCommentlength);
            //获取浏览时间
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("uid", ac.getCommenter());
            map2.put("aid", ac.getArticle());
            map2.put("ACtime", ac.getTime());

            UserViewArticleLog uva = userViewArticleLogMapper.VBE(map2);
            if (uva != null) {
                try {
                    jo.put("BeginTime", sdf.format(uva.getTime()));

                } catch (NullPointerException e) {
                    jo.put("EndTime", " ");
                }
                try {
                    jo.put("EndTime", sdf.format(uva.getTimeEnd()));

                } catch (NullPointerException e) {
                    jo.put("EndTime", " ");

                }
                try {
                    jo.put("source", uva.getSource());

                } catch (NullPointerException e) {
                    jo.put("source", 0);

                }
                //浏览时长
                int ViewLength = userViewArticleLogMapper.GetViewTime(uva.getId().toString());
                jo.put("ViewLength", ViewLength);
            } else {

                jo.put("BeginTime", "");
                jo.put("EndTime", " ");
                jo.put("source", 0);
                jo.put("ViewLength", 0);

            }

            //查询帖子信息
            Article al = articleMapper.selectByid(ac.getArticle().toString());
            if (al != null) {
                jo.put("ArticleTime", sdf.format(al.getTime()));
                jo.put("articleTitle", al.getTitle());
                //Group
                Group group = groupService.selectByPrimaryKey(al.getGroupId());
                jo.put("group", group.getName());
                //查询发帖人信息
                User user1 = userService.selectByPrimaryKey(al.getAuthor());
                jo.put("user1", user1);
            } else {
                jo.put("ArticleTime", "");
                jo.put("articleTitle", "");

                jo.put("group", "");
                //查询发帖人信息

                jo.put("user1", "");
            }


            //查询是否收藏
            int articleFollow = userFollowArticleMapper.FollowArticle(map2);
            jo.put("articleFollow", articleFollow);
            //查询浏览人信息
            User user2 = userService.selectByPrimaryKey(ac.getCommenter());
            jo.put("user2", user2);

            li.add(jo);
        }
        //当前是否回复、回帖字数、进入渠道（讨论区/提醒）、是否收藏。
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("帖子数据");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("编号");
        cell.setCellStyle(style);
        cell = row.createCell((short) 1);
        cell.setCellValue("帖子名称");
        cell.setCellStyle(style);
        cell = row.createCell((short) 2);
        cell.setCellValue("所属讨论区");
        cell.setCellStyle(style);
        cell = row.createCell((short) 3);
        cell.setCellValue("发帖时间");
        cell.setCellStyle(style);
        cell = row.createCell((short) 4);
        cell.setCellValue("发帖昵称");
        cell.setCellStyle(style);
        cell = row.createCell((short) 5);
        cell.setCellValue("发帖手机号");
        cell.setCellStyle(style);
        cell = row.createCell((short) 6);
        cell.setCellValue("浏览昵称");
        cell.setCellStyle(style);
        cell = row.createCell((short) 7);
        cell.setCellValue("浏览手机号");
        cell.setCellStyle(style);
        cell = row.createCell((short) 8);
        cell.setCellValue("浏览开始时间");
        cell.setCellStyle(style);
        cell = row.createCell((short) 9);
        cell.setCellValue("浏览结束");
        cell.setCellStyle(style);
        cell = row.createCell((short) 10);
        cell.setCellValue("浏览时长");
        cell.setCellStyle(style);
        cell = row.createCell((short) 11);
        cell.setCellValue("是否回复");
        cell.setCellStyle(style);
        cell = row.createCell((short) 12);
        cell.setCellValue("回复字数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 13);
        cell.setCellValue("是否收藏");
        cell.setCellStyle(style);
        cell = row.createCell((short) 14);
        cell.setCellValue("来源");
        cell.setCellStyle(style);
        for (int f = 0; f < li.size(); f++) {
            JSONObject jo = (JSONObject) li.get(f);
            row = sheet.createRow((int) 1 + f);
            row.createCell((short) 0).setCellValue(jo.getString("id"));
            row.createCell((short) 1).setCellValue(jo.getString("articleTitle"));
            row.createCell((short) 2).setCellValue(jo.getString("group"));
            row.createCell((short) 3).setCellValue(jo.getString("ArticleTime"));

            try {
                JSONObject user1 = jo.getJSONObject("user1");
                if (user1 != null) {
                    row.createCell((short) 5).setCellValue(user1.optString("name"));

                    try {
                        row.createCell((short) 4).setCellValue(user1.optString("nickname"));

                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }

            try {
                JSONObject user2 = jo.getJSONObject("user2");
                if (user2 != null) {
                    row.createCell((short) 7).setCellValue(user2.optString("name"));

                    row.createCell((short) 6).setCellValue(user2.optString("nickname"));
                }
            } catch (Exception e) {

            }


            row.createCell((short) 8).setCellValue(jo.getString("BeginTime"));
            row.createCell((short) 9).setCellValue(jo.getString("EndTime"));
            row.createCell((short) 10).setCellValue(jo.getString("ViewLength"));
            try {
                if (!jo.get("acLength").toString().equals("0")) {
                    row.createCell((short) 11).setCellValue("已回复");

                } else {
                    row.createCell((short) 11).setCellValue("未回复");

                }
            } catch (Exception e) {
                row.createCell((short) 11).setCellValue("未回复");
            }

            try {
                row.createCell((short) 12).setCellValue(jo.getString("acLength"));
            } catch (Exception e) {
                row.createCell((short) 12).setCellValue(" ");
            }

            if (!jo.getString("articleFollow").equals("0")) {
                row.createCell((short) 13).setCellValue("已收藏");
            } else {
                row.createCell((short) 13).setCellValue("未收藏");
            }
            try {
                if (jo.getString("source").equals("0")) {
                    row.createCell((short) 14).setCellValue("讨论区");
                } else {
                    row.createCell((short) 14).setCellValue("提醒");
                }
            } catch (Exception e) {
                row.createCell((short) 14).setCellValue("讨论区");
            }
        }
        OutputStream out = null;//创建一个输出流对象
        out = res.getOutputStream();
        String headerStr = "回帖数据";
        headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");
        res.setHeader("Content-disposition", "attachment; filename=" + headerStr + ".xls");//filename是下载的xls的名，建议最好用英文
        res.setContentType("application/msexcel;charset=UTF-8");//设置类型
        res.setHeader("Pragma", "No-cache");//设置头
        res.setHeader("Cache-Control", "no-cache");//设置头
        res.setDateHeader("Expires", 0);//设置日期头
        wb.write(out);
        out.flush();
        if (out != null) {
            out.close();
        }

    }

    //回帖数据界面
    @RequestMapping(value = "/ArticleCommentData")
    public ModelAndView ArticleCommentData(HttpSession session, HttpServletResponse res, HttpServletRequest req, @RequestParam(defaultValue = "1") int wantpage) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入了回帖数据界面"));

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        //获取评论数据
        int i = (wantpage - 1) * 10 + 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List<ArticleComment> aclist = articleCommentMapper.CommentListFY(map);
        int pagecount = articleCommentMapper.CommentListFYCount();
        if (pagecount > 10 && pagecount % 10 == 0) {
            pagecount = pagecount / 10;
        } else if (pagecount > 10 && pagecount % 10 != 0) {
            pagecount = (pagecount - pagecount % 10) / 10 + 1;
        } else if (pagecount > 0 && pagecount <= 10) {
            pagecount = 1;
        } else {
            pagecount = 0;
        }
        List li = new ArrayList();
        for (ArticleComment ac : aclist) {
            JSONObject jo = new JSONObject();
            jo.put("articleCommentId", ac.getId());
            //顺序id
            jo.put("id", i);
            i++;
            //获取字数
            int ArticleCommentlength = articleCommentMapper.commentLength(ac.getId().toString());

            jo.put("acLength", ArticleCommentlength);
            //获取浏览时间
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("uid", ac.getCommenter());
            map2.put("aid", ac.getArticle());
            map2.put("ACtime", ac.getTime());

            UserViewArticleLog uva = userViewArticleLogMapper.VBE(map2);
            if (uva != null) {
                try {
                    jo.put("BeginTime", sdf.format(uva.getTime()));

                } catch (NullPointerException e) {
                    jo.put("EndTime", " ");
                }
                try {
                    jo.put("EndTime", sdf.format(uva.getTimeEnd()));

                } catch (NullPointerException e) {
                    jo.put("EndTime", " ");

                }
                try {
                    jo.put("source", uva.getSource());

                } catch (NullPointerException e) {
                    jo.put("source", 0);

                }
                //浏览时长

                int ViewLength = userViewArticleLogMapper.GetViewTime(uva.getId().toString());
                jo.put("ViewLength", ViewLength);

            }

            //查询帖子信息
            Article al = articleMapper.selectByid(ac.getArticle().toString());
            if (al != null) {
                jo.put("ArticleTime", sdf.format(al.getTime()));
                jo.put("articleTitle", al.getTitle());
                //Group
                Group group = groupService.selectByPrimaryKey(al.getGroupId());
                jo.put("group", group.getName());
                //查询发帖人信息
                User user1 = userService.selectByPrimaryKey(al.getAuthor());
                jo.put("user1", user1);
            }


            //查询是否收藏
            int articleFollow = userFollowArticleMapper.FollowArticle(map2);
            jo.put("articleFollow", articleFollow);
            //查询浏览人信息
            User user2 = userService.selectByPrimaryKey(ac.getCommenter());
            jo.put("user2", user2);

            li.add(jo);
        }
        //export

        ModelAndView mav = new ModelAndView();
        mav.addObject("li", li);
        mav.addObject("nowpage", wantpage);
        mav.addObject("count", pagecount);
        mav.setViewName("/articleCommentData");
        return mav;
    }

    /**
     * 帖子收藏列表
     */
    @RequestMapping(value = "/like")
    public ModelAndView articleLike(HttpSession session, int id, @RequestParam(defaultValue = "1") int wantpage) {
        //查看收藏用户

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("aid", id);
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        List<UserFollowArticle> lu = articleMapper.getLike(map);
        int count = 0;
        count = articleMapper.getLikeCount(map);

        int pageCount = 0;
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }

        List li = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        for (int i = 0; i < lu.size(); i++) {
            JSONObject json = new JSONObject();
            User user = userMapper.selectByPrimaryKey(lu.get(i).getUserid());
            json.put("time", sdf.format(lu.get(i).getTime()));
            json.put("User", user.getNickname());
            li.add(json);
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("key", id);
        mav.addObject("count", pageCount);
        mav.addObject("nowpage", wantpage);
        mav.addObject("li", li);
        mav.setViewName("/parise");
        return mav;
    }

    /**
     * 积分兑换历史删除
     *
     * @throws IOException
     */
    @RequestMapping("/delGoodExchang")
    public void delGoodExchang(HttpSession session, int gid, int uid, String time, HttpServletResponse res) throws IOException, ParseException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("删除了积分兑换商品记录"));

        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss SSS");
        String s = time;
        Date date = formatter.parse(s);
        GoodDuihuan g = new GoodDuihuan();
        g.setGoodId(gid);
        g.setUserId(uid);
        g.setTime(date);
        goodDuihuanService.deleteByPrimaryKey(g);
        res.getWriter().write("success");
    }

    /**
     * 积分商品交换历史记录
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/goodExchangeHistory")
    public ModelAndView goodExchangeHistory(HttpServletResponse res, @RequestParam(defaultValue = "1") String wantpage) throws IOException {

        int count = goodDuihuanService.getAllCount();
        int pageCount = 0;
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }
        int wp = Integer.parseInt(wantpage);
        int begin = (wp - 1) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", begin);
        map.put("end", 10);
        List<GoodDuihuanView> gh = goodDuihuanService.getAll(map);
        List li = new ArrayList();
        for (int i = 0; i < gh.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("gid", gh.get(i).getGoodId());
            json.put("uid", gh.get(i).getUserId());

            json.put("handled", gh.get(i).getHandled());
            User user = userService.selectByPrimaryKey(gh.get(i).getUserId());
            json.put("tel", user.getName());
            json.put("name", gh.get(i).getName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss SSS");
            json.put("time", sdf.format(gh.get(i).getTime()).toString());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            json.put("time2", sdf2.format(gh.get(i).getTime()).toString());

            json.put("id", wp * i + 1);
            li.add(json);
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("type", 1);
        mav.addObject("info", li);
        mav.addObject("pagecount", pageCount);
        mav.addObject("nowpage", wp);
        mav.setViewName("/GoodList");
        return mav;
    }

    /**
     * 根据用户查询兑换列表
     */
    @RequestMapping(value = "/UserGood/search")
    public ModelAndView goodUser(String uid, @RequestParam(defaultValue = "1") String wantpage) {
        User u = userService.selectByName(uid);
        int id = 0;
        try {
            id = u.getId();
        } catch (NullPointerException e) {
        }

        int count = goodDuihuanService.getUsercount(id);
        int pageCount = 0;
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }
        int wp = Integer.parseInt(wantpage);
        int begin = (wp - 1) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", begin);
        map.put("end", 10);
        map.put("uid", id);
        List<GoodDuihuanView> gl = goodDuihuanService.getUserAll(map);
        List li = new ArrayList();
        for (int i = 0; i < gl.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("gid", gl.get(i).getGoodId());
            json.put("uid", gl.get(i).getUserId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss SSS");
            json.put("time", sdf.format(gl.get(i).getTime()).toString());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            json.put("time2", sdf2.format(gl.get(i).getTime()).toString());
            json.put("id", wp * i + 1);
            json.put("handled", gl.get(i).getHandled());
            User user = userService.selectByPrimaryKey(gl.get(i).getUserId());
            json.put("tel", user.getName());
            json.put("name", gl.get(i).getName());
            li.add(json);
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("uid", uid);
        mav.addObject("type", 2);
        mav.addObject("info", li);
        mav.addObject("pagecount", pageCount);
        mav.addObject("nowpage", wp);
        mav.setViewName("/GoodList");
        return mav;
    }

    /**
     * 查看商品详情
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/goodInfo")
    public ModelAndView goodInfo(HttpSession session, int id, HttpServletResponse res, HttpServletRequest req) throws IOException {
//查看@RequestParam(value="img", required=false)MultipartFile img,

        ModelAndView mav = new ModelAndView();
        Good good = goodMapper.selectByPrimaryKey(id);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("查看了'%s'的商品详情", good.getName()));

        mav.addObject("number", good.getNumber());
        mav.addObject("id", good.getId());
        mav.addObject("name", good.getName());
        mav.addObject("pic", good.getPic());
        mav.addObject("ex", good.getExchangeCount());
        mav.addObject("need", good.getNeedScore());
        mav.addObject("d", good.getDescription());
        mav.setViewName("/ChangeShop");
        return mav;
    }

    /**
     * 商品修改
     *
     * @throws IOException
     */
    @RequestMapping(value = "/goodChange", method = RequestMethod.POST)
    public ModelAndView goodChange(int id, String url, @RequestParam(value = "img", required = false) MultipartFile img, HttpServletRequest req,
                                   HttpServletResponse res, Good record, HttpSession session) throws IOException {
//        String c = new String(record.getDescription().getBytes("iso-8859-1"), "utf-8");
//        String name = new String(record.getName().getBytes("iso-8859-1"), "utf-8");
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了'%s'的商品信息", record.getName()));

        if (img.getSize() != 0) {
            String root = req.getSession().getServletContext().getRealPath("/");
            InputStream in = req.getInputStream();
            String dir = "/upload/good/";
            String imgPath = root + dir;
            String imgName = getRandomFileName() + ".jpg";
            File file = new File(imgPath, imgName);
            img.transferTo(file);
            record.setPic(dir + imgName);
        }
//        record.setName(name);
//        record.setDescription(c);
        goodMapper.updateByPrimaryKey(record);
//
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "success");
        Good good = goodMapper.selectByPrimaryKey(id);
        mav.addObject("number", good.getNumber());
        mav.addObject("id", good.getId());
        mav.addObject("name", good.getName());
        mav.addObject("pic", good.getPic());
        mav.addObject("ex", good.getExchangeCount());
        mav.addObject("need", good.getNeedScore());
        mav.addObject("d", good.getDescription());
        mav.setViewName("/ChangeShop");
        return mav;
    }

    /**
     * 商品列表
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/goodList")
    public ModelAndView goodList(HttpServletResponse res, @RequestParam(defaultValue = "1") String wantPage) throws IOException {
        List<Good> goodList = goodMapper.getAllGood(null);
        //分页
        int pageCount = 0;
        int count = goodList.size();
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }
        int wp = Integer.parseInt(wantPage);
        int begin = (wp - 1) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", begin);
        map.put("end", 10);
        List<Good> goodFy = goodMapper.GoodFy(map);
        ModelAndView mav = new ModelAndView();
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        mav.addObject("idlist", idList);
        mav.addObject("li", goodFy);
        mav.addObject("nowpage", wp);
        mav.addObject("pagecount", pageCount);
        mav.setViewName("/ShopList");
        return mav;
    }

    /**
     * 删除商品
     *
     * @throws IOException
     */
    @RequestMapping("/delGood")
    public void deleteGood(int id, HttpServletResponse res) throws IOException {
        goodMapper.deleteByPrimaryKey(id);
        res.getWriter().write("success");
    }

    /**
     * 商品添加
     *
     * @throws IOException
     * @throws IllegalStateException
     */
    @RequestMapping(value = "/view/goodAdd", method = RequestMethod.POST)
    public String goodAdd(HttpSession session, String url, @RequestParam(value = "img", required = false) MultipartFile img, HttpServletRequest req,
                          HttpServletResponse res, Good record) throws IllegalStateException, IOException {
        String root = req.getSession().getServletContext().getRealPath("/");
//        String r = new String(record.getName().getBytes("iso-8859-1"), "utf-8");
//        record.setName(r);

        InputStream in = req.getInputStream();
        String dir = "/upload/good/";
        String imgPath = root + dir;
        String imgName = getRandomFileName() + ".jpg";
        File file = new File(imgPath, imgName);
        img.transferTo(file);
        record.setPic(dir + imgName);
        goodMapper.insert(record);
        req.setAttribute("Message", "success");
        //添加商品发送信息
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("添加了'%s'商品", record.getName()));

        JavaSmsApi.newGoodNotification(goodMapper.selectByPrimaryKey(record.getId()));


        return "/GoodAdd";
    }

    /**
     * 积分规则修改，不可新建项，只可修改获取积分值
     *
     * @return
     * @throws Exception
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @RequestMapping(value = "/scoreChang")
    public void score(HttpSession session, @RequestParam(defaultValue = "null") String li1, @RequestParam(defaultValue = "null") String li2, HttpServletResponse res,
                      HttpServletRequest req, @RequestParam(defaultValue = "0") int scoreCount, @RequestParam(defaultValue = "1") int id) throws NoSuchFieldException, SecurityException, Exception {
//更新积分
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入积分规则页面"));

        ScoreConfigService scoreConfigService = ApplicationContextUtil.getBean("scoreConfigService", ScoreConfigService.class);
        try {
            li1 = req.getParameter("li1");
            String a[] = li1.split(",");
            li2 = req.getParameter("li2");
            String b[] = li2.split(",");
            for (int i = 0; i < a.length; i++) {
                int ids = Integer.parseInt(a[i]);
                int score = Integer.parseInt(b[i]);
                ScoreConfig scoreConfig = new ScoreConfig();
                scoreConfig.setId(ids);
                scoreConfig.setValue(score);
                session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了积分规则"));

                scoreConfigService.updateByPrimaryKey(scoreConfig);
            }
        } catch (NullPointerException e) {
        }
        ScoreUtil su = new ScoreUtil();
        List<ScoreConfig> scoreConfigList = scoreConfigService.selectAllConfig();
        List li = new ArrayList();
        for (ScoreConfig scoreConfig : scoreConfigList) {
            JSONObject json = new JSONObject();
            json.put("id", scoreConfig.getId());
            json.put("name", scoreConfig.getName());
            json.put("value", scoreConfig.getValue());
            li.add(json);
        }
        res.getWriter().write("success");
    }

    /**
     * 积分规则修改
     */

    @RequestMapping(value = "/scoreRegular")
    public ModelAndView scoreRegular(HttpSession session, @RequestParam(defaultValue = "null") String html, @RequestParam(defaultValue = "null") String li1, @RequestParam(defaultValue = "null") String li2, HttpServletResponse res,
                                     HttpServletRequest req, @RequestParam(defaultValue = "0") int scoreCount, @RequestParam(defaultValue = "1") int id) throws NoSuchFieldException, SecurityException, Exception {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了积分规则"));

        ModelAndView mav = new ModelAndView();
        mav.addObject("scoreCount", 1);
        String r = html;
//更新积分
        ScoreConfigService scoreConfigService = ApplicationContextUtil.getBean("scoreConfigService", ScoreConfigService.class);
        try {
            li1 = req.getParameter("li1");
            String a[] = li1.split(",");
            li2 = req.getParameter("li2");
            String b[] = li2.split(",");
            for (int i = 0; i < a.length; i++) {
                int ids = Integer.parseInt(a[i]);
                int score = Integer.parseInt(b[i]);
                ScoreConfig scoreConfig = new ScoreConfig();
                scoreConfig.setId(ids);
                scoreConfig.setValue(score);
                scoreConfigService.updateByPrimaryKey(scoreConfig);
                res.getWriter().write("success");
            }

        } catch (NullPointerException e) {

        }
        //更新规则
        if (html.equals("null")) {

        } else {
            Map<String, Object> maps = new HashMap<String, Object>();
            maps.put("message", r);
            maps.put("id", id);
            scoreConfigService.updateContent(maps);
        }

        ScoreUtil su = new ScoreUtil();
        List<ScoreConfig> scoreConfigList = scoreConfigService.selectAllConfig();
        List li = new ArrayList();
        for (ScoreConfig scoreConfig : scoreConfigList) {
            JSONObject json = new JSONObject();
            json.put("id", scoreConfig.getId());
            json.put("name", scoreConfig.getName());
            json.put("value", scoreConfig.getValue());
            li.add(json);
        }
        Map map = scoreConfigService.selectCotent();
        mav.addObject("li", li);
        mav.addObject("content", map.get("message"));
        mav.addObject("id", map.get("ScoreInfoid"));
        if ("null".equals(li)) {
            mav.setViewName("/scoreInfo");
            mav.addObject("message", "success");
        } else {
            mav.setViewName("/kindeditor-master/jsp/scoreT");

        }
        return mav;
    }

    /**
     * 用户积分列表
     */
    @RequestMapping(value = "/ScoreUser")
    public ModelAndView ScoreUser(@RequestParam(defaultValue = "1") String wantpage, HttpSession session, HttpServletRequest req) {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入用户积分列表页面"));

        //用户总数
        JSONObject result = new JSONObject();
        int pagesize = 10;
        int UserCount = userMapper.UserCount();
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % pagesize)) / pagesize + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        int wantPage = Integer.parseInt(wantpage);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantPage - 1) * pagesize);
        map.put("end", pagesize);

        List<User> userFY = userMapper.getUserFY(map);
        List li = new ArrayList();
        for (User u : userFY) {
            JSONObject j = new JSONObject();
            String dateStr = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            j.put("id", u.getId());
            j.put("name", u.getNickname());
            j.put("tel", u.getName());
            j.put("score", u.getScore());
            j.put("time", sdf.format(u.getRegisterTime()));
            li.add(j);
        }
        ModelAndView mav = new ModelAndView();

        mav.addObject("li", li);
        mav.addObject("nowpage", wantPage);
        mav.addObject("pagecount", pageCount);
        mav.setViewName("/ScoreUser");
        return mav;
    }

    /**
     * 积分规则展示
     */
    @RequestMapping(value = "/ScoreList")
    public ModelAndView ScoreInfo(HttpSession session) {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入积分规则页面"));

        ScoreUtil su = new ScoreUtil();
        ScoreConfigService scoreConfigService = (ScoreConfigService) ApplicationContextUtil.getApplicationContext().getBean("scoreConfigService");
        List<ScoreConfig> scoreConfigList = scoreConfigService.selectAllConfig();
        List li = new ArrayList();
        for (ScoreConfig scoreConfig : scoreConfigList) {
            JSONObject json = new JSONObject();
            json.put("id", scoreConfig.getId());
            json.put("name", scoreConfig.getName());
            json.put("value", scoreConfig.getValue());
            li.add(json);
        }
        Map map = scoreConfigService.selectCotent();
        ModelAndView mav = new ModelAndView();
        mav.addObject("scoreCount", 1);
        mav.addObject("li", li);
        mav.addObject("content", map.get("message"));
        mav.addObject("id", map.get("ScoreInfoid"));

        mav.setViewName("/kindeditor-master/jsp/scoreT");
        return mav;
    }

    /**
     * 积分规则修改
     */
    @RequestMapping(value = "/ScoreInfo")
    public ModelAndView UserScoreList(HttpSession session) {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入积分规则页面"));

        ScoreUtil su = new ScoreUtil();
        ScoreConfigService scoreConfigService = (ScoreConfigService) ApplicationContextUtil.getApplicationContext().getBean("scoreConfigService");
        List<ScoreConfig> scoreConfigList = scoreConfigService.selectAllConfig();
        List li = new ArrayList();
        for (ScoreConfig scoreConfig : scoreConfigList) {
            JSONObject json = new JSONObject();
            json.put("id", scoreConfig.getId());
            json.put("name", scoreConfig.getName());
            json.put("value", scoreConfig.getValue());
            li.add(json);
        }
        Map map = scoreConfigService.selectCotent();
        ModelAndView mav = new ModelAndView();
        mav.addObject("scoreCount", 1);
        mav.addObject("li", li);
        mav.addObject("content", map.get("message"));
        mav.addObject("id", map.get("ScoreInfoid"));

        mav.setViewName("/scoreInfo");
        return mav;
    }

    /**
     * 帮助列表，可隐藏、删除帮助
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/helpList")
    public ModelAndView helpList(HttpSession session, @RequestParam(defaultValue = "1", required = false) String wp, HttpServletResponse res) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入帮助列表界面页面"));

        int wantpage = Integer.parseInt(wp);
        int helpCount = helpMapper.helpCount();
        int pageCount = 0;
        if (helpCount > 0 && helpCount < 10) {
            pageCount = 1;
        } else if (helpCount > 10 && helpCount % 10 != 0) {
            pageCount = (helpCount - helpCount % 10) / 10 + 1;
        } else if (helpCount % 10 == 0) {
            pageCount = helpCount / 10;
        }
        if (wantpage <= 0 || wantpage > pageCount) {
            wantpage = 1;
        }

        int begin = (wantpage - 1) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", begin);
        map.put("end", 10);
        List<Help> helpList = helpMapper.helpListFy(map);
        ModelAndView mav = new ModelAndView();
        mav.addObject("nowpage", wantpage);
        mav.addObject("pageCount", pageCount);
        mav.addObject("helpList", helpList);
        mav.setViewName("/Help");
        return mav;
    }

    /**
     * 回馈列表，可删除、隐藏回馈
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/feedbackList")
    public ModelAndView feedbackList(HttpSession session, @RequestParam(defaultValue = "1") String wp, Feedback feedback, HttpServletResponse res) throws IOException {
        int wantpage = Integer.parseInt(wp);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("进入反馈列表页面"));

        int count = feedbackMapper.feedBackCount();

        int pageCount = 0;
        int nowpage = wantpage;
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }
        if (wantpage <= 0 || wantpage > pageCount) {
            wantpage = 1;
        }
        int begin = (wantpage - 1) * 10;
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", begin);
        map.put("end", 10);

        ModelAndView mav = new ModelAndView();
        List<Feedback> li = feedbackMapper.feedBackListFy(map);
        List list = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            User user = userService.selectByPrimaryKey(li.get(i).getUser());
            list.add(user.getNickname());
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantpage - 1) * 10 + i);
        }
        mav.addObject("idlist", idList);
        mav.addObject("pageCount", pageCount);
        mav.addObject("nowpage", nowpage);
        mav.addObject("userName", list);
        mav.addObject("list", li);
        mav.setViewName("/FeedbackList");
        return mav;
    }

    /**
     * 反馈搜索
     */
    @RequestMapping(value = "/feedSeach")
    public ModelAndView feendseach(@RequestParam(defaultValue = "1") String wp, String key, HttpServletResponse res) throws IOException {
        int wantpage = Integer.parseInt(wp);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("content", key);
        int count = feedbackMapper.feedbackSeachCount(map);

        int pageCount = 0;
        int nowpage = wantpage;
        if (count > 0 && count < 10) {
            pageCount = 1;
        } else if (count > 10 && count % 10 != 0) {
            pageCount = (count - count % 10) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        }
        if (wantpage <= 0 || wantpage > pageCount) {
            wantpage = 1;
        }
        int begin = (wantpage - 1) * 10;
        map.put("begin", begin);
        map.put("end", 10);

        ModelAndView mav = new ModelAndView();
        List<Feedback> li = feedbackMapper.feedbackSeach(map);
        List list = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            User user = userService.selectByPrimaryKey(li.get(i).getUser());
            list.add(user.getNickname());
        }
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wantpage - 1) * 10 + i);
        }
        mav.addObject("idlist", idList);
        mav.addObject("pageCount", pageCount);
        mav.addObject("nowpage", nowpage);
        mav.addObject("userName", list);
        mav.addObject("list", li);
        mav.addObject("type", 2);
        mav.addObject("key", key);
        mav.setViewName("/FeedbackList");
        return mav;
    }

    /**
     * 回馈详细信息，可回复回馈
     *
     * @return
     */
    @RequestMapping(value = "/feedbackInfo")
    public ModelAndView feedbackInfo(HttpSession session, @RequestParam(value = "id", required = false) Integer id, HttpServletResponse res) throws IOException {

        Feedback fl = feedbackMapper.selectByPrimaryKey(id);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("查看了'%s'的详细信息", id));

        User user = userService.selectByPrimaryKey(fl.getUser());
        ModelAndView mav = new ModelAndView();
        mav.addObject("id", id);
        mav.addObject("Userid", fl.getUser());
        mav.addObject("User", user.getRealname());
        mav.addObject("content", fl.getContent());
        mav.addObject("relay", fl.getReply());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        mav.addObject("time", sdf.format(fl.getTime()));
        mav.setViewName("/feedbackInfo");
        return mav;
    }

    /**
     * 编辑反馈
     *
     * @throws IOException
     */
    @RequestMapping(value = "/feeBdackEdit")
    public void editFeedback(HttpSession session, String content, int id, int user, String relay, HttpServletResponse res, @RequestParam(defaultValue = "0") int vis) throws IOException {
        if (vis != 0) {
            session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("设置了'%s'的显示状况", id));

            Feedback feedback = new Feedback();
            feedback.setId(id);
            feedback.setStatus(Byte(vis));
            feedbackService.updateByPrimaryKey(feedback);
            res.getWriter().write("success");
        }
        Feedback feedback = new Feedback();
//        String c = new String(content.getBytes("iso-8859-1"), "utf-8");
//        String r = new String(relay.getBytes("iso-8859-1"), "utf-8");
        feedback.setTime(new Date());
        feedback.setContent(content);
        feedback.setId(id);
        feedback.setUser(user);
        feedback.setReply(relay);
        feedback.setReplyTime(new Date());
        feedbackService.updateByPrimaryKey(feedback);
        res.getWriter().write("success");
    }

    private Byte Byte(int vis) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取所有群组
     *
     * @throws IOException
     */
    @RequestMapping("/groupInfo")
    public String grouptInfo(@RequestParam(defaultValue = "1") int msg, HttpServletRequest req, HttpServletResponse res, @RequestParam(defaultValue = "1") int wantpage, ModelMap modelmap) throws IOException {
        int begin = (wantpage - 1) * 10;
        int end = begin + 10;
        HxUtil hu = new HxUtil();
        List li = new ArrayList();
        List groupinfo = hu.getGroupInfo();

        for (int i = 0; i < groupinfo.size(); i++) {


            for (int f = i + 1; f < groupinfo.size(); f++) {
                JSONObject jo2 = (JSONObject) groupinfo.get(f);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                JSONObject jo = (JSONObject) groupinfo.get(i);
                long time = jo.getLong("time");
                long time2 = jo2.getLong("time");

                if (time2 > time) {

                    Object temp = groupinfo.get(f);
                    groupinfo.set(f, groupinfo.get(i));
                    groupinfo.set(i, temp);


                }

            }

        }

        if (end > groupinfo.size()) {
            end = groupinfo.size();
        }
        int count = 0;
        for (int i = begin; i < end; i++) {
            JSONObject jo = (JSONObject) groupinfo.get(i);
            JSONObject jo2 = new JSONObject();
            User user = userMapper.selectByName((String) jo.get("name"));
            long time = jo.getLong("time");
            jo2.put("t", time);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            String t = sdf.format(date);
            count = (int) jo.get("count");
            try {
                jo2.put("uname", user.getNickname());
            } catch (NullPointerException e) {
                jo2.put("uname", jo.get("name"));
            }
            jo2.put("time", t);
            jo2.put("id", jo.get("groupid"));
            jo2.put("gname", jo.get("groupname"));
            li.add(jo2);
        }


        int pageCount = 0;
        if (count % 10 != 0) {
            pageCount = (count - (count % 10)) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        } else if (count < 10 && count > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        if (msg == 0) {
            modelmap.put("msg", 0);

        }
        modelmap.put("nowpage", wantpage);
        modelmap.put("count", pageCount);
        modelmap.put("group", li);
        return "/groupList";
    }

    /**
     * 收索
     *
     * @throws IOException
     */
    @RequestMapping("/groupSeach")
    public String groupSeach(@RequestParam(defaultValue = "") String key, HttpServletRequest req, HttpServletResponse res, @RequestParam(defaultValue = "1") int wantpage, ModelMap modelmap) throws IOException {
        List groupSeach = new ArrayList();
        int begin = (wantpage - 1) * 10;
        int end = begin + 10;
        HxUtil hu = new HxUtil();
        List li = new ArrayList();
        List groupinfo = hu.getGroupInfo();


        int count = 0;
        for (int i = 0; i < groupinfo.size(); i++) {
            JSONObject jo = (JSONObject) groupinfo.get(i);
            JSONObject jo2 = new JSONObject();
            User user = userMapper.selectByName((String) jo.get("name"));
            long time = jo.getLong("time");
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            String t = sdf.format(date);
            count = (int) jo.get("count");
            try {
                jo2.put("uname", user.getNickname());
            } catch (NullPointerException e) {
                jo2.put("uname", jo.get("name"));
            }
            jo2.put("time", t);
            jo2.put("id", jo.get("groupid"));
            jo2.put("gname", jo.get("groupname"));

            boolean a = jo.get("groupname").toString().contains(key);
            boolean b = jo.get("name").toString().contains(key);
            boolean c;
            try {
                c = user.getNickname().toString().contains(key);
            } catch (NullPointerException e) {
                c = false;
            }

            if (a == true || b == true || c == true) {
                groupSeach.add(jo2);
            }
        }
        if (end > groupSeach.size()) {
            end = groupSeach.size();
        }
        for (int i = begin; i < end; i++) {
            li.add(groupSeach.get(i));
        }

        int pageCount = 0;
        if (groupSeach.size() % 10 != 0) {
            pageCount = (groupSeach.size() - (groupSeach.size() % 10)) / 10 + 1;
        } else if (groupSeach.size() % 10 == 0) {
            pageCount = groupSeach.size() / 10;
        } else if (groupSeach.size() < 10 && groupSeach.size() > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        modelmap.put("type", 2);
        modelmap.put("key", key);
        modelmap.put("nowpage", wantpage);
        modelmap.put("count", pageCount);
        modelmap.put("group", li);
        return "/groupList";
    }

    /**
     * 获取群组信息
     */
    @RequestMapping("/group")
    public ModelAndView group(String id, @RequestParam(defaultValue = "1") int wantpage, HttpServletRequest req) {
        ModelAndView mav = new ModelAndView();
        HxUtil hu = new HxUtil();
        UserHx uh = hu.getUserListUnderHxGrp(id);
        int begin = (wantpage - 1) * 10;
        int end = begin + 10;
        int count = uh.getCount();
        if (end > count) {
            end = count;
        }
        String owner = hu.getOwner(id);

        Object obj = uh.getUser();

        List<UserBriefInfo> list = (List<UserBriefInfo>) obj;

        if (list.size() == (end - 1)) {
            UserBriefInfo ub = new UserBriefInfo();
            User u = userMapper.selectByName(owner);

            if (u != null) {
                ub.setId(u.getId());
                ub.setName(u.getName());
                try {
                    ub.setNickname(u.getNickname());
                } catch (NullPointerException e) {
                    ub.setNickname(" ");
                }
            } else {
                ub.setName("群主不存在");
            }
            list.add(ub);
        }
        if ((list.size() + 1) < end) {
            end = list.size();
        }
        List li = new ArrayList();
        for (int i = begin; i < end; i++) {
            JSONObject json = new JSONObject();
            json.put("id", list.get(i).getId());
            json.put("tel", list.get(i).getName());
            try {
                json.put("nickname", list.get(i).getNickname());
            } catch (NullPointerException e) {
                json.put("nicknamme", "");
            }
            li.add(json);
        }

        int pageCount = 0;
        if (count % 10 != 0) {
            pageCount = (count - (count % 10)) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        } else if (count < 10 && count > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        mav.addObject("owner", owner);
        mav.addObject("id", id);
        mav.addObject("li", li);
        mav.addObject("nowpage", wantpage);
        mav.addObject("count", pageCount);
        mav.setViewName("/groupUser");
        return mav;
    }

    /**
     * 群组添加页面用户显示
     */
    @RequestMapping("/allUser")
    public ModelAndView AllUser(Model model, @RequestParam(defaultValue = "0") int type) {
     /*   List<User> ul = userMapper.getAllUserInfo();
        model.addAttribute("ul", ul);*/
        ModelAndView mav = new ModelAndView();
        List li = groupService.getExistsGroupInfo(true);
        if (type != 0) {
            mav.addObject("addmsg", "error");
        }
        mav.addObject("group", li);
        mav.addObject("extendsInfo", columnService.selectAllColumn());
        mav.setViewName("/groupAdd");

        return mav;
    }

    /**
     * 用户聊天数据
     *
     * @throws JsonProcessingException
     */
    @RequestMapping("/messageHistory")
    public ModelAndView messageHistory(HttpSession session, @RequestParam(defaultValue = "1") int wantpage) throws JsonProcessingException {
        HxUtil hu = new HxUtil();
        List groupinfo = hu.getGroupInfo();
        int begin = (wantpage - 1) * 10;
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", 10);
        List<History> li = HistoryMapper.getAll(map);
        List list = new ArrayList();
        for (int k = 0; k < li.size(); k++) {

            JSONObject obj = new JSONObject();
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            obj.put("time", sdf.format(li.get(k).getTime()));

            String newStr = li.get(k).getMsgType();
            if (newStr.equals("txt")) {
                obj.put("type", 1);
            } else if (newStr.equals("video")) {
                obj.put("type", 2);
            } else if (newStr.equals("img")) {
                obj.put("type", 3);
            } else if (newStr.equals("audio")) {
                obj.put("type", 4);
            } else if (newStr.equals("loc")) {
                obj.put("type", 1);
            }
            obj.put("msg", li.get(k).getContent());
            User user = userService.selectByName(li.get(k).getFrom());
            if (user != null) {
                try {
                    obj.put("from", user.getNickname());
                } catch (NullPointerException e) {
                    obj.put("from", user.getName());
                }
            } else {
                obj.put("from", "");
            }

            User user2 = userService.selectByName(li.get(k).getTo());
            if (user2 != null) {
                try {
                    obj.put("to", user2.getNickname());
                } catch (NullPointerException e) {
                    obj.put("to", user2.getName());
                }
            } else {
                //查看是否为群组
                JSONObject jo = hu.groupGetInfo(li.get(k).getTo());
                try {
                    String gname = jo.getString("name");
                    obj.put("to", gname);
                } catch (Exception e) {

                    obj.put("to", "");
                }

            }
            list.add(obj);
        }


        int pageCount = HistoryMapper.Count();
        if (pageCount % 10 != 0) {
            pageCount = (pageCount - (pageCount % 10)) / 10 + 1;
        } else if (pageCount % 10 == 0) {
            pageCount = pageCount / 10;
        } else if (pageCount < 10 && pageCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("group", groupinfo);
        mav.addObject("li", list);
        mav.addObject("count", pageCount);
        mav.addObject("nowpage", wantpage);
        mav.setViewName("/Message");
        return mav;
    }

    //导出用户聊天数据
    @RequestMapping("/msgExport")
    public void msgExport(HttpSession session, @RequestParam(defaultValue = "0") String groupid, HttpServletResponse res) throws IOException, ParseException {
        //导出所有
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("导出了用户聊天数据"));

        List<History> lh = new ArrayList();
        if (groupid.equals("0")) {
            lh = HistoryMapper.getMessage();
            //获取群组名。获取用户名
            for (int i = 0; i < lh.size(); i++) {
                if (lh.get(i).getChatType().equals("groupchat")) {
                    ObjectNode groupDetailNode = EasemobChatGroups.getGroupDetailsByChatgroupid(lh.get(i).getTo());
                    String gname = "";
                    try {
                        gname = groupDetailNode.get("data").get(0).get("name").toString().replaceAll("\"", "");
                    } catch (NullPointerException e) {
                        gname = "";

                    }
                    User user = userService.selectByName(lh.get(i).getFrom());
                    try {
                        lh.get(i).setFrom(user.getNickname());
                    } catch (NullPointerException e) {
                        try {
                            lh.get(i).setFrom(user.getName());
                        } catch (NullPointerException s) {
                            lh.get(i).setFrom("");
                        }

                    }
                    if (gname.length() > 0) {
                        lh.get(i).setTo(gname);
                    } else {
                        lh.get(i).setTo("");
                    }
                } else {
                    User user = userService.selectByName(lh.get(i).getFrom());
                    try {
                        lh.get(i).setFrom(user.getNickname());
                    } catch (NullPointerException e) {
                        try {
                            lh.get(i).setFrom(user.getName());

                        } catch (NullPointerException s) {
                            lh.get(i).setFrom(" ");

                        }
                    }
                    user = userService.selectByName(lh.get(i).getTo());
                    try {
                        lh.get(i).setTo(user.getNickname());
                    } catch (NullPointerException e) {
                        try {
                            lh.get(i).setFrom(user.getName());

                        } catch (NullPointerException s) {
                            lh.get(i).setFrom(" ");

                        }
                    }

                }
            }
        } else {
            //导出单个聊天
            lh = HistoryMapper.getGroup(groupid);
            //获取群组名。获取用户名
            String gname = "";
            ObjectNode groupDetailNode = EasemobChatGroups.getGroupDetailsByChatgroupid(groupid);
            try {
                gname = groupDetailNode.get("data").get(0).get("name").toString().replaceAll("\"", "");
            } catch (NullPointerException e) {
                gname = "";
            }
            for (int i = 0; i < lh.size(); i++) {
                String from = lh.get(i).getFrom();
                User user = userService.selectByName(from);
                try {
                    lh.get(i).setFrom(user.getNickname());
                } catch (NullPointerException e) {
                    lh.get(i).setFrom(user.getName());
                }
                lh.get(i).setTo(gname);
            }
        }
        //导出lh
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("聊天记录");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("发送者");
        cell.setCellStyle(style);
        cell = row.createCell((short) 1);
        cell.setCellValue("接收者");
        cell.setCellStyle(style);
        cell = row.createCell((short) 2);
        cell.setCellValue("内容");
        cell.setCellStyle(style);
        cell = row.createCell((short) 3);
        cell.setCellValue("内容类型");
        cell.setCellStyle(style);
        cell = row.createCell((short) 4);
        cell.setCellValue("时间");
        cell.setCellStyle(style);
        //发布者
        for (int i = 0; i < lh.size(); i++) {
            row = sheet.createRow((int) 1 + i);
            row.createCell((short) 0).setCellValue(lh.get(i).getFrom());
            row.createCell((short) 1).setCellValue(lh.get(i).getTo());
            row.createCell((short) 2).setCellValue(lh.get(i).getContent());
            if (lh.get(i).getMsgType().equals("txt")) {
                row.createCell((short) 3).setCellValue("文本");
            } else if (lh.get(i).getMsgType().equals("img")) {
                row.createCell((short) 3).setCellValue("图片");
            } else if (lh.get(i).getMsgType().equals("audio")) {
                row.createCell((short) 3).setCellValue("语音");
            } else if (lh.get(i).getMsgType().equals("loc")) {
                row.createCell((short) 3).setCellValue("地址");
            } else if (lh.get(i).getMsgType().equals("video")) {
                row.createCell((short) 3).setCellValue("视频");
            }


            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            row.createCell((short) 4).setCellValue(sdf.format(lh.get(i).getTime()));
        }
        OutputStream out = null;//创建一个输出流对象
        out = res.getOutputStream();
        String headerStr = "聊天记录";
        headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");
        res.setHeader("Content-disposition", "attachment; filename=" + headerStr + ".xls");//filename是下载的xls的名，建议最好用英文
        res.setContentType("application/msexcel;charset=UTF-8");//设置类型
        res.setHeader("Pragma", "No-cache");//设置头
        res.setHeader("Cache-Control", "no-cache");//设置头
        res.setDateHeader("Expires", 0);//设置日期头
        wb.write(out);
        out.flush();
        if (out != null) {
            out.close();
        }
    }

    /**
     * 同步用户聊天数据
     *
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping("/messageExport")
    public void exporylu(HttpSession session, HttpServletResponse res) throws IOException, ParseException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("同步聊天数据到本地"));

        History hsy = HistoryMapper.max();
        Date time = hsy.getTime();
        HxUtil hu = new HxUtil();
        int obSize = 0;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        do {
            JsonNode ja = hu.messageHistory(time);
            System.out.println("**" + ja);
            JsonNode ob = ja.get("entities");
            obSize = ob.size();
            if (obSize != 0) {
                hsy = HistoryMapper.max();
                time = hsy.getTime();
                ja = hu.messageHistory(time);
                ob = ja.get("entities");
                for (int i = 0; i < obSize; i++) {
                    String msg = "";
                    String type = "";
                    JSONObject obj = new JSONObject();
                    String from = ob.get(i).get("from").toString().replaceAll("\"", "");
                    String to = ob.get(i).get("to").toString().replaceAll("\"", "");
                    String chatType = ob.get(i).get("chat_type").toString().replaceAll("\"", "");

                    JsonNode j = ob.get(i).get("payload");
                    JsonNode jsn = j.get("bodies");
                    for (int k = 0; k < jsn.size(); k++) {
                        type = jsn.get(k).get("type").toString().replaceAll("\"", "");
                        if (type.equals("txt")) {
                            msg = jsn.get(k).get("msg").toString().replaceAll("\"", "");

                        } else if (type.equals("img")) {
                            msg = jsn.get(k).get("url").toString().replaceAll("\"", "");
                        } else if (type.equals("audio")) {
                            msg = jsn.get(k).get("url").toString().replaceAll("\"", "");

                        } else if (type.equals("loc")) {
                            msg = jsn.get(k).get("addr").toString().replaceAll("\"", "");
                        } else if (type.equals("video")) {
                            msg = jsn.get(k).get("url").toString().replaceAll("\"", "");
                        }

                    }
                    String t = ob.get(i).get("created").toString();
                    long l = Long.parseLong(t);
                    Date d = sdf.parse(sdf.format(l));
                    History hs = new History();
                    hs.setTime(d);
                    time = d;
                    hs.setChatType(chatType);
                    hs.setFrom(from);
                    hs.setTo(to);
                    hs.setMsgType(type);
                    hs.setContent(msg);
                    HistoryMapper.insert(hs);
                }
            }
        } while (obSize == 1000);
        res.getWriter().write("success");
    }

    /**
     * 分配聊天群组界面
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/groupAdd")
    public String groupAdd(HttpSession session, Model model, String gname, String owner, @RequestParam(defaultValue = "") String usrs, @RequestParam(defaultValue = "") String desc, @RequestParam(defaultValue = "20") int max, HttpServletResponse res, HttpServletRequest req) throws IOException {


        User ad = (User) session.getAttribute("adminUser");
        //获取所有用户
        AvailBean availBean = new ObjectMapper().readValue(usrs, AvailBean.class);
        List<User> lu = userService.selectUserByGroups(availBean.getGroup());
        Set<User> userSet = new HashSet<>(lu);
        userSet.addAll(Arrays.asList(userService.selectByNames(availBean.getUser().toArray(new String[0]))));
        List<String> luser = new ArrayList<>();
        luser.add(ad.getName());
        Iterator<User> iterator = userSet.iterator();
        while (iterator.hasNext()) {
            User us = iterator.next();
            luser.add(us.getName());
        }

//        AvailBean availBean = new ObjectMapper().readValue(usrs, AvailBean.class);
//        List<User> lu = userService.selectUserByGroups(availBean.getGroup());

        if (owner.length() == 0) {
            owner = ad.getName();
        }
        User us = userMapper.selectByName(owner);
        if (us == null) {
            return "redirect:/admin/allUser.do?type=1";
        }
        HxUtil hu = new HxUtil();
        String message = hu.groupAdd(gname, desc, owner, max, luser);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("添加了聊天群组'%s'", gname));

        return "redirect:/admin/groupInfo.do?msg=0";
    }

    /**
     * 群发消息界面
     *
     * @return
     */
    @RequestMapping(value = "/groupSendMessage")
    public String groupSendMessage() {
        return null;
    }

    /**
     * 删除组成员
     *
     * @throws IOException
     */
    @RequestMapping(value = "/deleteGroupUser")
    public void deleteGroupUser(HttpSession session, @RequestParam(defaultValue = "") String tels, String tel, String gid, HttpServletResponse res) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("删除了移除了聊天群组成员'%s'", tel));

        HxUtil hu = new HxUtil();
        if (tels.length() > 0) {
            String array01[] = tels.split(",");
            for (int i = 0; i < array01.length; i++) {
                hu.deleteGroupUser(array01[i], gid);
            }
        } else {
            hu.deleteGroupUser(tel, gid);
        }

        res.getWriter().write("success");

    }

    /**
     * 用户信息统计
     * todo
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/userData")
    public ModelAndView userData(@RequestParam(defaultValue = "0") int type, HttpServletRequest req, HttpServletResponse res, @RequestParam(defaultValue = "1") String wantpage,
                                 @RequestParam(defaultValue = "0") String url) throws IOException {

        int wantPage = Integer.parseInt(wantpage);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantPage - 1) * 10);
        map.put("end", 10);
        int pagesize = 10;
        int UserCount = userMapper.UserCount();
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % pagesize)) / pagesize + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        List<User> li = userMapper.getUserFY(map);
        List list = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("id", li.get(i).getId());
            json.put("tel", li.get(i).getName());
            json.put("name", li.get(i).getRealname());
            json.put("nickname", li.get(i).getNickname());

            //本月，上月
            if (type == 0) {
                //查询登录次数//本月发布话题数
                int ArticleCount=articleMapper.ArticleCountBen(li.get(i).getId());
                json.put("articleCount",ArticleCount);

                int loginCount = userLoginLogMapper.getLoginCount(li.get(i).getId());
                json.put("loginCount", loginCount);
                //查询反馈次数
                int feedCount = feedbackMapper.getFeedBackCount(li.get(i).getId());
                json.put("feedbackCount", feedCount);
                //问卷次数
                int wenjuan = li.get(i).getQuestionnaireTime();
                json.put("wenjuan", wenjuan);
                //浏览次数
                int viewCountarticle = userViewArticleLogMapper.getViewCount(li.get(i).getId());
                json.put("va", viewCountarticle);
                int viewCountdiary = userViewDiaryLogMapper.getViewCount(li.get(i).getId());
                json.put("vd", viewCountdiary);
                //评论次数
                int a = articleCommentMapper.getUserComment(li.get(i).getId());
                int d = diaryCommentMapper.getUsercomment(li.get(i).getId());
                json.put("comment", a + d);
            } else {
                //查询登录次数
                // 上月发布话题数
                int ArticleCount=articleMapper.ArticleCountShang(li.get(i).getId());
                json.put("articleCount",ArticleCount);
                int loginCount = userLoginLogMapper.getLoginCount2(li.get(i).getId());
                json.put("loginCount", loginCount);
                //查询反馈次数
                int feedCount = feedbackMapper.getFeedBackCount2(li.get(i).getId());
                json.put("feedbackCount", feedCount);
                //问卷次数
                int wenjuan = li.get(i).getQuestionnaireTime();
                json.put("wenjuan", wenjuan);
                //浏览次数
                int viewCountarticle = userViewArticleLogMapper.getViewCount2(li.get(i).getId());
                json.put("va", viewCountarticle);
                int viewCountdiary = userViewDiaryLogMapper.getViewCount2(li.get(i).getId());
                json.put("vd", viewCountdiary);
                //评论次数
                int a = articleCommentMapper.getUserComment2(li.get(i).getId());
                int d = diaryCommentMapper.getUsercomment2(li.get(i).getId());
                json.put("comment", a + d);
            }


            list.add(json);
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("type", type);
        mav.addObject("li", list);
        mav.addObject("nowpage", wantPage);
        mav.addObject("pagecount", pageCount);
        mav.setViewName("/Data");
        return mav;
    }

    /**
     * 未传id参数为新建帮助信息，否则为修改帮助信息
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/helpNew")
    public String helpNew(@RequestParam(value = "id", required = false) Integer id, Help help, HttpServletResponse res) throws IOException {
        if (id == null) {
            String cotent = help.getContent();
            helpMapper.insert(help);
        } else {
            help.setId(id);
            helpMapper.updateByPrimaryKey(help);
        }
        JSONObject result = new JSONObject();
        result.put("status", 1);
        res.setContentType("text/html;charset=utf-8");
        PrintWriter out = res.getWriter();
        out.println(result);
        out.flush();
        out.close();
        return null;
    }

    /**
     * 群组信息显示及修改
     *
     * @param gid
     * @param gname
     * @param desc
     * @param max
     * @param res
     * @throws IOException
     */
    @RequestMapping("/groupChangView")
    public ModelAndView groupChangView(String gid) {
        gid = gid.replaceAll("/", "");
        ModelAndView mav = new ModelAndView();
        HxUtil hu = new HxUtil();
        JSONObject js = hu.groupGetInfo(gid);
        mav.addObject("id", gid);
        mav.addObject("name", js.get("name"));
        mav.addObject("max", js.get("max"));
        mav.addObject("desc", js.get("desc"));
        mav.setViewName("/ChangeGroup");
        return mav;
    }

    //YG
    @RequestMapping("/groupChang")
    public void groupChang(HttpSession session, String gid, String gname, String desc, int max, HttpServletResponse res) throws IOException {
        HxUtil hu = new HxUtil();
//       gname = new String(gname.getBytes("iso-8859-1"), "utf-8");
//       desc = new String(desc.getBytes("iso-8859-1"), "utf-8");
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了'%s'聊天群组信息", gname));

        hu.ChangGroup(gid, gname, desc, max);
        res.getWriter().write("success");
    }

    @RequestMapping("/sendPushView")
    public ModelAndView sendpushView() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/PushAdd");
        //存入话题列表
        List<Article> al = articleMapper.all();
        mav.addObject("al", al);
        List li = groupService.getExistsGroupInfo(true);
        mav.addObject("group", li);
        mav.addObject("extendsInfo", columnService.selectAllColumn());
        return mav;
    }


    @RequestMapping(value = "/sendPush", method = RequestMethod.POST)
    public ModelAndView sendpush(HttpSession session, @RequestParam(value = "img", required = false) MultipartFile img, @RequestParam(defaultValue = "0") int a, @RequestParam(defaultValue = " ") String content, @RequestParam(defaultValue = " ") String title, String usrs, HttpServletRequest req) throws APIConnectionException, APIRequestException, IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("推送'%s'邀请信息", content));

        String ids = String.valueOf(a);
        String imgs = "";
        AvailBean availBean = new ObjectMapper().readValue(usrs, AvailBean.class);
        Set<User> lu = new HashSet<>(userService.selectUserByGroups(availBean.getGroup()));
        lu.addAll(Arrays.asList(userService.selectByNames(availBean.getUser().toArray(new String[0]))));

        if (img.getSize() != 0) {
            String root = req.getSession().getServletContext().getRealPath("/");
//            InputStream in = req.getInputStream();
            String dir = "/upload/good/";
            String imgPath = root + dir;
            String imgName = getRandomFileName() + ".jpg";
            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(imgPath, imgName);
            img.transferTo(file);
            imgs = dir + imgName;
        }
        Notifies n = new Notifies();
        n.setContent(content);
        n.setPlatform("极光推送");
        n.setSendStatus(0);
        n.setImg(imgs);
        n.setTitle(title);
        if (a != 0) {
            n.setUrl(ids);
        }

        notifiesService.insert(n);

        List<NotifiesUser> notifiesUsers = new ArrayList<>();

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

        for (User userInfo : lu) {
            NotifiesUser nu = new NotifiesUser();
            nu.setNid(n.getId());
            nu.setUname(userInfo.getName());
            notifiesUsers.add(nu);
            //mu.JPush(title, content, ids, userInfo.getName(), imgs);
            fixedThreadPool.execute(new PushRunnable(title, content, ids, userInfo.getName(), imgs));
        }
//        for (int i = 0; i < availBean.getUser().size(); i++) {
//            NotifiesUser nu = new NotifiesUser();
//            nu.setNid(n.getId());
//            nu.setUname(availBean.getUser().get(i));
//            if (notifiesUsers.contains(nu)) continue;
//            notifiesUsers.add(nu);
//            MessageUtil mu = new MessageUtil();
//            mu.JPush(title, content, ids, availBean.getUser().get(i), imgs);
//        }
        if (notifiesUsers.size() != 0) {
            notifiesService.insertNotifyUserByList(notifiesUsers);
        } else {
            List<User> lusers = userMapper.getAllUserInfo();
            for (int i = 0; i < lusers.size(); i++) {
                NotifiesUser nu = new NotifiesUser();
                nu.setNid(n.getId());
                nu.setUname(lusers.get(i).getName());
                notifiesUsers.add(nu);
            }
            notifiesService.insertNotifyUserByList(notifiesUsers);
        }

        return new ModelAndView("redirect:/admin/messageList.do?msg=2");
    }

    private class PushRunnable implements Runnable {

        private final String title;
        private final String content;
        private final String ids;
        private final String username;
        private final String imgs;

        public PushRunnable(String title, String content, String ids, String username, String imgs) {
            this.title = title;
            this.content = content;
            this.ids = ids;
            this.username = username;
            this.imgs = imgs;
        }

        @Override
        public void run() {
            try {
                new MessageUtil().JPush(title, content, ids, username, imgs);
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (APIRequestException e) {
                e.printStackTrace();
            }
        }
    }

    //推送信息列表
    @RequestMapping("/messageList")
    public ModelAndView messageList(@RequestParam(defaultValue = "0") int msg, HttpServletRequest req, HttpServletResponse res, @RequestParam(value = "wantpage", defaultValue = "1") String wantpage) throws ParseException {
        int wp = Integer.parseInt(wantpage);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wp - 1) * 10);
        map.put("end", 10);
        List<Notifies> li = notifiesService.selectNotifys(map);
        int count = notifiesService.count();
        int pageCount = 0;
        if (count % 10 != 0) {
            pageCount = (count - (count % 10)) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        } else if (count < 10 && count > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }

        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List timelist = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            JSONObject json = new JSONObject();
//            long time = li.get(i).getCreateTime();
//            Date date = new Date(time);
            json.put("time", sdf.format(li.get(i).getCreateTime()));
//            Date date = li.get(i).getCreateTime();
//            json.put("time", sdf.format(date));
            timelist.add(json);
        }
        ModelAndView modelAndView = new ModelAndView();
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelAndView.addObject("addMsg", msg);
        modelAndView.addObject("idlist", idList);
        modelAndView.addObject("timelist", timelist);
        modelAndView.addObject("nowpage", wp);
        modelAndView.addObject("count", pageCount);
        modelAndView.addObject("list", li);
        modelAndView.setViewName("/PushList");
        return modelAndView;
    }

    //推送信息列表
    @RequestMapping("/messageSeach")
    public ModelAndView messageSeach(HttpServletRequest req, HttpServletResponse res, @RequestParam(value = "wantpage", defaultValue = "1") String wantpage, @RequestParam(defaultValue = "") String key) throws ParseException {
        int wp = Integer.parseInt(wantpage);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("begin", (wp - 1) * 10);
        map.put("end", 10);
        map.put("key", key);
        List<Notifies> li = notifiesService.selectNotifysSeach(map);
        int count = notifiesService.countSeach(key);
        int pageCount = 0;
        if (count % 10 != 0) {
            pageCount = (count - (count % 10)) / 10 + 1;
        } else if (count % 10 == 0) {
            pageCount = count / 10;
        } else if (count < 10 && count > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }

        String dateStr2 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        List timelist = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            JSONObject json = new JSONObject();
//            long time = li.get(i).getCreateTime();
//            Date date = new Date(time);
            json.put("time", sdf.format(li.get(i).getCreateTime()));
//            Date date = li.get(i).getCreateTime();
//            json.put("time", sdf.format(date));
            timelist.add(json);
        }
        ModelAndView modelAndView = new ModelAndView();
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelAndView.addObject("key", key);
        modelAndView.addObject("type", 2);
        modelAndView.addObject("idlist", idList);
        modelAndView.addObject("timelist", timelist);
        modelAndView.addObject("nowpage", wp);
        modelAndView.addObject("count", pageCount);
        modelAndView.addObject("list", li);
        modelAndView.setViewName("/PushList");
        return modelAndView;
    }

    //删除推送信息
    @RequestMapping("/pushDel")
    public void pushDel(int id, HttpServletResponse res, HttpSession session) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("删除'%s'邀请记录", id));

        notifiesService.deleteByPrimaryKey(id);
        res.getWriter().write("success");
    }
    //用户详细数据导出(近一个月)姓名、手机号、昵称、登录次数、聊天次数、发帖数、回帖数、浏览次数和反馈次数

    @RequestMapping("/DataUserExportInfo")
    public void UserDataInfo(HttpSession session, @RequestParam(defaultValue = "1") int wantpage, HttpServletResponse res) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("导出用户详细信息记录"));
        //用户详细数据list
        List listInfo=new ArrayList();
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        List<User> li = userMapper.getUserFY(map);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("总数据");
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);


        // 创建一个居中格式
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("姓名");
        cell.setCellStyle(style);
        cell = row.createCell((short) 1);
        cell.setCellValue("手机号");
        cell.setCellStyle(style);
        cell = row.createCell((short) 2);
        cell.setCellValue("昵称");
        cell.setCellStyle(style);
        cell = row.createCell((short) 3);
        cell.setCellValue("登录次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 4);
        cell.setCellValue("发帖次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 5);
        cell.setCellValue("回帖次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 6);
        cell.setCellValue("浏览次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 7);
        cell.setCellValue("反馈次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 8);
        for (int i = 0; i < li.size(); i++) {


            User user = userService.selectByPrimaryKey(li.get(i).getId());
            //用户的信息
            //登录的信息
            List<UserLoginLog> ulogin = userLoginLogMapper.UserLoginInfo(li.get(i).getId());
            List listInfo2=new ArrayList();
            if (user != null) {
                String raname=" ";
                String name=" ";
                String nickname=" ";
                row = sheet.createRow(i+1);
                row.createCell((short) 0).setCellValue(user.getRealname());
                raname=user.getRealname();
                row.createCell((short) 1).setCellValue(user.getName());
                name=user.getName();
                try {
                    row.createCell((short) 2).setCellValue(user.getNickname());
                    nickname=user.getNickname();
                } catch (NullPointerException e) {
                    row.createCell((short) 2).setCellValue("");
                    listInfo2.add("");
                }
                //最近一月各种次数
                row.createCell((short) 3).setCellValue(ulogin.size());
                row.createCell((short) 4).setCellValue(articleMapper.selectMMCount(li.get(i).getId()).size());
                row.createCell((short) 5).setCellValue(articleCommentMapper.commentMM(li.get(i).getId()).size());
                row.createCell((short) 6).setCellValue(userViewArticleLogMapper.ViewBeginEnd(li.get(i).getId()).size());
                row.createCell((short) 7).setCellValue(feedbackMapper.FeedBackMM(li.get(i).getId()).size());

//                row = sheet.createRow(++lastRow);
//                row.createCell(0).setCellValue("登录开始时间");
//                row.createCell(1).setCellValue("登录结束时间");
//
//                row.createCell(2).setCellValue("发帖时间");
//                row.createCell(3).setCellValue("回帖时间");
//                row.createCell(4).setCellValue("反馈时间");
//
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//                row.createCell(5).setCellValue("浏览开始时间");
//                row.createCell(6).setCellValue("浏览结束时间");
                int[] A = new int[]{ulogin.size(), articleMapper.selectMMCount(li.get(i).getId()).size(), articleCommentMapper.commentMM(li.get(i).getId()).size(), userViewArticleLogMapper.ViewBeginEnd(li.get(i).getId()).size(), feedbackMapper.FeedBackMM(li.get(i).getId()).size()};
                int temp = 0;
                int k, min, max;
                min = max = A[0];
                for (k = 0; k < A.length; k++) {
                    if (A[k] > max)   // 判断最大值
                        max = A[k];
                    if (A[k] < min)   // 判断最小值
                        min = A[k];
                }

                temp=max;


                List<UserViewArticleLog> ViewList = userViewArticleLogMapper.ViewBeginEnd(li.get(i).getId());
                for (int h = 0; h < temp; h++) {
                    listInfo2.add(raname);
                    listInfo2.add(name);
                    listInfo2.add(nickname);

                    String timeBegin = "";
                    String timeOut = "";
                    try {
                     timeBegin = sdf.format(ulogin.get(h).getLoginTime());
                    } catch (Exception e) {
                    }
                    try{  if (ulogin.get(h).getLogoutTime() != null) {
                        timeOut = sdf.format(ulogin.get(h).getLogoutTime());
                    }
                    }catch(Exception e){

                    }
//                    row.createCell(0).setCellValue(timeBegin);
//                    row.createCell(1).setCellValue(timeOut);
                    listInfo2.add(timeBegin);
                    listInfo2.add(timeOut);

                    String mmTime="";
                    try {
                        if (articleMapper.selectMMCount(li.get(i).getId()).get(h).getTime() != null) {
//                            row.createCell(2).setCellValue(sdf.format(articleMapper.selectMMCount(li.get(i).getId()).get(h).getTime()));
                           mmTime=sdf.format(articleMapper.selectMMCount(li.get(i).getId()).get(h).getTime());
                        }
                    } catch (Exception e) {

                    }
                    listInfo2.add(mmTime);
                    String cmmtime="";
                    try {
                        if (articleCommentMapper.commentMM(li.get(i).getId()).get(h).getTime() != null) {
//                            row.createCell(3).setCellValue(sdf.format(articleCommentMapper.commentMM(li.get(i).getId()).get(h).getTime()));
                            cmmtime=  sdf.format(articleCommentMapper.commentMM(li.get(i).getId()).get(h).getTime());
                        }
                    } catch (Exception e) {

                    }
                    listInfo2.add(cmmtime);
                    String fbTime="";
                    try {
                        if (feedbackMapper.FeedBackMM(li.get(i).getId()).get(h).getTime() != null) {
//                             listInfo2.addrow.createCell(4).setCellValue(sdf.format(feedbackMapper.FeedBackMM(li.get(i).getId()).get(h).getTime()));
                             fbTime=sdf.format(sdf.format(feedbackMapper.FeedBackMM(li.get(i).getId()).get(h).getTime()));
                        }
                    } catch (Exception e) {

                    }
                    listInfo2.add(fbTime);
                    String timeBeginA="";
                    String timeEndA="";
                    try {
                        if (ViewList.get(h).getTime() != null) {
                            timeBeginA = sdf.format(ViewList.get(h).getTime());
                        }
                        if (ViewList.get(h).getTimeEnd() != null) {
                            timeEndA = sdf.format(ViewList.get(h).getTimeEnd());
                        }
//                        row.createCell(5).setCellValue(timeBegin);
//                        row.createCell(6).setCellValue(timeOut);

                    } catch (Exception e) {
                    }
                    listInfo2.add(timeBeginA);
                    listInfo2.add(timeEndA);
                }

       }
         listInfo.add(listInfo2);
        }
        HSSFSheet  sheet2 = wb.createSheet("详细数据");
       style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFRow  row2 = sheet2.createRow(0);
        row2.createCell(0).setCellValue("姓名");
        row2.createCell(1).setCellValue("手机号");
        row2.createCell(2).setCellValue("昵称");
        row2.createCell(3).setCellValue("登录开始时间");
        row2.createCell(4).setCellValue("登录结束时间");
        row2.createCell(5).setCellValue("发帖时间");
        row2.createCell(6).setCellValue("回帖时间");
        row2.createCell(7).setCellValue("反馈时间");
        row2.createCell(8).setCellValue("浏览开始");
        row2.createCell(9).setCellValue("浏览结束");
    int lastrow=1;
        for(int i=0; i<listInfo.size();i++)
        {
        List list2=(List)listInfo.get(i);

            if(list2.size()==0||list2.get(1).toString().length()==0){
                continue;
        }
            row2 = sheet2.createRow(lastrow);

            for(int j=0;j<list2.size();j++){

                if(j>9){
                    if(j%10==0){
                        lastrow++;
                        row2 = sheet2.createRow(lastrow);
                    }
                    try{
                        row2.createCell(j%10).setCellValue(list2.get(j).toString());

                    }catch (Exception e){
                        row2.createCell(j%10).setCellValue(" ");

                    }

                    }else{
                    try{
                        row2.createCell(j).setCellValue(list2.get(j).toString());

                    }catch (Exception e){
                        row2.createCell(j).setCellValue(" ");

                    }
                }


            }
            lastrow++;
        }
        OutputStream out = null;//创建一个输出流对象
        out = res.getOutputStream();
        String headerStr = "用户数据";
        headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");
        res.setHeader("Content-disposition", "attachment; filename=" + headerStr + ".xls");//filename是下载的xls的名，建议最好用英文
        res.setContentType("application/msexcel;charset=UTF-8");//设置类型
        res.setHeader("Pragma", "No-cache");//设置头
        res.setHeader("Cache-Control", "no-cache");//设置头
        res.setDateHeader("Expires", 0);//设置日期头
        wb.write(out);
        out.flush();
        if (out != null) {
            out.close();
        }

    }


    //用户总数据导出


    //用户数据导出
    @RequestMapping("/DataUserExport")
    public void export(HttpSession session, HttpServletRequest req, HttpServletResponse res, @RequestParam(defaultValue = "1") int wantpage,
                       @RequestParam(defaultValue = "0") String url) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("导出用户数据"));

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantpage - 1) * 10);
        map.put("end", 10);
        List<User> li = userMapper.getUserFY(map);
        List list = new ArrayList();
        for (int i = 0; i < li.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("id", li.get(i).getId());
            json.put("tel", li.get(i).getName());
            json.put("name", li.get(i).getRealname());
            json.put("nickname", li.get(i).getNickname());
            //查询登录次数0本月1上月

            int loginCount = userLoginLogMapper.selectCountLogin(li.get(i).getId());
            json.put("loginCount", loginCount);
            //查询反馈次数
            int feedCount = feedbackMapper.UserfeedBackCount(li.get(i).getId());
            json.put("feedbackCount", feedCount);
            //聊天数据
            User u = userService.selectByPrimaryKey(li.get(i).getId());
            //问卷次数
            int wenjuan = u.getQuestionnaireTime();
            json.put("wenjuan", wenjuan);
            //浏览次数
            int viewCountarticle = userViewArticleLogMapper.ViewCount(li.get(i).getId());
            json.put("va", viewCountarticle);
            int viewCountdiary = userViewDiaryLogMapper.ViewCount(li.get(i).getId());
            json.put("vd", viewCountdiary);
            //评论次数
            int a = articleCommentMapper.UserComment(li.get(i).getId());
            int d = diaryCommentMapper.Usercomment(li.get(i).getId());
            json.put("comment", a + d);


            list.add(json);
        }

//            String path = req.getServletContext().getRealPath("/");
        //String path ="C://Users//Administrator//Desktop";
        //下载
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("用户信息");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("姓名");
        cell.setCellStyle(style);
        cell = row.createCell((short) 1);
        cell.setCellValue("手机号");
        cell.setCellStyle(style);
        cell = row.createCell((short) 2);
        cell.setCellValue("昵称");
        cell.setCellStyle(style);

        cell = row.createCell((short) 3);
        cell.setCellValue("填写问卷次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 4);
        cell.setCellValue("发言次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 5);
        cell.setCellValue("累计登录次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 6);
        cell.setCellValue("提交意见与反馈次数");
        cell.setCellStyle(style);
        cell = row.createCell((short) 7);
        cell.setCellValue("浏览帖子次数");
        cell.setCellStyle(style);
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = (JSONObject) list.get(i);
            row = sheet.createRow((int) i + 1);
            // 第四步，创建单元格，并设置值
            try {
                row.createCell((short) 0).setCellValue(json.get("name").toString());

            } catch (NullPointerException e) {
                row.createCell((short) 0).setCellValue("null");

            }
            try {
                row.createCell((short) 1).setCellValue(json.get("tel").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 1).setCellValue("null");
            }
            try {
                row.createCell((short) 2).setCellValue(json.get("nickname").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 2).setCellValue("null");
            }
            try {
                row.createCell((short) 3).setCellValue(json.get("wenjuan").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 3).setCellValue("null");
            }
            try {
                row.createCell((short) 4).setCellValue(json.get("comment").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 4).setCellValue("null");
            }
            try {
                row.createCell((short) 5).setCellValue(json.get("loginCount").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 5).setCellValue("null");
            }
            try {
                row.createCell((short) 6).setCellValue(json.get("feedbackCount").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 6).setCellValue("null");
            }
            try {
                row.createCell((short) 7).setCellValue(json.get("va").toString());
            } catch (NullPointerException e) {
                row.createCell((short) 7).setCellValue("null");
            }


        }
        OutputStream out = null;//创建一个输出流对象
        out = res.getOutputStream();
        String headerStr = "用户数据";
        headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");
        res.setHeader("Content-disposition", "attachment; filename=" + headerStr + ".xls");//filename是下载的xls的名，建议最好用英文
        res.setContentType("application/msexcel;charset=UTF-8");//设置类型
        res.setHeader("Pragma", "No-cache");//设置头
        res.setHeader("Cache-Control", "no-cache");//设置头
        res.setDateHeader("Expires", 0);//设置日期头
        wb.write(out);
        out.flush();
        if (out != null) {
            out.close();
        }

    }

    /**
     * 管理员账户无
     *
     * @param msg
     * @param gid
     * @param req
     * @param res
     * @throws IOException
     */
    @RequestMapping("/messageSendText")
    public void messageSend(String msg, String gid, HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
//        String content = new String(msg.getBytes("iso-8859-1"), "utf-8");
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("发送'%s'聊天群组公告", msg));

        User ad = (User) session.getAttribute("adminUser");
        String from = ad.getName();
        EasemobMessages.senMessage(from, msg, gid);
        res.getWriter().write("success");
    }

    /**
     * 修改用户信息
     * 显示
     *
     * @throws SQLException
     */
    @RequestMapping("/UserChangView")
    public ModelAndView userchangView(int id) throws SQLException {
        User user = userService.selectByPrimaryKey(id);
        ModelAndView mav = new ModelAndView();
        propertyController pc = new propertyController();
        JSONObject js = pc.selectUesrProperty(id);
        List clist = (List) js.get("cinfo");
        List ilist = (List) js.get("info");
        List cx = (List) js.get("cx");
        int x = 0;
        List allList = new ArrayList();
        int h = 0;
        int l = 0;
        JSONObject jsonObj = new JSONObject();
        for (int i = clist.size() * h; i < ilist.size(); i++) {
            if (l == clist.size()) {
                l = 0;
            }
            String regEx = "date|day|hours|minutes|month|nanos|seconds|time|timezoneOffset|year";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(ilist.get(i).toString());
            boolean rs = m.find();
            if (rs == true) {
                JSONObject createDate = (JSONObject) ilist.get(i);
                Date tempDate = (Date) JSONObject.toBean(createDate, Date.class);
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempDate);
                try {
                    jsonObj.put(clist.get(l), createTime);
                } catch (Exception e) {
                    jsonObj.put(clist.get(l), ilist.get(i));
                }
            } else {
                jsonObj.put(clist.get(l), ilist.get(i));
            }
            if ((i + 1) % clist.size() == 0) {

                allList.add(x, jsonObj);
                x++;
                jsonObj = new JSONObject();
            }
            h++;
            l++;
        }
        mav.addObject("all", allList);
        mav.addObject("clist", cx);
        mav.addObject("id", id);
        mav.addObject("pwd", user.getPassword());
        mav.addObject("name", user.getRealname());
        mav.addObject("email", user.getEmail());
        mav.addObject("tel", user.getName());
        mav.addObject("nickname", user.getNickname());
        // mav.addObject("group", user.getGroup());
        mav.addObject("group", null);
        mav.addObject("score", user.getScore());
        mav.addObject("sex", user.getSex());
        mav.setViewName("/UserChang");
        return mav;
    }

    /**
     * 修改用户信息
     * 修改
     *
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping("/UserChang")
    public void userchang(HttpSession session, User user, String nickname, @RequestParam(defaultValue = "null") String array, @RequestParam(defaultValue = "null") String array2, HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException {
        if (!array.equals("null") && !array.equals("null")) {
            session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了'%s'用户的详细信息", user));

//            String a1 = new String(array.getBytes("iso-8859-1"), "utf-8");
//            String a2 = new String(array2.getBytes("iso-8859-1"), "utf-8");
            String a1 = array;
            String a2 = array2;
            String array01[] = a1.split(",");
            List cname = new ArrayList();
            for (int i = 0; i < array01.length; i++) {
                cname.add(array01[i]);
            }
            String array02[] = a2.split(",");
            List cvalue = new ArrayList();
            for (int i = 0; i < array02.length; i++) {
                cvalue.add(array02[i]);
            }

            propertyController ep = new propertyController();
            for (int i = 0; i < cname.size(); i++) {
                try {
                    ep.changProperty(cname.get(i).toString(), user.getId(), cvalue.get(i).toString());
                } catch (IndexOutOfBoundsException ioe) {
                    ep.changProperty(cname.get(i).toString(), user.getId(), " ");

                }
            }


        }
        String n = req.getParameter("nickname");
        user.setNickname(n);
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("修改了'%s'用户的详细信息", n));

        userMapper.updateByPrimaryKey(user);
        res.getWriter().write("success");
    }

    /**
     * 所有分组的显示
     */

    public List quanxian() {
        //获取所有群组
        HxUtil hu = new HxUtil();
        List li = hu.getGroupInfo();
        return li;
    }

    //权限页面
    @RequestMapping("/groupQuanxian")
    public ModelAndView groupQuanxian(@RequestParam(defaultValue = "1") int wantPage, HttpServletRequest req, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView();
        List li = this.quanxian();
        mav.addObject("group", li);
        mav.setViewName("/quanxianAdd");
        //显示所有用户
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("begin", (wantPage - 1) * 10);
        map.put("end", 10);
        List<User> userFY = userMapper.getUserFY(map);
        List Userli = new ArrayList();
        for (User u : userFY) {
            JSONObject j = new JSONObject();
            String dateStr = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            j.put("time", sdf.format(u.getRegisterTime()));
            j.put("nickname", u.getNickname());
            j.put("tel", u.getName());
            Userli.add(j);
        }
        int count = userMapper.UserCount();
        if (count > 10 && count % 10 == 0) {
            count = count / 10;
        } else if (count > 10 && count % 10 != 0) {
            count = (count - count % 10) / 10 + 1;
        } else if (count > 0 && count <= 10) {
            count = 1;
        } else {
            count = 0;
        }
        mav.addObject("ul", Userli);
        mav.addObject("nowpage", wantPage);
        mav.addObject("count", count);
        return mav;
    }

    /**
     * 话题发布权限界面
     */
    @RequestMapping("/articleAddView")
    public ModelAndView articleAddView(@RequestParam(defaultValue = "1") int wantPage, HttpServletRequest req, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("group", groupService.getExistsGroupInfo(true));
        mav.setViewName("/kindeditor-master/jsp/demo");
        return mav;
    }

    //*************权限添加
    @RequestMapping("/groupQuanxianAdd")
    public void QuanxianAdd(String user, String gid, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String a[] = user.split(",");
        List userList = new ArrayList();
        for (int i = 0; i < a.length; i++) {
            userList.add(a[i]);
        }
        String b[] = gid.split(",");
        List gidList = new ArrayList();
        for (int i = 0; i < b.length; i++) {
            gidList.add(b[i]);
        }
        //权限增加
        HxUtil hu = new HxUtil();
        for (int i = 0; i < gidList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                hu.groupAddUser(userList.get(j).toString(), gidList.get(i).toString());
            }
        }
        res.getWriter().write("success");
    }

    @RequestMapping("/userGroup")
    public ModelAndView userGroupList(@RequestParam(defaultValue = "3") int msg) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/UserGroupList");


        modelAndView.addObject("grps", this.groupService.getExistsGroup());
        List li = groupService.getExistsGroupInfo(true);
        modelAndView.addObject("group", li);
        modelAndView.addObject("msg", msg);
        modelAndView.addObject("extendsInfo", columnService.selectAllColumn());
        return modelAndView;
    }

    @RequestMapping("/userGroupModify")
    public ModelAndView userGroupModify(@RequestParam String grp) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/UserGroupModify");
        HashMap<String, Object> config = new HashMap<>();
        config.put("grp", grp);
        modelAndView.addObject("grpName", grp);
        modelAndView.addObject("users", this.userService.getUserListUnderGrp(config));
        return modelAndView;
    }

    @RequestMapping("/userGroupUserAdd")
    public ModelAndView userGroupUserAdd(@RequestParam String grp,
                                         @RequestParam(required = false) Integer sex,
                                         @RequestParam(required = false) Integer ageS,
                                         @RequestParam(required = false) Integer ageE,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String degree,
                                         @RequestParam(required = false) String marriage,
                                         @RequestParam(required = false) String key) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/UserGroupUserAdd");
        HashMap<String, Object> config = new HashMap<>();
        config.put("grp", grp);
        config.put("sex", sex == null ? -1 : sex);
        config.put("ageS", ageS);
        config.put("ageE", ageE);
        config.put("city", CommonUtil.processLikeParameter(city));
        config.put("degree", degree);
        config.put("marriage", marriage);
        config.put("key", CommonUtil.processLikeParameter(key));
        config.put("rawKey", key);
        modelAndView.addObject("extendsInfo", columnService.selectAllColumn());
        modelAndView.addObject("grpName", grp);
        modelAndView.addObject("users", this.userService.unGroupedUser(config));
        modelAndView.addObject("params", config);
        return modelAndView;
    }

    @RequestMapping("/userGroupNew")
    public ModelAndView userGroupNew(
            @RequestParam(required = false) Integer msg,
            @RequestParam(required = false) Integer sex,
            @RequestParam(required = false) Integer ageS,
            @RequestParam(required = false) Integer ageE,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) String marriage,
            @RequestParam(required = false) String key) throws UnsupportedEncodingException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/UserGroupNew");
        modelAndView.addObject("msg", msg);


        List li = groupService.getExistsGroupInfo(true);
        modelAndView.addObject("group", li);
        modelAndView.addObject("extendsInfo", columnService.selectAllColumn());


        HashMap config = new HashMap();
        config.put("sex", sex == null ? -1 : sex);
        config.put("ageS", ageS);
        config.put("ageE", ageE);
        config.put("city", CommonUtil.processLikeParameter(city));
        config.put("degree", degree);
        config.put("marriage", marriage);
        config.put("key", StringUtils.isEmpty(key) ? null : "%" + key + "%");
        config.put("rawKey", key);
        modelAndView.addObject("users", this.userService.unGroupedUser(config));
        modelAndView.addObject("params", config);
        return modelAndView;
    }

    @RequestMapping("/articleGroupManage")
    public ModelAndView articleGroupManage(@RequestParam int id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/ArticleGroupManage");
        modelAndView.addObject("article", this.articleMapper.selectByPrimaryKey(Integer.valueOf(id), null));

        try {
            modelAndView.addObject("avail", (new ObjectMapper()).writeValueAsString(this.articleEnableService.getAvail(Integer.valueOf(id))));
        } catch (JsonProcessingException var4) {
            var4.printStackTrace();
        }

        modelAndView.addObject("existGroups", this.groupService.getExistsGroup());
        return modelAndView;
    }


    //充值
    @RequestMapping("/Chong")
    public void Chong(HttpSession session, String tel, int money, HttpServletResponse res) throws NoSuchAlgorithmException, IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("为'%s'用户充值了'%s'", tel, money));


        chongZhi cz = new chongZhi();
        cz.chong(tel, money, "123");
        res.getWriter().write("success");
    }

    //导入用户属性
    @RequestMapping("exportUser")
    public ModelAndView exportlu(HttpServletRequest req, HttpServletResponse res, @RequestParam("file") MultipartFile file, @RequestParam(value = "wantpage", defaultValue = "1") String wantpage, ModelMap modelmap, HttpSession session) throws Exception, SQLException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("增加了用户扩展属性"));

        ModelAndView modelAndView = new ModelAndView();
        if (!file.isEmpty()) {
            String filePath = "";
            try {
                // 文件保存路径
                filePath = req.getSession().getServletContext().getRealPath("/") + "upload/"
                        + file.getOriginalFilename();
                // 转存文件
                file.transferTo(new File(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            propertyController pc = new propertyController();
            JSONObject json = pc.insert(filePath, res, req);
            modelAndView.addObject("msg1", json.get("message"));
            File file2 = new File(filePath);
            if (file2.exists()) {
                file2.delete();
            }
        }
        propertyController pc = new propertyController();
        int wp = Integer.parseInt(wantpage);
        int begin = (wp - 1) * 10;
        JSONObject js = pc.info(begin);
        List clist = (List) js.get("cinfo");
        List ilist = (List) js.get("info");
        List cx = (List) js.get("cx");
        int x = 0;
        List allList = new ArrayList();
        int h = 0;
        int l = 0;
        JSONObject jsonObj = new JSONObject();
        for (int i = clist.size() * h; i < ilist.size(); i++) {
            if (l == clist.size()) {
                l = 0;
            }
            String regEx = "date|day|hours|minutes|month|nanos|seconds|time|timezoneOffset|year";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(ilist.get(i).toString());
            boolean rs = m.find();
            if (rs) {
                JSONObject createDate = (JSONObject) ilist.get(i);
                Date tempDate = (Date) JSONObject.toBean(createDate, Date.class);
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempDate);
                try {
                    jsonObj.put(clist.get(l), createTime);
                } catch (Exception e) {
                    jsonObj.put(clist.get(l), ilist.get(i));
                }
            } else {
                jsonObj.put(clist.get(l), ilist.get(i));
            }
            if ((i + 1) % clist.size() == 0) {

                allList.add(x, jsonObj);
                x++;
                jsonObj = new JSONObject();
            }
            h++;
            l++;
        }
        JSONArray ja = new JSONArray();
        ja.add(allList);
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelmap.put("idlist", idList);
        modelAndView.addObject("all", allList);
        modelAndView.addObject("clist", cx);
        //用户总数
        JSONObject result = new JSONObject();
        int pagesize = 10;
        int UserCount = userMapper.UserCount();
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % pagesize)) / pagesize + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        //pageCount = UserCount / pagesize + (UserCount % pagesize == 0 ? 0 : 1);
        List li = this.quanxian();
        modelAndView.addObject("group", li);
        modelAndView.addObject("nowpage", wantpage);
        modelAndView.addObject("pagecount", pageCount);
        modelAndView.setViewName("/UserList");
        return modelAndView;


    }

    //批量excel添加用户
    @RequestMapping(value = "/inserUsers", method = RequestMethod.POST)
    public ModelAndView inserUsers(HttpSession session, HttpServletRequest req, @RequestParam("file") MultipartFile file, ModelMap modelmap) throws IOException, SQLException, ParseException {
        //获取路径
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("批量增加了用户"));

        ModelAndView modelAndView = new ModelAndView();
        List li = new ArrayList();
        propertyController pc = new propertyController();
        if (!file.isEmpty()) {
            String filePath = "";
            try {
                // 文件保存路径
                filePath = req.getSession().getServletContext().getRealPath("/") + "upload/"
                        + file.getOriginalFilename();
                // 转存文件
                file.transferTo(new File(filePath));
                String path = filePath;
                li = pc.inserUserS(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (li.size() == 0) {
                modelAndView.addObject("inserUsers", "success");
                modelAndView.addObject("messageList", li);
            } else {
                modelAndView.addObject("inserUsers", "error");
                modelAndView.addObject("messageList", li);
            }

        } else {
            modelAndView.addObject("inserUsers", "error");
            modelAndView.addObject("messageList", li);
        }
        modelAndView.addObject("grps", this.groupService.getExistsGroup());
        int wp = 1;
        int begin = (wp - 1) * 10;
        JSONObject js = pc.info(begin);
        List clist = (List) js.get("cinfo");
        List ilist = (List) js.get("info");
        List cx = (List) js.get("cx");
        int x = 0;
        List allList = new ArrayList();
        int h = 0;
        int l = 0;
        JSONObject jsonObj = new JSONObject();
        for (int i = clist.size() * h; i < ilist.size(); i++) {
            if (l == clist.size()) {
                l = 0;
            }
            String regEx = "date|day|hours|minutes|month|nanos|seconds|time|timezoneOffset|year";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(ilist.get(i).toString());
            boolean rs = m.find();
            if (rs == true) {
                JSONObject createDate = (JSONObject) ilist.get(i);
                Date tempDate = (Date) JSONObject.toBean(createDate, Date.class);
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempDate);
                try {
                    jsonObj.put(clist.get(l), createTime);
                } catch (Exception e) {
                    jsonObj.put(clist.get(l), ilist.get(i));
                }
            } else {
                jsonObj.put(clist.get(l), ilist.get(i));
            }
            if ((i + 1) % clist.size() == 0) {

                allList.add(x, jsonObj);
                x++;
                jsonObj = new JSONObject();
            }
            h++;
            l++;
        }
        JSONArray ja = new JSONArray();
        ja.add(allList);
        List idList = new ArrayList();
        for (int i = 1; i <= 10; i++) {
            idList.add((wp - 1) * 10 + i);
        }
        modelmap.put("idlist", idList);
        modelAndView.addObject("all", allList);
        modelAndView.addObject("clist", cx);
        //用户总数
        JSONObject result = new JSONObject();
        int pagesize = 10;
        int UserCount = userMapper.UserCount();
        int pageCount = 0;
        if (UserCount % 10 != 0) {
            pageCount = (UserCount - (UserCount % pagesize)) / pagesize + 1;
        } else if (UserCount % 10 == 0) {
            pageCount = UserCount / 10;
        } else if (UserCount < 10 && UserCount > 0) {
            pageCount = 1;
        } else {
            pageCount = 0;
        }
        modelAndView.addObject("nowpage", 1);
        modelAndView.addObject("pagecount", pageCount);
        modelAndView.setViewName("/UserList");
        return modelAndView;

    }

    /**
     * 删除组成员
     *
     * @throws IOException
     */
    @RequestMapping("/deleteGroup")
    public void delteGroup(HttpSession session, String gid, HttpServletResponse res) throws IOException {
        session.setAttribute(SESSION_KEY_OPERATOR_RECORD, String.format("删除了群组'%s'", gid));

        HxUtil hu = new HxUtil();
        hu.deleteGroup(gid);
        res.getWriter().write("success");
    }

    //实时查询
    @RequestMapping("/getArticleTitle")
    public void getArticleTitle(String content, HttpServletResponse res) throws IOException {
        List<Article> al = articleMapper.getArticleTitle(content);
        JSONObject jo = new JSONObject();
        jo.put("li", al);
        res.getWriter().write(jo.toString());
    }
}
