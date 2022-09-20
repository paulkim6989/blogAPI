package com.example.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.model.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
	
	@Query(value="select ROWNUM as rank, A.* from (select top 10 query, count(1) cnt from history group by query having count(1) > 0 order by count(1) desc) A", nativeQuery=true)
	List<History> searchPopularKeyword();
}
