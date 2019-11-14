package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import java.util.List;
import lombok.Data;

@Data
public class CategoryVO extends CategoryEntity {

    private List<CategoryEntity> subs;

}
