package com.glovodelivery.project.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private Long menuItemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(menuItemId, that.menuItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, menuItemId);
    }
}
