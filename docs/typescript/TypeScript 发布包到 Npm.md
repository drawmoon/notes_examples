# TypeScript 发布包到 Npm

- [TypeScript 发布包到 Npm](#typescript-发布包到-npm)
  - [包含的文件](#包含的文件)
  - [版本](#版本)
  - [Standard Version](#standard-version)
  - [Tag](#tag)

## 包含的文件

通过 `package.json` 里面的 `files` 字段进行控制，例如下面的示例中指定为 `dist`，则只会包含 `dist` 文件夹：

```json
{
    "files": [
        "dist"
    ]
}
```

部分文件不会被排除，包括并不限于：

- package.json
- package.json 里面的 `main` 字段指定的文件
- README.md
- LICENSE

## 版本

通过 `npm version <arguments>` 自动修改版本号，例如 `package.json` 的版本号为 `1.0.0`，分别执行 `version` 命令可以得到如下结果：

```json
// package.json

{
    "version": "1.0.0"
}
```

```sh
> npm version 1.0.0
v1.0.0

> npm version patch
v1.0.1

> npm version minor
v1.1.0

> npm version major
v2.0.0

> npm version prepatch
v1.0.1-0

> npm version prerelease
v1.0.1-0

> npm version prerelease --preid=beta
v1.0.1-beta.0

> npm version prerelease --preid=alpha
v1.0.1-alpha.0
```

## Standard Version

[standard-version](https://github.com/conventional-changelog/standard-version) 可以帮助我们自动生成更改日志，并且自动的根据更改范围调整版本号，意味着我们无需通过 `npm version` 手动去调整版本号，前提条件是我们必须严格遵守约定式提交规范进行代码提交。

安装 `standard-version`：

```sh
npm install --save-dev standard-version
```

在 `package.json` 添加脚本：

```json
{
  "scripts": {
    "release": "standard-version"
  }
}
```

执行发版：

```sh
npm run release
```

我们也可以通过 `-p` 或 `--prerelease` 指定预发布版本号：

```sh
npm run release --prerelease beta
```

当然我们也可以添加 Git 钩子，在提交代码的时候验证提交信息是否遵守约定式提交规范：[预提交钩子 - Commit Lint](./2-预提交钩子.md)

## Tag

- latest
- beta
- next

当我们通过 `npm install` 时，默认安装的是 `latest` 版本，如果希望安装 `beta` 版本，则必须明确指定版本号进行安装。

下面示例通过 `--tag beta` 发布一个测试版：

```sh
npm publish --tag beta
```
