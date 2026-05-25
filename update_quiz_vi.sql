-- ============================================================
-- Thêm columns tiếng Việt vào bảng quiz_questions
-- (Đã chạy ALTER TABLE riêng trước đó)
-- ============================================================

-- ============================================================
-- Điền dữ liệu tiếng Việt — Nhóm 1: Câu hội thoại (id 1-23)
-- ============================================================
UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi đang đi đến trường.',
    wrong_answer_1_vi = 'Tôi học ở trường.',
    wrong_answer_2_vi = 'Tôi thích trường học.',
    wrong_answer_3_vi = 'Tôi về nhà.'
WHERE id = 1;

UPDATE quiz_questions SET
    correct_answer_vi = 'Quả táo rất ngon.',
    wrong_answer_1_vi = 'Quả táo rất đắt.',
    wrong_answer_2_vi = 'Tôi muốn ăn táo.',
    wrong_answer_3_vi = 'Táo rất tốt cho sức khỏe.'
WHERE id = 2;

UPDATE quiz_questions SET
    correct_answer_vi = 'Cái này bao nhiêu tiền?',
    wrong_answer_1_vi = 'Cái này ở đâu?',
    wrong_answer_2_vi = 'Cái này là gì?',
    wrong_answer_3_vi = 'Tôi có thể thử không?'
WHERE id = 3;

UPDATE quiz_questions SET
    correct_answer_vi = 'Cho tôi xem thực đơn được không?',
    wrong_answer_1_vi = 'Nhà hàng ở đâu?',
    wrong_answer_2_vi = 'Tôi có thể thanh toán ngay bây giờ không?',
    wrong_answer_3_vi = 'Bạn có món chay không?'
WHERE id = 4;

UPDATE quiz_questions SET
    correct_answer_vi = 'Cho tôi 2 phần bulgogi.',
    wrong_answer_1_vi = 'Tôi không thích bulgogi.',
    wrong_answer_2_vi = 'Bulgogi rất cay.',
    wrong_answer_3_vi = 'Cho tôi 1 phần kimchi.'
WHERE id = 5;

UPDATE quiz_questions SET
    correct_answer_vi = 'Kimchi này quá cay.',
    wrong_answer_1_vi = 'Kimchi này rất ngon.',
    wrong_answer_2_vi = 'Tôi muốn thêm kimchi.',
    wrong_answer_3_vi = 'Canh này quá mặn.'
WHERE id = 6;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi uống nước.',
    wrong_answer_1_vi = 'Tôi ăn cơm.',
    wrong_answer_2_vi = 'Tôi rửa tay.',
    wrong_answer_3_vi = 'Tôi làm đổ nước.'
WHERE id = 7;

UPDATE quiz_questions SET
    correct_answer_vi = 'Canh này không ngon.',
    wrong_answer_1_vi = 'Canh này rất ngon.',
    wrong_answer_2_vi = 'Tôi muốn ăn canh.',
    wrong_answer_3_vi = 'Canh này quá nóng.'
WHERE id = 8;

UPDATE quiz_questions SET
    correct_answer_vi = 'Hãy đến sân bay.',
    wrong_answer_1_vi = 'Hãy đến khách sạn.',
    wrong_answer_2_vi = 'Ga tàu ở đâu?',
    wrong_answer_3_vi = 'Tôi đang ở sân bay.'
WHERE id = 9;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi bị mất hộ chiếu.',
    wrong_answer_1_vi = 'Tôi tìm thấy hộ chiếu.',
    wrong_answer_2_vi = 'Đây là vé của tôi.',
    wrong_answer_3_vi = 'Túi của tôi ở đâu?'
WHERE id = 10;

UPDATE quiz_questions SET
    correct_answer_vi = 'Vé này quá đắt.',
    wrong_answer_1_vi = 'Vé này rẻ.',
    wrong_answer_2_vi = 'Tôi muốn mua vé.',
    wrong_answer_3_vi = 'Tôi có thể mua vé ở đâu?'
