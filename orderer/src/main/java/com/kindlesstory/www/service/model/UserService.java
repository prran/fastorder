package com.kindlesstory.www.service.model;

import com.kindlesstory.www.exception.DatabaseException;
import org.springframework.dao.DataAccessException;
import com.kindlesstory.www.data.jpa.table.User;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.data.jpa.dao.UserRepository;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.DatabaseService;

@Service
public class UserService implements DatabaseService
{
    @Autowired
    private UserRepository userRepository;
    
    public boolean setName(final String passId, final String name) throws DatabaseException {
        try {
            final User user = new User();
            user.setUserId(passId);
            user.setUserName(name);
            this.userRepository.save(user);
            return true;
        }
        catch (DataAccessException e) {
            return false;
        }
        catch (Exception e2) {
            throw new DatabaseException();
        }
    }
    
    public String getName(final String passId) throws DatabaseException {
        try {
            final User user = this.userRepository.findById(passId).orElse(new User());
            return user.getUserName();
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public User findByName(final String name) throws DatabaseException {
        try {
            return this.userRepository.findByUserName(name);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void reverseBan(final String name) {
        final User user = this.userRepository.findByUserName(name);
        user.setUserBan(!user.getUserBan());
        this.userRepository.save(user);
    }
}