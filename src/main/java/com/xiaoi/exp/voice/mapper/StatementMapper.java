package com.xiaoi.exp.voice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoi.exp.voice.entity.Statement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementMapper extends BaseMapper<Statement> {

    @Select("select * from satisfaction order by create_time limit #{start},#{limit}")
    public List<Statement> getList(int start, int limit);

    @Select("select count(*) from satisfaction")
    public int getCount();

}
