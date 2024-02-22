package com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 库容曲线表(StorageCapacityCurve)表实体类
 *
 * @author makejava
 * @since 2023-12-02 16:39:04
 */
@Data
public class StorageCapacityCurve extends Model<StorageCapacityCurve> {
    //水位
    private BigDecimal waterLevel;
    //小数位
    private BigDecimal interpolation;
    //库容
    private BigDecimal storageCapacity;
    //水库代码
    private String reservoir;

    private Double rate;

}

