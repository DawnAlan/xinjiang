package com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼庄子水库平台水情结构树(LzzPlatformTree)表实体类
 *
 * @author makejava
 * @since 2023-12-05 19:26:23
 */
@Data
public class LzzPlatformTree extends Model<LzzPlatformTree> {
    //主键ID
    private String id;
    //名称
    private String name;
    //父ID
    private String pId;

}

