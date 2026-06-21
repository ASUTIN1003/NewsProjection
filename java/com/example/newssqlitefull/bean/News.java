package com.example.newssqlitefull.bean;

import java.io.Serializable;

/**
 * 新闻
 */
public class News implements Serializable {
    private Integer id;
    private Integer typeId;//类型 0科技，1娱乐，2体育，3军事,4汽车,5健康
    private String title;//标题
    private String img;//图片
    private String content;//内容
    private String issuer;//发布人
    private String date;//时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public News(Integer id, Integer typeId, String title, String img, String content, String issuer, String date) {
        this.id = id;
        this.typeId = typeId;
        this.title = title;
        this.img = img;
        this.content = content;
        this.issuer = issuer;
        this.date = date;
    }
}
