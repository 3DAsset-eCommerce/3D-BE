package com.phoenix.assetbe.model.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private EntityManager em;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetQueryRepository assetQueryRepository;

    @Autowired
    private AssetTagRepository assetTagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyAssetRepository myAssetRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() throws Exception {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User u1 = User.builder().email("user1@gmail.com").firstName("일").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u2 = User.builder().email("user2@gmail.com").firstName("이").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u3 = User.builder().email("user3@gmail.com").firstName("삼").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        User u4 = User.builder().email("user4@gmail.com").firstName("사").lastName("유저").status(Status.ACTIVE).role(Role.USER).password(passwordEncoder.encode("1234")).emailVerified(true).provider(SocialType.COMMON).build();
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));

        Asset a1 = Asset.builder().assetName("a").size(4.0).fileUrl("address-asset1.FBX").extension(".FBX").price(10000D).rating(4.0).releaseDate(LocalDate.parse("2023-05-01")).reviewCount(3L).visitCount(10000L).wishCount(1000L).creator("NationA").build();
        Asset a2 = Asset.builder().assetName("b").size(4.1).fileUrl("address-asset2.FBX").extension(".FBX").price(10001D).rating(4.1).releaseDate(LocalDate.parse("2023-05-02")).reviewCount(101L).visitCount(10001L).wishCount(1001L).creator("NationA").build();
        Asset a3 = Asset.builder().assetName("c").size(4.2).fileUrl("address-asset3.FBX").extension(".FBX").price(10002D).rating(4.2).releaseDate(LocalDate.parse("2023-05-03")).reviewCount(102L).visitCount(10002L).wishCount(1002L).creator("NationA").build();
        Asset a4 = Asset.builder().assetName("d").size(4.3).fileUrl("address-asset4.FBX").extension(".FBX").price(10003D).rating(4.3).releaseDate(LocalDate.parse("2023-05-04")).reviewCount(103L).visitCount(10003L).wishCount(1003L).creator("NationA").build();
        Asset a5 = Asset.builder().assetName("e").size(4.4).fileUrl("address-asset5.FBX").extension(".FBX").price(10004D).rating(4.4).releaseDate(LocalDate.parse("2023-05-05")).reviewCount(104L).visitCount(10004L).wishCount(1004L).creator("NationA").build();
        Asset a6 = Asset.builder().assetName("f").size(4.5).fileUrl("address-asset6.FBX").extension(".FBX").price(10005D).rating(4.5).releaseDate(LocalDate.parse("2023-05-06")).reviewCount(105L).visitCount(10005L).wishCount(1005L).creator("NationA").build();
        Asset a7 = Asset.builder().assetName("g").size(4.6).fileUrl("address-asset7.FBX").extension(".FBX").price(10006D).rating(4.6).releaseDate(LocalDate.parse("2023-05-07")).reviewCount(106L).visitCount(10006L).wishCount(1006L).creator("NationA").build();
        Asset a8 = Asset.builder().assetName("h").size(4.7).fileUrl("address-asset8.FBX").extension(".FBX").price(10007D).rating(4.7).releaseDate(LocalDate.parse("2023-05-08")).reviewCount(107L).visitCount(10007L).wishCount(1007L).creator("NationA").build();
        Asset a9 = Asset.builder().assetName("i").size(4.8).fileUrl("address-asset9.FBX").extension(".FBX").price(10008D).rating(4.8).releaseDate(LocalDate.parse("2023-05-09")).reviewCount(108L).visitCount(10008L).wishCount(1008L).creator("NationA").build();
        assetRepository.saveAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9));

        Category c1 = Category.builder().categoryName("A").build();
        Category c2 = Category.builder().categoryName("B").build();
        Category c3 = Category.builder().categoryName("C").build();
        categoryRepository.saveAll(Arrays.asList(c1, c2, c3));

        SubCategory sc1 = SubCategory.builder().subCategoryName("AA").build();
        SubCategory sc2 = SubCategory.builder().subCategoryName("AB").build();
        SubCategory sc3 = SubCategory.builder().subCategoryName("AC").build();
        SubCategory sc4 = SubCategory.builder().subCategoryName("BA").build();
        SubCategory sc5 = SubCategory.builder().subCategoryName("BB").build();
        SubCategory sc6 = SubCategory.builder().subCategoryName("BC").build();
        SubCategory sc7 = SubCategory.builder().subCategoryName("CA").build();
        SubCategory sc8 = SubCategory.builder().subCategoryName("CB").build();
        SubCategory sc9 = SubCategory.builder().subCategoryName("CC").build();
        subCategoryRepository.saveAll(Arrays.asList(sc1, sc2, sc3, sc4, sc5, sc6, sc7, sc8, sc9));

        Tag t1 = Tag.builder().tagName("tag1").build();
        Tag t2 = Tag.builder().tagName("tag2").build();
        Tag t3 = Tag.builder().tagName("tag3").build();
        Tag t4 = Tag.builder().tagName("tag4").build();
        Tag t5 = Tag.builder().tagName("tag5").build();
        Tag t6 = Tag.builder().tagName("tag6").build();
        tagRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6));

        AssetTag at1 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t1).build();
        AssetTag at2 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t2).build();
        AssetTag at3 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t3).build();
        AssetTag at4 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t4).build();
        AssetTag at5 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t5).build();
        AssetTag at6 = AssetTag.builder().asset(a2).category(c1).subCategory(sc2).tag(t6).build();
        AssetTag at7 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t1).build();
        AssetTag at8 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t2).build();
        AssetTag at9 = AssetTag.builder().asset(a3).category(c1).subCategory(sc3).tag(t3).build();
        AssetTag at10 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t4).build();
        AssetTag at11 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t5).build();
        AssetTag at12 = AssetTag.builder().asset(a4).category(c2).subCategory(sc4).tag(t6).build();
        AssetTag at13 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t1).build();
        AssetTag at14 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t2).build();
        AssetTag at15 = AssetTag.builder().asset(a5).category(c2).subCategory(sc5).tag(t3).build();
        AssetTag at16 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t4).build();
        AssetTag at17 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t5).build();
        AssetTag at18 = AssetTag.builder().asset(a6).category(c2).subCategory(sc6).tag(t6).build();
        AssetTag at19 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t1).build();
        AssetTag at20 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t2).build();
        AssetTag at21 = AssetTag.builder().asset(a7).category(c3).subCategory(sc7).tag(t3).build();
        AssetTag at22 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t4).build();
        AssetTag at23 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t5).build();
        AssetTag at24 = AssetTag.builder().asset(a8).category(c3).subCategory(sc8).tag(t6).build();
        AssetTag at25 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t1).build();
        AssetTag at26 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t2).build();
        AssetTag at27 = AssetTag.builder().asset(a9).category(c3).subCategory(sc9).tag(t3).build();
        AssetTag at28 = AssetTag.builder().asset(a1).category(c1).subCategory(sc1).tag(t6).build();
        assetTagRepository.saveAll(Arrays.asList(at1, at2, at3, at4, at5, at6, at7, at8, at9, at10, at11, at12,
                at13, at14, at15, at16, at17, at18, at19, at20, at21, at22, at23, at24, at25, at26, at27, at28));

        WishList w1 = WishList.builder().asset(a1).user(u1).build();
        WishList w2 = WishList.builder().asset(a3).user(u1).build();
        WishList w3 = WishList.builder().asset(a3).user(u2).build();
        WishList w4 = WishList.builder().asset(a4).user(u2).build();
        WishList w5 = WishList.builder().asset(a7).user(u1).build();
        WishList w6 = WishList.builder().asset(a7).user(u2).build();
        WishList w7 = WishList.builder().asset(a7).user(u3).build();
        WishList w8 = WishList.builder().asset(a8).user(u3).build();
        WishList w9 = WishList.builder().asset(a1).user(u4).build();
        wishListRepository.saveAll(Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8, w9));

        Cart cart1 = Cart.builder().asset(a1).user(u1).build();
        Cart cart2 = Cart.builder().asset(a1).user(u2).build();
        Cart cart3 = Cart.builder().asset(a1).user(u3).build();
        Cart cart4 = Cart.builder().asset(a2).user(u1).build();
        Cart cart5 = Cart.builder().asset(a2).user(u2).build();
        Cart cart6 = Cart.builder().asset(a2).user(u3).build();
        Cart cart7 = Cart.builder().asset(a4).user(u1).build();
        Cart cart8 = Cart.builder().asset(a5).user(u1).build();
        Cart cart9 = Cart.builder().asset(a9).user(u1).build();
        cartRepository.saveAll(Arrays.asList(cart1,cart2,cart3,cart4,cart5,cart6,cart7,cart8,cart9));

        MyAsset m1 = MyAsset.builder().asset(a1).user(u1).build();
        MyAsset m2 = MyAsset.builder().asset(a3).user(u1).build();
        MyAsset m3 = MyAsset.builder().asset(a5).user(u1).build();
        MyAsset m4 = MyAsset.builder().asset(a7).user(u1).build();
        MyAsset m5 = MyAsset.builder().asset(a1).user(u2).build();
        MyAsset m6 = MyAsset.builder().asset(a3).user(u2).build();
        MyAsset m7 = MyAsset.builder().asset(a1).user(u3).build();
        MyAsset m8 = MyAsset.builder().asset(a1).user(u4).build();
        myAssetRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8));

        Review r1 = Review.builder().rating(4D).content("만족").asset(a1).user(u1).build();
        Review r2 = Review.builder().rating(3D).content("평범").asset(a1).user(u2).build();
        Review r3 = Review.builder().rating(5D).content("완전만족").asset(a1).user(u3).build();
        Review r4 = Review.builder().rating(5D).content("완전만족").asset(a3).user(u2).build();
        reviewRepository.saveAll(Arrays.asList(r1, r2, r3, r4));

        em.clear();
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