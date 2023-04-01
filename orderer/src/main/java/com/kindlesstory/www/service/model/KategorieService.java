package com.kindlesstory.www.service.model;

import com.kindlesstory.www.exception.DatabaseException;
import com.kindlesstory.www.data.jpa.table.Kategorie;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.data.jpa.dao.KategorieRepository;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.DatabaseService;

@Service
public class KategorieService implements DatabaseService
{
    @Autowired
    private KategorieRepository kategorie;
    
    public List<String> getNames() throws DatabaseException {
        try {
            final List<Kategorie> kategories = this.kategorie.findAllByOrderByAgeAsc();
            final List<String> names = new ArrayList<String>();
            for (final Kategorie kategorie : kategories) {
                names.add(kategorie.getName());
            }
            return names;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
}