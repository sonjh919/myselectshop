package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByUser(User user, Pageable pageable);
}
