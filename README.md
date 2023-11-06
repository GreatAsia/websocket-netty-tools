# websocket-netty-tools
#模拟建立websocket连接，压测使用
#1 netty客户端文件(带header)
命令：  nohup java -jar ultrasnake-client-header.jar  --host 10.10.10.1 --port 8080 --file 6wc.cvs --ws ws &
--host: ip/域名
--port: 8080/443/80
--file: 文件
--ws: ws/wss

#2 netty客户端文件(不带header)
命令：java -jar ultrasnake-client.jar --host 10.10.10.1 --port 8000 --size 60000
--host: ip/域名
--port: 8080/80
--size: 连接数