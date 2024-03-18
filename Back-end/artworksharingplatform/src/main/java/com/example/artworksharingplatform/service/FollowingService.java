package com.example.artworksharingplatform.service;

import com.example.artworksharingplatform.entity.User;

public interface FollowingService {
    public String Following(User audience, User creator) throws Exception;

    public Integer NumOfFollowing(User creator) throws Exception;

    public Boolean IsFollow(User audience, User creator) throws Exception;
}
