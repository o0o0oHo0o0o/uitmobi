//package com.example.potholeapp.service;
//
//import com.example.potholeapp.model.user.User   ;
//import com.example.potholeapp.model.user.UserLocation;
//import com.example.potholeapp.repository.UserLocationRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserLocationService {
//
//    @Autowired
//    private UserLocationRepository userLocationRepository;
//
//    public void updateUserLocation(User user, Double latitude, Double longitude) {
//        UserLocation location = new UserLocation();
//        location.setUser(user);
//        location.setLatitude(latitude);
//        location.setLongitude(longitude);
//        location.setTimestamp(LocalDateTime.now());
//        userLocationRepository.save(location);
//    }
//
//    public List<UserLocation> getOtherUsersLocations(User user) {
//        return userLocationRepository.findAllByUserNot(user);
//    }
//
//    public Optional<UserLocation> getUserLocation(Long userId) {
//        // Giả sử bạn có UserRepository để lấy User theo ID
//        // User user = userRepository.findById(userId).orElseThrow(...);
//        // return userLocationRepository.findTopByUserOrderByTimestampDesc(user);
//        return Optional.empty(); // Cần triển khai theo dự án cụ thể
//    }
//}
