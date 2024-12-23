    package com.example.potholeapp.request;

    import lombok.*;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChangePasswordRequest {

        private String currentPassword;
        private String newPassword;
        private String confirmationPassword;
    }
