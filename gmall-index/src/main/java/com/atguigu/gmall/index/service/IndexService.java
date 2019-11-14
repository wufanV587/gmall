package com.atguigu.gmall.index.service;


import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.sun.org.apache.regexp.internal.RE;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPool;

@Service
public class IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

   /* @Autowired
    private JedisPool jedisPool;
*/
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final String KEY_PREFIX = "index:category";

    public List<CategoryEntity> queryLevel1Category() {

        Resp<List<CategoryEntity>> resp = this.gmallPmsClient.queryCategories(1, null);
        return resp.getData();
    }


    public List<CategoryVO> queryCategoryVO(Long pid) {
        //1.查询缓存，如果缓存中有则直接返回、
        String cache = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX+pid);
        if (StringUtils.isNotBlank(cache)){
            return JSON.parseArray(cache,CategoryVO.class);
        }
        //2.如果缓存中没有，查询数据库
        Resp<List<CategoryVO>> listResp = this.gmallPmsClient.quweyCategoryWithSub(pid);
        List<CategoryVO> categoryVOS = listResp.getData();
        //3.查询完成之后，放入缓存
        if (!CollectionUtils.isEmpty(categoryVOS)){
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryVOS));
        }
        return  categoryVOS;
    }
}
