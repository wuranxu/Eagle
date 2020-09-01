create table t_eagle_project_role
(
	id bigint auto_increment,
	project_id bigint not null,
	user_id bigint not null,
	project_role int not null,
	constraint t_eagle_project_role_pk
		primary key (id)
)
comment '项目成员表';

create index t_eagle_project_role_project_id_index
	on t_eagle_project_role (project_id);

create index t_eagle_project_role_user_id_index
	on t_eagle_project_role (user_id);

