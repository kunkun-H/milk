package com.milk.mapper;

import com.milk.entity.EvaluationImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: CategoryMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/1/31 23:52
 * @Version 1.0
 */
@Mapper
public interface EvaluationImageMapper {

    void insert(EvaluationImage img);

    @Select("select * from evaluation_image where evaluation_id=#{id}")
    List<EvaluationImage> selectByEvaluationId(Integer id);
}
