package com.xiaoi.exp.voice.mapper;

import com.xiaoi.exp.voice.entity.Statement;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementMapper {

    @Select("select * from satisfaction")
    public List<Statement> getList();
}
