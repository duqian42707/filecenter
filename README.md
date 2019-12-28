# 文件中心

统一文件管理中心，支持MongoDB、HDFS、磁盘、FTP等多种存储方式。
对外提供上传下载接口，调用者无需知道文件保存在什么位置。

## Get Start

修改配置文件

`application.properties`
```properties
# 修改数据源（选用mysql之外的数据库需要自己添加jdbc驱动包）
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/filecenter?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=111111
# 修改数据库方言
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect

# 修改默认的存储方式：mongo|hdfs|disk|ftp 选其一
filecenter.defaultStoreType=mongo
# 修改需要使用的存储方式：mongo|hdfs|disk|ftp 可选多个,用逗号隔开
spring.profiles.active=mongo,disk,ftp
```

激活了哪个存储方式,就去修改对应的配置文件。如激活了mongo：

`application-mongo.properties`
```properties
filecenter.mongo.enable=true
# 配置MongoDB的连接方式
filecenter.mongo.host=localhost
filecenter.mongo.port=27017
filecenter.mongo.database=filecenter
```


启动程序
```bash
java -jar filecenter.jar
```
访问页面 http://localhost:8080

