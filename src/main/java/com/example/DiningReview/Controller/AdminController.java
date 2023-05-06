package com.example.DiningReview.Controller;

import com.example.DiningReview.Model.AdminReviewAction;
import com.example.DiningReview.Model.Restaurant;
import com.example.DiningReview.Model.Review;
import com.example.DiningReview.Model.ReviewStatus;
import com.example.DiningReview.Repository.DiningReviewRepository;
import com.example.DiningReview.Repository.RestaurantRepository;
import com.example.DiningReview.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public final DiningReviewRepository diningReviewRepository;
    public final RestaurantRepository restaurantRepository;
    public final UserRepository userRepository;

    public AdminController(DiningReviewRepository diningReviewRepository, RestaurantRepository restaurantRepository,
                           UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this. restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @PutMapping("/reviews/{id}")
    public Review addStatus(@PathVariable Long id, @RequestBody AdminReviewAction action) {
        Optional<Review> reviewOptional = diningReviewRepository.findById(id);
        if(!reviewOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Review reviewToUpdate = reviewOptional.get();
        if(action.getAccept()) {
            reviewToUpdate.setStatus(ReviewStatus.ACCEPTED);
        }
        else {
            reviewToUpdate.setStatus(ReviewStatus.REJECTED);
        }
        diningReviewRepository.save(reviewToUpdate);

        Optional<Restaurant> restaurant = restaurantRepository.findById(reviewToUpdate.getRestaurantId());
        if(!restaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        updateRestaurantScores(restaurant.get());
        return reviewToUpdate;
    }

    private void updateRestaurantScores(Restaurant restaurant) {
        List<Review> reviews = diningReviewRepository.findReviewsByRestaurantIdAndStatus(restaurant.getId(), ReviewStatus.ACCEPTED);

        int sumPeanut = 0;
        int sumDairy = 0;
        int sumEgg = 0;
        int countPeanut = 0;
        int countDairy = 0;
        int countEgg = 0;

        for(Review rev : reviews) {
            if(!ObjectUtils.isEmpty(rev.getPeanutScore())) {
                sumPeanut += rev.getPeanutScore();
                countPeanut++;
            }
            if(!ObjectUtils.isEmpty(rev.getDairyScore())) {
                sumDairy += rev.getDairyScore();
                countDairy++;
            }
            if(!ObjectUtils.isEmpty(rev.getEggScore())) {
                sumEgg += rev.getEggScore();
                countEgg++;
            }

            int sum = sumPeanut + sumDairy + sumEgg;
            int count = countPeanut + countDairy + countEgg;

            float overallScore = (float) sum / count;
            restaurant.setOverallScore(decimalFormat.format(overallScore));

            if(countPeanut > 1) {
                float peanut = sumPeanut / countPeanut;
                restaurant.setPeanutScore(decimalFormat.format(peanut));
            }
            if(countDairy > 1) {
                float dairy = sumDairy / countDairy;
                restaurant.setDairyScore(decimalFormat.format(dairy));
            }
            if(countEgg > 1) {
                float egg = sumEgg / countEgg;
                restaurant.setEggScore(decimalFormat.format(egg));
            }

            restaurantRepository.save(restaurant);
        }
    }
}
