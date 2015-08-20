# --- !Ups

create table "FUND" (
  "ID" bigint generated by default as identity(start with 1) not null primary key,
  "NAME" varchar not null,
  "URL" varchar not null,
  "VERIFIED" BOOLEAN not null
);

# --- !Downs

drop table "FUND" if exists;
