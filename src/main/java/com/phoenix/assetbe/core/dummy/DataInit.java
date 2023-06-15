package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.AssetRepository;
import com.phoenix.assetbe.model.asset.MyAssetRepository;
import com.phoenix.assetbe.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, AssetRepository assetRepository, MyAssetRepository myAssetRepository){
        return args -> {

            // User 더미 데이터
            userRepository.save(newUser("유", "현주")); //1L 유현주@nate.com
            userRepository.save(newUser("송", "재근"));
            userRepository.save(newUser("양", "진호"));
            userRepository.save(newUser("이", "지훈"));


            // Asset 더미 데이터
            assetRepository.save(newAsset("뛰는 사람")); // 1L 이름만 존재
            assetRepository.save(newAsset("기어가는 사람"));

            assetRepository.save(newAsset1("뛰는 사람")); // 모든 필드 존재
            assetRepository.save(newAsset2("기어가는 사람"));

            // MyAsset 더미 데이터
//            myAssetRepository.save(newMyAsset1(newUser("송", "재근"), newAsset("뛰는 사람")));
//            myAssetRepository.save(newMyAsset2(newUser("송", "재근"), newAsset("기어가는 사람")));
        };
    }
}
