#!/bin/bash

# MeowTown 개발 환경 Docker 관리 스크립트

set -e

COMPOSE_FILE="docker-compose.yml"
PROJECT_NAME="meowtown"

# 색상 출력을 위한 함수
print_info() {
    echo -e "\033[36m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[32m[SUCCESS]\033[0m $1"
}

print_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

print_warning() {
    echo -e "\033[33m[WARNING]\033[0m $1"
}

# 도움말 출력
show_help() {
    echo "MeowTown 개발 환경 관리 스크립트"
    echo ""
    echo "사용법: $0 [COMMAND]"
    echo ""
    echo "COMMANDS:"
    echo "  start     개발 환경 시작 (PostgreSQL, Redis, 관리도구 포함)"
    echo "  stop      개발 환경 중지"
    echo "  restart   개발 환경 재시작"
    echo "  status    서비스 상태 확인"
    echo "  logs      로그 확인"
    echo "  clean     모든 데이터 삭제 및 정리"
    echo "  db-only   데이터베이스만 시작 (PostgreSQL, Redis)"
    echo "  reset     데이터베이스 리셋"
    echo "  help      이 도움말 출력"
    echo ""
    echo "예시:"
    echo "  $0 start       # 전체 개발 환경 시작"
    echo "  $0 db-only     # DB만 시작"
    echo "  $0 logs        # 실시간 로그 확인"
}

# 서비스 시작
start_services() {
    print_info "MeowTown 개발 환경을 시작합니다..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d
    
    print_info "서비스 상태 확인 중..."
    sleep 5
    
    # 헬스체크 대기
    print_info "데이터베이스 연결 대기 중..."
    timeout 60 bash -c 'until docker-compose -p meowtown exec -T meowtown-postgres pg_isready -U meowtown -d meowtown_dev; do sleep 2; done'
    
    print_success "MeowTown 개발 환경이 시작되었습니다!"
    print_info "서비스 접속 정보:"
    echo "  🐘 PostgreSQL:   localhost:5432"
    echo "     데이터베이스: meowtown_dev"
    echo "     사용자명:     meowtown"
    echo "     비밀번호:     meowtown123"
    echo ""
    echo "  🔴 Redis:        localhost:6379"
    echo ""
    echo "  🌐 pgAdmin:      http://localhost:8081"
    echo "     이메일:       admin@meowtown.local"
    echo "     비밀번호:     admin123"
    echo ""
    echo "  🔍 Redis Insight: http://localhost:8082"
    echo ""
    echo "Spring Boot 애플리케이션을 시작하려면:"
    echo "  ./gradlew bootRun"
}

# DB만 시작
start_db_only() {
    print_info "데이터베이스 서비스만 시작합니다..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d meowtown-postgres meowtown-redis
    
    print_info "데이터베이스 연결 대기 중..."
    timeout 60 bash -c 'until docker-compose -p meowtown exec -T meowtown-postgres pg_isready -U meowtown -d meowtown_dev; do sleep 2; done'
    
    print_success "데이터베이스 서비스가 시작되었습니다!"
    print_info "PostgreSQL: localhost:5432"
    print_info "Redis: localhost:6379"
}

# 서비스 중지
stop_services() {
    print_info "MeowTown 개발 환경을 중지합니다..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down
    print_success "개발 환경이 중지되었습니다."
}

# 서비스 재시작
restart_services() {
    print_info "MeowTown 개발 환경을 재시작합니다..."
    stop_services
    start_services
}

# 상태 확인
check_status() {
    print_info "서비스 상태:"
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME ps
}

# 로그 확인
show_logs() {
    print_info "실시간 로그를 확인합니다 (Ctrl+C로 종료)..."
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f
}

# 데이터 정리
clean_all() {
    print_warning "모든 데이터를 삭제합니다. 계속하시겠습니까? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_info "서비스 중지 및 데이터 삭제 중..."
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down -v --remove-orphans
        docker volume prune -f
        print_success "모든 데이터가 삭제되었습니다."
    else
        print_info "작업이 취소되었습니다."
    fi
}

# 데이터베이스 리셋
reset_database() {
    print_warning "데이터베이스를 리셋합니다. 모든 데이터가 삭제됩니다. 계속하시겠습니까? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_info "데이터베이스 리셋 중..."
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME stop meowtown-postgres
        docker volume rm ${PROJECT_NAME}_postgres_data 2>/dev/null || true
        docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d meowtown-postgres
        print_success "데이터베이스가 리셋되었습니다."
    else
        print_info "작업이 취소되었습니다."
    fi
}

# 메인 로직
case "${1:-help}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    status)
        check_status
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_all
        ;;
    db-only)
        start_db_only
        ;;
    reset)
        reset_database
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "알 수 없는 명령어: $1"
        echo ""
        show_help
        exit 1
        ;;
esac