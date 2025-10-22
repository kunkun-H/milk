package com.milk.mapper;

import com.milk.entity.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ClassName: VoucherOrderMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 14:42
 * @Version 1.0
 */
@Mapper
public interface VoucherOrderMapper {

    int countByUserAndVoucher(@Param("userId") Long userId,
                              @Param("voucherId") Long voucherId);

    int insert(VoucherOrder voucherOrder);
}
