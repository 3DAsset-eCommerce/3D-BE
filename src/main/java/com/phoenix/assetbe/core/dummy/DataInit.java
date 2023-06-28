package com.phoenix.assetbe.core.dummy;

import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.order.*;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataInit extends DummyEntity {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            AssetRepository assetRepository,
            CategoryRepository categoryRepository,
            SubCategoryRepository subCategoryRepository,
            TagRepository tagRepository,
            AssetCategoryRepository assetCategoryRepository,
            AssetSubCategoryRepository assetSubCategoryRepository,
            AssetTagRepository assetTagRepository,
            PreviewRepository previewRepository,
            CartRepository cartRepository,
            WishListRepository wishListRepository,
            OrderRepository orderRepository,
            OrderProductRepository orderProductRepository,
            PaymentRepository paymentRepository,
            MyAssetRepository myAssetRepository,
            ReviewRepository reviewRepository) {
        return args -> {

            // User  1L  yu  hyunju1   yuhyunju1@nate.com  qwe123!@#
            List<User> userList = Arrays.asList(
                    newUser("yu", "hyunju1"),
                    newUser("song", "jaegeun2"),
                    newUser("yang", "jinho3"),
                    newUser("lee", "jihun4"),
                    newUser("lee", "roun5"),
                    newUser("lee", "chanyung6"),
                    newUser("song", "jiyun7"),
                    newAdmin("kuan", "liza8")
            );
            userRepository.saveAll(userList);

            // 에셋 내용 리스팅
            List<String> firstTitle = Arrays.asList("cute", "pretty", "sexy", "luxury", "dirty");
            List<String> lastTitle = Arrays.asList("man", "woman", "boy", "girl", "runner", "dancer");
            List<String> titles = new ArrayList<String>(firstTitle.size() * lastTitle.size());
            for (String title1: firstTitle){
                for (String title2: lastTitle){
                    titles.add(title1 + "  " + title2);
                }
            }

            List<Double> priceList = Arrays.asList(1000D, 2000D, 3000D, 4000D, 5000D);
            List<Double> prices = Stream.generate(() -> priceList)
                    .limit(30)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<Double> sizeList = Arrays.asList(1D, 2D, 3D, 4D, 5D);
            List<Double> sizes = Stream.generate(() -> sizeList)
                    .limit(30)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<LocalDate> dates = new ArrayList<LocalDate>();
            String dateStr = "2023-06-";
            for(int i = 1; i <= 9; i++){
                dates.add(LocalDate.parse(dateStr + "0" + String.valueOf(i)));
            }
            for(int i = 10; i <= 30; i++){
                dates.add(LocalDate.parse(dateStr + String.valueOf(i)));
            }

            //Asset
            List<Asset> assetList = new ArrayList<Asset>();
            for(int i = 0; i < 8; i++){
                int rating = (i % 5) + 1;
                Asset asset = newAsset(titles.get(i), prices.get(i), sizes.get(i), dates.get(i), Double.valueOf(rating), 1L);
                assetList.add(asset);
            }
            Asset asset = newAsset(titles.get(8), prices.get(8), sizes.get(8), dates.get(8), 0D, 0L);
            assetList.add(asset);
            for(int i = 9; i < 18; i++){
                asset = newAsset1(titles.get(i), prices.get(i), sizes.get(i), dates.get(i), 0D, 0L);
                assetList.add(asset);
            }
            for(int i = 18; i < 30; i++){
                asset = newAsset(titles.get(i), prices.get(i), sizes.get(i), dates.get(i), 0D, 0L);
                assetList.add(asset);
            }
            assetRepository.saveAll(assetList);

            //Category
            List<String> categories = Arrays.asList("cute", "pretty", "sexy", "luxury", "dirty");
            List<Category> categoryList = new ArrayList<Category>();
            for (String category : categories) {
                categoryList.add(Category.builder().categoryName(category).build());
            }
            categoryRepository.saveAll(categoryList);

            //SubCategory
            List<String> subCategories = Arrays.asList("man", "woman", "boy", "girl", "runner", "dancer");
            List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
            for (String subcategory : subCategories) {
                subCategoryList.add(SubCategory.builder().subCategoryName(subcategory).build());
            }
            subCategoryRepository.saveAll(subCategoryList);

            //TagCategory
            List<Tag> tagList = new ArrayList<Tag>();
            for (int i = 1; i <= 10; i++) {
                tagList.add(Tag.builder().tagName("tag" + i).build());
            }
            tagRepository.saveAll(tagList);

            /**
             * Asset 총 30개
             * 상위 카테고리 5개 -> 에셋 6개씩
             * 하위 카테고리 6개 -> 에셋 5개씩
             * 태그 10개 에셋 -> 30개 공통
             */
            //AssetCategory
            List<AssetCategory> assetCategoryList = new ArrayList<AssetCategory>(30);
            for(int i = 0; i < 5; i++){ //1~6 동일한 카테고리
                Category category = categoryList.get(i);
                for(int j = 0; j < 6; j++){
                    asset = assetList.get((i * 6) + j);

                    assetCategoryList.add(AssetCategory.builder().asset(asset).category(category).build());
                }
            }
            assetCategoryRepository.saveAll(assetCategoryList);

            //AssetSubCategory
            List<AssetSubCategory> assetSubCategoryList = new ArrayList<AssetSubCategory>();
            for(int i = 0; i < 5; i++){ //1~6 동일한 카테고리, 각각 다른 서브 카테고리
                Category category = categoryList.get(i);
                for(int j = 0; j < 6; j++){
                    asset = assetList.get((i * 6) + j);
                    SubCategory subCategory = subCategoryList.get(j);

                    assetSubCategoryList.add(AssetSubCategory.builder().asset(asset).category(category).subCategory(subCategory).build());
                }
            }
            assetSubCategoryRepository.saveAll(assetSubCategoryList);

            //AssetTag
            List<AssetTag> assetTagList = new ArrayList<AssetTag>();
            for(int i = 0; i < 5; i++){
                Category category = categoryList.get(i);
                for(int j = 0; j < 6; j++){
                    asset = assetList.get((i * 6) + j);
                    SubCategory subCategory = subCategoryList.get(j);

                    for(int k = 0; k < 10; k++){
                        Tag tag = tagList.get(k);
                        assetTagList.add(AssetTag.builder().asset(asset).category(category).subCategory(subCategory).tag(tag).build());
                    }
                }
            }
            assetTagRepository.saveAll(assetTagList);

            // Preview
            List<Preview> previewList = new ArrayList<>();
            for(Asset asset1 : assetList){
                Preview preview = Preview.builder().asset(asset1).previewUrl("preview.url").build();
                previewList.add(preview);
            }
            previewRepository.saveAll(previewList);

            /**
             * 1L 사용자 -> 1L~8L 구매, 5L~12L 장바구니, 10L~18L 위시, 1L~8L 리뷰 작성
             * 2L 사용자 -> 5L~12L 장바구니, 10L~18L 위시
             * 3L 사용자 -> 1L~8L 구매, 5L~12L 장바구니
             * 4L 사용자 -> 1L~8L 구매, 10L~18L 위시
             * 5L 사용자 -> 1L~8L 구매
             * 6L 사용자 -> 10L~18L 위시
             * 7L 사용자 -> 5L~12L 장바구니
             */

            //Cart
            List<Cart> cartList = new ArrayList<>();
            List<Integer> cartUserIndexList = Arrays.asList(0, 1, 2, 6);

            for(Integer userIndex : cartUserIndexList ){
                User user = userList.get(userIndex);

                for(int i = 4; i < 12; i++){
                    asset = assetList.get(i);

                    cartList.add(Cart.builder().user(user).asset(asset).build());
                }
            }
            cartRepository.saveAll(cartList);

            //WishList
            List<WishList> wishListList = new ArrayList<>();
            List<Integer> wishUserIndexList = Arrays.asList(0, 1, 3, 5);

            for(Integer userIndex : wishUserIndexList ){
                User user = userList.get(userIndex);

                for(int i = 9; i < 18; i++){
                    asset = assetList.get(i);

                    wishListList.add(WishList.builder().user(user).asset(asset).build());
                }
            }
            wishListRepository.saveAll(wishListList);

            //Order
            List<OrderProduct> orderProductList = new ArrayList<>();
            List<MyAsset> myAssetList = new ArrayList<>();
            List<Integer> orderUserIndexList = Arrays.asList(0, 2, 3, 4);

            for(Integer userIndex : orderUserIndexList ){
                User user = userList.get(userIndex);

                Payment payment = Payment.builder().paymentTool("국민카드").totalPrice(21000D).receiptURL("receipt.url").build();
                Order order = Order.builder().user(user).phoneNumber("010-1234-1234").payment(payment).build();
                paymentRepository.save(payment);
                orderRepository.save(order);
                payment.mappingOrder(order);
                paymentRepository.save(payment);

                for(int i = 0; i < 8; i++){
                    asset = assetList.get(i);
                    OrderProduct orderProduct = OrderProduct.builder().order(order).asset(asset).build();
                    MyAsset myAsset = MyAsset.builder().asset(asset).user(user).build();

                    orderProductList.add(orderProduct);
                    myAssetList.add(myAsset);
                }
            }
            orderProductRepository.saveAll(orderProductList);
            myAssetRepository.saveAll(myAssetList);

            String content = "최고다 최고 ~~~~~~~~~~~~~~~~~~~~~~";
            List<Review> reviewList = new ArrayList<>();
            for(int i = 0; i < 8; i++){
                int rating = (i % 5) + 1;
                Review review = Review.builder()
                        .user(userList.get(0))
                        .asset(assetList.get(i))
                        .content(content)
                        .rating(Double.valueOf(rating)).build();
                reviewList.add(review);
            }
            reviewRepository.saveAll(reviewList);

        };
    }
}