WHERE id = 11;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi đã đặt khách sạn.',
    wrong_answer_1_vi = 'Tôi đang rời khách sạn.',
    wrong_answer_2_vi = 'Khách sạn ở đâu?',
    wrong_answer_3_vi = 'Khách sạn rất đắt.'
WHERE id = 12;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tàu đến lúc 3 giờ.',
    wrong_answer_1_vi = 'Tàu khởi hành lúc 3 giờ.',
    wrong_answer_2_vi = 'Tôi lỡ chuyến tàu 3 giờ.',
    wrong_answer_3_vi = 'Xe buýt đến lúc 3 giờ.'
WHERE id = 13;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi đi làm lúc 9 giờ sáng.',
    wrong_answer_1_vi = 'Tôi tan làm lúc 9 giờ tối.',
    wrong_answer_2_vi = 'Tôi ăn sáng lúc 9 giờ.',
    wrong_answer_3_vi = 'Tôi có cuộc họp lúc 9 giờ.'
WHERE id = 14;

UPDATE quiz_questions SET
    correct_answer_vi = 'Hôm nay tôi muốn về sớm.',
    wrong_answer_1_vi = 'Hôm nay tôi có nhiều việc.',
    wrong_answer_2_vi = 'Hôm nay tôi đi làm muộn.',
    wrong_answer_3_vi = 'Hôm nay tôi làm thêm giờ.'
WHERE id = 15;

UPDATE quiz_questions SET
    correct_answer_vi = 'Hãy photo tài liệu.',
    wrong_answer_1_vi = 'Hãy in email.',
    wrong_answer_2_vi = 'Tài liệu ở đâu?',
    wrong_answer_3_vi = 'Tôi bị mất file.'
WHERE id = 16;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi đang họp.',
    wrong_answer_1_vi = 'Cuộc họp bị hủy.',
    wrong_answer_2_vi = 'Hãy lên lịch họp.',
    wrong_answer_3_vi = 'Phòng họp ở đâu?'
WHERE id = 17;

UPDATE quiz_questions SET
    correct_answer_vi = 'Để tôi giới thiệu đồng nghiệp.',
    wrong_answer_1_vi = 'Đồng nghiệp tôi vắng mặt.',
    wrong_answer_2_vi = 'Tôi thích đồng nghiệp.',
    wrong_answer_3_vi = 'Đây là sếp của tôi.'
WHERE id = 18;

UPDATE quiz_questions SET
    correct_answer_vi = 'Hôm nay tôi rất mệt.',
    wrong_answer_1_vi = 'Hôm nay tôi rất vui.',
    wrong_answer_2_vi = 'Hôm nay tôi rất bận.',
    wrong_answer_3_vi = 'Hôm nay là ngày tốt.'
WHERE id = 19;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi hạnh phúc khi ở bên gia đình.',
    wrong_answer_1_vi = 'Tôi nhớ gia đình.',
    wrong_answer_2_vi = 'Gia đình tôi đông người.',
    wrong_answer_3_vi = 'Tôi đang đi du lịch cùng gia đình.'
WHERE id = 20;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tôi tức giận khi nghe lời anh ấy.',
    wrong_answer_1_vi = 'Tôi buồn khi nghe tin.',
    wrong_answer_2_vi = 'Tôi không nghe thấy anh ấy nói gì.',
    wrong_answer_3_vi = 'Anh ấy rất tức giận.'
WHERE id = 21;

UPDATE quiz_questions SET
    correct_answer_vi = 'Bộ phim này thực sự buồn.',
    wrong_answer_1_vi = 'Bộ phim này thực sự vui.',
    wrong_answer_2_vi = 'Tôi muốn xem phim.',
    wrong_answer_3_vi = 'Bài hát này buồn.'
WHERE id = 22;

