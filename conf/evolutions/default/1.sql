# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table interests (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  picture                   varchar(255),
  created                   timestamp,
  modified                  timestamp,
  constraint pk_interests primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table locations (
  id                        bigint not null,
  name                      varchar(255),
  geohash                   varchar(255),
  constraint pk_locations primary key (id))
;

create table profiles (
  id                        bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birth_date                timestamp,
  picture                   varchar(255),
  about                     TEXT,
  gender                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_profiles primary key (id))
;

create table promotions (
  id                        bigint not null,
  title                     varchar(255),
  address                   varchar(255),
  date                      timestamp,
  time                      timestamp,
  description               varchar(255),
  image                     varchar(255),
  status                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_promotions primary key (id))
;

create table pub_comments (
  id                        bigint not null,
  publication_id            bigint,
  profile_id                bigint,
  body                      TEXT,
  status                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_pub_comments primary key (id))
;

create table publications (
  id                        bigint not null,
  interest_id               bigint,
  location_id               bigint,
  profile_id                bigint,
  type                      integer,
  body                      TEXT,
  status                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_publications primary key (id))
;

create table security_role (
  id                        bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  last_login                timestamp,
  active                    boolean,
  status                    integer,
  created                   timestamp,
  modified                  timestamp,
  profile_id                bigint,
  constraint pk_users primary key (id))
;

create table user_permission (
  id                        bigint not null,
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
create sequence interests_seq;

create sequence linked_account_seq;

create sequence locations_seq;

create sequence profiles_seq;

create sequence promotions_seq;

create sequence pub_comments_seq;

create sequence publications_seq;

create sequence security_role_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_permission_seq;

alter table linked_account add constraint fk_linked_account_user_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_1 on linked_account (user_id);
alter table pub_comments add constraint fk_pub_comments_publication_2 foreign key (publication_id) references publications (id) on delete restrict on update restrict;
create index ix_pub_comments_publication_2 on pub_comments (publication_id);
alter table pub_comments add constraint fk_pub_comments_profile_3 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_pub_comments_profile_3 on pub_comments (profile_id);
alter table publications add constraint fk_publications_interest_4 foreign key (interest_id) references interests (id) on delete restrict on update restrict;
create index ix_publications_interest_4 on publications (interest_id);
alter table publications add constraint fk_publications_location_5 foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_publications_location_5 on publications (location_id);
alter table publications add constraint fk_publications_profile_6 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_publications_profile_6 on publications (profile_id);
alter table token_action add constraint fk_token_action_targetUser_7 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_7 on token_action (target_user_id);
alter table users add constraint fk_users_profile_8 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_users_profile_8 on users (profile_id);



alter table profiles_locations add constraint fk_profiles_locations_profile_01 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_locations add constraint fk_profiles_locations_locatio_02 foreign key (locations_id) references locations (id) on delete restrict on update restrict;

alter table profiles_interests add constraint fk_profiles_interests_profile_01 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_interests add constraint fk_profiles_interests_interes_02 foreign key (interests_id) references interests (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists interests;

drop table if exists linked_account;

drop table if exists locations;

drop table if exists profiles;

drop table if exists profiles_locations;

drop table if exists profiles_interests;

drop table if exists promotions;

drop table if exists pub_comments;

drop table if exists publications;

drop table if exists security_role;

drop table if exists token_action;

drop table if exists users;

drop table if exists users_security_role;

drop table if exists users_user_permission;

drop table if exists user_permission;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists interests_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists locations_seq;

drop sequence if exists profiles_seq;

drop sequence if exists promotions_seq;

drop sequence if exists pub_comments_seq;

drop sequence if exists publications_seq;

drop sequence if exists security_role_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_permission_seq;

