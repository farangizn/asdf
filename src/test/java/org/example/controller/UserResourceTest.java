package org.example.controller;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.example.dto.UserDTO;
import org.example.dto.UserInputDTO;
import org.example.entity.User;
import org.example.interfaces.UserService;
import org.example.mappers.UserInputMapper;
import org.example.mappers.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class UserResourceTest {

    @InjectMock
    UserRepository userRepository;

    @InjectMock
    UserResource userResource;

    @InjectMock
    UserMapper userMapper;

    @InjectMock
    UserInputMapper userInputMapper;

    @InjectMock
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("farangizhon2004@icloud.com");
        user.setName("farangiz");
        user.setUsername("farangizn");
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);

        Mockito.when(userRepository.listAll()).thenReturn(users);

        List<UserDTO> userDTOS = userResource.getAllUsers();

        assertNotNull(userDTOS);
        assertEquals(userDTOS.size(), 1);
        assertEquals(userDTOS.get(0).getId(), user.id);
        assertEquals(userDTOS.get(0).getUsername(), user.getUsername());
        assertEquals(userDTOS.get(0).getEmail(), user.getEmail());
    }

    @Test
    void getUserById() {

        Mockito.when(userRepository.findById(1L)).thenReturn(user);

        Response response = userResource.getUserById(1L);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());

        UserDTO entity = (UserDTO) response.getEntity();

        assertNotNull(entity);
        assertEquals(entity.getId(), user.id);
        assertEquals(entity.getUsername(), user.getUsername());
        assertEquals(entity.getEmail(), user.getEmail());

    }

    @Test
    void createUser() {

        Mockito.doNothing().when(userRepository).persist(any(User.class));

        UserInputDTO userInputDTO = new UserInputDTO("John", "john_d", "johndoe@gmail.com");

        Response response = userResource.createUser(userInputDTO);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());

        UserDTO userDTO = (UserDTO) response.getEntity();

        assertNotNull(userDTO);

    }



    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    // rest

    @Test
    public void testUsersGetEndpoint() {
        given()
                .when().get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        Mockito.when(userService.findAll()).thenReturn(Collections.emptyList());

        when()
                .get("/users")
                .then()
                .statusCode(200)
                .body(is("[]"));

        verify(userService).findAll();
    }

    @Test
    public void testGetAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");

        Mockito.when(userService.findAll()).thenReturn(Collections.singletonList(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);

        when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("[0].name", is("John Doe"));

        verify(userService).findAll();
    }

    @Test
    public void testGetUserById_Found() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");

        Mockito.when(userService.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);

        when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("John Doe"));

        verify(userService).findById(1L);
    }


    @Test
    public void testGetUserById_NotFound() {
        Mockito.when(userService.findById(999L)).thenReturn(Optional.empty());
        given()
                .pathParam("id", 999)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body("message", equalTo("User not found for ID: 999"));

        verify(userService).findById(999L);
    }

    @Test
    public void testCreateUser_Success() {
        UserInputDTO userInputDTO = new UserInputDTO();
        userInputDTO.setName("John Doe");
        userInputDTO.setEmail("john.doe@example.com");
        userInputDTO.setUsername("johndoe");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");

        Mockito.when(userInputMapper.toEntity(userInputDTO)).thenReturn(user);
        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);

        given()
                .contentType("application/json")
                .body(userInputDTO)
                .when()
                .post("/users")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("name", equalTo("John Doe"));

        verify(userService).save(user);
    }

    @Test
    public void testCreateUser_InvalidInput() {
        UserInputDTO invalidUser = new UserInputDTO(); // No fields set

        given()
                .contentType("application/json")
                .body(invalidUser)
                .when()
                .post("/users")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("message", equalTo("Name, email and username cannot be null"));

        verify(userService, Mockito.never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser_Success() {
        Mockito.when(userService.deleteById(1L)).thenReturn(true);

        given()
                .pathParam("id", 1)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        verify(userService).deleteById(1L);
    }

    @Test
    public void testDeleteUser_NotFound() {
        Mockito.when(userService.deleteById(999L)).thenReturn(false);

        given()
                .pathParam("id", 999)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        verify(userService).deleteById(999L);
    }
}

//
//@QuarkusTest
//class UserResourceTest {
//
//    @InjectMock
//    UserRepository userRepository;
//
//    @InjectMock
//    UserMapper userMapper;
//
//    @InjectMock
//    UserInputMapper userInputMapper;
//
//    @InjectMock
//    UserService userService;
//
//    private User user;
//    private UserDTO userDTO;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder()
//                .id(1L)
//                .email("farangizhon2004@icloud.com")
//                .name("farangiz")
//                .username("farangizn")
//                .build();
//
//        userDTO = UserDTO.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .name(user.getName())
//                .username(user.getUsername())
//                .build();
//    }
//
//    @Test
//    void getAllUsers_ShouldReturnUsersList() {
//        Mockito.when(userService.findAll()).thenReturn(List.of(user));
//        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);
//
//        List<UserDTO> result = userService.findAll().stream()
//                .map(userMapper::toDto)
//                .toList();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(userDTO, result.get(0));
//    }
//
//    @Test
//    void getUserById_ShouldReturnUser() {
//        Mockito.when(userService.findById(user.getId())).thenReturn(Optional.of(user));
//        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);
//
//        Response response = userService.findById(user.getId()).map(userMapper::toDto)
//                .map(dto -> Response.ok(dto).build())
//                .orElse(Response.status(Response.Status.NOT_FOUND).build());
//
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//        assertEquals(userDTO, response.getEntity());
//    }
//
//    @Test
//    void getUserById_NotFound() {
//        Mockito.when(userService.findById(999L)).thenReturn(Optional.empty());
//
//        Response response = userService.findById(999L).map(userMapper::toDto)
//                .map(dto -> Response.ok(dto).build())
//                .orElse(Response.status(Response.Status.NOT_FOUND).build());
//
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//        assertNotNull(response.getEntity());
//    }
//
//    @Test
//    void createUser_ShouldReturnCreatedResponse() {
//        UserInputDTO userInputDTO = new UserInputDTO("John", "john_d", "johndoe@example.com");
//
//        Mockito.when(userInputMapper.toEntity(userInputDTO)).thenReturn(user);
//        Mockito.when(userService.save(user)).thenAnswer(invocation -> {
//            user.setId(1L); // Simulating ID generation
//            return null;
//        });
//        Mockito.when(userMapper.toDto(user)).thenReturn(userDTO);
//
//        Response response = userService.save(userInputMapper.toEntity(userInputDTO));
//
//        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
//        assertEquals(userDTO, response.getEntity());
//    }
//
//    @Test
//    void createUser_InvalidInput() {
//        UserInputDTO invalidUserInput = new UserInputDTO(); // No fields set
//
//        Response response = userService.save(userInputMapper.toEntity(invalidUserInput));
//
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
//        assertEquals("Name, email and username cannot be null", response.getEntity());
//    }
//
//    @Test
//    void deleteUser_Success() {
//        Mockito.when(userService.deleteById(1L)).thenReturn(true);
//
//        Response response = userService.deleteById(1L)
//                ? Response.noContent().build()
//                : Response.status(Response.Status.NOT_FOUND).build();
//
//        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    void deleteUser_NotFound() {
//        Mockito.when(userService.deleteById(999L)).thenReturn(false);
//
//        Response response = userService.deleteById(999L)
//                ? Response.noContent().build()
//                : Response.status(Response.Status.NOT_FOUND).build();
//
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//    }
//}