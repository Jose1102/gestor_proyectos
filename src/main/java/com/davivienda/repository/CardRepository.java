package com.davivienda.repository;

import com.davivienda.model.BoardList;
import com.davivienda.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByListOrderByPositionAsc(BoardList list);

    @Query("SELECT c FROM Card c LEFT JOIN FETCH c.assignee LEFT JOIN FETCH c.list WHERE c.list = :list ORDER BY c.position")
    List<Card> findByListOrderByPositionAscWithDetails(@Param("list") BoardList list);
}
