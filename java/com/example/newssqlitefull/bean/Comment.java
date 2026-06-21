package com.example.newssqlitefull.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 评论
 */
public class Comment implements Serializable {
    private Integer id;
    private Integer newsId;//新闻ID
    private Integer userId;//用户ID
    private String content;//内容
    private String date;//时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Comment(Integer id, Integer newsId, Integer userId, String content, String date) {
        this.id = id;
        this.newsId = newsId;
        this.userId = userId;
        this.content = content;
        this.date = date;
    }
}
