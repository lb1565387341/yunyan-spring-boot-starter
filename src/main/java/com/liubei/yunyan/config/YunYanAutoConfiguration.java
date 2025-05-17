package com.liubei.yunyan.config;

import com.liubei.yunyan.service.auth.DefaultYunYanAuthManager;
import com.liubei.yunyan.service.auth.YunYanAuthManager;
import com.liubei.yunyan.service.client.YunYanApiClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 性能优化
 * 传统行为：proxyBeanMethods = true
 * Spring 默认使用 CGLIB 代理来拦截 @Bean 方法，确保调用的是容器中的单例 Bean。
 * 这种代理带来了额外的运行时开销，尤其是在启动时，可能会对复杂配置类生成代理类。
 * 优化行为：proxyBeanMethods = false
 * 当配置类中各个 @Bean 方法之间没有直接依赖关系时，CGLIB 代理的拦截行为是多余的。
 * 使用 proxyBeanMethods = false，可以直接调用方法，跳过代理逻辑，减少启动时间。
 * 对于现代框架来说，启动性能的提升非常关键，尤其是在微服务和容器化环境中，启动速度往往直接影响部署和扩展效率。
 * @author liubei
 */
@AutoConfiguration
@EnableConfigurationProperties(YunYanProperties.class)
public class YunYanAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "yun-yan", value = "enabled", havingValue = "true")
    public YunYanAuthManager yunYanAuthManager(YunYanProperties yunYanProperties) {
        return new DefaultYunYanAuthManager(yunYanProperties);
    }

    @Bean
    @ConditionalOnBean(YunYanAuthManager.class)
    public YunYanApiClient yunYanApiClient(YunYanAuthManager yunYanAuthManager) {
        return new YunYanApiClient(yunYanAuthManager);
    }

}
