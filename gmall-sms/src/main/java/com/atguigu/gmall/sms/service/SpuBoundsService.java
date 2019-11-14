package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SpuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import java.util.List;


/**
 * 商品spu积分设置
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:22:37
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

}

