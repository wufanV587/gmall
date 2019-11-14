package com.atguigu.gmallitem.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import java.util.List;
import lombok.Data;

@Data
public class ItemVO extends SkuInfoEntity{
    private BrandEntity brand;//品牌信息
    private CategoryEntity category;//分类信息
    private SpuInfoEntity spuInfo;//商品描述信息
    private List<String> pics; //sku的图片列表
    private List<ItemSaleVO> sales; //销售信息
    private Boolean store; //库存信息，是否有货
    private List<SkuSaleAttrValueEntity> skuSales;//sku商品销售属性信息
    private SpuInfoDescEntity spuInfoDesc;//商品信息描述
    private List<GroupVO> groupVOS;//组及组下的规格属性及值
}
