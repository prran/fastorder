package com.kindlesstory.www.data.jpa.table;

import javax.persistence.AccessType;
import javax.persistence.Access;
import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity(name = "KATEGORIE_TB")
public class Kategorie implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202212141558L;
    @Id
    @Column(name = "KATE_NAME", nullable = false)
    private String name;
    @Column(name = "AGE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date age;
    
    public String getName() {
        return name;
    }
    
    public Date getAge() {
        return age;
    }
    
    @Access(AccessType.PROPERTY)
    public void setName(final String name) {
        this.name = name;
    }
    
    @Access(AccessType.PROPERTY)
    public void setAge(final Date age) {
        this.age = age;
    }
}