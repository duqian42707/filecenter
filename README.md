

## 安装运行mysql
```bash
docker pull mysql:5.7
docker run -d -p 3306:3306 --name mysql \
  -v /data/mysql/datadir:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=111111 \
  mysql:5.7
```

## 安装运行MongoDB

```bash
docker pull mongo
docker run -d -p 27017:27017 --name mongo mongo
```


## 安装运行hadoop

```bash
```


## 安装运行ftp

```bash
```


## Get Start

启动程序
```bash
java -jar filecenter.jar
```
访问页面 http://localhost:8080

