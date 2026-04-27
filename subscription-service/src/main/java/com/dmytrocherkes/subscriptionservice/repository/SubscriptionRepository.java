package com.dmytrocherkes.subscriptionservice.repository;

import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

//    List<Subscription> findAllByCreatedByUserIdAndIsActiveTrue(Integer userId);

    List<Subscription> findAllByCreatedByUserId(Integer userId);

    Optional<Subscription> findByIdAndIsActiveTrue(UUID id);

    @Modifying
    @Query(value = "DELETE FROM subscriptions WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") UUID id);

}
