현재 진행중인 코드입니다.

코드 작업상 특이사항:
1.controller 에는 Dto 데이터 입력과 Service 실행만 허용합니다.

1-1.RestData 객체는 빈으로 등록해서 사용하지 않습니다.
>> 싱글톤으로 사용하면 스레드 처리 순서에 따라서 엄청난 혼란을 야기할 수 있습니다.
>> 멀티톤으로 사용한다면 Bean을 위해서 reflection 을 사용하는 것이 무의미 하므로 new로 할당합니다.

2.SQL을 DB에 직접 작성해서 사용해야 한다면 꼭 Parameter Binding을 활용해야 합니다.
>> jpa는 Parameter Binding을 활용하므로 SQLInjection으로 부터 보호받을 수 있으나
>> 사용자 정의 SQL에 의해서 보안의 구멍이 발생할 수 있습니다.  

보안사항 : XSS / CSRF / SQLInjection

남아있는 작업 : 
main view controller 에서 item 삭제 복구 수정 대응
order view controller 에서 생성 , 수정 , 실행 뷰 와 실행 결과 기록에 대응
schadule 로 새벽 4시에 하루 한번씩 삭제 예약 아이템들을 삭제

프론트 : 리프레시 구현 , 페이징 처리, orderview 와 삭제예약 생성