package com.fridgemate.api.service;

import com.fridgemate.api.domain.Item;
import com.fridgemate.api.dto.ConsumeReq;
import com.fridgemate.api.dto.ItemCreateReq;
import com.fridgemate.api.dto.ItemResp;
import com.fridgemate.api.exception.ItemNotFoundException;
import com.fridgemate.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("계란");
        testItem.setQuantity(new BigDecimal("10"));
        testItem.setUnit("개");
        testItem.setExpiryDate(LocalDate.now().plusDays(7));
        testItem.setCategory("유제품");
        testItem.setLocation("냉장");
    }

    @Test
    @DisplayName("재고 생성 성공")
    void createItem_Success() {
        // Given
        ItemCreateReq request = new ItemCreateReq();
        request.setName("계란");
        request.setQuantity(new BigDecimal("10"));
        request.setUnit("개");
        request.setExpiryDate(LocalDate.now().plusDays(7));
        request.setCategory("유제품");
        request.setLocation("냉장");

        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        // When
        ItemResp result = itemService.createItem(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("계란");
        assertThat(result.getQuantity()).isEqualTo(new BigDecimal("10"));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("재고 소비 성공")
    void consumeItem_Success() {
        // Given
        ConsumeReq request = new ConsumeReq();
        request.setAmount(new BigDecimal("3"));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        // When
        ItemResp result = itemService.consumeItem(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testItem.getQuantity()).isEqualTo(new BigDecimal("7"));
        verify(itemRepository).save(testItem);
    }

    @Test
    @DisplayName("재고 소비 실패 - 재고 부족")
    void consumeItem_InsufficientStock() {
        // Given
        ConsumeReq request = new ConsumeReq();
        request.setAmount(new BigDecimal("15")); // 보유량(10)보다 많이 소비

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        // When & Then
        assertThatThrownBy(() -> itemService.consumeItem(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("존재하지 않는 재고 조회")
    void getItem_NotFound() {
        // Given
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemService.getItem(999L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("재고 아이템을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("유통기한 임박 상품 조회")
    void getExpiringItems_Success() {
        // Given
        List<Item> expiringItems = List.of(testItem);
        when(itemRepository.findExpiringItems(any(LocalDate.class))).thenReturn(expiringItems);

        // When
        List<ItemResp> result = itemService.getExpiringItems(7);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("계란");
        verify(itemRepository).findExpiringItems(any(LocalDate.class));
    }
}
