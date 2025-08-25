using HttpApi.Core;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHttpContextAccessor();

// ��� GraphQL
builder.Services.AddGraphQLService();

// TODO: �޸� Read ��ʽ
builder.Services.Configure<Microsoft.AspNetCore.Server.Kestrel.Core.KestrelServerOptions>(options =>
{
    options.AllowSynchronousIO = true;
});

var app = builder.Build();

// ���� GraphQL
app.UseGraphQLService();

app.Run();