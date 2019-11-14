package com.atguigu.gmall.ums.service.impl;

import java.util.Date;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    private MemberDao memberDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("mobile", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return false;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        //1.校验验证码

        //2.生成盐
        String salt = StringUtils.substring(UUID.randomUUID().toString(), 0, 6);
        memberEntity.setSalt(salt);
        //3.加盐加密
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()+salt));
        //4.注册功能
        memberEntity.setLevelId(1L);//等级
        memberEntity.setStatus(1);//状态
        memberEntity.setCreateTime(new Date());//注册时间
        memberEntity.setIntegration(0);//积分
        memberEntity.setGrowth(0);//成长值
        this.save(memberEntity);
        //5.删除redis中的验证码




    }

    //查询用户
 /*   @Override
    public MemberEntity queryUser(String userName, String password) throws IllegalAccessException {
        //判断用户名是否存在
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", userName));
        //根据用户名查询用户是不存在
        if (memberEntity == null) {
            throw new IllegalAccessException("用户名不合法");
        }
        //对用户输入的密码进行加密加盐
        DigestUtils.md5Hex(password+memberEntity.getSalt());
        if (!StringUtils.equals(password, memberEntity.getPassword())) {
            //手机开发中用户名和密码遗弃判断，提示用户名或密码不合法
            throw new IllegalAccessException("密码不合法");
        }
        return memberEntity;
    }*/
    @Override
    public MemberEntity queryUser(String userName, String password) {

        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", userName));
        // 如果根据用户名查询的用户不存在说明用户名不合法，抛出异常
        if (memberEntity == null) {
            throw new IllegalArgumentException("用户名不合法！");
        }

        // 对用户输入的密码进行加密
        password = DigestUtils.md5Hex(password + memberEntity.getSalt());

        if (!StringUtils.equals(password, memberEntity.getPassword())) {
            throw new IllegalArgumentException("密码不合法！");
        }

        return memberEntity;

    }
}