create table app.data
(
"key" varchar(100),
"value" varchar(100)
)
partition by column ("key");
