package com.user.testdata;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.credlix.user.dto.RoleDTO;
import com.credlix.user.dto.TokenResponse;
import com.credlix.user.dto.UserDTO;
import com.credlix.user.dto.UserRoleDTO;
import com.credlix.user.enums.Authorities;
import com.credlix.user.enums.Roles;
import com.credlix.user.model.Address;
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
import com.credlix.user.repository.UserRepository;
import com.credlix.user.security.UserModel;
import com.credlix.user.service.impl.UserDetailService;
import com.credlix.user.util.CommonUtil;

import app.dto.BussinessAccountMappingDTO;
import app.dto.UserSignInRequestDTO;
import app.model.ResetPasswordToken;
import app.repository.ResetPasswordTokenRepository;
import app.util.CustomConfig;
import app.util.Utils;

	
/**
 * @author Sudhanshu
 *
 */
public class DummyData {
	
	

	@Mock
	public static UserDetailService userDetailService;
	
	@Mock
	public static CustomConfig config;
	
	@Mock
	public static MasterAccountRepository masterAccountRepository;

	@Mock
	public static BusinessAccountRepository businessAccountRepository;

	@Mock
	public static CustomUserRoleRepository cuRoleRepository;

	
	@Mock
	public static RoleRepository roleRepository;
	
	@Mock
	public static UserRepository userRepository;
	
	@Mock
	public static ResetPasswordTokenRepository resetPasswordTokenRepository;

	@Mock
	public static AuthenticationManager authenticationManager;

	@Mock
	public static Authentication authentication;

	@Mock
	public static CommonUtil commonUtil;

	

	/**
	 * 
	 * @return
	 */
	public static User getUser() {
		User user = new User();
		user.setId("2l");
		user.setName("testing");
		user.setOtpVerified(true);
		user.setActiveLogin(true);
		user.setAccountName("TeMaster");
		user.setEmail("test@gmail.com");
		user.setPhoneNumber("9822391519");
		user.setPassword("test@123");
		user.setStatus("active");
		return user;
	}
	
	/**
	 * 
	 * @return
	 */
	
	
	public static List<User> getUserList() {
		
				
		User user = new User();
		user.setId("2l");
		user.setName("testing");
		user.setOtpVerified(true);
		user.setActiveLogin(true);
		user.setAccountName("TeMaster");
		user.setEmail("test@gmail.com");
		user.setPhoneNumber("9822391519");
		user.setPassword("test@123");
		user.setStatus("active");
		
		
		
		User user1=new User();
		user1.setId("3l");
		user1.setName("data");
		user1.setOtpVerified(true);
		user1.setActiveLogin(true);
		user1.setAccountName("data");
		user1.setEmail("data@gmail.com");
		user1.setPhoneNumber("9822391519");
		user1.setPassword("Pass@123");
		user1.setStatus("active");
		
		
		User user2 = new User();
		user2.setId("4l");
		user2.setName("dummy");
		user2.setOtpVerified(true);
		user2.setActiveLogin(true);
		user2.setAccountName("Anchor");
		user2.setEmail("dummy@gmail.com");
		user2.setPhoneNumber("9822391519");
		user2.setPassword("PAss@123");
		user2.setStatus("active");
		
		
		User user3 = new User();
		user3.setId("5l");
		user3.setName("change");
		user3.setOtpVerified(true);
		user3.setActiveLogin(true);
		user3.setAccountName("Analyst");
		user3.setEmail("change@gmail.com");
		user3.setPhoneNumber("9822391519");
		user3.setPassword("cred@123");
		user3.setStatus("active");
	
		
		List<User> userList = new ArrayList<>();
		userList.add(user);
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);
		
