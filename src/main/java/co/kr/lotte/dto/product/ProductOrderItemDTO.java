package co.kr.lotte.dto.product;

import lombok.*;

import java.text.DecimalFormat;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderItemDTO {
    private int no;
    private int ordNo;
    private int prodNo;
    private int count;

    private ProductDTO product;

    public int getDisTotal() {
        return count * product.getDisPrice();
    }

    public String getDisTotalWithComma() {
        int price =  count * product.getDisPrice();
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(price);
    }
}