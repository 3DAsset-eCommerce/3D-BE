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
            userRepository.save(newUser("ssar", "쌀"));
            userRepository.save(newUser("cos", "코스"));

            assetRepository.save(newAsset("뛰는 사람"));
            assetRepository.save(newAsset("기어가는 사람"));
        };
    }
}
