# AspNetCore 启用严格的编译检查

## 启用可为空的类型检查

设置所有类型都不可为 `null`，如果需要某个类型可以为 `null`，必须显式声明为可空类型，否则会编译警告。

在 `<PropertyGroup>` 下添加 `<Nullable>` 项目设置，`enable` 为启用，`disable` 为禁用。

```xml
<Project Sdk="Microsoft.NET.Sdk">
    <PropertyGroup>
        <Nullable>enable</Nullable>
    </PropertyGroup>
</Project>
```

启用可为空的类型检查后，所有的可空类型的声明必须添加 `?`:

```csharp
string notNull = "Hello";
string? nullable = null;
```

## 将警告视为错误

在项目编译中，将所有警告消息报告为错误。

在 `<PropertyGroup>` 下添加 `<TreatWarningsAsErrors>` 项目设置，`true` 为启用，`false` 为禁用。

```xml
<Project Sdk="Microsoft.NET.Sdk">
    <PropertyGroup>
        <TreatWarningsAsErrors>true</TreatWarningsAsErrors>
    </PropertyGroup>
</Project>
```
