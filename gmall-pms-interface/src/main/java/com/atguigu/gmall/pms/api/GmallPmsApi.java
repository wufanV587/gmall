package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.ApiOperation;
import java.security.PublicKey;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {

    //分页查询（排序）

    @PostMapping("pms/spuinfo/list")
    public Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition queryCondition);

    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);

    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<SpuAttributeValueVO>> querySearchAttrValue(@PathVariable("spuId")Long spuId);

    @ApiOperation("查询分类信息")
    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategories(@RequestParam(value = "level", defaultValue = "0")Integer level, @RequestParam(value = "parentCid", required = false)Long parentCid);


    @ApiOperation("查询二级分类")
    @GetMapping("pms/category/{pid}")
    public Resp<List<CategoryVO>> quweyCategoryWithSub(@PathVariable("pid") Long pid);

    @ApiOperation("根据skuId查询spu信息")
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

    @ApiOperation("根据spuId查询spu信息")
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuInfo(@PathVariable("id") Long id);


    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<String>> queryPicsBySkuId(@PathVariable("skuId")Long skuId);


    @ApiOperation("详情查询")
    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrValues(@PathVariable("spuId")Long spuId);

    @ApiOperation("详情查询商品描述信息")
    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> querySpuInfoDesc(@PathVariable("spuId") Long spuId);

   /* @ApiOperation("查询属性分组")
    @GetMapping("pms/attrgroup/item/group/{cid}/{spuid}")
    public Resp<List<GroupVO>> queryGroupVOByCid(@PathVariable("cid")Long cid , @PathVariable("spuId")Long spuId);*/

    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
    public Resp<List<GroupVO>> queryGroupVOByCid(@PathVariable("cid")Long cid, @PathVariable("spuId")Long spuId);
}
