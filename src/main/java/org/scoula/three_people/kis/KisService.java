package org.scoula.three_people.kis;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.kis.api.DistributionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KisService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kis.appKey}")
    private String appKey;

    @Value("${kis.appSecret}")
    private String appSecret;

    @Value("${kis.approval}")
    private String approval;

    @Value("${kis.token}")
    private String token;

    public String sendChartData(DistributionRequest req) throws IOException {

        String url = "https://openapivts.koreainvestment.com:29443/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice?FID_ETC_CLS_CODE=&FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD="
                + req.companyCode()
                + "&FID_INPUT_HOUR_1="
                + req.time()
                + "&FID_PW_DATA_INCU_YN=Y";

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("authorization", token);
        headers.add("appkey", appKey);
        headers.add("appsecret", appSecret);
        headers.add("tr_id", "FHKST03010200");

        // HttpEntity를 사용하여 요청 헤더와 본문을 설정
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

}
