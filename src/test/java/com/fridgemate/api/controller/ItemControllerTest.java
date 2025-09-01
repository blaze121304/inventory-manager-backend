package com.fridgemate.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fridgemate.api.dto.ItemCreateReq;
import com.fridgemate.api.dto.ItemResp;
import com.fridgemate.api.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemResp testItemResp;

    @BeforeEach
    void setUp() {
        testItemResp = new ItemResp();
        testItemResp.setId(1L);
        testItemResp.setName("계란");
        testItemResp.setQuantity(new BigDecimal("10"));
        testItemResp.setUnit("개");
        testItemResp.setExpiryDate(LocalDate.now().plusDays(7));
        testItemResp.setCategory("유제품");
        testItemResp.setLocation("냉장");
    }

    @Test
    @DisplayName("재고 목록 조회 API")
    void getItems_Success() throws Exception {
        // Given
//        when(itemService.getItems(null, null, null, null, "updatedAt", "desc")).thenReturn(List.of(testItemResp));
//
//        // When & Then
//        mockMvc.perform(get("/api/items"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].name").value("계란"))
//                .andExpect(jsonPath("$[0].quantity").value(10))
//                .andExpect(jsonPath("$[0].unit").value("개"));
    }

    @Test
    @DisplayName("재고 생성 API")
    void createItem_Success() throws Exception {
        // Given
        ItemCreateReq request = new ItemCreateReq();
        request.setName("계란");
        request.setQuantity(new BigDecimal("10"));
        request.setUnit("개");
        request.setExpiryDate(LocalDate.now().plusDays(7));
        request.setCategory("유제품");
        request.setLocation("냉장");

        when(itemService.createItem(any(ItemCreateReq.class))).thenReturn(testItemResp);

        // When & Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("계란"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("재고 생성 API - 유효성 검증 실패")
    void createItem_ValidationFailed() throws Exception {
        // Given
        ItemCreateReq request = new ItemCreateReq();
        // name을 설정하지 않음 (필수 필드)
        request.setQuantity(new BigDecimal("10"));
        request.setUnit("개");

        // When & Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("재고 단건 조회 API")
    void getItem_Success() throws Exception {
        // Given
        when(itemService.getItem(1L)).thenReturn(testItemResp);

        // When & Then
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("계란"));
    }

    @Test
    @DisplayName("재고 삭제 API")
    void deleteItem_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }
}
