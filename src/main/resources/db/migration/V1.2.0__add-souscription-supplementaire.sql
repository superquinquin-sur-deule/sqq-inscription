create sequence SouscriptionSupplementaire_SEQ start with 1 increment by 50;

create table SouscriptionSupplementaire (
    id bigint not null,
    prenom varchar(255) not null,
    nom varchar(255) not null,
    email varchar(255) not null,
    partsSupplementaires bigint not null check ((partsSupplementaires >= 1)),
    status smallint not null check ((status between 0 and 2)),
    stripeSessionId varchar(255),
    createdAt timestamp(6) with time zone,
    updatedAt timestamp(6) with time zone,
    primary key (id)
);
