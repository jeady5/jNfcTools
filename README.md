# NFC Tools
这是一个基于Android的NFC工具。

## 1. 开发环境
* Win10
* Android Studio Hedgehog  
* Kotlin
* Compose

## 2、测试环境
RedMi K30 Pro， Android 14  
由于现有卡片有限，故仅测试了`ACTION_TECH_DISCOVERED`

## 3. 功能描述
*以下多处用到了感兴趣的标签，在该App中默认对全部标签感兴趣*

* 系统调度  
当系统检测到NFC标签时，可以选择性启动该App感兴趣的标签进一步处理。

* 前台调度  
当读卡Activity处于前台时，默认具有优先处理感兴趣NFC标签的权限，不再弹出可处理的App选择框。

* 前台读取  
当开启了读写器模式，标签信息直接通过回调函数返回

* 读卡  
目前仅支持读tag id和tech list。

* 其他  
敬请期待