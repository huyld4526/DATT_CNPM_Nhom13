-- ===========================================
-- CẤU HÌNH DATABASE
-- ===========================================
DROP DATABASE IF EXISTS sachcu_db;
CREATE DATABASE sachcu_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sachcu_db;

-- ===========================================
-- BẢNG USER
-- ===========================================
CREATE TABLE user (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    province VARCHAR(50),
    district VARCHAR(50),
    ward VARCHAR(50),
    status ENUM('PENDING','ACTIVE','SUSPENDED','BANNED','DELETED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================================
-- BẢNG ADMIN
-- ===========================================
CREATE TABLE admin (
    adminID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- BẢNG CATEGORY
-- ===========================================
CREATE TABLE category (
    categoryID INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL
);

-- ===========================================
-- BẢNG BOOK
-- ===========================================
CREATE TABLE book (
    bookID INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image VARCHAR(255),
    province VARCHAR(50),
    district VARCHAR(50),
    book_condition VARCHAR(50),
    contact_info VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- BẢNG POSTS
-- ===========================================
CREATE TABLE posts (
    postID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT NOT NULL,
    bookID INT NOT NULL UNIQUE,
    description TEXT,
    status ENUM('APPROVED','PENDING','DECLINED','SOLD') DEFAULT 'PENDING',
    FOREIGN KEY (userID) REFERENCES user(userID) ON DELETE CASCADE,
    FOREIGN KEY (bookID) REFERENCES book(bookID) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================================
-- BẢNG BOOK_CATEGORY
-- ===========================================
CREATE TABLE book_category (
    bookID INT NOT NULL,
    categoryID INT NOT NULL,
    PRIMARY KEY (bookID, categoryID),
    FOREIGN KEY (bookID) REFERENCES book(bookID) ON DELETE CASCADE,
    FOREIGN KEY (categoryID) REFERENCES category(categoryID) ON DELETE CASCADE
);

-- ===========================================
-- BẢNG REPORT
-- ===========================================
CREATE TABLE report (
    reportID INT AUTO_INCREMENT PRIMARY KEY,
    postID INT NOT NULL,
    adminID INT,
    reason TEXT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('OPEN','RESOLVED','DISMISSED') DEFAULT 'OPEN',
    FOREIGN KEY (postID) REFERENCES posts(postID) ON DELETE CASCADE,
    FOREIGN KEY (adminID) REFERENCES admin(adminID) ON DELETE SET NULL
);

-- ===========================================
-- CATEGORY SAMPLE DATA
-- ===========================================
INSERT INTO category (categoryID, category_name) VALUES
(1,	'Văn học Việt Nam'),
(2,	'Văn học nước ngoài'),
(3,	'Kỹ năng sống'),
(4,	'Tin học'),
(5,	'Kinh tế'),
(6,	'Thiếu nhi'),
(7,	'Truyện tranh - Manga'),
(8,	'Giáo trình đại học'),
(9, 'Tâm lý - Triết học'),
(10, 'Light Novel'),
(12, 'Khoa học viển tưởng'),
(13, 'Tiểu Thuyết'),
(14, 'Truyện ngắn');

-- ===========================================
-- ADMIN SAMPLE
-- ===========================================
INSERT INTO admin (adminID, name, email, password, created_at) VALUES
(1,'Quản trị viên','admin@sachcu.vn','$2a$12$lKGQzsHyOLBhzUarV2OVm.jinv3lKI45N5yx.JRRAKtH.psnnHZNS','2025-12-01 16:16:50');

-- ===========================================
-- USER SAMPLE DATA
-- ===========================================
INSERT INTO user (userID, name, email, password, phone, province, district, ward, status, created_at, updated_at) VALUES
(13, 'Nguyễn Văn Test',	'test@gmail.com', '$2a$10$B.vBwfDe5AdGfQ2mA5KpTeriWsP.biwqLGwjiqNZpwpdxYX18y3I.', '0909999999',	'79', '761', '26776', 'ACTIVE',	'2025-12-03 04:04:52.998102', '2025-12-03 04:04:52.998102'),
(14, 'Hồ Trọng Bảo', 'vungtau@gmail.com', '$2a$10$v2XgrkmVbQ8pPn2MevXljeJ07p5ZD9A4v8qqelMaDdzlHrieSUYpm',	'0909888888',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-03 04:43:43.409431',	'2025-12-12 09:08:18.261077'),
(15,	'Huy',	'huy@gmail.com',	'$2a$10$zql/pzUBgaDyyoQYu.QB5uzQyFzdjj4vMoonBXtOHlPOOlaq5Jsh.',	'0328605555',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-03 13:23:32.477215',	'2025-12-06 06:19:53.927825'),
(16,	'Huy',	'Hoang@gmail.com',	'$2a$10$x1u8HtXE8ROVR4tfduhu8.Kf0stGA6IOBmyZe0sQOjZog6n/4RM1.',	'0328605555',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-06 05:36:29.761204',	'2025-12-06 05:36:29.761204'),
(17,	'Bao',	'baocodon@gmail.com',	'$2a$10$m4j/WAnwzVwEfp/8vkK9HOX9gEEl6O/M5aSBPMQ23rJLh.SFcjI3W',	'0000001298',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-06 07:08:40.839113',	'2025-12-06 07:08:40.839113'),
(18,	'Nguyễn Văn A',	'vana@gmail.com',	'$2a$10$69B4wDtNa2jVv/90oxbhUObGYfEgnZSSG7k0aKm.deYMyvkmo1Pa6',	'0333564566',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-10 11:19:11.973839',	'2025-12-10 11:19:11.973839'),
(19,	'Trân Thành',	'tranthanh@gmail.com',	'$2a$10$k1nz3mYZwlhV3NJFu7fOXuwSyQ./lyQHbDonDUIp5uR0yNRaOB6k2',	'0328605113',	'96',	'967',	'32077',	'ACTIVE',	'2025-12-12 08:28:48.005674',	'2025-12-12 08:28:48.007178'),
(20,	'Nhật Doan',	'nhatdoan@gmail.com',	'$2a$10$k1nz3mYZwlhV3NJFu7fOXuwSyQ./lyQHbDonDUIp5uR0yNRaOB6k2',	'0908070666',	'79',	'761',	'26776',	'ACTIVE',	'2025-12-12 09:04:56.483508',	'2025-12-12 09:04:56.483508');

-- ===========================================
-- BOOK SAMPLE DATA (KHỚP CỘT)
-- ===========================================
INSERT INTO book (bookID, title, author, price, description, image, province, district, book_condition, contact_info, created_at) VALUES
(1,	'Đắc nhân tâm',	'Dale Carnegie',	60000.00,	'Sách kỹ năng kinh điển, bản in đẹp.',	NULL,	'4',	'42',	'Củ 80%',	'0300100222',	'2025-12-02 11:09:05.577692'),
(2,	'Nhà giả kim',	'Nguyễn Nhật Ánh',	75000.00,	'Sách đẹp, đọc 1 lần',	'http://localhost:8080/api/images/16088de5-9eae-4d11-8c2c-d48594f08a5d.jpg',	'4',	'42',	'Mới',	'0300100222',	'2025-12-03 13:31:14.126766'),
(4,	'Tôi thấy hoa vàng trên cỏ xanh',	'Nguyễn Nhật Ánh',	55000.00,	'Còn mới 85%',	'http://localhost:8080/api/images/d121b093-da44-443e-baf3-ca7ce066ea9b.jpg',	'4',	'42',	'Cũ nhẹ (90%)',	'0300100222',	'2025-12-03 13:31:14.126766'),
(6,	'Lập trình Python nâng cao',	'NXB CNTT',	95000.00,	'Hướng dẫn lập trình Python chi tiết.',	'http://localhost:8080/api/images/8d9f1afb-9f5f-475d-a26b-88f0c9eda815.jpg',	'4',	'42',	'Củ 80%',	'0300100222',	'2025-12-03 13:31:14.126766'),
(7,	'Cha giàu cha nghèo',	'Robert Kiyosaki',	65000.00,	'Sách tài chính kinh điển.',	'http://localhost:8080/api/images/09a2ad63-0727-48e9-a9ea-bbdbc3ee58e0.jpg',	'4',	'42',	'Củ 80%',	'0300100222',	'2025-12-03 13:31:14.126766'),
(8,	'7 thói quen hiệu quả',	'Stephen R.Covey',	80000.00,	'Phát triển bản thân.',	'http://localhost:8080/api/images/3dc81451-c2a7-4114-a10a-5202eb05729d.jpg',	'4',	'42',	'Củ 80%',	'0300100222',	'2025-12-03 13:31:14.126766'),
(29,	'7 thoái quen hiệu quả',	'Tôi',	85000.00,	'L',	'http://localhost:8080/api/images/cd7f5fb3-446e-42ae-99af-0f2edeabadf0.jpg',	'4',	'42',	'Củ 80%',	'0328605555',	'2025-12-03 13:31:14.126766'),
(34,	'Người quan trọng nhất là bản thân bạn',	'Hồ Trọng Bảo',	100000.00,	'Cuốn sách nói về kĩ năng sống. Vì mới nhận hàng chưa bóc siu muốn pass lại',	'http://localhost:8080/api/images/a6723956-56bb-463a-8c7c-6298b40026b2.png',	'4',	'42',	'Mới',	'0328605127',	'2025-12-06 15:42:22.531547'),
(36,	'Bình Ngô Đại Cáo',	'Phương Anh',	1000000.00,	'Là 1 kỳ án của Việt Nam',	'http://localhost:8080/api/images/dc8101b5-faec-4548-9465-fdbb0a2faa06.jpg',	'4',	'42',	'Cũ nhẹ (90%)',	'0328605127',	'2025-12-10 11:48:37.971791'),
(38,	'Buông Tay',	'Phương Hoa',	80000.00,	'Nói Chuyện Là Bản Năng, Giữ Miệng Là Tu Dưỡng, Im Lặng Là Trí Tuệ\n\nBí quyết để lời nói thật của mình dễ được người khác chấp nhận hơn? Rất nhiều người giỏi ăn nói đều cho rằng: so với việc đi thẳng vào vấn đề thì cách nói mềm mỏng, khôn khéo sẽ để được đối phương chấp nhận hơn. Không phải ai cũng có thể khiêm tốn tiếp thu lời đề nghị và chỉ bảo của người khác, cho dù bề ngoài đối phương tỏ ra không để ý, nhưng có thể trong lòng vẫn ngầm.\n\n',	'http://localhost:8080/api/images/7788d53b-75ed-4080-b7e0-e1e845970a64.jpg',	'4',	'53',	'Cũ nhẹ (90%)',	'0328604777',	'2025-12-12 13:09:38.780917'),
(39,	'Truyện Kiều',	'Nguyễn Du',	80000.00,	'Tác phẩm thơ Nôm lục bát kiệt xuất, kể về cuộc đời truân chuyên của Thúy Kiều, phản ánh những bất công xã hội và số phận con người dưới chế độ phong kiến.',	'http://localhost:8080/api/images/5a65dfa0-eaab-420b-87b4-802fdd0212eb.jpg',	'79',	'761',	'Cũ nhẹ (90%)',	'0328604777',	'2025-12-12 13:20:55.235845'),
(40,	'Số Đỏ',	'Vũ Trọng Phụng',	100000.00,	'Tiểu thuyết hiện thực trào phúng, phơi bày sự lố lăng, thối nát và xu hướng Âu hóa rởm đời của xã hội tư sản thành thị Việt Nam trước Cách mạng tháng Tám.',	'http://localhost:8080/api/images/222a41d8-6688-44c0-b10e-c331a0b25403.jpg',	'79',	'771',	'Cũ nhẹ (90%)',	'0328605127',	'2025-12-12 13:24:30.175589'),
(41,	'Chí Phèo',	'Nam Cao',	50000.00,	'Truyện ngắn kinh điển mô tả bi kịch bị tha hóa của người nông dân nghèo, đồng thời lên án xã hội đã cướp đi quyền làm người của họ.',	'http://localhost:8080/api/images/163eee6b-a853-443b-8c78-88e8d2d6aa3b.jpg',	'1',	'268',	'Cũ nhẹ (90%)',	'0328605127',	'2025-12-12 13:27:48.532446'),
(42,	'Tắt Đèn',	'Ngô Tất Tố',	150000.00,	'Tiểu thuyết hiện thực phê phán gay gắt chế độ thuế khóa và áp bức nông dân, tiêu biểu qua nhân vật chị Dậu.',	'http://localhost:8080/api/images/35e5b137-fd1f-41d6-a2b1-d04d4679fadd.jpg',	'1',	'268',	'Cũ nhẹ (90%)',	'03286066666',	'2025-12-12 13:30:11.121999'),
(43,	'Đời Thừa',	'Nam Cao',	99000.00,	'Truyện ngắn tiêu biểu cho bi kịch của người trí thức tiểu tư sản nghèo đói: anh Hộ - một nhà văn có tài năng và hoài bão, nhưng bị gánh nặng cơm áo gạo tiền làm thui chột lý tưởng, phải viết những tác phẩm \"thừa\", tầm thường.',	'http://localhost:8080/api/images/38cba478-1572-4797-b2ee-fb5d6ce2a4c0.webp',	'1',	'269',	'Cũ nhẹ (90%)',	'0328606699',	'2025-12-12 13:35:49.857692'),
(44,	'Sống Mòn',	'Nam Cao',	60000.00,	'Tiểu thuyết về cuộc sống của tầng lớp trí thức nghèo, qua nhân vật Thứ. Tác phẩm khắc họa sự bế tắc, tuyệt vọng và tình trạng sống mòn (cuộc sống vô vị, ngày qua ngày bị bào mòn) trong xã hội cũ.',	'http://localhost:8080/api/images/a3519465-780c-4487-9787-07a740d26c34.webp',	'79',	'764',	'Cũ (70%)',	'0328606788',	'2025-12-12 13:36:55.720213'),
(45,	'Giăng Sáng',	'Nam Cao',	250000.00,	'Truyện ngắn thể hiện sự giằng xé nội tâm của người nghệ sĩ: muốn sống lãng mạn, mơ mộng (nhìn trăng sáng) nhưng lại bị hiện thực tăm tối, nghèo khổ (những mảnh đời cơ cực xung quanh) kéo lại.',	'http://localhost:8080/api/images/cc2d78b7-4eda-40c8-b46d-0c870ae682c5.png',	'79',	'776',	'Cũ nhẹ (90%)',	'0328606788',	'2025-12-12 13:38:41.441576'),
(46,	'Một Bữa No',	'Nam Cao',	40000.00,	'Truyện ngắn phơi bày thảm cảnh của người nghèo bị áp bức đến mức nhân phẩm bị chà đạp. Bà lão Hợi và con trai phải ăn một bữa no cuối cùng trước khi bị đuổi khỏi nhà, gợi lên sự phẫn uất.',	'http://localhost:8080/api/images/a70be934-36a6-41bf-b41e-c21920e337cb.webp',	'79',	'761',	'Cũ (70%)',	'0328606788',	'2025-12-12 13:40:17.127065');


-- ===========================================
-- POSTS SAMPLE
-- ===========================================
SET NAMES utf8mb4;

INSERT INTO posts (postID, userID, bookID, description, status, created_at, updated_at) VALUES
(2,	14,	2,	'Sách đẹp, đọc 1 lần',	'APPROVED',	'2025-12-03 13:31:14.126766',	NULL),
(4,	14,	4,	'Còn mới 85%',	'APPROVED',	'2025-12-03 13:31:14.126766',	'2025-12-03 13:31:14.126766'),
(6,	14,	6,	'Dùng học Python rất tốt',	'APPROVED',	'2025-12-03 13:31:14.126766',	NULL),
(7,	14,	7,	'Bán lại giá tốt',	'APPROVED',	'2025-12-03 13:31:14.126766',	NULL),
(8,	14,	8,	'Sách kỹ năng còn mới',	'APPROVED',	'2025-12-03 13:31:14.126766',	'2025-12-03 13:31:14.126766'),
(25,	18,	36,	'Là 1 kỳ án của Việt Nam',	'APPROVED',	'2025-12-10 11:48:38.022220',	'2025-12-12 04:06:20.320846'),
(27,	19,	38,	'Nói Chuyện Là Bản Năng, Giữ Miệng Là Tu Dưỡng, Im Lặng Là Trí Tuệ\n\nBí quyết để lời nói thật của mình dễ được người khác chấp nhận hơn? Rất nhiều người giỏi ăn nói đều cho rằng: so với việc đi thẳng vào vấn đề thì cách nói mềm mỏng, khôn khéo sẽ để được đối phương chấp nhận hơn. Không phải ai cũng có thể khiêm tốn tiếp thu lời đề nghị và chỉ bảo của người khác, cho dù bề ngoài đối phương tỏ ra không để ý, nhưng có thể trong lòng vẫn ngầm.\n\n',	'APPROVED',	'2025-12-12 13:09:38.804390',	'2025-12-12 13:10:14.420069'),
(28,	19,	39,	'Tác phẩm thơ Nôm lục bát kiệt xuất, kể về cuộc đời truân chuyên của Thúy Kiều, phản ánh những bất công xã hội và số phận con người dưới chế độ phong kiến.',	'APPROVED',	'2025-12-12 13:20:55.241360',	'2025-12-12 13:30:47.319063'),
(29,	19,	40,	'Tiểu thuyết hiện thực trào phúng, phơi bày sự lố lăng, thối nát và xu hướng Âu hóa rởm đời của xã hội tư sản thành thị Việt Nam trước Cách mạng tháng Tám.',	'APPROVED',	'2025-12-12 13:24:30.182224',	'2025-12-12 13:30:43.967317'),
(30,	19,	41,	'Truyện ngắn kinh điển mô tả bi kịch bị tha hóa của người nông dân nghèo, đồng thời lên án xã hội đã cướp đi quyền làm người của họ.',	'APPROVED',	'2025-12-12 13:27:48.538444',	'2025-12-12 13:30:41.276625'),
(31,	19,	42,	'Tiểu thuyết hiện thực phê phán gay gắt chế độ thuế khóa và áp bức nông dân, tiêu biểu qua nhân vật chị Dậu.',	'APPROVED',	'2025-12-12 13:30:11.127869',	'2025-12-12 13:30:37.621301'),
(32,	19,	43,	'Truyện ngắn tiêu biểu cho bi kịch của người trí thức tiểu tư sản nghèo đói: anh Hộ - một nhà văn có tài năng và hoài bão, nhưng bị gánh nặng cơm áo gạo tiền làm thui chột lý tưởng, phải viết những tác phẩm \"thừa\", tầm thường.',	'APPROVED',	'2025-12-12 13:35:49.863871',	'2025-12-12 13:40:37.537793'),
(33,	19,	44,	'Tiểu thuyết về cuộc sống của tầng lớp trí thức nghèo, qua nhân vật Thứ. Tác phẩm khắc họa sự bế tắc, tuyệt vọng và tình trạng sống mòn (cuộc sống vô vị, ngày qua ngày bị bào mòn) trong xã hội cũ.',	'APPROVED',	'2025-12-12 13:36:55.725190',	'2025-12-12 13:40:34.849737'),
(34,	19,	45,	'Truyện ngắn thể hiện sự giằng xé nội tâm của người nghệ sĩ: muốn sống lãng mạn, mơ mộng (nhìn trăng sáng) nhưng lại bị hiện thực tăm tối, nghèo khổ (những mảnh đời cơ cực xung quanh) kéo lại.',	'APPROVED',	'2025-12-12 13:38:41.446498',	'2025-12-12 13:40:32.179762'),
(35,	19,	46,	'Truyện ngắn phơi bày thảm cảnh của người nghèo bị áp bức đến mức nhân phẩm bị chà đạp. Bà lão Hợi và con trai phải ăn một bữa no cuối cùng trước khi bị đuổi khỏi nhà, gợi lên sự phẫn uất.',	'APPROVED',	'2025-12-12 13:40:17.133265',	'2025-12-12 13:40:28.850134');
-- 2025-12-12 14:36:29 UTC

-- ===========================================
-- BOOK CATEGORY SAMPLE
-- ===========================================
SET NAMES utf8mb4;

INSERT INTO book_category (bookID, categoryID) VALUES
(4,	1),
(36, 1),
(39, 1),
(41, 1),
(42, 1),
(2,	2),
(1,	3),
(8,	3),
(29, 3),
(34, 3),
(6,	4),
(7,	5),
(38, 9),
(40, 13),
(44, 13),
(43, 14),
(45, 14),
(46, 14);
-- 2025-12-12 14:37:49 UTC

-- ===========================================
-- REPORT SAMPLE
-- ===========================================
INSERT INTO report (reportID, postID, adminID, reason, status, report_date) VALUES
(1,10,1,'Nội dung mập mờ, cần kiểm tra','OPEN',NULL),
(2,4,1,'Tình trạng sách không đúng mô tả','RESOLVED',NULL);