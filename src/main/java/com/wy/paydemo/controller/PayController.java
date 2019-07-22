package com.wy.paydemo.controller;

import com.alipay.api.AlipayApiException;
import com.wy.paydemo.dto.CommonResponse;
import com.wy.paydemo.service.AliPayService;
import com.wy.paydemo.service.WechatPayService;
import com.wy.paydemo.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private AliPayService aliPayService;

    /**
     * 微信H5支付
     * @param orderNo
     */
    @RequestMapping("/wxpayH5")
    public CommonResponse wxpayH5(HttpServletRequest request, String orderNo){
        String ip = WXPayUtil.getIp(request);
        System.out.println("ip=="+ip);
        CommonResponse commonResponse = new CommonResponse();
        Map<String, String> resultMap = wechatPayService.payH5(ip,orderNo);
        if (resultMap.get("return_code").equals("SUCCESS") && "SUCCESS".equals(resultMap.get("result_code"))) {
            //mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付,mweb_url的有效期为5分钟。
            if (StringUtils.isNotEmpty(resultMap.get("mweb_url"))) {
                System.out.println(resultMap.get("mweb_url"));
                commonResponse.setSuccess(resultMap.get("mweb_url"));
            } else {
                commonResponse.setFailed("-2", resultMap.get("err_code_des"));
            }
        } else if (resultMap.get("return_code").equals("SUCCESS") && "ORDERPAID".equals(resultMap.get("err_code"))) {
            commonResponse.setFailed("001", "订单已支付,请勿重复支付");
        } else {
            commonResponse.setFailed("-2", resultMap.get("err_code_des"));
        }
        return commonResponse;
    }

    /**
     * H5支付
     * @param orderNo
     */
    @RequestMapping("/alipayH5")
    public CommonResponse alipayH5(String orderNo){
        CommonResponse commonResponse = new CommonResponse();
        try {
            Map<String, String> resultMap = aliPayService.payH5(orderNo);
            //获取跳转form
            if (StringUtils.isNotEmpty(resultMap.get("form"))) {
                System.out.println(resultMap.get("form"));
                commonResponse.setSuccess( resultMap.get("form"));
            } else {
                commonResponse.setFailed("-2", "获取form表单失败，请重试");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            commonResponse.setFailed("-2", "支付网络异常");
        }
        return commonResponse;
    }


}
