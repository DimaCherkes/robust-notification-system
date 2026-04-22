package com.dmytrocherkes.subscriptionservice.service;

import com.dmytrocherkes.subscriptionservice.mapper.CityMapper;
import com.dmytrocherkes.subscriptionservice.model.dto.CityDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.enums.AwsMessageTypes;
import com.dmytrocherkes.subscriptionservice.model.enums.CityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl {

    private final SnsPublisher snsPublisher;

    @Value("${app.aws.sns.subscription-topic-arn}")
    private String subscriptionTopicArn;

    @Transactional
    public void updateActiveSubscriptionsCount(City city, Integer newValue) {
        if (city.getStatus() == CityStatus.ON_USE && newValue == 0) {
            // active subscriptions has dropped to 0 -> deactivate city
            city.setActiveSubscriptionsCount(newValue);
            city.setStatus(CityStatus.NOT_USED);
            CityDTO cityDTO = CityMapper.toDTO(city);

            snsPublisher.publishMessage(
                    subscriptionTopicArn,
                    cityDTO,
                    Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.CITY_DEACTIVATE.getType())
            );
        } else if (city.getStatus() == CityStatus.NOT_USED && newValue > 0) {
            // city wasn't used before, but now has active subscriptions -> activate city
            city.setActiveSubscriptionsCount(newValue);
            city.setStatus(CityStatus.ON_USE);
            CityDTO cityDTO = CityMapper.toDTO(city);

            snsPublisher.publishMessage(
                    subscriptionTopicArn,
                    cityDTO,
                    Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.CITY_ACTIVATE.getType())
            );
        } else {
            // active subscriptions count has changed and is not 0 -> update count
            city.setActiveSubscriptionsCount(newValue);
        }
    }
}
