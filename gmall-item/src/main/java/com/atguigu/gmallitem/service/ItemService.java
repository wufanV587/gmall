package com.atguigu.gmallitem.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmallitem.feign.GmallPmsClient;
import com.atguigu.gmallitem.feign.GmallSmsClient;
import com.atguigu.gmallitem.feign.GmallWmsClient;
import com.atguigu.gmallitem.vo.ItemVO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    public ItemVO item(Long skuId) {

        ItemVO itemVO = new ItemVO();
        //1.通过skuId查询所有的sku信息
       CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();//获取所有suk信息
            BeanUtils.copyProperties(skuInfoEntity, itemVO);
            Long spuId = skuInfoEntity.getSpuId();
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> brandCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //2.品牌
            Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandById(skuInfoEntity.getBrandId());
            itemVO.setBrand(brandEntityResp.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> categoryCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //3.分类
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
            itemVO.setCategory(categoryEntityResp.getData());
        },threadPoolExecutor);


        CompletableFuture<Void> spuInfoCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //4.spu信息
            Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuInfo(skuInfoEntity.getSpuId());
            itemVO.setSpuInfo(spuInfoEntityResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> picsfoCompletableFuture = CompletableFuture.runAsync(() -> {
            // 5.设置图片信息
            Resp<List<String>> picsResp = this.gmallPmsClient.queryPicsBySkuId(skuId);
            itemVO.setPics(picsResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> saleCompletableFuture =  CompletableFuture.runAsync(() -> {
            // 6.营销信息
            Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsClient.queryItemSaleVOs(skuId);
            itemVO.setSales(itemSaleResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> wareCompletableFuture = CompletableFuture.runAsync(() -> {
            //7.是否有货
            Resp<List<WareSkuEntity>> wareBySkuId = this.gmallWmsClient.queryWareBySkuId(skuId);
            List<WareSkuEntity> wareBySkuIdData = wareBySkuId.getData();
            itemVO.setStore(wareBySkuIdData.stream().anyMatch(t -> t.getStock() >0));
        },threadPoolExecutor);


        CompletableFuture<Void> saleAttrCompletableFuture =  skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //8.spu所有销售属性
            Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = this.gmallPmsClient.querySkuSaleAttrValues(skuInfoEntity.getSpuId());
            itemVO.setSkuSales(saleAttrValueResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> puInfoDescCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            // 9.spu的描述信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuInfoDesc(skuInfoEntity.getSpuId());
            itemVO.setSpuInfoDesc(spuInfoDescEntityResp.getData());
        },threadPoolExecutor);


        CompletableFuture<Void> groupVOsDescCompletableFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            // 10.规格属性分组及组下的规格参数及值
            Resp<List<GroupVO>> groupVOs = this.gmallPmsClient.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
            itemVO.setGroupVOS(groupVOs.getData());
        },threadPoolExecutor);

        CompletableFuture.anyOf(brandCompletableFuture,categoryCompletableFuture,spuInfoCompletableFuture,picsfoCompletableFuture,
                saleCompletableFuture,wareCompletableFuture,saleAttrCompletableFuture,puInfoDescCompletableFuture,groupVOsDescCompletableFuture).join();
        return itemVO;
    }
}
