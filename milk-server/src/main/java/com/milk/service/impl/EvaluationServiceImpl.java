package com.milk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.milk.context.BaseContext;
import com.milk.dto.EvaluationDTO;
import com.milk.dto.EvaluationPageQueryDTO;
import com.milk.dto.EvaluationReplyDTO;
import com.milk.entity.*;
import com.milk.mapper.*;
import com.milk.result.PageResult;
import com.milk.service.EvaluationService;
import com.milk.vo.EvaluationCountVO;
import com.milk.vo.EvaluationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: EvaluationServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/1 23:01
 * @Version 1.0
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private EvaluationMapper evaluationMapper;
    @Autowired
    private EvaluationImageMapper evaluationImageMapper;
    @Autowired
    private EvaluationReplyMapper evaluationReplyMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Transactional
    public void saveEvaluationWithImages(EvaluationDTO dto) {
        Evaluation evaluation = new Evaluation();
        BeanUtils.copyProperties(dto, evaluation);
        Orders orders=orderMapper.selectById(dto.getOrderId());
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUserId(orders.getUserId());
        evaluation.setNumber(orders.getNumber());
        evaluation.setPhone(orders.getPhone());
        evaluation.setConsignee(orders.getConsignee());
        evaluation.setIsReply(0);
        evaluationMapper.insert(evaluation);
        int id=evaluation.getId();

        orders.setStatus(8);
        orderMapper.update(orders);//修改订单状态为已评价

        if (dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                EvaluationImage img = new EvaluationImage();
                img.setEvaluationId(id);
                img.setImage(url);
                img.setCreateTime(LocalDateTime.now());
                evaluationImageMapper.insert(img);
            }
        }
    }

    @Override
    public PageResult conditionSearch(EvaluationPageQueryDTO evaluationPageQueryDTO) {
        PageHelper.startPage(evaluationPageQueryDTO.getPage(),evaluationPageQueryDTO.getPageSize());

        Page<Evaluation> page = evaluationMapper.pageQuery(evaluationPageQueryDTO);
        List<EvaluationVO> evaluationVOList = getEvaluationVOList(page);

        if(evaluationVOList!=null){
            for(EvaluationVO e:evaluationVOList){
                EvaluationReply evaluationReply=evaluationReplyMapper.getByEvaluationId(e.getId());
                if(evaluationReply!=null){
                    e.setReplyContent(evaluationReply.getReplyContent());
                    Employee employee = employeeMapper.selectEmployeeById(evaluationReply.getAdminId());
                    e.setAdminUserName(employee.getUsername());
                }
            }
        }
        return new PageResult(page.getTotal(), evaluationVOList);
    }

    private List<EvaluationVO> getEvaluationVOList(Page<Evaluation> page) {
        List<Evaluation> result = page.getResult();
        List<EvaluationVO> voList=new ArrayList<>();
        if(result!=null){
            for(Evaluation e:result){
                EvaluationVO evaluationVO=new EvaluationVO();
                BeanUtils.copyProperties(e,evaluationVO);
                List<EvaluationImage> imageList=evaluationImageMapper.selectByEvaluationId(e.getId());
                evaluationVO.setEvaluationImageList(imageList);
                voList.add(evaluationVO);
            }
        }
        return voList;
    }

    @Override
    public void reply(EvaluationReplyDTO evaluationReplyDTO) {
        EvaluationReply evaluationReply=new EvaluationReply();
        BeanUtils.copyProperties(evaluationReplyDTO,evaluationReply);
        Long currentId = BaseContext.getCurrentId();
        evaluationReply.setAdminId(currentId);
        evaluationReply.setReplyTime(LocalDateTime.now());
        evaluationReplyMapper.insert(evaluationReply);
        evaluationMapper.update(evaluationReplyDTO.getEvaluationId());
    }

    @Override
    public EvaluationVO getEvaluationDeatils(Long orderId) {
        Evaluation evaluation=evaluationMapper.selectByOrderId(orderId);
        EvaluationVO evaluationVO=new EvaluationVO();
        BeanUtils.copyProperties(evaluation,evaluationVO);
        List<EvaluationImage> evaluationImages = evaluationImageMapper.selectByEvaluationId(evaluation.getId());
        if(evaluationImages!=null){
            evaluationVO.setEvaluationImageList(evaluationImages);
        }
        EvaluationReply evaluationReply = evaluationReplyMapper.getByEvaluationId(evaluation.getId());
        if(evaluationReply!=null){
            evaluationVO.setReplyContent(evaluationReply.getReplyContent());
        }
        return evaluationVO;
    }

    @Override
    public List<EvaluationVO> getShopAllEvaluation(String type) {
        List<Evaluation> evaluationList=evaluationMapper.selectShopAllEvaluation(type);
        List<EvaluationVO> evaluationVOList=new ArrayList<>();
        for(Evaluation evaluation:evaluationList){
            //评价基本信息
            EvaluationVO evaluationVO=new EvaluationVO();
            BeanUtils.copyProperties(evaluation,evaluationVO);
            //获取评价的图片
            List<EvaluationImage> evaluationImages = evaluationImageMapper.selectByEvaluationId(evaluation.getId());
            evaluationVO.setEvaluationImageList(evaluationImages);
            //获取评价的回复信息
            EvaluationReply evaluationReply = evaluationReplyMapper.getByEvaluationId(evaluation.getId());
            if(evaluationReply!=null){
                evaluationVO.setReplyContent(evaluationReply.getReplyContent());
            }
            //获取用户的头像
            User userMapperById = userMapper.getById(evaluation.getUserId());
            evaluationVO.setAvatar(userMapperById.getAvatar());
            //获取商品名称
            List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(evaluation.getOrderId());
            if (byOrderId != null && !byOrderId.isEmpty()) {
                // 用流式写法更简洁
                String goodsNames = byOrderId.stream()
                        .map(OrderDetail::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" + ")); // 用“+”分隔
                evaluationVO.setName(goodsNames);
            }
            //加入集合
            evaluationVOList.add(evaluationVO);
        }
        return evaluationVOList;
    }

    @Override
    public EvaluationCountVO getEvaluationCount() {
        return evaluationMapper.selectEvaluationCount();
    }


}
