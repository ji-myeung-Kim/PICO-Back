package kr.omen.pico.domain.dto;

import kr.omen.pico.domain.Category;
import kr.omen.pico.domain.Estimate;
import kr.omen.pico.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class EstimateDTO {


    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Create {
//        private Long getEstimateIdx;
        private Long userIdx;
        private Long categoryIdx;
        private Long photographerIdx;
//        private Timestamp created;
        private String content;
        private String city;
        private String address;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer status;

        public Create(Estimate entity, Long photographer) {
//            getEstimateIdx = entity.getEstimateIdx();
            userIdx = entity.getUser().getUserIdx();
            categoryIdx = entity.getCategory().getCategoryIdx();
//            created=entity.getCreated();
            content = entity.getContent();
            city = entity.getCity();
            address = entity.getAddress();
            startDate = entity.getStartDate();
            endDate = entity.getEndDate();
            status = entity.getStatus();
            photographerIdx = photographer;
        }

        public Estimate toEntity(User user, Category category){
            return Estimate.builder()
                    .user(user)
                    .category(category)
//                    .created(created)
                    .content(content)
                    .city(city)
                    .address(address)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(status)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class SimpleEstimate{
        private Long estimateIdx;
        private Integer status;

        public SimpleEstimate(Estimate entity){
            this.estimateIdx=entity.getEstimateIdx();
            this.status=entity.getStatus();
        }
    }
}
