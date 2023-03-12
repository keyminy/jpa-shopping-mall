package com.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.entity.ItemImg;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long>{
	//상품 이미지 아이디의 오름차순으로 가져오는 쿼리메소드
	List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
	/* <ItemServiceTest>
	 select
        itemimg0_.item_img_id as item_img1_3_,
        itemimg0_.reg_time as reg_time2_3_,
        itemimg0_.update_time as update_t3_3_,
        itemimg0_.created_by as created_4_3_,
        itemimg0_.modified_by as modified5_3_,
        itemimg0_.img_name as img_name6_3_,
        itemimg0_.img_url as img_url7_3_,
        itemimg0_.item_id as item_id10_3_,
        itemimg0_.ori_img_name as ori_img_8_3_,
        itemimg0_.repimg_yn as repimg_y9_3_ 
    from
        item_img itemimg0_ 
    left outer join
        item item1_ 
            on itemimg0_.item_id=item1_.item_id 
    where
        item1_.item_id=? 
    order by
        itemimg0_.item_img_id asc
	 * */
	
	//상품의 대표 이미지 찾기
	ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);
}
