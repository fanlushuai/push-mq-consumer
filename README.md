# push-mq-consumer

- 1、spring rabbitmq的架子
  - aop的形式打印队列收到的日志。发现很多都是队列里面自己手动打印一下收到了xxxx。
  - 添加的traceId，能够通过traceId追踪到每一次处理的所有日志信息。
  - 个人本地消费者和测试公用环境隔离。通过maven的git插件读取本地的git信息，当spring环境变量是dev的时候，使用git email作为vhost的值，实现隔离。
- 2、接入了app推送。安卓推送采用信鸽服务。ios推送自己实现推送apns服务。


试试这个吧。https://github.com/signalapp/
