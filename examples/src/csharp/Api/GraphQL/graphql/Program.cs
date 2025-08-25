using HttpApi;
using HttpApi.Core;
using HttpApi.Entities;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Configuration.AddJsonFile("sample-data.json");

// ��Ӳ������ݿ�
var databaseName = Guid.NewGuid().ToString();
builder.Services
    .AddEntityFrameworkInMemoryDatabase()
    .AddDbContext<AppDbContext>((sp, options) => options.UseInMemoryDatabase(databaseName).UseInternalServiceProvider(sp));

builder.Services.AddHttpContextAccessor();

// ��� GraphQL
builder.Services.AddGraphQLService();

// TODO: �޸� Read ��ʽ
builder.Services.Configure<Microsoft.AspNetCore.Server.Kestrel.Core.KestrelServerOptions>(options =>
{
    options.AllowSynchronousIO = true;
});

var app = builder.Build();

// ��ʼ��ʾ������
using (var scope = app.Services.CreateScope())
{
    await DataGenerator.InitSampleData(scope.ServiceProvider, app.Configuration.GetSection("SampleData").Get<List<Order>>());
}

// ���� GraphQL
app.UseGraphQLService();

app.Run();