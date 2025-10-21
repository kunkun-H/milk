package com.milk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.milk.constant.MessageConstant;
import com.milk.constant.StatusConstant;
import com.milk.dto.SetmealDTO;
import com.milk.dto.SetmealPageQueryDTO;
import com.milk.entity.Goods;
import com.milk.entity.Setmeal;
import com.milk.entity.SetmealGoods;
import com.milk.exception.DeletionNotAllowedException;
import com.milk.exception.NameRepeatException;
import com.milk.exception.SetmealEnableFailedException;
import com.milk.mapper.GoodsMapper;
import com.milk.mapper.SetmealGoodsMapper;
import com.milk.mapper.SetmealMapper;
import com.milk.result.PageResult;
import com.milk.service.SetmealService;
import com.milk.vo.GoodsItemVO;
import com.milk.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: SetmealServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/4 18:21
 * @Version 1.0
 */
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealGoodsMapper setmealGoodsMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmealByName=setmealMapper.getByName(setmealDTO.getName());
        if(setmealByName!=null){
            throw new NameRepeatException("套餐名称已存在");
        }
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表插入数据
        setmealMapper.insert(setmeal);

        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealGoods> setmealGoods = setmealDTO.getSetmealGoods();
        if(setmealGoods !=null && setmealGoods.size()>0){
            setmealGoods.forEach(setmealGoodes -> {
                setmealGoodes.setSetmealId(setmealId);
            });
            //保存套餐和商品的关联关系
            setmealGoodsMapper.insertBatch(setmealGoods);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售商品，有停售商品提示"套餐内包含未启售菜品，无法启售"
        if(status == StatusConstant.ENABLE){
            //select a.* from goods a left join setmeal_goods b on a.id = b.goods_id where b.setmeal_id = ?
            List<Goods> goodsList = goodsMapper.getBySetmealId(id);
            if(goodsList != null && goodsList.size() > 0){
                goodsList.forEach(goods -> {
                    if(StatusConstant.DISABLE == goods.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐商品关系表中的数据
            setmealGoodsMapper.deleteBySetmealId(setmealId);
        });
    }

    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealGoods> setmealGoods = setmealGoodsMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealGoods(setmealGoods);

        return setmealVO;

    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //1、修改套餐表，执行update
        setmealMapper.update(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //2、删除套餐和商品的关联关系，操作setmeal_goods表，执行delete
        setmealGoodsMapper.deleteBySetmealId(setmealId);

        List<SetmealGoods> setmealGoods = setmealDTO.getSetmealGoods();
        setmealGoods.forEach(setmealGoodes -> {
            setmealGoodes.setSetmealId(setmealId);
        });
        //3、重新插入套餐和商品的关联关系，操作setmeal_goods表，执行insert
        setmealGoodsMapper.insertBatch(setmealGoods);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询商品选项
     * @param id
     * @return
     */
    public List<GoodsItemVO> getGoodsItemById(Long id) {
        return setmealMapper.getGoodsItemBySetmealId(id);
    }
}
