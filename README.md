# dubbo-simple

#### 介绍

简单的Dubbo项目，实现各种微服务特性

#### 软件架构

1. Dubbo实现RPC通信
2. 使用Nacos实现配置中心和注册中心
3. 使用Skyworking实现全链路追踪
4. 使用Sentinel实现流量控制、熔断降级、系统负载保护

#### 组件版本

- spring-boot-2.7.10
- dubbo-3.3.0-beta.1
- [OpenJDK21U-jdk_x64_windows_hotspot_21.0.4_7](https://adoptium.net/zh-CN/)
- [nacos-server-2.4.0-BETA](https://download.nacos.io/nacos-server/nacos-server-2.4.0.1.zip)
- [apache-skywalking-apm-10.0.1](https://dlcdn.apache.org/skywalking/10.0.1/apache-skywalking-apm-10.0.1.tar.gz)
- [apache-skywalking-java-agent-9.2.0](https://archive.apache.org/dist/skywalking/java-agent/9.2.0/apache-skywalking-java-agent-9.2.0.tgz)
- [sentinel-dashboard-1.8.8](https://github.com/alibaba/Sentinel/releases/download/1.8.8/sentinel-dashboard-1.8.8.jar)
- [apache-jmeter-5.6.3](https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.zip)

#### 创建Dubbo项目

1. 创建父模块

   添加SpringBoot和Dubbo的依赖版本控制

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-dependencies</artifactId>
       <version>${spring-boot.version}</version>
       <type>pom</type>
       <scope>import</scope>
   </dependency>
   <dependency>
       <groupId>org.apache.dubbo</groupId>
       <artifactId>dubbo-bom</artifactId>
       <version>${dubbo.version}</version>
       <type>pom</type>
       <scope>import</scope>
   </dependency>
   ```

2. 创建子模块

   添加Dubbo的Spring Boot启动依赖

   ```xml
   <dependency>
       <groupId>org.apache.dubbo</groupId>
       <artifactId>dubbo-spring-boot-starter</artifactId>
   </dependency>
   ```

   启动类增加注解@EnableDubbo

   ```java
   @EnableDubbo
   @SpringBootApplication
   public class BaseApplication {
       public static void main(String[] args) {
           SpringApplication.run(BaseApplication.class, args);
       }
   }
   ```

3. 在application.yml文件中配置Dubbo应用名称

   ```yaml
   dubbo:
     application:
       name: BaseApplication
   ```

#### 使用nacos实现配置中心

1. 增加配置中心依赖

   ```xml
   <dependency>
       <groupId>org.apache.dubbo</groupId>
       <artifactId>dubbo-configcenter-nacos</artifactId>
   </dependency>
   ```

2. 在application.yml文件中增加配置中心信息

   ```yaml
   dubbo:
     config-center:
       namespace: dev
       address: nacos://${nacos.address:127.0.0.1}:8848
   ```

3. 启动nacos，增加配置dubbo.properties

   ![增加配置文件dubbo.properties](https://foruda.gitee.com/images/1722265165523133701/0d3cc777_5363674.png "image-20240729214259353.png")


   ![配置文件dubbo.properties](https://foruda.gitee.com/images/1722265220671748450/a1b4d3c3_5363674.png "image-20240729214511330.png")

#### 使用nacos实现注册中心

1. 增加注册中心依赖

   ```xml
   <dependency>
       <groupId>org.apache.dubbo</groupId>
       <artifactId>dubbo-registry-nacos</artifactId>
   </dependency>
   ```

2. nacos中dubbo.properties配置增加注册中心配置

   ![增加注册中心配置](https://foruda.gitee.com/images/1722265367804094210/771fb5b0_5363674.png "image-20240729214815870.png")

#### 实现简单RPC通信

1. 创建NoticeService作为Provider，使用注解@DubboService标识服务

   ```java
   package com.wh.dubbo.base.service;
   
   import com.wh.dubbo.api.base.NoticeService;
   import org.apache.dubbo.config.annotation.DubboService;
   
   @DubboService
   public class NoticeServiceImpl implements NoticeService {
   
       @Override
       public boolean sendNotice(String notice) {
           try {
               System.out.println("发送通知:" + notice);
           } catch (Exception e) {
               e.printStackTrace();
           }
           return false;
       }
   }
   ```

2. 创建MgService作为Consumer，使用注解@DubboReference注入远端服务代理

   ```java
   package com.wh.dubbo.mg.service;
   
   import com.wh.dubbo.api.base.NoticeService;
   import com.wh.dubbo.api.mg.MgService;
   import org.apache.dubbo.config.annotation.DubboReference;
   import org.apache.dubbo.config.annotation.DubboService;
   
   import java.util.ArrayList;
   import java.util.List;
   import java.util.Map;
   
   @DubboService
   public class MgServiceImpl implements MgService {
       
       @DubboReference
       private NoticeService noticeService;
   
       @Override
       public List<Map<String, Object>> getDrugList() {
           noticeService.sendNotice("开始获取药品列表。");
           List<Map<String, Object>> drugList = new ArrayList<>();
           noticeService.sendNotice("药品列表组装完成");
           return drugList;
   
       }
   }
   ```

#### 配置负载均衡策略

| 算法                          | 特性                    | 备注                                                 | 配置值           |
| :---------------------------- | :---------------------- | :--------------------------------------------------- | :--------------- |
| Weighted Random LoadBalance   | 加权随机                | 默认算法，默认权重相同                               | random (默认)    |
| RoundRobin LoadBalance        | 加权轮询                | 借鉴于 Nginx 的平滑加权轮询算法，默认权重相同，      | roundrobin       |
| LeastActive LoadBalance       | 最少活跃优先 + 加权随机 | 背后是能者多劳的思想                                 | leastactive      |
| Shortest-Response LoadBalance | 最短响应优先 + 加权随机 | 更加关注响应速度                                     | shortestresponse |
| ConsistentHash LoadBalance    | 一致性哈希              | 确定的入参，确定的提供者，适用于有状态请求           | consistenthash   |
| P2C LoadBalance               | Power of Two Choice     | 随机选择两个节点后，继续选择“连接数”较小的那个节点。 | p2c              |
| Adaptive LoadBalance          | 自适应负载均衡          | 在 P2C 算法基础上，选择二者中 load 最小的那个节点    | adaptive         |

1. 配置NoticeService的负载均衡策略为：最短响应优先 + 加权随机（shortestresponse）

   ```java
   @DubboService(loadbalance = "shortestresponse")
   public class NoticeServiceImpl implements NoticeService
   ```

2. 配置MgService的负载均衡策略为：最少活跃优先 + 加权随机（leastactive）

   ```java
   @DubboService(loadbalance = "leastactive")
   public class MgServiceImpl implements MgService
   ```

#### 实现Skyworking全链路追踪

1. 增加版本控制

   ```xml
   <dependency>
       <groupId>org.apache.skywalking</groupId>
       <artifactId>apm-toolkit-micrometer-1.10</artifactId>
       <version>8.14.0</version>
   </dependency>
   ```

2. 增加Skyworking依赖

   ```xml
   <dependency>
       <groupId>org.apache.dubbo</groupId>
       <artifactId>dubbo-metrics-api</artifactId>
   </dependency>
   <dependency>
       <groupId>org.apache.skywalking</groupId>
       <artifactId>apm-toolkit-micrometer-1.10</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

3. 增加配置类

   ```java
   package com.wh.dubbo.mg.config;
   
   import io.micrometer.observation.ObservationHandler;
   import io.micrometer.observation.ObservationRegistry;
   import org.apache.dubbo.rpc.model.ApplicationModel;
   import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingDefaultTracingHandler;
   import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingReceiverTracingHandler;
   import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingSenderTracingHandler;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   public class ObservationConfiguration {
       @ConditionalOnBean(ObservationRegistry.class)
       @Bean
       ApplicationModel applicationModel(@Autowired ObservationRegistry observationRegistry) {
           ApplicationModel applicationModel = ApplicationModel.defaultModel();
           observationRegistry.observationConfig()
                   .observationHandler(new ObservationHandler.FirstMatchingCompositeObservationHandler(
                           new SkywalkingSenderTracingHandler(), new SkywalkingReceiverTracingHandler(),
                           new SkywalkingDefaultTracingHandler()
                   ));
           applicationModel.getBeanFactory().registerBean(observationRegistry);
           return applicationModel;
       }
   }
   ```

4. 配置启动时虚拟机参数

   -javaagent:代理jar包路径
   
   -DSW_AGENT_NAME=代理名称，也就是当前App的名称
   
   ```
   -javaagent:D:\wuhao\dubbo-module\apache-skywalking-java-agent-9.2.0\skywalking-agent\skywalking-agent.jar
   -DSW_AGENT_NAME=base
   ```

#### 使用Sentinel实现流量控制、熔断降级、系统负载保护

1. 增加依赖版本控制

   ```xml
   <dependency>
       <groupId>com.alibaba.csp</groupId>
       <artifactId>sentinel-apache-dubbo-adapter</artifactId>
       <version>1.8.8</version>
   </dependency>
   <dependency>
       <groupId>com.alibaba.csp</groupId>
       <artifactId>sentinel-transport-simple-http</artifactId>
       <version>1.8.8</version>
   </dependency>
   ```

2. 增加sentinel相关依赖

   ```xml
   <dependency>
       <groupId>com.alibaba.csp</groupId>
       <artifactId>sentinel-apache-dubbo-adapter</artifactId>
   </dependency>
   <dependency>
       <groupId>com.alibaba.csp</groupId>
       <artifactId>sentinel-transport-simple-http</artifactId>
   </dependency>
   ```

3. 在资源目录下增加sentinel配置文件 sentinel.properties

   ```properties
   project.name=mg
   csp.sentinel.dashboard.server=127.0.0.1:8080
   csp.sentinel.api.port=8722
   ```

4. 配置接口的流控规则和熔断规则

   ![流控规则](https://foruda.gitee.com/images/1722265407272715576/02804b7e_5363674.png "image-20240729222631504.png")

   ![熔断规则](https://foruda.gitee.com/images/1722265442402737848/5919faae_5363674.png "image-20240729222752877.png")

#### 使用 jmeter进行压力测试

1. 配置TestPlan

   ![线程池配置](https://foruda.gitee.com/images/1722265475376919957/d5f06a78_5363674.png "image-20240729225155105.png")

   ![http请求配置](https://foruda.gitee.com/images/1722265501625435500/c33fcbb0_5363674.png "image-20240729225201792.png")

2. Skyworking和Sentinel展示

   ![Sentinel实时监控](https://foruda.gitee.com/images/1722265533772857688/0ac39052_5363674.png "image-20240729222909873.png")
   
   ![Skyworking服务监控](https://foruda.gitee.com/images/1722265601780091254/cc0cab99_5363674.png "image-20240729222135904.png")

   ![Skyworking服务拓扑图](https://foruda.gitee.com/images/1722265683994898627/92c16e30_5363674.png "image-20240729222146506.png")

   ![Skyworking链路追踪](https://foruda.gitee.com/images/1722265744776476970/85663349_5363674.png "image-20240729222203179.png")
