<!DOCTYPE html>
<!-- saved from url=(0070)http://139.196.210.151/1/json.php?mod=admin&act=newUserPage&1465824954 -->
<html lang="en">
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Admin 2 - Bootstrap Admin Theme</title>
    <!-- Bootstrap Core CSS -->
    <link href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="//cdn.bootcss.com/metisMenu/1.1.3/metisMenu.min.css" rel="stylesheet">

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

</head>
<style>
    .a-upload {
        padding: 4px 10px;
        height: 20px;
        line-height: 20px;
        position: relative;
        cursor: pointer;
        color: #888;
        background: #fafafa;
        border: 1px solid #ddd;
        border-radius: 4px;
        overflow: hidden;
        display: inline-block;
        *display: inline;
        *zoom: 1
    }

    .a-upload input {
        position: absolute;
        font-size: 100px;
        right: 0;
        top: 0;
        opacity: 0;
        filter: alpha(opacity=0);
        cursor: pointer
    }

    .a-upload:hover {
        color: #444;
        background: #eee;
        border-color: #ccc;
        text-decoration: none
    }

    /*file*/
    .file {
        width: 100px;
        position: relative;
        display: inline-block;
        background: #ed5f00;
        color: white;
        border: 0px;
        border-radius: 4px;
        padding: 4px 12px;
        overflow: hidden;
        text-decoration: none;
        text-indent: 0;
        line-height: 20px;
    }

    .file input {
        position: absolute;
        font-size: 100px;
        right: 0;
        top: 0;
        opacity: 0;
    }

    .file:hover {
        background: #ed5f00;
        color: white;
        border: 0px;
        text-decoration: none;
    }
</style>
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
            <a class="navbar-brand" href="${basePath}/vcbbs/page/index.jsp">Admin v2.0</a>
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
                        <a href="${basePath}/vcbbs/page/index.jsp"><i class="fa fa-dashboard fa-fw"></i> 首页</a>
                    <li><a href=""><i class="fa fa-empire fa-fw"></i> 帐号管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li class='hideIfNotSuper'><a href="${basePath}/vcbbs/page/adminList.jsp">管理员列表</a></li>
                            <li class='hideIfNotSuper'><a href="${basePath}/vcbbs/page/adminAdd.jsp">添加管理员</a></li>
                            <li><a href="${basePath}/vcbbs/page/adminPass.jsp">修改密码</a></li>
                        </ul>
                    </li>


                    <li><a href=""><i class="fa fa-drupal fa-fw"></i>
                        用户管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/userList.do">用户列表</a></li>
                            <li><a href="${basePath}/vcbbs/page/UserAdd.jsp" class="active"
                                   style="color:white;background-color:#DEDEDE">新增用户</a></li>
                        </ul>
                    </li>


                    <li><a href=""><i class="fa fa-drupal fa-fw"></i>
                        讨论区管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/userGroup.do">讨论区列表</a></li>
                            <li><a href="${basePath}/vcbbs/admin/userGroupNew.do">新增讨论区</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-empire fa-fw"></i> 话题管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/articleList.do">话题列表</a></li>
                            <li><a href="${basePath}/vcbbs/admin/articleAddView.do">话题发布</a></li>

                        </ul>
                    </li>

                    <li><a href="">
                        <i class="fa fa-empire fa-fw"></i> 聊天管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/groupInfo.do">聊天群组列表</a></li>
                            <li><a href="${basePath}/vcbbs/admin/allUser.do">新增聊天群组</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 日记管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/diaryList.do">日记列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-gittip fa-fw"></i> 推送通知<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/messageList.do">推送列表</a></li>
                            <li><a href="${basePath}/vcbbs/admin/sendPushView.do">新建推送</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 积分管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/ScoreInfo.do">积分规则修改</a></li>
                            <li><a href="${basePath}/vcbbs/admin/ScoreList.do">积分规则展示</a></li>
                            <li><a href="${basePath}/vcbbs/admin/ScoreUser.do">用户积分列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 商品管理<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/goodExchangeHistory.do">兑换列表</a></li>
                            <li><a href="${basePath}/vcbbs/admin/goodList.do">商品列表</a></li>
                        </ul>
                    </li>


                    <li><a href="">
                        <i class="fa fa-gittip fa-fw"></i> 用户反馈<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/feedbackList.do">反馈列表</a></li>
                            <li><a href="${basePath}/vcbbs/page/Help.jsp">帮助</a></li>
                        </ul>
                    </li>


                    <li><a href="">

                        <i class="fa fa-gittip fa-fw"></i> 用户数据分析<span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level collapse">
                            <li><a href="${basePath}/vcbbs/admin/userData.do">用户数据</a></li>
                            <li><a href="${basePath}/vcbbs/admin/ArticleCommentData.do">回帖数据</a></li>
                            <li><a href="${basePath}/vcbbs/admin/messageHistory.do">聊天数据</a></li>
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

            <div class="row">
                <h4 class="page-header">添加新用户</h4>
                <form action="${basePath}/vcbbs/admin/user/new.do" class="form-horizontal" enctype="multipart/from-data"
                      method="post" style="width:90%" role="form">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">用户手机号：</label>
                        <div class="col-sm-5"><input type="text" class="form-control" name="name" value="" id="name">
                            <input type="hidden" class="form-control" name="name" value=""></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label" style="padding-top:7px;">初始密码：</label>
                        <div class="col-sm-5"><input type="text" class="form-control" name="password" value=""
                                                     id="password"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label" style="padding-top:7px;">昵称：</label>
                        <div class="col-sm-5"><input type="text" class="form-control" name="nickname" value=""
                                                     id="nickname"></div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label" style="padding-top:7px;">邮箱：</label>
                        <div class="col-sm-5"><input type="text" class="form-control" name="email" value="" id="email">
                        </div>
                    </div>
                    <div class="form-group" style="display:none;">
                        <label class="col-sm-2 control-label" style="padding-top:7px;">头像：</label>
                        <div class="col-sm-5">
                            <div id="queue"></div>
                            <div id="file_upload" class="uploadify" style="height: 30px; width: 120px;">
                                <object id="SWFUpload_0" type="application/x-shockwave-flash"
                                        data="http://139.196.210.151/statics//uploadify/uploadify.swf?preventswfcaching=1465824964907"
                                        width="120" height="30" class="swfupload"
                                        style="position: absolute; z-index: 1;">
                                    <param name="wmode" value="transparent">
                                    <param name="movie"
                                           value="/statics//uploadify/uploadify.swf?preventswfcaching=1465824964907">
                                    <param name="quality" value="high">
                                    <param name="menu" value="false">
                                    <param name="allowScriptAccess" value="always">
                                    <param name="flashvars"
                                           value="movieName=SWFUpload_0&amp;uploadURL=%2F1%2Fjson.php%3Fmod%3Dadmin%26act%3Dupload&amp;useQueryString=false&amp;requeueOnError=false&amp;httpSuccess=&amp;assumeSuccessTimeout=30&amp;params=&amp;filePostName=Filedata&amp;fileTypes=*.jpg%3B*.png&amp;fileTypesDescription=All%20Files&amp;fileSizeLimit=0&amp;fileUploadLimit=0&amp;fileQueueLimit=999&amp;debugEnabled=false&amp;buttonImageURL=%2F1%2F&amp;buttonWidth=120&amp;buttonHeight=30&amp;buttonText=&amp;buttonTextTopPadding=0&amp;buttonTextLeftPadding=0&amp;buttonTextStyle=color%3A%20%23000000%3B%20font-size%3A%2016pt%3B&amp;buttonAction=-110&amp;buttonDisabled=false&amp;buttonCursor=-2">
                                </object>
                                <div id="file_upload-button" class="uploadify-button "
                                     style="height: 30px; line-height: 30px; width: 120px;"><span
                                        class="uploadify-button-text">上传新焦点图</span></div>
                            </div>
                            <div id="file_upload-queue" class="uploadify-queue"></div>
                            <input name="avatar" type="text" style="display:none;" class="form-control" id="avatar"
                                   value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label" style="padding-top:7px;">性别：</label>
                        <div class="col-sm-5"><select class="form-control" name="sex" id="sex">
                            <option value="0">男</option>
                            <option value="1">女</option>
                            <option value="3">保密</option>
                        </select></div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-offset-2 col-sm-10">
                            <button type="button" onclick="add()"
                                    style="background:#ed5f00;color:white;border:0px;font-size:15px;height:30px;width:100px"
                                    class="btn btn-default" id="save_jobs">保存
                            </button>
                        </div>
                    </div>

                </form>
                <div style="position:absolute;top:57%;right:40%">
                    <form action="${basePath}/vcbbs/admin/inserUsers.do" method="post" ENCTYPE="multipart/form-data">
                        <a href="javascript:;" class="file">
                            <center>导入用户</center>
                            <input type="file" name="file">
                        </a>
                        <input type="submit" value="导入用户" id="submitExport" style="display:none">
                    </form>
                </div>
                <div style="position:absolute;top:57%;right:25%"><span class="showFileName fileerrorTip"></span></div>

            </div>
            <!-- /.row -->
        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->
