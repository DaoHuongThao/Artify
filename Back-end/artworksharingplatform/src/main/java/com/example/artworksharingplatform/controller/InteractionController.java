package com.example.artworksharingplatform.controller;

import java.util.List;
import java.util.UUID;

import com.example.artworksharingplatform.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.artworksharingplatform.entity.Interaction;
import com.example.artworksharingplatform.mapper.CommentMapper;
import com.example.artworksharingplatform.mapper.InteractionMapper;
import com.example.artworksharingplatform.model.InteractionDTO;
import com.example.artworksharingplatform.model.UserDTO;
import com.example.artworksharingplatform.service.CommentService;
import com.example.artworksharingplatform.service.InteractionService;
import com.example.artworksharingplatform.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/auth")
public class InteractionController {

    @Autowired
    InteractionService interactionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    InteractionMapper interactionMapper;

    @GetMapping("post/{postId}")
    public ResponseEntity<ApiResponse<List<InteractionDTO>>> getInteractionByPostId(@PathVariable UUID postId) {
        ApiResponse<List<InteractionDTO>> apiResponse = new ApiResponse<List<InteractionDTO>>();
        try {
            List<Interaction> interactions = interactionService.getInteractionsByPostId(postId);
            List<InteractionDTO> interactionDTOs = interactionMapper.toInteractionDTOList(interactions);

            apiResponse.ok(interactionDTOs);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.error(e);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("post/like")
    @PreAuthorize("hasRole('ROLE_AUDIENCE') or hasRole('ROLE_CREATOR')")
    public ResponseEntity<ApiResponse<InteractionDTO>> likePost(@RequestParam UUID postId) {
        ApiResponse<InteractionDTO> apiResponse = new ApiResponse<InteractionDTO>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isUserAuthenticated(authentication)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            UserDTO user = userService.findByEmailAddress(email);

            Interaction interaction = interactionService.likePost(postId, user.getUserId());
            InteractionDTO interactionDTO = interactionMapper.toInteractionDTO(interaction);

            apiResponse.ok(interactionDTO);
            return ResponseEntity.ok(apiResponse);
        } else {
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isUserAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails;
    }
}
