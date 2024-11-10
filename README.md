由于本人并不精通java，编程能力也不是很强，本插件停止更新，可以支持[鹊桥](https://github.com/17TheWord/QueQiao)，使用Websocket协议进行互通，比本插件更完善且安全
# chatSocketServer
一个用简单socket通信实现聊天发射器的mc插件  
版本支持  
**Bukkit**  
`paper`&&`spigot`: 1.16~1.20  (据说1.21也能用，没试过)

## 安装
* 1、在[release]中下载最新版本的jar文件
* 2、把下载好的.jar文件丢到服务器plugin目录

## 配置文件
/plugin/{插件名}/config.yml  
配置说明:  
* host  -->  字符串,socket服务器的地址,默认`"0.0.0.0"`
* port  -->    整数,socket服务器的端口,默认`21354`
* token -->  字符串,socket服务器的token,默认`"Token12345"`
* CMDprefix --> 字符串,控制台输出socket接收的数据的前缀,默认`"[socketReceived] >> "`
* CHATprefix --> 字符串,聊天窗口输出的前缀,默认`§6[Socket消息]§r`

## 功能
### v1.1.0
新增三个配置项
* `token` 默认为 `Token12345`，防止端口暴露有人搞事情
* `CMDprefix` 控制台输出socket接收的数据的前缀可以在配置文件修改了
* `CHATprefix` 聊天窗口输出的前缀，之前是§6[Socket消息]§r在聊天栏中广播消息的前缀

### v1.0.1
* 加入papi, 请各位使用v1.0.1的用户在服务器添加`PlaceholderAPI`前置插件
* 使用命令`/papi ecloud download Server`与`/papi ecloud download Player`补全papi变量
* 当socket接收到tps(TPS)返回服务器的tps
* 当socket接收到服务器信息(server_info)返回服务器一些基本信息

### v1.0.0
* 接收玩家进入、退出服务器消息
* 接收玩家聊天消息
* 接收玩家死亡消息

## 写在最后
第一次写java还有很多不足，大佬轻喷。  
本人不打算长期维护该插件，毕竟我不是主修java的

# CHANGELOG
## v1.1.0
### 新增
新增了三个配置
* token 默认为 `Token12345`，防止端口暴露有人搞事情
* `CMDprefix` 控制台输出socket接收的数据的前缀可以在配置文件修改了
* `CHATprefix` 聊天窗口输出的前缀，之前是§6[Socket消息]§r在聊天栏中广播消息的前缀
### 已知但是还没有解决的bug
* plugman无法重启此插件
* 指令也无法重启此插件
### 问题分析
* 暂时没有找到逻辑上的错误，可能是api更新了，如果有大佬麻烦给个思路谢谢
