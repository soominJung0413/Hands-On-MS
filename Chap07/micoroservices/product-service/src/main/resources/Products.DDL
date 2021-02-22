create table products (
                          id varchar ,
                          version int8,
                          product_id int8 unique,
                          name varchar,
                          weight int8,
                          constraint products_id_pk primary key (id),
                          constraint products_productId_uq unique (product_id)
);

select * from products;

create sequence products_seq owned by products.id;

alter table products alter column id set default nextval('products_seq');