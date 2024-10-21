package org.example.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.UserDTO;
import org.example.dto.UserInputDTO;
import org.example.entity.User;
import org.example.handling.ErrorResponse;
import org.example.interfaces.UserService;
import org.example.mappers.UserInputMapper;
import org.example.mappers.UserMapper;

import java.util.List;
import java.util.Optional;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserResource {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserInputMapper userInputMapper;

    @Inject
    public UserResource(UserService userService, UserMapper userMapper, UserInputMapper userInputMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userInputMapper = userInputMapper;
    }

    @GET
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream().map(userMapper::toDto).toList();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return Response.ok(userMapper.toDto(user.get())).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("User not found for ID: " + id))
                    .build();
        }
    }

    @POST
    @Transactional
    public Response createUser(UserInputDTO  userInputDTO) {
        if (userInputDTO == null || userInputDTO.getName() == null || userInputDTO.getEmail() == null || userInputDTO.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Name, email and username cannot be null"))
                    .build();
        }
        User user = userInputMapper.toEntity(userInputDTO);
        userService.save(user);
        return Response.status(Response.Status.CREATED).entity(userMapper.toDto(user)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, UserInputDTO userInputDTO) {
        if (userInputDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Optional<User> existingUserOpt = userService.findById(id);
        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            userService.update(user, userInputDTO);
            return Response.ok(userMapper.toDto(user)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("User under ID " + id + " not found")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        Boolean deleted = userService.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
