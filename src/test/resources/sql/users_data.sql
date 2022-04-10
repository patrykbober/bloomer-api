insert into role (id, creation_date, update_date, optlock_version, name, default_role)
values (1, now(), now(), 0, 'USER', true);
insert into role (id, creation_date, update_date, optlock_version, name, default_role)
values (2, now(), now(), 0, 'ADMIN', false);

insert into bloomer_user (id, creation_date, update_date, optlock_version, active, email, password)
values (1, now(), now(), 0, true, 'user@bloomer.com', '{noop}password');
insert into bloomer_user (id, creation_date, update_date, optlock_version, active, email, password)
values (2, now(), now(), 0, true, 'admin@bloomer.com', '{noop}password');

insert into user_has_roles (user_id, role_id)
values (1, 1);
insert into user_has_roles (user_id, role_id)
values (2, 1);
insert into user_has_roles (user_id, role_id)
values (2, 2);

insert into role (id, creation_date, update_date, optlock_version, name, default_role)
values (3, now(), now(), 0, 'TEST', false);