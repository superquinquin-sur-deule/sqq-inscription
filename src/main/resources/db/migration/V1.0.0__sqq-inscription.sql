
    create sequence Binome_SEQ start with 1 increment by 50;

    create sequence Cooperateur_SEQ start with 1 increment by 50;

    create table Binome (
        genre smallint check ((genre between 0 and 1)),
        id bigint not null,
        adresse varchar(255),
        codePostal varchar(255),
        dateNaissance varchar(255),
        email varchar(255),
        nom varchar(255),
        prenom varchar(255),
        telephone varchar(255),
        ville varchar(255),
        primary key (id)
    );

    create table Cooperateur (
        acceptationDesStatus boolean not null,
        etudiantOuMinimasSociaux boolean,
        genre smallint not null check ((genre between 0 and 1)),
        status smallint check ((status between 0 and 2)),
        binome_id bigint unique,
        id bigint not null,
        nombreDePersonnesDansLeFoyer bigint check ((nombreDePersonnesDansLeFoyer>=1)),
        parts bigint check ((parts>=1)),
        partsDeSoutien bigint,
        adresse varchar(255) not null,
        codePostal varchar(255) not null,
        email varchar(255) not null,
        nom varchar(255) not null,
        prenom varchar(255) not null,
        stripeSessionId varchar(255),
        telephone varchar(255) not null,
        ville varchar(255) not null,
        primary key (id)
    );

    alter table if exists Cooperateur 
       add constraint FKgddqpe2kte73jpswkskl2iik 
       foreign key (binome_id) 
       references Binome;
