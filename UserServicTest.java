package com.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.credlix.user.dto.RoleDTO;
import com.credlix.user.dto.TokenResponse;
import com.credlix.user.dto.UserDTO;
import com.credlix.user.dto.UserRoleDTO;
import com.credlix.user.enums.RoleType;
import com.credlix.user.enums.Roles;
import com.credlix.user.model.BusinessAccount;
import com.credlix.user.model.CustomUserRole;
import com.credlix.user.model.JwtAccessToken;
import com.credlix.user.model.MasterAccount;
import com.credlix.user.model.Role;
import com.credlix.user.model.User;
import com.credlix.user.repository.BusinessAccountRepository;
import com.credlix.user.repository.CustomUserRoleRepository;
import com.credlix.user.repository.MasterAccountRepository;
import com.credlix.user.repository.RoleRepository;
import com.credlix.user.repository.TokenRepository;
import com.credlix.user.repository.UserRepository;
import com.credlix.user.security.UserModel;
import com.credlix.user.service.impl.UserDetailService;
import com.credlix.user.util.CommonUtil;
import com.user.testdata.DummyData;

import app.JwtAuthServiceApp;
import app.dto.UserSignInRequestDTO;
import app.exception.CustomException;
import app.model.ResetPasswordToken;
import app.repository.ResetPasswordTokenRepository;
import app.security.JwtTokenProvider;
import app.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT )
@AutoConfigureDataMongo
@ContextConfiguration(classes = JwtAuthServiceApp.class)
public class UserServicTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Mock
	Authentication authentication;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	CustomUserRoleRepository cuRoleRepository;

	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	MasterAccountRepository masterAccountRepository;
	
	@Autowired
	BusinessAccountRepository businessAccountRepository;
	
	@Autowired
	ResetPasswordTokenRepository resetPasswordTokenRepository;
	
