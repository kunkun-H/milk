package com.milk.mapper;

import com.milk.entity.EvaluationReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
public interface EvaluationReplyMapper {


    void insert(EvaluationReply evaluationReply);

    @Select("select * from evaluation_reply where evaluation_id=#{id}")
    EvaluationReply getByEvaluationId(Integer id);
}
