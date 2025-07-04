#!/bin/bash

# 获取当前时间
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# 定义日志函数，使用 logger 写入系统日志
log_to_syslog() {
    logger -t NetworkStatus "[Network Status Check at $TIMESTAMP] $1"
}

# 收集 NetworkManager 状态
NM_STATUS=$(systemctl status NetworkManager)
log_to_syslog "NetworkManager Status:\n$NM_STATUS"

# 收集 route -n 信息
ROUTE_INFO=$(route -n)
log_to_syslog "Routing Table:\n$ROUTE_INFO"

# 收集 iptables 服务状态
IPTABLES_STATUS=$(systemctl status iptables)
log_to_syslog "iptables Service Status:\n$IPTABLES_STATUS"

# 收集 iptables NAT 表
IPTABLES_NAT=$(iptables -t nat -L -v -n)
log_to_syslog "iptables NAT Table:\n$IPTABLES_NAT"

# 收集网卡及 IP 状态
IP_STATUS=$(ip addr show)
log_to_syslog "Network Interfaces and IP Status:\n$IP_STATUS"

