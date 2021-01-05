package com.catwalk.publicapicatwalk.repository;

import com.catwalk.publicapicatwalk.model.Badge;
import com.catwalk.publicapicatwalk.model.User;
import com.catwalk.publicapicatwalk.model.constants.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, String> {
    List<Badge> findAllByUser(User u);
    List<Badge> findAllByBadgeType(BadgeType badgeType);
    List<Badge> findAllByBadgeTypeAndUser(BadgeType badgeType, User u);
}
