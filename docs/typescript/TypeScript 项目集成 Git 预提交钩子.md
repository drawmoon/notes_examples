# TypeScript 项目集成 Git 预提交钩子

- [TypeScript 项目集成 Git 预提交钩子](#typescript-项目集成-git-预提交钩子)
  - [安装 CommitLint](#安装-commitlint)
  - [安装 Husky](#安装-husky)

> [commitlint](https://commitlint.js.org/) 可以帮助我们在提交代码至 Git 仓库时，校验提交信息是否严格按照[约定式提交](https://www.conventionalcommits.org/zh-hans)规范进行代码提交。

## 安装 CommitLint

```sh
npm install --save-dev @commitlint/config-conventional @commitlint/cli
```

在项目根目录增加 commitlint 的配置文件，新建 `commitlint.config.js` 文件并写入如下这段配置代码：
```javascript
module.exports = {
  extends: ['@commitlint/config-conventional']
};
```

或直接执行这行命令创建并写入 `commitlint.config.js` 文件：
```sh
echo "module.exports = { extends: ['@commitlint/config-conventional'] };" > commitlint.config.js
```

## 安装 Husky

使用 [husky](https://typicode.github.io/husky/) 在提交时检查提交信息是否符合规范
```sh
npm install --save-dev husky
```

配置 Husky，添加钩子：
```sh
cat <<EEE > .husky/commit-msg
#!/bin/sh
. "\$(dirname "\$0")/_/husky.sh"

npx --no -- commitlint --edit "\${1}"
EEE
```

在 `package.json` 添加脚本：
```json
{
  "scripts": {
    "prepare": "husky install"
  }
}
```

执行脚本激活钩子：
```sh
npm run prepare
```
