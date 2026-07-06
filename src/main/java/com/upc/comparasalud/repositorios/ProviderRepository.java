package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findBySpecialtyContainingIgnoreCase(String specialty);

    Optional<Provider> findByAuthUser(AuthUser authUser);

    // HU – Buscar por dirección (street, district o city)
    @Query("""
        SELECT p FROM Provider p
        WHERE LOWER(p.street)   LIKE LOWER(CONCAT('%', :address, '%'))
           OR LOWER(p.district) LIKE LOWER(CONCAT('%', :address, '%'))
           OR LOWER(p.city)     LIKE LOWER(CONCAT('%', :address, '%'))
    """)
    List<Provider> findByAddressContainingIgnoreCase(@Param("address") String address);
    @Query("SELECT p FROM Provider p " +
            "WHERE p.pricePerAppointment >= :minPrice " +
            "AND p.pricePerAppointment <= :maxPrice " +
            "ORDER BY p.pricePerAppointment ASC")
    List<Provider> findByPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice);
    @Query("SELECT p FROM Provider p " +
            "WHERE p.averageRating >= :minRating " +
            "ORDER BY p.averageRating DESC")
    List<Provider> findByMinRating(@Param("minRating") BigDecimal minRating);

    @Query("SELECT p FROM Provider p ORDER BY p.pricePerAppointment ASC")
    List<Provider> findAllOrderByPriceAsc();

    @Query("SELECT p FROM Provider p ORDER BY p.pricePerAppointment DESC")
    List<Provider> findAllOrderByPriceDesc();

    @Query("SELECT p FROM Provider p ORDER BY p.averageRating ASC")
    List<Provider> findAllOrderByRatingAsc();

    @Query("SELECT p FROM Provider p ORDER BY p.averageRating DESC")
    List<Provider> findAllOrderByRatingDesc();

    @Query("""
SELECT p FROM Provider p
    WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY p.averageRating DESC
""")
    Page<Provider> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    List<Provider> findByClinics_Id(Long clinicId);
}