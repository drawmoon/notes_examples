using ApiVersionify;
using Microsoft.AspNetCore.Mvc.ApiExplorer;
using Microsoft.Extensions.Options;
using Swashbuckle.AspNetCore.SwaggerGen;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddApiVersioning(options =>
{
    // ������ Response Headers �з��� "api-supported-versions" �� "api-deprecated-versions"
    options.ReportApiVersions = true;

    // ��ʾ��ָ���汾��ʱ����Ĭ�ϰ汾�����Ϊ false ����ָ�� API �İ汾�ţ������� QueryString ����� "?api-version=1.0"
    // options.AssumeDefaultVersionWhenUnspecified = true;
});

// ��Ӱ汾���� API ��Դ�������� IApiVersionDescriptionProvider ��������֧�ָ��ݰ汾�ű�¶ Swagger �ĵ�
builder.Services.AddVersionedApiExplorer();

// Swagger �������
builder.Services.AddTransient<IConfigureOptions<SwaggerGenOptions>, ConfigureSwaggerOptions>();
builder.Services.AddSwaggerGen();

builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline.

// Swagger �������
using (var scope = app.Services.CreateScope())
{
    var provider = scope.ServiceProvider.GetRequiredService<IApiVersionDescriptionProvider>();

    app.UseSwagger();
    app.UseSwaggerUI(options =>
    {
        options.RoutePrefix = "api-docs";
        foreach (var description in provider.ApiVersionDescriptions)
        {
            options.SwaggerEndpoint($"/swagger/{description.GroupName}/swagger.json", description.GroupName);
        }
    });
}

app.MapControllers();

app.Run();