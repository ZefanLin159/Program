package com.newcorder.community.controller;

import com.newcorder.community.dao.CommentMapper;
import com.newcorder.community.entity.Comment;
import com.newcorder.community.entity.DiscussPost;
import com.newcorder.community.entity.Page;
import com.newcorder.community.entity.User;
import com.newcorder.community.service.CommentService;
import com.newcorder.community.service.DiscussPostService;
import com.newcorder.community.service.LikeService;
import com.newcorder.community.service.UserService;
import com.newcorder.community.util.CommunityConstant;
import com.newcorder.community.util.CommunityUtil;
import com.newcorder.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

//增加帖子的一些操作
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;
    //    获取当前用户
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
//        报错的情况将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功");

    }

    //显示帖子详情controller(显示评论怎么处理）
    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
//        帖子
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post", post);
//        直接提取当前发帖子的用户(作者）
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

//        点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);

//        点赞状态
        int likeStatus = hostHolder.getUser() == null ?
                0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        System.out.println(post.getId());
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                likeCount = likeService.findEntityLikeCount(ENTYTY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                //        点赞状态
                likeStatus = hostHolder.getUser() == null ?
                        0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTYTY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                // 回复列表
                int testId = comment.getId();
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTYTY_TYPE_COMMENT, testId, 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));

                        likeCount = likeService.findEntityLikeCount(ENTYTY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        //        点赞状态
                        likeStatus = hostHolder.getUser() == null ?
                                0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTYTY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTYTY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "site/discuss-detail";
    }
}