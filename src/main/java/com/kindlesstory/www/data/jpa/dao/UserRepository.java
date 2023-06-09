package com.kindlesstory.www.data.jpa.dao;

import com.kindlesstory.www.data.jpa.table.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>
{
    User findByUserName(String userName);
}