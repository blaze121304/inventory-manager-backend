package com.fridgemate.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(length = 30)
    private String category;

    @Column(length = 20)
    private String location;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder
    public Item(String name, BigDecimal quantity, String unit, LocalDate expiryDate, 
                String category, String location, LocalDate purchaseDate, String memo) {
        this.name = name;
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.category = category;
        this.location = location;
        this.purchaseDate = purchaseDate != null ? purchaseDate : LocalDate.now();
        this.memo = memo;
    }

    /**
     * 아이템 정보 업데이트
     */
    public void updateItem(String name, String category, BigDecimal quantity, 
                          String unit, LocalDate expiryDate, String location) {
        if (name != null) this.name = name;
        if (category != null) this.category = category;
        if (quantity != null) this.quantity = quantity;
        if (unit != null) this.unit = unit;
        if (expiryDate != null) this.expiryDate = expiryDate;
        if (location != null) this.location = location;
    }

    /**
     * 재고 소비 (수량 감소)
     */
    public void consume(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("소비량은 0보다 커야 합니다.");
        }
        if (this.quantity.compareTo(amount) < 0) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.quantity + " " + this.unit);
        }
        this.quantity = this.quantity.subtract(amount);
    }

    /**
     * 유통기한 임박 여부 확인 (일수 기준)
     */
    public boolean isExpiringSoon(int days) {
        if (expiryDate == null) {
            return false;
        }
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return expiryDate.isBefore(targetDate) || expiryDate.isEqual(targetDate);
    }

    /**
     * 유통기한까지 남은 일수
     */
    public Integer getDaysUntilExpiry() {
        if (expiryDate == null) {
            return null;
        }
        return (int) LocalDate.now().until(expiryDate).getDays();
    }

    /**
     * 재고 부족 여부 확인
     */
    public boolean isLowStock(double threshold) {
        return this.quantity.doubleValue() <= threshold;
    }
}
