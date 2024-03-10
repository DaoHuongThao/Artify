package com.example.artworksharingplatform.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.artworksharingplatform.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.artworksharingplatform.entity.Post;
import com.example.artworksharingplatform.entity.User;
import com.example.artworksharingplatform.mapper.UserMapper;
import com.example.artworksharingplatform.model.ApiResponse;
import com.example.artworksharingplatform.model.UserDTO;
import com.example.artworksharingplatform.repository.UserRepository;
import com.example.artworksharingplatform.service.AdminService;
import com.example.artworksharingplatform.service.CloudinaryService;
import com.example.artworksharingplatform.service.PostService;
import com.example.artworksharingplatform.service.UserService;

@RestController
@RequestMapping("/api/auth/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    UserService _userService;
    @Autowired
    PostService _postService;
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRepository _userRepository;

    @Autowired
    AdminService adminService;

    @Autowired
    CloudinaryService cloudinaryService;

    @PostMapping("BecomeCreator")
    public ResponseEntity<ApiResponse<User>> BecomeCreator(@RequestHeader("AudienceEmail") String Email) {

        ApiResponse<User> apiResponse = new ApiResponse<>();
        try {
            User userInfo = _userService.findByEmail(Email);
            userInfo.setStatus("ACTIVE");
            userInfo.setRole(Role.CREATOR);
            User user = _userRepository.save(userInfo);
            apiResponse.ok(user);

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            apiResponse.error(e);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("deletePost")
    public ResponseEntity<ApiResponse<Post>> DeletePost(@RequestHeader("PostId") UUID postId) {
        ApiResponse<Post> apiResponse = new ApiResponse<>();
        try {
            _postService.deleteArtwork(postId);
            apiResponse.ok();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.error(e);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("user/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@RequestPart(value = "user") UserDTO updatedUser,
                                                           @RequestPart(value = "image", required = false) MultipartFile file) {
        ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
        if (updatedUser != null) {
            try {
                updatedUser.setImagePath(null);
                if (file != null) {
                    updatedUser.setImagePath(uploadImage(file));
                }
                UserDTO user = adminService.updateUser(updatedUser);
                apiResponse.ok(user);
                return ResponseEntity.ok(apiResponse);
            } catch (Exception ex) {
                apiResponse.error(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    public String uploadImage(MultipartFile file) {
        Map data = cloudinaryService.upload(file);
        String url = data.get("url").toString();
        return url;
    }

    @GetMapping("user/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo(@RequestParam UUID userId) {
        ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
        try {
            if (userId != null) {
                UserDTO userInfo = adminService.getUserInfo(userId);
                if (userInfo != null) {
                    apiResponse.ok(userInfo);
                    return ResponseEntity.ok(apiResponse);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @GetMapping("user/list")
    public ResponseEntity<ApiResponse<List<UserDTO>>> viewAllUsers() {
        ApiResponse<List<UserDTO>> apiResponse = new ApiResponse<List<UserDTO>>();
        try {
            List<UserDTO> userlist = adminService.viewAllUsers();
            apiResponse.ok(userlist);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.error(e);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("user/add")
    public ResponseEntity<ApiResponse<UserDTO>> addUser(@RequestPart(value = "user") UserDTO addUser,
                                                        @RequestPart(value = "image", required = false) MultipartFile file) {
        ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
        if (addUser != null) {
            try {
                addUser.setImagePath(null);
                if (file != null) {
                    addUser.setImagePath(uploadImage(file));
                }
                UserDTO user = adminService.addUser(addUser);
                apiResponse.ok(user);
                return ResponseEntity.ok(apiResponse);
            } catch (Exception ex) {
                apiResponse.error(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}