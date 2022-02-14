package com.example.simple_todo;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoReadDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.domain.UserClaims;
import com.example.simple_todo.repository.TodoRepository;
import com.example.simple_todo.repository.UserRepository;
import com.example.simple_todo.service.JwtTokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TodoRepository todoRepository;

    Long userId;

    String username = "user";

    String userPassword = "12345";

    private String token;

    @BeforeAll
    void init() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(userPassword));
        userId = userRepository.save(user).getId();
        user.setId(userId);
        UserClaims userClaims = new UserClaims(userId, username);
        token = jwtTokenUtil.generateToken(userClaims);
    }

    @Test
    void notNestedTodoWhenValidInputTest() throws Exception {
        String title = "Test task";
        String updatedTitle = "Updated test task";
        assertNotEquals(title, updatedTitle);

        Long todoId = postTodoWhenValidInput(title, null).getId();
        putTodoWhenValidInput(todoId, updatedTitle, true);
        TodoReadDto todo = getTodosWhenValidInput().get(0);

        assertEquals(todo.getId(), todoId);
        assertEquals(todo.getTitle(), updatedTitle);
        assertEquals(todo.getDone(), true);

        deleteTodoWhenValidInput(todoId);
    }

    @Test
    void nestedTodoWhenValidInputTest() throws Exception {
        String title1 = "Test1";
        Long todoId1 = postTodoWhenValidInput(title1, null).getId();

        TodoReadDto todo1 = new TodoReadDto();
        todo1.setId(todoId1);
        todo1.setTitle(title1);
        todo1.setDone(false);

        String title1p1 = "Test1.1";
        Long todoId1p1 = postTodoWhenValidInput(title1p1, todoId1).getId();

        TodoReadDto todo1p1 = new TodoReadDto();
        todo1p1.setId(todoId1p1);
        todo1p1.setTitle(title1p1);
        todo1p1.setDone(false);

        String title1p2 = "Test1.2";
        Long todoId1p2 = postTodoWhenValidInput(title1p2, todoId1).getId();

        TodoReadDto todo1p2 = new TodoReadDto();
        todo1p2.setId(todoId1p2);
        todo1p2.setTitle(title1p2);
        todo1p2.setDone(false);

        String title1p2p1 = "Test1.2.1";
        Long todoId1p2p1 = postTodoWhenValidInput(title1p2p1, todoId1p2).getId();

        TodoReadDto todo1p2p1 = new TodoReadDto();
        todo1p2p1.setId(todoId1p2p1);
        todo1p2p1.setTitle(title1p2p1);
        todo1p2p1.setDone(false);
        todo1p2p1.setSubtasks(new ArrayList<>());

        List<TodoReadDto> list1p2 = new ArrayList<>();
        list1p2.add(todo1p2p1);
        todo1p2.setSubtasks(list1p2);

        todo1p1.setSubtasks(new ArrayList<>());

        List<TodoReadDto> list1 = new ArrayList<>();
        list1.add(todo1p1);
        list1.add(todo1p2);
        todo1.setSubtasks(list1);

        List<TodoReadDto> expectedTodoList = new ArrayList<>();
        expectedTodoList.add(todo1);

        List<TodoReadDto> todoListFromDb = getTodosWhenValidInput();
        assertTrue(compareTodoList(todoListFromDb, expectedTodoList));
        putTodoWhenValidInput(todoId1, title1 + " updated", true);
        deleteTodoWhenValidInput(todoId1p2);
    }

    boolean compareTodoList(List<TodoReadDto> list1, List<TodoReadDto> list2) {
        if (list1.size() != list2.size())
            return false;
        else {
            list1.sort(Comparator.comparing(TodoReadDto::getId));
            list2.sort(Comparator.comparing(TodoReadDto::getId));
            for (int i = 0; i < list1.size(); i++) {
                TodoReadDto todo1 = list1.get(i);
                TodoReadDto todo2 = list2.get(i);
                if (
                        !(
                        todo1.getId().equals(todo2.getId()) &&
                        todo1.getTitle().equals(todo2.getTitle()) &&
                        todo1.getDone().equals(todo2.getDone())
                        ) && compareTodoList(todo1.getSubtasks(), todo2.getSubtasks())
                )
                    return false;
            }
            return true;
        }
    }
    
    TodoReadDto postTodoWhenValidInput(String title, Long parentId) throws Exception {
        TodoCreateDto todo = new TodoCreateDto();
        todo.setTitle(title);
        todo.setParent(parentId);
        String requestBody = objectMapper.writeValueAsString(todo);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/todo/")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(requestBody)).andExpect(status().is2xxSuccessful())
                        .andReturn();
        TodoReadDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TodoReadDto.class);
        Long todoId = result.getId();
        if (todoRepository.findById(todoId).isPresent()) {
            Todo todoFromDb = todoRepository.findById(todoId).get();
            assertEquals(todoFromDb.getTitle(), todo.getTitle());
            if (parentId != null && todoFromDb.getParent() != null)
                assertEquals(todoFromDb.getParent().getId(), parentId);
            else {
                assertNull(parentId);
                assertNull(todoFromDb.getParent());
            }
        }
        else {
            fail();
        }
        return result;
    }

    void putTodoWhenValidInput(Long todoId, String title, Boolean done) throws Exception {
        TodoUpdateDto todo = new TodoUpdateDto();
        todo.setId(todoId);
        todo.setTitle(title);
        todo.setDone(done);

        String requestBody = objectMapper.writeValueAsString(todo);
        mockMvc.perform(
                        put("/api/todo/")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestBody)).andExpect(status().is2xxSuccessful())
                .andReturn();

        if (todoRepository.findById(todoId).isPresent()) {
            Todo todoFromDb = todoRepository.findById(todoId).get();
            assertEquals(todoFromDb.getTitle(), todo.getTitle());
            assertEquals(todoFromDb.getDone(), done);
        }
        else {
            fail();
        }
    }

    List<TodoReadDto> getTodosWhenValidInput() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/todo")
                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
    }

    void deleteTodoWhenValidInput(Long todoId) throws Exception {
        assertTrue(todoRepository.findById(todoId).isPresent());
        mockMvc.perform(
                        delete("/api/todo/" + todoId)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful());
        assertFalse(todoRepository.findById(todoId).isPresent());
    }

    @AfterEach
    void clearTodoRepo() {
        todoRepository.deleteAll();
    }

    @AfterAll
    void clearUserRepo() {
        userRepository.deleteAll();
    }
}