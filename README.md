
### 前端支撑
| 插件 | 版本   | 用途 |
|--- | ----- | ----- |
| node.js | ≥16 |  JavaScript运行环境 |

### 启动前端

```
npm install
```
```
npm run dev
```
### 后端支撑
| 插件 | 版本 | 用途 |
| --- | ----- |  ----- |
| jdk | 11 / 1.8 |java环境 |
| lombok | idea内 |代码简化插件 |
| maven | 最新版 |包管理工具 |
| redis | 最新版 | 缓存库 |
| mysql | 8.0 / 5.7 | 数据库 |

### 启动后端
开发工具内配置好maven并在代码中配置数据库，按顺序以此即可启动

|         应用         |       启动类        | 端口号  |
|:---------------:|:-------------------:|:----:|
|  cj-nacos-app   |  CjCloudNacosApp    | 8848 |
| cj-gateway-app  |  CjCloudGatewayApp  | 9003 |
| cj-actuator-app |  CjCloudActuatorApp | 9001 |
| cj-sentinel-app |  CjCloudSentinelApp | 9002 |
| cj-xxl-job-app  |  CjCloudXxlJobApp   | 9004 |
| cj-web-app      |  CjCloudWebApp      | 9101 |
| cj-biz-app      |  CjCloudBizApp      | 9102 |

## 代码结构

cj-cloud2.0框架对代码以插件化的模式进行分包，使得包层级结构更加清晰合理，同时降低了耦合度，关于插件模块化开发的规范请查阅文档【SNOWY-CLOUD开源文档——前端手册or后端手册——开发规范】板块。

```
cj-cloud
  |-cj-admin-web == 前端
    |-public == 基础静态文件
    |-src == 前端源代码
      |-api == API接口转发
      |-assets == 静态文件
      |-components == VUE组件
      |-config == 基础配置
      |-layout == 基础布局
      |-locales == 多语言配置
      |-router == 基础路由配置
      |-store == Pinia缓存配置
      |-style == 样式风格配置
      |-utils == 工具类
      |-views == 所有视图界面
  |-cj-base == 基础组件
    |-cj-common == 基础通用模块
  |-cj-modules == 应用组件
    |-cj-biz-app == 业务应用模块
    |-cj-web-app == 主应用模块
  |-cj-plugin == 插件组件
    |-cj-plugin-auth == 登录鉴权插件
        |-cj-plugin-auth-api == 登录鉴权插件api接口
        |-cj-plugin-auth-feign == 登录鉴权插件feign接口
        |-cj-plugin-auth-func == 登录鉴权插件func实现
    |-cj-plugin-biz == 业务功能插件
        |-cj-plugin-biz-api == 业务功能插件api接口
        |-cj-plugin-biz-func == 业务功能插件func实现
    |-cj-plugin-client == C端功能插件
        |-cj-plugin-client-api == C端功能插件api接口
        |-cj-plugin-client-func == C端功能插件func实现
    |-cj-plugin-dev == 开发工具插件
        |-cj-plugin-dev-api == 开发工具插件api接口
        |-cj-plugin-dev-feign == 开发工具插件feign接口
        |-cj-plugin-dev-func == 开发工具插件func实现
    |-cj-plugin-gen == 代码生成插件
        |-cj-plugin-gen-api == 代码生成插件api接口
        |-cj-plugin-gen-func == 代码生成插件func实现
    |-cj-plugin-sys == 系统功能插件
        |-cj-plugin-sys-api == 系统功能插件api接口
        |-cj-plugin-sys-feign == 系统功能插件feign接口
        |-cj-plugin-sys-func == 系统功能插件func实现
  |-cj-server == 服务组件
    |-cj-actuator-app == 监控服务模块
    |-cj-gateway-app == 网关服务模块
    |-cj-nacos-app == 注册中心/配置中心模块
    |-cj-sentinel-app == 限流服务模块
    |-cj-xxl-job-app == 分布式任务调度服务模块

```
