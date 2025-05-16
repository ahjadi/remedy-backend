package com.alialjadi.remedybackend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val inputStream = javaClass.classLoader.getResourceAsStream("serviceAccountKey.json")
            ?: throw IllegalStateException("Firebase service account file not found in classpath")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }

        return FirebaseMessaging.getInstance()
    }
}

//@Configuration
//class FirebaseConfig {
//
//    @PostConstruct
//    fun init() {
//        val serviceAccount = FileInputStream("src/main/resources/serviceAccountKey.json")
//
//        val options = FirebaseOptions.builder()
//            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//            .build()
//
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options)
//        }
//    }
//}