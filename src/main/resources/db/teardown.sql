-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table asset_tb;
truncate table asset_tag_tb;
truncate table asset_category_tb;
truncate table asset_sub_category_tb;
truncate table category_tb;
truncate table sub_category_tb;
truncate table tag_tb;
truncate table my_asset_tb;
truncate table review_tb;
truncate table refresh_token_tb;
truncate table cart_tb;
truncate table order_tb;
truncate table order_product_tb;
truncate table payment_tb;
truncate table wishlist_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화
