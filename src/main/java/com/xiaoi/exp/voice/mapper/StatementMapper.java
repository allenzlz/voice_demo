package com.xiaoi.exp.voice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoi.exp.voice.entity.Statement;
import com.xiaoi.exp.voice.entity.TjBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StatementMapper extends BaseMapper<Statement> {

    @Select("select * from satisfaction order by create_time limit #{start},#{limit}")
    public List<Statement> getList(int start, int limit);

    @Select("select count(*) from satisfaction")
    public int getCount();

    @Select("select satis_results,count(*) as count,stat_key from satisfaction group by satis_results,stat_key")
    public List<Map> getStCount();

    @Select("select stat_key from satisfaction group by stat_key order by stat_key desc")
    public List<Statement> getListByGourpByDate();

    @Select("select satis_results,count(*) as count from satisfaction where stat_key = #{stat_key}  group by satis_results")
    public List<Map> getListByGourpBySatisResults(String stat_key);
}
