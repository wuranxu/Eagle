create table t_eagle_user
(
	id bigint auto_increment,
	email               varchar(64) not null,
	nickname            varchar(24) not null,
	name                varchar(10) not null,
	password            varchar(64) not null,
	tel                 varchar(12) not null,
	role                integer default 0 not null,
	create_time datetime default NOW() not null,
	last_login_time datetime not null,
	last_login_ip varchar(24) not null,
	disabled boolean default false not null,
	constraint t_eagle_user_pk
		primary key (id)
)
comment '用户表';

-- create unique index t_eagle_user_email_uindex
-- 	on t_eagle_user (email);
--
-- create unique index t_eagle_user_tel_uindex
-- 	on t_eagle_user (tel);


create unique index t_eagle_user_tel_disabled_uindex
	on t_eagle_user (tel, disabled);

create unique index t_eagle_user_email_disabled_uindex
	on t_eagle_user (email, disabled);