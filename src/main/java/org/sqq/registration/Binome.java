package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Binome extends PanacheEntity {
    public Genre genre;
    public String nom;
    public String prenom;
    public String adresse;
    public String ville;
    public String codePostal;
    public String email;
    public String telephone;
    public String dateNaissance;
}