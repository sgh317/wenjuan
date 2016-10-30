package com.wenjuan.model;

import java.io.Serializable;
import java.util.Date;

public class UserFollowArticle implements Serializable {
    private Integer userid;

    private Date followTime;

    private Integer author;

    private String title;

    private Byte status;

    private Integer followCount;

    private Integer articleId;

    private Boolean avai;

    private Date top;

    private Date time;

    private Integer replyCount;

    private Boolean publishBackground;

    private String content;

    private String pics;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Date getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Date followTime) {
        this.followTime = followTime;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Boolean getAvai() {
        return avai;
    }

    public void setAvai(Boolean avai) {
        this.avai = avai;
    }

    public Date getTop() {
        return top;
    }

    public void setTop(Date top) {
        this.top = top;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Boolean getPublishBackground() {
        return publishBackground;
    }

    public void setPublishBackground(Boolean publishBackground) {
        this.publishBackground = publishBackground;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics == null ? null : pics.trim();
    }
}