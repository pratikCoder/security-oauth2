package com.soft.security.oauth2.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.soft.security.oauth2.db.entity.UserEntity;

public interface UserJpaRepository
		extends JpaRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {

	public UserEntity getUserByUserEmailId(String emailId);

}
