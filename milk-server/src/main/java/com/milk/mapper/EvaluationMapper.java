package com.milk.mapper;

import com.github.pagehelper.Page;
import com.milk.dto.EvaluationPageQueryDTO;
import com.milk.entity.Evaluation;
import com.milk.vo.EvaluationCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

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
public interface EvaluationMapper {

    void insert(Evaluation evaluation);

    Page<Evaluation> pageQuery(EvaluationPageQueryDTO evaluationPageQueryDTO);

    @Update("update evaluation set is_reply=1 where id=#{evaluationId} ")
    void update(Integer evaluationId);

    @Select("select * from evaluation where order_id= #{orderId}")
    Evaluation selectByOrderId(Long orderId);

    Integer countByMap(Map map);

    List<Evaluation> selectShopAllEvaluation(String type);

    EvaluationCountVO selectEvaluationCount();
}