		return userList;
	}
	/**
	 * @return MasterAccount DummyData
	 */
	public static MasterAccount getMasterAccount() {
		MasterAccount master = new MasterAccount();
		master.setId("22l");
		master.setName("Akelo");
		User user=DummyData.getUser();
		master.setUser(user);
		master.setCreatedBy(null);
		master.setAccountType("Manager");
		
		return master;
	}
	
	
	
	public static List<MasterAccount> getMasterAccountList() {
		MasterAccount master = new MasterAccount();
		master.setId("22l");
		master.setName("Akelo");
		master.setUser(new User());
		master.setCreatedBy(null);
		master.setAccountType("Manager");
		
		MasterAccount master1 = new MasterAccount();
		master1.setId("33l");
		master1.setName("Toboqui");
		master1.setUser(new User());
		master1.setCreatedBy(null);
		master1.setAccountType("Anchor");
		
		MasterAccount master2 = new MasterAccount();
		master2.setId("44l");
		master2.setName("Hothi");
		master2.setUser(new User());
		master2.setCreatedBy(null);
		master2.setAccountType("Admin");
		
		MasterAccount master3 = new MasterAccount();
		master3.setId("55l");
		master3.setName("Temu");
		master3.setUser(new User());
		master3.setCreatedBy(null);
		master3.setAccountType("Tester");
		
		
		List<MasterAccount> masterList=new ArrayList<>();
		masterList.add(master);
		masterList.add(master1);
		masterList.add(master2);
		masterList.add(master3);
		
		return masterList;
		
		
	}

	/**
	 * @return
	 */
	
	public static CustomUserRole getCustomUserRole() {
		CustomUserRole cuuser = new CustomUserRole();
		cuuser.setAccessType("Complete");
		cuuser.setAnchorMasterAccount(getMasterAccount());
		Set<String> data=new HashSet<>();
		cuuser.setBusinessAccounts(data);
		User user = DummyData.getUser();
		cuuser.setCreatedBy(user);
		cuuser.setMasterAccount(getMasterAccount());
		cuuser.setId("3");
		Role role2=DummyData.getRole();
		cuuser.setRole(role2);
		cuuser.setUser(user);
		return cuuser;
	}
	
	 

	/**
	 * @return
	 */
	public static ArrayList<CustomUserRole> getCustomUserRolesList() {
		ArrayList<CustomUserRole> arrayList = new ArrayList<CustomUserRole>();
		arrayList.add(getCustomUserRole());
		return arrayList;
	}

	/**
	 * @return
	 */
	public static UserDTO getUserDto() {
		UserDTO userdt = new UserDTO();
		userdt.setId("2l");
		userdt.setActiveLogin(true);
		userdt.setEmail("test@gmail.com");
		userdt.setName("Admin");
		userdt.setPassword("test@123");
		userdt.setRoleId(null);
		userdt.setRoles(new LinkedHashSet<String>());
		userdt.setTotalCount(1);
		userdt.setUserRoles(new LinkedHashSet<RoleDTO>());
		userdt.setBusinessAccounts(null);
		userdt.setFirstName("Testing");
		userdt.setLastName("method");
		return userdt;
		
	}

	/**
	 * @return
	 */
	public static Role getRole() {

		Role role = new Role();
		role.setActive(true);
		role.setBusinessId("32");
		Date test=new Date();
		test.setDate(2021/9/23);
		role.setCreatedAt(test);
		MasterAccount masterAccount = DummyData.getMasterAccount();
		role.setCreatedFor(masterAccount);
		role.setDefault(true);
		role.setDescription("sdj");
		role.setDisplayName("testing");
		role.setId("23");
		role.setName("BORROWER");
		role.setRoleType("BORROWER");
		role.setUniqueId("324");
		role.setUpdatedAt(new Date());
		HashSet<Authorities> hashSet = new HashSet<Authorities>();
		hashSet.add(Authorities.ADD_BORROWER);
		hashSet.add(Authorities.ACTIVATE_DEACTIVATE_USER);
		role.setAuthorities(hashSet);
		return role;
	}

	/**
	 * @return
	 */
	public static RoleDTO getRoleDto() {
		RoleDTO roleDto = new RoleDTO();
		roleDto.setBusinessId("ds");
		roleDto.setId("dsds");
		roleDto.setRoleType("BORROWER");
		roleDto.setId("33");
		roleDto.setName("Manager");
		roleDto.setDisplayName("anchor");
		roleDto.setPermissions(null);
		return roleDto;
	}

	/**
	 * @return
	 */
	public static JwtAccessToken getJwtToken() {
		JwtAccessToken token = new JwtAccessToken();
		token.setAccessToken("test");
		token.setBussinessId("Anchor");
		CustomUserRole customUserRole=DummyData.getCustomUserRole();
		token.setCustomUserRole(customUserRole);
		token.setEmail("test@gmail.com");
		token.setExpired(false);
		token.setForceExpire(false);
		token.setId("33");
		User user = DummyData.getUser();
		token.setUser(user);
		return token;
	}

	/**
	 * @return
	 */
	public static TokenResponse getresponseToken() {
		TokenResponse tokenResponse = new TokenResponse();
		tokenResponse.setAccessToken("ewsa");
		tokenResponse.setAccountName("me");
		tokenResponse.setBusinessId("8327");
		tokenResponse.setFirstName("Admin");
		tokenResponse.setLastName("");
		tokenResponse.setName("test");
		tokenResponse.setPermissions(new HashSet<>());
		UserRoleDTO userRole = new UserRoleDTO();
		userRole.setRole(DummyData.getRoleDto());
		tokenResponse.setRole(userRole);
		tokenResponse.setUserRole(new ArrayList<>());

		return tokenResponse;

	}

	/**
	 * @return
	 */
	public static UserSignInRequestDTO getuserSignInRequestDto() {

		UserSignInRequestDTO user = new UserSignInRequestDTO();
		user.setEmail("test@gmail.com");
		user.setPassword("9831111");

		return user;

	}
	
	public static BusinessAccount getBusiness() {
		BusinessAccount bus=new BusinessAccount();
		bus.setAccountRole(null);
		bus.setAddresses(null);
		
		return null;
		
	}
	
	public static org.springframework.security.core.userdetails.User getSecureUser(){
		org.springframework.security.core.userdetails.User user=new org.springframework.security.core.userdetails.User("test@gmail.com", "test@123", false, false, false, false, null );
		
		return user;
	}
}