UPDATE quiz_questions SET
    correct_answer_vi = 'Cuối tuần tôi rất chán.',
    wrong_answer_1_vi = 'Cuối tuần tôi rất bận.',
    wrong_answer_2_vi = 'Cuối tuần trôi qua nhanh.',
    wrong_answer_3_vi = 'Cuối tuần tôi vui lắm.'
WHERE id = 23;

-- ============================================================
-- Nhóm 2: Từ vựng mua sắm (id 24-43) — dạng "What is the meaning of..."
-- ============================================================
UPDATE quiz_questions SET
    correct_answer_vi = 'Cửa hàng',
    wrong_answer_1_vi = 'Chợ',
    wrong_answer_2_vi = 'Trung tâm thương mại',
    wrong_answer_3_vi = 'Đồ vật / Hàng hóa'
WHERE id = 24;

UPDATE quiz_questions SET
    correct_answer_vi = 'Chợ',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Trung tâm thương mại',
    wrong_answer_3_vi = 'Đồ vật / Hàng hóa'
WHERE id = 25;

UPDATE quiz_questions SET
    correct_answer_vi = 'Trung tâm thương mại',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Đồ vật / Hàng hóa'
WHERE id = 26;

UPDATE quiz_questions SET
    correct_answer_vi = 'Đồ vật / Hàng hóa',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 27;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tiền',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 28;

UPDATE quiz_questions SET
    correct_answer_vi = 'Giá cả',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 29;

UPDATE quiz_questions SET
    correct_answer_vi = 'Giá cả',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 30;

UPDATE quiz_questions SET
    correct_answer_vi = 'Hóa đơn',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 31;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tiền thối',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 32;

UPDATE quiz_questions SET
    correct_answer_vi = 'Tiền mặt',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 33;

UPDATE quiz_questions SET
    correct_answer_vi = 'Thẻ tín dụng',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 34;

UPDATE quiz_questions SET
    correct_answer_vi = 'Ví tiền',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 35;

UPDATE quiz_questions SET
    correct_answer_vi = 'Quần áo',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 36;

UPDATE quiz_questions SET
    correct_answer_vi = 'Giày',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 37;

UPDATE quiz_questions SET
    correct_answer_vi = 'Túi xách',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 38;

UPDATE quiz_questions SET
    correct_answer_vi = 'Đắt',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 39;

UPDATE quiz_questions SET
    correct_answer_vi = 'Rẻ',
    wrong_answer_1_vi = 'Cửa hàng',
    wrong_answer_2_vi = 'Chợ',
    wrong_answer_3_vi = 'Trung tâm thương mại'
WHERE id = 40;

-- id 41-43: mua sắm (động từ)
UPDATE quiz_questions SET correct_answer_vi='Mua', wrong_answer_1_vi='Cửa hàng', wrong_answer_2_vi='Chợ', wrong_answer_3_vi='Trung tâm thương mại' WHERE id=41;
UPDATE quiz_questions SET correct_answer_vi='Bán', wrong_answer_1_vi='Cửa hàng', wrong_answer_2_vi='Chợ', wrong_answer_3_vi='Trung tâm thương mại' WHERE id=42;
UPDATE quiz_questions SET correct_answer_vi='Thanh toán', wrong_answer_1_vi='Cửa hàng', wrong_answer_2_vi='Chợ', wrong_answer_3_vi='Trung tâm thương mại' WHERE id=43;

