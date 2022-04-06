English version of the README -> please [click here](./README-EN.md)

## 关于版本匹配说明
此Demo适用于CR系列的V3.5.2及以上控制器版本。

## DEMO说明文档

本demo为Java 编写的给Android手机或平板使用TCP方式控制Dobot协作机器人CR(以下简称CR)的DEMO

## DEMO系统要求

Android 4.3版本以上即可

## 工程文件说明

工程文件需要使用Android Studio打开，请使用4.0版本的Android Studio  
工程文件目录下分为四个模块分别为app、socket-client、socket-common-interface、socket-core。其中app为demo的主要模块，用于对CR的TCP通讯协议的封装以及实现部分简单的功能交互，如上使能、清除报警、急停、点动、MovJ和修改IO等功能。剩下的模块为实现TCP通讯功能的Socket客户端的底层实现。  
在app包内主要分为Client、Message和RobotState模块，分别对应了TCP客户端的实现，TCP通讯协议的封装和机器人状态的实时封装。  
* Client中实现了三个Client对应了CR三个端口和三种形式的消息协议。APIClient对应了29999端口，实现了一个一发一收的应答客户端。MoveClient对应了30003端口，只进行发送工作对应了CR的运动指令。StateClient对应了30004端口只进行接受机器人的状态协议。  
* Message则为CR的各种发送的消息封装，分为基础的BaseMessage和CRMessage.  
* RobotState则是对30004端口返回的数据进行了封装，用于生成各种CR的参数。
