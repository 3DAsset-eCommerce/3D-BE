-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table event_tb;
truncate table order_tb;
truncate table error_log_tb;
truncate table login_log_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화