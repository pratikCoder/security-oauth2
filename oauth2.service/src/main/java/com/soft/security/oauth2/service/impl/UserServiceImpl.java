package com.soft.security.oauth2.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.soft.security.oauth2.common.constants.ErrorMessages;
import com.soft.security.oauth2.common.exceptions.CommonException;
import com.soft.security.oauth2.db.entity.UserEntity;
import com.soft.security.oauth2.db.repository.UserJpaRepository;
import com.soft.security.oauth2.model.UserModel;
import com.soft.security.oauth2.service.UserAuth;
import com.soft.security.oauth2.service.UserService;
import com.soft.security.oauth2.service.utils.CommonUtils;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserJpaRepository userJpaRepo;

	@Autowired
	UserAuth userAuth;

	@Autowired
	private ModelMapper modelMapper;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public Boolean validateUser(String emailId, String password) throws CommonException {
		Boolean isValidUser = false;
		UserEntity userEntity = null;
		if (emailId.contains("@")) {
			userEntity = userJpaRepo.getUserByUserEmailId(emailId);
		}
		if (userEntity == null) {
			logger.error(ErrorMessages.USER_DOES_NOT_EXISTS + emailId);
			throw new CommonException(ErrorMessages.USER_DOES_NOT_EXISTS + emailId);
		} else if (!passwordEncoder.matches(password, userEntity.getPassword())) {
			logger.error(password, ErrorMessages.INVALID_USERNAME_AND_PASSWORD);
			throw new CommonException(ErrorMessages.INVALID_USERNAME_AND_PASSWORD);
		} else {
			isValidUser = true;
		}
		System.out.println("userEntity : " + userEntity);
		System.out.println("isValidUser : " + isValidUser);
		return isValidUser;
	}

	@Override
	public UserAuth getUserAuthorizationByEmailId(String emailId) throws CommonException {

		// UserAuth userAuth = new UserAuthImpl();

		UserEntity userEntity = validateIdAndGetUserEnt(emailId);
		userAuth.setUser(userEntity);

		List<GrantedAuthority> authoritiesList = new ArrayList<GrantedAuthority>();
		SimpleGrantedAuthority sga = new SimpleGrantedAuthority("USER");
		authoritiesList.add(sga);
		userAuth.setAuthoritiesList(authoritiesList);

		return userAuth;
	}

	private UserEntity validateIdAndGetUserEnt(String emailId) throws CommonException {
		if (!emailId.contains("@")) {
			throw new CommonException(ErrorMessages.USER_EMAIL_INVALID);
		}

		UserEntity userEntity = userJpaRepo.getUserByUserEmailId(emailId);
		// Validtion of user is exists
		// if (userEntity.getIsFirstTimeLogin()) {
		// System.out.println("toString==> " + userEntity.toString());
		// userEntity = updateFirstTimeLogin(userEntity.getUserEmailId());
		// }
		if (userEntity == null) {
			logger.error(ErrorMessages.USER_DOES_NOT_EXISTS + emailId);
			throw new CommonException(ErrorMessages.USER_DOES_NOT_EXISTS + emailId);
		}
		return userEntity;
	}

	//////////////////////////////////////////////////////////////////////

	@Override
	public UserModel addUser(UserModel userModel) throws CommonException {
		logger.debug("addUser() :: userModel : " + userModel);

		// UserServiceUtils.addUserModelValidate(userModel);

		Date createdDate = CommonUtils.currentDate();

		UserEntity userEntity = new UserEntity();
		userEntity.setCreatedDate(createdDate);
		userEntity.setUpdatedDate(createdDate);

		modelMapper.map(userModel, userEntity);

		String password = userEntity.getPassword();

		String encryptPassword = passwordEncoder.encode(password);

		userEntity.setPassword(encryptPassword);

		userEntity = userJpaRepo.saveAndFlush(userEntity);
		modelMapper.map(userEntity, userModel);
		logger.debug("addUser() end");

		userModel.setConfirmPassword(null);
		userModel.setPassword(null);

		return userModel;
	}

	@Override
	public UserModel updateUser(UserModel userModel) throws CommonException {
		logger.debug("updateUser() :: userModel : {} ", userModel);

		// UserServiceUtils.addUserModelValidate(userModel);

		Date updatedDate = CommonUtils.currentDate();

		UserEntity userEntity = new UserEntity();
		System.out.println("Date" + updatedDate);

		modelMapper.map(userModel, userEntity);

		userEntity.setUpdatedDate(updatedDate);

		System.out.println("==>" + userEntity.getCreatedDate());

		userEntity = userJpaRepo.saveAndFlush(userEntity);
		modelMapper.map(userEntity, userModel);
		logger.debug("updateUser() :: userModel : {} ", userModel);

		userModel.setConfirmPassword(null);
		userModel.setPassword(null);

		return userModel;
	}

	@Override
	public UserModel resetPassword(String oldPassword, String newPassword, String newConfirmPassword, Long userId)
			throws CommonException {
		logger.debug("resetPassword() :: userId : oldPassword : newPassword : newConfirmPassword : {} ", userId,
				oldPassword, newPassword, newConfirmPassword);

		// UserServiceUtils.addUserModelValidate(userModel);

		Date updatedDate = CommonUtils.currentDate();

		UserEntity userEntity = new UserEntity();
		UserModel userModel = new UserModel();
		System.out.println("Date" + updatedDate);

		userEntity = userJpaRepo.getOne(userId);

		userEntity.setUpdatedDate(updatedDate);

		System.out.println("==>" + userEntity.getCreatedDate());

		String oldPassEncrypt = passwordEncoder.encode(oldPassword);
		if (oldPassEncrypt.equals(userEntity.getPassword())) {
			String encryptPassword = passwordEncoder.encode(newPassword);
			userEntity.setPassword(encryptPassword);
		}

		userEntity = userJpaRepo.saveAndFlush(userEntity);
		modelMapper.map(userEntity, userModel);
		logger.debug("resetPassword() :: userModel : {} ", userModel);

		userModel.setConfirmPassword(null);
		userModel.setPassword(null);

		return userModel;
	}

	@Override
	public UserModel deleteUser(Long userId) throws CommonException {
		logger.debug("deleteUser() :: userId : {} ", userId);

		Date updatedDate = CommonUtils.currentDate();
		UserEntity userEntity = new UserEntity();
		UserModel userModel = new UserModel();

		userEntity = userJpaRepo.getOne(userId);

		userEntity.setUpdatedDate(updatedDate);
		userEntity.setIsActive(false);

		userEntity = userJpaRepo.saveAndFlush(userEntity);
		modelMapper.map(userEntity, userModel);
		logger.debug("deleteUser() :: userModel : {} ", userModel);

		userModel.setConfirmPassword(null);
		userModel.setPassword(null);

		return userModel;

	}

}
