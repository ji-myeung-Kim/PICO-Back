package kr.omen.pico.domain.dto;


import kr.omen.pico.domain.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class ResponseDTO {

    /**
     * Estimate 관련 ResponseDTO
     */
    // 변수명 변경 필요
    @Data
    public static class EstimateResponse {
        private Long estimateIdx;
        private Long userIdx;
        private Long categoryIdx;
        private Timestamp created;
        private String content;
        private String city;
        private String address;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer status;

        public EstimateResponse(Estimate entity) {
            this.estimateIdx = entity.getEstimateIdx();
            this.userIdx = entity.getUser().getUserIdx();
            this.categoryIdx = entity.getCategory().getCategoryIdx();
            this.created = entity.getCreated();
            this.content = entity.getContent();
            this.city = entity.getCity();
            this.address = entity.getAddress();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
            this.status = entity.getStatus();
        }
    }

    //유저가 견적서 클릭 시 보여줄 상세페이지와 지원한 작가목록 DTO
    @Data
    public static class EstimateDetailResponse {
        private Long estimateIdx;
        private Long userIdx;
        private Long categoryIdx;
        private String content;
        private String city;
        private String address;
        private Integer status;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<SimplePhotographerCard> applyList;

        public EstimateDetailResponse(Estimate entity, List<SimplePhotographerCard> applies) {
            this.estimateIdx = entity.getEstimateIdx();
            this.userIdx = entity.getUser().getUserIdx();
            this.categoryIdx = entity.getCategory().getCategoryIdx();
            this.content = entity.getContent();
            this.city = entity.getCity();
            this.address = entity.getAddress();
            this.status = entity.getStatus();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
            this.applyList = applies;
        }
    }


    @Getter
    @RequiredArgsConstructor
    public static class EstimateChatRoomDetail{
        private Long estimateIdx;
        private Long userIdx;
        private Long categoryIdx;
        private String content;
        private String city;
        private String address;
        private Integer status;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<SimplePhotographerCard> applyList;

        public EstimateChatRoomDetail(EstimateDetailResponse entity) {
            this.estimateIdx = entity.getEstimateIdx();
            this.userIdx = entity.userIdx;
            this.categoryIdx = entity.getCategoryIdx();
            this.content = entity.getContent();
            this.city = entity.getCity();
            this.address = entity.getAddress();
            this.status = entity.getStatus();
            this.startDate = entity.getStartDate();
            this.endDate = entity.getEndDate();
            this.applyList = entity.getApplyList();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class SimpleChatRoom{
        private Long chatRoomIdx;
        private Long userIdx;
        private Long photographerIdx;
        private Long estimateIdx;

        public SimpleChatRoom(ChatRoom entity){
            this.chatRoomIdx=entity.getChatRoomIdx();
            this.userIdx=entity.getUser().getUserIdx();
            this.photographerIdx=entity.getPhotographer().getPhotographerIdx();
            this.estimateIdx=entity.getEstimate().getEstimateIdx();
        }
    }

    //목록 출력 시 최소한의 정보만을 뿌려줄 DTO
    @Data
    public static class SimpleCard {
        private Long estimateIdx;
        private Integer status;
        private Timestamp created;

        public SimpleCard(Estimate entity) {
            this.estimateIdx = entity.getEstimateIdx();
            this.status = entity.getStatus();
            this.created = entity.getCreated();
        }
    }

    /**
     * Photographer 관련 ResponseDTO
     */

    //해당하는 견적서에 지원한 작가들 목록 출력시 최소한의 정보만 뿌려줄 DTO
    @Data
    public static class SimplePhotographerCard {

        private Long applyIdx;
        private Integer status;
        private Boolean isApplied;
        private UserDTO.SimpleUser user;
        private PhotographerDTO.SimplePhotographer photographer;
        private SimpleChatRoom chatRoom;

        public SimplePhotographerCard(Photographer entity, Apply apply,ChatRoom chatRoom) {
            this.applyIdx = apply.getApplyIdx();
            this.status = apply.getStatus();
            this.isApplied=apply.getIsApplied();
            this.user=new UserDTO.SimpleUser(entity.getUser());
            this.photographer=new PhotographerDTO.SimplePhotographer(entity);
            this.chatRoom=new SimpleChatRoom(chatRoom);
        }
    }

    @Data
    public static class searchPhotographerCard {
        private UserDTO.SimpleUser user;
        private PhotographerDTO.SimplePhotographer photographer;

        public searchPhotographerCard(Photographer entity) {
            this.user=new UserDTO.SimpleUser(entity.getUser());
            this.photographer=new PhotographerDTO.SimplePhotographer(entity);
        }
    }
    /**
     * 통합용 create ResponseDTO
     */
    @Data
    @AllArgsConstructor
    public static class BaseResponse {
        boolean success;
    }

    public static class Create extends BaseResponse {
        Long id;

        public Create(Long id, Boolean success) {
            super(success);
            this.id = id;
        }
    }

    public static class Delete extends BaseResponse {
        public Delete(Boolean success){
            super(success);
        }
    }

    public static class Update extends BaseResponse {
        public Update(Boolean success) {
            super(success);
        }
    }

    @Data
    @AllArgsConstructor
    public static class gradeAverage {
        Float grade;
    }

    @Data
    @AllArgsConstructor
    public static class reviewListResponse{
        private List<ReviewDTO.Card> reviewList;
    }

    @Data
    @AllArgsConstructor
    public static class chatMessageResponse{
        private Long chatMessageIdx;
        private Long chatRoomIdx;
        private Long userIdx;
        private String message;
        private Timestamp created;

        public chatMessageResponse(ChatMessage entity){
            this.chatMessageIdx = entity.getChatMessageIdx();
            this.chatRoomIdx = entity.getChatRoom().getChatRoomIdx();
            this.userIdx = entity.getUser().getUserIdx();
            this.message = entity.getMessage();
            this.created = entity.getCreated();
        }
    }
    /**
     * Work 관련 ResponseDTO
     */
    @Data
    public static class WorkResponse{

        private Long idx;
        private Long photographerIdx;
        private Long categoryIdx;
        private String title;
        private String content;
        private Timestamp created;
        private String thumbnail;

        public WorkResponse(Work entity){
            this.idx=entity.getWorkIdx();
            this.photographerIdx=entity.getPhotographer().getPhotographerIdx();
            this.categoryIdx=entity.getCategory().getCategoryIdx();
            this.title=entity.getTitle();
            this.content=entity.getContent();
            this.created=entity.getCreated();
            this.thumbnail=entity.getThumbnail();
        }

    }

    @Data
    @AllArgsConstructor
    public static class ChatMessageListResponse {
        private List<ChatMessageDTO.Card> chatMessageList;
    }

    @Data
    @AllArgsConstructor
    public static class ChatRoomResponse {
        private Long chatRoomIdx;
        private Long userIdx;
        private Long photographerIdx;
        private Long estimateIdx;

            public ChatRoomResponse(ChatRoom chatRoom){
                this.chatRoomIdx=chatRoom.getChatRoomIdx();
                this.userIdx = chatRoom.getUser().getUserIdx();
                this.photographerIdx = chatRoom.getPhotographer().getPhotographerIdx();
                this.estimateIdx = chatRoom.getEstimate().getEstimateIdx();
            }
    }

}
