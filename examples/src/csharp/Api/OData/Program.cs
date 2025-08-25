using Microsoft.AspNetCore.OData;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using OData;
using OData.Models;
using Swashbuckle.AspNetCore.SwaggerGen;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// ��� in-memory ���ݿ�
var databaseName = Guid.NewGuid().ToString();
builder.Services
    .AddEntityFrameworkInMemoryDatabase()
    .AddDbContext<AppDbContext>((sp, options) => options.UseInMemoryDatabase(databaseName).UseInternalServiceProvider(sp));

// Swagger �������
builder.Services.AddTransient<IConfigureOptions<SwaggerGenOptions>, ConfigureSwaggerOptions>();
builder.Services.AddSwaggerGen();

builder.Services.AddControllers()
    // ��� OData
    .AddOData(options => options.Select().Expand().Filter().OrderBy().SetMaxTop(100).Count());

var app = builder.Build();

// Configure the HTTP request pipeline.

// Swagger �������
app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "API Doc");

    options.RoutePrefix = "api-docs";
});

app.MapControllers();

// ��ʼ��ʾ������
using (var scpoe = app.Services.CreateScope())
{
    using (var dbContext = scpoe.ServiceProvider.GetRequiredService<AppDbContext>())
    {
        var text = File.ReadAllText("./sample-data.json", Encoding.UTF8);
        var users = JsonConvert.DeserializeObject<List<User>>(text);
        dbContext.AddRange(users);
        dbContext.SaveChanges();
    }
}

app.Run();