package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cooperateur1_id", "cooperateur2_id"}))
public class CooperateurRelationship extends PanacheEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooperateur1_id")
    public CooperateurV2 cooperateur1;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooperateur2_id")
    public CooperateurV2 cooperateur2;

    @NotNull
    @Enumerated(EnumType.STRING)
    public RelationshipType relationshipType;

    @CreationTimestamp
    public Instant createdAt;

    public CooperateurRelationship() {
    }
}
