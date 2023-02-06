package com.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long>{
	List<Item> findByItemNm(String ItemNm);

	List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
	
	/* JPQL 사용 */
	@Query("SELECT i FROM Item i "
			+ "WHERE i.itemDetail "
			+ "LIKE %:itemDetail% "
			+ "ORDER BY i.price DESC")
	List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
}
