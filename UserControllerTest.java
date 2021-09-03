package com.credlix.user.controller.test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.credlix.user.controller.UserController;
import com.credlix.user.dto.UserDTO;
import com.credlix.user.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.testdata.DummyData;

import app.JwtAuthServiceApp;
import app.dto.UserSignInRequestDTO;

/**
 * @author Sudhanshu
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JwtAuthServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;
	
	

	ObjectMapper obj = new ObjectMapper();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	/**
	 * 
	 * Signin Test implemented here.
	 * 
	 * @throws
	 */
	@Test
	public void SigninTest() throws Exception {
		Mockito.when(userService.signin(DummyData.getuserSignInRequestDto())).thenReturn(DummyData.getresponseToken());
//		when(userService.signin(Mockito.any(UserSignInRequestDTO.class))).thenReturn(null);
		UserSignInRequestDTO test = DummyData.getuserSignInRequestDto();

		String jsonRequest = obj.writeValueAsString(test);
		String url = "/users/signin";
		MvcResult result = mockMvc.perform(post(url).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		result.getResponse();
		String resultContent = result.getResponse().getContentAsString();
		Assert.assertNotNull(resultContent);

	} 

	/**
	 * 
	 * CreateUserwithRole test implemented here.
	 * 
	 * @throws
	 */
	@Test
	public void createUserWithRoleTest() throws Exception {
		
		doReturn(true).when(userService).createUserWithRole(ArgumentMatchers.any());
		UserDTO user = DummyData.getUserDto();
		String jsonRequest = obj.writeValueAsString(user);

		MvcResult result = mockMvc
				.perform(post("/users/create-user-with-role").content(jsonRequest)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		String resultContent = result.getResponse().getContentAsString();
		Assert.assertNotNull(resultContent);

	}

	/**
	 * SearchUSers test implemented here.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getUserBySearchKeyTest() throws Exception {
		String searchKey="test";
		Set<UserDTO> user = new HashSet<>();
		user.add(DummyData.getUserDto());
		Mockito.when(userService.getUserBySearchKey(searchKey)).thenReturn(user);
		
		String jsonRequest = obj.writeValueAsString(user);

		MvcResult result = mockMvc
				.perform(get("/users/search").content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		String resultContent = result.getResponse().getContentAsString();
		Assert.assertNotNull(resultContent);
	}

}
