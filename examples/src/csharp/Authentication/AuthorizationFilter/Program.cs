using AuthorizationFilter.Authentication;
using AuthorizationFilter.Middlewares;
using Microsoft.AspNetCore.Authentication;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication()
    // �����������֤
    .AddScheme<AuthenticationSchemeOptions, DefaultAuthenticationHandler>("Identity.Application", default)
    // ���������֤����
    .AddScheme<PasswordAuthenticationOptions, PasswordAuthenticationHandler>("PasswordV1", default, options =>
    {
        options.Endpoint = new Uri("http://localhost:5000/api/v1/account/password/verify");
    })
    .AddScheme<PasswordAuthenticationOptions, PasswordAuthenticationHandler>("PasswordV2", default, options =>
    {
        options.Endpoint = new Uri("http://localhost:5000/api/v1/account/password/verify");
    });

builder.Services.AddControllers();

var app = builder.Build();

app.UseAuthorization();

app.MapControllers();

app.Run();