package co.kr.lotte.controller.product;

import co.kr.lotte.dto.product.*;
import co.kr.lotte.entity.member.MemberEntity;
import co.kr.lotte.security.MyUserDetails;
import co.kr.lotte.service.CateService;
import co.kr.lotte.service.product.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CateService cateService;

    @GetMapping("/product/list")
    public String list(Model model, PageRequestDTO pageRequestDTO) {
        // 상품 목록 조회
        PageResponseDTO pageResponseDTO = productService.findByCate1AndCate2(pageRequestDTO);
        log.info("pageResponseDTO pg : " + pageResponseDTO.getPg());
        log.info("pageResponseDTO size : " + pageResponseDTO.getSize());
        log.info("pageResponseDTO total : " + pageResponseDTO.getTotal());
        log.info("pageResponseDTO start : " + pageResponseDTO.getStart());
        log.info("pageResponseDTO end : " + pageResponseDTO.getEnd());
        log.info("pageResponseDTO prev : " + pageResponseDTO.isPrev());
        log.info("pageResponseDTO next : " + pageResponseDTO.isNext());
        log.info("pageResponseDTO dtoList : " + pageResponseDTO.getDtoList().size());

        // 카테고리 조회
        String c1Name = cateService.getC1Name(pageRequestDTO.getCate1());
        String c2Name = cateService.getC2Name(pageRequestDTO.getCate1(), pageRequestDTO.getCate2());
        log.info("c1Name : " + c1Name);
        log.info("c2Name : " + c2Name);
        model.addAttribute(pageResponseDTO);
        model.addAttribute("c1Name", c1Name);
        model.addAttribute("c2Name", c2Name);
        return "/product/list";
    }

    @GetMapping("/product/view")
    public String view(Model model, SearchDTO searchDTO) {
        // 상품 상세 조회
        ProductDTO productDTO = productService.getProductDTO(searchDTO.getProdNo());

        // 카테고리 조회
        String c1Name = cateService.getC1Name(searchDTO.getCate1());
        String c2Name = cateService.getC2Name(searchDTO.getCate1(), searchDTO.getCate2());
        model.addAttribute("product", productDTO);
        model.addAttribute("cate1", searchDTO.getCate1());
        model.addAttribute("cate2", searchDTO.getCate2());
        model.addAttribute("c1Name", c1Name);
        model.addAttribute("c2Name", c2Name);
        return "/product/view";
    }

    @GetMapping("/product/cart")
    public String cart(Model model, @AuthenticationPrincipal Object principal) {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        List<ProductCartDTO> productCartDTOS = productService.findProductCartByUid(uid);
        log.info("cart size() : " + productCartDTOS.size());
        model.addAttribute("carts", productCartDTOS);
        return "/product/cart";
    }

    @ResponseBody
    @PostMapping("/product/cart")
    public int cart(@RequestBody ProductCartDTO productCartDTO) {
        return productService.insertProductCart(productCartDTO);
    }

    @PostMapping("/product/cartDelete")
    public String cart(@RequestParam(name = "chk") int[] chk) {
        productService.deleteCart(chk);
        return "redirect:/product/cart";
    }

    @GetMapping("/product/complete")
    public String complete(Model model, int ordNo) {
        ProductOrderDTO productOrderDTO = productService.findProductOrderById(ordNo);
        productOrderDTO.setOrdPaymentName();
        List<ProductOrderItemDTO> productOrderItemDTOS = productService.findProductOrderItemsByOrdNo(ordNo);
        for (ProductOrderItemDTO productOrderItemDTO : productOrderItemDTOS) {
            productOrderItemDTO.setProduct(productService.findProductById(productOrderItemDTO.getProdNo()));
        }
        model.addAttribute("order", productOrderDTO);
        model.addAttribute("orderItems", productOrderItemDTOS);
        return "/product/complete";
    }

    @GetMapping("/product/order")
    public String order(Model model, @RequestParam("jsonData") String jsonData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<SearchDTO> searchDTOS = objectMapper.readValue(jsonData, new TypeReference<List<SearchDTO>>() {});
        for (SearchDTO searchDTO : searchDTOS) {
            log.info("prodNo : " + searchDTO.getProdNo());
            log.info("count : " + searchDTO.getCount());
            log.info("cartNo : " + searchDTO.getCartNo());
            if (searchDTO.getProdNo() != 0) {
                searchDTO.setProduct(productService.findProductById(searchDTO.getProdNo()));
            }
        }
        model.addAttribute("products", searchDTOS);
        return "/product/order";
    }

    @ResponseBody
    @PostMapping("/product/order")
    public int order(ProductOrderDTO productOrderDTO) {
        int ordNo = productService.saveOrder(productOrderDTO);
        // 사용한 포인트가 5000원 이상이라면 포인트 소비
        if (productOrderDTO.getUsedPoint() >= 5000) {
            productService.usePoint(productOrderDTO.getOrdUid(), productOrderDTO.getUsedPoint(), ordNo);
        }
        return ordNo;
    }

    @ResponseBody
    @PostMapping("/product/orderItem")
    public int orderItem(@RequestParam("jsonData") String jsonData,
                         @RequestParam("ordNo") int ordNo,
                         @AuthenticationPrincipal Object principal) throws JsonProcessingException {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        ObjectMapper objectMapper = new ObjectMapper();
        List<SearchDTO> searchDTOS = objectMapper.readValue(jsonData, new TypeReference<List<SearchDTO>>() {});
        for (SearchDTO searchDTO : searchDTOS) {
            log.info("prodNo : " + searchDTO.getProdNo());
            log.info("count : " + searchDTO.getCount());
            log.info("cartNo : " + searchDTO.getCartNo());
        }
        productService.saveOrderItem(searchDTOS, ordNo, uid);
        return 1;
    }

    @GetMapping("/product/search")
    public String search() {
        return "/product/search";
    }

    @GetMapping("/product/findProduct")
    @ResponseBody
    public ProductDTO findProduct(@RequestParam("prodNo") int prodNo) {
        return productService.findProductById(prodNo);
    }

    @GetMapping("/product/checkReview")
    @ResponseBody
    public boolean checkReview(@RequestParam("prodNo") int prodNo, @AuthenticationPrincipal Object principal) {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        return productService.checkReview(prodNo, uid);
    }

    @GetMapping("/product/checkReceive")
    @ResponseBody
    public boolean checkReceive(@RequestParam("no") int no, @AuthenticationPrincipal Object principal) {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        return productService.checkReceive(no, uid);
    }

    @PostMapping("/product/orderReceive")
    @ResponseBody
    public String orderReceive(@RequestParam("no") int no, @AuthenticationPrincipal Object principal) {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        return productService.orderReceive(no, uid);
    }

    @PostMapping("/product/orderReview")
    @ResponseBody
    public int orderReview(ProductReviewDTO productReviewDTO, @AuthenticationPrincipal Object principal) {
        MemberEntity memberEntity = ((MyUserDetails) principal).getMember();
        String uid = memberEntity.getUid();
        productReviewDTO.setUid(uid);
        return productService.saveProductReview(productReviewDTO, uid);
    }
}