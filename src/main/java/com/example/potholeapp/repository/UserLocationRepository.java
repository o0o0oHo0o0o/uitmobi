//package com.example.potholeapp.repository;
//
//import com.example.potholeapp.model.user.User;
//import com.example.potholeapp.model.user.UserLocation;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
//    Optional<UserLocation> findTopByUserOrderByTimestampDesc(User user);
//    List<UserLocation> findAllByUserNot(User user);
//}
