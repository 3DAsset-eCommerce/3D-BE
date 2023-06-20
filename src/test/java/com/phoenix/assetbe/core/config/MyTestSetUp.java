package com.phoenix.assetbe.core.config;

import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.cart.Cart;
import com.phoenix.assetbe.model.cart.CartRepository;
import com.phoenix.assetbe.model.order.*;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MyTestSetUp extends DummyEntity{

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PreviewRepository previewRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TagRepository tagRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetSubCategoryRepository assetSubCategoryRepository;
    private final AssetTagRepository assetTagRepository;
    private final CartRepository cartRepository;
    private final WishListRepository wishListRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final PaymentRepository paymentRepository;
    private final MyAssetRepository myAssetRepository;

    public MyTestSetUp(UserRepository userRepository,
                       AssetRepository assetRepository,
                       PreviewRepository previewRepository,
                       CategoryRepository categoryRepository,
                       SubCategoryRepository subCategoryRepository,
                       TagRepository tagRepository,
                       AssetCategoryRepository assetCategoryRepository,
                       AssetSubCategoryRepository assetSubCategoryRepository,
                       AssetTagRepository assetTagRepository,
                       CartRepository cartRepository,
                       WishListRepository wishListRepository,
                       OrderRepository orderRepository,
                       OrderProductRepository orderProductRepository,
                       PaymentRepository paymentRepository,
                       MyAssetRepository myAssetRepository) {

        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.previewRepository = previewRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.tagRepository = tagRepository;
        this.assetCategoryRepository = assetCategoryRepository;
        this.assetSubCategoryRepository = assetSubCategoryRepository;
        this.assetTagRepository = assetTagRepository;
        this.cartRepository = cartRepository;
        this.wishListRepository = wishListRepository;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.paymentRepository = paymentRepository;
        this.myAssetRepository = myAssetRepository;
    }

    List<String> categories = Arrays.asList("cute", "pretty", "sexy", "luxury", "dirty");
    List<String> subCategories = Arrays.asList("man", "woman", "boy", "girl", "runner", "dancer");
    List<Double> priceList = Arrays.asList(1000D, 2000D, 3000D, 4000D, 5000D);
    List<Double> sizeList = Arrays.asList(1D, 2D, 3D, 4D, 5D);
    List<Double> ratingList = Arrays.asList(5D, 4D, 3D, 2D, 1D);


    public List<User> saveUser() {
        List<User> userList = Arrays.asList(
                newUser("yu", "hyunju1"),
                newUser("song", "jaegeun2"),
                newUser("yang", "jinho3"),
                newUser("lee", "jihun4"),
                newUser("lee", "roun5"),
                newUser("lee", "chanyung6"),
                newUser("song", "jiyun7")
        );
        userRepository.saveAll(userList);

        return userList;
    }

    public List<Asset> saveAsset(){

        List<String> firstTitle = Arrays.asList("cute", "pretty", "sexy", "luxury", "dirty");
        List<String> lastTitle = Arrays.asList("man", "woman", "boy", "girl", "runner", "dancer");
        List<String> titles = new ArrayList<String>(firstTitle.size() * lastTitle.size());
        for (String title1: firstTitle){
            for (String title2: lastTitle){
                titles.add(title1 + " " + title2);
            }
        }

        List<Double> prices = Stream.generate(() -> priceList)
                .limit(30)
                .flatMap(List::stream)
                .collect(Collectors.toList());

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

        List<Double> ratings = Stream.generate(() -> ratingList)
                .limit(30)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        //Asset
        List<Asset> assetList = new ArrayList<Asset>();
        for(int i = 0; i < 30; i++){
            Asset asset = newAsset(titles.get(i), prices.get(i), sizes.get(i), dates.get(i), ratings.get(i));
            assetList.add(asset);
        }
        assetRepository.saveAll(assetList);

        // Preview
        List<Preview> previewList = new ArrayList<>();
        for(Asset asset : assetList){
            Preview preview = Preview.builder().asset(asset).previewUrl("preview.url").build();
            previewList.add(preview);
        }
        previewRepository.saveAll(previewList);

        return assetList;
    }

    public void saveCategoryAndSubCategoryAndTag(List<Asset> assetList) {
        List<Category> categoryList = new ArrayList<>();
        for (String category : categories) {
            categoryList.add(Category.builder().categoryName(category).build());
        }
        categoryRepository.saveAll(categoryList);

        List<SubCategory> subCategoryList = new ArrayList<>();
        for (String subcategory : subCategories) {
            subCategoryList.add(SubCategory.builder().subCategoryName(subcategory).build());
        }
        subCategoryRepository.saveAll(subCategoryList);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tagList.add(Tag.builder().tagName("tag" + i).build());
        }
        tagRepository.saveAll(tagList);

        List<AssetCategory> assetCategoryList = new ArrayList<>(30);
        for(int i = 0; i < 5; i++){ //1~6 동일한 카테고리
            Category category = categoryList.get(i);
            for(int j = 0; j < 6; j++){
                Asset asset = assetList.get((i * 6) + j);

                assetCategoryList.add(AssetCategory.builder().asset(asset).category(category).build());
            }
        }
        assetCategoryRepository.saveAll(assetCategoryList);

        //AssetSubCategory
        List<AssetSubCategory> assetSubCategoryList = new ArrayList<>();
        for(int i = 0; i < 5; i++){ //1~6 동일한 카테고리, 각각 다른 서브 카테고리
            Category category = categoryList.get(i);
            for(int j = 0; j < 6; j++){
                Asset asset = assetList.get((i * 6) + j);
                SubCategory subCategory = subCategoryList.get(j);

                assetSubCategoryList.add(AssetSubCategory.builder().asset(asset).category(category).subCategory(subCategory).build());
            }
        }
        assetSubCategoryRepository.saveAll(assetSubCategoryList);

        //AssetTag
        List<AssetTag> assetTagList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            Category category = categoryList.get(i);
            for(int j = 0; j < 6; j++){
                Asset asset = assetList.get((i * 6) + j);
                SubCategory subCategory = subCategoryList.get(j);

                for(int k = 0; k < 10; k++){
                    Tag tag = tagList.get(k);
                    assetTagList.add(AssetTag.builder().asset(asset).category(category).subCategory(subCategory).tag(tag).build());
                }
            }
        }
        assetTagRepository.saveAll(assetTagList);
    }

    public void saveUserScenario(List<User> userList, List<Asset> assetList) {
        /**
         * 1L 사용자 -> 1L~8L 구매, 5L~12L 장바구니, 10L~18L 위시
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

        for (Integer userIndex : cartUserIndexList) {
            User user = userList.get(userIndex);

            for (int i = 4; i < 12; i++) {
                Asset asset = assetList.get(i);

                cartList.add(Cart.builder().user(user).asset(asset).build());
            }
        }
        cartRepository.saveAll(cartList);

        //WishList
        List<WishList> wishListList = new ArrayList<>();
        List<Integer> wishUserIndexList = Arrays.asList(0, 1, 3, 5);

        for (Integer userIndex : wishUserIndexList) {
            User user = userList.get(userIndex);

            for (int i = 9; i < 18; i++) {
                Asset asset = assetList.get(i);

                wishListList.add(WishList.builder().user(user).asset(asset).build());
            }
        }
        wishListRepository.saveAll(wishListList);

        //Order
        List<OrderProduct> orderProductList = new ArrayList<>();
        List<MyAsset> myAssetList = new ArrayList<>();
        List<Integer> orderUserIndexList = Arrays.asList(0, 2, 3, 4);

        for (Integer userIndex : orderUserIndexList) {
            User user = userList.get(userIndex);

            Payment payment = Payment.builder().paymentTool("국민카드").totalPrice(21000D).receiptURL("receipt.url").build();
            Order order = Order.builder().user(user).phoneNumber("010-1234-1234").payment(payment).build();
            paymentRepository.save(payment);
            orderRepository.save(order);
            payment.mappingOrder(order);
            paymentRepository.save(payment);

            for (int i = 0; i < 8; i++) {
                Asset asset = assetList.get(i);
                OrderProduct orderProduct = OrderProduct.builder().order(order).asset(asset).build();
                MyAsset myAsset = MyAsset.builder().asset(asset).user(user).build();

                orderProductList.add(orderProduct);
                myAssetList.add(myAsset);
            }
        }
        orderProductRepository.saveAll(orderProductList);
        myAssetRepository.saveAll(myAssetList);

    }
}
