package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.core.exception.Exception400;
import com.phoenix.assetbe.core.exception.Exception403;
import com.phoenix.assetbe.dto.wishList.WishRequest;
import com.phoenix.assetbe.model.asset.Asset;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.wish.WishListQueryRepository;
import com.phoenix.assetbe.model.wish.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("위시리스트 서비스 TEST")
public class WishServiceTest extends DummyEntity {

    @Mock
    private WishListRepository wishListRepository;
    @Mock
    private WishListQueryRepository wishListQueryRepository;
    @Mock
    private UserService userService;
    @Mock
    private AssetService assetService;
    private WishService wishService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wishService = new WishService(wishListRepository, wishListQueryRepository, userService, assetService);
    }

    @Test
    @DisplayName(" 담기 성공")
    void testAddWishList() {
        // given
        Long userId = 1L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        when(userService.findValidUserById(1L)).thenReturn(user);

        Asset asset1 = newAsset("에셋1", 1000D, 1D, LocalDate.now(), 1D, 1L);
        when(assetService.findAssetById(1L)).thenReturn(asset1);

        wishService.addWishService(addWishInDTO, myUserDetails);

        // then
        verify(userService, times(1)).findValidUserById(anyLong());
        verify(assetService, times(1)).findAssetById(anyLong());
        verify(wishListRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("위시리스트 담기 실패 : 존재하지 않는 유저")
    void testAddWishList_InvalidUser() {
        // given
        Long userId = 1L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        when(userService.findValidUserById(userId)).thenThrow(new Exception400("id", "존재하지 않는 사용자입니다."));

        assertThrows(Exception400.class, () -> wishService.addWishService(addWishInDTO, myUserDetails));

        // then
        verify(userService, times(1)).findValidUserById(anyLong());
        verify(assetService, never()).findAssetById(anyLong());
        verify(wishListRepository, never()).save(any());
    }

    @Test
    @DisplayName("위시리스트 담기 실패 : 권한 체크 실패")
    void testDAddWishList_AuthCheckFail() {
        // given
        Long userId = 1L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);

        User user = newUser("김", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> wishService.addWishService(addWishInDTO, myUserDetails));

        // then
        verify(userService, never()).findValidUserById(anyLong());
        verify(assetService, never()).findAssetById(anyLong());
        verify(wishListRepository, never()).save(any());
    }

    @Test
    @DisplayName("위시리스트 담기 실패 : 존재하지 않는 에셋")
    void testDAddWishList_InvalidAssetFail() {
        // given
        Long userId = 1L;
        Long assetId = 1L;

        WishRequest.AddWishInDTO addWishInDTO = new WishRequest.AddWishInDTO();
        addWishInDTO.setUserId(userId);
        addWishInDTO.setAssetId(assetId);

        User user = newUser("김", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        doThrow(new Exception400("id", "존재하지 않는 에셋입니다. "))
                .when(assetService).findAssetById(assetId);

        assertThrows(Exception400.class, () -> wishService.addWishService(addWishInDTO, myUserDetails));

        // then
        verify(userService, times(1)).findValidUserById(anyLong());
        verify(assetService, times(1)).findAssetById(anyLong());
        verify(wishListRepository, never()).save(any());
    }

    @Test
    @DisplayName("위시리스트 삭제 성공")
    void testDeleteWishList() {
        // given
        Long userId = 1L;
        List<Long> wishes = Arrays.asList(1L, 2L);

        WishRequest.DeleteWishInDTO deleteWishInDTO = new WishRequest.DeleteWishInDTO();
        deleteWishInDTO.setUserId(userId);
        deleteWishInDTO.setWishes(wishes);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when

        wishService.deleteWishService(deleteWishInDTO, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(wishListRepository, times(1)).deleteAllById(anyList());
    }

    @Test
    @DisplayName("위시리스트 삭제 실패 : 권한 체크 실패")
    void testDeleteWishList_AuthCheckFail() {
        // given
        Long userId = 1L;
        List<Long> wishes = Arrays.asList(1L, 2L);

        WishRequest.DeleteWishInDTO deleteWishInDTO = new WishRequest.DeleteWishInDTO();
        deleteWishInDTO.setUserId(userId);
        deleteWishInDTO.setWishes(wishes);

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> wishService.deleteWishService(deleteWishInDTO, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(wishListRepository, never()).deleteAllById(anyList());
    }

    @Test
    @DisplayName("위시리스트 조회 성공")
    void getWishListTest() {
        // given
        Long userId = 1L;

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        wishService.getWishListService(userId, myUserDetails);

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(wishListQueryRepository, times(1)).getWishListWithOrderAndCartByUserId(anyLong());
    }

    @Test
    @DisplayName("위시리스트 조회 실패 : 권한 체크 실패")
    void getWishListTest_AuthCheckFail() {
        // given
        Long userId = 1L;

        User user = newUser("유", "현주");
        MyUserDetails myUserDetails = new MyUserDetails(user);

        // when
        doThrow(new Exception403("권한이 없습니다."))
                .when(userService).authCheck(myUserDetails, userId);

        assertThrows(Exception403.class, () -> wishService.getWishListService(userId, myUserDetails));

        // then
        verify(userService, times(1)).authCheck(any(MyUserDetails.class), anyLong());
        verify(wishListQueryRepository, never()).getWishListWithOrderAndCartByUserId(anyLong());
    }
}
