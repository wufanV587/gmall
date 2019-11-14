package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import java.util.List;
import lombok.Data;

@Data
public class GroupVO {
    private String groupName;
    private List<ProductAttrValueEntity> baseAttrValues;
}