-- id 44-63: thời tiết
UPDATE quiz_questions SET correct_answer_vi='Thời tiết', wrong_answer_1_vi='Mưa', wrong_answer_2_vi='Tuyết', wrong_answer_3_vi='Gió' WHERE id=44;
UPDATE quiz_questions SET correct_answer_vi='Mưa', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Tuyết', wrong_answer_3_vi='Gió' WHERE id=45;
UPDATE quiz_questions SET correct_answer_vi='Tuyết', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Gió' WHERE id=46;
UPDATE quiz_questions SET correct_answer_vi='Gió', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=47;
UPDATE quiz_questions SET correct_answer_vi='Mây', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=48;
UPDATE quiz_questions SET correct_answer_vi='Bầu trời', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=49;
UPDATE quiz_questions SET correct_answer_vi='Mặt trời', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=50;
UPDATE quiz_questions SET correct_answer_vi='Mùa xuân', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=51;
UPDATE quiz_questions SET correct_answer_vi='Mùa hè', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=52;
UPDATE quiz_questions SET correct_answer_vi='Mùa thu', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=53;
UPDATE quiz_questions SET correct_answer_vi='Mùa đông', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=54;
UPDATE quiz_questions SET correct_answer_vi='Trời nắng', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=55;
UPDATE quiz_questions SET correct_answer_vi='Trời nhiều mây', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=56;
UPDATE quiz_questions SET correct_answer_vi='Nóng', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=57;
UPDATE quiz_questions SET correct_answer_vi='Lạnh', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=58;
UPDATE quiz_questions SET correct_answer_vi='Ấm áp', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=59;
UPDATE quiz_questions SET correct_answer_vi='Mát mẻ', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=60;
UPDATE quiz_questions SET correct_answer_vi='Nhiệt độ', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=61;
UPDATE quiz_questions SET correct_answer_vi='Ô / Dù', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=62;
UPDATE quiz_questions SET correct_answer_vi='Mùa', wrong_answer_1_vi='Thời tiết', wrong_answer_2_vi='Mưa', wrong_answer_3_vi='Tuyết' WHERE id=63;

-- id 64-83: sở thích
UPDATE quiz_questions SET correct_answer_vi='Sở thích', wrong_answer_1_vi='Âm nhạc', wrong_answer_2_vi='Phim', wrong_answer_3_vi='Đọc sách' WHERE id=64;
UPDATE quiz_questions SET correct_answer_vi='Âm nhạc', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Phim', wrong_answer_3_vi='Đọc sách' WHERE id=65;
UPDATE quiz_questions SET correct_answer_vi='Phim', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Đọc sách' WHERE id=66;
UPDATE quiz_questions SET correct_answer_vi='Đọc sách', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=67;
UPDATE quiz_questions SET correct_answer_vi='Tập thể dục', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=68;
UPDATE quiz_questions SET correct_answer_vi='Bài hát', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=69;
UPDATE quiz_questions SET correct_answer_vi='Nhảy múa', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=70;
UPDATE quiz_questions SET correct_answer_vi='Ảnh / Chụp ảnh', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=71;
UPDATE quiz_questions SET correct_answer_vi='Vẽ tranh', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=72;
UPDATE quiz_questions SET correct_answer_vi='Trò chơi', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=73;
UPDATE quiz_questions SET correct_answer_vi='Du lịch', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=74;
UPDATE quiz_questions SET correct_answer_vi='Nấu ăn', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=75;
UPDATE quiz_questions SET correct_answer_vi='Đàn piano', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=76;
UPDATE quiz_questions SET correct_answer_vi='Đàn guitar', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=77;
UPDATE quiz_questions SET correct_answer_vi='Leo núi', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=78;
UPDATE quiz_questions SET correct_answer_vi='Bơi lội', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=79;
UPDATE quiz_questions SET correct_answer_vi='Câu cá', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=80;
UPDATE quiz_questions SET correct_answer_vi='Tem thư', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=81;
UPDATE quiz_questions SET correct_answer_vi='Sưu tập', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=82;
UPDATE quiz_questions SET correct_answer_vi='Truyện tranh', wrong_answer_1_vi='Sở thích', wrong_answer_2_vi='Âm nhạc', wrong_answer_3_vi='Phim' WHERE id=83;

