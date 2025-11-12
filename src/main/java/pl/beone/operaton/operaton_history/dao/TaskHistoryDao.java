package pl.beone.operaton.operaton_history.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

@Primary
@Mapper
public interface TaskHistoryDao {
    List<Map<String,String>> getFullNameByUsername(@Param("username") String username);
}
