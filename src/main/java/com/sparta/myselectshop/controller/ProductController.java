package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.ApiUseTime;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.ApiUseTimeRepository;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final ApiUseTimeRepository apiUseTimeRepository;

    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 측정 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 응답 보내기
            return productService.createProduct(requestDto, userDetails.getUser());
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();
            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            // 로그인 회원 정보
            User loginUser = userDetails.getUser();

            // API 사용시간 및 DB 에 기록
            ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser)
                .orElse(null);
            if (apiUseTime == null) {
                // 로그인 회원의 기록이 없으면
                apiUseTime = new ApiUseTime(loginUser, runTime);
            } else {
                // 로그인 회원의 기록이 이미 있으면
                apiUseTime.addUseTime(runTime);
            }

            System.out.println("[API Use Time] Username: " + loginUser.getUsername() + ", Total Time: " + apiUseTime.getTotalTime() + " ms");
            apiUseTimeRepository.save(apiUseTime);
        }
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id,
        @RequestBody ProductMypriceRequestDto requestDto) {
        return productService.updateProduct(id, requestDto);
    }

    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("sortBy") String sortBy,
        @RequestParam("isAsc") boolean isAsc,

        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return productService.getProducts(userDetails.getUser(), page - 1, size, sortBy, isAsc);
    }

    @PostMapping("/products/{productId}/folder")
    public void addFolder(@PathVariable Long productId, @RequestParam Long folderId,
        @AuthenticationPrincipal UserDetailsImpl userDetails){
        productService.addFolder(productId, folderId, userDetails.getUser());
    }

    @GetMapping("/folders/{folderId}/products")
    public Page<ProductResponseDto> getProductsInFolder(
        @PathVariable Long folderId,
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("sortBy") String sortBy,
        @RequestParam("isAsc") boolean isAsc,

        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return productService.getProductsInFolder(
            folderId,
            page-1,
            size,
            sortBy,
            isAsc,
            userDetails.getUser()
        );
    }


}
