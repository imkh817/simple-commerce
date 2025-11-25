package project.simple_commerce.delivery.enums;

public enum DeliveryStatus {
    READY, // 배송 준비 중(출고 대기)
    PICKED_UP, // 택배기사가 상품 수거
    IN_TRANSIT, // 배송 중
    OUT_FOR_DELIVERY, // 배송 출발
    DELIVERED, // 배송 완료
    CANCELED, // 배송 취소 (고객 요청 or 시스템 취소 등)
    RETURN_REQUESTED, // 반품 요청 (고객이 반품 신청)
    RETURNING, // 반품 중 (회수 기사 이동)
    RETURNED, // 반품 완료
    DELIVERY_FAILED // 배송 실패 (주소 불명, 고객 부재 등으로 실패)
}
