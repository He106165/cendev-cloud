## 平台简介
### 平台架构
![Image text](http://git.censoft.com.cn/cendev-cloud/cendev-cloud/raw/master/doc/censoft-cloud.png)
### 文档 http://doc.censoft.com.cn


#### 部署准备：

- JDK >= 1.8(推荐1.8版本)
- MySQL >= 5.5.0 (推荐5.7版本)
- Maven >= 3.0
- lombok 插件

1. 安装mysql并初始化
`mysql5.6`以下存在`datetime(0)`无法导入的问题，可能需要自行替换`(0)->''`

    创建数据库`cendev_cloud`，并导入`sql\cendev_cloud.sql`,

2. 安装redis并设置端口和密码

3. 修改`cendev-config/src/main/resources/config`中`mysql`和`redis`配置

4. 依次绑定host：
```bash
127.0.0.1 eureka.cloud.xg.com
127.0.0.1 gateway.cloud.xg.com
```
    如果要使用eureka集群，请依次绑定eureka7002.com,eureka7003.com后修改各项目中的注释部分
    也可以随意更换本地host，但需要修改相应的配置文件
    
#### 工程结构说明：
```
cendev-cloud
|
├──cendev-common --通用包
|  |
|  ├──cendev-common-core --核心工具包
|  |
|  ├──cendev-common-redis --redis工具包
|  |
|  ├──cendev-common-log --日志工具包
|  |
|  ├──cendev-common-auth --权限工具包
|
├──cendev-config --cloud统一配置中心
|
├──cendev-eureka --注册中心
|
├──cendev-gateway --网关
|
├──cendev-service-api --服务api模块
|  |
|  ├──cendev-system-api --系统业务api
|
├──cendev-service --微服务
|  |
|  ├──cendev-system --系统业务
|  |
|  ├──cendev-auth --授权中心
|  |
|  ├──cendev-gen --代码生成
|  |
|  ├──cendev-dfs --文件
|
├──cendev-tool --工具
|  |
|  ├──cendev-monitor --监控中心
|
├──cendev-ant --前端 使用ant design框架

```



####启动顺序：
- eureka
- config
- gateway
- system
- auth
- gen 可选
- dfs 可选
- monitor 可选


"# cendev-cloud" 
