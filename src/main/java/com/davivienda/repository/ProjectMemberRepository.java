package com.davivienda.repository;

import com.davivienda.model.Project;
import com.davivienda.model.ProjectMember;
import com.davivienda.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByUserOrderByProjectId(User user);

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.project WHERE pm.user = :user ORDER BY pm.project.id")
    List<ProjectMember> findByUserWithProject(@Param("user") User user);

    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    List<ProjectMember> findByProjectOrderByRole(Project project);
}
