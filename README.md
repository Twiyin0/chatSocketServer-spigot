# chatSocketServer
一个用简单socket通信实现聊天发射器的mc插件  
版本支持  
**Bukkit**
`paper`&&`spigot`: 1.16~1.20

## 安装
* 1、在[release]中下载最新版本的jar文件
* 2、把下载好的.jar文件丢到服务器plugin目录

## 配置文件
/plugin/{插件名}/config.yml  
配置说明:  
* host  -->  字符串,socket服务器的地址,默认`"0.0.0.0"`
* port  -->    整数,socket服务器的端口,默认`21354`

## 功能
### v1.0.0
* 接收玩家进入、退出服务器消息
* 接收玩家聊天消息
* 接收玩家死亡消息

### v1.0.1
* 加入papi, 请各位使用v1.0.1的用户在服务器添加`PlaceholderAPI`前置插件
* 使用命令`/papi ecloud download Server`与`/papi ecloud download Player`补全papi变量
* 当socket接收到tps(TPS)返回服务器的tps
* 当socket接收到服务器信息(server_info)返回服务器一些基本信息

## 写在最后
第一次写java还有很多不足，大佬轻喷。  
本人不打算长期维护该插件，毕竟我不是主修java的
