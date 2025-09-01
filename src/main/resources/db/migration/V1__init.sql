-- FridgeMate 초기 테이블 생성

-- 재고 아이템 테이블
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity NUMERIC(15,3) NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    unit VARCHAR(20) NOT NULL,
    expiry_date DATE,
    category VARCHAR(30),
    location VARCHAR(20),
    memo TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 인덱스 생성
CREATE INDEX idx_items_expiry ON items(expiry_date);
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_category ON items(category);
CREATE INDEX idx_items_location ON items(location);

-- 업데이트 시간 자동 갱신 함수
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 업데이트 트리거
CREATE TRIGGER update_items_updated_at
    BEFORE UPDATE ON items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at();

-- 샘플 데이터 삽입
INSERT INTO items (name, quantity, unit, expiry_date, category, location, memo) VALUES
('계란', 10, '개', CURRENT_DATE + INTERVAL '7 days', '유제품', '냉장', '신선한 계란'),
('우유', 1, 'L', CURRENT_DATE + INTERVAL '5 days', '유제품', '냉장', '저지방 우유'),
('양파', 3, '개', CURRENT_DATE + INTERVAL '14 days', '채소', '실온', '국산 양파'),
('당근', 5, '개', CURRENT_DATE + INTERVAL '10 days', '채소', '냉장', '유기농 당근'),
('닭가슴살', 500, 'g', CURRENT_DATE + INTERVAL '3 days', '육류', '냉장', '신선한 닭가슴살'),
('밥', 2, '공기', CURRENT_DATE + INTERVAL '1 days', '곡물', '냉장', '밥솥에서 지은 밥'),
('대파', 2, '줄기', CURRENT_DATE + INTERVAL '5 days', '채소', '냉장', '국산 대파'),
('마늘', 1, '통', CURRENT_DATE + INTERVAL '30 days', '채소', '실온', '깐마늘'),
('간장', 1, '병', CURRENT_DATE + INTERVAL '365 days', '소스', '실온', '진간장'),
('식용유', 1, '병', CURRENT_DATE + INTERVAL '180 days', '소스', '실온', '콩기름');

-- MySQL 호환성을 위한 주석
-- MySQL 사용시 변경사항:
-- 1. BIGSERIAL -> BIGINT AUTO_INCREMENT
-- 2. NUMERIC -> DECIMAL
-- 3. TIMESTAMPTZ -> TIMESTAMP
-- 4. NOW() -> CURRENT_TIMESTAMP
-- 5. 함수 및 트리거 구문 변경 필요
