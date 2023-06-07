-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table wishlist_tb;
truncate table order_tb;
truncate table payment_tb;
truncate table orderproduct_tb;
truncate table faq_tb;
truncate table qna_tb;
truncate table cart_tb;
truncate table verifiedcode_tb;
truncate table refreshtoken_tb;
# truncate table error_log_tb;
# truncate table login_log_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화