create table recommendations (
                 id varchar ,
                 version int8,
                 product_id int8,
                 recommendation_id int8,
                 author varchar,
                 rating int8,
                 content varchar,
                 constraint rcs_id_pk primary key (id)
);

drop index rsc_product_rc_idx;

create unique index rsc_product_rc_idx on recommendations (product_id, recommendation_id);

create sequence recommendations_seq owned by recommendations.id;

alter table recommendations alter column id set default nextval('recommendations_seq');