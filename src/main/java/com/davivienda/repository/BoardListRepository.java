package com.davivienda.repository;

import com.davivienda.model.BoardList;
import com.davivienda.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardListRepository extends JpaRepository<BoardList, Long> {

    List<BoardList> findByProjectOrderByPositionAsc(Project project);

    @Query("SELECT bl FROM BoardList bl LEFT JOIN FETCH bl.project WHERE bl.project = :project ORDER BY bl.position")
    List<BoardList> findByProjectOrderByPositionAscWithProject(@Param("project") Project project);
}
