<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!-- saved from url=(0070)http://139.196.210.151/1/json.php?mod=admin&act=newUserPage&1465824954 -->
<html lang="en">
<%@ page language="java" pageEncoding="UTF-8" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Admin 2 - Bootstrap Admin Theme</title>
    <!-- jQuery -->
    <script src="//cdn.bootcss.com/jquery/2.1.3/jquery.min.js"></script>
    <script type="text/javascript" src="/vcbbs/page/loadContent.js"></script>
    <!-- Bootstrap Core CSS -->
    <link href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css"
          rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="//cdn.bootcss.com/metisMenu/1.1.3/metisMenu.min.css"
          rel="stylesheet">

    <!-- Custom CSS -->
    <link href="/vcbbs/css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="//cdn.bootcss.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <link href="http://139.196.210.151/statics/uploadify/uploadify.css" rel="stylesheet" type="text/css">
    <style>
        ul {
            list-style: none;
        }

        ul li {
            list-style: none;
        }

        #filter {
            text-align: left;
        }

        #list1 {
            border-right: 1px solid black;
            text-align: left;
            width: 120px;
            display: inline-block;
        }

        #list2 {
            text-align: left;
            width: 200px;
            display: inline-block;
            max-height: 150px;
            overflow-x: hidden;
        }
    </style>
</head>
<body>
<div id="wrapper">
    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${basePath}/page/index.jsp">Admin v2.0</a>
        </div>

        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav navbar-collapse">
                <ul class="nav" id="side-menu">
                    <li class="sidebar-search">
                        <div class="input-group custom-search-form">
                            <input type="text" class="form-control" placeholder="Search...">
                            <span class="input-group-btn">
                                    <button class="btn btn-default" type="button">
                                        <i class="fa fa-search"></i>
                                    </button>
                                </span>
                        </div>
                        <!-- /input-group -->
                    </li>
                    <li>
                        <a href="${basePath}/page/index.jsp"><i class="fa fa-dashboard fa-fw"></i> 首页</a>
                    <li><a href=""><i class="fa fa-empire fa-fw"></i> 帐号管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li class="hideIfNotSuper"><a href="${basePath}/page/adminList.jsp">管理员列表</a></li>
                            <li class="hideIfNotSuper"><a href="${basePath}/page/adminAdd.jsp">添加管理员</a></li>
                            <li><a href="${basePath}/page/adminPass.jsp">修改密码</a></li>
                        </ul>
                    </li>


                    <li><a href=""><i class="fa fa-drupal fa-fw"></i>
                        用户管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/userList.do">用户列表</a></li>
                            <li><a href="${basePath}/page/UserAdd.jsp">新增用户</a></li>
                        </ul>
                    </li>


                    <li><a href=""><i class="fa fa-drupal fa-fw"></i>
                        讨论区管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse in">
                            <li><a href="${basePath}/admin/userGroup.do" class="active"
                                   style="color:white;background-color:#DEDEDE">讨论区列表</a></li>
                            <li><a href="${basePath}/admin/userGroupNew.do">新增讨论区</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-empire fa-fw"></i> 话题管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/articleList.do">话题列表</a></li>
                            <li><a href="${basePath}/admin/articleAddView.do">话题发布</a></li>

                        </ul>
                    </li>

                    <li><a href="">
                        <i class="fa fa-empire fa-fw"></i> 聊天管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/groupInfo.do">聊天群组列表</a></li>
                            <li><a href="${basePath}/admin/allUser.do">新增聊天群组</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 日记管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/diaryList.do">日记列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-gittip fa-fw"></i> 推送通知<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/messageList.do">推送列表</a></li>
                            <li><a href="${basePath}/admin/sendPushView.do">新建推送</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 积分管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/ScoreInfo.do">积分规则修改</a></li>
                            <li><a href="${basePath}/admin/ScoreList.do">积分规则展示</a></li>
                            <li><a href="${basePath}/admin/ScoreUser.do">用户积分列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 商品管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/goodExchangeHistory.do">兑换列表</a></li>
                            <li><a href="${basePath}/admin/goodList.do">商品列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-gittip fa-fw"></i> 用户反馈<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/feedbackList.do">反馈列表</a></li>
                            <li><a href="${basePath}/page/Help.jsp">帮助</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 用户数据分析<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/admin/userData.do">用户数据</a></li>
                            <li><a href="${basePath}/admin/ArticleCommentData.do">回帖数据</a></li>
                            <li><a href="${basePath}/admin/messageHistory.do">聊天数据</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!-- /.sidebar-collapse -->
        </div>
        <!-- /.navbar-static-side -->
    </nav>

    <!-- Page Content -->
    <div id="page-wrapper" style="min-height: 685px;">
        <div class="container-fluid">
            <h1>${article.title}</h1>
            <ul id="list1">
                <c:forEach items="${existGroups}" var="list1" varStatus="status">
                    <li>
                        <input type="checkbox" onchange="return loadContent(this)"
                               value="<c:out value="${list1}"/>">
                        <c:out value="${list1}"/>
                    </li>
                </c:forEach>
            </ul>
            <ul id="list2"></ul>
            <button type="button" class="btn" onclick="saveAuthority()">提交</button>
        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->
</div>

<!-- Bootstrap Core JavaScript -->
<script src="//cdn.bootcss.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="//cdn.bootcss.com/metisMenu/1.1.3/metisMenu.min.js"></script>

<script src="/vcbbs/js/sb-admin-2.js"></script>

<!-- Custom Theme JavaScript -->
<script>var isSuperAdmin = ${sessionScope.adminUser.superAdmin?'true':'false'};</script>
<script src="http://139.196.210.151/statics//bootstrap/js/bootbox.js"></script>

<script src="http://139.196.210.151/statics//uploadify/jquery.uploadify.js" type="text/javascript"></script>
<script type="text/javascript">
    selectedGrpOrUser = JSON.parse('${avail}');

    function saveAuthority() {
        $.post(BASE_DIR + "/article/avail/set.do", {
            id: '${article.id}',
            availUserOrGrp: JSON.stringify(selectedGrpOrUser)
        }, function (data) {
            if (data.code == 0) {
                location.href = BASE_DIR + "/"
            } else {
                alert(data.message);
            }
        })
    }
</script>


</body>
</html>