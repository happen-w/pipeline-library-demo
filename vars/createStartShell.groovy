#!/usr/bin/env groovy

def call(String jar_name, String jvm_options, String logFile, String finishLabel) {

    return template = """
#!/bin/sh
export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# 要管理的进程名称
jar_name=${jar_name}
jvm_options=${jvm_options}
logFile=${logFile}
finishLabel=${finishLabel}
# 定义日志记录函数
log_message() {
    local message="\$1"
    echo "[\$(date '+%Y-%m-%d %H:%M:%S')] \$message"
}

# 查找进程PID
pid=\$(jps -l | grep "\$jar_name" | awk '{print \$1}')

if [ -z "\$pid" ]; then
    log_message "进程不存在，正在尝试启动..."
    nohup java \$jvm_options -jar "\$jar_name" >/dev/null 2>&1 &

    # 等待一会儿让进程启动
    sleep 5

    # 检查是否启动成功
    new_pid=\$(jps -l | grep "\$jar_name" | awk '{print \$1}')
    if [ -n "\$new_pid" ]; then
        log_message "启动成功 (PID: \$new_pid)"
    else
        log_message "启动失败，请检查日志文件 \${logFile}"
        exit 1
    fi
else
    log_message "找到运行中的进程 (PID: \$pid)，正在停止..."
    kill -9 "\$pid"

    sleep 2
    # 检查是否成功停止
    if ps -p "\$pid" > /dev/null; then
        log_message "无法停止进程 (PID: \$pid)"
        exit 1
    else
        log_message "进程已成功停止，正在重新启动..."
        nohup java \$jvm_options -jar "\$jar_name" >/dev/null 2>&1 &

        # 等待一会儿让进程启动
        sleep 5

        new_pid=\$(jps -l | grep "\$jar_name" | awk '{print \$1}')
        if [ -n "\$new_pid" ]; then
            log_message "重启成功 (PID: \$new_pid)"
        else
            log_message "重启失败，请检查日志文件 \${logFile}"
            exit 1
        fi
    fi
fi

timeout 100s bash -c "
    tail -f \${logFile} | while read line
     do
         echo \\"\\\$line\\"
         if [[ \\"\\\$line\\" == *\\"\${finishLabel}\\"* ]]; then
             echo \${finishLabel}退出循环
             exit 0
         fi
     done
"
exit 0
 """

}