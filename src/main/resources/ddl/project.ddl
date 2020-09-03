create table t_eagle_project
(
    id           bigint auto_increment,
    project_name varchar(24)           not null,
    gitlab_url   varchar(256)          not null,
    description  varchar(200)          null,
    owner        bigint                not null,
    create_time  datetime              not null,
    update_time  datetime              not null,
    creator      bigint                not null,
    updater      bigint                not null,
    avatar       varchar(16)           null,
    disabled     boolean default FALSE not null,
    constraint t_eagle_project_pk
        primary key (id)
)
    comment '项目表';

create unique index t_eagle_project_project_name_disabled_uindex
	on t_eagle_project (project_name, disabled);

