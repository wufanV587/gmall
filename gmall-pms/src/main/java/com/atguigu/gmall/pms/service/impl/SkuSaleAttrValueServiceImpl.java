package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SkuSaleAttrValueDao;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import org.springframework.util.CollectionUtils;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageVo(page);
    }


 /*   @Override
    public List<SkuSaleAttrValueEntity> querySkuSaleAttrValues(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuInfoEntities)){
            return null;
        }
//        Map<Long, List<SkuInfoEntity>> collect = skuInfoEntities.stream().collect(Collectors.groupingBy(SkuInfoEntity::getSpuId));
        List<Long> skuIds = skuInfoEntities.stream().map(skuInfoEntity -> skuInfoEntity.getSkuId()).collect(Collectors.toList());
        return this.list(new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id",skuIds));
    }*/

    @Override
    public List<SkuSaleAttrValueEntity> querySkuSaleAttrValues(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuInfoEntities)){
            return null;
        }
        List<Long> skuIds = skuInfoEntities.stream().map(skuInfoEntity -> skuInfoEntity.getSkuId()).collect(Collectors.toList());
        return this.list(new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id", skuIds));
    }

}