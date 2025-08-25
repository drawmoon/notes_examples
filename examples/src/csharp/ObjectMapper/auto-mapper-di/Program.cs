using AutoMapper;
using ObjectMapper.Dto;
using ObjectMapper.Entities;
using ObjectMapper.Mappers;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// ʹ�÷����������� Mapper �ķ�ʽת�����󣬽� IMapper ע�ᵽ������
builder.Services.AddSingleton<AutoMapper.IConfigurationProvider>(sp => new MapperConfiguration(config =>
{
    config.AddProfile(typeof(UserMapperProfile));
}));
builder.Services.AddScoped<IMapper>(sp => new Mapper(sp.GetRequiredService<AutoMapper.IConfigurationProvider>(), sp.GetService));

var app = builder.Build();

// Configure the HTTP request pipeline.

app.MapGet("/", (IMapper mapper) =>
{
    var users = new[]
    {
        new User
        {
            Id = Guid.NewGuid().ToString(),
            FirstName = "hu",
            LastName = "hongqi",
            Sex = UserSex.Man
        },
    };

    return mapper.Map<UserDto[]>(users);
});

app.Run();
