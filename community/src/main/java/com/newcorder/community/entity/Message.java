package com.newcorder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private int status; //0 未读 1 已读 2 删除
    private String content;
    private Date createTime;
}
