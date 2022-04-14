package com.newcorder.community.controller;

import com.newcorder.community.enent.EventProducer;
import com.newcorder.community.entity.Event;
import com.newcorder.community.entity.Page;
import com.newcorder.community.entity.User;
import com.newcorder.community.service.FollowService;
import com.newcorder.community.service.UserService;
import com.newcorder.community.util.CommunityConstant;
import com.newcorder.community.util.CommunityUtil;
import com.newcorder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //    这里最好补充拦截器检查
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

//        触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_USER)
                .setEntityId(entityId)
                .setEntityUserid(entityId);
        eventProducer.fileEvent(event);

        return CommunityUtil.getJSONString(0, "已关注");
    }

    //    这里最好补充拦截器检查
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User uTemp = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(uTemp.getId()));

            }
        }
        model.addAttribute("users", userList);
        return "site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followers" + userId);
        page.setRows((int) followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User uTemp = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(uTemp.getId()));

            }
        }
        model.addAttribute("users", userList);
        return "site/follower";
    }


    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
//        userId 界面用户是否被关注
        return followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }
}
