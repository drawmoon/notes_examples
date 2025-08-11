# 开发环境 Linux 篇

- [开发环境 Linux 篇](#开发环境-linux-篇)
  - [终端](#终端)
    - [Fish](#fish)
      - [Fish 配置在终端不显示用户名](#fish-配置在终端不显示用户名)
  - [Node.js](#nodejs)
    - [在 Fish 终端中使用 nvm](#在-fish-终端中使用-nvm)
  - [just](#just)
  - [Python 开发环境](#python-开发环境)
    - [安装 Miniconda 管理 Python 环境](#安装-miniconda-管理-python-环境)
  - [Java 开发环境](#java-开发环境)
  - [安装 Gradle 构建 Java 项目](#安装-gradle-构建-java-项目)
  - [WSL](#wsl)
    - [Use Windows proxied connections](#use-windows-proxied-connections)

## 终端

### Fish

```sh
sudo add-apt-repository ppa:fish-shell/release-4
sudo apt update
sudo apt install fish
```

将 Fish 添加到系统认可的 Shell 列表:

```sh
# 查看 Fish 的安装路径（通常为 /usr/bin/fish）
which fish

# 将路径添加到 /etc/shells
echo /usr/bin/fish | sudo tee -a /etc/shells
```

设置 Fish 为默认 Shell:

```sh
chsh -s /usr/bin/fish
```

[fisher - Fish 插件管理器](https://github.com/jorgebucaran/fisher):

```sh
curl -sL https://git.io/fisher | source && fisher install jorgebucaran/fisher
```

#### Fish 配置在终端不显示用户名

```sh
vim ~/.config/fish/functions/fish_prompt.fish

function fish_prompt
    set_color green
    echo -n (prompt_pwd)
    set_color normal
    echo -n ' > '
end
```

## Node.js

### 在 Fish 终端中使用 nvm

```sh
fisher install jorgebucaran/nvm.fish
```



## just

```sh
curl -LSfs https://just.systems/install.sh | sh -s -- --to /usr/local/bin
```

## Python 开发环境

### 安装 Miniconda 管理 Python 环境

```sh
wget -c https://mirrors.tuna.tsinghua.edu.cn/anaconda/miniconda/Miniconda3-latest-Linux-x86_64.sh

chmod +x Miniconda3-latest-Linux-x86_64.sh

./Miniconda3-latest-Linux-x86_64.sh
```

初始化 Conda

```sh
conda init "$(basename "${SHELL}")"

# conda init bash
# conda init fish
```




## Java 开发环境

```sh
# install OpenJDK
sudo apt install openjdk-8-jdk


# ~/.config/fish/config.fish
export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
```

## 安装 Gradle 构建 Java 项目

```sh
# install Gradle
wget https://mirrors.cloud.tencent.com/gradle/gradle-8.9-bin.zip
sudo unzip -d /opt/gradle ./gradle-8.9-bin.zip

# ~/.config/fish/config.fish
export GRADLE_HOME="/opt/gradle/gradle-8.9"
set -x PATH "$GRADLE_HOME/bin" $PATH
```





## WSL

### Use Windows proxied connections

```sh
# ~/.config/fish/config.fish

set windowsip (grep nameserver < /etc/resolv.conf | awk '{print $2}')
set selfip (ip route | grep default | awk '{print $3}')

export HTTP_PROXY="http://$selfip:7897"
export HTTPS_PROXY="http://$selfip:7897"
export ALL_PROXY="http://$selfip:7897"
```
