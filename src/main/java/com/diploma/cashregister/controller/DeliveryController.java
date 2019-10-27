package com.diploma.cashregister.controller;

import com.diploma.cashregister.domain.OrderPayments;
import com.diploma.cashregister.domain.ProviderProduct;
import com.diploma.cashregister.service.DeliveryService;
import com.diploma.cashregister.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Controller
public class DeliveryController {
    @Autowired
    private final DeliveryService deliveryService;
    @Autowired
    private final ProductService productService;

    public DeliveryController(DeliveryService deliveryService, ProductService productService) {
        this.deliveryService = deliveryService;
        this.productService = productService;
    }

    @GetMapping("delivery")
    public String delivery(Model model){
        return "delivery/delivery";
    }

    @GetMapping("/delivery_number")
    public String getDelivery(@RequestParam(required = true) long number,
                           Model model
    ) {
        Set<OrderPayments> orderPayments = deliveryService.getOrderPayments(number);

        orderPayments.stream().forEach(payment ->{
            if (payment.getType().equalsIgnoreCase("pre")) model.addAttribute("pre", payment.getSum());
            else if (payment.getType().equalsIgnoreCase("onDelivery")) model.addAttribute("post", payment.getSum());
            else  model.addAttribute("credit", payment.getSum());
        });
        model.addAttribute("order",deliveryService.getOrder(number));
        model.addAttribute("bucket",deliveryService.getProductFromOrderBucket(number));

        return "delivery/delivery";
    }

    @GetMapping("/createOrder")
    public String getDelivery(Model model,@RequestParam(required = false,defaultValue = "-1") long provider ) {
        if (provider != -1){
            model.addAttribute("supplier",provider);
            model.addAttribute("products",productService.getAllProductsByProvider(provider));
        }
        model.addAttribute("suppliers",deliveryService.getProviders());
        return "delivery/createOrder";
    }

    @PostMapping(value = "/delivery_number")
    public @ResponseBody String saveDelivery(
            @RequestBody String json
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(json, new TypeReference<Map<String,String>>(){});
        deliveryService.createDelivery(map);
        return json;
    }
}