-- id 84-103: gia đình
UPDATE quiz_questions SET correct_answer_vi='Gia đình', wrong_answer_1_vi='Bố mẹ', wrong_answer_2_vi='Bố', wrong_answer_3_vi='Mẹ' WHERE id=84;
UPDATE quiz_questions SET correct_answer_vi='Bố mẹ', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố', wrong_answer_3_vi='Mẹ' WHERE id=85;
UPDATE quiz_questions SET correct_answer_vi='Bố', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Mẹ' WHERE id=86;
UPDATE quiz_questions SET correct_answer_vi='Mẹ', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=87;
UPDATE quiz_questions SET correct_answer_vi='Anh trai (của nam)', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=88;
UPDATE quiz_questions SET correct_answer_vi='Anh trai (của nữ)', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=89;
UPDATE quiz_questions SET correct_answer_vi='Chị gái (của nam)', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=90;
UPDATE quiz_questions SET correct_answer_vi='Chị gái (của nữ)', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=91;
UPDATE quiz_questions SET correct_answer_vi='Em (trai/gái)', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=92;
UPDATE quiz_questions SET correct_answer_vi='Em trai', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=93;
UPDATE quiz_questions SET correct_answer_vi='Em gái', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=94;
UPDATE quiz_questions SET correct_answer_vi='Ông nội/ngoại', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=95;
UPDATE quiz_questions SET correct_answer_vi='Bà nội/ngoại', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=96;
UPDATE quiz_questions SET correct_answer_vi='Con trai', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=97;
UPDATE quiz_questions SET correct_answer_vi='Con gái', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=98;
UPDATE quiz_questions SET correct_answer_vi='Chồng', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=99;
UPDATE quiz_questions SET correct_answer_vi='Vợ', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=100;
UPDATE quiz_questions SET correct_answer_vi='Vợ chồng', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=101;
UPDATE quiz_questions SET correct_answer_vi='Họ hàng', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=102;
UPDATE quiz_questions SET correct_answer_vi='Em bé', wrong_answer_1_vi='Gia đình', wrong_answer_2_vi='Bố mẹ', wrong_answer_3_vi='Bố' WHERE id=103;

-- id 104-123: giao thông
UPDATE quiz_questions SET correct_answer_vi='Giao thông / Phương tiện', wrong_answer_1_vi='Xe hơi', wrong_answer_2_vi='Xe buýt', wrong_answer_3_vi='Tàu hỏa' WHERE id=104;
UPDATE quiz_questions SET correct_answer_vi='Xe hơi', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe buýt', wrong_answer_3_vi='Tàu hỏa' WHERE id=105;
UPDATE quiz_questions SET correct_answer_vi='Xe buýt', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Tàu hỏa' WHERE id=106;
UPDATE quiz_questions SET correct_answer_vi='Tàu hỏa', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=107;
UPDATE quiz_questions SET correct_answer_vi='Tàu điện ngầm', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=108;
UPDATE quiz_questions SET correct_answer_vi='Máy bay', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=109;
UPDATE quiz_questions SET correct_answer_vi='Tàu thuyền', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=110;
UPDATE quiz_questions SET correct_answer_vi='Xe đạp', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=111;
UPDATE quiz_questions SET correct_answer_vi='Taxi', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=112;
UPDATE quiz_questions SET correct_answer_vi='Xe máy', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=113;
UPDATE quiz_questions SET correct_answer_vi='Ga / Trạm', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=114;
UPDATE quiz_questions SET correct_answer_vi='Trạm xe buýt', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=115;
UPDATE quiz_questions SET correct_answer_vi='Sân bay', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=116;
UPDATE quiz_questions SET correct_answer_vi='Vé', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=117;
UPDATE quiz_questions SET correct_answer_vi='Lên xe / Đi', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=118;
UPDATE quiz_questions SET correct_answer_vi='Xuống xe', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=119;
UPDATE quiz_questions SET correct_answer_vi='Đi bộ', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=120;
UPDATE quiz_questions SET correct_answer_vi='Đường / Phố', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=121;
UPDATE quiz_questions SET correct_answer_vi='Đèn giao thông', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=122;
UPDATE quiz_questions SET correct_answer_vi='Lái xe', wrong_answer_1_vi='Giao thông', wrong_answer_2_vi='Xe hơi', wrong_answer_3_vi='Xe buýt' WHERE id=123;
