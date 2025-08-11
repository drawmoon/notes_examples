# 开发环境 MacOS 篇

- [开发环境 MacOS 篇](#开发环境-macos-篇)
  - [Homebrew](#homebrew)
  - [终端](#终端)
    - [Fish](#fish)
  - [Python 开发环境](#python-开发环境)
    - [安装 uv 管理 Python 环境](#安装-uv-管理-python-环境)
    - [安装 Miniconda 管理 Python 环境](#安装-miniconda-管理-python-环境)
    - [Python 格式化与代码检查工具：Ruff](#python-格式化与代码检查工具ruff)
  - [Just](#just)

## Homebrew

```sh
export HOMEBREW_BREW_GIT_REMOTE="https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git"
export HOMEBREW_CORE_GIT_REMOTE="https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git"
export HOMEBREW_INSTALL_FROM_API=1

git clone --depth=1 https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/install.git brew-install

/bin/bash brew-install/install.sh

rm -rf brew-install
```

Apple Silicon CPU 需要将 Homebrew 路径加入到环境变量:

```sh
test -r ~/.bash_profile && echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.bash_profile
test -r ~/.zprofile && echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
```



## 终端

### Fish

```sh
brew install fish
```

将 Fish 添加到系统认可的 Shell 列表:

```sh
# 查看 Fish 的安装路径（例如：/opt/homebrew/bin/fish 或 /usr/local/bin/fish）
which fish

# 将路径添加到 /etc/shells
sudo sh -c 'echo /opt/homebrew/bin/fish >> /etc/shells'
```

设置 Fish 为默认 Shell:

```sh
chsh -s /opt/homebrew/bin/fish
```

其他自定义配置:

```sh
# ~/.config/fish/config.fish
source ~/.profile.fish


# ~/.profile.fish
source $HOME/.local/bin/env.fish


# ~/.local/bin/env.fish
if not contains "$HOME/.local/bin" $PATH
    # Prepending path in case a system-installed binary needs to be overridden
    set -x PATH "$HOME/.local/bin" $PATH
end
```



## Python 开发环境

### 安装 uv 管理 Python 环境

```sh
curl -LsSf https://astral.sh/uv/install.sh | sh
```

### 安装 Miniconda 管理 Python 环境

```sh
brew install --cask miniconda

# 初始化 Conda
conda init "$(basename "${SHELL}")"
```

### Python 格式化与代码检查工具：Ruff

```sh
uv tool install ruff@latest
```


## Just

```sh
brew install just
```



