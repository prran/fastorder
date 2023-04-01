package com.kindlesstory.www.data.jpa.table;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity(name = "USER_TB")
public class User implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202212260439L;
    @Id
    @Column(name = "USER_ID", nullable = false)
    private String userId;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "USER_BAN")
    private boolean userBan;
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public boolean getUserBan() {
        return userBan;
    }
    
    public void setUserBan(final boolean userBan) {
        this.userBan = userBan;
    }
}