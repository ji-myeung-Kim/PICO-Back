package kr.omen.pico.service;

import kr.omen.pico.dao.ApplyRepository;
import kr.omen.pico.dao.PhotographerRepository;
import kr.omen.pico.dao.ReviewRepository;
import kr.omen.pico.dao.UserRepository;
import kr.omen.pico.domain.Apply;
import kr.omen.pico.domain.Photographer;
import kr.omen.pico.domain.Review;
import kr.omen.pico.domain.User;
import kr.omen.pico.domain.dto.PhotographerDTO;
import kr.omen.pico.domain.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApplyRepository applyRepository;
    private final PhotographerRepository photographerRepository;
    private final UserService userService;
    public boolean isNotYetReview(User user, Photographer photographer){

        boolean flag = false;

        Review review = reviewRepository.findByUserAndPhotographer(user, photographer);
        if(review == null){
            flag = true;
        }
        return flag;
    }
    /*
    자신과 계약이 체결이 완료되고 리뷰가 작성되지않은 항목에 대해서 리뷰 작성 가능
    5 - 계약에 체결되어있지만 리뷰가 작성되지 않은상태(리뷰를 작성할 수 있는 상태)
    6 - 계약이 체결되어 리뷰가 작성되어 있는 상태
    리뷰를 작성하게 될시 status가 5 -> 6으로 변경되어 더 이상 쓸 수 없도록 작성
     */
    @Transactional
    public Review saveReview(ReviewDTO.Create dto, Long userIdx) {

        Review review = null;

//        apply 상태가 5일때만 댓글 달 수 있는 상태 = 5번일때만 리뷰작성 가능
//        리뷰가 작성되면 5-> 6 , 6은 매칭됐지만 댓글이 달리지 않은 상태
        try {
            Apply apply = applyRepository.findById(dto.getApplyIdx()).get();
            Photographer photographer = photographerRepository.findById(dto.getPhotographerIdx()).get();
            User estimateUser = userRepository.findById(apply.getEstimate().getUser().getUserIdx()).get();
            User tokenUser = userRepository.findById(userIdx).get();
            if (apply.getStatus().equals(5) && estimateUser == tokenUser) {
                apply.update(6);
                review = reviewRepository.save(new Review(tokenUser, photographer, dto.getCreated(), dto.getContent(), dto.getGrade()));

                // 리뷰 작성 후 작가 정보 수정
                // 임의로 @Setter 사용
                Float newGradeAverage = gradeAverage(photographer.getPhotographerIdx());
                photographer.setGrade(newGradeAverage);
                photographerRepository.save(photographer);
            } else if (apply.getStatus()==6) {
                System.out.println("이미 리뷰를 작성하였습니다.");
            } else {
                System.out.println("체결된 지원만 리뷰를 작성할 수 없습니다..");
            }
        } catch (NullPointerException | NoSuchElementException e) {
            System.out.println("체결되지 않은 계약입니다.");
//            e.printStackTrace();
        }
        return review;
    }

    /*
    자신이 작성한 리뷰 삭제
     */
    @Transactional
    public boolean deleteReview(Long reviewIdx, Long photographerIdx){

        boolean result = false;

        Photographer photographer = photographerRepository.findById(photographerIdx).get();
        List<Review> reviewList = reviewRepository.findAllByPhotographer(photographer);

        try{
            for(Review review2 : reviewList) {
                if (reviewIdx.equals(review2.getReviewIdx())) {

                    Review review = reviewRepository.findById(review2.getReviewIdx()).get();
                    User user = userRepository.findById(review.getUser().getUserIdx()).get();

                    reviewRepository.deleteById(review.getReviewIdx());
                    result = true;
                } else {
                    System.out.println("자신이 작성하지 않았거나  없는 리뷰입니다.");
                }
            }
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("해당리뷰가 존재하지 않습니다.");
        }
        return result;
    }

    /*
    리뷰수정
     */

    public boolean updateReview(ReviewDTO.Update dto, Long userIdx) {

        boolean result = false;

        Photographer photographer = photographerRepository.findById(dto.getPhotographerIdx()).get();
        List<Review> reviewList = reviewRepository.findAllByPhotographer(photographer);
        User user = userRepository.findById(userIdx).get();
        try {
            for (Review review2 : reviewList) {
                if (dto.getReviewIdx().equals(review2.getReviewIdx()) && review2.getUser().getUserIdx().equals(user.getUserIdx())) {
                    Review review = reviewRepository.findById(review2.getReviewIdx()).get();
                    review.update(dto.getContent(), dto.getGrade());
                    reviewRepository.save(review);
                    result = true;
                    break;
                } else {
                    System.out.println("자신이 작성한 리뷰만 수정할 수 있습니다.");
                }
            }
        }catch (NoSuchElementException e){
//            e.printStackTrace();
        }
        return result;
    }

    /*
    해당 작가의 평균 grade 조회
     */
    public Float gradeAverage(Long photographerIdx) {

        Photographer photographer = photographerRepository.findById(photographerIdx).get();
        List<Review> reviewList = reviewRepository.findAllByPhotographer(photographer);

        Float total = (float) 0;
        for(Review review : reviewList){
            total += review.getGrade();
        }
        return total / reviewList.size();
    }

    /*
    작가들의 리뷰 리스트
     */
    public List<ReviewDTO.Card> reviewListByPhotographer(Long userIdx){

        List<Review> reviewList = null;
        User user = userRepository.findById(userIdx).get();
        Photographer photographer = photographerRepository.findByUser(user);

        reviewList = reviewRepository.findAllByPhotographer(photographer);
        List<ReviewDTO.Card> testReivew = new ArrayList<>();
        for(Review review : reviewList){
            testReivew.add(new ReviewDTO.Card(review, user));
        }
        return testReivew;
    }
}
