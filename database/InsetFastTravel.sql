USE master;
GO

/* Xóa database cũ để có thể chạy lại toàn bộ file mà không bị trùng bảng,
   index hoặc dữ liệu UNIQUE. Lưu ý: dữ liệu cũ sẽ bị xóa. */
IF DB_ID('FastTravelDB') IS NOT NULL
BEGIN
    ALTER DATABASE FastTravelDB
    SET SINGLE_USER
    WITH ROLLBACK IMMEDIATE;

    DROP DATABASE FastTravelDB;
END;
GO

CREATE DATABASE FastTravelDB;
GO
USE FastTravelDB;
GO
CREATE TABLE provinces(id BIGINT IDENTITY PRIMARY KEY,name NVARCHAR(100) NOT NULL UNIQUE,code VARCHAR(10),active BIT NOT NULL DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME());
CREATE TABLE users(id BIGINT IDENTITY PRIMARY KEY,full_name NVARCHAR(120) NOT NULL,email VARCHAR(150) NOT NULL UNIQUE,phone VARCHAR(20) NOT NULL UNIQUE,password_hash VARCHAR(255) NOT NULL,role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' CHECK(role IN('ADMIN','STAFF','DRIVER','CUSTOMER')),status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK(status IN('ACTIVE','INACTIVE','LOCKED')),date_of_birth DATE,gender NVARCHAR(20),address NVARCHAR(255),avatar_url VARCHAR(500),created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME());
CREATE TABLE stations(id BIGINT IDENTITY PRIMARY KEY,province_id BIGINT NOT NULL,name NVARCHAR(150) NOT NULL,address NVARCHAR(255) NOT NULL,latitude FLOAT,longitude FLOAT,phone VARCHAR(20),active BIT NOT NULL DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),CONSTRAINT fk_station_province FOREIGN KEY(province_id)REFERENCES provinces(id));
CREATE TABLE routes(id BIGINT IDENTITY PRIMARY KEY,code VARCHAR(30) NOT NULL UNIQUE,name NVARCHAR(255) NOT NULL,origin_station_id BIGINT NOT NULL,destination_station_id BIGINT NOT NULL,distance_km INT,duration_minutes INT,base_price DECIMAL(14,2) NOT NULL,active BIT NOT NULL DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(origin_station_id)REFERENCES stations(id),FOREIGN KEY(destination_station_id)REFERENCES stations(id),CHECK(origin_station_id<>destination_station_id),CHECK(base_price>=0));
CREATE TABLE vehicles(id BIGINT IDENTITY PRIMARY KEY,license_plate VARCHAR(20) NOT NULL UNIQUE,name NVARCHAR(120) NOT NULL,type VARCHAR(20) NOT NULL CHECK(type IN('SLEEPER','LIMOUSINE','SEATER')),total_seats INT NOT NULL CHECK(total_seats BETWEEN 4 AND 60),floors INT NOT NULL DEFAULT 1 CHECK(floors IN(1,2)),image_url VARCHAR(500),active BIT NOT NULL DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME());
CREATE TABLE seats(id BIGINT IDENTITY PRIMARY KEY,vehicle_id BIGINT NOT NULL,seat_number VARCHAR(10) NOT NULL,[floor] INT NOT NULL,row_index INT NOT NULL,column_index INT NOT NULL,seat_type VARCHAR(30) DEFAULT 'STANDARD',active BIT DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(vehicle_id)REFERENCES vehicles(id)ON DELETE CASCADE,CONSTRAINT uq_vehicle_seat UNIQUE(vehicle_id,seat_number));
CREATE TABLE trips(id BIGINT IDENTITY PRIMARY KEY,code VARCHAR(30) NOT NULL UNIQUE,route_id BIGINT NOT NULL,vehicle_id BIGINT NOT NULL,driver_id BIGINT,departure_time DATETIME2 NOT NULL,arrival_time DATETIME2 NOT NULL,price DECIMAL(14,2) NOT NULL,status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK(status IN('SCHEDULED','BOARDING','RUNNING','FINISHED','CANCELLED')),boarding_note NVARCHAR(500),created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(route_id)REFERENCES routes(id),FOREIGN KEY(vehicle_id)REFERENCES vehicles(id),FOREIGN KEY(driver_id)REFERENCES users(id),CHECK(arrival_time>departure_time),CHECK(price>=0));
CREATE TABLE trip_stops(id BIGINT IDENTITY PRIMARY KEY,trip_id BIGINT NOT NULL,station_id BIGINT NOT NULL,stop_order INT NOT NULL,arrival_time DATETIME2,departure_time DATETIME2,pickup_allowed BIT DEFAULT 1,dropoff_allowed BIT DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(trip_id)REFERENCES trips(id)ON DELETE CASCADE,FOREIGN KEY(station_id)REFERENCES stations(id),CONSTRAINT uq_trip_stop_order UNIQUE(trip_id,stop_order));
CREATE TABLE trip_seats(id BIGINT IDENTITY PRIMARY KEY,trip_id BIGINT NOT NULL,seat_id BIGINT NOT NULL,status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK(status IN('AVAILABLE','HELD','BOOKED','BLOCKED')),hold_token VARCHAR(100),hold_expires_at DATETIME2,[version] BIGINT DEFAULT 0,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(trip_id)REFERENCES trips(id)ON DELETE CASCADE,FOREIGN KEY(seat_id)REFERENCES seats(id),CONSTRAINT uq_trip_seat UNIQUE(trip_id,seat_id));
CREATE TABLE promotions(id BIGINT IDENTITY PRIMARY KEY,code VARCHAR(30) NOT NULL UNIQUE,name NVARCHAR(150) NOT NULL,type VARCHAR(20) NOT NULL CHECK(type IN('PERCENT','FIXED')),[value] DECIMAL(14,2) NOT NULL,max_discount DECIMAL(14,2),min_order_amount DECIMAL(14,2),usage_limit INT,used_count INT DEFAULT 0,start_at DATETIME2 NOT NULL,end_at DATETIME2 NOT NULL,active BIT DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),CHECK(end_at>start_at));
CREATE TABLE bookings(id BIGINT IDENTITY PRIMARY KEY,code VARCHAR(30) NOT NULL UNIQUE,user_id BIGINT NOT NULL,trip_id BIGINT NOT NULL,promotion_id BIGINT,subtotal DECIMAL(14,2) NOT NULL,discount_amount DECIMAL(14,2) NOT NULL DEFAULT 0,total_amount DECIMAL(14,2) NOT NULL,status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT' CHECK(status IN('PENDING_PAYMENT','PAID','CANCELLED','EXPIRED','REFUNDED')),payment_method VARCHAR(30),expires_at DATETIME2,paid_at DATETIME2,cancelled_at DATETIME2,cancellation_reason NVARCHAR(500),created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(user_id)REFERENCES users(id),FOREIGN KEY(trip_id)REFERENCES trips(id),FOREIGN KEY(promotion_id)REFERENCES promotions(id));
CREATE TABLE tickets(id BIGINT IDENTITY PRIMARY KEY,code VARCHAR(40) NOT NULL UNIQUE,booking_id BIGINT NOT NULL,trip_seat_id BIGINT NOT NULL,pickup_station_id BIGINT,dropoff_station_id BIGINT,passenger_name NVARCHAR(120) NOT NULL,passenger_phone VARCHAR(20) NOT NULL,passenger_email VARCHAR(150),ticket_price DECIMAL(14,2) NOT NULL,status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK(status IN('PENDING','VALID','BOARDED','CANCELLED','REFUNDED')),qr_payload VARCHAR(500),created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(booking_id)REFERENCES bookings(id)ON DELETE CASCADE,FOREIGN KEY(trip_seat_id)REFERENCES trip_seats(id),FOREIGN KEY(pickup_station_id)REFERENCES stations(id),FOREIGN KEY(dropoff_station_id)REFERENCES stations(id));
CREATE TABLE payments(id BIGINT IDENTITY PRIMARY KEY,booking_id BIGINT NOT NULL,transaction_code VARCHAR(50) NOT NULL UNIQUE,gateway_transaction_id VARCHAR(100),gateway VARCHAR(30) DEFAULT 'VNPAY',type VARCHAR(20) NOT NULL DEFAULT 'PAYMENT' CHECK(type IN('PAYMENT','REFUND')),status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK(status IN('PENDING','SUCCESS','FAILED')),amount DECIMAL(14,2) NOT NULL,bank_code VARCHAR(30),response_code VARCHAR(20),error_message NVARCHAR(500),transaction_time DATETIME2,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(booking_id)REFERENCES bookings(id));
CREATE TABLE service_addons(id BIGINT IDENTITY PRIMARY KEY,name NVARCHAR(120) NOT NULL,description NVARCHAR(500),price DECIMAL(14,2) NOT NULL,icon VARCHAR(100),active BIT DEFAULT 1,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME());
CREATE TABLE ticket_services(id BIGINT IDENTITY PRIMARY KEY,ticket_id BIGINT NOT NULL,service_addon_id BIGINT NOT NULL,quantity INT NOT NULL,subtotal DECIMAL(14,2) NOT NULL,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(ticket_id)REFERENCES tickets(id)ON DELETE CASCADE,FOREIGN KEY(service_addon_id)REFERENCES service_addons(id));
CREATE TABLE reviews(id BIGINT IDENTITY PRIMARY KEY,booking_id BIGINT NOT NULL UNIQUE,user_id BIGINT NOT NULL,rating INT NOT NULL CHECK(rating BETWEEN 1 AND 5),comment NVARCHAR(1000),visible BIT DEFAULT 1,admin_reply NVARCHAR(1000),created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(booking_id)REFERENCES bookings(id),FOREIGN KEY(user_id)REFERENCES users(id));
CREATE TABLE notifications(id BIGINT IDENTITY PRIMARY KEY,user_id BIGINT NOT NULL,title NVARCHAR(200),message NVARCHAR(1000),type VARCHAR(30),read_flag BIT DEFAULT 0,created_at DATETIME2 DEFAULT SYSDATETIME(),updated_at DATETIME2 DEFAULT SYSDATETIME(),FOREIGN KEY(user_id)REFERENCES users(id)ON DELETE CASCADE);
GO
CREATE INDEX ix_trip_search ON trips(route_id,departure_time,status);CREATE INDEX ix_tripseat_status ON trip_seats(trip_id,status);CREATE INDEX ix_booking_user ON bookings(user_id,created_at DESC);CREATE INDEX ix_booking_expiry ON bookings(status,expires_at);CREATE INDEX ix_payment_booking ON payments(booking_id,status);
GO
CREATE OR ALTER VIEW vw_trip_availability AS SELECT t.id trip_id,t.code,r.name route_name,t.departure_time,t.price,v.name vehicle_name,SUM(CASE WHEN ts.status='AVAILABLE' THEN 1 ELSE 0 END) available_seats,COUNT(ts.id) total_seats FROM trips t JOIN routes r ON r.id=t.route_id JOIN vehicles v ON v.id=t.vehicle_id LEFT JOIN trip_seats ts ON ts.trip_id=t.id GROUP BY t.id,t.code,r.name,t.departure_time,t.price,v.name;
GO
CREATE OR ALTER PROCEDURE sp_release_expired_seats AS BEGIN SET NOCOUNT ON;BEGIN TRAN;UPDATE trip_seats SET status='AVAILABLE',hold_token=NULL,hold_expires_at=NULL,updated_at=SYSDATETIME() WHERE status='HELD' AND hold_expires_at<SYSDATETIME();UPDATE bookings SET status='EXPIRED',updated_at=SYSDATETIME() WHERE status='PENDING_PAYMENT' AND expires_at<SYSDATETIME();COMMIT;END;
GO
INSERT provinces(name,code)VALUES(N'TP. Hồ Chí Minh','HCM'),(N'Tây Ninh','TNI'),(N'Lâm Đồng','LDG'),(N'Bà Rịa - Vũng Tàu','VTU'),(N'Đà Nẵng','DNG'),(N'Hà Nội','HAN');
INSERT stations(province_id,name,address,phone)VALUES(1,N'Bến xe Miền Đông mới',N'501 Hoàng Hữu Nam, TP Thủ Đức','19006067'),(2,N'Bến xe Tây Ninh',N'Đường Trưng Nữ Vương, Tây Ninh','02763822363'),(3,N'Bến xe Đà Lạt',N'01 Tô Hiến Thành, Đà Lạt','02633835858'),(4,N'Bến xe Vũng Tàu',N'192 Nam Kỳ Khởi Nghĩa, Vũng Tàu','02543859972'),(5,N'Bến xe Trung tâm Đà Nẵng',N'185 Tôn Đức Thắng, Đà Nẵng','02363786123'),(6,N'Bến xe Nước Ngầm',N'01 Ngọc Hồi, Hà Nội','02438612158');
INSERT users(full_name,email,phone,password_hash,role,status)VALUES(N'Quản trị FastTravel','admin@fasttravel.vn','0900000001','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','ADMIN','ACTIVE'),(N'Nguyễn Tứ Văn','van@fasttravel.vn','0900000002','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','CUSTOMER','ACTIVE'),(N'Tài xế Nguyễn Minh','driver@fasttravel.vn','0900000003','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','DRIVER','ACTIVE');
INSERT routes(code,name,origin_station_id,destination_station_id,distance_km,duration_minutes,base_price)VALUES('SG-TN',N'Sài Gòn - Tây Ninh',1,2,100,180,150000),('SG-DL',N'Sài Gòn - Đà Lạt',1,3,310,420,320000),('SG-VT',N'Sài Gòn - Vũng Tàu',1,4,105,150,180000),('SG-DN',N'Sài Gòn - Đà Nẵng',1,5,960,1020,650000);
INSERT vehicles(license_plate,name,type,total_seats,floors)VALUES('51B-123.45',N'FastTravel Premium 01','SLEEPER',34,2),('70B-678.90',N'FastTravel Limousine 02','LIMOUSINE',22,1);
DECLARE @v BIGINT=1,@i INT=1;WHILE @i<=34 BEGIN INSERT seats(vehicle_id,seat_number,[floor],row_index,column_index)VALUES(@v,CONCAT(CASE WHEN @i<=17 THEN 'A' ELSE 'B' END,FORMAT(CASE WHEN @i<=17 THEN @i ELSE @i-17 END,'00')),CASE WHEN @i<=17 THEN 1 ELSE 2 END,((@i-1)%17)/4+1,((@i-1)%4)+1);SET @i+=1;END;
SET @v=2;SET @i=1;WHILE @i<=22 BEGIN INSERT seats(vehicle_id,seat_number,[floor],row_index,column_index)VALUES(@v,CONCAT('L',FORMAT(@i,'00')),1,(@i-1)/4+1,((@i-1)%4)+1);SET @i+=1;END;
INSERT trips(code,route_id,vehicle_id,driver_id,departure_time,arrival_time,price,boarding_note)VALUES('FT-SGTN-01',1,1,3,DATEADD(DAY,1,DATEADD(HOUR,7,CAST(CAST(GETDATE() AS DATE)AS DATETIME2))),DATEADD(DAY,1,DATEADD(HOUR,10,CAST(CAST(GETDATE() AS DATE)AS DATETIME2))),150000,N'Có mặt trước 30 phút'),('FT-SGDL-01',2,2,3,DATEADD(DAY,1,DATEADD(HOUR,22,CAST(CAST(GETDATE() AS DATE)AS DATETIME2))),DATEADD(DAY,2,DATEADD(HOUR,5,CAST(CAST(GETDATE() AS DATE)AS DATETIME2))),320000,N'Xe đêm limousine');
INSERT trip_seats(trip_id,seat_id)SELECT 1,id FROM seats WHERE vehicle_id=1;INSERT trip_seats(trip_id,seat_id)SELECT 2,id FROM seats WHERE vehicle_id=2;
INSERT service_addons(name,description,price,icon)VALUES(N'Hành lý thêm 10kg',N'Thêm 10kg hành lý ký gửi',50000,'luggage'),(N'Suất ăn',N'Suất ăn nhẹ trên hành trình',60000,'meal'),(N'Bảo hiểm chuyến đi',N'Bảo hiểm tai nạn hành khách',30000,'shield');
INSERT promotions(code,name,type,[value],max_discount,min_order_amount,usage_limit,start_at,end_at)VALUES('WELCOME10',N'Chào mừng khách mới','PERCENT',10,100000,100000,1000,GETDATE(),DATEADD(YEAR,1,GETDATE())),('FAST50K',N'Giảm ngay 50.000đ','FIXED',50000,NULL,300000,500,GETDATE(),DATEADD(MONTH,6,GETDATE()));
GO

/* =========================================================
   DỮ LIỆU MẪU CHO CÁC BẢNG CÒN LẠI
   ========================================================= */

-- Các điểm dừng của từng chuyến
INSERT trip_stops(trip_id,station_id,stop_order,arrival_time,departure_time,pickup_allowed,dropoff_allowed)
VALUES
(1,1,1,NULL,DATEADD(DAY,1,DATEADD(HOUR,7,CAST(CAST(GETDATE() AS DATE) AS DATETIME2))),1,0),
(1,2,2,DATEADD(DAY,1,DATEADD(HOUR,10,CAST(CAST(GETDATE() AS DATE) AS DATETIME2))),NULL,0,1),
(2,1,1,NULL,DATEADD(DAY,1,DATEADD(HOUR,22,CAST(CAST(GETDATE() AS DATE) AS DATETIME2))),1,0),
(2,3,2,DATEADD(DAY,2,DATEADD(HOUR,5,CAST(CAST(GETDATE() AS DATE) AS DATETIME2))),NULL,0,1);

-- Đơn đặt vé: một đơn đã thanh toán và một đơn đang chờ thanh toán
INSERT bookings(code,user_id,trip_id,promotion_id,subtotal,discount_amount,total_amount,status,payment_method,expires_at,paid_at)
VALUES
('BK-FT-0001',2,1,1,300000,30000,270000,'PAID','VNPAY',NULL,SYSDATETIME()),
('BK-FT-0002',2,2,2,320000,50000,270000,'PENDING_PAYMENT','VNPAY',DATEADD(MINUTE,15,SYSDATETIME()),NULL);

-- Đánh dấu các ghế được sử dụng bởi hai đơn trên
DECLARE @tripSeat1 BIGINT=(SELECT TOP 1 id FROM trip_seats WHERE trip_id=1 ORDER BY id);
DECLARE @tripSeat2 BIGINT=(SELECT id FROM (SELECT id,ROW_NUMBER() OVER(ORDER BY id) AS rn FROM trip_seats WHERE trip_id=1) s WHERE rn=2);
DECLARE @tripSeat3 BIGINT=(SELECT TOP 1 id FROM trip_seats WHERE trip_id=2 ORDER BY id);

UPDATE trip_seats SET status='BOOKED',updated_at=SYSDATETIME() WHERE id IN(@tripSeat1,@tripSeat2);
UPDATE trip_seats SET status='HELD',hold_token='HOLD-BK-FT-0002',hold_expires_at=DATEADD(MINUTE,15,SYSDATETIME()),updated_at=SYSDATETIME() WHERE id=@tripSeat3;

-- Vé hành khách
INSERT tickets(code,booking_id,trip_seat_id,pickup_station_id,dropoff_station_id,passenger_name,passenger_phone,passenger_email,ticket_price,status,qr_payload)
VALUES
('TK-FT-0001',1,@tripSeat1,1,2,N'Nguyễn Tứ Văn','0900000002','van@fasttravel.vn',150000,'VALID','FASTTRAVEL|TK-FT-0001|VALID'),
('TK-FT-0002',1,@tripSeat2,1,2,N'Nguyễn Minh Anh','0900000004','minhanh@example.com',150000,'VALID','FASTTRAVEL|TK-FT-0002|VALID'),
('TK-FT-0003',2,@tripSeat3,1,3,N'Nguyễn Tứ Văn','0900000002','van@fasttravel.vn',320000,'PENDING','FASTTRAVEL|TK-FT-0003|PENDING');

-- Giao dịch thanh toán VNPay
INSERT payments(booking_id,transaction_code,gateway_transaction_id,gateway,type,status,amount,bank_code,response_code,transaction_time)
VALUES
(1,'PAY-FT-0001','VNPAY-202608240001','VNPAY','PAYMENT','SUCCESS',270000,'NCB','00',SYSDATETIME()),
(2,'PAY-FT-0002',NULL,'VNPAY','PAYMENT','PENDING',270000,NULL,NULL,NULL);

-- Dịch vụ đi kèm trên vé
INSERT ticket_services(ticket_id,service_addon_id,quantity,subtotal)
VALUES
(1,1,1,50000),
(1,3,1,30000),
(2,2,1,60000);

-- Đánh giá chuyến đi mẫu
INSERT reviews(booking_id,user_id,rating,comment,visible,admin_reply)
VALUES(1,2,5,N'Xe sạch, nhân viên hỗ trợ nhiệt tình và khởi hành đúng giờ.',1,N'Cảm ơn bạn đã sử dụng FastTravel!');

-- Thông báo cho khách hàng và tài xế
INSERT notifications(user_id,title,message,type,read_flag)
VALUES
(2,N'Đặt vé thành công',N'Đơn BK-FT-0001 đã thanh toán thành công. Vui lòng có mặt trước giờ khởi hành 30 phút.','BOOKING',0),
(2,N'Chờ thanh toán',N'Đơn BK-FT-0002 đang chờ thanh toán qua VNPay.','PAYMENT',0),
(3,N'Lịch chạy mới',N'Bạn được phân công chuyến FT-SGTN-01.','TRIP',0);

-- Cập nhật số lượt đã dùng của khuyến mãi
UPDATE promotions SET used_count=1,updated_at=SYSDATETIME() WHERE id IN(1,2);
GO

-- Kiểm tra nhanh dữ liệu vừa thêm
SELECT * FROM vw_trip_availability;
SELECT * FROM bookings ORDER BY id;
SELECT * FROM tickets ORDER BY id;
SELECT * FROM payments ORDER BY id;
GO