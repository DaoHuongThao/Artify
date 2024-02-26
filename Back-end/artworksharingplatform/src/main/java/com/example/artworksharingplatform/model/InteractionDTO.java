package com.example.artworksharingplatform.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InteractionDTO {
    private UUID postId;
    private String comment;
    private Boolean isLiked;
    private UUID userId;
    private String name;
}
