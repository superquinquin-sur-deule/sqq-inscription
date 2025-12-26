package org.sqq.registration.api;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.sqq.registration.CooperateurV2;
import org.sqq.registration.DataSource;
import org.sqq.registration.MemberStatus;
import org.sqq.registration.PaymentStatus;
import org.sqq.registration.api.dto.CooperateurV2DTO;
import org.sqq.registration.api.dto.CooperateurV2Mapper;
import org.sqq.registration.api.dto.PagedResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Path("/api/v2/administration")
@RolesAllowed("admin")
public class CooperateurV2AdminResource {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "nom", "prenom", "email", "createdAt", "updatedAt", "memberStatus", "paymentStatus"
    );

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Inject
    CooperateurV2Mapper mapper;

    @Inject
    EntityManager entityManager;

    @Path("/cooperateurs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponse<CooperateurV2DTO> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("createdAt,desc") String sort,
            @QueryParam("search") String search,
            @QueryParam("type") String type,
            @QueryParam("memberStatus") String memberStatus,
            @QueryParam("paymentStatus") String paymentStatus,
            @QueryParam("source") String source,
            @QueryParam("createdFrom") String createdFrom,
            @QueryParam("createdTo") String createdTo
    ) {
        // Validate and constrain parameters
        if (page < 0) page = 0;
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;

        // Build query
        StringBuilder jpql = new StringBuilder("SELECT c FROM CooperateurV2 c");
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(c) FROM CooperateurV2 c");
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Type filter (PRINCIPAL or BINOME)
        if (type != null && !type.isBlank()) {
            if ("BINOME".equalsIgnoreCase(type)) {
                conditions.add("EXISTS (SELECT r FROM CooperateurRelationship r WHERE r.cooperateur2 = c)");
            } else if ("PRINCIPAL".equalsIgnoreCase(type)) {
                conditions.add("NOT EXISTS (SELECT r FROM CooperateurRelationship r WHERE r.cooperateur2 = c)");
            }
        }

        // MemberStatus filter
        if (memberStatus != null && !memberStatus.isBlank()) {
            try {
                MemberStatus status = MemberStatus.valueOf(memberStatus.toUpperCase());
                conditions.add("c.memberStatus = ?" + (params.size() + 1));
                params.add(status);
            } catch (IllegalArgumentException ignored) {
                // Invalid status, ignore filter
            }
        }

        // PaymentStatus filter
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            try {
                PaymentStatus status = PaymentStatus.valueOf(paymentStatus.toUpperCase());
                conditions.add("c.paymentStatus = ?" + (params.size() + 1));
                params.add(status);
            } catch (IllegalArgumentException ignored) {
                // Invalid status, ignore filter
            }
        }

        // Source filter
        if (source != null && !source.isBlank()) {
            try {
                DataSource ds = DataSource.valueOf(source.toUpperCase());
                conditions.add("c.source = ?" + (params.size() + 1));
                params.add(ds);
            } catch (IllegalArgumentException ignored) {
                // Invalid source, ignore filter
            }
        }

        // Date range filters
        if (createdFrom != null && !createdFrom.isBlank()) {
            try {
                Instant fromInstant = LocalDate.parse(createdFrom).atStartOfDay().toInstant(ZoneOffset.UTC);
                conditions.add("c.createdAt >= ?" + (params.size() + 1));
                params.add(fromInstant);
            } catch (Exception ignored) {
                // Invalid date, ignore filter
            }
        }

        if (createdTo != null && !createdTo.isBlank()) {
            try {
                Instant toInstant = LocalDate.parse(createdTo).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                conditions.add("c.createdAt < ?" + (params.size() + 1));
                params.add(toInstant);
            } catch (Exception ignored) {
                // Invalid date, ignore filter
            }
        }

        // Global text search
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.toLowerCase() + "%";
            conditions.add("(" +
                    "LOWER(c.prenom) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(c.nom) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(c.email) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(c.telephone) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(c.adresse) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(c.ville) LIKE ?" + (params.size() + 1) + " OR " +
                    "LOWER(COALESCE(c.notes, '')) LIKE ?" + (params.size() + 1) +
                    ")");
            params.add(pattern);
        }

        // Build WHERE clause
        if (!conditions.isEmpty()) {
            String whereClause = " WHERE " + String.join(" AND ", conditions);
            jpql.append(whereClause);
            countJpql.append(whereClause);
        }

        // Parse and apply sorting
        String sortField = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.Descending;

        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length >= 1 && SORTABLE_FIELDS.contains(sortParts[0])) {
                sortField = sortParts[0];
            }
            if (sortParts.length >= 2 && "asc".equalsIgnoreCase(sortParts[1])) {
                sortDirection = Sort.Direction.Ascending;
            }
        }

        jpql.append(" ORDER BY c.").append(sortField);
        jpql.append(sortDirection == Sort.Direction.Ascending ? " ASC" : " DESC");

        // Execute count query
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);
        for (int i = 0; i < params.size(); i++) {
            countQuery.setParameter(i + 1, params.get(i));
        }
        long totalElements = countQuery.getSingleResult();

        // Execute data query with pagination
        TypedQuery<CooperateurV2> dataQuery = entityManager.createQuery(jpql.toString(), CooperateurV2.class);
        for (int i = 0; i < params.size(); i++) {
            dataQuery.setParameter(i + 1, params.get(i));
        }
        dataQuery.setFirstResult(page * size);
        dataQuery.setMaxResults(size);

        List<CooperateurV2> entities = dataQuery.getResultList();
        List<CooperateurV2DTO> dtos = mapper.toDTOList(entities);

        return new PagedResponse<>(dtos, totalElements, page, size);
    }
}
