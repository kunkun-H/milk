package com.milk.mapper;

import com.milk.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * ClassName: VoucherMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 11:33
 * @Version 1.0
 */
@Mapper
public interface VoucherMapper {
    void insert(SeckillVoucher seckillVoucher);

    @Select("select * from seckill_voucher where id=#{voucherId}")
    SeckillVoucher getById(Long voucherId);

    int update(@Param("voucherId") Long voucherId);
}
