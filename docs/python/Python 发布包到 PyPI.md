# 发布 Python 包到 PyPI

- [发布 Python 包到 PyPI](#发布-python-包到-pypi)
  - [版本控制](#版本控制)
  - [将文件添加到包中](#将文件添加到包中)
  - [查找文件](#查找文件)
  - [发布到 PyPI](#发布到-pypi)
    - [构建前的准备](#构建前的准备)
    - [构建包](#构建包)
    - [测试包](#测试包)
  - [上传包](#上传包)
    - [使用 ApiToken](#使用-apitoken)
  - [参考](#参考)

## 版本控制

需要使用名为 Bumpversion 的工具，可以从 PyPI 中安装 Bumpversion：

```bash
pip install --upgrade bumpversion
```

```bash
bumpversion --current-version 1.0.0 minor setup.py pyprojname/__init__.py
```

使用配置文件：

新建 `.bumpversion.cfg` 文件，例如：

```conf
[bumpversion]
current_version = 1.0.0
commit = True
tag = True

[bumpversion:file:setup.py]

[bumpversion:file:pyprojname/__init__.py]
```

更新版本号，添加 `new_version` 配置：

```conf
[bumpversion]
current_version = 1.0.0
new_version = 1.0.1
commit = True
tag = True

[bumpversion:file:setup.py]

[bumpversion:file:pyprojname/__init__.py]
```

提交合并后，执行 `bumpversion part` 命令，会自动更改相关配置，且会新建一个 Tag

更多命令与配置请参阅 Bumpversion：

- [bumpversion](https://github.com/peritus/bumpversion)

在 Python 中，版本号的格式为：

- v1.0.0
- v1.0.0b1
- v1.0.0a1
- v1.0.0rc1

其中 `b` 表示 `beta` ，`a` 表示 `alpha`，`rc` 表示 `release`

## 将文件添加到包中

例如，需要将 `data.json` 文件作为包的一部分，则需要在 `setup()` 中指定此类文件，首先需要新建一个名为 `MANIFEST.in` 的清单文件，清单文件中指定需要包含的文件：

```conf
include myproj/data.json
```

有关更多的规则，请参阅文档：

- [manifest template commands](https://docs.python.org/distutils/commandref.html#creating-a-source-distribution-the-sdist-command)

接下来在 `setup()` 中指定复制这些非代码文件：

```python
setup(
    ...
    include_package_data=True,
    ...
)
```

## 查找文件

```python
from pkg_resources import resource_stream

with resource_stream("myproj", "data.json") as s:
    return json.load(s)
```

## 发布到 PyPI

要将包上传到 PyPI，需要使用名为 Twine 与 Wheel 的工具：

```bash
pip install --upgrade twine wheel
```

### 构建前的准备

执行以下命令对包进行检查：

```bash
python setup.py check
```

### 构建包

PyPI 上的包需要被包装成发行包，发行包最常见的格式是源代码档案（`.tar.gz`）与 Python wheels（`.whl`）。

执行以下命令构建发行包：

```bash
python setup.py sdist bdist_wheel
```

构建完成会在项目根目录创建 `dist` 目录，里面会包含 `.tar.gz` 和 `.whl` 文件。

### 测试包

本地测试包是否能成功安装：

```bash
pip install dist\pyprojname-1.0.0-py3-none-any.whl
```

检查包是否能在 PyPI 中正常呈现：

```bash
twine check dist/*
```

输出 `PASSED` 即表示通过。

## 上传包

先上传包到 [TestPyPI](https://test.pypi.org/) 确保一切都按照预期工作：

```bash
twine upload --repository-url https://test.pypi.org/legacy/ dist/*
```

确保一切正确后，执行以下命令上传包到 [PyPI](https://pypi.org/)：

```bash
twine upload dist/*
```

### 使用 ApiToken

如果你的账号开启了两步验证，使用账号密码发布时，可能会提示 `403 Forbidden` 错误，这时可以尝试使用 ApiToken 进行发布

在用户目录下新建 `.pypirc` 文件，内容如下：

```conf
[distutils]
  index-servers =
    pyprojname_release
    pyprojname_test

[pyprojname]
  repository = https://upload.pypi.org/legacy/
  username = __token__
  password = <your token>

[pyprojname_test]
  repository = https://test.pypi.org/legacy/
  username = __token__
  password = <your token>
```

然后执行以下命令上传包到 [PyPI](https://pypi.org/)：`

```bash
twine upload --repository pyprojname_release dist/*
```

## 参考

- [How to Publish an Open-Source Python Package to PyPI - Real Python](https://realpython.com/pypi-publish-python-package/)
- [怎样将Python项目发布到PyPI](https://zhuanlan.zhihu.com/p/37987613)
