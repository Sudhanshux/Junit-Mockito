package com.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.test.context.ContextConfiguration;

import com.credlix.user.model.User;
import com.credlix.user.repository.UserRepository;
import com.user.testdata.DummyData;

import app.JwtAuthServiceApp;
import app.dto.UserSignInRequestDTO;

/**
 * @author Sudhanshu
 *
 *         //
 */
@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureDataMongo
@ContextConfiguration(classes = JwtAuthServiceApp.class)
public class UserRepositoryTest {

	@Mock
	UserRepository userRepository;

//	@Mock
//	MongoTemplate mongoTemplate;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		// MongodStarter starter = MongodStarter.getDefaultInstance();

	}

	/**
	 * Signin Test implemented here.
	 * 
	 */
	@Test
	public void signintest() {
		
		UserSignInRequestDTO user = new UserSignInRequestDTO();
		user.setEmail("test@gmail.com");
		Optional<User> userdata=Optional.of(DummyData.getUser());
		//assertEquals(user2, userRepository.findByEmail(user.getEmail()))
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userdata);
		Optional<User> findByEmail = userRepository.findByEmail(user.getEmail());
		assertEquals(findByEmail.get().getEmail(), user.getEmail());
		assertNotNull(findByEmail);
		assertThat(findByEmail.isPresent());
	}

	/**
	 * CreateUserWithRole test implemented here.
	 */
	@Test
	public void createUserWithRoleTest() {
		List<User> userList=DummyData.getUserList();
		this.userRepository.saveAll(userList);
		List<User> findAll = userRepository.findAll();
		assertNotNull(userRepository.findAll());

	}

	/**
	 * SearchUsers test implemented here.
	 * 
	 */
	@Test
	public void searchUsersTest() {
		UserSignInRequestDTO user = DummyData.getuserSignInRequestDto();

		Optional<User> test = userRepository.findByEmail(DummyData.getUser().getEmail());

		assertThat(test.isPresent());

		boolean Email = userRepository.existsByEmail(DummyData.getUser().getEmail());

		assertFalse(Email);

	}

}
