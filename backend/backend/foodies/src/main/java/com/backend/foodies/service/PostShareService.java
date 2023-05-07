package com.backend.foodies.service;

import com.backend.foodies.dto.PostDTO;
import com.backend.foodies.dto.SharePostDTO;
import com.backend.foodies.model.PostShare;
import com.backend.foodies.model.User;
import com.backend.foodies.repository.PostShareRepository;
import com.backend.foodies.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostShareService {

    @Autowired
    private PostShareRepository postShareRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> getPostById(String id){
        Optional<PostShare> post =  postShareRepository.findById(id);
        if(post.isPresent()){
            return new ResponseEntity<>(post.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No Post Found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getPosts(){
        List<PostShare> notifications = postShareRepository.findAll();
        return new ResponseEntity<List<PostShare>>(notifications, HttpStatus.OK);
    }

    public ResponseEntity<?> getsharedPostsByUserId(String userId) {
        List<PostShare> sharedPosts = postShareRepository.findByUserId(userId);
        List<SharePostDTO> sharedPostDTOList = new ArrayList<>();

        for (PostShare postshare:sharedPosts) {
            SharePostDTO sharedPostDTO = new SharePostDTO();
            sharedPostDTO.setId(postshare.getId());
            sharedPostDTO.setCaption(postshare.getCaption());
            sharedPostDTO.setUpdatedAt(postshare.getUpdatedAt());
            sharedPostDTO.setCreatedAt(postshare.getCreatedAt());
            sharedPostDTO.setUserId(postshare.getUserId());

            Optional<User> user =  userRepository.findById(postshare.getUserId());
            if(user.isPresent()) {
                sharedPostDTO.setUsername(user.get().getUsername());
                sharedPostDTO.setProfileImage(user.get().getProfileImage());
            }

            PostDTO postDTO = new PostDTO();
            postDTO.setId(postshare.getPost().getId());
            postDTO.setCaption(postshare.getPost().getCaption());
            postDTO.setImgLink(postshare.getPost().getImgLink());
            postDTO.setUpdatedAt(postshare.getPost().getUpdatedAt());
            postDTO.setCreatedAt(postshare.getPost().getCreatedAt());
            postDTO.setUserId(postshare.getPost().getUserId());

            Optional<User> postUser =  userRepository.findById(postshare.getPost().getUserId());
            if(postUser.isPresent()) {
                postDTO.setUsername(postUser.get().getUsername());
                postDTO.setProfileImage(postUser.get().getProfileImage());
            }

            sharedPostDTO.setPost(postDTO);

            sharedPostDTOList.add(sharedPostDTO);
        }

        return new ResponseEntity<>(sharedPostDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> savePost(PostShare postShare){
        try{
            postShare.setCreatedAt(new Date(System.currentTimeMillis()));
            postShare.setUpdatedAt(new Date(System.currentTimeMillis()));
            postShareRepository.save(postShare);
            return new ResponseEntity<PostShare>(postShare, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updatePostById(String id,PostShare postShare){
        Optional<PostShare> existingPost =  postShareRepository.findById(id);
        if(existingPost.isPresent()){
            PostShare updatePost = existingPost.get();
            if(postShare.getCaption() != null) {
                updatePost.setCaption(postShare.getCaption());
            }
            updatePost.setUpdatedAt(new Date(System.currentTimeMillis()));
            return new ResponseEntity<>(postShareRepository.save(updatePost), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Shared Post Update Error",HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deletePostById(String id){
        try{
            postShareRepository.deleteById(id);
            return new ResponseEntity<>("Success deleted with " + id,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}