</div>
<!-- jQuery -->
<script src="//cdn.bootcss.com/jquery/2.1.3/jquery.min.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="//cdn.bootcss.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="//cdn.bootcss.com/metisMenu/1.1.3/metisMenu.min.js"></script>

<!-- Custom Theme JavaScript -->
<script>var isSuperAdmin = ${sessionScope.adminUser.superAdmin?'true':'false'};</script>
<script src="/vcbbs/js/sb-admin-2.js"></script>

<!-- Custom Theme JavaScript -->
<script src="http://139.196.210.151/statics//bootstrap/js/bootbox.js"></script>

<script src="http://139.196.210.151/statics//uploadify/jquery.uploadify.js" type="text/javascript"></script>
<script type="text/javascript">
    function add() {
        var nickname = $("#nickname").val();
        var pwd = $("#password").val();
        var email = $("#email").val();
        var sex = $("#sex").val();
        var name = $("#name").val();
        $.ajax({
            url: "${basePath}/vcbbs/admin/user/new.do?nickname=" + nickname + "&password=" + pwd + "&email=" + email + "&sex=" + sex + "&name=" + name,
            type: 'post',
            dataType: "json",
            success: function (data, textStatus) {
                if (data.message == "success") {
                    alert("添加成功");
                    location.href = "${basePath}/vcbbs/admin/userList.do";
                } else {
                    alert(data.message);
                }
            }
        });
    }

    $(".file").on("change", "input[type='file']", function () {
        var filePath = $(this).val();
        if (filePath.indexOf("xls") != -1 || filePath.indexOf("xlsx") != -1) {
            $(".fileerrorTip").html("").hide();
            var arr = filePath.split('\\');
            var fileName = arr[arr.length - 1];
            $(".showFileName").html(fileName).show();
            $("#submitExport").click();
        } else {
            $(".showFileName").html("");
            $(".fileerrorTip").html("您上传文件类型有误！").show();
            $(this).val("");
            return false;
        }
    })

</script>


</body>
</html>