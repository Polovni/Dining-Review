package com.example.DiningReview.Controller;
import com.example.DiningReview.Model.Restaurant;
import com.example.DiningReview.Repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;


@RequestMapping("/restaurants")
@RestController
public class RestaurantController {
    private final Pattern zipCodePattern = Pattern.compile("\\d{5}");
    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/{zipCode}")
    public Iterable<Restaurant> getByZipCodeAndAllergyScore(@PathVariable String zipCode,
                                                            @RequestParam String allergy) {
        validateZipCode(zipCode);
        Iterable<Restaurant> restaurants = Collections.EMPTY_LIST;
        if(allergy.equalsIgnoreCase("peanut")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipCode);
        }
        if(allergy.equalsIgnoreCase("egg")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipCode);
        }
        if(allergy.equalsIgnoreCase("dairy")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipCode);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return restaurants;
    }

    @GetMapping
    public Iterable<Restaurant> getAllRestaurant() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if(!restaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return restaurant.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        validateNewRestaurant(restaurant);

        return restaurantRepository.save(restaurant);
    }

    @GetMapping("/search")
    public Iterable<Restaurant> searchRestaurant(@RequestParam String zipCode, @RequestParam String allergy) {
        Iterable<Restaurant> restaurants;
        if(allergy.equalsIgnoreCase("peanut")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipCode);
        }
        else if(allergy.equalsIgnoreCase("dairy")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipCode);
        }
        else if(allergy.equalsIgnoreCase("egg")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipCode);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return restaurants;
    }

    private void validateNewRestaurant(Restaurant restaurant) {
        if(ObjectUtils.isEmpty(restaurant.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> alreadyExists = restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if(alreadyExists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateZipCode(String zipcode) {
        if(!zipCodePattern.matcher(zipcode).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
