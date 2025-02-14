package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.stereotype.Component;

@Component
public class MatchExecutor {

    public void execute(
            Order buyOrder,
            Order sellOrder,
            int matchQuantity,
            int matchedPrice,
            StringBuilder matchingLog,
            OrderBook orderBook
    ) {
        // 체결 로직
        buyOrder.fill(matchQuantity);
        sellOrder.fill(matchQuantity);

        // 체결 로그 작성
        matchingLog.append(String.format(
                "Matched %d units @ %d between BUY[%s] and SELL[%s]\n",
                matchQuantity, matchedPrice, buyOrder.getId(), sellOrder.getId()
        ));

        // 마지막 체결가 갱신
        orderBook.setLastTradedPrice(buyOrder.getCompanyCode(), matchedPrice);

        // Repository 등에 체결 정보 저장 등...
    }
}
