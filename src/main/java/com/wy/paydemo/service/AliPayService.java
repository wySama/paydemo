package com.wy.paydemo.service;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayService {

    @Value("${alipay_app_id}")
    String app_id;

    @Value("${alipay_merchant_private_key}")
    String merchant_private_key;

    @Value("${alipay_alipay_public_key}")
    String alipay_public_key;

    @Value("${alipay_return_url}")
    String return_url;

    @Value("${alipay_sign_type}")
    String sign_type;

    @Value("${alipay_charset}")
    String charset;

    @Value("${alipay_gatewayUrl}")
    String gatewayUrl;

    @Value("${alipay_log_path}")
    String log_path;

    @Value("${alipay_seller_email}")
    String seller_email;
    @Value("${alipay_h5_notify_url}")
    String alipay_h5_notify_url;

    public Map<String, String> payH5(String orderNo) throws AlipayApiException {
        Map<String, String> resultMap = new HashMap<>();
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request类
        //设置回调地址
        alipayRequest.setNotifyUrl(alipay_h5_notify_url);
        alipayRequest.setReturnUrl(return_url);
        Map<String,Object> map=new HashMap<>();
        //商户订单号
        map.put("out_trade_no",orderNo);

        //订单总金额（元，保留2位小数）
        String orderFee = "4526";//数据库存的是分，需要转换
        BigDecimal fee = new BigDecimal(orderFee).divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
        map.put("total_amount" , fee);

        //订单标题
        map.put("body", "订单:"+ orderNo);//订单号

        //该笔订单允许的最晚付款时间，m-分钟，h-小时，d-天，1c-当天
        map.put("timeout_express","5m");
        //把订单信息转换为json对象的字符串
        String postdata = JSONObject.fromObject(map).toString();
        alipayRequest.setBizContent(postdata);
        String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        resultMap.put("form",form);
        return resultMap;

    }



}
