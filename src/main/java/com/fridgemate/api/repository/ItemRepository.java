package com.fridgemate.api.repository;

import com.fridgemate.api.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 키워드로 상품명 검색 (대소문자 무시) - 페이징
     */
    Page<Item> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Item> findByNameContainingIgnoreCase(String keyword, Sort sort);

    /**
     * 카테고리별 조회 - 페이징 지원
     */
    Page<Item> findByCategory(String category, Pageable pageable);
    List<Item> findByCategory(String category, Sort sort);

    /**
     * 보관 위치별 조회 - 페이징 지원
     */
    Page<Item> findByLocation(String location, Pageable pageable);
    List<Item> findByLocation(String location, Sort sort);

    /**
     * 카테고리와 위치로 조회 - 페이징 지원
     */
    Page<Item> findByCategoryAndLocation(String category, String location, Pageable pageable);
    List<Item> findByCategoryAndLocation(String category, String location, Sort sort);

    /**
     * 유통기한 임박 상품 조회 (지정된 일수 이내)
     */
    @Query("SELECT i FROM Item i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= :targetDate ORDER BY i.expiryDate ASC")
    List<Item> findExpiringItems(@Param("targetDate") LocalDate targetDate);

    /**
     * 재고 부족 아이템 조회
     */
    @Query("SELECT i FROM Item i WHERE i.quantity <= :threshold")
    List<Item> findLowStockItems(@Param("threshold") double threshold);

    /**
     * 복합 조건 검색
     */
    @Query("SELECT i FROM Item i WHERE " +
            "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:location IS NULL OR i.location = :location) AND " +
            "(:expiring IS NULL OR (i.expiryDate IS NOT NULL AND i.expiryDate <= :expiringDate))")
    List<Item> findBySearchCriteria(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("location") String location,
            @Param("expiring") Integer expiring,
            @Param("expiringDate") LocalDate expiringDate,
            Sort sort);

    /**
     * 모든 아이템 조회 (정렬 적용)
     */
    List<Item> findAll(Sort sort);

    /**
     * 카테고리 목록 조회
     */
    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL ORDER BY i.category")
    List<String> findDistinctCategories();

    /**
     * 보관 위치 목록 조회
     */
    @Query("SELECT DISTINCT i.location FROM Item i WHERE i.location IS NOT NULL ORDER BY i.location")
    List<String> findDistinctLocations();

    /**
     * 재고가 있는 아이템만 조회 (레시피 추천용)
     */
    @Query("SELECT i FROM Item i WHERE i.quantity > 0")
    List<Item> findAvailableItems();

    /**
     * 카테고리별 아이템 수 조회
     */
    long countByCategory(String category);

    /**
     * 위치별 아이템 수 조회
     */
    long countByLocation(String location);

    /**
     * 카테고리별 통계 조회
     */
    @Query("SELECT i.category, COUNT(i), SUM(i.quantity) FROM Item i WHERE i.category IS NOT NULL GROUP BY i.category ORDER BY i.category")
    List<Object[]> getCategoryStats();

    /**
     * 위치별 통계 조회
     */
    @Query("SELECT i.location, COUNT(i), SUM(i.quantity) FROM Item i WHERE i.location IS NOT NULL GROUP BY i.location ORDER BY i.location")
    List<Object[]> getLocationStats();
}