package com.example.newssqlitefull.bean;

import org.litepal.crud.DataSupport;

/**
 * 浏览记录
 */
public class Browse{
    private Integer id;
    private Integer newsId;//新闻ID
    private Integer userId;//用户ID

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

    public Browse(Integer id, Integer newsId, Integer userId) {
        this.id = id;
        this.newsId = newsId;
        this.userId = userId;
    }
}
