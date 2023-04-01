package com.kindlesstory.www.data.jpa.dao.custom;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.kindlesstory.www.exception.DatabaseException;
import java.sql.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class CustomQuery
{
    @Autowired
    private DriverManagerDataSource jdbc;
    private Connection conn;
    private static final String RANKING_SQL_FORMAT = "select rank from (select @crank := @crank + 1 as rank, SORT_ITEM_LENGTH from (select SORT_ITEM_LENGTH from SORT_ITEM_TB group by SORT_ITEM_LENGTH) as t, (select @crank := 0) r order by SORT_ITEM_LENGTH) as t where SORT_ITEM_LENGTH = (select SORT_ITEM_LENGTH from SORT_ITEM_TB where ITEM_REF_CODE = '%s');";
    
    public int findRankBySortItemLengthAsItemRefCodeInSortItem(final String itemRefCode) throws DatabaseException {
        try {
            final String SQL = String.format(RANKING_SQL_FORMAT, itemRefCode);
            if (conn == null) {
                conn = jdbc.getConnection();
            }
            final PreparedStatement psmt = conn.prepareStatement(SQL);
            final ResultSet rs = psmt.executeQuery();
            int resultValue = 0;
            while (rs.next()) {
                resultValue = rs.getInt("rank");
            }
            psmt.close();
            return resultValue;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
}