create table device
(
    id   int auto_increment,
    name varchar(255) default 'UNKNOWN' null,
    constraint device_id_uindex
        unique (id)
);

alter table device
    add primary key (id);

create table sensor
(
    id        int auto_increment,
    name      varchar(255) null,
    device_id int          null,
    constraint sensor_id_uindex
        unique (id),
    constraint sensor_device_id_fk
        foreign key (device_id) references device (id)
            on update set null on delete set null
);

alter table sensor
    add primary key (id);

create table data
(
    sensor_id     int                                   not null,
    data_time     timestamp default current_timestamp() not null on update current_timestamp(),
    hits          int                                   null,
    temperature_1 double                                null,
    humidity      double                                null,
    co2_1         int                                   null,
    co2_2         int                                   null,
    temperature_2 int                                   null,
    primary key (sensor_id, data_time),
    constraint data_sensor_id_fk
        foreign key (sensor_id) references sensor (id)
            on update cascade on delete cascade
);

insert into device values (1, 'Default');