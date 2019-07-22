package com.wy.paydemo.service;

import com.wy.paydemo.wxpay.sdk.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatPayService {


    @Value("${wx_appid}")
    String appid;
    @Value("${wx_mch_id}")
    String mch_id;
    @Value("${wx_refund_url}")
    String refund_url;
    @Value("${wx_key}")
    String key;
    @Value("${cert.path}")
    String certPath;
    @Value("${wx_h5_notify_url}")
    String h5_notify_url;

    public class MyWXPayConfig extends WXPayConfig {

        @Override
        public String getAppID() {
            return appid;
        }

        @Override
        public String getMchID() {
            return mch_id;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public InputStream getCertStream() {
            File file = new File(certPath);
            InputStream certStream = null;
            try {
                certStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return certStream;
        }

        @Override
        public IWXPayDomain getWXPayDomain() {
            IWXPayDomain iwxPayDomain = new IWXPayDomain() {
                @Override
                public void report(String domain, long elapsedTimeMillis, Exception ex) {

                }

                @Override
                public DomainInfo getDomain(WXPayConfig config) {
                    return new DomainInfo(WXPayConstants.DOMAIN_API, true);
                }
            };
            return iwxPayDomain;
        }
    }

    /**
     * H5付款-统一下单
     * @return
     */
    public Map<String, String> payH5(String ip,String orderNo) {
        Map<String, String> resultMap = null;
        try {
            WXPayConfig config = new MyWXPayConfig();
            WXPay wxPay = new WXPay(config);

            Map<String, String> map = new HashMap<>();
            map.put("appid", appid);
            map.put("mch_id", mch_id);
            map.put("nonce_str", WXPayUtil.generateNonceStr());
            map.put("body", "订单:"+ orderNo);//订单号

            map.put("out_trade_no", orderNo);//订单号
            map.put("total_fee", "23.56");
            map.put("spbill_create_ip", ip);
            map.put("notify_url", h5_notify_url);
            map.put("trade_type", "MWEB");//H5支付的交易类型为MWEB
            map.put("sign_type", "HMAC-SHA256");
            String xmlStr = WXPayUtil.generateSignedXml(map, key);
            resultMap = wxPay.unifiedOrder(WXPayUtil.xmlToMap(xmlStr));
            System.out.println(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
