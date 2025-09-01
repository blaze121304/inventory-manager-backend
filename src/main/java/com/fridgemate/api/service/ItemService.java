package com.fridgemate.api.service;

import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.ItemCreateReq;
import com.fridgemate.api.dto.ItemResp;
import com.fridgemate.api.dto.ItemUpdateReq;
import com.fridgemate.api.dto.ConsumeReq;
import com.fridgemate.api.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 아이템 목록 조회 (페이징)
     */
    public Page<ItemResp> getItems(
            String category,
            String location,
            String sortBy,
            String sortDir,
            Pageable pageable) {

        // 정렬 조건이 있으면 새로운 Pageable 생성
        if (sortBy != null) {
            Sort sort = createSort(sortBy, sortDir);
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        Page<Item> items;
        if (category != null && location != null) {
            items = itemRepository.findByCategoryAndLocation(category, location, pageable);
        } else if (category != null) {
            items = itemRepository.findByCategory(category, pageable);
        } else if (location != null) {
            items = itemRepository.findByLocation(location, pageable);
        } else {
            items = itemRepository.findAll(pageable);
        }

        return items.map(this::toItemResp);
    }

    /**
     * 아이템 상세 조회
     */
    public ItemResp getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다. ID: " + id));
        return toItemResp(item);
    }

    /**
     * 아이템 생성
     */
    @Transactional
    public ItemResp createItem(ItemCreateReq request) {
        Item item = Item.builder()
                .name(request.getName())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expiryDate(request.getExpiryDate())
                .location(request.getLocation())
                .purchaseDate(LocalDate.now())
                .memo(request.getMemo())
                .build();

        Item savedItem = itemRepository.save(item);
        return toItemResp(savedItem);
    }

    /**
     * 아이템 수정
     */
    @Transactional
    public ItemResp updateItem(Long id, ItemUpdateReq request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다. ID: " + id));

        item.updateItem(
                request.getName(),
                request.getCategory(),
                request.getQuantity(),
                request.getUnit(),
                request.getExpiryDate(),
                request.getLocation()
        );

        Item savedItem = itemRepository.save(item);
        return toItemResp(savedItem);
    }

    /**
     * 아이템 삭제
     */
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("아이템을 찾을 수 없습니다. ID: " + id);
        }
        itemRepository.deleteById(id);
    }

    /**
     * 아이템 소비
     */
    @Transactional
    public ItemResp consumeItem(Long id, ConsumeReq request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다. ID: " + id));

        if (item.getQuantity().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("소비할 수량이 보유 수량보다 많습니다.");
        }

        item.consume(request.getAmount());
        Item savedItem = itemRepository.save(item);
        return toItemResp(savedItem);
    }

    /**
     * 곧 만료될 아이템 조회
     */
    public List<ItemResp> getExpiringItems(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        List<Item> items = itemRepository.findExpiringItems(targetDate);
        return items.stream()
                .map(this::toItemResp)
                .toList();
    }

    /**
     * 재고 부족 아이템 조회
     */
    public List<ItemResp> getLowStockItems(double threshold) {
        List<Item> items = itemRepository.findLowStockItems(threshold);
        return items.stream()
                .map(this::toItemResp)
                .toList();
    }

    /**
     * 재고가 있는 아이템 조회 (레시피 추천용)
     */
    public List<ItemResp> getAvailableItems() {
        List<Item> items = itemRepository.findAvailableItems();
        return items.stream()
                .map(this::toItemResp)
                .toList();
    }

    /**
     * 카테고리 목록 조회
     */
    public List<String> getCategories() {
        return itemRepository.findDistinctCategories();
    }

    /**
     * 위치 목록 조회
     */
    public List<String> getLocations() {
        return itemRepository.findDistinctLocations();
    }

    /**
     * 카테고리별 아이템 통계
     */
    public List<Object[]> getCategoryStats() {
        return itemRepository.getCategoryStats();
    }

    /**
     * 위치별 아이템 통계
     */
    public List<Object[]> getLocationStats() {
        return itemRepository.getLocationStats();
    }

    /**
     * Entity를 DTO로 변환
     */
    private ItemResp toItemResp(Item item) {
        return ItemResp.from(item);
    }

    /**
     * 정렬 조건 생성
     */
    private Sort createSort(String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return switch (sortBy != null ? sortBy.toLowerCase() : "created") {
            case "name" -> Sort.by(direction, "name");
            case "category" -> Sort.by(direction, "category");
            case "quantity" -> Sort.by(direction, "quantity");
            case "expiry" -> Sort.by(direction, "expiryDate");
            case "location" -> Sort.by(direction, "location");
            case "updated" -> Sort.by(direction, "updatedAt");
            default -> Sort.by(direction, "createdAt");
        };
    }

    /**
     * 카테고리별 아이템 수 조회
     */
    public long countByCategory(String category) {
        return itemRepository.countByCategory(category);
    }

    /**
     * 위치별 아이템 수 조회
     */
    public long countByLocation(String location) {
        return itemRepository.countByLocation(location);
    }
}