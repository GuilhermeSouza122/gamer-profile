package com.gamerprofile.service;

import com.gamerprofile.model.User;
import com.gamerprofile.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

	private final UserRepository userRepository;

	public CurrentUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User requireUser(HttpServletRequest request) {
		if (request.getSession(false) == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
		}
		Object userId = request.getSession(false).getAttribute("userId");
		if (!(userId instanceof Number number)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
		}
		return userRepository.findById(number.longValue())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User session expired"));
	}
}
