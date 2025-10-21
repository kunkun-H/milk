package com.milk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.milk.constant.MessageConstant;
import com.milk.constant.StatusConstant;
import com.milk.dto.GoodsDTO;
import com.milk.dto.GoodsPageQueryDTO;
import com.milk.entity.Goods;
import com.milk.entity.GoodsFlavor;
import com.milk.exception.DeletionNotAllowedException;
import com.milk.exception.NameRepeatException;
import com.milk.mapper.GoodsFlavorMapper;
import com.milk.mapper.GoodsMapper;
import com.milk.mapper.SetmealGoodsMapper;
import com.milk.result.PageResult;
import com.milk.service.GoodsService;
import com.milk.vo.GoodsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: GoodsServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/2 23:44
 * @Version 1.0
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;
    @Autowired
    private SetmealGoodsMapper setmealGoodsMapper;

    @Override
    @Transactional
    public void saveWithFlavor(GoodsDTO goodsDTO) {
        Goods goodsByName = goodsMapper.getByName(goodsDTO.getName());
        if(goodsByName !=null){
            throw new NameRepeatException("商品名称已存在");
        }
        Goods goods =new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        goodsMapper.insert(goods);

        Long id = goods.getId();

        List<GoodsFlavor> flavors = goodsDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            for(GoodsFlavor flavor : flavors){
                flavor.setGoodsId(id);
            }
            goodsFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult page(GoodsPageQueryDTO goodsPageQueryDTO) {
        PageHelper.startPage(goodsPageQueryDTO.getPage(), goodsPageQueryDTO.getPageSize());
        Page<GoodsVO> page= goodsMapper.page(goodsPageQueryDTO);
        long total = page.getTotal();
        List<GoodsVO> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void openOrStop(Integer status, Long id) {
        Goods goods =new Goods();
        goods.setId(id);
        goods.setStatus(status);
        goodsMapper.update(goods);
    }

    @Override
    public void update(GoodsDTO goodsDTO) {
        Goods goods =new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        goodsMapper.update(goods);

        //删除原有的口味数据
        goodsFlavorMapper.deleteByGoodsId(goodsDTO.getId());

        List<GoodsFlavor> flavors = goodsDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            for(GoodsFlavor flavor : flavors){
                flavor.setGoodsId(goodsDTO.getId());
            }
            goodsFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public GoodsVO selectById(Long id) {
        Goods goods = goodsMapper.getById(id);
        List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.getByGoodsId(id);
        GoodsVO goodsVO =new GoodsVO();
        BeanUtils.copyProperties(goods, goodsVO);
        goodsVO.setFlavors(goodsFlavors);
        return goodsVO;
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for(Long id : ids){
            Goods goods = goodsMapper.getById(id);
            if(goods.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setMealIds = setmealGoodsMapper.getSetmealIdsByGoodsIds(ids);
        if(setMealIds!=null && setMealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //循环删除
//        for(Long id:ids){
//            goodsMapper.deleteById(id);
//            goodsFlavorMapper.deleteByGoodsId(id);
//        }

        //批量删除
        goodsMapper.deleteBatchByIds(ids);
        goodsFlavorMapper.deleteBatchByGoodsIds(ids);
    }

    @Override
    public List<Goods> list(Long categoryId) {
        Goods goods =new Goods();
        goods.setCategoryId(categoryId);
        goods.setStatus(StatusConstant.ENABLE);
        return goodsMapper.getListByCategoryId(goods);
    }

    /**
     * 条件查询商品和口味
     * @param goods
     * @return
     */
    public List<GoodsVO> listWithFlavor(Goods goods) {
        List<Goods> goodsList = goodsMapper.getListByCategoryId(goods);

        List<GoodsVO> goodsVOList = new ArrayList<>();

        for (Goods d : goodsList) {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(d, goodsVO);

            //根据商品id查询对应的口味
            List<GoodsFlavor> flavors = goodsFlavorMapper.getByGoodsId(d.getId());

            goodsVO.setFlavors(flavors);
            goodsVOList.add(goodsVO);
        }

        return goodsVOList;
    }

    @Override
    public List<Goods> getListByName(String name) {
        Goods goods =new Goods();
        goods.setName(name);
        goods.setStatus(StatusConstant.ENABLE);
        return goodsMapper.getListByCategoryId(goods);
    }
}
