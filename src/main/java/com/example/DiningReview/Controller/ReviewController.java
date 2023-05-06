package com.example.DiningReview.Controller;

import com.example.DiningReview.Model.Restaurant;
import com.example.DiningReview.Model.Review;
import com.example.DiningReview.Model.ReviewStatus;
import com.example.DiningReview.Model.User;
import com.example.DiningReview.Repository.DiningReviewRepository;
import com.example.DiningReview.Repository.RestaurantRepository;
import com.example.DiningReview.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    public final DiningReviewRepository diningReviewRepository;
    public final UserRepository userRepository;
    public final RestaurantRepository restaurantRepository;

    public ReviewController(DiningReviewRepository diningReviewRepository,
                            UserRepository userRepository,
                            RestaurantRepository restaurantRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addReview(@RequestBody Review review) {
        validateReview(review);
        review.setStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(review);
    }

    @GetMapping
    public Iterable<Review> getAllReviews() {
        return diningReviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        Optional<Review> reviewOptional= diningReviewRepository.findById(id);
        if(!reviewOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return reviewOptional.get();
    }

    private void validateReview(Review review) {
        if(ObjectUtils.isEmpty(review.getSubmittedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(ObjectUtils.isEmpty(review.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(ObjectUtils.isEmpty(review.getPeanutScore()) &&
                ObjectUtils.isEmpty(review.getDairyScore()) &&
                ObjectUtils.isEmpty(review.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser =  userRepository.findUserByDisplayName(review.getSubmittedBy());
        if(!existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<Restaurant> existingRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if(!existingRestaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
