package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.*;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;

import static com.skyg0d.shop.shiny.util.order.OrderCreator.createOrder;
import static com.skyg0d.shop.shiny.util.order.OrderCreator.createOrderResponse;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for OrderRepository")
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    @DisplayName("findAllByUser Returns List Of Orders Inside Page Object When Successful")
    void findAllByUser_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        Order expectedOrder = persistOrder();

        Page<Order> ordersPage = orderRepository.findAllByUser(PageRequest.of(0, 1), expectedOrder.getUser());

        assertThat(ordersPage).isNotEmpty();

        assertThat(ordersPage.getContent()).isNotEmpty();

        assertThat(ordersPage.getContent().get(0)).isNotNull();

        assertThat(ordersPage.getContent().get(0).getId()).isEqualTo(expectedOrder.getId());
    }

    private Order persistOrder() {
        Order orderToBeSave = createOrder();

        Product productToBeSave = orderToBeSave.getProducts().get(0);

        List<Category> categoriesSaved = categoryRepository.saveAllAndFlush(productToBeSave.getCategories());

        productToBeSave.setCategories(new HashSet<>(categoriesSaved));

        Product productSaved = productRepository.save(productToBeSave);

        orderToBeSave.setProducts(List.of(productSaved));

        User userToBeSave = orderToBeSave.getUser();

        List<Role> rolesSaved = roleRepository.saveAllAndFlush(userToBeSave.getRoles());

        userToBeSave.setRoles(new HashSet<>(rolesSaved));

        User userSaved = userRepository.save(userToBeSave);

        orderToBeSave.setUser(userSaved);

        return orderRepository.save(orderToBeSave);
    }
}
