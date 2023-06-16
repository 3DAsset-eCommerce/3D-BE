package com.phoenix.assetbe.model.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.user.*;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("에셋 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AssetQueryRepositoryTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private EntityManager em;
    @Autowired
    private AssetQueryRepository assetQueryRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);
    }

    @Test
    public void find_assets_test() {
        //Given
        Long userId = 1L;
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());

        Page<AssetResponse.AssetsOutDTO.AssetDetail> result = assetQueryRepository.findAssetsWithUserIdAndPaging(userId, pageable);

        //then
//        assertThat(result.size(), is(9)); //페이징 하기전 테스트
        assertThat(result.getContent().size(), is(3));
        assertThat(result.getContent().get(0).getAssetId(), is(9L));
        assertNull(result.getContent().get(0).getWishlistId());
        assertThat(result.getContent().get(0).getCartId(), is(9L));
        assertThat(result.getContent().get(1).getAssetId(), is(8L));
        assertNull(result.getContent().get(1).getWishlistId());
        assertNull(result.getContent().get(1).getCartId());
        assertThat(result.getContent().get(2).getAssetId(), is(7L));
        assertThat(result.getContent().get(2).getWishlistId(), is(5L));
        assertNull(result.getContent().get(2).getCartId());
    }
}