//	@Spy
//	@InjectMocks
//	UserService userService;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	ModelMapper modelMapper;

	@Mock
	JwtTokenProvider jwtTokenProvider;
	
	@InjectMocks
	UserDetailService userDetailService;
	
	@Mock
	Criteria criteria;
	
	
	 @Mock 
	 SecurityContext mockSecurityContext;
	 
	 ModelMapper m = new ModelMapper();

	

	@BeforeEach
	void setUp() {
		List<User> saveAll = this.userRepository.saveAll(DummyData.getUserList());
		this.cuRoleRepository.save(DummyData.getCustomUserRole());

	}

	@AfterEach
	void endUp() {
		this.userRepository.deleteAll();
	}

	@Test
	public void signin() {
		UserSignInRequestDTO user = new UserSignInRequestDTO();
		user.setEmail("test@gmail.com");
		user.setPassword("r@123");
		MasterAccount masterAccount = DummyData.getMasterAccount();
		User saveAll = this.userRepository.save(DummyData.getUser());
		masterAccount.setUser(saveAll);
		MasterAccount save2 = masterAccountRepository.save(masterAccount);
		CustomUserRole customUserRole2 = DummyData.getCustomUserRole();
		Role save = roleRepository.save(DummyData.getRole());
		customUserRole2.setUser(saveAll);
		customUserRole2.setMasterAccount(save2);
		customUserRole2.setRole(save);
		cuRoleRepository.save(customUserRole2);
		try {
			Optional<User> dbUserOpt = userRepository.findByEmail(user.getEmail());
			if (!dbUserOpt.isPresent()) {
				throw new CustomException("User Email Not Found", HttpStatus.UNPROCESSABLE_ENTITY);
			}
			User dbUser = dbUserOpt.get();
			if(!dbUser.isActiveLogin()) {
				throw new CustomException("Account Deactivated. Please contact administrator", HttpStatus.UNPROCESSABLE_ENTITY);
			}
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			
			tokenRepository.save(DummyData.getJwtToken());
			List<JwtAccessToken> tokens = tokenRepository.findByEmailOrderByCreatedAtDesc(user.getEmail()); 
			//If no token found for user
			 
			List<CustomUserRole> cuUserRoles = cuRoleRepository.findByUser(dbUser);

			JwtAccessToken previousToken = null;
			if(tokens != null && !tokens.isEmpty()) {
				previousToken = tokens.get(tokens.size()-1);
			}
			CustomUserRole customUserRole = null;
			if (previousToken != null && previousToken.getCustomUserRole() != null) {
				customUserRole = previousToken.getCustomUserRole();
				//TODO
				if(!customUserRole.getRole().getRoleType().equals(RoleType.BORROWER.name())){
					for(CustomUserRole customRole : cuUserRoles) {
						if(customRole.getRole().getRoleType().equals(RoleType.BORROWER.name())) {
							customUserRole = customRole;
							break;
						}
					}
				}
			} else {
				List<CustomUserRole> roles = cuRoleRepository.findByUser(dbUserOpt.get());
				if(roles == null || roles.isEmpty()) {
					throw new CustomException("No User Role found for User", HttpStatus.UNPROCESSABLE_ENTITY);
				}
				for(CustomUserRole customRole : cuUserRoles) {
					if(customRole.getRole().getRoleType().equals(RoleType.BORROWER.name())) {
						customUserRole = customRole;
						break;
					}
				}
				if(customUserRole == null) {
					customUserRole = cuUserRoles.get(0);
				}
			}
			String token = jwtTokenProvider.createToken(dbUser, customUserRole);
			TokenResponse response = new TokenResponse();

			if(dbUser.getName()!=null)
				response.setFirstName(dbUser.getName().trim());
			response.setLastName("");
			response.setName(dbUser.getName());
			response.setAccountName(dbUser.getAccountName());
			response.setAccessToken(token);

			UserRoleDTO userRoleDto = modelMapper.map(customUserRole, UserRoleDTO.class);

			RoleDTO roleDTO = modelMapper.map(customUserRole.getRole() , RoleDTO.class);
			roleDTO.setPermissions(CommonUtil.getAuthoritiesList(customUserRole.getRole().getAuthorities()));
			roleDTO.setPermissionDTOs(CommonUtil.getDTOFromPermissons(customUserRole.getRole().getAuthorities()));
			userRoleDto.setRole(roleDTO);

			List<UserRoleDTO> userRoles = cuUserRoles.stream().map(r-> {
				UserRoleDTO uRoleDto =	modelMapper.map(r, UserRoleDTO.class);
				RoleDTO roleResponse = modelMapper.map(r.getRole() , RoleDTO.class);
				roleResponse.setPermissions(CommonUtil.getAuthoritiesList(r.getRole().getAuthorities()));
				roleResponse.setPermissionDTOs(CommonUtil.getDTOFromPermissons(r.getRole().getAuthorities()));
				uRoleDto.setRole(roleResponse);
				return uRoleDto;
			}).collect(Collectors.toList());
			response.setUserRole(userRoles);
			response.setRole(userRoleDto);
			MasterAccount anchorMasterAccount = null ;
			//			response.setUserRole(useRoles);
			if(customUserRole.getAnchorMasterAccount() == null) {
				anchorMasterAccount  = customUserRole.getMasterAccount();
			}else {
				anchorMasterAccount = customUserRole.getAnchorMasterAccount();
			}
			response.setChannleFinancing(anchorMasterAccount.getIsChannleFinancing());
			response.setEPFinancing(anchorMasterAccount.getIsEPFinancing());
			response.setDomestic(anchorMasterAccount.getIsDomestic());
			response.setExim(anchorMasterAccount.getIsExim());
			if(anchorMasterAccount !=null) {
				if(anchorMasterAccount.getDisplayName() != null &&
						anchorMasterAccount.getDisplayName().equals("ACCORD")) {
					response.setAccord(true);
				}
				if(anchorMasterAccount.getDisplayName() != null) {
					response.setAccountName(anchorMasterAccount.getDisplayName());
				}
				if(anchorMasterAccount.getDisplayName() != null &&
						anchorMasterAccount.getDisplayName().equals("NUPHI")) {
					response.setNuphiUser(true);
				}
			}
			System.out.println(response);
		} catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// ----------------------------------------------

	@Test(expected=CustomException.class)
	public void createUserWithRoleTest() {
		
		UserDTO userDTO = DummyData.getUserDto();
		Role save = roleRepository.save(DummyData.getRole());
		userDTO.setRole("BORROWER");
		userDTO.setRoleId("22l");
		Set<String> roles=new HashSet<>();
		roles.add("23");
		userDTO.setRoles(roles);
		MasterAccount masterAccount = DummyData.getMasterAccount();
		Optional<User> usOpt = userRepository.findByEmail(userDTO.getEmail());
		//Mockito.doReturn(masterAccount).when(userService).getCurrentUserMasterAccount();
		//Optional<MasterAccount> masOpts = this.getCurrentUserMasterAccount();
		
		Optional<MasterAccount> masOpt =Optional.of(masterAccount);
		Mockito.when(this.getCurrentUserMasterAccount()).thenReturn(masOpt);


		if (!usOpt.isPresent()) {
			try {
				String name = userDTO.getName() != null ? userDTO.getName() : "User";
				User user = new User();
				user.setName(name);
				user.setPassword("test@123");
				user.setActiveLogin(true);
				user.setOtpVerified(false);
				user.setCreatedAt(new Date());
				user.setEmail(userDTO.getEmail().trim().toLowerCase());
				List<MasterAccount> masAccs = new ArrayList<>();
				masAccs.add(masOpt.get());
				user.setMasterAccounts(masAccs);
				userRepository.save(user);
				String roleName = "";
				for (String roleID : userDTO.getRoles()) {
					Optional<com.credlix.user.model.Role> roleOpt = roleRepository.findById(roleID);
					if (roleOpt.isPresent()) {
						roleName = roleOpt.get().getDisplayName();
						this.assignRole(user, userDTO.getBusinessAccounts(), roleOpt.get());
					}
				}

				try {
					String resetPassUrl;

					resetPassUrl = getResetPasswordUrl(user.getEmail());

					Map<String, Object> model = new HashMap<>();
					model.put("name", user.getName());
					model.put("toEmail", user.getEmail());
					// model.put("role" , roleName);
					model.put("resetPassUrl", resetPassUrl);
					model.put("anchorName", "Mogli Labs India Pvt. Ltd.");
					model.put("templateName", "onboardUser.ftl");
					model.put("subject", "Welcome to Credlix");
//					emailService.sendEmailWithTemplate(model);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			throw new CustomException("User already exists.", HttpStatus.BAD_REQUEST);
		}
	}
	
	//------------------------------------------------------------------------------
	
	
	
	@Test
	public void getUserBySearchKeyTest() throws Exception {
		Optional<MasterAccount> master = Optional.of(DummyData.getMasterAccount());
		Mockito.when(this.getCurrentUserMasterAccount()).thenReturn(master);

		String Key = "abcd";
		Query query = new Query(criteria.where("email").regex(Key));
		List<User> saveAll = this.userRepository.saveAll(DummyData.getUserList());
		List<User> userList = mongoTemplate.find(query, User.class);
		Set<UserDTO> userDTOs = new HashSet<UserDTO>();
		UserDTO userDto = m.map(DummyData.getUser(), UserDTO.class);
		if (!Key.isEmpty() && userList != null) {
			try {
				List<CustomUserRole> role = new ArrayList<>();
				role.add(DummyData.getCustomUserRole());

				if (userList != null && !userList.isEmpty()) {
					userDTOs = userList.stream().map(e -> {
						UserDTO userDTO = m.map(e, UserDTO.class);
						// getting roles by user
						List<CustomUserRole> userRoles = role;
						Set<RoleDTO> roleDtos = userRoles.stream().map(r -> {
							RoleDTO roleDTO;
							roleDTO = m.map(r.getRole(), RoleDTO.class);
							roleDTO.setPermissionDTOs(CommonUtil.getDTOFromPermissons(r.getRole().getAuthorities()));
							return roleDTO;
						}).collect(Collectors.toSet());
						userDTO.setUserRoles(roleDtos);
						return userDTO;
					}).collect(Collectors.toSet());
					assertThat(userDTOs);
					System.out.println(userDTOs);
				} else {
					System.out.println("No data found for " + Key);

				}

			} catch (Exception e) {
				System.out.println("No User Found related with this " + Key + "data");
			}
		} else {
			System.out.println("No data found for " + Key);
		}
	}

	
	
	public Optional<MasterAccount> getCurrentUserMasterAccount(){
	    Authentication authentication = Mockito.mock(Authentication.class);
		// Mockito.whens() for your authorization object
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("test@gmail.com", "test@123");
		//Mockito.when(auth.getPrincipal()).thenReturn(principal);
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
		
		if (user == null) {
			return Optional.empty();
		}
		User userEntity = userDetailService.loadUserEntityByEmail(user.getUsername());
		// userMo = userDetailService.loadUserByUsername(otpMessage);
		UserModel userModel = (UserModel) auth.getPrincipal();

		if(Roles.MASTER_ADMIN.name().equals(userModel.getRole())) {
			return masterAccountRepository.findByUser(userEntity);
		}else {
			// find  by role
			MasterAccount masterAccount = DummyData.getMasterAccount();
			User saveAll = this.userRepository.save(DummyData.getUser());
			masterAccount.setUser(saveAll);
			MasterAccount save2 = masterAccountRepository.save(masterAccount);
			CustomUserRole customUserRole2 = DummyData.getCustomUserRole();
			Role save = roleRepository.save(DummyData.getRole());
			customUserRole2.setUser(saveAll);
			customUserRole2.setMasterAccount(save2);
			customUserRole2.setRole(save);
			cuRoleRepository.save(customUserRole2);
			com.credlix.user.model.Role role  = roleRepository.findByNameAndRoleTypeAndIsActive(
					userModel.getRole().getName(),userModel.getRole().getRoleType(),true);
			Optional<CustomUserRole> cuOpt = cuRoleRepository.findByUserAndRole(userEntity, role);
			if(cuOpt.get().getMasterAccount() !=null) {
				return Optional.of(cuOpt.get().getMasterAccount());
			}else {
				Set<String> buIds = cuOpt.get().getBusinessAccounts();
				Iterator<String> iterator = buIds.iterator();
				Optional<BusinessAccount> busAcc = businessAccountRepository.findById(iterator.next());
				if(busAcc.isPresent()) {
					return  Optional.of( busAcc.get().getMasterAccount());
				}else {
					return Optional.empty();
				}
			}
		}
	}
	
	private void assignRole(User user, Set<String> businessAccSet, com.credlix.user.model.Role role) {
		Optional<MasterAccount> masterOpt = getCurrentUserMasterAccount();
		//CustomUserRole loggedInCuRole = userDetailService.getUserRoleByToken(userDetailService.getLoggedInUserToken());
		if(masterOpt.isPresent()) {
			List<CustomUserRole> cuOpt = cuRoleRepository.findByUser(user);
			if(CommonUtil.isNullorEmpty(cuOpt)) {
				CustomUserRole customUserRole = new CustomUserRole();
				customUserRole.setUser(user);
				customUserRole.setRole(role);
				//logged in user
				customUserRole.setCreatedBy(userDetailService.loadUserByEmail(this.getLoggedInUser().getUsername()).get());
				customUserRole.setMasterAccount(masterOpt.get());
				customUserRole.setAnchorMasterAccount(userDetailService.getAnchorMasterAccount());
				customUserRole.setBusinessAccounts(businessAccSet);
				cuRoleRepository.save(customUserRole);	
			}
		}
	}
	public UserModel getLoggedInUser() {
		return userDetailService.getLoggedInUser();
	}
	
	public String getResetPasswordUrl(String email) {
		String resetPassUrl = null;
		Optional<User> userOpt = userRepository.findByEmail(email);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			String passToken = null;;
			try {
				passToken = Utils.encode(email);
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
			ResetPasswordToken resetToken = new ResetPasswordToken();
			resetToken.setEmailToken(passToken);
			resetToken.setUser(user);
			Date date = new Date();
			resetToken.setCreatedAt(date);
			resetToken.setUpdatedAt(date);
			resetToken.setExpired(false);
			resetPasswordTokenRepository.save(resetToken);
			//resetPassUrl=config.appUrl + "auth/reset-password?token="  + passToken;
		}
		return resetPassUrl;
	}

}
