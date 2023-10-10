# 基础通用开发组件
基础通用模块: cj-common

该模块用于被各插件的api引用，提供了通用分页请求、通用基础实体、通用结果、通用定时任务抽象接口等
```
<!-- 每个插件api都要引入common -->
<dependency>
<groupId>vip.xiaonuo</groupId>
<artifactId>snowy-common</artifactId>
<version>${project.parent.version}</version>
</dependency>
```
### 代码结构
```
cj-cloud
  |-cj-admin-web == 前端
  |-cj-base == 基础组件
    |-cj-common == 基础通用模块
        |-cj-common-annotation == 自定义注解
            |--自定义日志注解
            |--自定义节流防抖注解
            |--自定义包装注解
        |-cj-common-cache == 缓存
            | --通用Redis缓存操作器
        |-cj-common-consts == 常量
            | --应用启动信息常量
            | --权限资源静态常量
            | --Feign 静态常量
        |-cj-common-enums == 枚举
            | --通用删除标志枚举
            | --异常码枚举
            | --通用排序方式枚举
            | --系统内置的不可删除的标识枚举
            | --系统模块数据类型枚举
        |-cj-common-excel == Excel相关公用
            | --EasyExcel自定义合并策略
        |-cj-common-exception == 全局异常管理
            | --通用异常
        |-cj-common-handler == 操作处理
            | --Sm4Cbc加解密
        |-cj-common-listener == 侦听
            | --通用数据变化事件中心 事件发布器
            | --通用数据变化侦听器
        |-cj-common-page == 通用分页请求
            | --通用分页请求
        |-cj-common-pojo == 
        |-cj-common-prop == 配置
            | --通用基础配置
        |-cj-common-runner == 
        |-cj-common-sse == sse|指令集
            | --通用SSE参数
        |-cj-common-timer == 定时器相关
            | --定时器执行者
        |-cj-common-util == 工具类
            | --通用头像工具类
            | --文件下载工具类
            | --加密工具类
            | --通用邮件工具类
            | --过滤器异常工具类
            | --根据ip地址定位工具类
            | --Spring切面工具类
            | --通用获取当前网速工具类
            | --通用响应工具类 
            | --HttpServlet工具类
            | --时间格式化工具类
            | --用户代理工具类
  |-cj-modules == 应用组件
  |-cj-plugin == 插件组件
  |-cj-server == 服务组件


```

