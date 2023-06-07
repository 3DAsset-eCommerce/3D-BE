package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, AssetRepository assetRepository){
        return args -> {

            // User 더미 데이터
            userRepository.save(newUser("유", "현주"));
            userRepository.save(newUser("송", "재근"));
            userRepository.save(newUser("양", "진호"));
            userRepository.save(newUser("이", "지훈"));

            assetRepository.save(newAsset("뛰는 사람"));
            assetRepository.save(newAsset("기어가는 사람"));

        };
    }
}
