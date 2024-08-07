package com.hotelbooking.HotelBooking.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotelbooking.HotelBooking.dto.UserLoginDTO;
import com.hotelbooking.HotelBooking.entity.User;
import com.hotelbooking.HotelBooking.entity.employee.Admin;
import com.hotelbooking.HotelBooking.responses.AdminResponse;
import com.hotelbooking.HotelBooking.responses.UserResponse;
import com.hotelbooking.HotelBooking.service.adminservice.AdminAuthService;
import com.hotelbooking.HotelBooking.service.serviceinterface.BusinessAccountService;
import com.hotelbooking.HotelBooking.service.serviceinterface.UserService;
import com.hotelbooking.HotelBooking.service.userservice.UserAuthService;
import com.hotelbooking.HotelBooking.utils.CookieUtils;
import com.hotelbooking.HotelBooking.utils.JWTUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	UserAuthService userAuthService;
	
	@Autowired
	AdminAuthService adminAuthService;
	
	@Autowired
	UserService userService;
	@Autowired
	BusinessAccountService businessAccountService;
	
	@Autowired
	private JWTUtils jwtUtils;

	@Autowired
    @Qualifier("userDetailsServiceImpl")
	private UserDetailsService userDetailsService;

	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String ENCODING_UTF8 = "UTF-8";

	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signup(@RequestBody User user) {
		User result= userAuthService.signup(user);
		return ResponseEntity.ok(new UserResponse(result));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		userAuthService.logout(response);
		return ResponseEntity.ok("logout sucess");
	}
	@PostMapping("/validate")
	public ResponseEntity<UserResponse> validate(HttpServletRequest request) {
		String accessToken = CookieUtils.getCookieValueByName(request, "accessToken");
		String userName = jwtUtils.extractUsername(accessToken);
		User user=userService.findByUserName(userName);
		UserResponse userDTO=new UserResponse(user);
		if(businessAccountService.isBusinessAccountExistForUserId(user.getId())) {
			userDTO.setHaveBusinessAccount(true);
		}
		return ResponseEntity.ok(userDTO);

	}

	@PostMapping("/signin")
	public ResponseEntity<UserResponse> signin(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response,
			HttpServletRequest request) {
		User result = userAuthService.signin(userLoginDTO, response);
		UserResponse userDTO=new UserResponse(result);
		if(businessAccountService.isBusinessAccountExistForUserId(userDTO.getId())) {
			userDTO.setHaveBusinessAccount(true);
		}
		return ResponseEntity.ok(userDTO);
	}


	@PostMapping("/refreshToken")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException {
		String refreshToken = CookieUtils.getCookieValueByName(request, "refreshToken");
		if(refreshToken==null || refreshToken.isBlank()) {
			writeErrorResponse(response, "{\"statusCode\": 403, \"error\": \"Expired\"}");
			return;
		}
		try {
			String newAccessToken = refreshAccessToken(refreshToken);
			ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken).httpOnly(true)
					.secure(true).path("/").maxAge(604800).sameSite("None").build();
			response.addHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {		
			throw e;
		}

	}

	public String refreshAccessToken(String refreshToken) {
		try {
			String userName = jwtUtils.extractUsername(refreshToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

			if (jwtUtils.isTokenValid(refreshToken, userDetails)) {
				String newAccessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
				return newAccessToken;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private void writeErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
		response.setContentType(CONTENT_TYPE_JSON);
		response.setCharacterEncoding(ENCODING_UTF8);
		response.getWriter().write(errorMessage);
	}
}
