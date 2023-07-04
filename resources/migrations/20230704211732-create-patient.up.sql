create type gender as enum ('man','woman','non-binary','other');
--;; 
create type sex as enum ('male','female');
--;;
create table if not exists patient (
  id char(16) not null primary key default nanoid(),
  name text not null,
  sex sex not null,
  gender gender,
  dob date not null,
  address text,
  created timestamp default now()
);
