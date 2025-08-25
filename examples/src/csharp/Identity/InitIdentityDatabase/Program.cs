using InitIdentityDatabase;
using InitIdentityDatabase.IdentityEntities;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// ע�����ݿ�������
var connectionString = builder.Configuration.GetConnectionString("Default");
builder.Services.AddDbContext<IdentityDbContext>(options => options.UseNpgsql(connectionString));

// ע�������Ϣ�Ĺ���������� UserManager, RoleManager
builder.Services.AddIdentity<UserIdentity, UserIdentityRole>()
    .AddEntityFrameworkStores<IdentityDbContext>();

var app = builder.Build();

// Configure the HTTP request pipeline.

// ���ݿ�Ǩ��
using (var scope = app.Services.CreateScope())
{
    using (var dbContext = scope.ServiceProvider.GetRequiredService<IdentityDbContext>())
    {
        dbContext.Database.Migrate();
    }
}

app.Run();