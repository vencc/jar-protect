# jar-protect
jar包加壳加密工具

## 介绍
java 本身是开放性极强的语言,代码也容易被反编译,没有语言层面的一些常规保护机制,jar包很容易被反编译和破解。
受classfinal（已停止维护）设计启发,针对springboot日常项目开发,重新编写安全可靠的jar包加壳加密技术,用于保护软件版权。

### 使用说明
1. 使用jdk8编译,支持jdk8+版本
2. 目前支持springboot打包的jar文件（其他未测）
3. 目前支持class文件加密和yml,properties,xml等配置文件加密

### 加密设计
![加密](/doc/encode.jpg)
### 加密命令
jdk17 需要加--add-opens java.base/java.lang=ALL-UNNAMED
``` 
#fromJar 待加密的jar包的地址,支持相对路径
#excludeClass 排除(不加密)类文件,支持前后*进行模糊匹配
#includeJar 包含(需要加密)jar包,支持前后*进行模糊匹配
#includeConfig 包含(需要加密)文件,如.xml,.properties,.yml等配置文件,支持前后*进行模糊匹配
java -jar jar-project.jar --fromJar "c:\\tool\\a.jar" --excludeClass "*EurekaApplication*" --includeJar "lmc-*" --includeConfig "*.properties"
``` 
### 加密配置
文件名: jar-project.security.properties
``` 
#过期时间,为空则不限制过期时间(默认到9999-01-01)
expireTime=2023-07-01
#加密密码,为空则随机生成动态密码
password=
#加密解密文件地址(加密java代码源码),为空则使用自带des加密
myEncryptCodeFile=加密.java
#加密方写入的版权信息声明,为空则无
myVersionInfo=请正规渠道获得版本授权文件,严禁进行反编译修改或破解,一经发现会追溯法律责任！
```
加密.java模板
```
package com.free.bsf.jarprotect.core.encrypt;
import com.free.bsf.jarprotect.core.base.BsfException;
import com.free.bsf.jarprotect.core.base.Context;
import com.free.bsf.jarprotect.core.encrypt.IEncrypt;

/*MyEncrypt类名不能更改,注意引用相应的包(仅支持jdk自身的类库,不能使用第三方类库)*/
public class MyEncrypt implements IEncrypt {
    @Override
    public byte[] e(byte[] d) {
        try {
           //Context.Default.getPassword()
           /*加密逻辑代码*/
        }catch (Exception e){
            throw new BsfException(e);
        }
    }

    @Override
    public byte[] d(byte[] d) {
        try {
            //Context.Default.getPassword()
            /*解密逻辑代码*/
        }catch (Exception e){
            throw new BsfException(e);
        }
    }
}
```
* 自定义编码案例[Base64+DES](/doc/base64_DES.md)
* 自定义编码案例[RSA](/doc/RSA.md)
* 自定义编码案例[远程不透明加密](/doc/Remote.md)

### 解密设计
![解密](/doc/decode.jpg)

### 解密命令
jdk17 需要加--add-opens java.base/java.lang=ALL-UNNAMED
``` 
#【格式】java -javaagent:已加密.jar -jar 已加密.jar
java -javaagent:encrypt-lmc-demo-provider-1.0-SNAPSHOT.jar -jar encrypt-lmc-demo-provider-1.0-SNAPSHOT.jar
``` 

### 解密配置
一般为加密jar包后自动生成,文件名为{jar包名}.security.properties,解密jar需要配套此解密配置文件
```
#加密密码
password=
#解密秘钥代码
myDecryptCode=
```

### 未来扩展
1. 增加远程授权管理端

##### by [车江毅](https://gitee.com/chejiangyi)
