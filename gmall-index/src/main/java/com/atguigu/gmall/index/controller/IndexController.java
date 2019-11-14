package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import java.util.List;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryLevel1Category(){
        List<CategoryEntity> categoryEntities = this.indexService.queryLevel1Category();
        return Resp.ok(categoryEntities);
    }

    @GetMapping("cates/{pid}")
    public Resp<List<CategoryVO>> queryCategoryVO(@PathVariable("pid")Long pid){
        List<CategoryVO> categoryVOS= this.indexService.queryCategoryVO(pid);
        return Resp.ok(categoryVOS);
    }


}
