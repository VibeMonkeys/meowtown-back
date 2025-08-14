-- MeowTown Database 초기화 스크립트

-- PostGIS 확장 설치
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS postgis_sfcgal;
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;

-- UUID 확장 설치
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 데이터베이스 설정
SET timezone = 'Asia/Seoul';

-- 공간 데이터 테이블 생성을 위한 권한 부여
GRANT ALL PRIVILEGES ON DATABASE meowtown_dev TO meowtown;

-- 확장 확인
SELECT name, default_version, installed_version 
FROM pg_available_extensions 
WHERE name IN ('postgis', 'uuid-ossp')
ORDER BY name;

-- PostGIS 버전 확인
SELECT PostGIS_Version();

-- 초기 데이터베이스 설정 완료 메시지
DO $$
BEGIN
    RAISE NOTICE 'MeowTown Database initialized successfully!';
    RAISE NOTICE 'PostGIS extension installed';
    RAISE NOTICE 'UUID extension installed';
    RAISE NOTICE 'Timezone set to Asia/Seoul';
END $$;