package com.upc.comparasalud.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSearchResponseDTO {

    private List<ProviderSummaryDTO> providers;
    private PaginationDTO pagination;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderSummaryDTO {
        private Long providerId;
        private String fullName;
        private String specialty;
        private BigDecimal pricePerAppointment;
        private BigDecimal averageRating;
        private Integer experienceYears;
        private AddressDTO address;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private String district;
        private String city;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDTO {
        private int currentPage;
        private int totalPages;
        private long totalResults;
    }
}
