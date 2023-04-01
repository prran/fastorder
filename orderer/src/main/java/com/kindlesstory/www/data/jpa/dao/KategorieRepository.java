package com.kindlesstory.www.data.jpa.dao;

import java.util.List;
import com.kindlesstory.www.data.jpa.table.Kategorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KategorieRepository extends JpaRepository<Kategorie, Long>
{
    List<Kategorie> findAllByOrderByAgeAsc();
}