package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.wishList.WishRequest;
import com.phoenix.assetbe.dto.wishList.WishResponse;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.wish.WishList;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;
import com.phoenix.assetbe.model.wish.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishService {

    private final WishListRepository wishListRepository;
    private final WishListQueryRepository wishListQueryRepository;
    private final UserService userService;
    private final AssetService assetService;


    @Transactional
    public void addWishService(WishRequest.AddWishInDTO addWishInDTO, MyUserDetails myUserDetails) {
        Long userId = addWishInDTO.getUserId();
        Long assetId = addWishInDTO.getAssetId();

        userService.authCheck(myUserDetails, userId);

        User userPS = userService.findValidUserById(userId);
        Asset assetPS = assetService.findAssetById(assetId);

        WishList wish = WishList.builder().user(userPS).asset(assetPS).build();

        try {
            wishListRepository.save(wish);
        } catch (Exception e) {
            throw new Exception500("위시리스트 담기 실패 : "+e.getMessage());
        }
    }

    @Transactional
    public void deleteWishService(WishRequest.DeleteWishInDTO deleteWishInDTO, MyUserDetails myUserDetails) {
        Long userId = deleteWishInDTO.getUserId();
        List<Long> wishes = deleteWishInDTO.getWishes();

        userService.authCheck(myUserDetails, userId);

        try {
            wishListRepository.deleteAllById(wishes);
        } catch (Exception e) {
            throw new Exception500("위시리스트 삭제 실패 : "+e.getMessage());
        }
    }

    public List<WishResponse.GetWishListWithOrderAndCartOutDTO> getWishListService(Long userId, MyUserDetails myUserDetails) {
        userService.authCheck(myUserDetails, userId);
        List<WishResponse.GetWishListWithOrderAndCartOutDTO> wishList = wishListQueryRepository.getWishListWithOrderAndCartByUserId(userId);
        return wishList;
    }
}
