# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comunities (
  id                        bigint not null,
  interest_id               bigint,
  location_id               bigint,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_comunities primary key (id))
;

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
  user_id                   bigint,
  first_name                varchar(255),
  last_name                 varchar(255),
  birth_date                timestamp,
  picture                   varchar(255),
  about                     varchar(255),
  gender                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_profiles primary key (id))
;

create table pubComments (
  id                        bigint not null,
  publication_id            bigint,
  user_id                   bigint,
  body                      varchar(255),
  status                    integer,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_pubComments primary key (id))
;

create table publications (
  id                        bigint not null,
  community_id              bigint,
  user_id                   bigint,
  type                      integer,
  body                      varchar(255),
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


create table interests_users (
  interests_id                   bigint not null,
  users_id                       bigint not null,
  constraint pk_interests_users primary key (interests_id, users_id))
;

create table locations_profiles (
  locations_id                   bigint not null,
  profiles_id                    bigint not null,
  constraint pk_locations_profiles primary key (locations_id, profiles_id))
;

create table profiles_locations (
  profiles_id                    bigint not null,
  locations_id                   bigint not null,
  constraint pk_profiles_locations primary key (profiles_id, locations_id))
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

create table users_interests (
  users_id                       bigint not null,
  interests_id                   bigint not null,
  constraint pk_users_interests primary key (users_id, interests_id))
;
create sequence comunities_seq;

create sequence interests_seq;

create sequence linked_account_seq;

create sequence locations_seq;

create sequence profiles_seq;

create sequence pubComments_seq;

create sequence publications_seq;

create sequence security_role_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_permission_seq;

alter table comunities add constraint fk_comunities_interest_1 foreign key (interest_id) references interests (id) on delete restrict on update restrict;
create index ix_comunities_interest_1 on comunities (interest_id);
alter table comunities add constraint fk_comunities_location_2 foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_comunities_location_2 on comunities (location_id);
alter table linked_account add constraint fk_linked_account_user_3 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_3 on linked_account (user_id);
alter table profiles add constraint fk_profiles_user_4 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_profiles_user_4 on profiles (user_id);
alter table pubComments add constraint fk_pubComments_publication_5 foreign key (publication_id) references publications (id) on delete restrict on update restrict;
create index ix_pubComments_publication_5 on pubComments (publication_id);
alter table pubComments add constraint fk_pubComments_user_6 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_pubComments_user_6 on pubComments (user_id);
alter table publications add constraint fk_publications_community_7 foreign key (community_id) references comunities (id) on delete restrict on update restrict;
create index ix_publications_community_7 on publications (community_id);
alter table publications add constraint fk_publications_user_8 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_publications_user_8 on publications (user_id);
alter table token_action add constraint fk_token_action_targetUser_9 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_9 on token_action (target_user_id);
alter table users add constraint fk_users_profile_10 foreign key (profile_id) references profiles (id) on delete restrict on update restrict;
create index ix_users_profile_10 on users (profile_id);



alter table interests_users add constraint fk_interests_users_interests_01 foreign key (interests_id) references interests (id) on delete restrict on update restrict;

alter table interests_users add constraint fk_interests_users_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table locations_profiles add constraint fk_locations_profiles_locatio_01 foreign key (locations_id) references locations (id) on delete restrict on update restrict;

alter table locations_profiles add constraint fk_locations_profiles_profile_02 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_locations add constraint fk_profiles_locations_profile_01 foreign key (profiles_id) references profiles (id) on delete restrict on update restrict;

alter table profiles_locations add constraint fk_profiles_locations_locatio_02 foreign key (locations_id) references locations (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

alter table users_interests add constraint fk_users_interests_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_interests add constraint fk_users_interests_interests_02 foreign key (interests_id) references interests (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists comunities;

drop table if exists interests;

drop table if exists interests_users;

drop table if exists linked_account;

drop table if exists locations;

drop table if exists locations_profiles;

drop table if exists profiles;

drop table if exists profiles_locations;

drop table if exists pubComments;

drop table if exists publications;

drop table if exists security_role;

drop table if exists token_action;

drop table if exists users;

drop table if exists users_security_role;

drop table if exists users_user_permission;

drop table if exists users_interests;

drop table if exists user_permission;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists comunities_seq;

drop sequence if exists interests_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists locations_seq;

drop sequence if exists profiles_seq;

drop sequence if exists pubComments_seq;

drop sequence if exists publications_seq;

drop sequence if exists security_role_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_permission_seq;

