package com.atguigu.gmall.gateway.config;


import com.atguigu.core.utils.RsaUtils;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
/*
* auth:
  jwt:
    publicKeyPath: D:\\project-0615\\tem\\rsa.pub
    privateKeyPath: D:\\project-0615\\tem\\rsa.pri
    expire: 180 #单位是分钟
    cookieName: GMALL_TOKEN
* */

    private String publicKeyPath;
    private String cookieName;

    private PublicKey publicKey;

    @PostConstruct
    public void init(){

        try {
            //获取公钥
            RsaUtils.getPrivateKey(publicKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败");
            e.printStackTrace();
        }
    }
}
