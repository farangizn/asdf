package org.example.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.PostDTO;
import org.example.dto.PostInputPTO;
import org.example.entity.Post;
import org.example.handling.ErrorResponse;
import org.example.interfaces.PostService;
import org.example.mappers.PostMapper;

import java.util.List;
import java.util.Optional;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PostResource {

    private final PostService postService;
    private final PostMapper postMapper;

    @Inject
    public PostResource(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GET
    public Response getAllPosts() {
        return Response.ok(postService.findAll().stream().map(post -> {
            PostDTO postDTO = postMapper.toDto(post);
            postDTO.setUserId(post.getUser().id);
            return postDTO;
        }).toList()).build();
    }

    @GET
    @Path("/{id}")
    public Response getPostById(@PathParam("id") Long id) {
        Optional<Post> post = postService.findById(id);
        return post.map(p -> {
            PostDTO postDTO = postMapper.toDto(p);
            postDTO.setUserId(p.getUser().id);
            return Response.ok(postDTO);
        }).orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Post not found for ID: " + id))).build();
    }

    @POST
    @Transactional
    public Response createPost(PostInputPTO postInputPTO) {
        if (postInputPTO == null || postInputPTO.getTitle() == null || postInputPTO.getBody() == null || postInputPTO.getUserId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Post title, body and user ID must not be null"))
                    .build();
        }
        PostDTO postDTO = postMapper.toDto(postService.save(postInputPTO));
        postDTO.setUserId(postInputPTO.getUserId());
        return Response.status(Response.Status.CREATED).entity(postDTO).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updatePost(@PathParam("id") Long id, PostInputPTO postInputPTO) {
        if (postInputPTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Post must not be null"))
                    .build();
        }

        Optional<Post> postOpt = postService.findById(id);
        if (postOpt.isPresent()) {
            Post existingPost = postOpt.get();
            postService.update(existingPost, postInputPTO);
            PostDTO postDTO = postMapper.toDto(existingPost);
            postDTO.setUserId(postInputPTO.getUserId());
            return Response.status(Response.Status.CREATED).entity(postDTO).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Post not found for ID: " + id))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletePost(@PathParam("id") Long id) {
        Boolean deleted = postService.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Post not found for ID: " + id))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getPostsByUserId(@PathParam("userId") Long userId) {
        List<PostDTO> posts = postService.findAllByUserId(userId).stream().map(post -> {
            PostDTO postDTO = postMapper.toDto(post);
            postDTO.setUserId(post.getUser().id);
            return postDTO;
        }).toList();
        return Response.ok(posts).build();
    }

    @DELETE
    @Path("/user/{userId}")
    @Transactional
    public Response deletePostsByUserId(@PathParam("userId") Long userId) {
        postService.deleteAllByUserId(userId);
        return Response.noContent().build();
    }
}
