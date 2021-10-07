package com.censoft.dfs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * <p>File：DfsConfig.java</p>
 * <p>Title: 分布式文件配置</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2019 2019年8月29日 下午5:27:02</p>
 * <p>Company: yinrunnet.com </p>
 * @author censoft
 * @version 1.0
 */
@Data
@Component
public class DfsConfig
{
    /** 路径*/
    @Value("${dfs.path}")
    private String path;

    //生产环境建议用nginx绑定域名映射
    /** 域名*/
    @Value("${dfs.domain}")
    private String domain;
}
