package com.example.suchna_sangam.controller;


import com.example.suchna_sangam.model.User;
import com.example.suchna_sangam.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/users"})
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/{userId}"})
    public User getUserDetails(@PathVariable String userId) throws ExecutionException, InterruptedException {
        UserService var10000 = this.userService;
        return UserService.getUserById(userId);
    }

    @GetMapping({"/operators/{districtId}"})
    public List<User> getUserByDistrictId(@PathVariable String districtId) throws ExecutionException, InterruptedException {
        return this.userService.getOperatorsByDistrict(districtId);
    }

    @GetMapping({"/operator-ids/{districtId}"})
    public List<String> getOperatorIdsByDistrict(@PathVariable String districtId) throws ExecutionException, InterruptedException {
        return this.userService.getOperatorIdsByDistrict(districtId);
    }

    @PostMapping({"/logout"})
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", (String)null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping({"/reset-password"})
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) throws ExecutionException, InterruptedException {
        String email = (String)request.get("email");
        String newPassword = (String)request.get("newPassword");
        boolean updated = this.userService.updatePassword(email, newPassword);
        return updated ? ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully")) : ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
    }
}
