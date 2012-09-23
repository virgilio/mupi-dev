# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table interests (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  picture                   varchar(255),
  status                    integer,
  created                   datetime,
  modified                  datetime,
  constraint pk_interests primary key (id))
;

create table linked_account (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table locations (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  geohash                   varchar(255),
  constraint pk_locations primary key (id))
;

create table profiles (
  id                        bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birth_date                datetime,
  picture                   varchar(255),
  about                     TEXT,
  gender                    integer,
  status                    integer,
  created                   datetime,
  modified                  datetime,
  constraint pk_profiles primary key (id))
;

create table promotions (
  id                        bigint auto_increment not null,
  publication_id            bigint,
  title                     varchar(255),
  address                   varchar(255),
  date                      datetime,
  time                      datetime,
  description               varchar(255),
  picture                   varchar(255),
  link                      varchar(255),
  status                    integer,
  created                   datetime,
  modified                  datetime,
  constraint pk_promotions primary key (id))
;

create table pub_comments (
  id                        bigint auto_increment not null,
  publication_id            bigint,
  profile_id                bigint,
  body                      TEXT,
  status                    integer,
  created                   datetime,
  modified                  datetime,
  constraint pk_pub_comments primary key (id))
;

create table publications (
  id                        bigint auto_increment not null,
  interest_id               bigint,
  location_id               bigint,
  profile_id                bigint,
  pub_typ                   integer,
  body                      TEXT,
  status                    integer,
  created                   datetime,
  modified                  datetime,
  constraint pk_publications primary key (id))
;

create table security_role (
  id                        bigint auto_increment not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table token_action (
  id                        bigint auto_increment not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   datetime,
  expires                   datetime,
  constraint ck_token_action_type check (type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  name                      varchar(255),
  last_login                datetime,
  active                    tinyint(1) default 0,
  status                    integer,
  created                   datetime,
  modified                  datetime,
  profile_id                bigint,
  constraint pk_users primary key (id))
;

create table user_permission (
  id                        bigint auto_increment not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;


create table profiles_locations (
  profiles_id                    bigint not null,
  locations_id                   bigint not null,
  constraint pk_profiles_locations primary key (profiles_id, locations_id))
;

create table profiles_interests (
  profiles_id                    bigint not null,
  interests_id                   bigint not null,
  constraint pk_profiles_interests primary key (profiles_id, interests_id))
;

create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;
alter table linked_account add constraint fk_linked_account_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_1 on linked_account (user_id);
alter table promotions add constraint fk_promotions_publication_2 foreign key (publication_id) references publications (id) on delete restrict on update restrict;
create index ix_promotions_publication_2 on promotions (publication_id);
alter table pub_comments add constraint fk_pub_comments_publication_3 foreign key (publication_id) references publications (id) on delete restrict on update restrict;
create index ix_pub_comments_publication_3 on pub_comments (publication_id);
alter table pub_comments add constraint fk_pub_comments_profile_4 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_pub_comments_profile_4 on pub_comments (profile_id);
alter table publications add constraint fk_publications_interest_5 foreign key (interest_id) references interests (id) on delete restrict on update restrict;
create index ix_publications_interest_5 on publications (interest_id);
alter table publications add constraint fk_publications_location_6 foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_publications_location_6 on publications (location_id);
alter table publications add constraint fk_publications_profile_7 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_publications_profile_7 on publications (profile_id);
alter table token_action add constraint fk_token_action_targetUser_8 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_8 on token_action (target_user_id);
alter table users add constraint fk_users_profile_9 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_users_profile_9 on users (profile_id);



alter table profiles_locations add constraint fk_profiles_locations_profile_01 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_locations add constraint fk_profiles_locations_locatio_02 foreign key (locations_id) references locations (id) on delete restrict on update restrict;

alter table profiles_interests add constraint fk_profiles_interests_profile_01 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_interests add constraint fk_profiles_interests_interes_02 foreign key (interests_id) references interests (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table interests;

drop table linked_account;

drop table locations;

drop table profiles;

drop table profiles_locations;

drop table profiles_interests;

drop table promotions;

drop table pub_comments;

drop table publications;

drop table security_role;

drop table token_action;

drop table users;

drop table users_security_role;

drop table users_user_permission;

drop table user_permission;

SET FOREIGN_KEY_CHECKS=1;

